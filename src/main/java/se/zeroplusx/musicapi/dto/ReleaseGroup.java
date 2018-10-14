package se.zeroplusx.musicapi.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class ReleaseGroup {
    @JsonProperty(value = "primary-type")
    String primaryType;
    String id;
    String title;
}
