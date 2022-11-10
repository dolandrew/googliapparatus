package googliapparatus;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GoogliConfig {

    @Value("${twitter.api.key}")
    private String apiKey;

    @Value("${twitter.api.key.secret}")
    private String apiKeySecret;

    @Value("${twitter.access.token}")
    private String accessToken;

    @Value("${twitter.access.token.secret}")
    private String accessTokenSecret;

    public final String getApiKey() {
        return apiKey;
    }

    public final String getApiKeySecret() {
        return apiKeySecret;
    }

    public final String getAccessToken() {
        return accessToken;
    }

    public final String getAccessTokenSecret() {
        return accessTokenSecret;
    }
}
