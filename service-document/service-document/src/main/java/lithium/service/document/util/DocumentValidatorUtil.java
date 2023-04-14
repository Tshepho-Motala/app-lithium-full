package lithium.service.document.util;

import lithium.exceptions.Status405UnsupportedDocumentTypeException;
import lithium.exceptions.Status406OverLimitFileSizeException;
import lithium.service.document.data.objects.RequiredFileType;
import lithium.tokens.LithiumTokenUtil;
import lombok.extern.slf4j.Slf4j;
import net.sf.jmimemagic.Magic;
import net.sf.jmimemagic.MagicMatch;
import net.sf.jmimemagic.MagicMatchNotFoundException;
import net.sf.jmimemagic.MagicParseException;
import net.sf.jmimemagic.MagicException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@Slf4j
@Service
public class DocumentValidatorUtil {

    @Value("${spring.servlet.multipart.max-file-size}")
    private String maxFileSze;

    private final List<String> supportedFileTypes = Arrays.asList("image/jpg", "image/jpeg", "image/png", "application/pdf", "text/plain");

    public void checkIfDocumentIsValid(MultipartFile multipartFile, String documentType, LithiumTokenUtil tokenUtil) throws Status405UnsupportedDocumentTypeException,
            IOException, Status406OverLimitFileSizeException {
        if(multipartFile == null){
            log.error("Can't upload a null file for user : {}", tokenUtil.guid());
            throw new Status405UnsupportedDocumentTypeException("Document to be uploaded was not selected");
        }
        validateFile(multipartFile.getBytes(), multipartFile.getOriginalFilename(), documentType, tokenUtil.guid());
    }

    public void checkIfDocumentIsValid(byte[] fileData, String fileName, LithiumTokenUtil tokenUtil) throws Status405UnsupportedDocumentTypeException,
            Status406OverLimitFileSizeException {
        if(fileData == null){
            log.error("Can't upload a null file for user : {}", tokenUtil.guid());
            throw new Status405UnsupportedDocumentTypeException("Document to be uploaded was not selected");
        }
        validateFile(fileData, fileName, tokenUtil.guid());
    }

    private void validateFile(byte[] fileData, String fileName, String userGuid) throws Status405UnsupportedDocumentTypeException,
            Status406OverLimitFileSizeException {
        validateFileTypeAndExtension(fileData,fileName, userGuid);
        checkFileSizeLimit(fileData, fileName, userGuid);
    }

    private void validateFile(byte[] fileData, String fileName, String fileType, String userGuid) throws Status405UnsupportedDocumentTypeException,
            Status406OverLimitFileSizeException {
        validateFileTypeAndExtension(fileData,fileName, userGuid);
        checkFileSizeLimit(fileData, fileName, userGuid);
    }

    public String resolveFileType(String fileName, byte[] mpFile, String userGuid) throws Status405UnsupportedDocumentTypeException,
            MagicMatchNotFoundException, MagicException, MagicParseException {
        validateFileTypeAndExtension(mpFile, fileName, userGuid);
        MagicMatch match = Magic.getMagicMatch(mpFile, false);
        return match.getMimeType();
    }

    private void checkFileSizeLimit(byte[] fileData, String fileName, String userGuid) throws Status406OverLimitFileSizeException{
        long packetSize = Long.parseLong(maxFileSze.substring(0, maxFileSze.length() - 2)) * 1024 * 1024;
        if (packetSize < fileData.length) {
            log.error("Can't upload file (" + fileName + ") for user " + userGuid + " due over limit file size (limit:" + maxFileSze + ")");
            throw new Status406OverLimitFileSizeException("Over limit file size (limit:" + maxFileSze + ")");
        }
    }

    private String returnAllRequiredFileTypes(){
        return Arrays.toString(RequiredFileType.values()).replaceAll("[\\[\\]]","");
    }

    private void validateFileTypeAndExtension(byte[] fileData, String fileName, String userGuid) throws Status405UnsupportedDocumentTypeException {
        try {
            MagicMatch match = Magic.getMagicMatch(fileData, false);
            if (!returnAllRequiredFileTypes().contains(match.getExtension().toUpperCase())) {
                log.error("Can't upload file (" + fileName + ") for user " + userGuid + ". Unsupported document type (supported types are " + returnAllRequiredFileTypes() + ")");
                throw new Status405UnsupportedDocumentTypeException("Unsupported document type (supported types are " + returnAllRequiredFileTypes() + ")");
            }

            if (!supportedFileTypes.contains(match.getMimeType())) {
                log.error("Can't upload file (" + fileName + ") for user " + userGuid + " due to a mismatch between provided " +
                        "document type calculated from extension " + match.getExtension().toLowerCase() + " and actual document type " + match.getMimeType());
                throw new Status405UnsupportedDocumentTypeException("Can't upload file (" + fileName + ") due to a mismatch between provided " +
                        "document type calculated from extension " + match.getExtension().toLowerCase() + " and actual document type " + match.getMimeType());
            }
        } catch (MagicParseException | MagicMatchNotFoundException | MagicException e) {
            log.error("Can't upload file (" + fileName + ") " + e.getMessage());
            throw new Status405UnsupportedDocumentTypeException("Can't upload file (" + fileName + ") " + e.getMessage());
        }
    }
}
