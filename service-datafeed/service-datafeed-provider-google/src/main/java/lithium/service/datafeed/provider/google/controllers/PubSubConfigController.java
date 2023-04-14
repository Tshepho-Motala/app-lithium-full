package lithium.service.datafeed.provider.google.controllers;


import lithium.metrics.TimeThisMethod;
import lithium.service.datafeed.provider.google.config.PubSubConfigService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/system/isPubSubActivated")
public class PubSubConfigController {

    @Autowired
    private PubSubConfigService configService;

    @TimeThisMethod
    @PostMapping()
    public boolean isChannelActivated(
            @RequestParam("domainName") String domainName,
            @RequestParam("channelName") String channelName){
        return configService.isChannelActivated(domainName,channelName);
    }
}
