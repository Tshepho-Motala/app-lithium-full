package lithium.service.settlement.services;

import java.text.SimpleDateFormat;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import lithium.service.client.objects.placeholders.Placeholder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import lithium.service.client.LithiumServiceClientFactory;
import lithium.service.client.LithiumServiceClientFactoryException;
import lithium.service.domain.client.DomainClient;
import lithium.service.domain.client.objects.Domain;
import lithium.service.entity.client.EntityClient;
import lithium.service.entity.client.objects.Entity;
import lithium.service.mail.client.objects.EmailData;
import lithium.service.mail.client.stream.MailStream;
import lithium.service.settlement.data.entities.Settlement;
import lithium.service.settlement.data.entities.SettlementPDF;
import lithium.service.settlement.data.repositories.SettlementPDFRepository;
import lithium.service.settlement.data.repositories.SettlementRepository;
import lithium.service.user.client.UserApiInternalClient;
import lithium.service.user.client.objects.User;

import static lithium.service.client.objects.placeholders.PlaceholderBuilder.REPORT_SETTLEMENT_PERIOD;
import static lithium.service.client.objects.placeholders.PlaceholderBuilder.REPORT_SETTLEMENT_TO;

@Service
public class SettlementPDFService {
	@Autowired SettlementRepository settlementRepo;
	@Autowired SettlementPDFRepository settlementPDFRepo;
	@Autowired MailStream mailStream;
	@Autowired LithiumServiceClientFactory services;
	
	private static final String EMAIL_TEMPLATE_NAME = "settlement_statement";
	private static final String EMAIL_TEMPLATE_LANG = "en";
	private static final int EMAIL_PRIORITY_HIGH = 1;
	
	public byte[] preview(lithium.service.settlement.data.entities.Settlement settlement) throws Exception {
		Entity entity = null;
		User user = null;
		
		if (settlement.getEntity() != null && settlement.getEntity().getUuid() != null) {
			EntityClient entityClient = services.target(EntityClient.class, "service-entity", true);
			entity = entityClient.findByUuid(settlement.getEntity().getUuid()).getData();
		} else if (settlement.getUser() != null && settlement.getUser().getGuid() != null) {
			UserApiInternalClient userClient = services.target(UserApiInternalClient.class, "service-user", true);
			user = userClient.getUser(settlement.getUser().getGuid()).getData();
		}
		
		DomainClient domainClient = services.target(DomainClient.class, "service-domain", true);
		Domain domain = domainClient.findByName(settlement.getDomain().getName()).getData().getParent();
		ResponseEntity<byte[]> response = domainClient.getImage(domain.getId(), "logo");
		
		lithium.service.settlement.pdf.Settlement s = new lithium.service.settlement.pdf.Settlement(domain, response.getBody(), settlement, entity, user);
		
		byte[] pdf = s.printPDF();
		
		return pdf;
	}
	
	public Settlement create(lithium.service.settlement.data.entities.Settlement settlement) throws Exception {
		Entity entity = null;
		User user = null;
		
		if (settlement.getEntity() != null && settlement.getEntity().getUuid() != null) {
			EntityClient entityClient = services.target(EntityClient.class, "service-entity", true);
			entity = entityClient.findByUuid(settlement.getEntity().getUuid()).getData();
		} else if (settlement.getUser() != null && settlement.getUser().getGuid() != null) {
			UserApiInternalClient userClient = services.target(UserApiInternalClient.class, "service-user", true);
			user = userClient.getUser(settlement.getUser().getGuid()).getData();
		}
		
		DomainClient domainClient = services.target(DomainClient.class, "service-domain", true);
		Domain domain = domainClient.findByName(settlement.getDomain().getName()).getData().getParent();
		ResponseEntity<byte[]> response = domainClient.getImage(domain.getId(), "logo");
		
		lithium.service.settlement.pdf.Settlement s = new lithium.service.settlement.pdf.Settlement(domain, response.getBody(), settlement, entity, user);
		
		byte[] pdf = s.printPDF();
		
		SettlementPDF settlementPDF = SettlementPDF.builder().pdf(pdf).sent(false).build();
		settlementPDF = settlementPDFRepo.save(settlementPDF);
		
		settlement.setPdf(settlementPDF);
		settlement = settlementRepo.save(settlement);
		
		return settlement;
	}
	
	public void sendPdf(Settlement settlement) throws LithiumServiceClientFactoryException {
		if (settlement.getPdf() != null && settlement.getPdf().getPdf().length > 0) {
			Entity entity = null;
			User user = null;
			
			if (settlement.getEntity() != null && settlement.getEntity().getUuid() != null) {
				EntityClient entityClient = services.target(EntityClient.class, "service-entity", true);
				entity = entityClient.findByUuid(settlement.getEntity().getUuid()).getData();
			} else if (settlement.getUser() != null && settlement.getUser().getGuid() != null) {
				UserApiInternalClient userClient = services.target(UserApiInternalClient.class, "service-user", true);
				user = userClient.getUser(settlement.getUser().getGuid()).getData();
			}
			
			SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");
			String name = Optional.ofNullable(entity).map(Entity::getName).orElse(user.getUsername());
			Set<Placeholder> placeholders = new HashSet<>();
			placeholders.add(REPORT_SETTLEMENT_TO.from(name));
			placeholders.add(REPORT_SETTLEMENT_PERIOD.from(sdf.format(settlement.getDateStart()) + " to " + sdf.format(settlement.getDateEnd())));

			mailStream.process(
					EmailData.builder()
							.authorSystem()
							.domainName(settlement.getDomain().getName())
							.emailTemplateName(EMAIL_TEMPLATE_NAME)
							.emailTemplateLang(EMAIL_TEMPLATE_LANG)
							.to(entity != null ? entity.getEmail() : user.getEmail())
							.priority(EMAIL_PRIORITY_HIGH)
							.placeholders(placeholders)
							.attachmentName((name + "-settlement-" + sdf.format(settlement.getDateStart()) + "-" +
									sdf.format(settlement.getDateEnd()) + ".pdf").toLowerCase())
							.attachmentData(settlement.getPdf().getPdf())
							.build()
			);
		}
	}
	
	public Settlement markPdfSent(Settlement settlement) {
		SettlementPDF pdf = settlement.getPdf();
		pdf.setSent(true);
		pdf = settlementPDFRepo.save(pdf);
		settlement.setPdf(pdf);
		settlement = settlementRepo.save(settlement);
		return settlement;
	}
}
