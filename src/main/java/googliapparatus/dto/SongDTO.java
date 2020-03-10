package googliapparatus.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.HashSet;
import java.util.Set;

@JsonIgnoreProperties(ignoreUnknown = true)
public class SongDTO {
    private String name;

    private String link;

    private Set<String> lyricSnippets = new HashSet<>();

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public Set<String> getLyricSnippets() {
        return lyricSnippets;
    }

    public void setLyricSnippets(Set<String> lyricSnippets) {
        this.lyricSnippets = lyricSnippets;
    }
}
