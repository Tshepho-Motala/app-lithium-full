package lithium.service.avatar.services;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.transaction.Transactional;

import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;

import lithium.service.avatar.data.entities.Avatar;
import lithium.service.avatar.data.entities.Graphic;
import lithium.service.avatar.data.entities.UserAvatar;
import lithium.service.avatar.data.repositories.AvatarRepository;
import lithium.service.avatar.data.repositories.GraphicRepository;
import lithium.service.avatar.data.repositories.UserAvatarRepository;
import lithium.service.avatar.data.specifications.AvatarSpecifications;
import lithium.tokens.LithiumTokenUtil;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class AvatarService {
	@Autowired AvatarRepository repository;
	@Autowired UserAvatarRepository userAvatarRepository;
	@Autowired DomainService domainService;
	@Autowired GraphicRepository graphicRepository;
	@Autowired ApplicationContext appContext;
	
	public Page<Avatar> findByDomain(String domainName, String searchValue, Pageable pageable, LithiumTokenUtil tokenUtil) {
		Specification<Avatar> spec = Specification.where(AvatarSpecifications.domain(domainName));
		if ((searchValue != null) && (searchValue.length() > 0)) {
			Specification<Avatar> s = Specification.where(AvatarSpecifications.any(searchValue));
			spec = (spec == null)? s: spec.and(s);
		}
		Page<Avatar> result = repository.findAll(spec, pageable);
		return result;
	}
	
	public List<Avatar> findEnabledByDomain(String domainName) {
		return repository.findByDomainNameAndEnabledTrue(domainName);
	}
	
	public Avatar findDomainDefault(String domainName) {
		return repository.findByDomainNameAndIsDefaultTrue(domainName);
	}
	
	@Transactional(rollbackOn=Exception.class)
	@Retryable(backoff=@Backoff(delay=500), maxAttempts=10)
	public Avatar add(String domainName, lithium.service.avatar.client.objects.Avatar avatarPost) {
		Graphic graphic = graphicRepository.save(
			Graphic.builder()
			.name(avatarPost.getGraphicBasic().getFilename())
			.type(avatarPost.getGraphicBasic().getFiletype())
			.size(avatarPost.getGraphicBasic().getFilesize())
			.image(avatarPost.getGraphicBasic().getBase64())
			.build()
		);
		
		if (avatarPost.getIsDefault() != null && avatarPost.getIsDefault()) {
			Avatar oldDefault = findDomainDefault(domainName);
			if (oldDefault != null) {
				oldDefault.setIsDefault(false);
				oldDefault = repository.save(oldDefault);
			}
		}
		
		Avatar avatar = Avatar.builder()
		.domain(domainService.findOrCreate(domainName))
		.name(avatarPost.getName())
		.description(avatarPost.getDescription())
		.graphic(graphic)
		.enabled(avatarPost.getEnabled())
		.isDefault(avatarPost.getIsDefault())
		.build();
		
		return repository.save(avatar);
	}
	
	@Transactional(rollbackOn=Exception.class)
	@Retryable(backoff=@Backoff(delay=500), maxAttempts=10)
	public void delete(String domainName, Long id) {
		Avatar domainDefaultAvatar = findDomainDefault(domainName);
		if (domainDefaultAvatar != null && domainDefaultAvatar.getId() == id)
			domainDefaultAvatar = null;
		for (UserAvatar ua: userAvatarRepository.findByAvatarId(id)) {
			if (domainDefaultAvatar == null) {
				userAvatarRepository.delete(ua);
			} else {
				ua.setAvatar(domainDefaultAvatar);
				ua = userAvatarRepository.save(ua);
			}
		}
		Avatar avatar = repository.findOne(id);
		Long graphicId = avatar.getGraphic().getId();
		repository.deleteById(id);
		graphicRepository.deleteById(graphicId);
	}
	
	public Avatar toggleEnable(String domainName, Avatar avatar) {
		avatar.setEnabled(!avatar.getEnabled());
		if (!avatar.getEnabled()) {
			Avatar domainDefaultAvatar = findDomainDefault(domainName);
			for (UserAvatar ua: userAvatarRepository.findByAvatarId(avatar.getId())) {
				ua.setAvatar(domainDefaultAvatar);
				ua = userAvatarRepository.save(ua);
			}
		}
		return repository.save(avatar);
	}
	
	public Avatar setAsDefault(String domainName, Avatar avatar) {
		Avatar oldDefault = findDomainDefault(domainName);
		if (oldDefault != null) {
			oldDefault.setIsDefault(false);
			oldDefault = repository.save(oldDefault);
		}
		avatar.setIsDefault(true);
		return repository.save(avatar);
	}
	
	public ResponseEntity<byte[]> getAvatarImageAsResponseEntity(Long avatarId) {
		Avatar avatar = repository.findOne(avatarId);
		HttpHeaders headers = new HttpHeaders();
		headers.setCacheControl(CacheControl.maxAge(1, TimeUnit.DAYS).noTransform().cachePublic().getHeaderValue());
		byte[] imageBytes = null;
		if (avatar != null) {
			switch (avatar.getGraphic().getType()) {
				case MediaType.IMAGE_GIF_VALUE: headers.setContentType(MediaType.IMAGE_GIF); break;
				case MediaType.IMAGE_JPEG_VALUE: headers.setContentType(MediaType.IMAGE_JPEG); break;
				case MediaType.IMAGE_PNG_VALUE: headers.setContentType(MediaType.IMAGE_PNG); break;
				default:;
			}
			imageBytes = avatar.getGraphic().getImage();
		} else {
			headers.setContentType(MediaType.IMAGE_PNG);
			imageBytes = getSysAvatarBytes();
		}
		return new ResponseEntity<>(imageBytes, headers, HttpStatus.OK);
	}
	
	private byte[] getSysAvatarBytes() {
		Resource r = appContext.getResource("classpath:/images/sys-avatar.png");
		byte[] sysAvatarBytes = null;
		if (r.exists()) {
			try {
				sysAvatarBytes = IOUtils.toByteArray(r.getInputStream());
			} catch (IOException e) {
				log.warn("Unable to read sys avatar from: " + r.getDescription());
			}
		}
		return sysAvatarBytes;
	}
}
