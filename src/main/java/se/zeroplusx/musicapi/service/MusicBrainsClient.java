package se.zeroplusx.musicapi.service;

import enums.Types;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.web.client.RestTemplate;
import se.zeroplusx.musicapi.entity.*;
import se.zeroplusx.musicapi.exception.RequestToApiException;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Component
public class MusicBrainsClient {

    private static final String MUSIC_BRAINS = "http://musicbrainz.org/ws/2";
    private static final String COVER_ART = "http://coverartarchive.org/release-group/";
    private static final String DISCOGS = "https://api.discogs.com/artists/";
    private final RestTemplate restTemplate = new RestTemplate();

    //   http://musicbrainz.org/ws/2/artist/f27ec8db-af05-4f36-916e-3d57f91ecf5e?&fmt=json&inc=url-rels+release-groups
    public CompletableFuture<Result> getArtistInfo(String artistMbid) {
        String url = MUSIC_BRAINS + "/artist/" + artistMbid + "?fmt=json&inc=url-rels+release-groups";
        CompletableFuture<ArtistInfo> artistInfoCompletableFuture = CompletableFuture.supplyAsync(() ->
                restTemplate.getForEntity(url, ArtistInfo.class).getBody())
                .exceptionally(throwable -> {
                    throw new RequestToApiException("Something went wrong");
                });
        return artistInfoCompletableFuture.thenApply(artistInfo -> {
            if (artistInfo == null) {
                throw new RequestToApiException("Something went wrong");
            }

            List<AlbumInfo> albumInfo = getAlbumInfo(artistInfo);
            artistInfo.setAlbums(albumInfo);

            if (!CollectionUtils.isEmpty(artistInfo.getRelations())) {
                artistInfo.getRelations().parallelStream().forEach(relation -> {
                    String type = relation.getType();
                    if (Types.discogs.name().equals(type)) {
                        ResourceObject relationUrl = relation.getUrl();
                        String resourceUrl = relationUrl.getResource();
                        getDescriptionAndName(resourceUrl, artistInfo);
                    }
                });
            }
            return getResult(artistMbid, artistInfo);
        }).exceptionally(throwable -> {
            throw new RequestToApiException("Something went wrong");
        });
    }

    @Async
    private List<AlbumInfo> getAlbumInfo(ArtistInfo artistInfo) {
        List<AlbumInfo> albumInfos = new ArrayList<>();

        List<ReleaseGroup> releases = artistInfo.getReleaseGroups();
        if (releases != null) {
            releases.parallelStream().forEach(release -> {
                AlbumInfo info = new AlbumInfo();
                String type = release.getPrimaryType();
                if (Types.Album.name().equals(type)) {
                    String id = release.getId();
                    info.setId(id);
                    info.setTitle(release.getTitle());
                    albumInfos.add(info);
                }
            });
        }
        albumInfos.parallelStream()
                .forEach(albumInfo -> getAlbumArt(albumInfo, albumInfo.getId()));
        return albumInfos;
    }

    //    https://api.discogs.com/artists/
    @Async
    private void getDescriptionAndName(String url, ArtistInfo artistInfo) {
        String[] split = url.split("/");
        String id = split[split.length - 1];
        String discogsApiUrl = DISCOGS + id;
        ArtistData artistData = restTemplate.getForEntity(discogsApiUrl, ArtistData.class).getBody();
        artistInfo.setArtistData(artistData);
    }

    //   http://coverartarchive.org/release-group/f32fab67-77dd-3937-addc-9062e2 8e4c37
    @Async
    private void getAlbumArt(AlbumInfo albumInfo, String albumId) {
        CompletableFuture.supplyAsync(() -> {
            String url = COVER_ART + albumId;
            LinkedHashMap map = restTemplate.getForObject(url, LinkedHashMap.class);
            albumInfo.setImage(((LinkedHashMap) ((ArrayList) map.get(Types.images.name())).get(0)).get(Types.image.name()).toString());
            return albumInfo;
        }).exceptionally(throwable -> {
            throw new RequestToApiException("Something went wrong");
        });
    }

    private Result getResult(String artistMbid, ArtistInfo artistInfo) {
        Result result = new Result();
        result.setAlbums(artistInfo.getAlbums());
        result.setMbid(artistMbid);
        if (artistInfo.getArtistData() != null) {
            result.setDescription(artistInfo.getArtistData().getProfile());
            result.setName(artistInfo.getArtistData().getRealname());
        }
        return result;
    }
}
