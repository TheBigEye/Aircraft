package minicraft.entity.mob;

import minicraft.core.io.Settings;
import minicraft.core.io.Sound;
import minicraft.gfx.Color;
import minicraft.gfx.Font;
import minicraft.gfx.MobSprite;
import minicraft.gfx.Screen;

public class Keeper extends EnemyMob {
    private static final MobSprite[][][] sprites = new MobSprite[2][2][2];
    static {
        sprites[0][0][0] = new MobSprite(52, 0, 6, 6, 0);
    }
    
    private int slimeSpawnRate = 0;
    private int tickTime = 0;

    public Keeper(int lvl) {
    	super(5, sprites, 12000, false, 16 * 8, -1, 10, 50);
    }

    public void tick() {
        super.tick();
        tickTime++;

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

            // texture phases
            if (yd > 1) { // up
            	if (tickTime /12 %2 == 0) {
            		sprites[0][0][0] = new MobSprite(64, 13, 4, 4, 0); // up 1
            	} else {
            		sprites[0][0][0] = new MobSprite(64, 13, 4, 4, 1); // up 2
            	}
            } else if (yd < 1) { // down
            	if (tickTime /12 %2 == 0) {
            		sprites[0][0][0] = new MobSprite(68, 13, 4, 4, 0); // down 1
            	} else {
            		sprites[0][0][0] = new MobSprite(68, 13, 4, 4, 1); // down 2
            	}
            }

            if (xd > 1) { // right
            	sprites[0][0][0] = new MobSprite(72, 13, 4, 4, 0);
            } else if (xd < 1) { // left
            	sprites[0][0][0] = new MobSprite(72, 13, 4, 4, 1);
            }

        } else {
            // if the enemy was following the player, but has now lost it, it stops moving.
            // *that would be nice, but I'll just make it move randomly instead.
            randomizeWalkDir(false);
        }
    }

    @Override
    public void render(Screen screen) {
        sprites[0][0][0].render(screen, x - 16, y - 24);

        int textColor = Color.get(-1, Color.rgb(255, 0, 0));
        int textColor2 = Color.get(-1, Color.rgb(200, 0, 0));
        String h = health + "/" + maxHealth;

        int textwidth = Font.textWidth(h);
        Font.draw(h, screen, (x - textwidth / 2) + 1, y - 40, textColor2);
        Font.draw(h, screen, (x - textwidth / 2), y - 41, textColor);

        String txt = "";
        int w = Font.textWidth(txt) / 2;
        Font.drawTransparentBackground(txt, screen, x - w, y - 45 - Font.textHeight());
    }

    public boolean canSwim() {
        return false;
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

        Sound.Mob_keeper_death.playOnGui();
        level.add(new SlimyWizard(1), x, y);
        super.die();
    }
}
