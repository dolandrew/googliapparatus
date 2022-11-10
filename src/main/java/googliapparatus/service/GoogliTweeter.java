package googliapparatus.service;

import googliapparatus.GoogliConfig;
import oauth.signpost.OAuthConsumer;
import oauth.signpost.commonshttp.CommonsHttpOAuthConsumer;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import java.net.URLEncoder;
import java.nio.charset.Charset;

@Service
public final class GoogliTweeter {

    private static final Logger LOG = LoggerFactory.getLogger(
            GoogliTweeter.class);

    private final GoogliConfig googliConfig;

    private final Environment environment;

    public GoogliTweeter(final GoogliConfig config,
                         final Environment env) {
        this.googliConfig = config;
        this.environment = env;
    }

    public void tweet(final String tweet) {
        LOG.warn("@GoogliApparatus tweeted: \"" + tweet + "\"");
        tweet(tweet + "\n\n" + System.currentTimeMillis(),
                googliConfig.getApiKey(), googliConfig.getApiKeySecret(),
                googliConfig.getAccessToken(),
                googliConfig.getAccessTokenSecret());
    }

    public void tweet(final String tweet, final String apiKey,
                      final String apiKeySecret, final String accessToken,
                      final String accessTokenSecret) {
        String encodedTweet = URLEncoder.encode(tweet,
                Charset.defaultCharset());
        String url = "https://api.twitter.com/1.1/statuses/update.json?status="
                + encodedTweet;
        String failureMessage = "Error trying to tweet: \"" + tweet + "\".";

        post(url, failureMessage, apiKey, apiKeySecret,
                accessToken, accessTokenSecret, tweet);
    }

    private void post(final String url, final String failureMessage,
                      final String apiKey, final String apiKeySecret,
                      final String accessToken, final String accessTokenSecret,
                      final String tweet) {
        try {
            OAuthConsumer oAuthConsumer = new CommonsHttpOAuthConsumer(apiKey,
                    apiKeySecret);
            oAuthConsumer.setTokenWithSecret(accessToken, accessTokenSecret);
            HttpPost httpPost = new HttpPost(url);
            oAuthConsumer.sign(httpPost);
            HttpClient httpClient = new DefaultHttpClient();
            if (localEnvironment()) {
                if (tweet != null) {
                    LOG.info("Would have tweeted: " + tweet);
                }
                return;
            }
            httpClient.execute(httpPost);
            if (null != null) {
                LOG.info(null);
            }
        } catch (Exception e) {
            LOG.error(failureMessage, e);
        }
    }

    private boolean localEnvironment() {
        return environment.getActiveProfiles().length > 0
                && environment.getActiveProfiles()[0].equals("local");
    }
}
