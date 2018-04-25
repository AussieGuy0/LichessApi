package au.com.anthonybruno.lichessclient;


import au.com.anthonybruno.lichessclient.http.Json;
import au.com.anthonybruno.lichessclient.http.JsonClient;
import au.com.anthonybruno.lichessclient.model.account.Email;
import au.com.anthonybruno.lichessclient.model.account.KidModeStatus;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.apache.http.Header;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicHeader;

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

    public ObjectNode setMyKidModeStatus(boolean status) {
        ObjectNode toPost = Json.createJsonObject();
        toPost.set("v", JsonNodeFactory.instance.booleanNode(status));
        return (ObjectNode) httpClient.post(URLS.ACCOUNT + "/kid", toPost).toJson();
    }


}
