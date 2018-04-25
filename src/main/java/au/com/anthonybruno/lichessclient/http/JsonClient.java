package au.com.anthonybruno.lichessclient.http;

import com.fasterxml.jackson.databind.JsonNode;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;

import java.io.IOException;

public class JsonClient {

    private final HttpClient client;
    
    public JsonClient(HttpClient httpClient) {
        this.client = httpClient;
    }

    public JsonResponse get(String url) {
        try {
            return new JsonResponse(this.client.execute(new HttpGet(url)));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public JsonResponse post(String url, Object body) {
        return post(url, Json.writeObjectToJson(body));
    }

    public JsonResponse post(String url, JsonNode json) {
        HttpPost httpPost = new HttpPost(url);
        HttpEntity body = new StringEntity(json.toString(), ContentType.APPLICATION_JSON);
        httpPost.setEntity(body);
        try {
            return new JsonResponse(this.client.execute(httpPost));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
