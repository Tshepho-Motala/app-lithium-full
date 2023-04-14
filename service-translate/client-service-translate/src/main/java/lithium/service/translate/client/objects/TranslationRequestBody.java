package lithium.service.translate.client.objects;

import java.io.Serializable;
import java.util.Locale;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TranslationRequestBody implements Serializable{
	private static final long serialVersionUID = 1L;
	Locale locale;
	String code;
}
