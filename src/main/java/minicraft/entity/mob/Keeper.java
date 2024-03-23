package minicraft.entity.mob;

import minicraft.core.io.Settings;
import minicraft.core.io.Sound;
import minicraft.graphic.Color;
import minicraft.graphic.Font;
import minicraft.graphic.MobSprite;
import minicraft.graphic.Screen;

public class Keeper extends GiantBossMob {
    private static MobSprite[][][] spritesMain;

    static {
    	spritesMain = new MobSprite[2][4][2];
        for (int i = 0; i < 1; i++) { // Normal wizard
            MobSprite[][] list = MobSprite.compileMobSpriteAnimations(60, 4, 4, 4);
            spritesMain[i] = list;
        }
    }
    
    public static boolean beaten = false;
    public static boolean active = true;
    public static Keeper entity = null;
    
    public static int length;
 
    // private int slimeSpawnRate = 0;

    public Keeper(int lvl) {
    	super(5, spritesMain, 12000, false, 16 * 8, -1, 10, 50);
    	
        active = true;
        entity = this;
        beaten = false;
        
        walkTime = 2;
        
        this.setHitboxSize(6, 6);
    }

    public void tick() {
        super.tick();
        
        length = health / (maxHealth / 100);

        /*slimeSpawnRate++;
        if (slimeSpawnRate >= 1500) {
            getLevel().add(new Slime(0), x, y + 5);
            getLevel().add(new Slime(0), x, y - 5);
            getLevel().add(new Slime(0), x + 5, y);
            getLevel().add(new Slime(0), x - 5, y);
            slimeSpawnRate = 0;
        }*/

        Player player = getClosestPlayer();
        if (player != null) {
            int xd = player.x - x;
            int yd = player.y - y;

            int sig0 = 1;
            xa = ya = 0;

            if (xd < sig0) xa = -1;
            else if (xd > sig0) xa = 1;
            if (yd < sig0) ya = -1;
            else if (yd > sig0) ya = 1;
        } else {
            // if the enemy was following the player, but has now lost it, it stops moving.
            // *that would be nice, but I'll just make it move randomly instead.
            randomizeWalkDir(false);
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
            Font.drawBar(screen, (x - Screen.w / 12 + 16), y - 24, length);
        }

        // Bossbar percent
        if (Settings.get("bossbar").equals("Percent")) {
            Font.draw(h, screen, (x - textwidth / 2) + 1, y - 17, textColor2);
            Font.draw(h, screen, (x - textwidth / 2), y - 18, textColor);
        }
    }

    public boolean canSwim() {
        return false;
    }
    
    public boolean canWool() {
        return true;
    }

    @SuppressWarnings("unused")
    public void die() {
        int min = 0;
        int max = 0;
        if (Settings.get("diff").equals("Peaceful")) {
            min = 1;
            max = 3;
        }
        if (Settings.get("diff").equals("Easy")) {
            min = 1;
            max = 3;
        }
        if (Settings.get("diff").equals("Normal")) {
            min = 1;
            max = 2;
        }
        if (Settings.get("diff").equals("Hard")) {
            min = 0;
            max = 2;
        }

        Sound.playAt("keeperDeath", this.x, this.y);
        
        beaten = true;
        active = false;
        entity = null;

        level.add(new SlimyWizard(1), x, y);
        super.die();
    }
    
    @Override
	public int getLightRadius() {
		return 3;
	}
}
