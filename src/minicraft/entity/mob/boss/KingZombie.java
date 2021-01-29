package minicraft.entity.mob.boss;

import minicraft.core.Updater;
import minicraft.core.io.Settings;
import minicraft.entity.mob.EnemyMob;
import minicraft.entity.mob.Player;
import minicraft.entity.mob.Zombie;
import minicraft.gfx.MobSprite;
import minicraft.item.Items;

public class KingZombie extends EnemyMob {
	private static MobSprite[][][] sprites;
	static {
		sprites = new MobSprite[4][4][2];
		for (int i = 0; i < 4; i++) {
			MobSprite[][] list  = MobSprite.compileMobSpriteAnimations(16, 8);
			sprites[i] = list;
		}
	}
	
	/**
	 * Creates a zombie king of the given level.
	 * @param lvl Zombie's level.
	 */
	public KingZombie(int lvl) {
		super(3, sprites, 5, 100);
	}
	
	@Override
	public void tick() {
		super.tick();
				
		if (random.nextInt(500)==1) {
			getLevel().add(new Zombie(3), x, y + 5);
			getLevel().add(new Zombie(3), x, y - 5);
			getLevel().add(new Zombie(3), x + 5, y);
			getLevel().add(new Zombie(3), x - 5, y);
		}
		
		Player player = getClosestPlayer();
		if (player != null && player != null) { // checks if player is on zombies level and if there is no time left on randonimity timer
			int xd = player.x - x;
			int yd = player.y - y;
				/// if player is less than 6.25 tiles away, then set move dir towards player
				int sig0 = 1; // this prevents too precise estimates, preventing mobs from bobbing up and down.
				xa = ya = 0;
				if (xd < sig0) xa = -1;
				if (xd > sig0) xa = +1;
				if (yd < sig0) ya = -1;
				if (yd > sig0) ya = +1;
			} else {
				// if the enemy was following the player, but has now lost it, it stops moving.
					//*that would be nice, but I'll just make it move randomly instead.
				randomizeWalkDir(false);
			}
		}
	
	
/*	@Override
	public void render(Screen screen) {
		
	int textcol = Color.get(-1, Color.rgb(255, 0, 0));
	int textcol2 = Color.get(-1, Color.rgb(200, 0, 0));
	String h = health + "/" + maxHealth;
	
	int textwidth = Font.textWidth(h);
	Font.draw(h, screen, (x - textwidth/2) + 1, y - 17, textcol2);
	Font.draw(h, screen, (x - textwidth/2), y - 18, textcol);
}*/
	
	public void die() {
		if (Settings.get("diff").equals("Easy")) dropItem(20, 20, Items.get("cloth"));
		if (Settings.get("diff").equals("Normal")) dropItem(15, 15, Items.get("cloth"));
		if (Settings.get("diff").equals("Hard")) dropItem(10, 10, Items.get("cloth"));
		
		if(random.nextInt(60) == 2) {
			level.dropItem(x, y, Items.get("emerald"));
		}
		
		if(random.nextInt(40) == 19) {
			int rand = random.nextInt(3);
			if(rand == 0) {
				level.dropItem(x, y, Items.get("green clothes"));
			} else if(rand == 1) {
				level.dropItem(x, y, Items.get("red clothes"));
			} else if(rand == 2) {
				level.dropItem(x, y, Items.get("blue clothes"));
			}
		}
		
		super.die();
		Updater.notifyAll("The zombie king has "
				          + "been defeated!", 200);
	}
}

