package minicraft.level.tile;

import minicraft.core.Game;
import minicraft.core.io.Sound;
import minicraft.entity.Direction;
import minicraft.entity.mob.Mob;
import minicraft.entity.mob.Player;
import minicraft.entity.particle.SmashParticle;
import minicraft.entity.particle.TextParticle;
import minicraft.graphic.Color;
import minicraft.graphic.ConnectorSprite;
import minicraft.graphic.Screen;
import minicraft.graphic.Sprite;
import minicraft.item.Item;
import minicraft.item.Items;
import minicraft.item.ToolItem;
import minicraft.item.ToolType;
import minicraft.level.Level;

/// this is the typical stone you see underground and on the surface, that gives coal.

public class AltarTile extends Tile {
    private final ConnectorSprite sprite = new ConnectorSprite(AltarTile.class, new Sprite(27, 6, 3, 3, 1), new Sprite(32, 6, 2, 2, 1), new Sprite(30, 6, 2, 2, 1));

    protected AltarTile(String name) {
        super(name, (ConnectorSprite) null);
        connectorSprite = sprite;
    }

    @Override
    public boolean tick(Level level, int xt, int yt) {
        int damage = level.getData(xt, yt);
        if (damage > 0) {
            level.setData(xt, yt, damage - 1);
            return true;
        }
        return false;
    }

    @Override
    public void render(Screen screen, Level level, int x, int y) {
		// Get the tiles from a 3x3 area from the tile center
		Tile[] areaTiles = level.getAreaTiles(x, y, 1);

        if (level.depth == 1) {
            Tiles.get("Cloud").render(screen, level, x, y); // Sky.
        } else {
            Tiles.get("Dirt").render(screen, level, x, y); // caves, surface and dungeon
        }

		for (Tile tile : areaTiles) {
			if (tile == Tiles.get("Obsidian")) Tiles.get("Obsidian").render(screen, level, x, y);
			if (tile == Tiles.get("Stone bricks")) Tiles.get("Stone bricks").render(screen, level, x, y);
			if (tile == Tiles.get("Oak planks")) Tiles.get("Oak planks").render(screen, level, x, y);
			if (tile == Tiles.get("Spruce planks")) Tiles.get("Spruce planks").render(screen, level, x, y);
			if (tile == Tiles.get("Birch planks")) Tiles.get("Birch planks").render(screen, level, x, y);
			if (tile == Tiles.get("Holy bricks")) Tiles.get("Holy bricks").render(screen, level, x, y);
		}

        sprite.render(screen, level, x, y);
    }


    @Override
    public boolean hurt(Level level, int x, int y, Mob source, int hurtDamage, Direction attackDir) {
        hurt(level, x, y, hurtDamage);
        return true;
    }

    @Override
    public boolean interact(Level level, int xt, int yt, Player player, Item item, Direction attackDir) {
        // creative mode can just act like survival here
        if (item instanceof ToolItem) {
            ToolItem tool = (ToolItem) item;
            if (tool.type == ToolType.Pickaxe && player.payStamina(4 - tool.level) && tool.payDurability()) {
                hurt(level, xt, yt, (random.nextInt(5) + 1) + (tool.level));
                return true;
            }
        }
        return false;
    }

    @Override
    public void hurt(Level level, int x, int y, int hurtDamage) {
        int damage = level.getData(x, y) + hurtDamage;
        int altarHealth = 150;
        if (Game.isMode("Creative")) {
        	hurtDamage = damage = altarHealth;
        }

        Sound.playAt("genericHurt", x << 4, y << 4);
        level.add(new SmashParticle(x << 4, y << 4));
        level.add(new TextParticle("" + hurtDamage, (x << 4) + 8, (y << 4) + 8, Color.DARK_RED));

        if (damage >= altarHealth) {
            level.dropItem((x << 4) + 8, (y << 4) + 8, 1, 1, Items.get("Holy Stone"));
            level.setTile(x, y, Tiles.get("Dirt"));
        } else {
            level.setData(x, y, damage);
        }
    }
}
