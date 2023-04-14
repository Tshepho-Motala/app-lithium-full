package lithium.service.user.client.objects;

import java.util.ArrayList;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GrdCategorized {
	private List<Domain> domains;
	public void addDomain(Domain domain) {
		if (domains == null) domains = new ArrayList<>();
		domains.add(domain);
	}
	
	@Data
	@Builder
	@NoArgsConstructor
	@AllArgsConstructor
	public static class Domain {
		private Long id;
		private String name;
		private List<Category> categories;
		public void addCategory(Category category) {
			if (categories == null) categories = new ArrayList<>();
			categories.add(category);
		}
		public Category findCategory(Long id) {
			return categories.stream().filter(c -> c.getId() == id).findAny().orElse(null);
		}
	}
	
	@Data
	@Builder
	@NoArgsConstructor
	@AllArgsConstructor
	public static class Category {
		private Long id;
		private String name;
		private String description;
		private List<GRD> grds;
		public void addGRD(GRD grd) {
			if (grds == null) grds = new ArrayList<>();
			grds.add(grd);
		}
		public void addAllGRDs(List<GRD> all) {
			if (grds == null) grds = new ArrayList<>();
			grds.addAll(all);
		}
		public GRD findGRD(Long id) {
			return grds.stream().filter(g -> g.getId() == id).findAny().orElse(null);
		}
		public String getNameKey() {
			return ("GLOBAL.CAT."+id+".NAME").toUpperCase();
		}
		public String getDescriptionKey() {
			return ("GLOBAL.CAT."+id+".DESCR").toUpperCase();
		}
	}
	
	@Data
	@Builder
	@NoArgsConstructor
	@AllArgsConstructor
	public static class GRD {
		private Long id;
		private Long categoryId;
		private Boolean selfApplied; // Is this role applicable to the current domain ?
		private Boolean descending; // Is this role applicable to the children for this domain ?
		private String name;
		private String role;
		private String description;
//		private Boolean fromParent;
		public String getNameKey() {
			return ("GLOBAL.ROLE."+role+".NAME").toUpperCase();
		}
		public String getDescriptionKey() {
			return ("GLOBAL.ROLE."+role+".DESCR").toUpperCase();
		}
	}
}
