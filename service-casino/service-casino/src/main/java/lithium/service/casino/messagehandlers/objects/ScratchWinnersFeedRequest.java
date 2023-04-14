package lithium.service.casino.messagehandlers.objects;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@JsonIgnoreProperties(ignoreUnknown = true)
@Data
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ScratchWinnersFeedRequest implements Serializable {
	private static final long serialVersionUID = 1L;
	private String domain;
	private String room;
}