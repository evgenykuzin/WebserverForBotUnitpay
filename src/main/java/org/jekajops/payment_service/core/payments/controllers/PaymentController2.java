package org.jekajops.payment_service.core.payments.controllers;

import com.google.gson.JsonObject;
import org.jekajops.payment_service.core.context.Context;
import org.jekajops.payment_service.core.database.Database;
import org.jekajops.payment_service.core.entities.User;
import org.jekajops.payment_service.vk.VKManager;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.sql.SQLException;
import java.util.Map;
import java.util.TreeMap;

import static org.jekajops.payment_service.core.payments.Utils.getSignatureString;
import static org.jekajops.payment_service.core.payments.Utils.PROJECT_SECRET_KEY;

@RestController
@RequestMapping("/api/v2/")
public class PaymentController2 {
    static final String PAYER_CURRENCY = "RUB";

    @GetMapping("/")
    public ResponseEntity<String> onRequest(@RequestParam Map<String, String> queryParameters) {
        String signature = getSignature(queryParameters);
        System.out.println("FROM UNITPAY REQUEST: " + queryParameters);
        return handle(queryParameters, signature);
    }

    public ResponseEntity<String> handle(Map<String, String> payment, String signature) {
        var checkErrors = checkErrors(payment, signature);
        if (checkErrors.getStatusCode().isError()){
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
            return getErrorJson("Sum or Account parameters error!");
        }
        return getSuccessJson();
    }

    private ResponseEntity<String> checkErrors(Map<String, String> payment, String signature){
        try {
            double amount = Double.parseDouble(payment.get("amount"));
            double profit = Double.parseDouble(payment.get("profit"));
            System.out.println("profit = " + profit);
            int prankCost = Context.SETTINGS.PRANK_COST.getDATA();
            if (amount < prankCost) {
                return getErrorJson("You enter a wrong cost! It is lower then prank cost (" + amount + " < " + prankCost + ").");
            }
        } catch (NumberFormatException | NullPointerException e) {
            e.printStackTrace();
            return getErrorJson("Missed 'amount' parameter.");
        }

        String currency = payment.get("currency");
        if (currency == null || !currency.equals(PAYER_CURRENCY)) {
            return getErrorJson("wrong currency!");
        }

        if (signature == null || !signature.equals(payment.get("sign"))) {
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
        Map<String, String> map = new TreeMap<>(queryParameters);
        map.remove("sign");
        return getSignatureString(PROJECT_SECRET_KEY, map.values(), ":");
    }

}