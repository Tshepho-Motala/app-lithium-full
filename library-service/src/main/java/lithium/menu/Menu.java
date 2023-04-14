package lithium.menu;

import java.util.List;

public class Menu extends MenuItem {
	
	public Menu() {
	}
	
	public Menu(Menu copy) {
		super(copy);
	}

	public void setMenuItems(List<MenuItem> list) {
		super.setChildren(list);
	}
	
	public List<MenuItem> getMenuItems() {
		return super.getChildren();
	}
}
