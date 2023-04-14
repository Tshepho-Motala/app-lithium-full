package lithium.service.cashier.client.objects;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@Builder
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude={"base64"})
@JsonIdentityInfo(generator = ObjectIdGenerators.None.class, property = "id")
public class Image {
	private Long id;
	private int version;
	private byte[] base64;
	private String filename;
	private String filetype;
	private Long filesize;
}
