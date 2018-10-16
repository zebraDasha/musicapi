package se.zeroplusx.musicapi.service;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import se.zeroplusx.musicapi.dto.*;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

@Component
public class MusicBrainsClient {

    private static final String MUSIC_BRAINS = "http://musicbrainz.org/ws/2";
    private static final String COVER_ART = "http://coverartarchive.org/release-group/";

    @Async
    private static void getDescriptionAndName(String url, ArtistInfo artistInfo) {
        try {
            String[] split = url.split("/");
            String id = split[split.length - 1];

            RestTemplate restTemplate = new RestTemplate();
            String discogsApiUrl = "https://api.discogs.com/artists/" + id;
            ArtistData artistData = restTemplate.getForEntity(discogsApiUrl, ArtistData.class).getBody();
            artistInfo.setArtistData(artistData);
        } catch (Exception e) {
            throw new RuntimeException();
        }
    }

    //   http://coverartarchive.org/release-group/f32fab67-77dd-3937-addc-9062e2 8e4c37
    @Async
    private static String getAlbumArt(String albumId) {
        try {
            RestTemplate restTemplate = new RestTemplate();
            String url = COVER_ART + albumId;
            LinkedHashMap map = restTemplate.getForObject(url, LinkedHashMap.class);

            return ((LinkedHashMap) ((ArrayList) map.get("images")).get(0)).get("image").toString();
        } catch (Exception e) {
            return null;
        }
    }

    //   http://musicbrainz.org/ws/2/artist/f27ec8db-af05-4f36-916e-3d57f91ecf5e?&fmt=json&inc=url-rels+release-groups
    public ArtistInfo getArtistInfo(String artistMbid) {
        try {
            String url = MUSIC_BRAINS + "/artist/" + artistMbid + "?fmt=json&inc=url-rels+release-groups";
            RestTemplate template = new RestTemplate();

            ArtistInfo artistInfo = template.getForEntity(url, ArtistInfo.class).getBody();

            assert artistInfo != null;
            artistInfo.setMbid(artistMbid);
            getAlbumInfo(artistInfo);
            if (artistInfo.getRelations() != null) {
                List<Relation> relations = artistInfo.getRelations();
                final int relationsCount = relations.size();

                if (relationsCount > 0) {
                    for (Relation relation : relations) {
                        String type = relation.getType();
                        if ("discogs".equals(type)) {
                            ResourceObject relationUrl = relation.getUrl();
                            String resourceUrl = relationUrl.getResource();
                            getDescriptionAndName(resourceUrl, artistInfo);
                        }
                    }
                }
            }
            return artistInfo;
        } catch (Exception e) {
            throw new RuntimeException();
        }
    }

    @Async
    private void getAlbumInfo(ArtistInfo artistInfo) {
        List<ReleaseGroup> releases = artistInfo.getReleaseGroups();
        if (releases != null) {
            List<AlbumInfo> albumInfos = new ArrayList<>();
            releases.forEach(release->{
                AlbumInfo info = new AlbumInfo();
                String type = release.getPrimaryType();
                if ("Album".equals(type)) {
                    String id = release.getId();
                    info.setId(id);
                    info.setTitle(release.getTitle());
                    info.setImage(getAlbumArt(id));
                    albumInfos.add(info);
                }
            });
            artistInfo.setAlbums(albumInfos);
        }
    }

}
