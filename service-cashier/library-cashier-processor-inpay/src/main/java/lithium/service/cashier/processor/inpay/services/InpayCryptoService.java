package lithium.service.cashier.processor.inpay.services;

import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.cms.CMSObjectIdentifiers;
import org.bouncycastle.asn1.cms.ContentInfo;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cert.jcajce.JcaCertStore;
import org.bouncycastle.cert.jcajce.JcaX509CertificateConverter;
import org.bouncycastle.cms.CMSAlgorithm;
import org.bouncycastle.cms.CMSEnvelopedData;
import org.bouncycastle.cms.CMSEnvelopedDataGenerator;
import org.bouncycastle.cms.CMSEnvelopedDataParser;
import org.bouncycastle.cms.CMSException;
import org.bouncycastle.cms.CMSProcessable;
import org.bouncycastle.cms.CMSProcessableByteArray;
import org.bouncycastle.cms.CMSSignedData;
import org.bouncycastle.cms.CMSSignedDataGenerator;
import org.bouncycastle.cms.CMSTypedData;
import org.bouncycastle.cms.Recipient;
import org.bouncycastle.cms.RecipientInformation;
import org.bouncycastle.cms.SignerInformation;
import org.bouncycastle.cms.SignerInformationStore;
import org.bouncycastle.cms.jcajce.JcaSignerInfoGeneratorBuilder;
import org.bouncycastle.cms.jcajce.JceCMSContentEncryptorBuilder;
import org.bouncycastle.cms.jcajce.JceKeyTransEnvelopedRecipient;
import org.bouncycastle.cms.jcajce.JceKeyTransRecipientInfoGenerator;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.openssl.jcajce.JcaPEMWriter;
import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.operator.OutputEncryptor;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;
import org.bouncycastle.operator.jcajce.JcaDigestCalculatorProviderBuilder;
import org.bouncycastle.util.Store;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Security;
import java.security.SignatureException;
import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

@Service
public class InpayCryptoService {

    public String signAndEncryptRequest(String privateKey, String firstCertificate, String secondCertificate, String payload) throws Exception {

        PrivateKey priKey = getPrivateKey(privateKey);
        X509Certificate fCertificate = getCertificate(firstCertificate);
        X509Certificate sCertificate = getCertificate(secondCertificate);

        Security.addProvider(new BouncyCastleProvider());

        CMSSignedData signedData = signData(payload, priKey, fCertificate);
        CMSEnvelopedData envelopedData = encrypt(sCertificate, signedData);

        return toPKCS7(envelopedData.getEncoded());
    }

    public String decryptAndVerifyResponse(String privateKey, String inpayCaChain, String body) throws Exception {
        PrivateKey priKey = getPrivateKey(privateKey);
        PublicKey publKey = getPublicKey(inpayCaChain);

        Security.addProvider(new BouncyCastleProvider());
        CMSSignedData signature = decrypt(priKey, prepareBody(body));
        return verifyAndExtractData(publKey, signature);
    }

    private CMSEnvelopedData encrypt(X509Certificate certificate, CMSSignedData signedData) throws CertificateEncodingException, CMSException, IOException {
        CMSEnvelopedDataGenerator generator = new CMSEnvelopedDataGenerator();
        generator.addRecipientInfoGenerator(new JceKeyTransRecipientInfoGenerator(certificate));
        OutputEncryptor encryptor = new JceCMSContentEncryptorBuilder(CMSAlgorithm.AES256_CBC).build();
        CMSEnvelopedData envelopedData = generator.generate(new CMSProcessableByteArray(signedData.getEncoded()), encryptor);
        return envelopedData;
    }

    private CMSSignedData signData(String payload, PrivateKey privateKey, X509Certificate certificate) throws CertificateEncodingException, OperatorCreationException, CMSException {
        List<X509Certificate> certList = new ArrayList<X509Certificate>();
        certList.add(certificate);
        Store<?> certs = new JcaCertStore(certList);

        CMSSignedDataGenerator signGen = new CMSSignedDataGenerator();
        ContentSigner signer = new JcaContentSignerBuilder("SHA256withRSA")
                .setProvider("BC")
                .build(privateKey);
        signGen.addSignerInfoGenerator(
                new JcaSignerInfoGeneratorBuilder(new JcaDigestCalculatorProviderBuilder().setProvider("BC").build())
                        .build(signer, certificate)
        );
        signGen.addCertificates(certs);

        CMSTypedData content = new CMSProcessableByteArray(new ASN1ObjectIdentifier(CMSObjectIdentifiers.data.getId()), payload.getBytes());
        CMSSignedData signedData = signGen.generate(content, true);
        return signedData;
    }

    private String verifyAndExtractData(PublicKey publicKey, CMSSignedData signature) throws CertificateException, NoSuchAlgorithmException, InvalidKeyException, NoSuchProviderException, SignatureException {
        SignerInformationStore signers = signature.getSignerInfos();
        byte[] data = null;
        Iterator it = signers.getSigners().iterator();
        while (it.hasNext()) {
            SignerInformation signer = (SignerInformation) it.next();
            Collection certCollection = signature.getCertificates().getMatches(signer.getSID());
            Iterator certIt = certCollection.iterator();
            X509CertificateHolder cert = (X509CertificateHolder) certIt.next();

            X509Certificate myCA = new JcaX509CertificateConverter().getCertificate(cert);
            myCA.verify(publicKey);

            CMSProcessable sc = signature.getSignedContent();
            data = (byte[]) sc.getContent();
        }
        return new String(data);
    }

    private CMSSignedData decrypt(PrivateKey privateKey, byte[] encryptedData) throws IOException, CMSException {
        CMSEnvelopedDataParser parser = new CMSEnvelopedDataParser(encryptedData);
        RecipientInformation recInfo = getSingleRecipient(parser);
        Recipient recipient = new JceKeyTransEnvelopedRecipient(privateKey);
        return new CMSSignedData(recInfo.getContentStream(recipient).getContentStream());
    }

    private RecipientInformation getSingleRecipient(CMSEnvelopedDataParser parser) {
        Collection recInfos = parser.getRecipientInfos().getRecipients();
        Iterator recipientIterator = recInfos.iterator();
        if (!recipientIterator.hasNext()) {
            throw new RuntimeException("Could not find recipient");
        }
        return (RecipientInformation) recipientIterator.next();
    }

    private byte[] prepareBody(String body) {
        body = body.replace("-----BEGIN PKCS7-----", "")
                .replace("-----END PKCS7-----", "")
                .replaceAll("\\s+", "");
        return Base64.getDecoder().decode(body);
    }

    private String toPKCS7(byte[] envelopedData) throws IOException {
        ContentInfo contentInfo = ContentInfo.getInstance(ASN1Sequence.fromByteArray(envelopedData));
        StringWriter stringWriter = new StringWriter();
        JcaPEMWriter writer = new JcaPEMWriter(stringWriter);
        writer.writeObject(contentInfo);
        writer.close();
        return stringWriter.toString();
    }

    private PublicKey getPublicKey(String caChainCertificate) throws Exception {
        String caChainCertificateResult = caChainCertificate
                .replaceAll("-----BEGIN CERTIFICATE-----", "")
                .replaceAll("-----END CERTIFICATE-----", "")
                .replaceAll("\\s+", "");
        byte[] caChainCertificateBytes = Base64.getDecoder().decode(caChainCertificateResult);
        return CertificateFactory.getInstance("X.509")
                .generateCertificate(new ByteArrayInputStream(caChainCertificateBytes)).getPublicKey();
    }

    private X509Certificate getCertificate(String certificate) throws Exception {
        String certificateResult = certificate
                .replaceAll("-----BEGIN CERTIFICATE-----", "")
                .replaceAll("-----END CERTIFICATE-----", "")
                .replaceAll("\\s+", "");
        byte[] certificateBytes = Base64.getDecoder().decode(certificateResult);
        return (X509Certificate) CertificateFactory.getInstance("X.509")
                .generateCertificate(new ByteArrayInputStream(certificateBytes));
    }

    private PrivateKey getPrivateKey(String privateKey) throws Exception {
        String pkcs8Pem = privateKey
                .replace("-----BEGIN PRIVATE KEY-----", "")
                .replace("-----END PRIVATE KEY-----", "")
                .replaceAll("\\s+", "");
        byte[] pkcs8Bytes = Base64.getDecoder().decode(pkcs8Pem);
        return KeyFactory.getInstance("RSA").generatePrivate(new PKCS8EncodedKeySpec(pkcs8Bytes));
    }
}
