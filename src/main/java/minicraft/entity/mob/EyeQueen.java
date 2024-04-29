package minicraft.entity.mob;

import minicraft.core.Updater;
import minicraft.core.io.Sound;
import minicraft.entity.Direction;
import minicraft.entity.Entity;
import minicraft.entity.Fireball;
import minicraft.entity.particle.FireParticle;
import minicraft.graphic.Color;
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
        for (int i = 0; i < 1; i++) {
            MobSprite[][] list = MobSprite.compileMobSpriteAnimations(60, 0, 4, 4);
            spritesMain[i] = list;
        }
    }
 
    public static boolean beaten = false;
    public static boolean active = true;
    public static EyeQueen entity = null;
    
    private boolean deathing = false;
    
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
        
        deathing = false;
        
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
        
        // Update the length for the bossbar
        length = health / (maxHealth / 100);
        
        // The the nearest player
        Player player = getClosestPlayer();
        
        // Change phases by health
        if (health <= 12000 && currentPhase == 1) {
			level.add(new FireParticle(x - random.nextInt(24), y + random.nextInt(24), 9));
			level.add(new FireParticle(x + random.nextInt(24), y - random.nextInt(24), 9));
		
			level.add(new FireParticle(x - random.nextInt(24), y - random.nextInt(24), 9));
			level.add(new FireParticle(x + random.nextInt(24), y + random.nextInt(24), 9));
            
        	// Change to phase 2
        	Sound.playAt("eyeQueenChangePhase", this.x, this.y);
        	currentPhase = 2;
        }
        
        if (health <= 6000 && currentPhase == 2) {
			level.add(new FireParticle(x - random.nextInt(24), y + random.nextInt(24), 9));
			level.add(new FireParticle(x + random.nextInt(24), y - random.nextInt(24), 9));
		
			level.add(new FireParticle(x - random.nextInt(24), y - random.nextInt(24), 9));
			level.add(new FireParticle(x + random.nextInt(24), y + random.nextInt(24), 9));
            
        	// Change to phase 3
			Sound.playAt("eyeQueenChangePhase", this.x, this.y);
        	currentPhase = 3;
        }

        // If a tile obstructs the path, we hit it
        if ((tickTime / 2 % 16 == 0)) {
			Direction attackDir = dir; // Make the attack direction equal the current direction
			Point interactionTile = getInteractionTile(); // Attempts to hurt the tile in the appropriate direction.
			
			// Check if tile is in bounds of the map.
			if ((interactionTile.x >= 0 && interactionTile.y >= 0) && (interactionTile.x < level.w && interactionTile.y < level.h)) {
				Tile targetTile = level.getTile(interactionTile.x, interactionTile.y);
				
				// Any solid tiles (except some) will be damaged
				if ((targetTile != Tiles.get("Hard Rock") || targetTile != Tiles.get("Summon Altar")) && !targetTile.mayPass(level, interactionTile.x, interactionTile.y, this)) { 
					
					// Hit the tile
					targetTile.hurt(level, interactionTile.x, interactionTile.y, this, random.nextInt(10) + 16, attackDir);
					
					// Add fire particles :)
					for (int i = 0; i < random.nextInt(8) + 1; i++) {
						level.add(new FireParticle(interactionTile.x - 8 + random.nextInt(16), interactionTile.y - 6 + random.nextInt(12)));
					}
				}
			}
        }
        
        
        if (attackDelay > 0) {
            xa = ya = 0;
            int dir = ((attackDelay - 45) / 4) % 4; // the direction of attack.
            dir = ((dir * 2) % 4) + (dir / 2); // direction attack changes
            
            if (attackDelay < 45) {
            	dir = 0; // direction is reset, if attackDelay is less than 45; prepping for attack.
            }

            this.dir = Direction.getDirection(dir);
            
            if (this.deathing && tickTime % 2 == 0) {
				level.add(new FireParticle(x - random.nextInt(24), y + random.nextInt(24), random.nextInt(9)));
				level.add(new FireParticle(x + random.nextInt(24), y - random.nextInt(24), random.nextInt(9)));
			
				level.add(new FireParticle(x - random.nextInt(24), y - random.nextInt(24), random.nextInt(9)));
				level.add(new FireParticle(x + random.nextInt(24), y + random.nextInt(24), random.nextInt(9)));
            }

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
            
        } else if (attackTime > 0 && currentPhase == 1 && !this.deathing) {
            xa = ya = 0;
            attackTime = (int) (attackTime * 0.92); // attackTime will decrease by 7% every time.
            double dir = attackTime * 0.25 * (attackTime % 2 * 2 - 1); // assigns a local direction variable from the attack time.
            double speed = (0.7) + attackType * 0.2; // speed is dependent on the attackType. (higher attackType, faster speeds)
            level.add(new Fireball(this, Math.cos(dir) * speed, Math.sin(dir) * speed, 2, random.nextInt(2) + 1)); // adds a fireball entity with the cosine and sine of dir times speed.

            Sound.playAt("wizardSpawnSpark", this.x, this.y);
            
            return; // skips the rest of the code (attackTime was > 0; ie we're attacking.)
            
        } else if (attackTime > 0 && currentPhase >= 2 && !this.deathing) {
            xa = ya = 0;
            attackTime = (int) (attackTime * 0.92);
            double dir = attackTime * 0.25 * (attackTime % 2 * 2 - 1);
            double speed = (0.7) + attackType * 0.2;
            level.add(new Fireball(this, Math.cos(dir) * speed, Math.sin(dir) * speed, 3, random.nextInt(3) + 1));

            Sound.playAt("wizardSpawnSpark", this.x, this.y);
            
            return;
        }
        
        if (player != null && randomWalkTime == 0 && (tickTime % (200 + random.nextInt(200)) == 0)) {
            int xd = player.x - x; // x distance to player
            int yd = player.y - y; // y distance to player
            if (xd * xd + yd * yd < 50 * 50 && attackDelay == 0 && attackTime == 0) {
            	if (currentPhase == 1) {
            		attackDelay = 60 * 6; // ...then set attackDelay to 360 (6 seconds at default 60 ticks/sec)
            	} else {
            		attackDelay = 60 * 3; // ...then set attackDelay to 180 (3 seconds at default 60 ticks/sec)
            	}
            }
        }
        
        
        // Death animation
        if (this.health <= 100 && !this.deathing) {
        	this.health = 200;
        	this.deathing = true;
        	Sound.playAt("eyeQueenDeath", this.x, this.y);
        	tickTime = 0;
        }
        
        if (this.deathing) {
        	if (this.health <= 200) this.health = 200;
        	
        	attackDelay = 60 * 6;
        	attackTime = 60 * 6;
        	
        	if (tickTime >= 50) {
        		this.deathing = false;
        		this.die();
        	}
        }
        
    }

    @Override
    public void render(Screen screen) {
    	if (attackDelay > 0) {
    		if (tickTime / 4 % 2 == 0) {
				super.render(screen, Color.RED, deathing);
				return;
    		}
		}
			
		super.render(screen);
    }
    
    @Override
    protected void touchedBy(Entity entity) {
        if (entity instanceof Player) {
            ((Player) entity).hurt(this, random.nextInt(2) + 2);
        }
    }

    public void die() {
        
        active = false;
        entity = null;
        
        if (!beaten) {
        	Updater.notifyAll("The Eye Queen was beaten!", 200);
        	beaten = true;
        }
        
        dropItem(25, 50, Items.get("emerald"));
        dropItem(1, 1, Items.get("Grimoire"));
        
        super.die();
    }
    
	@Override
	public int getLightRadius() {
		return currentPhase == 3 ? 5: 4;
	}

}