package minicraft.entity.mob;

import java.util.Random;

import minicraft.core.io.Settings;
import minicraft.core.io.Sound;
import minicraft.entity.Arrow;
import minicraft.entity.mob.boss.AirWizardPhase3;
import minicraft.entity.particle.FireParticle;
import minicraft.gfx.MobSprite;
import minicraft.gfx.Screen;

public class EyeQueen extends EnemyMob {
    private static final MobSprite[][][] sprites = new MobSprite[2][2][2];

    static {
        sprites[0][0][0] = new MobSprite(58, 0, 6, 6, 0);
    }
    
    private Random rnd = new Random();

    public EyeQueen(int lvl) {
    	super(5, sprites, 9, 100);
    	

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
		if (player != null) { // checks if player is on zombies level and if there is no time left on randonimity timer
			int xd = player.x - x;
			int yd = player.y - y;
				/// if player is less than 6.25 tiles away, then set move dir towards player
				int sig0 = 1; // this prevents too precise estimates, preventing mobs from bobbing up and down.
				xa = ya = 0;
				
				if (xd < sig0) xa = -1;
				if (xd > sig0) xa = +1;
				if (yd < sig0) ya = -1;
				if (yd > sig0) ya = +1;
				
				
				//  texture phases
				//up
				if (yd > sig0) {
					sprites[0][0][0] = new MobSprite(58, 0, 6, 6, 0);
				}
				
				//down
				if (yd < sig0) {
					sprites[0][0][0] = new MobSprite(58, 6, 6, 6, 0);
				}
				
				
			} else {
				// if the enemy was following the player, but has now lost it, it stops moving.
					//*that would be nice, but I'll just make it move randomly instead.
				randomizeWalkDir(false);
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
    
	public void die() {
		int min = 0, max = 0;
		if (Settings.get("diff").equals("Easy")) {min = 1; max = 3;}
		if (Settings.get("diff").equals("Normal")) {min = 1; max = 2;}
		if (Settings.get("diff").equals("Hard")) {min = 0; max = 2;}
		
		super.die();
		Sound.eyeChangePhase.play();
		level.add(new EyeQueenPhase2(1), x, y);
		
		int randX = rnd.nextInt(10);
		int randY = rnd.nextInt(9);
		level.add(new FireParticle(x - 8 + randX, y - 6 + randY));
		level.add(new FireParticle(x - 8 + randX, y - 6 + randY));
		level.add(new FireParticle(x - 8 + randX, y - 6 + randY));
	}

}
