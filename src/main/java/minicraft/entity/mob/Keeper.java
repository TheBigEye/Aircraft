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

    public Keeper(int lvl) {
        super(5, sprites, 9, 100);
    }

    public void tick() {
        super.tick();

        // if (Game.isMode("Creative")) return; // Should not attack if player is in
        // creative

        if (random.nextInt(1500) == 1) {
            getLevel().add(new Slime(0), x, y + 5);
            getLevel().add(new Slime(0), x, y - 5);
            getLevel().add(new Slime(0), x + 5, y);
            getLevel().add(new Slime(0), x - 5, y);
        }

        Player player = getClosestPlayer();
        if (player != null) {
            int xd = player.x - x;
            int yd = player.y - y;

            int sig0 = 1;

            xa = ya = 0;

            if (xd < sig0)
                xa = -1;
            if (xd > sig0)
                xa = +1;
            if (yd < sig0)
                ya = -1;
            if (yd > sig0)
                ya = +1;

            // texture phases
            if (yd > sig0) { // up
                sprites[0][0][0] = new MobSprite(52, 0, 6, 6, 0);
            }
            if (yd < sig0) { // down
                sprites[0][0][0] = new MobSprite(52, 6, 6, 6, 0);
            }

            if (xd > sig0) { // right
                sprites[0][0][0] = new MobSprite(52, 12, 6, 6, 0);
            }
            if (xd < sig0) { // left
                sprites[0][0][0] = new MobSprite(52, 18, 6, 6, 0);
            }

        } else {
            // if the enemy was following the player, but has now lost it, it stops moving.
            // *that would be nice, but I'll just make it move randomly instead.
            randomizeWalkDir(false);
        }
    }

    @Override
    public void render(Screen screen) {
        sprites[0][0][0].render(screen, x - 25, y - 34);

        int textcol = Color.get(-1, Color.rgb(255, 0, 0));
        int textcol2 = Color.get(-1, Color.rgb(200, 0, 0));
        String h = health + "/" + maxHealth;

        int textwidth = Font.textWidth(h);
        Font.draw(h, screen, (x - textwidth / 2) + 1, y - 40, textcol2);
        Font.draw(h, screen, (x - textwidth / 2), y - 41, textcol);

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

        Sound.Mob_keeper_death.play();
        level.add(new SlimyWizard(1), x, y);
        super.die();
    }

}
