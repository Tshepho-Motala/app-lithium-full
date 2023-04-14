package lithium.service.user.services;

import lithium.client.changelog.Category;
import lithium.client.changelog.ChangeLogService;
import lithium.client.changelog.SubCategory;
import lithium.client.changelog.objects.ChangeLogFieldChange;
import lithium.service.user.data.entities.ClosureReason;
import lithium.service.user.data.entities.Domain;
import lithium.service.user.client.objects.ClosureReasonBasic;
import lithium.service.user.data.repositories.ClosureReasonRepository;
import lithium.service.user.data.specifications.ClosureReasonSpecification;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class ClosureReasonService {
    @Autowired
    ClosureReasonRepository closureReasonRepository;
    @Autowired
    @Setter
    ChangeLogService changeLogService;
    @Autowired
    DomainService domainService;

    public Page<ClosureReason> table(String domainName, String searchValue, PageRequest pageable, Boolean hideDeleted) {
        Specification<ClosureReason> spec = Specification.where(ClosureReasonSpecification.domain(domainName));
        if (hideDeleted != null && hideDeleted) {
            Specification<ClosureReason> deletedHidden = Specification.where(ClosureReasonSpecification.deleted(false));
            spec = spec.and(deletedHidden);
        }
        if ((searchValue != null) && (searchValue.length() > 0)) {
            Specification<ClosureReason> s = Specification.where(ClosureReasonSpecification.any(searchValue));
            spec = (spec == null) ? s : spec.and(s);
        }
        Page<ClosureReason> result = closureReasonRepository.findAll(spec, pageable);
        return result;
    }

    public void delete(Long closureReasonId, String comment, String authorGuid) {
        ClosureReason closureReason = closureReasonRepository.findOne(closureReasonId);
        ChangeLogFieldChange c = ChangeLogFieldChange.builder()
                .field("deleted")
                .fromValue(String.valueOf(closureReason.isDeleted()))
                .toValue("true")
                .build();
        closureReason.setDeleted(true);
        closureReasonRepository.save(closureReason);
        List<ChangeLogFieldChange> clfc = new ArrayList<>();
        clfc.add(c);
        try {

            changeLogService.registerChangesWithDomain("closureReason",
                    "delete",
                    closureReason.getId(),
                    authorGuid,
                    comment,
                    null,
                    clfc, Category.ACCOUNT, SubCategory.CLOSURE, 0, closureReason.getDomain().getName());
        } catch (Exception e) {
            log.error("Could not save changelog.", e);
        }
        log.info("Deleted ClosureReason : " + closureReason);
    }

    public ClosureReason add(ClosureReasonBasic closureReasonBasic, String domainName, String authorGuid) {
        Domain domain = domainService.findOrCreate(domainName);
        ClosureReason closureReason = ClosureReason.builder()
                .description(closureReasonBasic.getDescription())
                .text(closureReasonBasic.getText())
                .deleted(false)
                .domain(domain)
                .build();
        log.info("Add [closureReason=" + closureReason + "]");

        closureReason = closureReasonRepository.save(closureReason);
        try {
            List<ChangeLogFieldChange> clfc = changeLogService.copy(closureReason, new ClosureReason(),
                    new String[]{"id", "domain", "description", "text", "deleted"});
            changeLogService.registerChangesWithDomain(
                    "closureReason",
                    "create",
                    closureReason.getId(),
                    authorGuid,
                    null,
                    null,
                    clfc, Category.ACCOUNT, SubCategory.CLOSURE, 0, domainName);
        } catch (Exception e) {
            log.error("Could not save changelog.", e);
        }
        return closureReason;
    }

    public ClosureReason save(ClosureReasonBasic closureReasonBasic, String authorGuid) {

        ClosureReason closureReason = closureReasonRepository.findOne(closureReasonBasic.getId());

        ClosureReason closureCopy = ClosureReason.builder().build();
        BeanUtils.copyProperties(closureReason, closureCopy);

        closureReason.setDescription(closureReasonBasic.getDescription());
        closureReason.setText(closureReasonBasic.getText());
        log.info("Save [closureReason=" + closureReason + "]");


        closureReason = closureReasonRepository.save(closureReason);

        try {
            List<ChangeLogFieldChange> clfc = changeLogService.copy(closureReason, closureCopy,
                    new String[]{"description", "text"});
            changeLogService.registerChangesWithDomain(
                    "closureReason",
                    "edit",
                    closureReasonBasic.getId(),
                    authorGuid,
                    null,
                    null,
                    clfc, Category.ACCOUNT, SubCategory.CLOSURE, 0, closureReason.getDomain().getName()
            );
        } catch (Exception e) {
            log.error("Could not save changelog.", e);
        }
        return closureReason;
    }

}
