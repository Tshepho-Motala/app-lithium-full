package lithium.jpa.entity;

/**
 * Required when implementing the FindOrCreate pattern on code.
 *
 * @see lithium.jpa.repository.FindOrCreateByCodeRepository
 */
public interface EntityWithUniqueCode {
    public String getCode();
    public void setCode(String name);
}
