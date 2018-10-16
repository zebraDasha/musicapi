package se.zeroplusx.musicapi.repository.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.web.client.RestTemplate;
import se.zeroplusx.musicapi.model.ArtistInfo;
import se.zeroplusx.musicapi.repository.MusicBrainsRepository;

@Repository
public class MusicBrainsRepositoryImpl implements MusicBrainsRepository {
    private final RestTemplate restTemplate;

    @Autowired
    public MusicBrainsRepositoryImpl(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Override
    public ArtistInfo getArtistInfo(String url) {
        return restTemplate.getForObject(url, ArtistInfo.class);
    }
}
