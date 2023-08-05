package minicraft.entity.furniture;

import org.jetbrains.annotations.Nullable;

import minicraft.core.Game;
import minicraft.core.Updater;
import minicraft.core.World;
import minicraft.core.io.Sound;
import minicraft.entity.Direction;
import minicraft.entity.Entity;
import minicraft.entity.mob.AirWizard;
import minicraft.entity.mob.Player;
import minicraft.entity.particle.SmashParticle;
import minicraft.entity.particle.TextParticle;
import minicraft.graphic.Color;
import minicraft.graphic.Sprite;
import minicraft.item.Inventory;
import minicraft.item.Item;
import minicraft.item.Items;
import minicraft.item.StackableItem;

public class DungeonChest extends Chest {
    private static final Sprite OPEN_SPRITE = new Sprite(4, 30, 2, 2, 2);
    private static final Sprite LOCK_SPRITE = new Sprite(2, 30, 2, 2, 2);

    private boolean isLocked;
    private int tickTime = 0;

    /**
     * Creates a custom chest with the name Dungeon Chest.
     * 
     * @param fillInventory
     */
    public DungeonChest(boolean fillInventory) {
        this(fillInventory, false);
    }

    public DungeonChest(boolean fillInventory, boolean unlocked) {
        super("Dungeon Chest");
        if (fillInventory) {
            fillInventory();
        }

        setLocked(!unlocked);
    }

    @Override
    public Furniture clone() {
        return new DungeonChest(false, !this.isLocked);
    }
    
	@Override
	public void tick() {
		super.tick();
		tickTime++;
		
		// Dungeon chest proximity sound
		if (isLocked && tickTime / 2 % 16 == 0) {
			if (random.nextBoolean()) {
				if (!random.nextBoolean()) {
					Sound.dungeonChest1.playOnLevel(this.x, this.y);
				} else {
					Sound.dungeonChest2.playOnLevel(this.x, this.y);
				}
			} else {
				Sound.dungeonChest3.playOnLevel(this.x, this.y);
			}
		}
	}

    @Override
    public boolean use(Player player) {
        if (isLocked) {
            boolean activeKey = player.activeItem != null && player.activeItem.equals(Items.get("Key"));
            boolean invKey = player.getInventory().count(Items.get("Key")) > 0;
            if (activeKey || invKey) { // if the player has a key...
                if (!Game.isMode("Creative")) { // remove the key unless on creative mode.
                    if (activeKey) { // remove activeItem
                        StackableItem key = (StackableItem) player.activeItem;
                        key.count--;
                    } else { // remove from inv
                        player.getInventory().removeItem(Items.get("Key"));
                    }
                }

                isLocked = false;
                this.sprite = OPEN_SPRITE; // set to the unlocked color

                level.add(new SmashParticle(x << 4, y << 4));
                level.add(new TextParticle("-1 key", x, y, Color.RED));
                level.chestCount--;
                if (level.chestCount == 0) { // if this was the last chest...
                	Sound.rainThunder2.playOnDisplay();
                	
                    level.dropItem(x, y, 5, Items.get("Gold Apple"));

                    Updater.notifyAll("You hear a noise from the surface!", -100); // notify the player of the developments
                    // add a level 2 airwizard to the middle surface level.
                    AirWizard wizard = new AirWizard(true);
                    wizard.x = World.levels[World.levelIndex(0)].w / 2;
                    wizard.y = World.levels[World.levelIndex(0)].h / 2;
                    World.levels[World.levelIndex(0)].add(wizard);
                }

                return super.use(player); // the player unlocked the chest.
            }
            return false; // the chest is locked, and the player has no key.
        } else {
            return super.use(player); // the chest was already unlocked.
        }
    }

    /**
     * Populate the inventory of the DungeonChest using the loot table system
     */
    private void fillInventory() {
        Inventory dungeonChestInventory = getInventory(); // Yes, I'm that lazy. ;P
        dungeonChestInventory.clear(); // clear the inventory.

        fillInventoryRandom("dungeonchest", 0);
    }

    public boolean isLocked() {
        return isLocked;
    }

    public void setLocked(boolean locked) {
        this.isLocked = locked;

        // auto update sprite
        sprite = locked ? DungeonChest.LOCK_SPRITE : DungeonChest.OPEN_SPRITE;
    }

    /** what happens if the player tries to push a Dungeon Chest. */
    @Override
    protected void touchedBy(Entity entity) {
        if (!isLocked) { // can only be pushed if unlocked. 
            super.touchedBy(entity);
        }
    }

    @Override
    public boolean interact(Player player, @Nullable Item item, Direction attackDir) {
        if (!isLocked) {
            return super.interact(player, item, attackDir);
        }
        return false;
    }
}