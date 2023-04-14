package lithium.service.raf.controllers;
import  lithium.util.IPUtil;
import static lithium.service.Response.Status.INTERNAL_SERVER_ERROR;
import static lithium.service.Response.Status.OK;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lithium.service.Response;
import lithium.service.raf.client.objects.ClickBasic;
import lithium.service.raf.data.entities.Click;
import lithium.service.raf.services.ClickService;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/click")
@Slf4j
public class ClickController {
	@Autowired
	ClickService service;

	@PostMapping
	public Response<Click> click(@RequestBody ClickBasic clickBasic, HttpServletRequest request) {
		Click click = null;
		if (clickBasic.getIp() == null) {
			clickBasic.setIp(IPUtil.ipFromRequest(request));
		}
		try {
			click = service.click(clickBasic.getPlayerGuid(), clickBasic.getIp(), clickBasic.getUserAgent());
			return Response.<Click>builder().data(click).status(OK).build();
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			return Response.<Click>builder().status(INTERNAL_SERVER_ERROR).build();
		}
	}
}
