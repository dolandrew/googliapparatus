package googliapparatus.service;

import googliapparatus.entity.SongEntity;
import googliapparatus.entity.SongEntityStaging;
import googliapparatus.repository.SongEntityRepository;
import googliapparatus.repository.SongEntityStagingRepository;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.util.UUID;

import static org.hibernate.internal.util.collections.CollectionHelper.isNotEmpty;

@Component
@EnableScheduling
public class SongLoader {

    private final RestTemplate restTemplate;

    private final SongEntityStagingRepository songEntityStagingRepository;

    private final SongEntityRepository songEntityRepository;

    private final GoogliTweeter googliTweeter;

    private static final String PHISH_NET_URL = "http://www.phish.net";

    private static final Logger LOG = LoggerFactory.getLogger(SongLoader.class);

    public SongLoader(RestTemplate restTemplate, SongEntityStagingRepository songEntityStagingRepository, SongEntityRepository songEntityRepository, GoogliTweeter googliTweeter) {
        this.restTemplate = restTemplate;
        this.songEntityStagingRepository = songEntityStagingRepository;
        this.songEntityRepository = songEntityRepository;
        this.googliTweeter = googliTweeter;
    }

    @Scheduled(cron="${cron.load.songs}")
    public void loadSongs() throws InterruptedException {
        googliTweeter.tweet("Checking for new songs to load into GoogliApparatus...");
        String response = restTemplate.getForObject(PHISH_NET_URL + "/songs", String.class);
        Document doc = Jsoup.parse(response);
        Elements elements = doc.getElementsByTag("tr");
        int allSongs = 1;
        int newSongs = 0;
        songEntityStagingRepository.deleteAll();
        for (Element element : elements.subList(1, elements.size())) {
            newSongs = processSong(element, newSongs);
            Thread.sleep(10000);
            LOG.warn("...processed " + allSongs++ + " / " + elements.size() + " songs...");
        }
        googliTweeter.tweet("Finished processing " + allSongs + " songs successfully. Loading staged songs...");
        for (SongEntityStaging song : songEntityStagingRepository.findAll()) {
            SongEntity songEntity = new SongEntity();
            songEntity.setId(song.getId());
            songEntity.setLink(song.getLink());
            songEntity.setLyrics(song.getLyrics());
            songEntity.setName(song.getName());
            songEntity.setNameLower(song.getNameLower());
            songEntity.setLyricsBy(song.getLyricsBy());
            songEntityRepository.save(songEntity);
        }
        googliTweeter.tweet("Finished loading staged songs.");
    }

    @Transactional()
    public int processSong(Element element, int j) throws InterruptedException {
        SongEntityStaging songEntity = new SongEntityStaging();

        Element td = element.getElementsByTag("td").get(0);
        String songName = td.wholeText();
        if (isNotEmpty(songEntityRepository.findAllByName(songName))) {
            LOG.warn("Skipping " + songName + " because it is already loaded.");
            return j;
        }
        LOG.warn("Processing " + songName + "...");
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
        LOG.warn("Finished processing " + songName + " successfully.");
        songEntityStagingRepository.save(songEntity);
        return j++;
    }

    private void setLyricsBy(SongEntityStaging songEntity, String cleanLyrics) {
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
                            .replace("seven below inc.", "")
                            .replace("©", "")
                            .replace("ï¿½", "")
                            .replace("  ", " ");
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