package lithium.service.translate.controllers;

import lithium.exceptions.Status500InternalServerErrorException;
import lithium.service.translate.client.objects.ChangeSet;
import lithium.service.translate.services.AngularService;
import lithium.service.translate.services.ChangeSetService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/apiv2")
public class ChangeSetController {

    @Autowired ChangeSetService changeSetService;
    @Autowired AngularService angularService;

    @GetMapping("/changesets/list")
    public List<ChangeSet> getChangeSets(@RequestParam("name") String name) {
        return changeSetService.getChangeSets(name);
    }

    @PostMapping("/changesets/register")
    public void registerChangeSet(@RequestParam("locale2") String locale2, @RequestParam("name") String name,
            @RequestParam("changeReference") String changeReference, @RequestParam("checksum") String checksum) {
        changeSetService.registerNewChangeSet(locale2, name, changeReference, checksum);
    }

    @GetMapping("/changesets/build-translation-cache")
    public void buildTranslationCache() {
        angularService.evictAllCacheValues();
    }

    @DeleteMapping("changesets/remove")
    public void removeChangeSet(@RequestParam("locale2") String locale2, @RequestParam("name") String name, @RequestParam("changeReference") String changeReference) {
        changeSetService.removeChangeSet(locale2, name, changeReference);
    }

    @DeleteMapping("changesets/remove/all")
    public void removeAllChangeSets(@RequestParam("name") String name) {
        changeSetService.removeAllChangeSetsByName(name);
    }
}