package minicraft.screen;

import minicraft.gfx.Point;
import minicraft.screen.entry.ItemEntry;

class ItemListMenu extends Menu {

	static Builder getBuilder() {
		return (new Builder(true, 0, RelPos.LEFT)
			.setPositioning(new Point(9, 9), RelPos.BOTTOM_RIGHT)
			.setDisplayLength(9)
			.setSelectable(true)
			.setScrollPolicies(1, false)
		);
	}

	protected ItemListMenu(Builder builder, ItemEntry[] entries, String title) {
		super(builder
			.setEntries(entries)
			.setTitle(title)
			.createMenu()
		);
	}

	protected ItemListMenu(ItemEntry[] entries, String title) {
		this(getBuilder(), entries, title);
	}
}
