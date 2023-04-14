package lithium.service.product.data.repositories;

import org.springframework.data.repository.PagingAndSortingRepository;

import lithium.service.product.data.entities.Product;
import lithium.service.product.data.entities.ProductGraphic;

public interface ProductGraphicRepository extends PagingAndSortingRepository<ProductGraphic, Long> {
	ProductGraphic findByProductAndGraphicFunctionIdAndEnabledTrueAndDeletedFalse(Product product, long graphicFunctionId);
}
