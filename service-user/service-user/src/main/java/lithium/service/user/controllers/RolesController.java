package lithium.service.user.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lithium.service.Response;
import lithium.service.Response.Status;
import lithium.service.user.data.entities.Category;
import lithium.service.user.data.entities.Role;
import lithium.service.user.data.repositories.CategoryRepository;
import lithium.service.user.data.repositories.RoleRepository;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/roles")
public class RolesController {
	@Autowired
	private RoleRepository roleRepository;
	@Autowired
	private CategoryRepository categoryRepository;
	
	@GetMapping("/all")
	public Response<Iterable<Role>> list() {
		log.debug("Listing all Roles");
		Iterable<Role> all = roleRepository.findAll(Sort.by(new Sort.Order(Direction.ASC, "category"))); 
		return Response.<Iterable<Role>>builder().data(all).status(Status.OK).build();
	}
	
	@PostMapping("/addcategory")
	public Response<Category> addCategory(
		@RequestParam("name") String name,
		@RequestParam("description") String description
	) throws InterruptedException {
//		Thread.sleep((long)(Math.random() * 1000));
		Category category = categoryRepository.findByName(name);
		log.debug("Category : "+category);
		if (category == null) {
			log.info("Creating new category : "+name);
			category = categoryRepository.save(Category.builder().name(name).description(description).build());
		}
		return Response.<Category>builder().data(category).status(Status.OK).build();
	}
	
	@PostMapping("/addrole")
	public Response<Role> addRole(
		@RequestParam("name") String name,
		@RequestParam("role") String roleString,
		@RequestParam("description") String description,
		@RequestParam("categoryName") String categoryName
	) throws InterruptedException {
//		Thread.sleep((long)(Math.random() * 2000));
		Category category = categoryRepository.findByName(categoryName);
		Role role = roleRepository.findByRole(roleString);
		log.debug("Role : "+role);
		if (role == null) {
			log.info("Creating new role : "+roleString);
			role = roleRepository.save(Role.builder().name(name).role(roleString).description(description).category(category).build());
		}
		return Response.<Role>builder().data(role).status(Status.OK).build();
	}
}
