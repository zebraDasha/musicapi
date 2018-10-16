package se.zeroplusx.musicapi.model;

import lombok.Data;

import java.util.List;

@Data
public class Result {
    private String mbid;
    private String name;
    private String description;
    private List<AlbumInfo> albums;
}
