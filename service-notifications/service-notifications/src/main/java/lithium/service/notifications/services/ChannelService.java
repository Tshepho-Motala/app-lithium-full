package lithium.service.notifications.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import lithium.service.notifications.data.entities.Channel;
import lithium.service.notifications.data.repositories.ChannelRepository;

@Service
public class ChannelService {
	@Autowired ChannelRepository repository;
	
	public Channel findOrCreate(String name) {
		Channel channel = repository.findByName(name);
		if (channel == null) {
			channel = repository.save(Channel.builder().name(name).build());
		}
		return channel;
	}
}
