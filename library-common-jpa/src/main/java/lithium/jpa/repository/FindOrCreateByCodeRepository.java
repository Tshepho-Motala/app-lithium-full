package lithium.jpa.repository;

import lithium.jpa.entity.EntityFactory;
import lithium.jpa.entity.EntityWithUniqueCode;
import lithium.jpa.entity.EntityWithUniqueName;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.io.Serializable;

/**
 * Extend from {@code FindOrCreateByCodeRepository} in your Spring Data JPA
 * {@link org.springframework.data.repository.Repository} implementation interface in order to force your repository
 * to follow the unique code pattern. This will force your entity to define a field named code, and will
 * provide a default implementation for a finder that will create the object for you if the object with that unique code
 * does not already exist in the database.
 * <p>
 * The caller should then provide a callback that populates the object with useful data in the case where it does not
 * already exist in the database.
 * <pre>
 * {@code
 * Currency currency = currencyRepository.findOrCreateByCode(request.getCurrencyCode(), () -> new Currency());
 * }
 * </pre>
 * <p>Your entity will need to implement the {@link EntityWithUniqueCode} interface</p>
 */
@NoRepositoryBean
public interface FindOrCreateByCodeRepository<T extends EntityWithUniqueCode, ID extends Serializable> extends PagingAndSortingRepository<T, ID> {

    T findByCode(String code);

    default public T findOrCreateByCode(String code, EntityFactory<T> factory) {
        T t = findByCode(code);
        if (t == null) {
            t = factory.build();
            t.setCode(code);
            save(t);
        }
        return t;
    }
}
