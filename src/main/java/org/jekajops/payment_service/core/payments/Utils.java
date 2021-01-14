package org.jekajops.payment_service.core.payments;

import org.jekajops.payment_service.core.utils.files.PropertiesManager;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Collection;
import java.util.Properties;

public class Utils {
    private static final Properties properties = PropertiesManager.getProperties("anypay");
    public static final String PROJECT_SECRET_KEY = properties.getProperty("project.secret_key");
    public static final String MERCHANT_ID = properties.getProperty("merchant_id");

    public static String getSignatureString(String secret_key, Collection<String> parameters, String delimiter) {
        final StringBuilder sb = new StringBuilder();
        parameters.forEach(v-> {
            if (v != null) sb.append(v).append(delimiter);
        });
        sb.append(secret_key);
        MessageDigest digest;
        try {
            digest = MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }
        byte[] hash = digest.digest(sb.toString().getBytes(StandardCharsets.UTF_8));
        StringBuilder sb2 = new StringBuilder();
        for (byte b : hash) {
            sb2.append(String.format("%02x", b));
        }
        return sb2.toString();
    }

}