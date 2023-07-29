package minicraft.entity.mob;

import minicraft.core.io.Settings;
import minicraft.core.io.Sound;
import minicraft.entity.Direction;
import minicraft.entity.Entity;
import minicraft.graphic.Color;
import minicraft.graphic.Font;
import minicraft.graphic.MobSprite;
import minicraft.graphic.Point;
import minicraft.graphic.Screen;
import minicraft.level.tile.Tile;

public class EyeQueen extends GiantBossMob {
    private static MobSprite[][][] spritesMain;

    static {
    	spritesMain = new MobSprite[2][4][2];
        for (int i = 0; i < 1; i++) { // Normal wizard
            MobSprite[][] list = MobSprite.compileMobSpriteAnimations(60, 0, 4, 4);
            spritesMain[i] = list;
        }
    }
 
    public static boolean beaten = false;
    public static boolean active = true;
    public static EyeQueen entity = null;

    public static int length;
    
    public EyeQueen(int lvl) {
        super(5, spritesMain, 24000, false, 16 * 8, -1, 10, 50);
        
        active = true;
        entity = this;
        beaten = false;

        walkTime = 2;
        
        this.setHitboxSize(8, 6);
    }

    public boolean canSwim() {
        return false;
    }
    
    public boolean canWool() {
        return true;
    }

    
    private Point getInteractionTile() {
        int x = this.x;
        int y = this.y - 2;

        x += dir.getX() * 12;
        y += dir.getY() * 12;

        return new Point(x >> 4, y >> 4);
    }

    public void tick() {
        super.tick();
        
        length = health / (maxHealth / 100);

        if (tickTime / 2 % 16 == 0) {
			Direction attackDir = dir; // Make the attack direction equal the current direction
			
			// Attempts to hurt the tile in the appropriate direction.
			Point interactionTile = getInteractionTile();
			
			// Check if tile is in bounds of the map.
			if (interactionTile.x >= 0 && interactionTile.y >= 0 && interactionTile.x < level.w && interactionTile.y < level.h) {
				Tile targetTile = level.getTile(interactionTile.x, interactionTile.y);
				/*if (targetTile == Tiles.get("Oak Tree"))*/ targetTile.hurt(level, interactionTile.x, interactionTile.y, this, random.nextInt(10) + 16, attackDir);
			}
        }
    }
    


    @Override
    public void render(Screen screen) {
    	super.render(screen);

        int textColor = Color.get(1, 0, 204, 0);
        int textColor2 = Color.get(1, 0, 51, 0);
        int percent = health / (maxHealth / 100);
        String h = percent + "%";

        if (percent < 1) {
            h = "1%";
        }

        if (percent < 16) {
            textColor = Color.get(1, 204, 0, 0);
            textColor2 = Color.get(1, 51, 0, 0);
        } else if (percent < 51) {
            textColor = Color.get(1, 204, 204, 9);
            textColor2 = Color.get(1, 51, 51, 0);
        }
        
        int textwidth = Font.textWidth(h);
        
        // Bossbar on the the Air wizard
        if (Settings.get("bossbar").equals("On entity")) {
            Font.drawBar(screen, (x - Screen.w / 12 + 16), y - 24, length, "testificate");
        }

        // Bossbar percent
        if (Settings.get("bossbar").equals("Percent")) {
            Font.draw(h, screen, (x - textwidth / 2) + 1, y - 17, textColor2);
            Font.draw(h, screen, (x - textwidth / 2), y - 18, textColor);
        }
    }
    
    @Override
    protected void touchedBy(Entity entity) {
        if (entity instanceof Player) {
            // if the entity is the Player, then deal them 1 or 2 damage points.
            ((Player) entity).hurt(this, random.nextInt(2) + 2);
        }
    }


    public void die() {
        Sound.Mob_eyeBoss_changePhase.playOnLevel(this.x, this.y);
        
        beaten = true;
        active = false;
        entity = null;
        
        super.die();
    }
    
	@Override
	public int getLightRadius() {
		return 3;
	}

}