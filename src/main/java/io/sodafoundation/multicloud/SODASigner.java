package io.sodafoundation.multicloud;

import com.amazonaws.Request;
import com.amazonaws.DefaultRequest;
import com.amazonaws.auth.AWS4Signer;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.http.HttpMethodName;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

public class SODASigner {

    public static String getSignature(String uri, String host, String accessKey, String secretKey,
                               String region, String date, String payload){

        Map<String, String> headers = new HashMap<>();
        headers.put("Host", host);
        headers.put("X-Amz-Content-Sha256", payload);
        headers.put("X-Amz-Date", date);

        Request<Void> request = new DefaultRequest<Void>("s3");
        request.setHttpMethod(HttpMethodName.GET);
        request.setEndpoint(URI.create(uri));
        request.setHeaders(headers);

        AWSCredentials credentials = new BasicAWSCredentials(accessKey, secretKey);
        AWS4Signer signer = new AWS4Signer();
        signer.setRegionName(region);
        signer.setServiceName(request.getServiceName());
        signer.sign(request, credentials);

        for (Map.Entry<String, String> entry : request.getHeaders().entrySet()) {
            System.out.println(entry.getKey() + " " + entry.getValue());
        }
        return request.getHeaders().get("Authorization");

    }

}
