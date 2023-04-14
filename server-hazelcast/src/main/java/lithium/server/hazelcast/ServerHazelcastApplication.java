package lithium.server.hazelcast;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;

import com.hazelcast.config.Config;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;

@SpringBootApplication
@EnableScheduling
public class ServerHazelcastApplication {

	public static void main(String[] args) {
		SpringApplication.run(ServerHazelcastApplication.class, args);
	}
	
	@Bean
	HazelcastInstance hazelcastInstance() {
		return Hazelcast.newHazelcastInstance(Config.load());
	}
}
