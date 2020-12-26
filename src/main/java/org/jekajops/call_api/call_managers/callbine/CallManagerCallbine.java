package org.jekajops.call_api.call_managers.callbine;

import org.jekajops.call_api.call_managers.CallManagerInterface;
import org.jekajops.call_api.call_managers.time.TimeUtil;
import org.jekajops.call_api.exceptions.CallException;
import org.jekajops.call_api.exceptions.LoaderException;
import org.jekajops.core.database.Database;
import org.jekajops.core.entities.Order;
import org.jekajops.core.http.body_builders.BodyBuilder;
import org.jekajops.core.http.headers.HeadersModel;
import org.jekajops.core.http.models.RequestModel;
import org.jekajops.core.http.services.HttpService;
import org.jekajops.core.http.services.NetHttpService;
import org.jekajops.core.utils.files.FileManager;
import org.jekajops.core.utils.parsers.JsonObjectParser;

import javax.annotation.Nullable;
import java.io.File;
import java.io.IOException;
import java.net.http.HttpRequest;
import java.sql.SQLException;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class CallManagerCallbine implements CallManagerInterface {
    private static final String CAMPAIGN_ID = "O6F71160242319494E5C817BD";
    private static final String API_TOKEN = "9c69dee8-9de9-6b0a-0f03-af602afa";
    private final NetHttpService httpService;
    private static final Logger logger = Logger.getGlobal();

    public CallManagerCallbine() {
        httpService = new NetHttpService();
    }

    @Override
    public void call(Set<Order> orders, String audioUrl) {
        for (Order order : orders) {
            var params = initParams();
            params.put("campaign_id", CAMPAIGN_ID);
            params.put("other1", "[audio:" + audioUrl + "]");
            params.put("phone", order.phone());
            var request = requestMethod("add", params);
            var response = httpService.getResponse(request);
            logger.log(Level.INFO, "call response: "+response);
            var jop = new JsonObjectParser(response.getResponseString());
            var statusText = jop.get("status");
            if (!statusText.equals("success")) {
                logger.log(Level.WARNING, "error in " + order + " while calling with status " + statusText);
                turnMoneyBack(order);
                continue;
            }
            var callId = jop.get("call_id");
            try {
                new Database().updateOrderCallId(order.id(), callId);
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        }
    }

    @Override
    public File record(Order order) throws CallException, IOException {
        String recordUrl = getStatus(order.callId()).getRecord_url();
        if (recordUrl == null || recordUrl.isEmpty()) {
            throw new LoaderException("record for " + order + " is not complete yet");
        }
        return FileManager.download(recordUrl, "record", "mp3");
    }

    private Status getStatus(String callId) {
        var params = initParams();
        params.put("call_id", callId);
        var request = requestMethod("status", params);
        var response = httpService.getResponse(request);
        logger.log(Level.INFO, "getStatus response: " + response);
        JsonObjectParser jop = new JsonObjectParser(response.getResponseString());
        return new Status(
                callId,
                jop.get("phone"),
                jop.get("duration"),
                jop.get("record_url"),
                jop.get("status"),
                jop.get("attempt"),
                jop.get("completion_flag")
        );
    }

    @Override
    public void checkErrors(@Nullable Order order) {
        if (order == null) return;
        Status status = getStatus(order.callId());
        boolean hasError = false;
        String stStr = status.getStatus();
        if (stStr.matches("[0-9]")) {
            int st = Integer.parseInt(stStr);
            hasError = (st > 3 && st != 5 && st != 255);
        } else if (stStr.equals("error")) {
            hasError = true;
            logger.log(Level.WARNING, "error in " + status);
        }
        var dif = new Date().getTime() - order.startTime();
        if (!hasError) {
            hasError = dif > TimeUtil.HOUR * 8;
            if (hasError) logger.log(Level.WARNING,"too long call for order {" + order + "}");
        }
        if (hasError) {
            logger.log(Level.INFO, "order {" + order + "} has status: " + stStr);
            removeCall(order);
            turnMoneyBack(order);
        }

    }

    public void checkErrors(Collection<Order> orders) {
        if (orders == null || orders.isEmpty()) return;
        for (Order order : orders) {
            checkErrors(order);
        }
    }

    @Override
    public String getApiUrl() {
        return "https://callbine.ru/api/v1/call/";
    }

    @Override
    public String getApiToken() {
        return API_TOKEN;
    }

    private void removeCall(Order order) {
        var params = new HashMap<String, String>();
        params.put("api_key", getApiToken());
        params.put("call_id", order.callId());
        var request = requestMethod("remove", params);
        var response = httpService.getResponse(request);
    }

    private RequestModel<HttpRequest> requestMethod(String command, Map<String, String> params) {
        StringBuilder respString = new StringBuilder(getApiUrl());
        respString.append(command).append("/?");
        int counter = 0;
        int limit = params.entrySet().size();
        for (var entry : params.entrySet()) {
            counter++;
            respString
                    .append(entry.getKey())
                    .append("=")
                    .append(entry.getValue());
            if (counter != limit) respString.append("&");
        }
        return httpService.constructRequest(
                respString.toString(),
                HttpService.GET,
                HeadersModel.EMPTY_IMPL,
                BodyBuilder.NO_BODY
        );
    }

    private HashMap<String, String> initParams() {
        var params = new HashMap<String, String>();
        params.put("api_key", getApiToken());
        return params;
    }
}
