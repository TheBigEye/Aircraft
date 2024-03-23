package minicraft.level.tile;

import org.tinylog.Logger;

import minicraft.core.Updater;
import minicraft.core.io.Settings;
import minicraft.core.io.Sound;
import minicraft.entity.Direction;
import minicraft.entity.mob.Player;
import minicraft.entity.particle.FireParticle;
import minicraft.graphic.Screen;
import minicraft.graphic.Sprite;
import minicraft.item.Item;
import minicraft.item.Items;
import minicraft.item.PowerGloveItem;
import minicraft.level.Level;

public class TorchTile extends Tile {
    private static Sprite sprite = new Sprite(5, 3, 0);
    
    private Tile onType;
    
    int spawnX = 0;
    int spawnY = 0;

    public static TorchTile getTorchTile(Tile onTile) {
		int id = onTile.id & 0xFFFF;
		
        if (id < 16384) {
            id += 16384;
        } else {
            Logger.info("Tried to place torch on torch tile ...");
        }

        if (Tiles.containsTile(id)) {
            return (TorchTile) Tiles.get(id);
        } else {
            TorchTile tile = new TorchTile(onTile);
            Tiles.add(id, tile);
            return tile;
        }
    }

    private TorchTile(Tile onType) {
        super("Torch " + onType.name, sprite);
        this.onType = onType;
        this.connectsToSand = onType.connectsToSand;
        this.connectsToGrass = onType.connectsToGrass;
        this.connectsToSkyGrass = onType.connectsToSkyGrass;
        this.connectsToSkyHighGrass = onType.connectsToSkyHighGrass;
        this.connectsToSkyDirt = onType.connectsToSkyDirt;
        this.connectsToFerrosite = onType.connectsToFerrosite;
        this.connectsToSnow = onType.connectsToSnow;
        this.connectsToFluid = onType.connectsToFluid;
    }

    @Override
    public void render(Screen screen, Level level, int x, int y) {
        onType.render(screen, level, x, y);
        sprite.render(screen, (x << 4) + 4, (y << 4) + 4);
        
		if (!Updater.paused && tickCount / 2 % 2 == 0 && Settings.getBoolean("particles")) {
			if (random.nextBoolean()) {
				level.add(new FireParticle(spawnX, spawnY));
			}
		}
    }
    
    @Override
	public boolean tick(Level level, int x, int y) {

    	int data = level.getData(x, y);
		if (data != 5) {
			level.setData(x, y, data + 1);
		}  

		spawnX = (x << 4) + 4;
		spawnY = (y << 4) + random.nextInt(2) - random.nextInt(1);
		
		return false;
	}

    @Override
    public int getLightRadius(Level level, int x, int y) {
    	if (level.getData(x, y) == 0) {
    		return 1;
    	}
    
	    return level.getData(x, y);
    }

    @Override
    public boolean interact(Level level, int xt, int yt, Player player, Item item, Direction attackDir) {
        if (item instanceof PowerGloveItem) {
        	Sound.playAt("genericHurt", xt << 4, yt << 4);
            level.setTile(xt, yt, this.onType);
            level.dropItem((xt << 4) + 8, (yt << 4) + 8, Items.get("Torch"));
            return true;
        } else {
            return false;
        }
    }
}
