package lithium.jpa.entity;

/**
 * A factory interface used by the FindOrCreate family of repository interfaces so that callers my provide
 * a builder function with lambda that creates an object if it does not already exist.
 *
 * <pre>
 * {@code
 * Currency currency = currencyRepository.findOrCreateByCode(request.getCurrencyCode(), () -> new Currency());
 * }
 * </pre>
 *
 * @param <T> The type of the object to create.
 * @see lithium.jpa.repository.FindOrCreateByCodeRepository
 * @see lithium.jpa.repository.FindOrCreateByNameRepository
 * @see lithium.jpa.repository.FindOrCreateByGuidRepository
 */
public interface EntityFactory<T> {
    public T build();
}
