package org.jekajops.core.payments.controllers;

import com.google.gson.JsonObject;
import org.jekajops.core.context.Context;
import org.jekajops.core.database.Database;
import org.jekajops.core.entities.User;
import org.jekajops.core.payments.Payment;
import org.jekajops.vk.VKManager;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.sql.SQLException;
import java.util.*;

import static org.jekajops.core.payments.Utils.getSignatureString;
import static org.jekajops.core.payments.Utils.projectSecretKey;

@RestController
@RequestMapping("/api/v1/")
public class PaymentController {
    private Payment payment = new Payment();

    @GetMapping("/")
    public ResponseEntity<String> onRequest(@RequestParam Map<String, String> queryParameters) {
        String signature = getSignature(queryParameters);
        payment = new Payment(queryParameters);
        System.out.println("FROM UNITPAY REQUEST: " + queryParameters);
        System.out.println("Payment: " + payment);

        return switch (payment.getMethod()) {
            case Payment.PAY -> pay(payment, signature);
            case Payment.CHECK -> check(payment, signature);
            case Payment.ERROR -> error(payment, signature);
            case Payment.PREAUTH -> preauth(payment, signature);
            default -> getErrorJson("Wrong method name");
        };
    }

    @GetMapping("/pay")
    public ResponseEntity<String> pay(@RequestParam Map<String, String> queryParameters) {
        payment = new Payment(queryParameters);
        System.out.println("PAY FROM UNITPAY REQUEST: " + queryParameters);
        return pay(payment, getSignature(queryParameters));
    }

    @GetMapping("/check")
    public ResponseEntity<String> check(@RequestParam Map<String, String> queryParameters) {
        payment = new Payment(queryParameters);
        System.out.println("CHECK FROM UNITPAY REQUEST: " + queryParameters);
        return check(payment, getSignature(queryParameters));
    }

    @GetMapping("/error")
    public ResponseEntity<String> error(@RequestParam Map<String, String> queryParameters) {
        payment = new Payment(queryParameters);
        System.out.println("ERROR FROM UNITPAY REQUEST: " + queryParameters);
        return error(payment, getSignature(queryParameters));
    }

    @GetMapping("/preauth")
    public ResponseEntity<String> preauth(@RequestParam Map<String, String> queryParameters) {
        payment = new Payment(queryParameters);
        System.out.println("PREAUTH FROM UNITPAY REQUEST: " + queryParameters);
        return preauth(payment, getSignature(queryParameters));
    }

    public ResponseEntity<String> pay(Payment payment, String signature) {
            var checkErrors = checkErrors(payment, signature);
            if (checkErrors.getStatusCode().isError()){
                return checkErrors;
            }
            try {
                String test = payment.getTest();
                if (test != null && !test.equals("1")) {
                    double sum = Double.parseDouble(payment.getOrderSum());
                    int userId = Integer.parseInt(payment.getAccount());
                    User user = new Database().getUserByUserId(userId);
                    user.updatePayment(sum);
                    new VKManager().sendMessage("Баланс пополнен на "
                            + sum + " руб.", userId);
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

    public ResponseEntity<String> check(Payment payment, String signature) {
        return checkErrors(payment, signature);
    }

    private ResponseEntity<String> checkErrors(Payment payment, String signature){
        try {
            double orderSum = Double.parseDouble(payment.getOrderSum());
            double payerSum = Double.parseDouble(payment.getPayerSum());

            int prankCost = Context.SETTINGS.PRANK_COST.getDATA();
            if (orderSum < prankCost || payerSum < prankCost || orderSum == 0 || payerSum == 0) {
                return getErrorJson("You enter a wrong cost! It is lower then prank cost (" + orderSum + " < " + prankCost + ").");
            }
        } catch (NumberFormatException | NullPointerException e) {
            e.printStackTrace();
            return getErrorJson("Missed 'sum' parameters.");
        }
        String orderCurrency = payment.getOrderCurrency();
        String payerCurrency = payment.getPayerCurrency();
        if (orderCurrency == null || !orderCurrency.equals(payerCurrency)) {
            return getErrorJson("wrong currency!");
        }
        if (signature == null || !signature.equals(payment.getSignature())) {
            return getErrorJson("wrong signature");
        }
        return getSuccessJson();
    }

    public ResponseEntity<String> error(Payment payment, String signature) {
        return getSuccessJson();
    }

    public ResponseEntity<String> preauth(Payment payment, String signature) {
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
        map.remove("signature");
        map.remove("params[signature]");
        map.remove("sign");
        map.remove("params[sign]");
        return getSignatureString(projectSecretKey, map);
    }

}
