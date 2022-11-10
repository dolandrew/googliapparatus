package googliapparatus.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public final class CaffeineService {

    private static final Logger LOG = LoggerFactory.getLogger(
            CaffeineService.class);

    private RestTemplate restTemplate = new RestTemplateBuilder().build();

    @Scheduled(cron = "0 */15 * * * *")
    public void guayusa() {
        LOG.info("Caffeinating...");
        restTemplate.getForObject(
                "http://googli-apparatus-backend.herokuapp.com/index.html",
                String.class);
    }
}
