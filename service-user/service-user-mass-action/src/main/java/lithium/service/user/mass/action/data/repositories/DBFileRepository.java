package lithium.service.user.mass.action.data.repositories;

import lithium.service.user.mass.action.data.entities.DBFile;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DBFileRepository extends JpaRepository<DBFile, Long> {
}