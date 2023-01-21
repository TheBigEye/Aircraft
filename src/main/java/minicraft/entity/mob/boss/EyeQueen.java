package minicraft.entity.mob.boss;

import minicraft.core.io.Settings;
import minicraft.core.io.Sound;
import minicraft.entity.Entity;
import minicraft.entity.mob.EnemyMob;
import minicraft.entity.mob.Player;
import minicraft.gfx.Color;
import minicraft.gfx.Font;
import minicraft.gfx.MobSprite;
import minicraft.gfx.Screen;

public class EyeQueen extends EnemyMob {
    private static final MobSprite[][][] sprites = new MobSprite[1][1][1];

    static {
        sprites[0][0][0] = new MobSprite(64, 0, 4, 4, 0);
    }
 
    public static boolean beaten = false;
    public static boolean active = true;
    public static EyeQueen entity = null;
    
    private int tickTime = 0;
    public static int length;
    
    public EyeQueen(int lvl) {
        super(5, sprites, 24000, false, 16 * 8, -1, 10, 50);
        
        active = true;
        entity = this;
        beaten = false;

        walkTime = 2;
    }

    public boolean canSwim() {
        return false;
    }
    
    public boolean canWool() {
        return true;
    }

    public void tick() {
        super.tick();
        tickTime++;
        
        length = health / (maxHealth / 100);
        int spriteMirror = random.nextInt(1);

        Player player = level.getClosestPlayer(x, y);
        if (player != null && randomWalkTime == 0) { // if there is a player around, and the walking is not random
            int xd = player.x - x; // the horizontal distance between the player and the air wizard.
            int yd = player.y - y; // the vertical distance between the player and the air wizard.

            if ((xd * xd + yd * yd) < (16*16 * 8*8)) {
                xa = 0; // accelerations
                ya = 0;
                
                // these four statements basically just find which direction is away from the player:
                if (xd < 1) xa -= 1;
                else if (xd > 1) xa += 1;
                if (yd < 1) ya -= 1;
                else if (yd > 1) ya += 1;
                
                /// SEGMENTED TEXTURES (THIS IS HARD)
                
                if (yd > 1) { // up
                	if (tickTime /12 %2 == 0) {
                		sprites[0][0][0] = new MobSprite(64, 0, 4, 4, spriteMirror); // up 1
                	} else {
                		if (random.nextBoolean()) {
                			sprites[0][0][0] = new MobSprite(68, 0, 4, 4, spriteMirror); // up 2
                		} else {
                			sprites[0][0][0] = new MobSprite(72, 0, 4, 4, spriteMirror); // up 2
                		}
                	}
                } else if (yd < 1) { // down
                	if (tickTime /12 %2 == 0) {
                		sprites[0][0][0] = new MobSprite(64, 8, 4, 4, spriteMirror); // down 1
                	} else {
                		if (random.nextBoolean()) {
                			sprites[0][0][0] = new MobSprite(68, 8, 4, 4, spriteMirror); // up 2
                		} else {
                			sprites[0][0][0] = new MobSprite(72, 8, 4, 4, spriteMirror); // up 2
                		}
                	}
                }

                if (xd > 1) { // right
                	if (tickTime /12 %2 == 0) {
                		sprites[0][0][0] = new MobSprite(64, 4, 4, 4, 0); // right 1
                	} else {
                		if (random.nextBoolean()) {
                			sprites[0][0][0] = new MobSprite(68, 4, 4, 4, 0); // right 2
                		} else {
                			sprites[0][0][0] = new MobSprite(72, 4, 4, 4, 0); // right 2
                		}
                	}
                } else if (xd < 1) { // left
                	if (tickTime /12 %2 == 0) {
                		sprites[0][0][0] = new MobSprite(64, 4, 4, 4, 1); // left 1
                	} else {
                		if (random.nextBoolean()) {
                			sprites[0][0][0] = new MobSprite(68, 4, 4, 4, 1); // left 2
                		} else {
                			sprites[0][0][0] = new MobSprite(72, 4, 4, 4, 1); // left 2
                		}
                	}
                }
            
	        }/* else if ((xd * xd + yd * yd) > (16*16 * 15*15)) { // 15 squares away
	            /// drags the airwizard to the player, maintaining relative position.
	            double hypot = Math.sqrt(xd * xd + yd * yd);
	            int newxd = (int) (xd * Math.sqrt(16 * 16 * 15 * 15) / hypot);
	            int newyd = (int) (yd * Math.sqrt(16 * 16 * 15 * 15) / hypot);
	            x = player.x - newxd;
	            y = player.y - newyd;  
	        }*/
        }
    }

    @Override
    public void render(Screen screen) {
        int pupilColor = (int)((length) * 2.55f);
    	
        sprites[0][0][0].render(screen, x - 16, y - 24, -1, Color.get(1, 255 - pupilColor, pupilColor, 0));

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
            ((Player) entity).hurt(this, 2);
        }
    }


    public void die() {    
        Sound.Mob_eyeBoss_changePhase.playOnGui();
        
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