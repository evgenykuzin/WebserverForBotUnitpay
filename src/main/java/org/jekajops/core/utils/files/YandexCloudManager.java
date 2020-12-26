package org.jekajops.core.utils.files;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;

import java.io.File;

public class YandexCloudManager {
    private static final String keyId = "Exp1bFPxsBf3kEUU5LGP";
    private static final String secretKey = "spo_v5I9gHI2LQ9xwHH_YMkIrx7p_me8K3vrEnBx";
    private static final AWSCredentials credentials = new BasicAWSCredentials(keyId, secretKey);
    private static final AmazonS3 s3 = initAmazonS3();
    private static final String BUC = "buc";
    private static final String BASE_FILE_URL = "https://storage.yandexcloud.net/" + BUC + "/";

    public static String uploadFile(File file) {
        try {
            String fileName = file.getName();
            fileName = fileName.replaceAll("\\s", "");
            fileName = fileName.replaceAll("'", "");
            fileName = fileName.replaceAll("[\\\\/,`?&%$#@!]", "");

            s3.putObject(BUC, fileName, file);
            System.out.println(getFileUrl(fileName));
            return getFileUrl(fileName);
        } catch (AmazonServiceException ase) {
            ase.printStackTrace();
        } finally {
            file.delete();
        }
        return null;
    }

    private static AmazonS3 initAmazonS3() {
        return AmazonS3ClientBuilder.standard()
                .withCredentials(new AWSStaticCredentialsProvider(credentials))
                .withEndpointConfiguration(
                        new AmazonS3ClientBuilder.EndpointConfiguration(
                                "storage.yandexcloud.net", "ru-central1"
                        )
                )
                .build();
    }

    private static String getFileUrl(String name) {
        return BASE_FILE_URL + name;
    }

}
