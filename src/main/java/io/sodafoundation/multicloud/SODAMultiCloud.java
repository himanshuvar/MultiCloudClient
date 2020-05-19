package io.sodafoundation.multicloud;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import uk.co.lucasweb.aws.v4.signer.HttpRequest;
import uk.co.lucasweb.aws.v4.signer.Signer;
import uk.co.lucasweb.aws.v4.signer.credentials.AwsCredentials;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.SimpleTimeZone;

public class SODAMultiCloud {

    private static final String CONTENT_SHA_256 = "e3b0c44298fc1c149afbf4c8996fb92427ae41e4649b934ca495991b7852b855";

    private static String accessKey = "oiayuQJDzFL0sXSF2F9K";
    private static String secretKey = "T5SoSwZkn2syoRPEEH5sBswbmEl9HmtWWhXHXEZa";
    public static final String ISO8601BasicFormat = "yyyyMMdd'T'HHmmss'Z'";

    private static final OkHttpClient httpClient = new OkHttpClient();

    public static void main(String[] args) throws IOException, URISyntaxException, NoSuchAlgorithmException {
        String hostIP = "192.168.20.162";
        String port = "8090";
        String host = hostIP + ":" + port;
        String region = "ap-south-1";
        String bucket = "test";
        String date = getDate();
        listBucket(host, "/", region, date);
        getBucket(host, "/", region, date, bucket);
        listBucketSODASignature(host, "/", region, date);
    }

    public static String getDate() {
        Date now = new Date();
        SimpleDateFormat dateTimeFormat = new SimpleDateFormat(ISO8601BasicFormat);
        dateTimeFormat.setTimeZone(new SimpleTimeZone(0, "UTC"));
        return dateTimeFormat.format(now);
    }

    public static String getSignature(String uri, String method, String host,
                               String region, String payload) throws URISyntaxException {

        HttpRequest request = new HttpRequest(method, new URI(uri));

        String date = getDate();
        String signature = Signer.builder()
                .awsCredentials(new AwsCredentials(accessKey, secretKey))
                .region(region)
                .header("Host", host)
                .header("x-amz-date", date)
                .header("x-amz-content-sha256", payload)
                .buildS3(request, payload)
                .getSignature();

        return signature;
    }

    public static void listBucketSODASignature(String host, String endPoint, String region,
                                  String date) throws URISyntaxException, IOException {
        String uri = "http://" + host + endPoint;

        System.out.println("Getting Version 4 Signature\n");
        String signature = SODASigner.getSignature(uri, host, accessKey, secretKey, region, date, CONTENT_SHA_256);

        System.out.println(signature);

        Request httpRequest = new Request.Builder()
                .url(uri)
                .header("Host", host)
                .header("X-Amz-Date", date)
                .header("X-Amz-Content-Sha256", CONTENT_SHA_256)
                .header("Authorization", signature)
                .build();

        Response response = httpClient.newCall(httpRequest).execute();

        System.out.println("Listing Buckets\n");
        System.out.println(response.code());
        // Get response body
        System.out.println(response.body().string());

    }

    public static void listBucket(String host, String endPoint, String region,
                                  String date) throws URISyntaxException, IOException {
        String uri = "http://" + host + endPoint;

        System.out.println("Getting Version 4 Signature\n");
        String signature = getSignature(uri, "GET", host, region ,CONTENT_SHA_256);

        System.out.println(signature);

        Request httpRequest = new Request.Builder()
                .url(uri)
                .header("Host", host)
                .header("x-amz-date", date)
                .header("x-amz-content-sha256", CONTENT_SHA_256)
                .header("Authorization", signature)
                .build();

        Response response = httpClient.newCall(httpRequest).execute();

        System.out.println("Listing Buckets\n");
        System.out.println(response.code());
            // Get response body
        System.out.println(response.body().string());

    }

    public static void getBucket(String host, String endPoint, String region, String date,
                                  String bucket) throws URISyntaxException, IOException {
        String uri = "http://" + host + endPoint + bucket;

        String signature = getSignature(uri, "GET", host, region ,CONTENT_SHA_256);

        System.out.println("Getting Version 4 Signature\n");
        System.out.println(signature);

        Request httpRequest = new Request.Builder()
                .url(uri)
                .header("Host", host)
                .header("x-amz-date", date)
                .header("x-amz-content-sha256", CONTENT_SHA_256)
                .header("Authorization", signature)
                .build();

        Response response = httpClient.newCall(httpRequest).execute();

        System.out.println("Getting Bucket\n");
        System.out.println(response.code());
        // Get response body
        System.out.println(response.body().string());

    }
}
