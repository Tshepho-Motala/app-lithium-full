package lithium.service.cashier.data.objects;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class DomainMethodOrder {
    List<ShortDomainMethod> elements = new ArrayList<>();

    @Data
    @Builder(toBuilder = true)
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ShortDomainMethod {
        private String name;
        private Long id;
        private Integer priority;
        private boolean enabled;
        private Boolean feDefault;

        @Override
        public String toString() {
          return " " + name + " (" + id +
              (enabled ? "" : ", off") +
              (feDefault != null && feDefault ? ", default" : "") +
              ") ";
        }
    }
}
