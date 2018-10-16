package se.zeroplusx.musicapi.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.LinkedHashMap;
import java.util.List;

public class AlbumCover {
    private String url;

    @JsonProperty("images")
    public void setUrl(List<LinkedHashMap> images) {
        for (LinkedHashMap image : images) {
            if ((boolean)image.get("front")) {
                url = (String)image.get("image");
            }
        }
    }

    public String getUrl() {
        return url;
    }
}
