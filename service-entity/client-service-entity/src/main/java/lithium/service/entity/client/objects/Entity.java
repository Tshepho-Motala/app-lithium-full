package lithium.service.entity.client.objects;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@Builder
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class Entity {
	private Long id;
	private String uuid;
	private Status status;
	private String name;
	private String description;
	private String email;
	private String telephoneNumber;
	private String cellphoneNumber;
	private Domain domain;
	private Address physicalAddress;
	private Address billingAddress;
	private Date createdDate;
	private Date updatedDate;
	private EntityType entityType;
	private BankDetails bankDetails;
}
