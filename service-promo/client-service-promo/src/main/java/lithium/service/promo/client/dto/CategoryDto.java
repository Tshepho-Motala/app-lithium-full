package lithium.service.promo.client.dto;

/**
 * @author Rivalani
 * Created this to help deserialize from ICategory transfered over the wire
 */
public class CategoryDto implements ICategory{

    private String category;

    public CategoryDto(String category) {
        this.category = category;
    }

    @Override
    public String getCategory() {
        return category;
    }
}
