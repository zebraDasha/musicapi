package se.zeroplusx.musicapi.repository.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import se.zeroplusx.musicapi.model.AlbumCover;
import se.zeroplusx.musicapi.repository.CoverArtsRepository;

@Repository
public class CoverArtsRepositoryImpl implements CoverArtsRepository {
    private final RestTemplate restTemplate;

    @Autowired
    public CoverArtsRepositoryImpl(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Override
    public String getAlbumCover(String url) {
        AlbumCover cover = null;
        try {
            cover = restTemplate.getForObject(url, AlbumCover.class);
        } catch (HttpClientErrorException e) {
//      Sometimes album doesn't have a cover. Do nothing.
        }
        return cover == null ? "" : cover.getUrl();
    }
}
