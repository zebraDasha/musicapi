package se.zeroplusx.musicapi.service;

import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;
import se.zeroplusx.musicapi.enums.Types;
import se.zeroplusx.musicapi.model.*;
import se.zeroplusx.musicapi.repository.CoverArtsRepository;
import se.zeroplusx.musicapi.repository.DiscogsRepository;
import se.zeroplusx.musicapi.repository.MusicBrainsRepository;
import se.zeroplusx.musicapi.service.impl.MusicBrainsClientImpl;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class CompanyServiceTest {
    @Test
    public void testGetAlbumArt() {
        CoverArtsRepository coverArtsRepositoryMock = Mockito.mock(CoverArtsRepository.class);
        MusicBrainsRepository musicBrainsRepositoryMock = Mockito.mock(MusicBrainsRepository.class);
        DiscogsRepository discogsRepositoryMock = Mockito.mock(DiscogsRepository.class);

        MusicBrainsClient musicBrainsClient = new MusicBrainsClientImpl(musicBrainsRepositoryMock, coverArtsRepositoryMock, discogsRepositoryMock);
        Mockito.when(coverArtsRepositoryMock.getAlbumCover(Mockito.anyString())).thenReturn("testUrl");

        AlbumInfo albumInfo = new AlbumInfo();
        String albumArt = musicBrainsClient.getAlbumArt(albumInfo, "1512");

        Mockito.verify(coverArtsRepositoryMock, Mockito.times(1)).getAlbumCover(Mockito.anyString());
        Assert.assertEquals("testUrl", albumArt);
    }

    @Test
    public void testGetArtistInfo() throws ExecutionException, InterruptedException {
        CoverArtsRepository coverArtsRepositoryMock = Mockito.mock(CoverArtsRepository.class);
        MusicBrainsRepository musicBrainsRepositoryMock = Mockito.mock(MusicBrainsRepository.class);
        DiscogsRepository discogsRepositoryMock = Mockito.mock(DiscogsRepository.class);

        ArtistInfo artistInfo = new ArtistInfo();
        AlbumInfo albumInfo = new AlbumInfo();
        albumInfo.setId("albumInfoId");
        albumInfo.setImage("albumInfoImage");
        albumInfo.setTitle("albumInfoTitle");
        List<AlbumInfo> albumInfos = new ArrayList<>();
        albumInfos.add(albumInfo);
        artistInfo.setAlbums(albumInfos);

        ArtistData artistData = new ArtistData();
        artistData.setProfile("Profile");
        artistData.setRealname("RealName");
        artistInfo.setArtistData(artistData);
        artistInfo.setMbid("artistMbid");
        Relation relation = new Relation();
        relation.setType(Types.discogs.name());
        ResourceObject resourceObject = new ResourceObject();
        resourceObject.setId("resourceObjectId");
        resourceObject.setResource("discogs");
        relation.setUrl(resourceObject);
        List<Relation> relations = new ArrayList<>();
        relations.add(relation);
        artistInfo.setRelations(relations);
        ReleaseGroup releaseGroup = new ReleaseGroup();
        releaseGroup.setId("releaseGroupId");
        releaseGroup.setPrimaryType(Types.Album.name());
        releaseGroup.setTitle("releaseGroup title");
        List<ReleaseGroup> releaseGroups = new ArrayList<>();
        releaseGroups.add(releaseGroup);
        artistInfo.setReleaseGroups(releaseGroups);
        artistInfo.setResourceUrl("resourceUrl");

        MusicBrainsClient musicBrainsClient = new MusicBrainsClientImpl(musicBrainsRepositoryMock, coverArtsRepositoryMock, discogsRepositoryMock);
        Mockito.when(musicBrainsRepositoryMock.getArtistInfo(Mockito.anyString())).thenReturn(artistInfo);
        Mockito.when(discogsRepositoryMock.getArtistData(Mockito.anyString())).thenReturn(artistData);
        Mockito.when(coverArtsRepositoryMock.getAlbumCover(Mockito.anyString())).thenReturn("testUrl");

        Result artistInfoResult = musicBrainsClient.getArtistInfo("mbid");

        Assert.assertEquals("Profile", artistInfoResult.getDescription());
        Assert.assertEquals("mbid", artistInfoResult.getMbid());
        Assert.assertEquals("releaseGroupId", artistInfoResult.getAlbums().get(0).getId());
        Assert.assertEquals("testUrl", artistInfoResult.getAlbums().get(0).getImage());
        Assert.assertEquals("releaseGroup title", artistInfoResult.getAlbums().get(0).getTitle());
        Assert.assertEquals("RealName", artistInfoResult.getName());

        Mockito.verify(musicBrainsRepositoryMock, Mockito.times(1)).getArtistInfo(Mockito.anyString());
        Mockito.verify(discogsRepositoryMock, Mockito.times(1)).getArtistData(Mockito.anyString());
        Mockito.verify(coverArtsRepositoryMock, Mockito.times(1)).getAlbumCover(Mockito.anyString());
    }

    @Test
    public void testGetAlbumInfo() {
        CoverArtsRepository coverArtsRepositoryMock = Mockito.mock(CoverArtsRepository.class);
        MusicBrainsRepository musicBrainsRepositoryMock = Mockito.mock(MusicBrainsRepository.class);
        DiscogsRepository discogsRepositoryMock = Mockito.mock(DiscogsRepository.class);
        MusicBrainsClient musicBrainsClient = new MusicBrainsClientImpl(musicBrainsRepositoryMock, coverArtsRepositoryMock, discogsRepositoryMock);
        Mockito.when(coverArtsRepositoryMock.getAlbumCover(Mockito.anyString())).thenReturn("testUrl");
        ArtistInfo artistInfo = new ArtistInfo();
        ReleaseGroup releaseGroup = new ReleaseGroup();
        releaseGroup.setId("releaseGroupId");
        releaseGroup.setPrimaryType(Types.Album.name());
        releaseGroup.setTitle("releaseGroup title");
        List<ReleaseGroup> releaseGroups = new ArrayList<>();
        releaseGroups.add(releaseGroup);
        artistInfo.setReleaseGroups(releaseGroups);
        List<AlbumInfo> albumInfo = musicBrainsClient.getAlbumInfo(artistInfo);
        Assert.assertNotNull(albumInfo);
        Assert.assertEquals("releaseGroupId", albumInfo.get(0).getId());
        Assert.assertEquals("releaseGroup title", albumInfo.get(0).getTitle());
        Assert.assertEquals("testUrl", albumInfo.get(0).getImage());

        Mockito.verify(coverArtsRepositoryMock, Mockito.times(1)).getAlbumCover(Mockito.anyString());
    }

    @Test
    public void testGetDescriptionAndName() {
        CoverArtsRepository coverArtsRepositoryMock = Mockito.mock(CoverArtsRepository.class);
        MusicBrainsRepository musicBrainsRepositoryMock = Mockito.mock(MusicBrainsRepository.class);
        DiscogsRepository discogsRepositoryMock = Mockito.mock(DiscogsRepository.class);
        MusicBrainsClient musicBrainsClient = new MusicBrainsClientImpl(musicBrainsRepositoryMock, coverArtsRepositoryMock, discogsRepositoryMock);

        ArtistData expectedArtistData = new ArtistData();
        expectedArtistData.setProfile("Profile");
        expectedArtistData.setRealname("RealName");

        Mockito.when(discogsRepositoryMock.getArtistData(Mockito.anyString())).thenReturn(expectedArtistData);

        ArtistInfo artistInfo = new ArtistInfo();

        ArtistData artistData = musicBrainsClient.getDescriptionAndName("url", artistInfo);

        Assert.assertEquals("Profile", artistData.getProfile());
        Assert.assertEquals("RealName", artistData.getRealname());

        Mockito.verify(discogsRepositoryMock, Mockito.times(1)).getArtistData(Mockito.anyString());
    }

    @Test
    public void testGetResult() {
        CoverArtsRepository coverArtsRepositoryMock = Mockito.mock(CoverArtsRepository.class);
        MusicBrainsRepository musicBrainsRepositoryMock = Mockito.mock(MusicBrainsRepository.class);
        DiscogsRepository discogsRepositoryMock = Mockito.mock(DiscogsRepository.class);
        MusicBrainsClient musicBrainsClient = new MusicBrainsClientImpl(musicBrainsRepositoryMock, coverArtsRepositoryMock, discogsRepositoryMock);

        ArtistData expectedArtistData = new ArtistData();
        expectedArtistData.setProfile("Profile");
        expectedArtistData.setRealname("RealName");


        ArtistInfo artistInfo = new ArtistInfo();
        AlbumInfo albumInfo = new AlbumInfo();
        albumInfo.setId("albumInfoId");
        albumInfo.setImage("albumInfoImage");
        albumInfo.setTitle("albumInfoTitle");
        List<AlbumInfo> albumInfos = new ArrayList<>();
        albumInfos.add(albumInfo);
        artistInfo.setAlbums(albumInfos);

        ArtistData artistData = new ArtistData();
        artistData.setProfile("Profile");
        artistData.setRealname("RealName");
        artistInfo.setArtistData(artistData);
        artistInfo.setMbid("artistMbid");
        Relation relation = new Relation();
        relation.setType(Types.discogs.name());
        ResourceObject resourceObject = new ResourceObject();
        resourceObject.setId("resourceObjectId");
        resourceObject.setResource("discogs");
        relation.setUrl(resourceObject);
        List<Relation> relations = new ArrayList<>();
        relations.add(relation);
        artistInfo.setRelations(relations);
        ReleaseGroup releaseGroup = new ReleaseGroup();
        releaseGroup.setId("releaseGroupId");
        releaseGroup.setPrimaryType(Types.Album.name());
        releaseGroup.setTitle("releaseGroup title");
        List<ReleaseGroup> releaseGroups = new ArrayList<>();
        releaseGroups.add(releaseGroup);
        artistInfo.setReleaseGroups(releaseGroups);
        artistInfo.setResourceUrl("resourceUrl");

        Result result = musicBrainsClient.getResult("artistMbid", artistInfo);
        Assert.assertEquals("RealName", result.getName());
        Assert.assertEquals(artistInfo.getAlbums(), result.getAlbums());
        Assert.assertEquals("artistMbid", result.getMbid());
        Assert.assertEquals("Profile", result.getDescription());

        Mockito.verify(musicBrainsRepositoryMock, Mockito.times(0)).getArtistInfo(Mockito.anyString());
        Mockito.verify(discogsRepositoryMock, Mockito.times(0)).getArtistData(Mockito.anyString());
        Mockito.verify(coverArtsRepositoryMock, Mockito.times(0)).getAlbumCover(Mockito.anyString());
    }
}
