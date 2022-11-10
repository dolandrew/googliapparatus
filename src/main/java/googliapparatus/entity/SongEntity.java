package googliapparatus.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public final class SongEntity {
    @Id
    private String id;

    @Column(nullable = false)
    private String link;

    @Column(length = 10485760)
    private String lyrics;

    @Column
    private String lyricsBy;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String nameLower;

    public String getId() {
        return id;
    }

    public void setId(final String s) {
        this.id = s;
    }

    public String getLink() {
        return link;
    }

    public void setLink(final String s) {
        this.link = s;
    }

    public String getLyrics() {
        return lyrics;
    }

    public void setLyrics(final String s) {
        this.lyrics = s;
    }

    public String getLyricsBy() {
        return lyricsBy;
    }

    public void setLyricsBy(final String s) {
        this.lyricsBy = s;
    }

    public String getName() {
        return name;
    }

    public void setName(final String s) {
        this.name = s;
    }

    public String getNameLower() {
        return nameLower;
    }

    public void setNameLower(final String s) {
        this.nameLower = s;
    }
}
