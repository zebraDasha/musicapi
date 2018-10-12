package se.zeroplusx.musicapi.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonUnwrapped;
import lombok.Data;

import java.util.List;

@Data
public class ArtistInfo {
    @JsonProperty("release-groups")
    private List<ReleaseGroup> releaseGroups;
    private List<Relation> relations;
    private String mbid;
    private ArtistData artistData;
    private List<AlbumInfo> albums;
}
