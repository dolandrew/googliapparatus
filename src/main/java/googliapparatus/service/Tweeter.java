package googliapparatus.service;

import oauth.signpost.OAuthConsumer;
import oauth.signpost.commonshttp.CommonsHttpOAuthConsumer;
import oauth.signpost.exception.OAuthCommunicationException;
import oauth.signpost.exception.OAuthExpectationFailedException;
import oauth.signpost.exception.OAuthMessageSignerException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.Charset;

@Service
public class Tweeter {

    private static final Logger LOG = LoggerFactory.getLogger(Tweeter.class);

    @Value("${twitter.api.key}")
    private String apiKey;

    @Value("${twitter.api.key.secret}")
    private String apiKeySecret;

    @Value("${twitter.access.token}")
    private String accessToken;

    @Value("${twitter.access.token.secret}")
    private String accessTokenSecret;

    @Autowired
    private Environment environment;

    public void tweet(String query) throws OAuthCommunicationException, OAuthExpectationFailedException, OAuthMessageSignerException, IOException {

        OAuthConsumer oAuthConsumer = new CommonsHttpOAuthConsumer(apiKey,
                apiKeySecret);
        oAuthConsumer.setTokenWithSecret(accessToken, accessTokenSecret);

        String tweet = "New Googli search: " + query;

        String encodedTweet = URLEncoder.encode(tweet, Charset.defaultCharset());

        HttpPost httpPost = new HttpPost(
                "https://api.twitter.com/1.1/statuses/update.json?status=" + encodedTweet);

        oAuthConsumer.sign(httpPost);

        LOG.warn("Created tweet: \"" + tweet + "\"");

        HttpClient httpClient = new DefaultHttpClient();
        if (environment.getActiveProfiles().length > 0  && environment.getActiveProfiles()[0].equals("local")) {
            return;
        }
        LOG.warn("Tweeting...");
        httpClient.execute(httpPost);
        LOG.warn("Successfully posted tweet.");
    }
}