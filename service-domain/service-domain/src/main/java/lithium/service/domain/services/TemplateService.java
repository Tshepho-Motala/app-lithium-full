package lithium.service.domain.services;

import lithium.client.changelog.Category;
import lithium.client.changelog.ChangeLogService;
import lithium.client.changelog.SubCategory;
import lithium.client.changelog.objects.ChangeLogFieldChange;
import lithium.client.changelog.objects.ChangeLogs;
import lithium.service.Response;
import lithium.service.client.datatable.DataTableRequest;
import lithium.service.client.datatable.DataTableResponse;
import lithium.service.domain.data.entities.Domain;
import lithium.service.domain.data.entities.Template;
import lithium.service.domain.data.entities.TemplateRevision;
import lithium.service.domain.data.repositories.DomainRepository;
import lithium.service.domain.data.repositories.TemplateRepository;
import lithium.service.domain.data.repositories.TemplateRevisionRepository;
import lithium.service.domain.data.specifications.TemplateSpecification;
import lithium.tokens.LithiumTokenUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.Date;
import java.util.List;

@Service
public class TemplateService {
	@Autowired TemplateRepository repo;
	@Autowired TemplateRevisionRepository revisionRepo;
	@Autowired UserService userService;
	@Autowired DomainRepository domainRepo;
	@Autowired ChangeLogService changeLogService;

	public DataTableResponse<Template> table(@PathVariable String domainName, DataTableRequest request) {

		Specification<Template> spec = Specification.where(TemplateSpecification.isDelete(false))
        .and(TemplateSpecification.domainName(domainName));

		if ((request.getSearchValue() != null) && (request.getSearchValue().length() > 0)) {
			Specification<Template> s = Specification.where(TemplateSpecification.any(request.getSearchValue()));
			spec = (spec == null)? s: spec.and(s);
		}
		return new DataTableResponse<>(request, repo.findAll(spec, request.getPageRequest()));
	}

	public List<Template> findByDomainNameAndLang(String domainName, String lang) {
		return repo.findByDomainNameAndLang(domainName, lang);
	}

	public Template findByNameAndLangAndDomainName(String domainName, String name, String lang) {
		return repo.findByDomainNameAndNameAndLang(domainName, name, lang);
	}

	@Transactional
	public Template add(String domainName, Template t, LithiumTokenUtil util) throws Exception {
		Domain domain = domainRepo.findByName(domainName);
		if (domain == null) throw new Exception("Domain does not exist");

		t.setDomain(domain);
		t = repo.save(t);
		t.getCurrent().setTemplate(t);
		t.setCurrent(revisionRepo.save(t.getCurrent()));

		t = repo.save(t);

    List<ChangeLogFieldChange> clfc = changeLogService.compare(t,new Template(),
        new String[] { "lang", "name", "enabled" });
    ChangeLogFieldChange fieldEnabledChange =
        ChangeLogFieldChange.builder().field("domain").fromValue("").toValue(t.getDomain().getName()).build();
    clfc.add(fieldEnabledChange);
    changeLogService.registerChangesForNotesWithFullNameAndDomain("template", "create", t.getId(), util.guid(), util, null,
        null, clfc, Category.SUPPORT, SubCategory.TEMPLATES, 0, domainName);

    return t;
	}

  @Transactional
  public Boolean delete(String domainName, Template t, LithiumTokenUtil util) throws Exception {
    Domain domain = domainRepo.findByName(domainName);
    if (domain == null) {
      throw new Exception("Domain does not exist");
    }

    if (t.getDeleted()) {
      throw new Exception("Template does not exist");
    }

    t.setDeleted(true);
    t.setName(t.getName());

    repo.save(t);

    List<ChangeLogFieldChange> clfc = changeLogService.compare(new Template(), t,
        new String[] { "lang", "name","deleted", "editBy" });
    ChangeLogFieldChange fieldEnabledChange =
        ChangeLogFieldChange.builder().field("domain").fromValue(t.getDomain().getName()).toValue("").build();
    clfc.add(fieldEnabledChange);
    changeLogService.registerChangesForNotesWithFullNameAndDomain("template", "delete", t.getId(), util.guid(), util, null,
        null, clfc, Category.SUPPORT, SubCategory.TEMPLATES, 0, domainName);

    return true;
  }


  @Transactional
	public Template edit(String domainName, Template t, LithiumTokenUtil util) throws Exception {
		Domain domain = domainRepo.findByName(domainName);
		if (domain == null) throw new Exception("Domain does not exist");

		if (t.getEdit() == null) {
      lithium.service.domain.data.entities.User user = userService.findOrCreate(util.guid());
			TemplateRevision revision = TemplateRevision.builder().template(t).description(t.getCurrent().getDescription()).content(t.getCurrent().getContent()).head(t.getCurrent().getHead()).build();
			revision = revisionRepo.save(revision);

			t.setDomain(domain);
			t.setEdit(revision);
			t.setEditBy(user);
			t.setEditStartedOn(new Date());
			t = repo.save(t);
		}
		return t;
	}

	public Template save(String domainName, Template t, LithiumTokenUtil util) throws Exception {
		Domain domain = domainRepo.findByName(domainName);
		if (domain == null) throw new Exception("Domain does not exist");

		Template current = repo.findOne(t.getId());
		TemplateRevision currentRevision = revisionRepo.findOne(t.getCurrent().getId());
		List<ChangeLogFieldChange> clfc = changeLogService.copy(t.getEdit(), currentRevision, new String[] { "description", "content", "head" });
		if (current.getEnabled() != t.getEnabled()) {
			ChangeLogFieldChange fieldEnabledChange =
					ChangeLogFieldChange.builder().field("enabled").fromValue(current.getEnabled().toString()).toValue(t.getEnabled().toString()).build();
			clfc.add(fieldEnabledChange);
		}

		if(!current.getLang().equals(t.getLang())) {
      ChangeLogFieldChange fieldChangeName =
          ChangeLogFieldChange.builder().field("lang").fromValue(current.getLang()).toValue(t.getLang()).build();
      clfc.add(fieldChangeName);
    }

		if (!current.getName().equals(t.getName())) {
			ChangeLogFieldChange fieldChangeName =
					ChangeLogFieldChange.builder().field("name").fromValue(current.getName()).toValue(t.getName()).build();
			clfc.add(fieldChangeName);
		}
    if (!current.getDomain().getName().equals(t.getDomain().getName())) {
      ChangeLogFieldChange fieldChangeName =
          ChangeLogFieldChange.builder().field("domain").fromValue(current.getDomain().getName()).toValue(t.getDomain().getName()).build();
      clfc.add(fieldChangeName);
    }
      t.getEdit().setTemplate(t);
		revisionRepo.save(t.getEdit());
		t.setDomain(domain);
		t.setCurrent(t.getEdit());
		t.setEdit(null);
		t.setEditBy(null);
		t.setEditStartedOn(null);
		t = repo.save(t);
    changeLogService.registerChangesForNotesWithFullNameAndDomain("template", "edit", t.getId(), util.guid(), util, null,
        null, clfc, Category.SUPPORT, SubCategory.TEMPLATES, 0, domainName);

    return t;
	}

	public Template continueLater(String domainName, Template t, String authorGuid) throws Exception {
		Domain domain = domainRepo.findByName(domainName);
		if (domain == null) throw new Exception("Domain does not exist");

		Template current = repo.findOne(t.getId());
		t.getEdit().setTemplate(t);
		TemplateRevision edit = revisionRepo.save(t.getEdit());
		current.setDomain(domain);
		current.setEdit(edit);
		current.setEditStartedOn(new Date());
		current.setEditBy(userService.findOrCreate(authorGuid));
		current = repo.save(current);
		return current;
	}

	public Template cancelEdit(String domainName, Template t) throws Exception {
		Domain domain = domainRepo.findByName(domainName);
		if (domain == null) throw new Exception("Domain does not exist");

		Template current = repo.findOne(t.getId());
		current.setDomain(domain);
		current.setEdit(null);
		current.setEditBy(null);
		current.setEditStartedOn(null);
		current = repo.save(current);
		return current;
	}

	public Response<ChangeLogs> changeLogs(String domainName, Long id, int p) throws Exception {
		Domain domain = domainRepo.findByName(domainName);
		if (domain == null) throw new Exception("Domain does not exist");

		return changeLogService.list("template", id, p);
	}
}
