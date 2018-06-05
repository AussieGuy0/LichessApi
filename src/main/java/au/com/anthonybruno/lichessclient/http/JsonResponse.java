package au.com.anthonybruno.lichessclient.http;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.CloseableHttpResponse;

import java.io.IOException;

public class JsonResponse implements AutoCloseable {

    private final CloseableHttpResponse httpResponse;

    public JsonResponse(CloseableHttpResponse httpResponse) {
        this.httpResponse = httpResponse;
    }

    public JsonNode toJson() {
        try {
            return Json.readJson(httpResponse.getEntity().getContent());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public <T> T toObject(Class<T> c) {
        try {
            return Json.readJson(httpResponse.getEntity().getContent(), c);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void close() {
        try {
            httpResponse.close();
        } catch (IOException e) {
            //closing quietly!
        }
    }
}
