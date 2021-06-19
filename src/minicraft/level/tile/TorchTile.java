package minicraft.level.tile;

import java.util.Random;

import minicraft.core.io.Sound;
import minicraft.entity.Direction;
import minicraft.entity.mob.Player;
import minicraft.entity.particle.FireParticle;
import minicraft.gfx.Screen;
import minicraft.gfx.Sprite;
import minicraft.item.Item;
import minicraft.item.Items;
import minicraft.item.PowerGloveItem;
import minicraft.level.Level;

public class TorchTile extends Tile {
	private static Sprite sprite = new Sprite(5, 3, 0);
	private static int LIGHT = 5;

	private Tile onType;

	private Random rnd = new Random();

	public static TorchTile getTorchTile(Tile onTile) {
		int id = onTile.id & 0xFF;
		// noinspection ConstantConditions
		if (id < 128)
			id += 128;
		else
			System.out.println("tried to place torch on torch tile...");

		if (Tiles.containsTile(id))
			return (TorchTile) Tiles.get(id);
		else {
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
		this.connectsToSnow = onType.connectsToSnow;
		this.connectsToFluid = onType.connectsToFluid;
	}

	public void render(Screen screen, Level level, int x, int y) {
		onType.render(screen, level, x, y);
		sprite.render(screen, x * 16 + 4, y * 16 + 4);
	}

	public boolean tick(Level level, int x, int y) {

		if (random.nextInt(5) == 1) {

			if (random.nextInt(2) == 0) {
				LIGHT = 6;
			}
			if (random.nextInt(2) == 1) {
				LIGHT = 5;
			}
		}
		return false;

	}

	public int getLightRadius(Level level, int x, int y) {
		return LIGHT;
	}

	public boolean interact(Level level, int xt, int yt, Player player, Item item, Direction attackDir) {
		if (item instanceof PowerGloveItem) {
			level.setTile(xt, yt, this.onType);
			Sound.Tile_generic_hurt.play();
			int randX = rnd.nextInt(20);
			int randY = rnd.nextInt(19);
			level.add(new FireParticle(xt - 1 + randX, yt - 1 + randY));
			level.dropItem(xt * 16 + 8, yt * 16 + 8, Items.get("Torch"));
			return true;
		} else {
			return false;
		}
	}
}
