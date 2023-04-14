package lithium.service.avatar.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import lithium.service.avatar.data.entities.Avatar;
import lithium.service.avatar.data.entities.User;
import lithium.service.avatar.data.entities.UserAvatar;
import lithium.service.avatar.data.repositories.UserAvatarRepository;
import lithium.service.promo.client.stream.MissionStatsStream;
import lithium.tokens.LithiumTokenUtil;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class UserAvatarService {
	@Autowired UserAvatarRepository repository;
	@Autowired AvatarService avatarService;
	@Autowired UserService userService;
	@Autowired MissionStatsStream missionStatsStream;
	
	public UserAvatar findByUserGuid(String domainName, String userName) {
		UserAvatar userAvatar = repository.findByUserGuid(domainName+"/"+userName);
		if (userAvatar == null) {
			Avatar domainDefaultAvatar = avatarService.findDomainDefault(domainName);
			if (domainDefaultAvatar != null && domainDefaultAvatar.getEnabled()) {
				userAvatar = UserAvatar.builder()
				.avatar(avatarService.findDomainDefault(domainName))
				.build();
			} else {
				userAvatar = UserAvatar.builder().avatar(Avatar.builder().id(-1L).build()).build();
			}
		}
		return userAvatar;
	}
	
	public UserAvatar setUserAvatar(String userGuid, Avatar avatar, LithiumTokenUtil tokenUtil) throws Exception {
		if (!tokenUtil.guid().equalsIgnoreCase(userGuid)) {
			throw new Exception("Token mismatch. userGuid: " + userGuid + ", tokenUtil guid: " + tokenUtil.guid());
		}
		UserAvatar userAvatar = repository.findByUserGuid(userGuid);
		if (userAvatar == null) {
			User user = userService.findOrCreate(userGuid);
			userAvatar = UserAvatar.builder()
			.user(user)
			.avatar(avatar)
			.build();
		} else {
			userAvatar.setAvatar(avatar);
		}
		userAvatar = repository.save(userAvatar);
//		try {
//			missionStatsStream.register(
//				MissionStatBasic.builder()
//				.ownerGuid(userGuid)
//				.type(Type.TYPE_AVATAR)
//				.action(Action.ACTION_UPDATE)
//				.identifier(null)
//				.value(null)
//				.build()
//			);
//			log.debug("Streamed stat entry for avatar update | userGuid: " + userGuid + ", userAvatar: " + userAvatar.getId());
//		} catch (Exception e) {
//			log.error("Could not stream stat entry for avatar update | userGuid: " + userGuid + ", userAvatar: " + userAvatar.getId() + " | " + e.getMessage(), e);
//		}
		return userAvatar;
	}
}
