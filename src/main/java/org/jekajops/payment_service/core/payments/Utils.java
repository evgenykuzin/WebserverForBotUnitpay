package org.jekajops.payment_service.core.payments;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Collection;

public class Utils {

    public static String getSignatureString(Collection<String> parameters, String delimiter, String hashMethodName) {
        final StringBuilder sb = new StringBuilder();
        parameters.forEach(v-> {
            if (v != null) {
                sb.append(v).append(delimiter);
            }
        });
        sb.replace(sb.length()-1, sb.length(), "");
        System.out.println(sb.toString());
        MessageDigest digest;
        try {
            digest = MessageDigest.getInstance(hashMethodName);
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