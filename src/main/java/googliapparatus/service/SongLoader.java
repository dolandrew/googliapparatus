package googliapparatus.service;

import googliapparatus.entity.SongEntity;
import googliapparatus.repository.SongEntityRepository;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.UUID;

import static org.hibernate.internal.util.collections.CollectionHelper.isNotEmpty;

@Component
public class SongLoader {

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private SongEntityRepository songEntityRepository;

    private String PHISH_NET_URL = "http://www.phish.net";

//    @PostConstruct
    public void loadSongs() throws InterruptedException {
        String response = restTemplate.getForObject(PHISH_NET_URL + "/songs", String.class);
        Document doc = Jsoup.parse(response);
        Elements elements = doc.getElementsByTag("tr");
        for (Element element : elements.subList(1, elements.size())) {
            Element td = element.getElementsByTag("td").get(0);
            String songName = td.wholeText();
            if (isNotEmpty(songEntityRepository.findAllByName(songName))) {
                continue;
            }
            SongEntity songEntity = new SongEntity();
            songEntity.setId(UUID.randomUUID().toString());
            songEntity.setLink(PHISH_NET_URL + td.getElementsByTag("a").attr("href") + "/lyrics");
            songEntity.setName(songName);
            songEntity.setNameLower(songName.toLowerCase());

            String lyricsResponse = restTemplate.getForObject(songEntity.getLink(), String.class);
            Document lyricsDoc = Jsoup.parse(lyricsResponse);
            Elements blockquotes = lyricsDoc.getElementsByTag("blockquote");
            if (blockquotes.size() > 0) {
                Element lyrics = blockquotes.get(0);
                String cleanLyrics = cleanPreLyricText(lyrics);
                setLyricsBy(songEntity, cleanLyrics);
                songEntity.setLyrics(removeCreditsFromLyrics(cleanLyrics).toLowerCase());
            }
            songEntityRepository.save(songEntity);
            Thread.sleep(15000);
        }
    }

    private void setLyricsBy(SongEntity songEntity, String cleanLyrics) {
        int openParen = cleanLyrics.indexOf("(");
        int closeParen = cleanLyrics.indexOf(")");
        if (openParen != -1 && closeParen != -1) {
            String lyricsBy = cleanLyrics.substring(openParen + 1, closeParen);
            songEntity.setLyricsBy(lyricsBy);
        }
    }

    private String cleanPreLyricText(Element lyrics) {
        return lyrics.wholeText()
                            .replaceAll("[\r\n\t]", " ")
                            .replace("Who Is She?", "")
                            .replace("Music, Inc.", "")
                            .replace("Music, Inc", "")
                            .replace("by  Music, Inc", "")
                            .replace("Music, BMI", "")
                            .replace("Music (BMI)", "")
                            .replace("(BMI)", "")
                            .replace("(BMI_", "")
                            .replace("©", "")
                            .replace("ï¿½", "");
    }

    private String removeCreditsFromLyrics(String cleanLyrics) {
        cleanLyrics = cleanLyrics.replace("(Anastasio)", "")
                .replace("(Gordon)", "")
                .replace("(Lewis)", "")
                .replace("(Reed)", "")
                .replace("(Anastasio, Marshall)", "")
                .replace("(Anastasio/Fishman/Gordon/McConnell/Dude of Life)", "")
                .replace("(Fishman)", "")
                .replace("(Anastasio/Fishman)", "")
                .replace("(Anastasio/Fishman/Gordon/McConnell)", "")
                .replace("(Anastasio/Gordon/Fishman/McConnell/Long)", "")
                .replace("(Anastasio/Marshall/Gordon/McConnell/Fishman)", "")
                .replace("(Anastasio/Marshall/Woolf)", "")
                .replace("(Byrne)  Warner Chappell  (ASCAP)", "")
                .replace("(Diamond/Horovitz/Yauch)", "")
                .replace("(Dylan)  Copyright  1968 by Dwarf Music; renewed 1996 by Dwarf Music", "")
                .replace("(Fishman/Anastasio/Marshall/McConnell)", "")
                .replace("(Gordon, Linitz)", "")
                .replace("(Gordon/Anastasio/Fishman/McConnell)", "")
                .replace("(Hendrix)   Experience Hendrix LLC (ASCAP)", "")
                .replace("(Howard/Emerson)", "")
                .replace("(O'Brien)", "")
                .replace("(Seals)", "")
                .replace("(Ween)", "")
                .replace("(Anastasio/O'Brien)", "")
                .replace("(Anastasio/Marshall/Herman)", "")
                .replace("(Garcia/Hunter)", "")
                .replace("(Anastasio/Marshall/Woolf/Szuter)", "")
                .replace("(Anastasio, Fishman, Gordon, Marshall, McConnell)", "")
                .replace("(Anastasio/Marshall)", "")
                .replace("(Anastasio/Pollak)", "")
                .replace("(Anastasio, Goodman)", "")
                .replace("(Anastasio/Fishman/Gordon/McConnell/Marshall)", "")
                .replace("(Anastasio/Dude of Life)", "")
                .replace("   by   ", "")
                .replace("(Anastasio/Abrahams/The Dude of Life)", "");
        return cleanLyrics;
    }
}