package se.zeroplusx.musicapi.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import se.zeroplusx.musicapi.entity.Result;
import se.zeroplusx.musicapi.service.MusicBrainsClient;

import java.util.concurrent.CompletableFuture;

@RestController
public class GetDataController {
    private final MusicBrainsClient musicBrainsClient;

    public GetDataController(MusicBrainsClient musicBrainsClient) {
        this.musicBrainsClient = musicBrainsClient;
    }

    @GetMapping(value = "/api/artist/{artistMbid}")
    public CompletableFuture<Result> getArtistInfo(@PathVariable String artistMbid) {
        return musicBrainsClient.getArtistInfo(artistMbid);
    }
}
