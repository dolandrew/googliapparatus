package googliapparatus.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.HashSet;
import java.util.Set;

@JsonIgnoreProperties(ignoreUnknown = true)
public final class SongDto {
    private String link;

    private Set<String> lyricSnippets = new HashSet<>();

    private String name;

    public String getLink() {
        return link;
    }

    public Set<String> getLyricSnippets() {
        return lyricSnippets;
    }

    public String getName() {
        return name;
    }
}
