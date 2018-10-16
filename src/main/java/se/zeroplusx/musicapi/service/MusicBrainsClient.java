package se.zeroplusx.musicapi.service;

import se.zeroplusx.musicapi.model.AlbumInfo;
import se.zeroplusx.musicapi.model.ArtistData;
import se.zeroplusx.musicapi.model.ArtistInfo;
import se.zeroplusx.musicapi.model.Result;

import java.util.List;
import java.util.concurrent.ExecutionException;

public interface MusicBrainsClient {
    Result getArtistInfo(String artistMbid) throws ExecutionException, InterruptedException;

    List<AlbumInfo> getAlbumInfo(ArtistInfo artistInfo);

    ArtistData getDescriptionAndName(String url, ArtistInfo artistInfo);

    Result getResult(String artistMbid, ArtistInfo artistInfo);

    String getAlbumArt(AlbumInfo albumInfo, String albumId);
}
