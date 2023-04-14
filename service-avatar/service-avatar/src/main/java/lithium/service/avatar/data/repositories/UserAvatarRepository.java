package lithium.service.avatar.data.repositories;

import java.util.List;

import org.springframework.data.repository.PagingAndSortingRepository;

import lithium.service.avatar.data.entities.UserAvatar;

public interface UserAvatarRepository extends PagingAndSortingRepository<UserAvatar, Long> {
	UserAvatar findByUserGuid(String userGuid);
	List<UserAvatar> findByAvatarId(Long avatarId);
}
