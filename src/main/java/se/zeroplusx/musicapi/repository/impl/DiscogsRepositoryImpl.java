package se.zeroplusx.musicapi.repository.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.web.client.RestTemplate;
import se.zeroplusx.musicapi.model.ArtistData;
import se.zeroplusx.musicapi.repository.DiscogsRepository;

@Repository
public class DiscogsRepositoryImpl implements DiscogsRepository {
    private final RestTemplate restTemplate;

    @Autowired
    public DiscogsRepositoryImpl(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Override
    public ArtistData getArtistData(String discogsApiUrl) {
        return restTemplate.getForObject(discogsApiUrl, ArtistData.class);
    }
}
