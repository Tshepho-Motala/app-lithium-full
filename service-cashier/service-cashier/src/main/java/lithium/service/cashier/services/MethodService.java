package lithium.service.cashier.services;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import lithium.service.cashier.data.entities.Method;
import lithium.service.cashier.data.repositories.MethodRepository;

@Service
public class MethodService {
	@Autowired
	private MethodRepository methodRepository;
	
	public Method findByCode(String code) {
		return methodRepository.findByCode(code);
	}
	public Method findOne(Long methodId) {
		return methodRepository.findOne(methodId);
	}
	public List<Method> findAll() {
		Iterable<Method> methods = methodRepository.findAll();
		List<Method> methodList = new ArrayList<>();
		methods.forEach(methodList::add);
		return methodList;
	}
	
//	public Method save(String name, String code, String url, Image image, Boolean enabled) {
//		return save(
//			Method.builder()
//			.code(code)
//			.name(name)
//			.image(image)
//			.enabled(enabled)
//			.build()
//		);
//	}
	
	public Method save(Method method) {
		return methodRepository.save(method);
	}
}
