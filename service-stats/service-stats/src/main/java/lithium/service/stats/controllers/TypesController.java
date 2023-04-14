package lithium.service.stats.controllers;

import lithium.service.Response;
import lithium.service.stats.data.entities.Type;
import lithium.service.stats.data.repositories.TypeRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/types")
@RestController
@Slf4j
public class TypesController {
	@Autowired private TypeRepository repository;

	@GetMapping
	public Response<Iterable<Type>> findAll() {
		return Response.<Iterable<Type>>builder().data(repository.findAll()).status(Response.Status.OK).build();
	}
}
