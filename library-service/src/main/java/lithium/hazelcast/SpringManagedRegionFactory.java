package lithium.hazelcast;

import com.hazelcast.hibernate.HazelcastCacheRegionFactory;

public class SpringManagedRegionFactory extends HazelcastCacheRegionFactory {

    public SpringManagedRegionFactory() throws IllegalAccessException {
        super.instance = HazelcastSingleton.getInstance().getHazelcast();
    }
}
