package minicraft.entity.mob;

import minicraft.core.Game;
import minicraft.core.Updater;
import minicraft.core.io.Settings;
import minicraft.core.io.Sound;
import minicraft.entity.Direction;
import minicraft.entity.Entity;
import minicraft.entity.Spark;
import minicraft.graphic.Color;
import minicraft.graphic.Font;
import minicraft.graphic.MobSprite;
import minicraft.graphic.Screen;
import minicraft.item.Items;
import minicraft.saveload.Save;
import minicraft.screen.AchievementsDisplay;

public class AirWizard extends EnemyMob {

	private static MobSprite[][][] spritesMain;

    private static MobSprite[][][] spritesFirstPahse;
    private static MobSprite[][][] spritesSecondPhase;
    private static MobSprite[][][] spritesThirdPhase;
    
    static {
    	// FIXME: The Air Wizard skin sprites should change beetween phases
    	spritesMain = new MobSprite[2][4][2];
        spritesFirstPahse = new MobSprite[2][4][2];
        spritesSecondPhase = new MobSprite[2][4][2];
        spritesThirdPhase = new MobSprite[2][4][2];
        
        for (int i = 0; i < 2; i++) { // Normal wizard
            MobSprite[][] list = MobSprite.compileMobSpriteAnimations(20, 10 + (i * 2));
            spritesFirstPahse[i] = list;
        }
        
        for (int i = 0; i < 2; i++) { // Angry wizard
            MobSprite[][] list = MobSprite.compileMobSpriteAnimations(20, 14 + (i * 2));
            spritesSecondPhase[i] = list;
        }
        
        for (int i = 0; i < 2; i++) { // Furius wizard
            MobSprite[][] list = MobSprite.compileMobSpriteAnimations(30, 10 + (i * 2));
            spritesThirdPhase[i] = list;
        }
        
        spritesMain = spritesFirstPahse; // Start on first phase
    }

    public static boolean beaten = false;
    public static boolean active = true;
    public static AirWizard entity = null;

    public boolean secondform;
    private int currentPhase = 0;
    private int attackDelay = 0;
    private int attackTime = 0;
    private int attackType = 0;
    public static int length;

    /**
     * Constructor for the AirWizard. Will spawn as secondary form if lvl>1.
     * 
     * @param lvl The AirWizard level.
     */
    public AirWizard(int lvl) {
        this(lvl > 1);
    }

    /**
     * Constructor for the AirWizard.
     * 
     * @param secondform determines if the wizard should be level 2 or 1.
     */
    public AirWizard(boolean secondform) {
        super(secondform ? 2 : 1, spritesMain, secondform ? 18000 : 10500, false, 16 * 8, -1, 10, 50);
        
        active = true;
        entity = this;
        currentPhase = 1;

        this.secondform = secondform;
        if (secondform) speed = 3;
        if (!secondform) {
        	beaten = false; // <- needed for new worlds :(
        	speed = 2;
        }
        
        walkTime = 2;
    }

    @Override
    public boolean canSwim() {
        return secondform;
    }

    @Override
    public boolean canWool() {
        return true;
    }

    @Override
    public void tick() {
        super.tick();
        
        length = health / (maxHealth / 100);
        
        if (Game.isMode("Creative")) {
            return; // Should not attack if game mode is creative
        }
       
        // Change phases by health
        if (health <= (secondform ? 12000 : 7000) && currentPhase == 1) {
        	// change to phase 2
        	Sound.airWizardChangePhase.playOnLevel(this.x, this.y);
        	spritesMain = spritesSecondPhase;
        	currentPhase = 2;
        }
        
        if (health <= (secondform ? 6000 : 3500) && currentPhase == 2) {
        	// change to phase 3
        	Sound.airWizardChangePhase.playOnLevel(this.x, this.y);
        	spritesMain = spritesThirdPhase;
        	currentPhase = 3;
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
                attackTime = attackTimeOptions[currentPhase - 1][random.nextInt(3)] * (secondform ? 3 : 2);
            }
            return; // skips the rest of the code (attackDelay must have been > 0)
            
        } else if (attackTime > 0 && currentPhase == 1) { // First phase sparks attack
            xa = ya = 0;
            attackTime = (int) (attackTime * 0.92); // attackTime will decrease by 7% every time.
            double dir = attackTime * 0.25 * (attackTime % 2 * 2 - 1); // assigns a local direction variable from the attack time.
            double speed = (secondform ? 1.2 : 0.7) + attackType * 0.2; // speed is dependent on the attackType. (higher attackType, faster speeds)
            
            if (random.nextBoolean() && health <= (secondform ? 15000 : 8750)) {
            	level.add(new Spark(this, Math.cos(dir) * speed, Math.sin(dir) * speed, 2)); // adds a spark entity with the cosine and sine of dir times speed.
            } else {
            	level.add(new Spark(this, Math.cos(dir) * speed, Math.sin(dir) * speed, 1)); // adds a spark entity with the cosine and sine of dir times speed.
            }
            
            Sound.airWizardSpawnSpark.playOnLevel(this.x, this.y);
            
            return; // skips the rest of the code (attackTime was > 0; ie we're attacking.)
        }
        
        // Second phase sparks attack
        else if (attackTime > 0 && currentPhase == 2) {
            xa = ya = 0;
            attackTime = (int) (attackTime * 0.92);
            double dir = attackTime;
            double speed = (secondform ? 1.2 : 0.7) + attackType * 0.2;
            level.add(new Spark(this, Math.cos(dir) * speed, Math.sin(dir) * speed, 2));
            
            Sound.airWizardSpawnSpark.playOnLevel(this.x, this.y);
            
            return;
        }
        
        // Third phase sparks attack
        else if (attackTime > 0 && currentPhase == 3) {
            xa = ya = 0;
            attackTime *= 0.92;
            double dir = ((double) attackTime) * 0.25d * ((double) (((attackTime % 2) * 2) - 1)); 
            double speed = (secondform ? 1.2 : 0.7) + attackType * 0.2;
            level.add(new Spark(this, Math.cos(dir) * speed, Math.sin(dir) * speed, 3));
            
            Sound.airWizardSpawnSpark.playOnLevel(this.x, this.y);
            
            return;
        }
        
        Player player = getClosestPlayer();
        if (player != null && randomWalkTime == 0) { // if there is a player around, and the walking is not random
            int xd = player.x - x; // the horizontal distance between the player and the air wizard.
            int yd = player.y - y; // the vertical distance between the player and the air wizard.
            
            if (xd*xd + yd*yd < 16*16 * 2*2) {
                /// Move away from the player if less than 2 blocks away
                xa = 0; // accelerations
                ya = 0;
                
                // these four statements basically just find which direction is away from the player:
                if (xd < 0) xa = +1;
                if (xd > 0) xa = -1;
                if (yd < 0) ya = +1;
                if (yd > 0) ya = -1;
                
            } else if (xd * xd + yd * yd > 16*16 * 15*15) { // 15 squares away
                /// drags the airwizard to the player, maintaining relative position.
                double hypot = Math.sqrt(xd * xd + yd * yd);
                int newxd = (int) (xd * Math.sqrt(16 * 16 * 15 * 15) / hypot);
                int newyd = (int) (yd * Math.sqrt(16 * 16 * 15 * 15) / hypot);
                x = player.x - newxd;
                y = player.y - newyd;
            }
        }
        
        if (tickTime % 4 != 0 && currentPhase != 1) {
            speed = 1;
        }
        
        if (player != null && randomWalkTime == 0) {
            int xd = player.x - x; // x dist to player
            int yd = player.y - y; // y dist to player
            if (random.nextInt(4) == 0 && xd * xd + yd * yd < 50 * 50 && attackDelay == 0 && attackTime == 0) {
            	if (currentPhase == 1) {
            		attackDelay = 60 * 4; // ...then set attackDelay to 240 (4 seconds at default 60 ticks/sec)
            	} else {
            		attackDelay = 60 * 2; // ...then set attackDelay to 120 (2 seconds at default 60 ticks/sec)
            	}
            }
        }
         
    }

    @Override
    public void doHurt(int damage, Direction attackDir) {
        super.doHurt(damage, attackDir);
        if (attackDelay == 0 && attackTime == 0) {
            attackDelay = 60 * 2 / (currentPhase + 1);
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
            Font.drawBar(screen, (x - Screen.w / 12 + 26), y - 24, length, "testificate");
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
            ((Player) entity).hurt(this, (secondform ? 2 : 1));
        }
    }

    /** What happens when the air wizard dies */
    @Override 
    public void die() {
        Player[] players = level.getPlayers();
        
        // If the player is still here
        if (players.length > 0) {
            for (Player player : players) {
            	// Give the player 100K or 500K points.
                player.addScore((secondform ? 500000 : 100000));
            }
        }

        Sound.airWizardDeath.playOnLevel(this.x, this.y);
        
        level.dropItem(x, y, Items.get("AlAzif"));

        if (!secondform) {  
			// Kill first Air wizard achievement
			AchievementsDisplay.setAchievement("minicraft.achievement.airwizard", true);
	
            if (!beaten) {
            	Updater.notifyAll("The Air Wizard was beaten?", 200);
            	Updater.notifyAll("Unlocked Dungeons!", 200);
            	beaten = true;
            }

            active = false;
            entity = null;

        } else {
			// Kill second Air wizard achievement
			AchievementsDisplay.setAchievement("minicraft.achievement.second_airwizard", true);
			
            if (!Settings.getBoolean("unlockedskin")) {
            	Updater.notifyAll("The Air Wizard was beaten!", 200);
                Updater.notifyAll("A costume lies on the ground...", -200);
            }
            
            active = false;
            entity = null;
            
            // Unlock the Air wizard suit :D
            Settings.set("unlockedskin", true);
        }
        new Save();
        super.die(); // Calls the die() method in EnemyMob.java
    }

    @Override
    public int getMaxLevel() {
        return 2;
    }
    
	@Override
	public int getLightRadius() {
		return secondform ? 3: 2;
	}
}