package lithium.service.affiliate.provider.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lithium.service.Response;
import lithium.service.Response.Status;
import lithium.service.affiliate.provider.data.entities.Brand;
import lithium.service.affiliate.provider.data.repositories.BrandRepository;

@RestController
@RequestMapping("/brands")
public class BrandsController {

	@Autowired BrandRepository brandRepository;
	
	@RequestMapping
	public Response<Iterable<Brand>> findBrands() {
		return Response.<Iterable<Brand>>builder().data(brandRepository.findAll()).status(Status.OK).build();
	}
	
}
