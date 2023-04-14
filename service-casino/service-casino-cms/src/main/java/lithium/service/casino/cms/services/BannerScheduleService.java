package lithium.service.casino.cms.services;

import lithium.service.casino.cms.storage.entities.BannerSchedule;
import lithium.service.casino.cms.storage.repositories.BannerScheduleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class BannerScheduleService {

    @Autowired
    private BannerScheduleRepository repository;


    public Page<BannerSchedule> getExpired(PageRequest pageRequest) {
        return repository.findAllExpired(pageRequest);
    }

    public List<BannerSchedule> saveAll(List<BannerSchedule> list) {
        Iterable<BannerSchedule> bannerSchedulesIterable = repository.saveAll(list);
        List<BannerSchedule> bannerSchedules = new ArrayList<>();
        bannerSchedulesIterable.forEach(bannerSchedules::add);
        return bannerSchedules;
    }

    public List<BannerSchedule> getByBannerId(Long bannerId) {
        return repository.findAllByBannerIdAndClosedFalseAndDeletedFalse(bannerId);
    }

    public void deleteOldOpenByBannerId(Long bannerId) {
        List<BannerSchedule> bannerSchedules = getByBannerId(bannerId);
        bannerSchedules.stream().forEach(bannerSchedule -> bannerSchedule.setDeleted(true));
        saveAll(bannerSchedules);
    }
}
