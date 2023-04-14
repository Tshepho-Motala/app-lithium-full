package lithium.service.kyc.provider.onfido.service;

import com.onfido.Onfido;
import com.onfido.exceptions.OnfidoException;
import com.onfido.models.Document;
import lithium.exceptions.Status500InternalServerErrorException;
import lithium.service.kyc.provider.onfido.config.ProviderConfig;
import lithium.service.kyc.provider.onfido.repositories.UserApplicantRepository;
import lithium.service.user.client.service.UserApiInternalClientService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
@Slf4j
@AllArgsConstructor
public class OnfidoDocumentService extends OnfidoBaseService {

    public Document uploadDocument(Onfido onfido, String applicantId, MultipartFile file,
                                   String issuingCountry, String type, String side, String authorGuid) throws Status500InternalServerErrorException, IOException, OnfidoException {

        Document.Request request = Document.request()
                .applicantId(applicantId)
                .issuingCountry(issuingCountry)
                .type(type)
                .side(side);

        Document document = onfido.document.upload(file.getInputStream(), file.getOriginalFilename(), request);
        log.info("Uploaded document to Onfido ("+document.getId()+") for " + applicantId + " by " + authorGuid);
        return document;

    }
}
