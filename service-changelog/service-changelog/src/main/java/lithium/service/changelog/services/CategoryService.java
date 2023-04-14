package lithium.service.changelog.services;

import lithium.client.changelog.Category;
import lithium.client.changelog.SubCategory;
import lithium.service.changelog.data.repositories.CategoryRepository;
import lithium.service.changelog.data.repositories.SubCategoryRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class CategoryService {

    @Autowired CategoryRepository categoryRepository;
    @Autowired SubCategoryRepository subCategoryRepository;

    /**
     * Called on service startup to setup the categories and sub-categories
     */
    public void setupFromEnums() {
        List<lithium.service.changelog.data.entities.Category> categories = Arrays.stream(Category.values())
                .map(category -> findOrCreateCategory(category.getName())
        ).collect(Collectors.toList());

        Arrays.stream(SubCategory.values())
                .map(subCategory -> subCategoryRepository.findOrCreateByName(subCategory.getName(),
                    () -> lithium.service.changelog.data.entities.SubCategory.builder()
                            .category(getCategoryFromSubCategory(categories, subCategory.getCategory().getName()))
                            .build()))
                .collect(Collectors.toList());
    }

    private lithium.service.changelog.data.entities.Category getCategoryFromSubCategory(List<lithium.service.changelog.data.entities.Category> categories, String categoryName) {
        return categories.stream()
                .filter(category -> category.getName().equals(categoryName))
                .findFirst()
                .get();
    }

    @Retryable(maxAttempts = 10, backoff = @Backoff(delay = 10, maxDelay = 100, random = true))
    public lithium.service.changelog.data.entities.Category findOrCreateCategory(String categoryName){
       return categoryRepository.findOrCreateByName(categoryName,
            () -> lithium.service.changelog.data.entities.Category.builder()
                .name(categoryName)
                .build());
    }

    @Retryable(maxAttempts = 10, backoff = @Backoff(delay = 10, maxDelay = 100, random = true))
    public lithium.service.changelog.data.entities.SubCategory findOrCreateSubCategory(String subCategoryName, lithium.service.changelog.data.entities.Category category){
        return subCategoryRepository.findOrCreateByName(subCategoryName,
            () -> lithium.service.changelog.data.entities.SubCategory.builder()
                .category(category)
                .build());
    }

    @Retryable(maxAttempts = 10, backoff = @Backoff(delay = 10, maxDelay = 100, random = true))
    public lithium.service.changelog.data.entities.Category findCategoryByName(String categoryName){
       return categoryRepository.findByName(categoryName);
    }

    @Retryable(maxAttempts = 10, backoff = @Backoff(delay = 10, maxDelay = 100, random = true))
    public lithium.service.changelog.data.entities.SubCategory findSubCategoryByName(String subCategoryName){
        return subCategoryRepository.findByName(subCategoryName);
    }


}
