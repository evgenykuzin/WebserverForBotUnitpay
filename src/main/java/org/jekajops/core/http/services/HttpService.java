package org.jekajops.core.http.services;

import org.jekajops.core.http.body_builders.BodyBuilder;
import org.jekajops.core.http.headers.Header;
import org.jekajops.core.http.headers.HeadersModel;
import org.jekajops.core.http.models.ClientModel;
import org.jekajops.core.http.models.RequestModel;
import org.jekajops.core.http.models.ResponseModel;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.CloseableHttpClient;

import java.net.http.HttpClient;
import java.net.http.HttpRequest;

public interface HttpService<Request, Client> {
    String POST = "POST";
    String GET = "GET";
    String PUT = "PUT";
    String DELETE = "DELETE";
    String TYPE_JSON = "application/json; charset=UTF-8";
    Header JSON_CONTENTTYPE_HEADER = new Header("Content-Type", TYPE_JSON);
    HttpService<HttpRequest, HttpClient> JAVA_NET_SERVICE = new NetHttpService();
    HttpService<HttpUriRequest, CloseableHttpClient> APACHE_HTTP_SERVICE = new DefaultHttpService();
    ClientModel<Client> constructClient();
    RequestModel<Request> constructRequest(String url, String method, HeadersModel headers, BodyBuilder bodyBuilder);
    ResponseModel getResponse(RequestModel<Request> request);
}
