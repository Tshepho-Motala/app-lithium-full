package lithium.service.casino.cms.services;

import lithium.service.casino.cms.exceptions.Status404BannerNotFound;
import lithium.service.casino.cms.storage.entities.Banner;
import lithium.service.casino.cms.storage.entities.BannerSchedule;
import lithium.service.casino.cms.storage.entities.Domain;
import lithium.service.casino.cms.storage.repositories.BannerRepository;
import lithium.service.domain.client.exceptions.Status550ServiceDomainClientException;
import lombok.extern.slf4j.Slf4j;
import org.dmfs.rfc5545.DateTime;
import org.dmfs.rfc5545.recur.InvalidRecurrenceRuleException;
import org.dmfs.rfc5545.recur.RecurrenceRule;
import org.dmfs.rfc5545.recur.RecurrenceRuleIterator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class BannerService {

    @Autowired
    private BannerRepository bannerRepository;

    @Autowired
    private BannerScheduleService bannerScheduleService;

    @Autowired
    private DomainService domainService;

    public Banner retrieveBanner(Long id) throws Status404BannerNotFound {
        Optional<Banner> banner = bannerRepository.findById(id);
        if (!banner.isPresent()) {
            throw new Status404BannerNotFound("Banner not found: id:" + id);
        }
        return banner.get();

    }

    @Transactional
    public Banner updateBanner(String domainName,Banner banner) throws Status404BannerNotFound, InvalidRecurrenceRuleException {
        Optional<Banner> oldBanner = bannerRepository.findById(banner.getId());
        if (!oldBanner.isPresent()) {
            log.error("Unable to update banner, Banner not found: id: " + banner.getId());
            throw new Status404BannerNotFound("Banner not found: id: " + banner.getId());
        }
        Domain domain = domainService.findOrCreate(domainName);
        banner.setDomain(domain);
        if(!banner.getRecurrencePattern().equals(oldBanner.get().getRecurrencePattern()))
        {
            updateBannerSchedules(banner);
        }

        return bannerRepository.save(banner);
    }

    private void updateBannerSchedules(Banner banner) throws InvalidRecurrenceRuleException {
        deletedOldSchedules(banner.getId());
        addBannerSchedules(banner);
        banner.getBannerSchedules().forEach(bannerSchedule -> bannerSchedule.setBanner(banner));
    }

    private void deletedOldSchedules(Long bannerId) {
        bannerScheduleService.deleteOldOpenByBannerId(bannerId);
    }

    @Transactional
    public void disableBanner(String domainName, Long bannerId) throws Status404BannerNotFound {
        Optional<Banner> bannerOptional = bannerRepository.findById(bannerId);
        if(!bannerOptional.isPresent()) {
            log.error("Unable to update banner, Banner not found: id: " + bannerId);
            throw new Status404BannerNotFound("Unable not delete, banner not found: id:" + bannerId);
        }
        bannerOptional.get().setDeleted(true);
        bannerRepository.save(bannerOptional.get());
    }


    @Transactional
    public Banner createBanner(String domainName, Banner banner) throws InvalidRecurrenceRuleException {
        Domain domain = domainService.findOrCreate(domainName);
        banner.setDomain(domain);
        banner.setDeleted(false);
        banner = bannerRepository.save(banner);
        updateBannerSchedules(banner);
        return banner;
    }

    private static void addBannerSchedules(Banner banner) throws InvalidRecurrenceRuleException {
        int indexOfRRule = banner.getRecurrencePattern().indexOf("RRULE:") + 6;
        String substring = banner.getRecurrencePattern().substring(indexOfRRule);

        RecurrenceRule rrule = new RecurrenceRule(substring);
        DateTime startDate = new DateTime(banner.getStartDate().getTime());
        RecurrenceRuleIterator rRuleIterator = rrule.iterator(startDate);
        rRuleIterator.fastForward(DateTime.now().startOfDay());

        for (int i = 0; i < 2 && rRuleIterator.hasNext(); i++) {
            DateTime nextDateTime = rRuleIterator.nextDateTime();
            Date nextDate = new Date(nextDateTime.startOfDay().getTimestamp());
            BannerSchedule bannerSchedule = BannerSchedule.create(banner, nextDate);
            banner.getBannerSchedules().add(bannerSchedule);
        }
    }

    public List<Banner> retrieveByDomain(String domainName) throws Status550ServiceDomainClientException {
        List<Banner> banners = bannerRepository.findAllByDomainNameAndDeletedFalse(domainName);
        return banners;
    }

    public Page<Banner> getBannersWithExpiredSchedules(PageRequest pageRequest) {
        return bannerRepository.findAllByBannerSchedulesClosedFalseAndBannerSchedulesEndDateAfter(new Date(), pageRequest);
    }
}
