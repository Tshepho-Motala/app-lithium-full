package lithium.service.casino.provider.roxor.data.response.evolution;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VideoSnapshot {
    Map<String, String> links = new HashMap<>();
    Map<String, String> thumbnails = new HashMap<>();
}
