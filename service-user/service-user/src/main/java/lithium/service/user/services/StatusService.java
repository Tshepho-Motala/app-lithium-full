package lithium.service.user.services;

import com.google.common.collect.Lists;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import lithium.service.user.data.entities.Status;
import lithium.service.user.data.entities.StatusReason;
import lithium.service.user.data.entities.StatusStatusReasonAssociation;
import lithium.service.user.data.repositories.StatusReasonRepository;
import lithium.service.user.data.repositories.StatusRepository;
import lithium.service.user.data.repositories.StatusStatusReasonAssociationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class StatusService {
	@Autowired private StatusRepository statusesRepo;
	@Autowired private StatusReasonRepository reasonsRepo;
	@Autowired private StatusStatusReasonAssociationRepository associationsRepo;

	public Iterable<Status> findAllNotDeletedStatuses() {
    //LSPLAT-6954 Status "Deleted" should be hidden from any LBO screens
		return statusesRepo.findAllByNameIsNot(lithium.service.user.client.enums.Status.DELETED.statusName());
	}

	public Iterable<StatusReason> findAllStatusReasons() {
		return reasonsRepo.findAll();
	}

	public List<StatusReason> findReasonsByStatus(Status status) {
		return associationsRepo.findByStatus(status)
		.stream()
		.map(association -> association.getReason())
		.collect(Collectors.toList());
	}

	/**
	 * Called on service startup.<br/>
	 * Populates statuses, reasons, and their associations based on the respective enums.<br/><br/>
	 *
	 * To add new statuses and/or reasons, alter the enums.
	 *
	 * @see lithium.service.user.client.enums.Status
	 * @see lithium.service.user.client.enums.StatusReason
	 */
	public void setupFromEnums() {
		Arrays.stream(lithium.service.user.client.enums.StatusReason.values()).forEach(reason -> {
			reasonsRepo.findOrCreateByName(reason.statusReasonName(),
				() -> StatusReason.builder().description(reason.description()).build());
		});
		Arrays.stream(lithium.service.user.client.enums.Status.values()).forEach(status -> {
			Status dbStatus = statusesRepo.findOrCreateByName(status.statusName(),
				() -> Status.builder().description(status.description()).userEnabled(status.userEnabled()).build());
			Arrays.stream(status.possibleReasons()).forEach(possibleReason -> {
				StatusReason reason = reasonsRepo.findByName(possibleReason.statusReasonName());
				if (associationsRepo.findByStatusAndReason(dbStatus, reason) == null) {
					associationsRepo.save(
						StatusStatusReasonAssociation.builder()
						.status(dbStatus)
						.reason(reason)
						.build()
					);
				}
			});
		});
	}
}
