package au.com.anthonybruno.lichessclient;


import au.com.anthonybruno.lichessclient.http.Json;
import au.com.anthonybruno.lichessclient.http.JsonClient;
import au.com.anthonybruno.lichessclient.model.Status;
import au.com.anthonybruno.lichessclient.model.account.Email;
import au.com.anthonybruno.lichessclient.model.account.KidModeStatus;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.apache.http.Header;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicHeader;

import java.net.URISyntaxException;
import java.util.Collections;
import java.util.List;

public class LichessClient {

    public static final String BASE_URL ="https://lichess.org";
    private final JsonClient httpClient;

    public LichessClient(String apiToken) {
        List<Header> defaultHeaders = Collections.singletonList(new BasicHeader("Authorization", "Bearer " + apiToken));
        this.httpClient = new JsonClient(HttpClientBuilder.create().setDefaultHeaders(defaultHeaders).build());
    }

    public ObjectNode getMyProfile() {
        return (ObjectNode) httpClient.get(URLS.ACCOUNT + "/me").toJson();
    }

    public Email getMyEmailAddress() {
        return httpClient.get(URLS.ACCOUNT + "/email").toObject(Email.class);
    }

    public ObjectNode getMyPreferences() {
        return (ObjectNode) httpClient.get(URLS.ACCOUNT + "/preferences").toJson();
    }

    public KidModeStatus getMyKidModeStatus() {
        return httpClient.get(URLS.ACCOUNT + "/kid").toObject(KidModeStatus.class);
    }

    public Status setMyKidModeStatus(boolean status) {
        String url;
        try {
            url = new URIBuilder(URLS.ACCOUNT + "/kid").addParameter("v", status + "").build().toString();
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
        return httpClient.post(url).toObject(Status.class);
    }

    public Status upgradeToBotAccount() {
        return httpClient.post(URLS.BOT + "/account/upgrade").toObject(Status.class);
    }

    public Status makeMove(String gameId, String move) {
        String url = URLS.BOT + "/game/" + gameId + "/move/" + move;
        return  httpClient.post(url).toObject(Status.class);
    }

    public Status abortGame(String gameId) {
        String url = URLS.BOT + "/game/" + gameId + "/abort";
        return httpClient.post(url).toObject(Status.class);
    }

    public Status acceptChallenge(String challengeId) {
        return httpClient.post(URLS.CHALLENGE + "/" + challengeId + "/accept").toObject(Status.class);
    }

    public Status declineChallenge(String challengeId) {
        return httpClient.post(URLS.CHALLENGE + "/" + challengeId + "/decline").toObject(Status.class);
    }


}
