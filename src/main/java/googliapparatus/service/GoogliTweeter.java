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
public class GoogliTweeter {

    private static final Logger LOG = LoggerFactory.getLogger(GoogliTweeter.class);

    private final GoogliConfig creds;

    private final Environment environment;

    public GoogliTweeter(GoogliConfig creds, Environment environment) {
        this.creds = creds;
        this.environment = environment;
    }

    public void tweet(String tweet) {
        LOG.warn("@GoogliApparatus tweeted: \"" + tweet + "\"");
        tweet(tweet + "\n\n" + System.currentTimeMillis(),
                creds.getApiKey(), creds.getApiKeySecret(),
                creds.getAccessToken(), creds.getAccessTokenSecret());
    }

    public void tweet(String tweet, Throwable e) {
        LOG.warn("@GoogliApparatus tweeted: \"" + tweet + "\"", e);
        tweet(tweet + ": " + e.getCause() + "\n\n" + System.currentTimeMillis(),
                creds.getApiKey(), creds.getApiKeySecret(),
                creds.getAccessToken(), creds.getAccessTokenSecret());
    }

    public void tweet(String tweet, String apiKey, String apiKeySecret, String accessToken, String accessTokenSecret) {
        String encodedTweet = URLEncoder.encode(tweet, Charset.defaultCharset());
        String url = "https://api.twitter.com/1.1/statuses/update.json?status=" + encodedTweet;
        String failureMessage = "Error trying to tweet: \"" + tweet + "\".";

        post(url, null, failureMessage, apiKey, apiKeySecret, accessToken, accessTokenSecret, tweet);
    }

    private void post(String url, String successMessage, String failureMessage, String apiKey, String apiKeySecret, String accessToken, String accessTokenSecret, String tweet) {
        try {
            OAuthConsumer oAuthConsumer = new CommonsHttpOAuthConsumer(apiKey, apiKeySecret);
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
            if (successMessage != null) {
                LOG.info(successMessage);
            }
        } catch (Exception e) {
            LOG.error(failureMessage, e);
        }
    }

    private boolean localEnvironment() {
        return environment.getActiveProfiles().length > 0 && environment.getActiveProfiles()[0].equals("local");
    }
}
