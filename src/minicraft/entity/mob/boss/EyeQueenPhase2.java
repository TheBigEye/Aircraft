package minicraft.entity.mob.boss;

import minicraft.core.io.Settings;
import minicraft.core.io.Sound;
import minicraft.entity.Fireball;
import minicraft.entity.mob.EnemyMob;
import minicraft.entity.mob.Player;
import minicraft.gfx.MobSprite;
import minicraft.gfx.Screen;

public class EyeQueenPhase2 extends EnemyMob {
    private static final MobSprite[][][] sprites = new MobSprite[2][2][2];

    static {
        sprites[0][0][0] = new MobSprite(64, 0, 6, 6, 0);
    }
    
	private int arrowtime;
	private int artime;

    public EyeQueenPhase2(int lvl) {
    	super(5, sprites, 9, 100);
    	
		arrowtime = 500 / (lvl + 5);
		artime = arrowtime;
    }

	public void tick() {
		super.tick();
		
		/**if (random.nextInt(2000)==1) {
			getLevel().add(new Slime(0), x, y + 5);
			getLevel().add(new Slime(0), x, y - 5);
			getLevel().add(new Slime(0), x + 5, y);
			getLevel().add(new Slime(0), x - 5, y);
		}**/
		
		Player player = getClosestPlayer();
		if (player != null) {
			int xd = player.x - x;
			int yd = player.y - y;
				int sig0 = 1; 
				xa = ya = 0;
				if (xd < sig0) xa = -1;
				if (xd > sig0) xa = +1;
				if (yd < sig0) ya = -1;
				if (yd > sig0) ya = +1;
				
				//  texture phases
				//up
				if (yd > sig0) {
					sprites[0][0][0] = new MobSprite(64, 0, 6, 6, 0);
				}				
				//down
				if (yd < sig0) {
					sprites[0][0][0] = new MobSprite(64, 6, 6, 6, 0);
				}
				if (xd > sig0) { // right
					sprites[0][0][0] = new MobSprite(64, 12, 6, 6, 0);
				}			
				if (xd < sig0) { // left
					sprites[0][0][0] = new MobSprite(64, 18, 6, 6, 0);
				}
				
			} else {
				// if the enemy was following the player, but has now lost it, it stops moving.
				randomizeWalkDir(false);
			}
		
		if(skipTick()) return;
		
		if (player != null && randomWalkTime == 0) {
			artime--;
			
			int xd = player.x - x;
			int yd = player.y - y;
			if (xd * xd + yd * yd < 100 * 100) {
				if (artime < 1) {
					level.add(new Fireball(this, dir, lvl));

					artime = arrowtime;
				}
			}
		}
		
		
		}
		
    
    @Override
    public void render(Screen screen) {
        sprites[0][0][0].render(screen, x - 25, y - 34);

	/*	int textcol = Color.get(-1, Color.rgb(255, 0, 0));
		int textcol2 = Color.get(-1, Color.rgb(200, 0, 0));
		String h = health + "/" + maxHealth;
		
    	int textwidth = Font.textWidth(h);
    	Font.draw(h, screen, (x - textwidth/2) + 1, y -40, textcol2);
    	Font.draw(h, screen, (x - textwidth/2), y -41, textcol);
    	*/
    }
	
    public boolean canSwim() {
		return true;
	}

	@SuppressWarnings("unused")
	public void die() {
		int min = 0, max = 0;
		if (Settings.get("diff").equals("Peaceful")) {min = 1; max = 3;}
		if (Settings.get("diff").equals("Easy")) {min = 1; max = 3;}
		if (Settings.get("diff").equals("Normal")) {min = 1; max = 2;}
		if (Settings.get("diff").equals("Hard")) {min = 0; max = 2;}
		
		super.die();
		Sound.Mob_eyeBoss_changePhase.play();
		level.add(new EyeQueenPhase3(1), x, y);
	}

}
