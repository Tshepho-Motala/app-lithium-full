package lithium.service.document.data.objects;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class TextValue {
    private String text;
    private Object value;
    private boolean typeSensitive;


    public TextValue(String text, Object value) {
        this.text = text;
        this.value = value;
    }

}
