package org.jekajops.payment_service.core.http.services;

import org.jekajops.payment_service.core.http.body_builders.JsonBodyBuilder;
import org.jekajops.payment_service.core.http.body_builders.JsonBodyBuilder.BodyJson;
import org.jekajops.payment_service.core.http.headers.Header;
import org.jekajops.payment_service.core.http.headers.HeadersModel;
import org.jekajops.payment_service.core.http.headers.HeadersModelImpl;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.methods.RequestBuilder;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.jekajops.payment_service.core.http.body_builders.BodyBuilder;
import org.jekajops.payment_service.core.http.models.ClientModel;
import org.jekajops.payment_service.core.http.models.RequestModel;
import org.jekajops.payment_service.core.http.models.ResponseModel;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;

public class DefaultHttpService implements HttpService<HttpUriRequest, CloseableHttpClient> {

    @Override
    public ClientModel<CloseableHttpClient> constructClient() {
        return () -> HttpClientBuilder.create().build();
    }

    public RequestModel<HttpUriRequest> constructRequest(String url, BodyBuilder bodyBuilder) {
        HeadersModel headers = new HeadersModelImpl("Content-Type", TYPE_JSON);
        return constructRequest(url, POST, headers, bodyBuilder);
    }

    @Override
    public RequestModel<HttpUriRequest> constructRequest(String url, String method, HeadersModel headers, BodyBuilder bodyBuilder) {
        final RequestBuilder request;
        switch (method) {
            case POST:
                request = RequestBuilder.post();
                break;
            case GET:
                request = RequestBuilder.get();
                break;
            case PUT:
                request = RequestBuilder.put();
                break;
            case DELETE:
                request = RequestBuilder.delete();
                break;
            default:
                request = null;
        }
        if (request == null) throw new IllegalArgumentException("http method is not correct (" + method + ")");
        try {
            request.setUri(URI.create(url));
            for (Header header : headers.getSet()) {
                request.addHeader(header.getKey(), header.getValue());
            }
            request.setEntity(new StringEntity(bodyBuilder.getJsonString()));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new RequestModel<>(
                url,
                request.toString(),
                bodyBuilder.getJsonString(),
                headers
        ) {
            @Override
            public HttpUriRequest getRequest() {
                return request.build();
            }
        };
    }

    @Override
    public ResponseModel getResponse(RequestModel<HttpUriRequest> request) {
        try {
            CloseableHttpClient client = constructClient().client();
            CloseableHttpResponse response = client.execute(request.getRequest());
            return new ResponseModel(
                    readResponse(response),
                    response.getStatusLine().getReasonPhrase(),
                    response.getStatusLine().getStatusCode()
            );
        } catch (IOException e) {
            e.printStackTrace();
        }
        return ResponseModel.EMPTY;
    }

    private String readResponse(CloseableHttpResponse response) {
        StringBuilder builder = new StringBuilder();
        if (response == null) return null;
        try {
            BufferedReader bufReader = new BufferedReader(
                    new InputStreamReader(
                            response.getEntity().getContent()
                    )
            );
            String line;
            while ((line = bufReader.readLine()) != null) {
                builder.append(line);
                builder.append(System.lineSeparator());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return builder.toString();
    }

    public static void main(String[] args) {
        DefaultHttpService service = new DefaultHttpService();
        RequestModel<HttpUriRequest> requestModel = service.constructRequest(
                "http://localhost:8080/TrollVkBot_war/obj",
                POST,
                new HeadersModelImpl(JSON_CONTENTTYPE_HEADER),
                (JsonBodyBuilder) object -> {
                    object.addProperty("cmd", "history");
                    object.addProperty("type", "out");
                    object.addProperty("status", "Success");
                    object.addProperty("phone", "89523663611");
                    object.addProperty("user", "user");
                    object.addProperty("start", "88888888");
                    object.addProperty("duration", "124");
                    object.addProperty("link", "https://records/record1.mp3");
                    object.addProperty("crm_token", "123456789qwerty");
                    object.addProperty("callid", "12345");
                    return new BodyJson(object);
                }
        );
        System.out.println(requestModel.toString());
        ResponseModel responseModel = service.getResponse(requestModel);
        System.out.println(responseModel.toString());
    }
}
