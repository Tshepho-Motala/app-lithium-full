package lithium.menu;

import java.util.ArrayList;

public interface MenuChildrenGenerator {
	public ArrayList<MenuItem> generateChildren(MenuItem parent);
}
