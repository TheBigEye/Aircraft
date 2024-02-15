package minicraft.entity.mob;

import minicraft.core.Updater;
import minicraft.core.io.Settings;
import minicraft.core.io.Sound;
import minicraft.entity.Direction;
import minicraft.entity.Entity;
import minicraft.entity.Fireball;
import minicraft.entity.particle.FireParticle;
import minicraft.graphic.Color;
import minicraft.graphic.Font;
import minicraft.graphic.MobSprite;
import minicraft.graphic.Point;
import minicraft.graphic.Screen;
import minicraft.item.Items;
import minicraft.level.tile.Tile;
import minicraft.level.tile.Tiles;

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
    
    private int attackDelay = 0;
    private int attackTime = 0;
    private int attackType = 0;
    
    private int currentPhase = 0;

    public static int length;
    
    public EyeQueen(int lvl) {
        super(5, spritesMain, 18000, false, 16 * 8, -1, 10, 50);
        
        active = true;
        entity = this;
        beaten = false;

        walkTime = 2;
        
        currentPhase = 1;
        
        this.setHitboxSize(6, 6);
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
        
        Player player = getClosestPlayer();
        
        // Change phases by health
        if (health <= 12000 && currentPhase == 1) {
            int randX = random.nextInt(16);
            int randY = random.nextInt(16);
            level.add(new FireParticle(x - 0 + randX, y - 0 + randY));
            level.add(new FireParticle(x - 32 + randX, y - 24 + randY));
            level.add(new FireParticle(x - 26 + randX, y - 14 + randY));
        	// change to phase 2
        	Sound.eyeQueenChangePhase.playOnLevel(this.x, this.y);
        	currentPhase = 2;
        }
        
        if (health <= 6000 && currentPhase == 2) {
            int randX = random.nextInt(16);
            int randY = random.nextInt(16);
            level.add(new FireParticle(x - 0 + randX, y - 0 + randY));
            level.add(new FireParticle(x - 32 + randX, y - 24 + randY));
            level.add(new FireParticle(x - 26 + randX, y - 14 + randY));
        	// change to phase 3
        	Sound.eyeQueenChangePhase.playOnLevel(this.x, this.y);
        	currentPhase = 3;
        }

        if ((tickTime / 2 % 16 == 0)) {
			Direction attackDir = dir; // Make the attack direction equal the current direction
			
			// Attempts to hurt the tile in the appropriate direction.
			Point interactionTile = getInteractionTile();
			
			// Check if tile is in bounds of the map.
			if (interactionTile.x >= 0 && interactionTile.y >= 0 && interactionTile.x < level.w && interactionTile.y < level.h) {
				Tile targetTile = level.getTile(interactionTile.x, interactionTile.y);
				if ((targetTile != Tiles.get("Hard Rock") || targetTile != Tiles.get("Summon Altar")) && !targetTile.mayPass(level, interactionTile.x, interactionTile.y, this)) { 
					targetTile.hurt(level, interactionTile.x, interactionTile.y, this, random.nextInt(10) + 16, attackDir);
					for (int i = 0; i < 1 + random.nextInt(8); i++) {
						int randX = random.nextInt(16);
						int randY = random.nextInt(12);
						level.add(new FireParticle(interactionTile.x - 8 + randX, interactionTile.y - 6 + randY));
					}
				}
			}
        }
        
        
        if (attackDelay > 0) {
            xa = ya = 0;
            int dir = ((attackDelay - 45) / 4) % 4; // the direction of attack.
            dir = ((dir * 2) % 4) + (dir / 2); // direction attack changes
            if (attackDelay < 45) dir = 0; // direction is reset, if attackDelay is less than 45; prepping for attack.

            this.dir = Direction.getDirection(dir);

            attackDelay--;
            if (attackDelay == 0) {
                // attackType is set to 0 by default
                attackType = 0;
                if (health < maxHealth / 2) attackType = 1;
                if (health < maxHealth / 10) attackType = 2;
                
                // Select a random attack time based on phase
                int attackTimeOptions[][] = {
                    {160, 146, 125},
                    {1080, 1050, 1030},
                    {2120, 2200, 2400}
                };
                attackTime = attackTimeOptions[currentPhase - 1][random.nextInt(3)] * 2;
            }
            return; // skips the rest of the code (attackDelay must have been > 0)
            
        } else if (attackTime > 0 && currentPhase == 1) { // First phase sparks attack
            xa = ya = 0;
            attackTime = (int) (attackTime * 0.92); // attackTime will decrease by 7% every time.
            double dir = attackTime * 0.25 * (attackTime % 2 * 2 - 1); // assigns a local direction variable from the attack time.
            double speed = (0.7) + attackType * 0.2; // speed is dependent on the attackType. (higher attackType, faster speeds)
            level.add(new Fireball(this, Math.cos(dir) * speed, Math.sin(dir) * speed, 2, random.nextInt(2) + 1)); // adds a spark entity with the cosine and sine of dir times speed.

            Sound.airWizardSpawnSpark.playOnLevel(this.x, this.y);
            
            return; // skips the rest of the code (attackTime was > 0; ie we're attacking.)
            
        } else if (attackTime > 0 && currentPhase >= 2) { // First phase sparks attack
            xa = ya = 0;
            attackTime = (int) (attackTime * 0.92); // attackTime will decrease by 7% every time.
            double dir = attackTime * 0.25 * (attackTime % 2 * 2 - 1); // assigns a local direction variable from the attack time.
            double speed = (0.7) + attackType * 0.2; // speed is dependent on the attackType. (higher attackType, faster speeds)
            level.add(new Fireball(this, Math.cos(dir) * speed, Math.sin(dir) * speed, 3, random.nextInt(3) + 1)); // adds a spark entity with the cosine and sine of dir times speed.

            Sound.airWizardSpawnSpark.playOnLevel(this.x, this.y);
            
            return; // skips the rest of the code (attackTime was > 0; ie we're attacking.)
        }
        
        if (player != null && randomWalkTime == 0 && (tickTime / 2 % 16 == 0)) {
            int xd = player.x - x; // x dist to player
            int yd = player.y - y; // y dist to player
            if (random.nextInt(32) == 0 && xd * xd + yd * yd < 50 * 50 && attackDelay == 0 && attackTime == 0) {
            	if (currentPhase == 1) {
            		attackDelay = 60 * 6; // ...then set attackDelay to 240 (4 seconds at default 60 ticks/sec)
            	} else {
            		attackDelay = 60 * 3; // ...then set attackDelay to 120 (2 seconds at default 60 ticks/sec)
            	}
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
        Sound.eyeQueenDeath.playOnLevel(this.x, this.y);
        
        beaten = true;
        active = false;
        entity = null;
        
        Updater.notifyAll("The Eye Queen was beaten!", 200);
        
        dropItem(25, 50, Items.get("emerald"));
        dropItem(1, 1, Items.get("AlAzif"));
        
        super.die();
    }
    
	@Override
	public int getLightRadius() {
		return currentPhase == 3 ? 4: 3;
	}

}
