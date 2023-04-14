package lithium.service.cashier.processor.trustly.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import lithium.service.cashier.processor.trustly.api.data.request.Request;
import lithium.service.cashier.processor.trustly.api.data.response.TrustlyResponse;
import lithium.service.cashier.processor.trustly.api.exceptions.TrustlyConnectionException;
import lithium.service.cashier.processor.trustly.api.exceptions.TrustlyDataException;
import lithium.service.cashier.processor.trustly.api.exceptions.TrustlySignatureException;
import lithium.service.cashier.processor.trustly.api.security.SignatureHandler;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.security.PublicKey;

public class SignedAPI {

    public static TrustlyResponse sendRequest(final Request request, String apiUrl, PublicKey publicKey) throws Exception{
        final ObjectMapper mapper = new ObjectMapper();
        mapper.configure(SerializationFeature.WRITE_NULL_MAP_VALUES, false);

        final String jsonResponse = newHttpPost(mapper.writeValueAsString(request), apiUrl);

        final TrustlyResponse response = mapper.readValue(jsonResponse, TrustlyResponse.class);
        verifyResponse(response, request.getUUID(), publicKey);
        return response;
    }

    private static String newHttpPost(final String request, String apiUrl) {
        try {
            final CloseableHttpClient httpClient = HttpClients.createDefault();
            final HttpPost httpPost = new HttpPost(apiUrl);
            final StringEntity jsonRequest = new StringEntity(request, "UTF-8");
            httpPost.addHeader("content-type", "application/json");
            httpPost.setEntity(jsonRequest);

            final HttpResponse result = httpClient.execute(httpPost);
            return EntityUtils.toString(result.getEntity(), "UTF-8");
        }
        catch (final IOException e) {
            throw new TrustlyConnectionException("Failed to send request.", e);
        }
    }

    private static void verifyResponse(final TrustlyResponse response, final String requestUUID, PublicKey publicKey) {
        if (!SignatureHandler.verifyResponseSignature(response, publicKey)) {
            throw new TrustlySignatureException("Incoming data signature is not valid");
        }
        if(response.getUUID() != null && !response.getUUID().equals(requestUUID) ) {
            throw new TrustlyDataException("Incoming data signature is not valid");
        }
    }
}
