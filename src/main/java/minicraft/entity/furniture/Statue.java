package minicraft.entity.furniture;

import minicraft.core.Game;
import minicraft.core.Updater;
import minicraft.core.io.Sound;
import minicraft.entity.Direction;
import minicraft.entity.mob.Player;
import minicraft.entity.particle.TextParticle;
import minicraft.graphic.Color;
import minicraft.graphic.Screen;
import minicraft.graphic.Sprite;
import minicraft.item.*;

public class Statue extends Furniture {

	private static final Sprite NORMAL_SPRITE = new Sprite(8, 32, 3, 3, 2);
    private static final Sprite MEDIUM_BROKEN_SPRITE = new Sprite(11, 32, 3, 3, 2);
    private static final Sprite FULL_BROKEN_SPRITE = new Sprite(14, 32, 3, 3, 2);

	private int health;

	public Statue() {
		super("Eye Statue", NORMAL_SPRITE, 9, 3);
		health = 150;
	}

	@Override
	public boolean use(Player player) {
        return Game.isMode("Creative");
    }

	@Override
	public boolean interact(Player player, Item item, Direction attackDir) {
		if (item instanceof ToolItem) {
			ToolItem tool = (ToolItem) item;

			Sound.playAt("genericHurt", this.x, this.y);

			int toolDamage;
			if (Game.isMode("Creative")) {
				toolDamage = health;
			} else {
				toolDamage = tool.level + random.nextInt(2);

				if (tool.type == ToolType.Pickaxe) {
					toolDamage += random.nextInt(5) + 2;
				}

				if (player.potionEffects.containsKey(PotionType.Haste)) {
					toolDamage *= 2;
				}
			}

			health -= toolDamage;
			level.add(new TextParticle("" + toolDamage, x, y + 2, Color.get(-1, 200, 300, 400)));
			if (health <= 0) {
				level.remove(this);

                // Random spawner sound
				switch (random.nextInt(3) + 1) {
					case 1: Sound.playAt("spawnerDestroy1", this.x, this.y); break;
					case 2: Sound.playAt("spawnerDestroy2", this.x, this.y); break;
				    case 3: Sound.playAt("spawnerDestroy3", this.x, this.y); break;
				    default: Sound.playAt("genericHurt", this.x, this.y); break;
				}

				player.addScore(600);
            }

            return true;
        }

        if (item instanceof PowerGloveItem && Game.isMode("Creative")) {
        	level.remove(this);
        	if (!(player.activeItem instanceof PowerGloveItem)) {
        		player.getInventory().add(0, player.activeItem);
        	}
        	player.activeItem = new FurnitureItem(this);
        	return true;
        }

        if (item == null) {
        	return use(player);
        }

        return false;
    }

	@Override
	public void tryPush(Player player) {
        // We do nothing here ...
    }

	@Override
    public void render(Screen screen) {
		if (health <= 50) {
			FULL_BROKEN_SPRITE.render(screen, x - 12, y - 16);
		} else if (health <= 100) {
			MEDIUM_BROKEN_SPRITE.render(screen, x - 12, y - 16);
		} else {
			NORMAL_SPRITE.render(screen, x - 12, y - 16);
		}
    }

	@Override
	public Furniture clone() {
		return new Statue();
	}

	@Override
	public int getLightRadius() {
		switch (Updater.getTime()) {
			case Day: return 0;
			case Evening: return 3;
			case Night: return 6;
			case Morning: return 3;
			default: return 0;
		}
	}
}
