package lithium.service.cashier.processor.bluem;

import lithium.service.cashier.processor.bluem.exceptions.BluemInvalidSignatureException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

import javax.xml.crypto.dsig.XMLSignature;
import javax.xml.crypto.dsig.XMLSignatureFactory;
import javax.xml.crypto.dsig.dom.DOMValidateContext;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.security.PublicKey;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.Base64;

@Slf4j
@Service
public class BluemSignatureHandler {
    private static final Base64.Encoder base64Encoder = Base64.getEncoder();
    private static final Base64.Decoder base64Decoder = Base64.getDecoder();

    public static void checkSignature(String data, String publicKey) throws Exception {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        dbf.setNamespaceAware(true);

        Document doc = dbf.newDocumentBuilder().parse(new ByteArrayInputStream(data.getBytes(StandardCharsets.UTF_8)));

        NodeList nl = doc.getElementsByTagNameNS(XMLSignature.XMLNS, "Signature");
        if (nl.getLength() == 0) {
            throw new BluemInvalidSignatureException("Cannot find Signature element. Request: " + data);
        }

        XMLSignatureFactory fac = XMLSignatureFactory.getInstance("DOM");

        DOMValidateContext valContext = new DOMValidateContext(getPublicKey(publicKey), nl.item(0));

        XMLSignature signature = fac.unmarshalXMLSignature(valContext);
        if (!signature.validate(valContext)) {
            throw new BluemInvalidSignatureException("Signature check is failed for webhook. Request: " + data);
        }
    }

    static private PublicKey getPublicKey(String publicKey) throws Exception {
        byte[] merchantCertificateBytes = base64Decoder.decode(publicKey);
        X509Certificate certificate = (X509Certificate) CertificateFactory.getInstance("X.509")
            .generateCertificate(new ByteArrayInputStream(merchantCertificateBytes));
        return certificate.getPublicKey();
    }
}
