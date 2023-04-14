package lithium.service.mail.client;

import lithium.service.Response;
import lithium.service.client.datatable.DataTableResponse;
import lithium.service.mail.client.objects.Email;
import lithium.service.mail.client.objects.SystemEmailData;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "service-mail", path="/mail/system")
public interface SystemMailClient {

    @RequestMapping(path = "/send")
    public Response<Email> save(@RequestBody SystemEmailData systemMail) throws Exception;

    @RequestMapping(method = RequestMethod.POST, path = "/find")
    public DataTableResponse<Email> find(@RequestParam("guid") String guid, @RequestParam("page") int page, @RequestParam("size") int size);
}
