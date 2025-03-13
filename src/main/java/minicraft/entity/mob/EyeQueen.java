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

public class EyeQueen extends GiantBossMob {
    private static MobSprite[][][] spritesMain;
 
    static {
        // Compile the main sprites for the EyeQueen's animations.
        spritesMain = new MobSprite[2][4][2];
        for (int i = 0; i < 1; i++) {
            MobSprite[][] list = MobSprite.compileMobSpriteAnimations(60, 0, 4, 4);
            spritesMain[i] = list;
        }
    }
    
    /// THIS IS A MESS, BUT IT'S OKAY LOL

    // Global static variables to track the EyeQueen's state
    public static boolean beaten = false;
    public static boolean active = true;
    public static EyeQueen entity = null;

    // Instance variables for boss behavior and attacks
    private boolean deathing = false;
    private int attackDelay = 0;
    private int attackTime = 0;
    private int attackType = 0;
    private int currentPhase = 0;
    private boolean randomAttack = false;
    public static int length; // Used to update the boss life bar

    // Variables for composite attacks
    private int compositeAttackTimer = 400; // Initial cooldown in ticks
    private int compositeAttackSubTimer = 0;
    private int tripleAttackCounter = 0;
    private boolean isCompositeAttack = false;
    // Composite attack states:
    // 1 = triple attack, 2 = prolonged laser beam, 3 = teleport impact attack
    private int compositeAttackStep = 0;

    // Variables for accumulated damage (approx. 10 sec = 600 ticks)
    private int damageAccumulated = 0;
    private int damageTimer = 0;

    // Variables for movement control and dash
    private int stuckTimer = 0;
    private int lastX, lastY;
    private int dashCooldown = 0; // 20 sec = 1200 ticks
    private boolean dashUsed = false;
    private boolean chargedAttackPerformed = false; // Activated when a charged attack begins

    // Constructor to initialize the EyeQueen boss
    public EyeQueen(int lvl) {
        super(5, spritesMain, 10000, false, 16 * 8, -1, 10, 1000);
        active = true;
        entity = this;
        beaten = false;
        walkTime = 2;
        fartick = true;
        currentPhase = 1;
        deathing = false;
        setHitboxSize(6, 6);
        lastX = x;
        lastY = y;
    }

    @Override
    public boolean canSwim() {
        return true;
    }
    
    public boolean canWool() {
        return true;
    }
    
    // Get the tile in front of the boss for interaction
    private Point getInteractionTile() {
        int ix = this.x;
        int iy = this.y - 2;
        ix += dir.getX() * 18;
        iy += dir.getY() * 18;
        return new Point(ix >> 4, iy >> 4);
    }
    
    // Finds a valid teleport location within a 'range' of tiles around a center point.
    // The chosen point must have a 3x3 area free of solid tiles.
    private Point findTeleportLocation(int centerTileX, int centerTileY, int range) {
        for (int i = 0; i < 100; i++) {
            int offsetX = random.nextInt(range * 2 + 1) - range;
            int offsetY = random.nextInt(range * 2 + 1) - range;
            
            int newTileX = centerTileX + offsetX;
            int newTileY = centerTileY + offsetY;
            
            if (newTileX < 0 || newTileY < 0 || newTileX >= level.w || newTileY >= level.h) continue;
            boolean free = true;
            
            // Check the surrounding 3x3 area for passable tiles
            for (int dx = -1; dx <= 1 && free; dx++) {
                for (int dy = -1; dy <= 1 && free; dy++) {
                    int tx = newTileX + dx, ty = newTileY + dy;
                    
                    if (tx < 0 || ty < 0 || tx >= level.w || ty >= level.h) {
                        free = false;
                    } else {
                        Tile t = level.getTile(tx, ty);
                        if (!t.mayPass(level, tx, ty, this)) free = false;
                    }
                }
            }
            if (free) return new Point(newTileX, newTileY);
        }
        return null;
    }
    
    // Checks if there is any solid tile in the 3x3 area around a given tile
    private boolean hasSolidTileAround(int tileX, int tileY) {
        for (int dx = -1; dx <= 1; dx++) {
            for (int dy = -1; dy <= 1; dy++) {
                int tx = tileX + dx, ty = tileY + dy;
                if (tx < 0 || ty < 0 || tx >= level.w || ty >= level.h) continue;
                Tile t = level.getTile(tx, ty);
                if (!t.mayPass(level, tx, ty, this)) return true;
            }
        }
        return false;
    }
    
    // Teleports the boss to a location near the player (within 'range' tiles)
    // ensuring that the chosen location and its surrounding 3x3 area are valid.
    private void teleportNearPlayer(Player player, int range) {
        int playerTileX = player.x >> 4;
        int playerTileY = player.y >> 4;
        Point tp = findTeleportLocation(playerTileX, playerTileY, range);
        if (tp != null) {
            this.x = tp.x << 4;
            this.y = tp.y << 4;
            Sound.playAt("eyeQueenPuff", this.x, this.y);
        }
    }
    
    // Teleports the boss directly in front of the player based on the player's direction.
    private void teleportInFrontOfPlayer(Player player) {
        int playerTileX = player.x >> 4;
        int playerTileY = player.y >> 4;
        int offsetX = 0, offsetY = 0;
        
        // Assume player.dir represents the player's current facing direction.
        switch(player.dir) {
            case UP:    offsetY =  1; break;
            case DOWN:  offsetY = -1; break;
            case LEFT:  offsetX =  1; break;
            case RIGHT: offsetX = -1; break;
        }
        
        Point tp = findTeleportLocation(playerTileX + offsetX, playerTileY + offsetY, 1);
        if (tp != null) {
            this.x = tp.x << 4;
            this.y = tp.y << 4;
            Sound.playAt("eyeQueenPuff", this.x, this.y);
        }
    }
    
    // Executes a dash attack: if the player is very close, inflicts damage and teleports
    // the boss up to 5 tiles away from the player.
    private void performDashAttack(Player player) {
        int damage = (currentPhase == 1 ? 3 : 6);
        player.hurt(this, damage);
        
        // Teleport the boss far from the player (maximum 5 tiles)
        int playerTileX = player.x >> 4;
        int playerTileY = player.y >> 4;
            
        Point tp = findTeleportLocation(playerTileX, playerTileY, 5);
        if (tp != null) {
            this.x = tp.x << 4;
            this.y = tp.y << 4;
        }
        
        Sound.playAt("dash", this.x, this.y);
        dashCooldown = 1200; // 20 seconds cooldown
        dashUsed = true;
    }
    
    // When the boss is hurt by another mob, process damage and possibly trigger a composite attack.
    @Override
    public void hurt(Mob mob, int damage) {
        // Do not allow damage during the death animation or if health would drop below 100
        if (deathing || health - damage < 100) return;

        super.hurt(mob, damage);
        damageAccumulated += damage;
        damageTimer = 0;
        
        // If damage accumulated exceeds 100 and no composite attack is in progress, trigger a charged composite attack.
        if (damageAccumulated >= 100 && !isCompositeAttack) {
            chargedAttackPerformed = true;
            
            // Randomly choose between laser (state 2) or teleport attack (state 3)
            if (random.nextDouble() < 0.5) {
                compositeAttackStep = 2;
            } else {
                compositeAttackStep = 3;
            }
            
            compositeAttackSubTimer = 0;
            tripleAttackCounter = 0;
            isCompositeAttack = true;
        }
    }
    
    @Override
    public void tick() {
        super.tick();
        
        // Update the boss life bar length based on current health.
        length = health / (maxHealth / 100);
        Player player = getClosestPlayer();
        
        // Change phase based on health thresholds.
        if (health <= 8334 && currentPhase == 1) {
            // Spawn fire particles to show phase transition effects.
            level.add(new FireParticle(x - random.nextInt(24), y + random.nextInt(24), 9));
            level.add(new FireParticle(x + random.nextInt(24), y - random.nextInt(24), 9));
            level.add(new FireParticle(x - random.nextInt(24), y - random.nextInt(24), 9));
            level.add(new FireParticle(x + random.nextInt(24), y + random.nextInt(24), 9));
            Sound.playAt("eyeQueenChangePhase", this.x, this.y);
            currentPhase = 2;
            speed = 2;
        }
        
        if (health <= 4168 && currentPhase == 2) {
            // Spawn additional particles and update phase.
            level.add(new FireParticle(x - random.nextInt(24), y + random.nextInt(24), 9));
            level.add(new FireParticle(x + random.nextInt(24), y - random.nextInt(24), 9));
            level.add(new FireParticle(x - random.nextInt(24), y - random.nextInt(24), 9));
            level.add(new FireParticle(x + random.nextInt(24), y + random.nextInt(24), 9));
            Sound.playAt("eyeQueenChangePhase", this.x, this.y);
            currentPhase = 3;
        }
        
        // Update movement: Check if the boss has moved
        if (Math.abs(x - lastX) < 2 && Math.abs(y - lastY) < 2) {
            stuckTimer++;
        } else {
            stuckTimer = 0;
            lastX = x;
            lastY = y;
        }
        
        // If the boss is stuck for 3 seconds and there are solid tiles nearby,
        // teleport the boss closer to the player (within 4 tiles).
        if (stuckTimer >= 120 && attackDelay == 0 && attackTime == 0 && !isCompositeAttack && player != null) {
            int tileX = x >> 4, tileY = y >> 4;
            if (hasSolidTileAround(tileX, tileY)) {
                teleportNearPlayer(player, 4);
                stuckTimer = 0;
            }
        }
        
        // Decrement dash cooldown if active.
        if (dashCooldown > 0) dashCooldown--;
        
        // If a charged attack has been performed, check if conditions are met for a dash.
        if (chargedAttackPerformed && !dashUsed && dashCooldown == 0 && player != null) {
            int dx = (player.x - this.x), dy = (player.y - this.y);
            
            // Assume 2 tiles = 32 pixels (16 pixels per tile)
            if (dx * dx + dy * dy < (32 * 32)) {
                // Chance to perform dash depends on current phase.
                double chance = (currentPhase == 1 ? 0.2 : (currentPhase == 2 ? 0.3 : 0.4));
                if (random.nextDouble() < chance) {
                    performDashAttack(player);
                }
            }
        }
        
        // If the player moves too far away (more than 12 tiles) during a charged attack,
        // teleport the boss in front of the player.
        if (player != null && isCompositeAttack && (attackDelay > 0 || attackTime > 0)) {
            int dx = (player.x - this.x), dy = (player.y - this.y);
            if (dx * dx + dy * dy > (192 * 192)) { // Approximately 12 tiles
                teleportInFrontOfPlayer(player);
            }
        }
        
        // PROCESSING COMPOSITE ATTACK BEHAVIOR
        if (isCompositeAttack) {
        	
            // State 1: Triple attack
            if (compositeAttackStep == 1) {
                if (compositeAttackSubTimer <= 0) {
                    if (tripleAttackCounter > 0) {
                    	
                        if (player != null) {
                            // Calculate an angle towards the player with a small random deviation.
                            double baseAngle = Math.atan2(player.y - this.y, player.x - this.x);
                            double deviation = (random.nextDouble() - 0.5) * (Math.PI / 12);
                            double angle = baseAngle + deviation;
                            double attackSpeed = 1.0 + (currentPhase - 1) * 0.5;
                            level.add(new Fireball(this, Math.cos(angle) * attackSpeed, Math.sin(angle) * attackSpeed, 3, random.nextInt(2) + 1));
                            Sound.playAt("wizardSpawnSpark", this.x, this.y);
                        }
                        
                        tripleAttackCounter--;
                        compositeAttackSubTimer = 20;
                    } else {
                    	
                        // Switch to the next composite attack state:
                        // If teleport attack was chosen, keep it; otherwise, switch to laser beam.
                        if (compositeAttackStep != 3) {
                            compositeAttackStep = 2;
                        }
                        compositeAttackSubTimer = 0;
                        
                    }
                    
                } else {
                    compositeAttackSubTimer--;
                }
                
                return;
            }
            
            // State 2: Prolonged laser beam attack
            else if (compositeAttackStep == 2) {
                // Over several ticks, fire projectiles in sequence to simulate a laser beam.
                if (compositeAttackSubTimer <= 0) {
                    if (player != null) {
                        for (int i = 0; i < 3; i++) { // Fire in bursts each cycle.
                            double baseAngle = Math.atan2(player.y - this.y, player.x - this.x);
                            double deviation = (random.nextDouble() - 0.5) * (Math.PI / 12);
                            double angle = baseAngle + deviation;
                            double attackSpeed = 1.5 + (currentPhase - 1) * 0.5;
                            level.add(new Fireball(this, Math.cos(angle) * attackSpeed, Math.sin(angle) * attackSpeed, 3, random.nextInt(2) + 1));
                            Sound.playAt("wizardSpawnSpark", this.x, this.y);
                        }
                    }
                    compositeAttackSubTimer = 10; // Delay between bursts.
                } else {
                    compositeAttackSubTimer--;
                }
                
                // Total duration of the laser beam lasts 60 ticks (1 second).
                if (++attackTime > 60) {
                    isCompositeAttack = false;
                    compositeAttackStep = 0;
                    compositeAttackTimer = (currentPhase == 1 ? 400 : (currentPhase == 2 ? 300 : 200));
                    damageAccumulated = 0;
                    damageTimer = 0;
                    chargedAttackPerformed = false;
                    dashUsed = false;
                    attackTime = 0;
                }
                return;
            }
            
            // State 3: Direct teleport attack
            else if (compositeAttackStep == 3) {
                if (player != null) {
                    // Teleport the boss in front of the player based on the player's direction.
                    teleportInFrontOfPlayer(player);
                    // Launch a single, powerful projectile upon arrival.
                    double baseAngle = Math.atan2(player.y - this.y, player.x - this.x);
                    double attackSpeed = 2.0 + (currentPhase - 1) * 0.5;
                    level.add(new Fireball(this, Math.cos(baseAngle) * attackSpeed, Math.sin(baseAngle) * attackSpeed, 3, random.nextInt(2) + 2));
                    Sound.playAt("wizardSpawnSpark", this.x, this.y);
                }
                
                // Reset composite attack parameters.
                isCompositeAttack = false;
                compositeAttackStep = 0;
                compositeAttackTimer = (currentPhase == 1 ? 400 : (currentPhase == 2 ? 300 : 200));
                damageAccumulated = 0;
                damageTimer = 0;
                chargedAttackPerformed = false;
                dashUsed = false;
                return;
            }
        } else {
            // If not in a composite attack, decrement the composite attack timer
            if (compositeAttackTimer > 0) compositeAttackTimer--;
            
            // Start a composite attack if conditions are met.
            if (!isCompositeAttack && compositeAttackTimer <= 0 && attackDelay == 0 && attackTime == 0) {
                isCompositeAttack = true;
                
                // Random decision: 50% chance to initiate triple attack (state 1) or direct teleport attack (state 3)
                if (random.nextDouble() < 0.5) {
                    compositeAttackStep = 1;
                    tripleAttackCounter = 3;
                    compositeAttackSubTimer = 0;
                } else {
                    compositeAttackStep = 3;
                    compositeAttackSubTimer = 0;
                }
            }
        }
        
        // Conventional (non-composite) attacks handling.
        if (attackDelay > 0) {
            xa = ya = 0;
            int dirVal = ((attackDelay - 45) / 4) % 4;
            dirVal = ((dirVal * 2) % 4) + (dirVal / 2);
            
            if (attackDelay < 45) dirVal = 0;
            
            this.dir = Direction.getDirection(dirVal);
            
            // Add death animation particles if in the death animation phase.
            if (this.deathing && tickTime % 2 == 0) {
                level.add(new FireParticle(x - random.nextInt(24), y + random.nextInt(24), random.nextInt(9)));
                level.add(new FireParticle(x + random.nextInt(24), y - random.nextInt(24), random.nextInt(9)));
                level.add(new FireParticle(x - random.nextInt(24), y - random.nextInt(24), random.nextInt(9)));
                level.add(new FireParticle(x + random.nextInt(24), y + random.nextInt(9), random.nextInt(9)));
            }
            
            // Fire a projectile during the charging period.
            if (attackDelay > 4 && attackDelay < 40) {
                level.add(new Fireball(this, Math.sin(attackDelay - 45) * 1.2, Math.cos(attackDelay - 45) * 1.2, 2, random.nextInt(2) + 1));
                Sound.playAt("wizardSpawnSpark", this.x, this.y);
            }
            
            attackDelay--;
            if (attackDelay == 0) {
                attackType = 0;
                
                // Adjust attack type based on remaining health.
                if (health < maxHealth / 2) attackType = 1;
                if (health < maxHealth / 10) attackType = 2;
                
                // Array containing possible attack durations for each phase.
                int[][] attackTimeOptions = {
                    { 160, 146, 125 },
                    { 1080, 1050, 1030 },
                    { 2120, 2200, 2400 }
                };
                attackTime = attackTimeOptions[currentPhase - 1][random.nextInt(3)] * 2;
            }
            return;
            
        } else if (attackTime > 0 && currentPhase == 1 && !this.deathing) {
            // Phase 1 attack behavior: gradually reduce attackTime and fire periodic projectiles.
            xa = ya = 0;
            attackTime = (int)(attackTime * 0.92);
            double dirVal = attackTime * 0.25 * ((attackTime % 2) * 2 - 1);
            double spd = 0.7 + attackType * 0.2;
            level.add(new Fireball(this, Math.cos(dirVal) * spd, Math.sin(dirVal) * spd, 2, random.nextInt(2) + 1));
            Sound.playAt("wizardSpawnSpark", this.x, this.y);
            return;
            
        } else if (attackTime > 0 && currentPhase >= 2 && !this.deathing) {
            // Phase 2 and above attack behavior: similar to phase 1 but with stronger projectiles.
            xa = ya = 0;
            attackTime = (int)(attackTime * 0.92);
            double dirVal = attackTime * 0.25 * ((attackTime % 2) * 2 - 1);
            double spd = 0.7 + attackType * 0.2;
            level.add(new Fireball(this, Math.cos(dirVal) * spd, Math.sin(dirVal) * spd, 3, random.nextInt(3) + 1));
            Sound.playAt("wizardSpawnSpark", this.x, this.y);
            return;
        }
        
        // Movement: if the player is nearby and the boss is not attacking,
        // adjust behavior to start an attack.
        if (player != null && randomWalkTime == 0 && (tickTime % (200 + random.nextInt(200)) == 0)) {
            int xd = player.x - x;
            int yd = player.y - y;
            if (xd * xd + yd * yd < 50 * 50 && attackDelay == 0 && attackTime == 0) {
                if (currentPhase == 1) {
                    attackDelay = 60 * 6;
                } else {
                    attackDelay = 60 * 3;
                }
            }
        }
        
        // Death animation: When health falls below or equals 100, start death sequence.
        if (this.health <= 100 && !this.deathing) {
            this.health = 100;
            this.deathing = true;
            Sound.playAt("eyeQueenDeath", this.x, this.y);
            tickTime = 0;
        }
        
        if (this.deathing) {
            if (this.health <= 100) this.health = 100;
            attackDelay = 42 * 6;
            attackTime = 42 * 6;
            if (tickTime >= 30) {
                this.deathing = false;
                this.die();
            }
        }
        
        // Reset accumulated damage if 10 seconds (600 ticks) pass without additional damage.
        if (damageAccumulated > 0) {
            damageTimer++;
            if (damageTimer > 600) {
                damageAccumulated = 0;
                damageTimer = 0;
            }
        }
    }
    
    @Override
    public void render(Screen screen) {
        // Render with a red tint during attack delay to indicate charging
        if (attackDelay > 0) {
            if (tickTime / 4 % 2 == 0) {
                super.render(screen, Color.RED, deathing);
                return;
            }
        }
        super.render(screen);
    }
    
    // Called when another entity touches the boss. If it's a player, inflict damage.
    @Override
    protected void touchedBy(Entity entity) {
        if (entity instanceof Player) {
            ((Player) entity).hurt(this, random.nextInt(2) + 2);
        }
    }
    
    // Method to handle the boss's death. Notifies players and drops items.
    public void die() {
        active = false;
        entity = null;
        
        if (!beaten) {
        	beaten = true;
            Updater.notifyAll("The Eye Queen was beaten!", 200);
        }
        
        dropItem(25, 50, Items.get("Emerald"));
        dropItem(1, 1, Items.get("Grimoire"));
        super.die();
    }
    
    // Returns the light radius based on the current phase.
    @Override
    public int getLightRadius() {
        return currentPhase == 3 ? 5 : 4;
    }
}
