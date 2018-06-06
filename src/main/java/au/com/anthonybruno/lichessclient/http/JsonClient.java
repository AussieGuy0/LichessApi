package au.com.anthonybruno.lichessclient.http;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class JsonClient implements AutoCloseable {

    private final CloseableHttpClient client;
    
    public JsonClient(CloseableHttpClient httpClient) {
        this.client = httpClient;
    }

    public void getAndStream(String url, NodeProcessor processor) {
        try {
            CloseableHttpResponse response = this.client.execute(new HttpGet(url));
            HttpEntity entity = response.getEntity();
            BufferedReader reader = new BufferedReader(new InputStreamReader(entity.getContent()));
            while (reader.ready()) {
                String line = reader.readLine();
                processor.processNode((ObjectNode) Json.readJson(line));
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public JsonResponse get(String url) {
        try {
            return new JsonResponse(this.client.execute(new HttpGet(url)));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public JsonResponse post(String url) {
        return post(url, null);
    }

    public JsonResponse post(String url, Object body) {
        return post(url, Json.writeObjectToJson(body));
    }

    public JsonResponse post(String url, JsonNode json) {
        HttpPost httpPost = new HttpPost(url);
        if (json != null) {
            HttpEntity body = new StringEntity(json.toString(), ContentType.APPLICATION_JSON);
            httpPost.setEntity(body);
        }
        try {
            return new JsonResponse(this.client.execute(httpPost));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void close() throws Exception {
        client.close();
    }
}
