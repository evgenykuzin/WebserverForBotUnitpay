package org.jekajops.core.utils.payments;

import org.jekajops.core.http.headers.HeadersModel;
import org.jekajops.core.http.headers.HeadersModelImpl;
import org.jekajops.core.http.services.NetHttpService;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.TreeMap;

import static org.jekajops.core.utils.payments.Utils.*;

public class PaymentManager {
    private static final NetHttpService service;
    private static final String URL = "https://unitpay.money/pay/";
    private static final String login = "twinchik7@mail.ru";
    private static final HeadersModel headers = new HeadersModelImpl();
    static {
       service = new NetHttpService();
    }

    public static String initPay(double sum, String user){
        var params = new LinkedHashMap<String, String>();
        params.put("account", user);
        params.put("desc", "prank");
        params.put("sum", String.valueOf(sum));
        String signature = getSignatureString(projectSecretKey, params);
        params.put("signature", signature);

        var request = service.constructGetRequest(
                URL+projectPublicKey,
                params
        );
        return request.getUrl();
    }
}
