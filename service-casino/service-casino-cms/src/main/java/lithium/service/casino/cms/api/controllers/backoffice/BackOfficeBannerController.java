package lithium.service.casino.cms.api.controllers.backoffice;

import lithium.service.Response;
import lithium.service.casino.cms.exceptions.Status404BannerNotFound;
import lithium.service.casino.cms.services.BannerService;
import lithium.service.casino.cms.storage.entities.Banner;
import lithium.service.casino.exceptions.Status422InvalidParameterProvidedException;
import lithium.service.client.datatable.DataTableResponse;
import lithium.service.domain.client.exceptions.Status550ServiceDomainClientException;
import lombok.extern.slf4j.Slf4j;
import org.dmfs.rfc5545.recur.InvalidRecurrenceRuleException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RequestMapping("/backoffice/{domainName}/banners")
@RestController
public class BackOfficeBannerController {

    @Autowired
    private BannerService bannerService;

    @PostMapping("{id}/get")
    public Response<Banner> getBanner(@PathVariable String domainName, @PathVariable Long id) throws Status422InvalidParameterProvidedException, Status404BannerNotFound {
        Banner banner = bannerService.retrieveBanner(id);
        return Response.<Banner>builder().data(banner).status(Response.Status.OK).build();

    }

    @PostMapping("/create")
    public Response<Banner> createBanner(@PathVariable String domainName, @RequestBody Banner banner) throws Status422InvalidParameterProvidedException, Status404BannerNotFound, InvalidRecurrenceRuleException {
        banner = bannerService.createBanner(domainName, banner);
        return Response.<Banner>builder().data(banner).status(Response.Status.OK).build();
    }

    @PostMapping("{id}/update")
    public Response<Banner> createBanner(@PathVariable String domainName, @PathVariable Long id, @RequestBody Banner banner) throws Status422InvalidParameterProvidedException, Status404BannerNotFound, InvalidRecurrenceRuleException {
        if (!banner.getId().equals(id)) {
            throw new Status422InvalidParameterProvidedException("It does not match");
        }
        banner = bannerService.updateBanner(domainName, banner);
        return Response.<Banner>builder().data(banner).status(Response.Status.OK).build();
    }

    @PostMapping("{id}/remove")
    public Response<Banner> removeBanner(@PathVariable String domainName, @PathVariable Long id) throws Status422InvalidParameterProvidedException, Status404BannerNotFound {
        bannerService.disableBanner(domainName, id);
        return Response.<Banner>builder().status(Response.Status.OK).build();
    }

    @PostMapping("find-all")
    public Response<List<Banner>> getBannersByDomain(@PathVariable String domainName) throws Status550ServiceDomainClientException {
        List<Banner> bannerList = bannerService.retrieveByDomain(domainName);
        return Response.<List<Banner>>builder().data(bannerList).status(Response.Status.OK).build();
    }

}
