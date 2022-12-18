package minicraft.entity.mob.boss;

import minicraft.core.Game;
import minicraft.core.Updater;
import minicraft.core.io.Settings;
import minicraft.core.io.Sound;
import minicraft.entity.Direction;
import minicraft.entity.Entity;
import minicraft.entity.Spark;
import minicraft.entity.mob.EnemyMob;
import minicraft.entity.mob.Player;
import minicraft.gfx.Color;
import minicraft.gfx.Font;
import minicraft.gfx.MobSprite;
import minicraft.gfx.Screen;

public class AirWizardPhase2 extends EnemyMob {
    private static MobSprite[][][] sprites;
    static {
        sprites = new MobSprite[2][4][2];
        for (int i = 0; i < 2; i++) {
            MobSprite[][] list = MobSprite.compileMobSpriteAnimations(16, 12 + (i * 2));
            sprites[i] = list;
        }
    }

    public static boolean beaten = false;
    public static boolean active = true;

    public boolean secondform;
    private int attackDelay = 0;
    private int attackTime = 0;
    private int attackType = 0;
    public static int length;

    /**
     * Constructor for the AirWizard. Will spawn as secondary form if lvl>1.
     * 
     * @param lvl The AirWizard level.
     */
    public AirWizardPhase2(int lvl) {
        this(lvl > 1);
    }

    /**
     * Constructor for the AirWizard.
     * 
     * @param secondform determines if the wizard should be level 2 or 1.
     */
    public AirWizardPhase2(boolean secondform) {
        super(secondform ? 2 : 1, sprites, secondform ? 2000 : 2000, false, 16 * 8, -1, 10, 50);
        
        active = true;

        this.secondform = secondform;
        if (secondform) speed = 3;
        if (!secondform) speed = 2;
        
        walkTime = 2;
    }

    public boolean canSwim() {
        return secondform;
    }

    public boolean canWool() {
        return true;
    }

    @Override
    public void tick() {
        super.tick();
        
        length = health / (maxHealth / 100);

        if (Game.isMode("Creative")) {
            return; // Should not attack if player is in creative
        }

        if (attackDelay > 0) {
            xa = ya = 0;
            int dir = (attackDelay - 45) / 4 % 4; // the direction of attack.
            dir = (dir * 2 % 4) + (dir / 2); // direction attack changes
            if (attackDelay < 45) dir = 0; // direction is reset, if attackDelay is less than 45; prepping for attack.

            this.dir = Direction.getDirection(dir);

            attackDelay--;
            if (attackDelay == 0) {
                // attackType is set to 0 by default
                attackType = 0;
                if (health < maxHealth / 2) {
                    attackType = 1;
                }
                if (health < maxHealth / 10) {
                    attackType = 2;
                }

                // Select a random attack time
                int attackTimeOptions[] = {60, 46, 25};
                attackTime = attackTimeOptions[random.nextInt(3)] * (secondform ? 3 : 2);
            }
            return; // skips the rest of the code (attackDelay must have been > 0)
        }

        if (attackTime > 0) {
            xa = ya = 0;
            attackTime = (int) (attackTime * 0.92); // attackTime will decrease by 7% every time.
            double dir = attackTime; // assigns a local direction variable from the attack time.
            double speed = (secondform ? 1.2 : 0.7) + attackType * 0.2; // speed is dependent on the attackType. (higher attackType, faster speeds)
            level.add(new Spark(this, Math.cos(dir) * speed, Math.sin(dir) * speed, 2)); // adds a spark entity with the cosine and sine of dir times speed.
            return; // skips the rest of the code (attackTime was > 0; ie we're attacking.)
        }

        Player player = getClosestPlayer();

        if (player != null && randomWalkTime == 0) { // if there is a player around, and the walking is not random
            int xd = player.x - x; // the horizontal distance between the player and the air wizard.
            int yd = player.y - y; // the vertical distance between the player and the air wizard.
            if (xd * xd + yd * yd < 16 * 16 * 2 * 2) {
                /// Move away from the player if less than 2 blocks away
                xa = 0; // accelerations
                ya = 0;
                // these four statements basically just find which direction is away from the player:
                if (xd < 0) xa = +1;
                if (xd > 0) xa = -1;
                if (yd < 0) ya = +1;
                if (yd > 0) ya = -1;
            } else if (xd * xd + yd * yd > 16 * 16 * 15 * 15) {// 15 squares away
                /// drags the airwizard to the player, maintaining relative position.
                double hypot = Math.sqrt(xd * xd + yd * yd);
                int newxd = (int) (xd * Math.sqrt(16 * 16 * 15 * 15) / hypot);
                int newyd = (int) (yd * Math.sqrt(16 * 16 * 15 * 15) / hypot);
                x = player.x - newxd;
                y = player.y - newyd;
            }
        }

        if (player != null && randomWalkTime == 0) {
            int xd = player.x - x; // x dist to player
            int yd = player.y - y; // y dist to player
            if (random.nextInt(4) == 0 && xd * xd + yd * yd < 50 * 50 && attackDelay == 0 && attackTime == 0) {
                attackDelay = 60 * 2; // ...then set attackDelay to 120 (2 seconds at default 60 ticks/sec)
            }
        }
    }

    @Override
    public void doHurt(int damage, Direction attackDir) {
        super.doHurt(damage, attackDir);
        if (attackDelay == 0 && attackTime == 0) {
            attackDelay = 60 * 3;
        }
    }

    @Override
    public void render(Screen screen) {
        super.render(screen);

        int textcol = Color.get(1, 0, 204, 0);
        int textcol2 = Color.get(1, 0, 51, 0);
        int percent = health / (maxHealth / 100);
        String h = percent + "%";

        if (percent < 1)
            h = "1%";

        if (percent < 16) {
            textcol = Color.get(1, 204, 0, 0);
            textcol2 = Color.get(1, 51, 0, 0);
        } else if (percent < 51) {
            textcol = Color.get(1, 204, 204, 9);
            textcol2 = Color.get(1, 51, 51, 0);
        }
        
        int textwidth = Font.textWidth(h);
        
        // Bossbar on the the Air wizard phase 2
        if (Settings.get("bossbar").equals("On entity")) {
            Font.drawBar(screen, (x - Screen.w / 12 + 16), y - 24, length, "testificate");
        }
        
        // Bossbar percent
        if (Settings.get("bossbar").equals("Percent")) {
            Font.draw(h, screen, (x - textwidth / 2) + 1, y - 17, textcol2);
            Font.draw(h, screen, (x - textwidth / 2), y - 18, textcol);
        }
    }

    @Override
    protected void touchedBy(Entity entity) {
        if (entity instanceof Player) {
            // if the entity is the Player, then deal them 1 or 2 damage points.
            ((Player) entity).hurt(this, (secondform ? 4 : 2));
        }
    }

    /** What happens when the air wizard dies */
    public void die() {
        Player[] players = level.getPlayers();

        // If there is at least one player in the level
        if (players.length > 0) {
            for (Player p : players) {
                // Give the player 10K or 50K points
                p.addScore(secondform ? 50000 : 10000); 
            }

            // Play sound on the world at the position of the Air Wizard
            Sound.airWizardChangePhase.playOnWorld(x, y);
        }

        // If the Air Wizard is in its first form
        if (!secondform) {
            // Add a new instance of AirWizardPhase3 with 1 life remaining
            level.add(new AirWizardPhase3(1), x, y);
            Updater.notifyAll("Phase III");

            // If the Air Wizard has not been beaten yet, notify all with "Phase III" after 200 milliseconds
            if (!beaten) Updater.notifyAll("Phase III", 200);
            beaten = true;
            active = false;
        } 
 
        // If the Air Wizard is in its second form
        else {
            // Add a new instance of AirWizardPhase3 that has been defeated
            level.add(new AirWizardPhase3(true), x, y);
            Updater.notifyAll("Phase III");
        }

        // Call the die() method in EnemyMob.java
        super.die();
    }

    public int getMaxLevel() {
        return 2;
    }
}
