package lithium.menu;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.StringTokenizer;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Data
@Slf4j
@Builder
@NoArgsConstructor
@AllArgsConstructor
@RequiredArgsConstructor
public class MenuItem implements Comparable<MenuItem> {
	
	@NonNull
	private String nameKey;
	@NonNull
	private String nameDefault;
	@NonNull
	private String location = "";
	@NonNull
	private String icon;
	@NonNull
	private String[] roles;
	
	private int order;
	
	private boolean active = false;
	
	private MenuChildrenGenerator childrenGenerator = null;
	
	private List<MenuItem> children = new ArrayList<MenuItem>();
	
	private Object[] nameKeyArgs;

	public MenuItem(String nameKey, String nameDefault, Object[] nameKeyArgs, String location, String icon, String[] roles, int order) {
		this.nameKey = nameKey;
		this.nameDefault = nameDefault;
		this.nameKeyArgs = nameKeyArgs;
		this.location = location;
		this.icon = icon;
		this.roles = roles;
		this.order = order;
	}
	
	public MenuItem(String nameKey, String nameDefault, Object[] nameKeyArgs, String location, String icon, String roles, int order) {
		this(nameKey, nameDefault, nameKeyArgs, location, icon, new String[] {}, order);
		if (roles == null) return;
		StringTokenizer st = new StringTokenizer(roles, " \t\n\r\f,");
		ArrayList<String> newRoles = new ArrayList<String>();
		while (st.hasMoreTokens()) {
			String role = st.nextToken();
			newRoles.add(role);
		}
		this.roles = newRoles.toArray(new String[newRoles.size()]);
	}
	
	public MenuItem(String nameKey, String nameDefault, String location, String icon, String roles, int order) {
		this(nameKey, nameDefault, new Object[] {}, location, icon, roles, order);
	}

	public MenuItem(String nameKey, String nameDefault, String location, String icon, String[] roles, int order) {
		this(nameKey, nameDefault, new Object[] {}, location, icon, roles, order);
	}	

	public MenuItem(MenuItem copy) {
		copy.generateChildren();
		if (copy.nameKey != null) this.nameKey = new String(copy.nameKey);
		if (copy.nameDefault != null) this.nameDefault = new String(copy.nameDefault);
		if (copy.nameKeyArgs != null) this.nameKeyArgs = copy.nameKeyArgs;
		if (copy.location != null) this.location = new String(copy.location);
		if (copy.icon != null) this.icon = new String(copy.icon);	
		if (copy.roles != null) this.roles = copy.roles.clone();
		this.order = copy.order;
		for (MenuItem child: copy.children) {
			children.add(new MenuItem(child));
		}
	}
	
	public void addRoles(String[] newRoles) {
		ArrayList<String> newArray = new ArrayList<String>();
		for (String newRole: newRoles) {
			boolean found = false;
			for (String role: roles) {
				if (role.equals(newRole)) {
					found = true;
					break;
				}
			}
			if (!found) newArray.add(newRole);
		}
		if (!newArray.isEmpty()) {
			for (String role: roles) newArray.add(role);
			roles = newArray.toArray(new String[newArray.size()]);
		}
	}

	public void setChildren(List<MenuItem> list) {
		children = list;
		Collections.sort(children);
	}
	
	public int compareTo(MenuItem o) {
		if (order > o.getOrder()) return 1; else
		if (order < o.getOrder()) return -1; else
		return 0;
	}
	
	public void setActiveLocation(String location) {
		if (this.location.equals("/")) {
			active = location.equals("/");
		} else {
			if (location.startsWith(this.location)) active = true;
			for (MenuItem child: children) {
				child.setActiveLocation(location);
			}
		}
	}
	
	public boolean isAccessible(List<String> testRoles) {
		if (this.roles == null) return true;
		if (this.roles.length == 0) return true;
				
		for (String role: roles) {
			for (String testRole: testRoles) {
				if (role.equals(testRole)) return true;
			}
		}
		return false;
	}
	
	public void removeInaccessibleItems(List<String> roles) {
		List<MenuItem> copy = new ArrayList<MenuItem>(children);
		for (MenuItem child: copy) {
			if (!child.isAccessible(roles)) {
				children.remove(child); 
			} else {
				child.removeInaccessibleItems(roles);
			}
		}
	}
	
	public MenuItem addChild(MenuItem item) {
		if (children == null) children = new ArrayList<MenuItem>();
		children.add(item);
		Collections.sort(children);
		return this;
	}
	
	public String rolesToString() {
		String result = "";
		if (roles == null) return result;
		for (String role: roles) {
			if (result != "") result += ", ";
			result += role;
		}
		return result;
	}
	
	public MenuItem mergeChildren(List<MenuItem> items) {
		for (MenuItem item: items) {
			log.info("Checking if there is already a " + item.nameKey + " menu in the children of " + nameKey);
			boolean found = false;
			for (MenuItem child: children) {
				if (child.getNameKey().equals(item.getNameKey())) {
					log.info("Menu " + nameKey + " already contains a child named " + item.nameKey);
					found = true;
					child.addRoles(item.getRoles());
					child.mergeChildren(item.getChildren());
				}
			}
			if (!found) {
				log.info("Adding the new menu " + item.nameKey + " to " + nameKey);
				addChild(item);
			}
		}
		return this;
	}
	
	public void printMenu(String prefix) {
		prefix = prefix + "/" + nameKey + "(" + rolesToString() + ")"; 
		log.info(prefix);
		for (MenuItem child: children) {
			child.printMenu(prefix);
		}
	}
	
	public MenuItem generateChildren(MenuChildrenGenerator generator) {
		this.childrenGenerator = generator;
		return this;
	}
	
	public void generateChildren() {
		if (childrenGenerator != null) {
			children = childrenGenerator.generateChildren(this);
		}
	}
}
