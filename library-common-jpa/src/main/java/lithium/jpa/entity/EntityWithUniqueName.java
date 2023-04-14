package lithium.jpa.entity;

/**
 * Required when implementing the FindOrCreate pattern on name.
 *
 * @see lithium.jpa.repository.FindOrCreateByNameRepository
 */
public interface EntityWithUniqueName {
    public String getName();
    public void setName(String name);
}
