package lithium.jpa.entity;

/**
 * Required when implementing the FindOrCreate pattern on guid.
 *
 * @see lithium.jpa.repository.FindOrCreateByGuidRepository
 */
public interface EntityWithUniqueGuid {
    public String getGuid();
    public void setGuid(String guid);
}
