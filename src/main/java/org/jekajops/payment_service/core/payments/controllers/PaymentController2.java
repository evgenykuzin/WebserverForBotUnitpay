package org.jekajops.payment_service.core.payments.controllers;

import com.google.gson.JsonObject;
import org.jekajops.payment_service.core.context.Context;
import org.jekajops.payment_service.core.database.Database;
import org.jekajops.payment_service.core.entities.User;
import org.jekajops.payment_service.core.utils.files.PropertiesManager;
import org.jekajops.payment_service.vk.VKManager;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.sql.SQLException;
import java.util.*;

import static org.jekajops.payment_service.core.payments.Utils.getSignatureString;

@RestController
@RequestMapping("/api/v2/")
public class PaymentController2 {
    private static final Properties properties = PropertiesManager.getProperties("anypay");
    public static final String PROJECT_SECRET_KEY = properties.getProperty("project.secret_key");
    public static final String MERCHANT_ID = properties.getProperty("merchant_id");
    static final String PAYER_CURRENCY = "RUB";

    @GetMapping("/")
    public ResponseEntity<String> onRequest(@RequestParam Map<String, String> queryParameters) {
        String inputSignature = queryParameters.get("sign");
        String signature = getSignature(queryParameters);
        System.out.println("FROM PAY REQUEST: " + queryParameters);
        return handle(queryParameters, signature, inputSignature);
    }

    public ResponseEntity<String> handle(Map<String, String> payment, String signature, String inputSignature) {
        var checkErrors = checkErrors(payment, signature, inputSignature);
        if (checkErrors.getStatusCode().isError()) {
            return checkErrors;
        }
        try {
            String test = payment.get("test");
            if (test != null && !test.equals("1")) {
                double amount = Double.parseDouble(payment.get("amount"));
                int userId = Integer.parseInt(payment.get("user_id"));
                User user = new Database().getUserByUserId(userId);
                user.updatePayment(amount);
                new VKManager().sendMessage("Баланс пополнен на "
                        + amount + " руб.", userId);
            }
        } catch (SQLException sqle) {
            sqle.printStackTrace();
            return getErrorJson("Database error!");
        } catch (NumberFormatException nfe) {
            nfe.printStackTrace();
            return getErrorJson("Number format error");
        }
        return getSuccessJson();
    }

    private ResponseEntity<String> checkErrors(Map<String, String> payment, String signature, String inputSignature) {
        try {
            String amountParam = payment.get("amount");
            if (amountParam == null) return getErrorJson("Missed 'amount' parameter.");
            double amount = Double.parseDouble(amountParam);
            String profitParam = payment.get("profit");
            if (profitParam != null) {
                double profit = Double.parseDouble(profitParam);
                System.out.println("profit = " + profit);
            }
            int prankCost = Context.SETTINGS.PRANK_COST.getDATA();
            if (amount < prankCost) {
                return getErrorJson("You enter a wrong cost! It is lower then prank cost (" + amount + " < " + prankCost + ").");
            }
        } catch (NumberFormatException e) {
            e.printStackTrace();
            return getErrorJson("Missed 'amount' parameter.");
        }

        String currency = payment.get("currency");
        if (currency == null || !currency.equals(PAYER_CURRENCY)) {
            return getErrorJson("wrong currency!");
        }

        if (signature == null || !signature.equals(inputSignature)) {
            return getErrorJson("wrong signature");
        }
        return getSuccessJson();
    }

    private ResponseEntity<String> getErrorJson(String errorMessage) {
        return getResponse("error", errorMessage, HttpStatus.BAD_GATEWAY);
    }

    private ResponseEntity<String> getErrorJson() {
        return getResponse("error", "unknown error", HttpStatus.BAD_GATEWAY);
    }

    private ResponseEntity<String> getSuccessJson(String successMessage) {
        return getResponse("result", successMessage, HttpStatus.OK);
    }

    private ResponseEntity<String> getSuccessJson() {
        return getResponse("result", "success", HttpStatus.OK);
    }

    public ResponseEntity<String> getResponse(String resultKey, String message, HttpStatus status) {
        JsonObject jsonResponse = new JsonObject();
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("message", message);
        jsonResponse.add(resultKey, jsonObject);
        return new ResponseEntity<>(jsonResponse.toString(), status);
    }

    private static String getSignature(Map<String, String> queryParameters) {
        var params = new ArrayList<String>();
        params.add(MERCHANT_ID);
        params.addAll(getParams(queryParameters, "amount", "pay_id"));
        params.add(PROJECT_SECRET_KEY);
        return getSignatureString(params, ":", "MD5");
    }

    private static String getParam(Map<String, String> queryParameters, String name) {
        var param = queryParameters.get(name);
        if (param == null) param = "";
        return param;
    }

    private static List<String> getParams(Map<String, String> queryParameters, String... names) {
        var params = new ArrayList<String>();
        for (String name : names) {
            params.add(getParam(queryParameters, name));
        }
        return params;
    }

}