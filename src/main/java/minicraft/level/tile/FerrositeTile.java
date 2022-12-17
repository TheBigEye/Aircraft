package minicraft.level.tile;

import minicraft.core.Game;
import minicraft.core.io.Settings;
import minicraft.core.io.Sound;
import minicraft.entity.Direction;
import minicraft.entity.Entity;
import minicraft.entity.mob.Mob;
import minicraft.entity.mob.Player;
import minicraft.entity.particle.FerrositeParticle;
import minicraft.gfx.ConnectorSprite;
import minicraft.gfx.Sprite;
import minicraft.item.Item;
import minicraft.item.Items;
import minicraft.item.ToolItem;
import minicraft.item.ToolType;
import minicraft.level.Level;

public class FerrositeTile extends Tile {
    private static ConnectorSprite sprite = new ConnectorSprite(FerrositeTile.class, new Sprite(12, 22, 3, 3, 1), new Sprite(15, 24, 2, 2, 1), new Sprite(15, 22, 2, 2, 1)) {
        @Override
        public boolean connectsTo(Tile tile, boolean isSide) {
            return tile != Tiles.get("Infinite fall");
        }
    };
    
    private int tickTime = 0;

    protected FerrositeTile(String name) {
        super(name, sprite);
        connectsToFerrosite = true;
        connectsToCloud = true;
        maySpawn = true;
    }
    
    @Override
    public boolean tick(Level level, int xt, int yt) {
    	tickTime++;
        return false;
    }
     
	public void steppedOn(Level level, int x, int y, Entity entity) {
		if (tickTime / 8 % 2 == 0 && Settings.get("particles").equals(true)) {
			// Spawn ferrosite particles under the mobs
			if (entity instanceof Mob) {
				if (random.nextInt(1) == 0) {
					int spawnX  = (entity.x - 4) + random.nextInt(8) - random.nextInt(8);
					int spawnY = (entity.y - 4) + random.nextInt(8) - random.nextInt(8);
					
					for (Direction dir : Direction.values()) {
						Tile neighbour = level.getTile(x + dir.getX(), y + dir.getY());
						if (neighbour != null) {
							
							// Particles only spawn on ferrosite cloud tiles.
							if (!(neighbour instanceof FerrositeTile)) { 
								// Offsets
								if (dir.getX() < 0) if ((entity.x % 16) < 8) spawnX += 8 - entity.x % 16;
								if (dir.getX() > 0) if ((entity.x % 16) > 7) spawnX -= entity.x % 16 - 8;
								if (dir.getY() < 0) if ((entity.y % 16) < 8) spawnY += 8 - entity.y % 16;
								if (dir.getY() > 0) if ((entity.y % 16) > 7) spawnY -= entity.y % 16 - 8;
							}
						}
					}
	
					level.add(new FerrositeParticle(spawnX, spawnY));
				}
			}
		}
	}

    @Override
    public boolean interact(Level level, int xt, int yt, Player player, Item item, Direction attackDir) {
    	// we don't want the tile to break when attacked with just anything, even in creative mode
        if (item instanceof ToolItem) {
            ToolItem tool = (ToolItem) item;
            if (tool.type == ToolType.Shovel && player.payStamina(5)) {

                /* 
                 If the current level is the sky then when breaking the
                 ferrosite tile Infinite fall appears, if not, hole will appear
                */
                if (Game.currentLevel == 4) {
                    level.setTile(xt, yt, Tiles.get("Infinite fall"));
                } else {
                    level.setTile(xt, yt, Tiles.get("Hole"));
                }

                Sound.genericHurt.playOnGui();
                level.dropItem(xt * 16 + 8, yt * 16 + 8, 1, 3, Items.get("Ferrosite"));
                return true;
            }
        }
        return false;
    }

}
