package au.com.anthonybruno.lichessclient;


import au.com.anthonybruno.lichessclient.http.Json;
import au.com.anthonybruno.lichessclient.http.JsonClient;
import au.com.anthonybruno.lichessclient.http.JsonResponse;
import au.com.anthonybruno.lichessclient.http.NodeProcessor;
import au.com.anthonybruno.lichessclient.model.Status;
import au.com.anthonybruno.lichessclient.model.account.Email;
import au.com.anthonybruno.lichessclient.model.account.KidModeStatus;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.apache.http.Header;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicHeader;

import java.net.URISyntaxException;
import java.util.Collections;
import java.util.List;

public class LichessClient implements AutoCloseable {

    public static final String BASE_URL = "https://lichess.org";
    private final JsonClient httpClient;

    public LichessClient(String apiToken) {
        List<Header> defaultHeaders = Collections.singletonList(new BasicHeader("Authorization", "Bearer " + apiToken));
        this.httpClient = new JsonClient(HttpClientBuilder.create().setDefaultHeaders(defaultHeaders).build());
    }

    public ObjectNode getMyProfile() {
        return (ObjectNode) get(URLS.ACCOUNT + "/me");
    }

    public String getMyEmailAddress() {
        return get(URLS.ACCOUNT + "/email", Email.class).getEmail();
    }

    public ObjectNode getMyPreferences() {
        return (ObjectNode) get(URLS.ACCOUNT + "/preferences");
    }

    public boolean getMyKidModeStatus() {
        return get(URLS.ACCOUNT + "/kid", KidModeStatus.class).isOn();
    }

    public Status setMyKidModeStatus(boolean status) {
        String url;
        try {
            url = new URIBuilder(URLS.ACCOUNT + "/kid").addParameter("v", status + "").build().toString();
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
        return post(url, Status.class);
    }

    public Status upgradeToBotAccount() {
        return post(URLS.BOT + "/account/upgrade", Status.class);
    }

    public void streamIncomingEvents(NodeProcessor processor) {
        httpClient.getAndStream(URLS.STREAM + "/event", processor);
    }

    public void streamGameState(String gameId, NodeProcessor processor) {
        httpClient.getAndStream(URLS.BOT + "/game/stream/" + gameId, processor);
    }

    public Status makeMove(String gameId, String move) {
        String url = URLS.BOT + "/game/" + gameId + "/move/" + move;
        return post(url, Status.class);
    }

    public Status writeInChat(String gameId, String room, String message) {
        String url = URLS.BOT + "/game/" + gameId + "/chat";
        ObjectNode json = Json.createJsonObject();
        json.put("room", room);
        json.put("text", message);
        return post(url, json, Status.class);
    }


    public Status abortGame(String gameId) {
        String url = URLS.BOT + "/game/" + gameId + "/abort";
        return post(url, Status.class);
    }

    public Status resignGame(String gameId) {
        String url = URLS.BOT + "/game/" + gameId + "/resign";
        return post(url, Status.class);
    }

    public Status acceptChallenge(String challengeId) {
        return post(URLS.CHALLENGE + "/" + challengeId + "/accept", Status.class);
    }

    public Status declineChallenge(String challengeId) {
        return post(URLS.CHALLENGE + "/" + challengeId + "/decline", Status.class);
    }

    public ObjectNode getMembersOfTeam(String teamId, Integer max) {
        String url;
        try {
            URIBuilder uriBuilder = new URIBuilder(URLS.TEAM + "/" + teamId + "/users");
            if (max != null) {
                uriBuilder.addParameter("max", String.valueOf(max));
            }
            url = uriBuilder.build().toString();
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
        return (ObjectNode) get(url);
    }

    public ObjectNode getCurrentTournaments() {
        return (ObjectNode) get(URLS.TOURNAMENT.toString());
    }

    private JsonNode get(String url) {
        try (JsonResponse response = httpClient.get(url)) {
            return response.toJson();
        }
    }

    private <T> T get(String url, Class<T> toConvertTo) {
        try (JsonResponse response = httpClient.get(url)) {
            return response.toObject(toConvertTo);
        }
    }

    private <T> T post(String url, Class<T> toConvertTo) {
        try (JsonResponse response = httpClient.post(url)) {
            return response.toObject(toConvertTo);
        }
    }

    private <T> T post(String url, ObjectNode postData, Class<T> toConvertTo) {
        try (JsonResponse response = httpClient.post(url, postData)) {
            return response.toObject(toConvertTo);
        }
    }

    @Override
    public void close() throws Exception {
        httpClient.close();
    }
}
