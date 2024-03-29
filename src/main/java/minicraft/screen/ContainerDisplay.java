package minicraft.screen;

import minicraft.core.Game;
import minicraft.core.io.InputHandler;
import minicraft.entity.ItemHolder;
import minicraft.entity.furniture.Chest;
import minicraft.entity.mob.Player;
import minicraft.graphic.Screen;
import minicraft.item.Inventory;
import minicraft.item.Item;
import minicraft.item.StackableItem;

public class ContainerDisplay extends Display {

	private static final int padding = 10;

	private Player player;
	private Chest chest;

	public ContainerDisplay(Player player, Chest chest) {
		super(
			new InventoryMenu(player, player.getInventory(), "Inventory"), 
			new InventoryMenu(chest, chest.getInventory(), chest.name)
		);

		this.player = player;
		this.chest = chest;

		menus[1].translate(menus[0].getBounds().getRight(), 0);

		if (menus[1].getNumOptions() == 0) {
			onSelectionChange(1, 0);
		}
	}

	@Override
	protected void onSelectionChange(int oldSel, int newSel) {
		super.onSelectionChange(oldSel, newSel);

		if (oldSel == newSel) {
			return; // this also serves as a protection against access to menus[0] when such may not exist.
		}

		int shift = 0;
		
		if (newSel == 0) { // Inventory
			shift = padding - menus[0].getBounds().getLeft();
		}

		if (newSel == 1) { // Chest
			shift = (Screen.w - padding) - menus[1].getBounds().getRight();
		}

		for (Menu menu : menus) {
			menu.translate(shift, 0);
		}
	}

	private int getOtherIdx() {
		return (selection + 1) % 2;
	}

	@Override
	public void tick(InputHandler input) {
		super.tick(input);

		if (input.getKey("menu").clicked || chest.isRemoved()) {
			Game.setDisplay(null);
			return;
		}

		Menu currentMenu = menus[selection];
		int otherIdx = getOtherIdx();

		if (currentMenu.getNumOptions() > 0 && (input.getKey("attack").clicked || input.getKey("drop-one").clicked)) {
			// switch inventories
			Inventory from, to;

			if (selection == 0) {
				from = player.getInventory();
				to = chest.getInventory();
			} else {
				from = chest.getInventory();
				to = player.getInventory();
			}

			int toSel = menus[otherIdx].getSelection();
			int fromSel = currentMenu.getSelection();

			Item fromItem = from.get(fromSel);

			boolean transferAll = input.getKey("attack").clicked || !(fromItem instanceof StackableItem) || ((StackableItem) fromItem).count == 1;

			Item toItem = fromItem.clone();

			if (!transferAll) {
				((StackableItem) fromItem).count--; // this is known to be valid.
				((StackableItem) toItem).count = 1;
				// items are setup for sending.
			} else { // transfer whole item/stack.
				if (!(Game.isMode("Creative") && from == player.getInventory())) {
					from.remove(fromSel); // remove it
				}
			}
			
			to.add(toSel, toItem);
			update();
		}
	}

	public void onInvUpdate(ItemHolder holder) {
		if (holder == player || holder == chest) {
			update();
		}
	}

	private void update() {
		menus[0] = new InventoryMenu((InventoryMenu) menus[0]);
		menus[1] = new InventoryMenu((InventoryMenu) menus[1]);
		menus[1].translate(menus[0].getBounds().getRight(), 0);
		onSelectionChange(0, selection);
		if (menus[1].getNumOptions() == 0) {
			onSelectionChange(1, 0);
		}
	}
}
