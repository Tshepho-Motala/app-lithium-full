package lithium.jpa.repository;

import lithium.jpa.entity.EntityWithUniqueGuid;
import lithium.jpa.entity.EntityFactory;
import lithium.jpa.exceptions.CannotAcquireLockException;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import javax.persistence.LockModeType;
import java.io.Serializable;

/**
 * Extend from {@code FindOrCreateByGuidRepository} in your Spring Data JPA
 * {@link org.springframework.data.repository.Repository} implementation interface in order to force your repository
 * to follow the unique guid pattern. This will force your entity to define a field named guid, and will
 * provide a default implementation for a finder that will create the object for you if the object with that unique guids
 * does not already exist in the database.
 * <p>
 * The caller should then provide a callback that populates the object with useful data in the case where it does not
 * already exist in the database.
 * <pre>
 * {@code
 * User user = userRepository.findOrCreateByGuid(playerGuid, () -> new User());
 * }
 * </pre>
 * <p>Your entity will need to implement the {@link EntityWithUniqueGuid} interface</p>
 */
@NoRepositoryBean
public interface FindOrCreateByGuidRepository<T extends EntityWithUniqueGuid, ID extends Serializable> extends PagingAndSortingRepository<T, ID> {

    T findByGuid(String guid);

    @Query("select locking_o from #{#entityName} locking_o where locking_o.guid = :guid")
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    T findByGuidAlwaysLock(@Param("guid") String guid);

    default public T findOrCreateByGuid(String guid, EntityFactory<T> factory) {
        T t = findByGuid(guid);
        if (t == null) {
            t = factory.build();
            t.setGuid(guid);
            save(t);
        }
        return t;
    }

    default public T findOrCreateByGuidAlwaysLock(String guid, EntityFactory<T> factory) throws CannotAcquireLockException {
        try {
            T t = findByGuidAlwaysLock(guid);
            if (t == null) {
                t = factory.build();
                t.setGuid(guid);
                save(t);
            }
            return t;
        } catch (org.springframework.dao.CannotAcquireLockException e) {
            throw new CannotAcquireLockException(e.getMessage(), e);
        }
    }

}
