package lithium.service.cashier.processor.trustly.api.security;

import com.fasterxml.jackson.annotation.JsonProperty;
import lithium.service.cashier.processor.trustly.api.data.notification.Notification;
import lithium.service.cashier.processor.trustly.api.data.request.AttributeData;
import lithium.service.cashier.processor.trustly.api.data.request.Request;
import lithium.service.cashier.processor.trustly.api.data.request.RequestData;
import lithium.service.cashier.processor.trustly.api.data.response.ErrorBody;
import lithium.service.cashier.processor.trustly.api.data.response.Result;
import lithium.service.cashier.processor.trustly.api.data.response.TrustlyResponse;
import lithium.service.cashier.processor.trustly.api.exceptions.TrustlyAPIException;
import lithium.service.cashier.processor.trustly.api.exceptions.TrustlySignatureException;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.openssl.PEMDecryptorProvider;
import org.bouncycastle.openssl.PEMEncryptedKeyPair;
import org.bouncycastle.openssl.PEMKeyPair;
import org.bouncycastle.openssl.PEMParser;
import org.bouncycastle.openssl.jcajce.JcaPEMKeyConverter;
import org.bouncycastle.openssl.jcajce.JcePEMDecryptorProviderBuilder;
import org.bouncycastle.util.io.pem.PemObject;

import java.io.IOException;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.security.InvalidKeyException;
import java.security.KeyException;
import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.SignatureException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class SignatureHandler {
    private static SignatureHandler instance;

    private static final Base64.Encoder base64Encoder = Base64.getEncoder();
    private static final Base64.Decoder base64Decoder = Base64.getDecoder();

    public static void signRequest(final Request request, PrivateKey privateKey) throws Exception{
        final RequestData requestData = request.getParams().getData();
        final String requestMethod = request.getMethod().toString();
        final String uuid = request.getUUID();
        final String plainText = String.format("%s%s%s", requestMethod, uuid, serializeData(requestData));

        final String signedData = createSignature(plainText, privateKey);

        request.getParams().setSignature(signedData);
    }

    public static void signNotificationResponse(final TrustlyResponse response, PrivateKey privateKey) throws Exception {
        final String requestMethod = response.getResult().getMethod().toString();
        final String uuid = response.getUUID();
        final Object data = response.getResult().getData();
        final String plainText = String.format("%s%s%s", requestMethod, uuid, serializeObject(data));

        final String signedData = createSignature(plainText, privateKey);

        response.getResult().setSignature(signedData);
    }

    private static String createSignature(final String plainText, PrivateKey privateKey) {
        try {
            final Signature signatureInstance = Signature.getInstance("SHA1withRSA");
            signatureInstance.initSign(privateKey);
            signatureInstance.update(plainText.getBytes("UTF-8"));

            final byte[] signature = signatureInstance.sign();
            return base64Encoder.encodeToString(signature);
        }
        catch (UnsupportedEncodingException | NoSuchAlgorithmException e) {
            throw new TrustlySignatureException(e);
        }
        catch (final InvalidKeyException e) {
            throw new TrustlySignatureException("Invalid private key", e);
        }
        catch (final SignatureException e) {
            throw new TrustlySignatureException("Failed to create signature", e);
        }
    }

    public static PublicKey getPublicKey(final byte[] rsaKey) throws KeyException {
        try {
            final PEMParser pemParser = new PEMParser(new StringReader(new String(rsaKey)));
            final PemObject object = pemParser.readPemObject();

            final JcaPEMKeyConverter converter = new JcaPEMKeyConverter().setProvider(new BouncyCastleProvider());

            final byte[] encoded = object.getContent();
            final SubjectPublicKeyInfo subjectPublicKeyInfo = new SubjectPublicKeyInfo(
                    ASN1Sequence.getInstance(encoded));

            return converter.getPublicKey(subjectPublicKeyInfo);
        }
        catch (final IOException e) {
            throw new KeyException("Failed to load Trustly public key", e);
        }
    }

    public static PrivateKey getPrivateKey(final byte[] rsaKey, final String password) throws KeyException {
        try {
            final PEMParser pemParser = new PEMParser(new StringReader(new String(rsaKey)));
            final Object object = pemParser.readObject();

            final PEMDecryptorProvider decProv = new JcePEMDecryptorProviderBuilder().build(password.toCharArray());
            final JcaPEMKeyConverter converter = new JcaPEMKeyConverter();

            final KeyPair kp;
            if (object instanceof PEMEncryptedKeyPair) { // Password required
                kp = converter.getKeyPair(((PEMEncryptedKeyPair) object).decryptKeyPair(decProv));
            } else {
                kp = converter.getKeyPair((PEMKeyPair) object);
            }

            return kp.getPrivate();
        }
        catch (final IOException e) {
            throw new KeyException("Failed to load private key", e);
        }
    }


    public static boolean verifyResponseSignature(final TrustlyResponse response, PublicKey publicKey)  {
        final String method;
        String uuid;
        final String serializedData;
        final String signatureBase64;

        if (response.successfulResult()) {
            final Result result = response.getResult();
            method = result.getMethod() == null ? "" : result.getMethod().toString();
            uuid = result.getUuid();
            serializedData = serializeObject(result.getData());
            signatureBase64 = result.getSignature();
        }
        else {
            final ErrorBody error = response.getError().getError();
            method = error.getMethod() == null ? "" : error.getMethod().toString();
            uuid = error.getUuid();
            serializedData = serializeData(error.getData());
            signatureBase64 = error.getSignature();
        }

        if (uuid == null) {
            uuid = "";
        }

        return performSignatureVerification(publicKey, method, uuid, serializedData, signatureBase64);
    }


    private static String serializeData(final Object data) {
        return serializeData(data, true);
    }

    private static String serializeData(final Object data, final boolean serializeNullMap) {
        try {
            //Sort all fields found in the data object class
            final List<Field> fields = getAllFields(new LinkedList<>(), data.getClass());
            fields.sort(Comparator.comparing(Field::getName));

            //Get values using reflection
            final StringBuilder builder = new StringBuilder();
            for (final Field field : fields) {

                if (field.get(data) == null && data instanceof AttributeData) {
                    continue;
                }

                final String jsonFieldName;
                if (field.isAnnotationPresent(JsonProperty.class)) {
                    jsonFieldName = field.getAnnotation(JsonProperty.class).value();
                }
                else {
                    jsonFieldName = field.getName();
                }

                if (field.getType().equals(Map.class)) {
                    if (serializeNullMap) {
                        builder.append(jsonFieldName);
                        if (field.get(data) != null) {
                            builder.append(serializeObject(field.get(data)));
                        }
                        continue;
                    }
                    else {
                        if (field.get(data) != null) {
                            builder.append(jsonFieldName);
                            builder.append(serializeObject(field.get(data)));
                        }
                        continue;
                    }
                }

                builder.append(jsonFieldName);

                if (field.get(data) != null) {
                    builder.append(field.get(data));
                }
            }
            return builder.toString();
        }
        catch (final IllegalAccessException e) {
            throw new TrustlyAPIException("Failed to serialize data", e);
        }
    }

    private static String serializeObject(final Object object) {
        final StringBuilder builder = new StringBuilder();

        if (object instanceof Map) {
            populateStringBuilder(builder, (Map) object);
        }
        else if (object instanceof ArrayList) {
            for (final Object mapEntry : (ArrayList) object) {
                populateStringBuilder(builder, (Map) mapEntry);
            }
        }
        else {
            throw new RuntimeException("Unhandled class of object: " + object.getClass());
        }

        return builder.toString();
    }

    private static void populateStringBuilder(final StringBuilder builder, final Map mapEntry) {
        final List<String> strings = new ArrayList<String>(mapEntry.keySet());
        Collections.sort(strings);
        for (final String key : strings) {
            final Object data = mapEntry.get(key);

            if (data != null) {
                builder.append(key);
                if (data instanceof AttributeData) {
                    builder.append(serializeData(data));
                } else {
                    builder.append(data);
                }
            }
        }
    }

    private static List<Field> getAllFields(List<Field> fields, final Class<?> type) {
        for (final Field field: type.getDeclaredFields()) {
            field.setAccessible(true);
            fields.add(field);
        }

        if (type.getSuperclass() != null) {
            fields = getAllFields(fields, type.getSuperclass());
        }

        return fields;
    }

    public static boolean verifyNotificationSignature(final Notification notification, PublicKey publicKey) {
        final String method = notification.getMethod().toString();
        final String uuid = notification.getUUID();
        final String serializedData = serializeData(notification.getParams().getData(), false);
        final String signatureBase64 = notification.getParams().getSignature();

        return performSignatureVerification(publicKey, method, uuid, serializedData, signatureBase64);
    }

    private static boolean performSignatureVerification(final PublicKey publicKey, final String method, final String uuid, final String serializedData, final String responseSignature) {
        try {
            final byte[] signature = base64Decoder.decode(responseSignature);
            final Signature signatureInstance = Signature.getInstance("SHA1withRSA");
            signatureInstance.initVerify(publicKey);
            final String expectedPlainText = String.format("%s%s%s", method, uuid, serializedData);
            signatureInstance.update(expectedPlainText.getBytes("UTF-8"));
            return signatureInstance.verify(signature);
        }
        catch (final IOException e) {
            throw new TrustlySignatureException("Failed to decode signature", e);
        }
        catch (final NoSuchAlgorithmException e) {
            throw new TrustlySignatureException(e);
        }
        catch (final InvalidKeyException e) {
            throw new TrustlySignatureException("Invalid public key", e);
        }
        catch (final SignatureException e) {
            throw new TrustlySignatureException("Failed to verify signature", e);
        }
    }
}
