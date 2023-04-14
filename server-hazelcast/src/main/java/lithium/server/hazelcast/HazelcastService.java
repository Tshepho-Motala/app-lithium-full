package lithium.server.hazelcast;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.hazelcast.core.HazelcastInstance;

@Service
public class HazelcastService {
	
	@Autowired
	HazelcastInstance hazelcast;
	
	@Scheduled(fixedRate = 10000) 
	public void status() {
		hazelcast.getMap("hazelcast-server-node").set("last-update", new Date().toString());
	}
	
}
