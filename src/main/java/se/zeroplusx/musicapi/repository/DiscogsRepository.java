package se.zeroplusx.musicapi.repository;

import se.zeroplusx.musicapi.model.ArtistData;

public interface DiscogsRepository {
    ArtistData getArtistData(String discogsApiUrl);
}
