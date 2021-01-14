package org.jekajops.payment_service.core.http.services;

import org.jekajops.payment_service.core.http.body_builders.BodyBuilder;
import org.jekajops.payment_service.core.http.headers.HeadersModel;
import org.jekajops.payment_service.core.http.headers.HeadersModelImpl;
import org.jekajops.payment_service.core.http.models.ClientModel;
import org.jekajops.payment_service.core.http.models.RequestModel;
import org.jekajops.payment_service.core.http.models.RequestModelDefault;
import org.jekajops.payment_service.core.http.models.ResponseModel;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Map;

public class NetHttpService implements HttpService<HttpRequest, HttpClient> {
    @Override
    public ClientModel<HttpClient> constructClient() {
        return HttpClient::newHttpClient;
    }

    @Override
    public RequestModel<HttpRequest> constructRequest(String url, String method, HeadersModel headers, BodyBuilder bodyBuilder) {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .method(method, HttpRequest.BodyPublishers.ofString(bodyBuilder.getJsonString()))
                .build();
        return new RequestModelDefault<>(
                url,
                request.toString(),
                bodyBuilder.getJsonString(),
                request
        );
    }

    @Override
    public ResponseModel getResponse(RequestModel<HttpRequest> request) {
        try {
            HttpClient client = constructClient().client();
            HttpResponse<String> response = client.send(request.getRequest(),
                    HttpResponse.BodyHandlers.ofString());
            return new ResponseModel(
                    response.body(),
                    "",
                    response.statusCode()
            );
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            return ResponseModel.EMPTY;
        }
    }

    public RequestModel<HttpRequest> constructGetRequest(String url, String... params) {
        if (params.length > 0) url += "?";
        StringBuilder urlBuilder = new StringBuilder(url);
        for (int i = 0; i < params.length; i++) {
            urlBuilder.append(params[i]);
            if (i != params.length - 1) urlBuilder.append("&");
        }
        return constructRequest(urlBuilder.toString(), GET, new HeadersModelImpl(), BodyBuilder.NO_BODY);
    }

    public RequestModel<HttpRequest> constructGetRequest(String url, Map<String, String> params) {
        if (params != null && !params.isEmpty()) {
            url += "?";
            StringBuilder urlBuilder = new StringBuilder(url);
            params.forEach((k, v) -> urlBuilder
                    .append(k)
                    .append("=")
                    .append(v)
                    .append("&"));
            urlBuilder.deleteCharAt(urlBuilder.lastIndexOf("&"));
            url = urlBuilder.toString();
        }
        return constructRequest(url, GET, new HeadersModelImpl(), BodyBuilder.NO_BODY);
    }

}
