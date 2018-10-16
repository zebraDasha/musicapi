package se.zeroplusx.musicapi.service.impl;

import se.zeroplusx.musicapi.enums.Types;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import se.zeroplusx.musicapi.model.*;
import se.zeroplusx.musicapi.repository.CoverArtsRepository;
import se.zeroplusx.musicapi.repository.DiscogsRepository;
import se.zeroplusx.musicapi.repository.MusicBrainsRepository;
import se.zeroplusx.musicapi.service.MusicBrainsClient;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@Component
public class MusicBrainsClientImpl implements MusicBrainsClient {
    private static final String MUSIC_BRAINS = "http://musicbrainz.org/ws/2/artist/%MBID%?&fmt=json&inc=url-rels+release-groups";
    private static final String COVER_ART = "http://coverartarchive.org/release-group/";
    private static final String DISCOGS = "https://api.discogs.com/artists/";

    private final MusicBrainsRepository musicBrainsRepository;
    private final CoverArtsRepository coverArtsRepository;
    private final DiscogsRepository discogsRepository;

    public MusicBrainsClientImpl(MusicBrainsRepository musicBrainsRepository, CoverArtsRepository coverArtsRepository, DiscogsRepository discogsRepository) {
        this.musicBrainsRepository = musicBrainsRepository;
        this.coverArtsRepository = coverArtsRepository;
        this.discogsRepository = discogsRepository;
    }

    //   http://musicbrainz.org/ws/2/artist/{id}
    // f.e f27ec8db-af05-4f36-916e-3d57f91ecf5e
    public Result getArtistInfo(String artistMbid) throws ExecutionException, InterruptedException {
        String url = MUSIC_BRAINS.replace("%MBID%", artistMbid);
        ArtistInfo artistInfo = musicBrainsRepository.getArtistInfo(url);
        CompletableFuture<ArtistData> artistDataCompletableFuture = CompletableFuture.completedFuture(null);

        assert artistInfo != null;
        if (!CollectionUtils.isEmpty(artistInfo.getRelations()))
            for (Relation relation : artistInfo.getRelations()) {
                String type = relation.getType();
                if (Types.discogs.name().equals(type)) {
                    ResourceObject relationUrl = relation.getUrl();
                    String resourceUrl = relationUrl.getResource();
                    artistDataCompletableFuture = CompletableFuture.supplyAsync(() ->
                            getDescriptionAndName(resourceUrl, artistInfo)
                    );
                    break;
                }
            }
        List<AlbumInfo> albumInfo = getAlbumInfo(artistInfo);

        artistInfo.setAlbums(albumInfo);
        artistInfo.setArtistData(artistDataCompletableFuture.get());
        return getResult(artistMbid, artistInfo);
    }

    @Async
    public List<AlbumInfo> getAlbumInfo(ArtistInfo artistInfo) {
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

        Map<String, CompletableFuture<String>> albumArts = new HashMap<>();
        albumInfos.forEach(albumInfo -> albumArts.put(albumInfo.getId(),
                CompletableFuture.supplyAsync(() -> getAlbumArt(albumInfo, albumInfo.getId()))));
        albumInfos.forEach(albumInfo -> {
            try {
                albumInfo.setImage(albumArts.get(albumInfo.getId()).get());
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        });
        return albumInfos;
    }

    //    https://api.discogs.com/artists/{id}
    @Async
    public ArtistData getDescriptionAndName(String url, ArtistInfo artistInfo) {
        String discogsApiUrl = getDiscogsUrl(url);
        return discogsRepository.getArtistData(discogsApiUrl);
    }

    private String getDiscogsUrl(String url) {
        String[] split = url.split("/");
        String id = split[split.length - 1];
        return DISCOGS + id;
    }

    //   http://coverartarchive.org/release-group/{id}
    // f.e. f32fab67-77dd-3937-addc-9062e2 8e4c37
    @Async
    public String getAlbumArt(AlbumInfo albumInfo, String albumId) {
        String url = COVER_ART + albumId;
        return coverArtsRepository.getAlbumCover(url);
    }

    public Result getResult(String artistMbid, ArtistInfo artistInfo) {
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
