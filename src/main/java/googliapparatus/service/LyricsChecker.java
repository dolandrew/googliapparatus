package googliapparatus.service;

import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@EnableScheduling
@Service
public class LyricsChecker {

    private final GoogliTweeter googliTweeter;

    private final RestTemplate restTemplate;

    private static final String PHISH_NET_URL = "http://www.phish.net";

    public LyricsChecker(GoogliTweeter googliTweeter, RestTemplate restTemplate) {
        this.googliTweeter = googliTweeter;
        this.restTemplate = restTemplate;
    }

    @Scheduled(cron="0 0 */2 * * *")
    public void checkForLyrics() {
        try {
            String response = restTemplate.getForObject(PHISH_NET_URL + "/song/i-am-in-miami/lyrics", String.class);
            if (!response.contains("No known lyrics")) {
                googliTweeter.tweet("SciFi Soldier lyrics have been added! Update the googli.");
            }
        } catch (Exception e) {
            googliTweeter.tweet("GoogliApparatus caught exception: " + e.getCause());
        }
    }
}