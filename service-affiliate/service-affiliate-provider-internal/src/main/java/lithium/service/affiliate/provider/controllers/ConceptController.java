package lithium.service.affiliate.provider.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lithium.service.Response;
import lithium.service.Response.Status;
import lithium.service.affiliate.provider.data.entities.Concept;
import lithium.service.affiliate.provider.data.entities.ConceptType;
import lithium.service.affiliate.provider.service.ConceptService;

@RestController
@RequestMapping("/concept")
public class ConceptController {
	
	@Autowired public ConceptService conceptService;

	@RequestMapping("/add")
	public Response<Concept> addConcept(@RequestBody Concept concept) {
		return Response.<Concept>builder().data(conceptService.findOrCreate(concept)).status(Status.OK).build();
	}
	
	@RequestMapping("/edit")
	public Response<Concept> editConcept(@RequestBody Concept concept) {
		return Response.<Concept>builder().data(conceptService.edit(concept)).status(Status.OK).build();
	}
	
	@RequestMapping("/list")
	public Response<List<Concept>> listConcepts() {
		return Response.<List<Concept>>builder().data(conceptService.listConcepts()).status(Status.OK).build();
	}
	
	@RequestMapping("/type/add")
	public Response<ConceptType> addConceptType(@RequestBody ConceptType conceptType) {
		return Response.<ConceptType>builder().data(conceptService.findOrCreate(conceptType)).status(Status.OK).build();
	}
	
	@RequestMapping("/type/edit")
	public Response<ConceptType> editConceptType(@RequestBody ConceptType conceptType) {
		return Response.<ConceptType>builder().data(conceptService.edit(conceptType)).status(Status.OK).build();
	}
	
	@RequestMapping("/type/list")
	public Response<List<ConceptType>> listConceptTypes() {
		return Response.<List<ConceptType>>builder().data(conceptService.listConceptTypes()).status(Status.OK).build();
	}
}
