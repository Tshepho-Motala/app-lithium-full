package lithium.hazelcast;

import com.hazelcast.core.HazelcastInstance;
import java.util.Optional;

public final class HazelcastSingleton {
    private static HazelcastSingleton instance;

    private volatile HazelcastInstance hazelcast;
    
    private HazelcastSingleton() {
    }

    public synchronized static HazelcastSingleton getInstance() {
        if (instance == null) {
            instance = new HazelcastSingleton();
        }

        return instance;
    }

    public HazelcastInstance getHazelcast() throws IllegalAccessException {
        return Optional.ofNullable(hazelcast).orElseThrow(
                () -> new IllegalAccessException("Hazelcast instance has not been set"));
        
    }

    public void setHazelcast(HazelcastInstance hazelcast) throws IllegalAccessException {
        if (this.hazelcast == null) {
            this.hazelcast = hazelcast;
        } else {
            throw new IllegalAccessException("Hazelcast instance already set");
        }
    }
}
