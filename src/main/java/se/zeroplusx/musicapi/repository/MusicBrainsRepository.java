package se.zeroplusx.musicapi.repository;

import se.zeroplusx.musicapi.model.ArtistInfo;

public interface MusicBrainsRepository {
    ArtistInfo getArtistInfo(String artistMbid);
}
