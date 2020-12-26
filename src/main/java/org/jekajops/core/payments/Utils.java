package org.jekajops.core.payments;

import org.jekajops.core.utils.files.PropertiesManager;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Map;
import java.util.Properties;

public class Utils {
    private static final Properties properties = PropertiesManager.getProperties("unitpay");
    public static final String projectSecretKey = properties.getProperty("project.secret_key");
    public static final String projectPublicKey = properties.getProperty("project.public_key");
    public static final String apiSecretKey = properties.getProperty("api.secret_key");
    public static final String apiTestKey = properties.getProperty("api.test_key");

    public static String getSignatureString(String secret_key, Map<String, String> queryParameters) {
        final StringBuilder sb = new StringBuilder();
        queryParameters.forEach((k,v)-> {
            if (v != null) sb.append(v).append("{up}");
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
