package lithium.service.cashier.services;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import lithium.service.cashier.data.entities.Profile;
import lithium.service.cashier.data.entities.ProfileRequirements;
import lithium.service.cashier.data.repositories.ProfileRepository;
import lithium.service.cashier.data.repositories.ProfileRequirementsRepository;
import lithium.service.cashier.data.repositories.UserRepository;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class ProfileService {
	@Autowired
	private DomainMethodProcessorProfileService domainMethodProcessorProfileService;
	@Autowired
	private DomainMethodProfileService domainMethodProfileService;
	@Autowired
	private DomainService domainService;
	@Autowired
	private ProfileRepository profileRepository;
	@Autowired
	private ProfileRequirementsRepository profileRequirementsRepository;
	@Autowired
	private UserRepository userRepository;
	
	public Profile update(
		Long profileId,
		String code,
		String name,
		String description,
		Long totalDeposits,
		Long totalPayouts,
		Integer numberDeposits,
		Integer numberPayouts,
		Integer accountActiveDays
	) {
		Profile profile = profileRepository.findOne(profileId);
		ProfileRequirements profileRequirements = profile.getProfileRequirements();
		profileRequirements.setTotalDeposits(totalDeposits);
		profileRequirements.setTotalPayouts(totalPayouts);
		profileRequirements.setNumberDeposits(numberDeposits);
		profileRequirements.setNumberPayouts(numberPayouts);
		profileRequirements.setAccountActiveDays(accountActiveDays);
		profileRequirements = profileRequirementsRepository.save(profileRequirements);
		profile.setCode(code);
		profile.setName(name);
		profile.setDescription(description);
		profile.setProfileRequirements(profileRequirements);
		return profileRepository.save(profile);
	}
	
	public Profile save(
		Profile profile
	) {
		Profile p = profileRepository.findByDomainNameAndCode(profile.getDomain().getName(), profile.getCode());
		if (p == null) {
			return profileRepository.save(
				Profile.builder()
				.code(profile.getCode())
				.name(profile.getName())
				.description(profile.getDescription())
				.domain(domainService.findOrCreateDomain(profile.getDomain().getName()))
				.build()
			);
		} else {
			p.setCode(profile.getCode());
			p.setName(profile.getName());
			p.setDescription(profile.getDescription());
			return profileRepository.save(p);
		}
	}
	
	public Profile save(
		String domainName,
		String code,
		String name,
		String description,
		Long totalDeposits,
		Long totalPayouts,
		Integer numberDeposits,
		Integer numberPayouts,
		Integer accountActiveDays
	) {
		Profile profile = profileRepository.findByDomainNameAndCode(domainName, code);
		if (profile == null) {
			//Completely new profile creation
			ProfileRequirements profileRequirements = profileRequirementsRepository.save(
				ProfileRequirements.builder()
				.totalDeposits(totalDeposits)
				.totalPayouts(totalPayouts)
				.numberDeposits(numberDeposits)
				.numberPayouts(numberPayouts)
				.accountActiveDays(accountActiveDays)
				.build()
			);
			return profileRepository.save(
				Profile.builder()
				.code(code)
				.name(name)
				.description(description)
				.domain(domainService.findOrCreateDomain(domainName))
				.profileRequirements(profileRequirements)
				.build()
			);
		} else {
			//Profile found, only updating existing record.
			ProfileRequirements profileRequirements = profile.getProfileRequirements();
			profileRequirements.setTotalDeposits(totalDeposits);
			profileRequirements.setTotalPayouts(totalPayouts);
			profileRequirements.setNumberDeposits(numberDeposits);
			profileRequirements.setNumberPayouts(numberPayouts);
			profileRequirements.setAccountActiveDays(accountActiveDays);
			profileRequirements = profileRequirementsRepository.save(profileRequirements);
			profile.setCode(code);
			profile.setName(name);
			profile.setDescription(description);
			profile.setProfileRequirements(profileRequirements);
			return profileRepository.save(profile);
		}
	}
	
	public Profile find(Long profileId) {
		return profileRepository.findOne(profileId);
	}
	
	public List<Profile> findAll() {
		log.warn("Returning ALL Profiles.");
		Iterable<Profile> iterable = profileRepository.findAll();
		List<Profile> profiles = new ArrayList<>();
		iterable.forEach(profiles::add);
		return profiles.stream().filter(p -> !p.getDeleted()).collect(Collectors.toList());
	}
	
	public List<Profile> findByDomainName(String domainName) {
		return profileRepository.findByDomainName(domainName).stream().filter(p -> !p.getDeleted()).collect(Collectors.toList());
	}
	
	public Profile findByDomainNameAndCode(String domainName, String code) {
		return profileRepository.findByDomainNameAndCode(domainName, code);
	}
	
	public List<Profile> delete(String domainName, Profile profile) { 
		domainMethodProfileService.findByProfile(profile).forEach(dmp -> {
			if (dmp.getEnabled() == null || dmp.getEnabled())
				domainMethodProfileService.toggleEnable(dmp);
		});
		domainMethodProcessorProfileService.findByProfile(profile).forEach(dmpp -> {
			if (dmpp.getEnabled() == null || dmpp.getEnabled()) {
				dmpp.setEnabled(false);
				domainMethodProcessorProfileService.save(dmpp);
			}
		});
		profile.getUsers().forEach(user -> {
			user.setProfile(null);
			userRepository.save(user);
		});
		profile.setDeleted(true);
		profile.setCode(profile.getCode()+"_"+new Date().getTime());
		profile.setName(profile.getName()+"_"+new Date().getTime());
		profileRepository.save(profile);
		return findByDomainName(domainName);
	}
}