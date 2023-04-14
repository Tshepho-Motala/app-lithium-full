package lithium.jpa.repository;

import lithium.jpa.entity.EntityFactory;
import lithium.jpa.entity.EntityWithUniqueGuid;
import lithium.jpa.entity.EntityWithUniqueName;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import javax.persistence.LockModeType;
import java.io.Serializable;

/**
 * Extend from {@code FindOrCreateByNameRepository} in your Spring Data JPA
 * {@link org.springframework.data.repository.Repository} implementation interface in order to force your repository
 * to follow the unique code pattern. This will force your entity to define a field named name, and will
 * provide a default implementation for a finder that will create the object for you if the object with that unique name
 * does not already exist in the database.
 * <p>
 * The caller should then provide a callback that populates the object with useful data in the case where it does not
 * already exist in the database.
 * <pre>
 * {@code
 * Domain domain = domainRepository.findOrCreateByName(domainName, () -> new Domain());
 * }
 * </pre>
 * <p>Your entity will need to implement the {@link EntityWithUniqueName} interface</p>
 */
@NoRepositoryBean
public interface FindOrCreateByNameRepository<T extends EntityWithUniqueName, ID extends Serializable> extends PagingAndSortingRepository<T, ID> {

    T findByName(String name);

    default public T findOrCreateByName(String name, EntityFactory<T> factory) {
        T t = findByName(name);
        if (t == null) {
            t = factory.build();
            t.setName(name);
            save(t);
        }
        return t;
    }
}
