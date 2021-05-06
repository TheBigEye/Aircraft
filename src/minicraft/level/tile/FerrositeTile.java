package minicraft.level.tile;

import minicraft.core.Game;
import minicraft.core.io.Sound;
import minicraft.entity.Direction;
import minicraft.entity.Entity;
import minicraft.entity.mob.Mob;
import minicraft.entity.mob.Player;
import minicraft.gfx.ConnectorSprite;
import minicraft.gfx.Sprite;
import minicraft.item.Item;
import minicraft.item.Items;
import minicraft.item.PotionType;
import minicraft.item.ToolItem;
import minicraft.item.ToolType;
import minicraft.level.Level;

public class FerrositeTile extends Tile {
	private static ConnectorSprite sprite = new ConnectorSprite(FerrositeTile.class, new Sprite(12, 22, 3, 3, 1, 3), new Sprite(15, 24, 2, 2, 1, 3), new Sprite(15, 22, 2, 2, 1)) {
		public boolean connectsTo(Tile tile, boolean isSide) {

			return tile != Tiles.get("Infinite fall");
		}
	};
	
	protected FerrositeTile(String name) {
		super(name, sprite);
		connectsToFerrosite = true;
		connectsToCloud = true;
		maySpawn = true;
	}

	public boolean mayPass(Level level, int x, int y, Entity e) {
		return true;
	}


	public void steppedOn(Level level, int x, int y, Entity entity) {
		if (entity instanceof Mob) {
			level.setData(x, y, 10);
		}
		if (entity instanceof Player) {

			level.setData(x, y, 10);

			Player.moveSpeed = 3;
			
		} else {
			if (random.nextInt(16) == 1) {
				Player.moveSpeed = 1;
			} 
			if (Game.player.getPotionEffects().containsKey(PotionType.Speed) && random.nextInt(16) == 1) {
				Player.moveSpeed = 2;
			}
			if (Game.player.getPotionEffects().containsKey(PotionType.xSpeed) && random.nextInt(16) == 1) {
				Player.moveSpeed = 3;
			}
		}
	}

	public boolean interact(Level level, int xt, int yt, Player player, Item item, Direction attackDir) {
		// creative mode
		if (item instanceof ToolItem) {
			ToolItem tool = (ToolItem) item;
			if (tool.type == ToolType.Shovel && player.payStamina(5)) {
				level.setTile(xt, yt, Tiles.get("Infinite fall")); // would allow you to shovel cloud, I think.
				Sound.monsterHurt.play();
				level.dropItem(xt * 16 + 8, yt * 16 + 8, 1, 3, Items.get("cloud"));
				return true;
			}
		}
		return false;
	}

}
