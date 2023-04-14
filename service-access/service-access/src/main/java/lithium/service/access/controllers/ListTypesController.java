package lithium.service.access.controllers;

import static lithium.service.Response.Status.OK;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lithium.service.Response;
import lithium.service.access.data.entities.ListType;
import lithium.service.access.data.repositories.ListTypeRepository;

@RestController
@RequestMapping("/listTypes")
public class ListTypesController {
	@Autowired ListTypeRepository listTypeRepository;
	
	@GetMapping("/find/all")
	public Response<Iterable<ListType>> findAll() {
		return Response.<Iterable<ListType>>builder().data(listTypeRepository.findAll()).status(OK).build();
	}
}