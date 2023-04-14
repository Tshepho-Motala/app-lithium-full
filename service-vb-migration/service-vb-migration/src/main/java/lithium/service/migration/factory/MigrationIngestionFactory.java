package lithium.service.migration.factory;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import lithium.service.libraryvbmigration.data.enums.MigrationType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class MigrationIngestionFactory {
  private final Map<MigrationType, MigrationIngestion> viewerMap;

  @Autowired
  private MigrationIngestionFactory(List<MigrationIngestion> viewers) {
    viewerMap = viewers.stream().collect(Collectors.toUnmodifiableMap(MigrationIngestion::getType, Function.identity()));
  }

  public MigrationIngestion getMigration(MigrationType viewerType) {
    return   Optional.ofNullable(viewerMap.get(viewerType)).orElseThrow(IllegalArgumentException::new);
  }

}
