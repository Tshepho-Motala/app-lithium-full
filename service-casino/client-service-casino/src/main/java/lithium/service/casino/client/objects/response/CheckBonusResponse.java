package lithium.service.casino.client.objects.response;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlElement;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@ToString(callSuper=true)
@EqualsAndHashCode(callSuper = false)
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CheckBonusResponse extends Response implements Serializable {
	private static final long serialVersionUID = 1L;
	@XmlElement(name="BONUSID")
	private Integer bonusId;
}