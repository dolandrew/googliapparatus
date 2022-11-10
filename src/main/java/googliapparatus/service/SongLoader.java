package googliapparatus.service;

import googliapparatus.entity.SongEntity;
import googliapparatus.repository.SongEntityRepository;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.UUID;

import static org.hibernate.internal.util.collections.CollectionHelper.isNotEmpty;

@Component
@EnableScheduling
public class SongLoader {

    private RestTemplate restTemplate = new RestTemplateBuilder().build();

    private final SongEntityRepository songEntityRepository;

    private final GoogliTweeter googliTweeter;

    private static final String PHISH_NET_URL = "http://www.phish.net";

    private static final Logger LOG = LoggerFactory.getLogger(SongLoader.class);

    public SongLoader(final SongEntityRepository entityRepository,
                      final GoogliTweeter tweeter) {
        this.songEntityRepository = entityRepository;
        this.googliTweeter = tweeter;
    }

    @Scheduled(cron = "${cron.load.songs}")
    @Transactional
    public final void loadSongs() {
        try {
            LOG.info("Checking for new songs to load...");
            String response = restTemplate.getForObject(PHISH_NET_URL
                    + "/songs", String.class);
            processSongs(response);
        } catch (Exception e) {
            googliTweeter.tweet("GoogliApparatus caught exception "
                    + "while loading songs: " + e.getCause());
        }
    }

    private void addSongIfNew(final Element td) throws InterruptedException {
        String songName = td.wholeText();
        if (isNotEmpty(songEntityRepository.findAllByName(songName))) {
            LOG.info("Skipping " + songName + " because it is already loaded.");
            return;
        }
        String link = PHISH_NET_URL + td.getElementsByTag("a")
                .attr("href") + "/lyrics";
        saveSong(songName, link);
        Thread.sleep(10000);
    }

    private void saveSong(final String songName, final String link) {
        SongEntity songEntity = new SongEntity();
        songEntity.setId(UUID.randomUUID().toString());
        songEntity.setLink(link);
        songEntity.setName(songName);
        songEntity.setNameLower(songName.toLowerCase());
        String cleanedLyrics = getCleanedLyrics(songEntity);
        if (cleanedLyrics != null) {
            songEntity.setLyrics(cleanedLyrics);
            songEntity.setLyricsBy(getLyricsBy(cleanedLyrics));
        }

        LOG.info("Added " + songName + " successfully.");
        songEntityRepository.save(songEntity);
    }

    private String getCleanedLyrics(final SongEntity songEntity) {
        String lyricsResponse = restTemplate.getForObject(songEntity.getLink(),
                String.class);
        if (lyricsResponse != null) {
            Document lyricsDoc = Jsoup.parse(lyricsResponse);
            var blockquotes = lyricsDoc.getElementsByTag("blockquote");
            if (blockquotes.size() > 0) {
                Element lyrics = blockquotes.get(0);
                String cleanLyrics = cleanPreLyricText(lyrics);
                return removeCreditsFromLyrics(cleanLyrics).toLowerCase();
            }
        }
        return null;
    }

    private void processSongs(final String response) {
        Document doc = Jsoup.parse(response);
        Elements elements = doc.getElementsByTag("tr");
        List<Element> subList = elements.subList(1, elements.size());
        for (int i = 0; i < subList.size(); i++) {
            Element element = subList.get(i);
            processSong(element);
            LOG.info("Processed " + i + 1 + " / " + elements.size() + " songs"
                    + "...");
        }
    }

    private void processSong(final Element element) {
        Element td = element.getElementsByTag("td").get(0);
        String songName = td.wholeText();
        try {
            addSongIfNew(td);
        } catch (Exception e) {
            googliTweeter.tweet("GoogliApparatus caught exception "
                    + "while loading " + songName + " : " + e.getCause());
            // continue processing songs
        }
    }

    private String getLyricsBy(final String cleanLyrics) {
        int openParen = cleanLyrics.indexOf("(");
        int closeParen = cleanLyrics.indexOf(")");
        if (openParen != -1 && closeParen != -1) {
            return cleanLyrics.substring(openParen + 1, closeParen);
        }
        return null;
    }

    private String cleanPreLyricText(final Element lyrics) {
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
                .replace("seven below inc.", "")
                .replace("©", "")
                .replace("ï¿½", "")
                .replace("  ", " ");
    }

    private String removeCreditsFromLyrics(final String lyricsWithCredits) {
        return lyricsWithCredits.replace("(Anastasio)", "")
                .replace("(Gordon)", "")
                .replace("(Lewis)", "")
                .replace("(Reed)", "")
                .replace("(Anastasio, Marshall)", "")
                .replace("(Anastasio/Fishman/Gordon/"
                        + "McConnell/Dude of Life)", "")
                .replace("(Fishman)", "")
                .replace("(Anastasio/Fishman)", "")
                .replace("(Anastasio/Fishman/"
                        + "Gordon/McConnell)", "")
                .replace("(Anastasio/Gordon/"
                        + "Fishman/McConnell/Long)", "")
                .replace("(Anastasio/Marshall/"
                        + "Gordon/McConnell/Fishman)", "")
                .replace("(Anastasio/Marshall/Woolf)", "")
                .replace("(Byrne)  Warner "
                        + "Chappell  (ASCAP)", "")
                .replace("(Diamond/Horovitz/Yauch)", "")
                .replace("(Dylan)  Copyright  1968 by Dwarf Music; "
                        + "renewed 1996 by Dwarf Music", "")
                .replace("(Fishman/Anastasio/"
                        + "Marshall/McConnell)", "")
                .replace("(Gordon, Linitz)", "")
                .replace("(Gordon/Anastasio/Fishman/McConnell)",
                        "")
                .replace("(Hendrix)   Experience"
                        + " Hendrix LLC (ASCAP)", "")
                .replace("(Howard/Emerson)", "")
                .replace("(O'Brien)", "")
                .replace("(Seals)", "")
                .replace("(Ween)", "")
                .replace("(Anastasio/O'Brien)", "")
                .replace("(Anastasio/Marshall/Herman)", "")
                .replace("(Garcia/Hunter)", "")
                .replace("(Anastasio/Marshall/Woolf/Szuter)",
                        "")
                .replace("(Anastasio, Fishman, Gordon, "
                        + "Marshall, McConnell)", "")
                .replace("(Anastasio/Marshall)", "")
                .replace("(Anastasio/Pollak)", "")
                .replace("(Anastasio, Goodman)", "")
                .replace("(Anastasio/Fishman/Gordon/"
                        + "McConnell/Marshall)", "")
                .replace("(Anastasio/Dude of Life)", "")
                .replace("   by   ", "")
                .replace("(Anastasio/Abrahams/The Dude of Life)",
                        "");
    }
}
