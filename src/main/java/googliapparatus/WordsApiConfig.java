package googliapparatus;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class WordsApiConfig {

    @Value("${words.api.key}")
    private String apiKey;

    public final String getApiKey() {
        return apiKey;
    }
}
