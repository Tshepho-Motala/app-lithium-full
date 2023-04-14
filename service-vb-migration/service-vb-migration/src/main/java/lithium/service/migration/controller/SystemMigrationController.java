package lithium.service.migration.controller;

import lithium.exceptions.Status400BadRequestException;
import lithium.exceptions.Status412DomainNotFoundException;
import lithium.metrics.TimeThisMethod;
import lithium.service.Response;
import lithium.service.Response.Status;
import lithium.service.migration.config.ServiceVbMigrationConfigProperties;
import lithium.service.migration.service.MigrationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/system/migration/{domainName}")
public class SystemMigrationController {

  private final MigrationService migrationService;
  private final ServiceVbMigrationConfigProperties properties;

  @PostMapping("/initiate-migration/{migration-type}/{page-size}")
  @TimeThisMethod
  public Response<String> initiateMigration(@PathVariable("domainName") String domainName,
      @PathVariable("migration-type") String migrationType,
      @PathVariable(value = "page-size", required = false) int pageSize
  ) throws Status412DomainNotFoundException {
    try {
      pageSize = ObjectUtils.isEmpty(pageSize) ? properties.getQuery().getBatchSize() : pageSize;
      return Response.<String>builder().data(migrationService.initializeMigration(domainName, migrationType, pageSize)).build();
    } catch (Status400BadRequestException e) {
      log.error(e.getMessage(), e);
      return Response.<String>builder().status(Status.BAD_REQUEST).message(e.getMessage()).build();
    } catch (InterruptedException e) {
      log.error(e.getMessage());
      Thread.currentThread().interrupt();
      return Response.<String>builder().status(Status.INTERNAL_SERVER_ERROR).message(e.getMessage()).build();
    } catch (Exception e) {
      log.error(e.getMessage(), e);
      return Response.<String>builder().status(Status.SERVER_TIMEOUT).message(e.getMessage()).build();
    }
  }



}
