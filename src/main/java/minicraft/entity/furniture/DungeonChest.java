package minicraft.entity.furniture;

import org.jetbrains.annotations.Nullable;

import minicraft.core.Game;
import minicraft.core.Updater;
import minicraft.core.World;
import minicraft.core.io.Sound;
import minicraft.entity.Direction;
import minicraft.entity.Entity;
import minicraft.entity.mob.Player;
import minicraft.entity.mob.boss.AirWizard;
import minicraft.entity.particle.SmashParticle;
import minicraft.entity.particle.TextParticle;
import minicraft.gfx.Color;
import minicraft.gfx.Sprite;
import minicraft.item.Inventory;
import minicraft.item.Item;
import minicraft.item.Items;
import minicraft.item.StackableItem;

public class DungeonChest extends Chest {
    private static final Sprite openSprite = new Sprite(4, 30, 2, 2, 2);
    private static final Sprite lockSprite = new Sprite(2, 30, 2, 2, 2);

    private boolean isLocked;
    private int tickTime = 0;

    /**
     * Creates a custom chest with the name Dungeon Chest.
     * 
     * @param populateInv
     */
    public DungeonChest(boolean populateInv) {
        this(populateInv, false);
    }

    public DungeonChest(boolean populateInv, boolean unlocked) {
        super("Dungeon Chest");
        if (populateInv) {
            populateInv();
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
		if (tickTime / 8 % 8 == 0 && random.nextInt(8) == 4) {
			if (random.nextBoolean()) {
				if (!random.nextBoolean()) {
					Sound.dungeonChest1.playOnWorld(x, y);
				} else {
					Sound.dungeonChest2.playOnWorld(x, y);
				}
			} else {
				Sound.dungeonChest3.playOnWorld(x, y);
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
                this.sprite = openSprite; // set to the unlocked color

                level.add(new SmashParticle(x << 4, y << 4));
                level.add(new TextParticle("-1 key", x, y, Color.RED));
                level.chestCount--;
                if (level.chestCount == 0) { // if this was the last chest...
                	player.isRaining = true; // Starts to rain
                	Sound.rainThunder2.playOnGui();
                	
                    level.dropItem(x, y, 5, Items.get("Gold Apple"));

                    Updater.notifyAll("You hear a noise from the surface!", -100); // notify the player of the developments
                    // add a level 2 airwizard to the middle surface level.
                    AirWizard wizard = new AirWizard(true);
                    wizard.x = World.levels[World.lvlIdx(0)].w / 2;
                    wizard.y = World.levels[World.lvlIdx(0)].h / 2;
                    World.levels[World.lvlIdx(0)].add(wizard);
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
    private void populateInv() {
        Inventory inv = getInventory(); // Yes, I'm that lazy. ;P
        inv.clearInv(); // clear the inventory.

        populateInvRandom("dungeonchest", 0);
    }

    public boolean isLocked() {
        return isLocked;
    }

    public void setLocked(boolean locked) {
        this.isLocked = locked;

        // auto update sprite
        sprite = locked ? DungeonChest.lockSprite : DungeonChest.openSprite;
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