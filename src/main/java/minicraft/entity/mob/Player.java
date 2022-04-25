package minicraft.entity.mob;

import java.time.LocalDateTime;
import java.time.Month;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import org.jetbrains.annotations.Nullable;
import org.tinylog.Logger;

import minicraft.core.Game;
import minicraft.core.Updater;
import minicraft.core.World;
import minicraft.core.io.InputHandler;
import minicraft.core.io.Settings;
import minicraft.core.io.Sound;
import minicraft.entity.Arrow;
import minicraft.entity.Boat;
import minicraft.entity.ClientTickable;
import minicraft.entity.Direction;
import minicraft.entity.Entity;
import minicraft.entity.ItemEntity;
import minicraft.entity.ItemHolder;
import minicraft.entity.furniture.Bed;
import minicraft.entity.furniture.DeathChest;
import minicraft.entity.furniture.Furniture;
import minicraft.entity.furniture.Tnt;
import minicraft.entity.mob.villager.Cleric;
import minicraft.entity.mob.villager.Librarian;
import minicraft.entity.particle.CloudParticle;
import minicraft.entity.particle.FerrositeParticle;
import minicraft.entity.particle.FireParticle;
import minicraft.entity.particle.Particle;
import minicraft.entity.particle.PotionParticle;
import minicraft.entity.particle.SplashParticle;
import minicraft.entity.particle.TextParticle;
import minicraft.gfx.Color;
import minicraft.gfx.MobSprite;
import minicraft.gfx.Point;
import minicraft.gfx.Rectangle;
import minicraft.gfx.Screen;
import minicraft.item.ArmorItem;
import minicraft.item.FishingData;
import minicraft.item.FishingRodItem;
import minicraft.item.FurnitureItem;
import minicraft.item.Inventory;
import minicraft.item.Item;
import minicraft.item.Items;
import minicraft.item.PotionItem;
import minicraft.item.PotionType;
import minicraft.item.PowerGloveItem;
import minicraft.item.Recipes;
import minicraft.item.StackableItem;
import minicraft.item.TileItem;
import minicraft.item.ToolItem;
import minicraft.item.ToolType;
import minicraft.level.Level;
import minicraft.level.tile.Tile;
import minicraft.level.tile.Tiles;
import minicraft.saveload.Save;
import minicraft.screen.AchievementsDisplay;
import minicraft.screen.CommandsDisplay;
import minicraft.screen.CraftingDisplay;
import minicraft.screen.InfoDisplay;
import minicraft.screen.LoadingDisplay;
import minicraft.screen.PauseDisplay;
import minicraft.screen.PlayerInvDisplay;
import minicraft.screen.WorldSelectDisplay;
import minicraft.util.Vector2;

public class Player extends Mob implements ItemHolder, ClientTickable {
    protected InputHandler input;

    private Random rnd = new Random();

    // Attack static variables
    private static final int playerHurtTime = 30;
    private static final int INTERACT_DIST = 12;
    private static final int ATTACK_DIST = 20;

    // Score static variables
    private static final int mtm = 300; // time given to increase multiplier before it goes back to 1.
    public static final int MAX_MULTIPLIER = 50; // maximum score multiplier.

    public double moveSpeed = 1; // The number of coordinate squares to move; each tile is 16x16.

    // Score variables
    private int score; // the player's score
    private int multipliertime = mtm; // Time left on the current multiplier.
    private int multiplier = 1; // Score multiplier

    // These 2 ints are ints saved from the first spawn - this way the spawn pos is always saved.
    public int spawnx = 0, spawny = 0; // these are stored as tile coordinates, not entity coordinates.
    // public boolean bedSpawn = false;

    // private boolean hasSetHome = false;
    public boolean skinon;
    // private int homeSetX, homeSetY;

    // The maximum stats that the player can have.
    public static final int maxStat = 10;
    public static final int maxHealth = maxStat;
    public static final int maxStamina = maxStat;
    public static final int maxHunger = maxStat;
    public static final int maxArmor = 100;

    // Player sprite variables
    public static MobSprite[][] sprites = MobSprite.compileMobSpriteAnimations(0, 16); // Normal player sprites
    private static MobSprite[][] carrySprites = MobSprite.compileMobSpriteAnimations(0, 18); // The sprites while carrying something.
    private static MobSprite[][] suitSprites = MobSprite.compileMobSpriteAnimations(8, 16); // The "airwizard suit" sprites.
    private static MobSprite[][] carrySuitSprites = MobSprite.compileMobSpriteAnimations(8, 18); // The "airwizard suit" sprites.

    private Inventory inventory;

    public Item activeItem;
    Item attackItem; // attackItem is useful again b/c of the power glove.
    private Item prevItem; // Holds the item held before using the POW glove.

    int attackTime;
    public Direction attackDir;

    private int onStairDelay; // The delay before changing levels.
    private int onFallDelay; // The delay before falling b/c we're on an InfiniteFallTile

    public int hunger, stamina, armor; // The current stats
    public int armorDamageBuffer;

    @Nullable
    public ArmorItem curArmor; // The color/type of armor to be displayed.

    private int staminaRecharge; // The ticks before charging a bolt of the player's stamina
    private static final int maxStaminaRecharge = 10; // Cutoff value for staminaRecharge
    public int staminaRechargeDelay; // The recharge delay ticks when the player uses up their stamina.

    private int hungerStamCnt, stamHungerTicks; // Tiers of hunger penalties before losing a burger.
    private static final int maxHungerTicks = 400; // The cutoff value for stamHungerTicks

    private static final int[] maxHungerStams = { 16, 10, 7, 5 }; // hungerStamCnt required to lose a burger.
    private static final int[] hungerTickCount = { 160, 120, 30, 10 }; // Ticks before decrementing stamHungerTicks.
    private static final int[] hungerStepCount = { 32, 8, 3, 1 }; // Steps before decrementing stamHungerTicks.
    private static final int[] minStarveHealth = { 7, 5, 3, 0 }; // min hearts required for hunger to hurt you.

    private int stepCount; // Used to penalize hunger for movement.
    private int hungerChargeDelay; // The delay between each time the hunger bar increases your health
    private int hungerStarveDelay; // The delay between each time the hunger bar decreases your health

    public HashMap<PotionType, Integer> potioneffects; // The potion effects currently applied to the player
    public boolean showpotioneffects; // Whether to display the current potion effects on screen
    private int cooldowninfo; // Prevents you from toggling the info pane on and off super fast.
    private int regentick; // Counts time between each time the regen potion effect heals you.
    private int healtick; // Used for the heal potion healing animation

    // private final int acs = 25; // Default ("start") arrow count
    public int shirtColor = Color.get(1, 51, 51, 0); // Player shirt color.

    public boolean isFishing = false;
    public int maxFishingTicks = 120;
    public int fishingTicks = maxFishingTicks;
    public int fishingLevel;
    
    public boolean playerBurning = false;
    private boolean fallWarn = false;
    private int burnTime = 0;

    // Note: the player's health & max health are inherited from Mob.java

    public String getDebugHunger() {
        return hungerStamCnt + "_" + stamHungerTicks;
    }

    public Player(@Nullable Player previousInstance, InputHandler input) {
        super(sprites, Player.maxHealth);
        x = 24;
        y = 24;

        this.input = input;
        inventory = new Inventory() {
            @Override
            public void add(int idx, Item item) {
                if (Game.isMode("creative")) {
                    if (count(item) > 0) return;
                    
                    item = item.clone();
                    
                    if (item instanceof StackableItem) ((StackableItem) item).count = 1;
                }
                super.add(idx, item);
            }

            @Override
            public Item remove(int idx) {
                if (Game.isMode("creative")) {
                    Item cur = get(idx);
                    if (cur instanceof StackableItem) ((StackableItem) cur).count = 1;
                    if (count(cur) == 1) {
                        super.remove(idx);
                        super.add(0, cur);
                        return cur.clone();
                    }
                }
                return super.remove(idx);
            }
        };

        // if(previousInstance == null)
        // inventory.add(Items.arrowItem, acs);

        potioneffects = new HashMap<>();
        showpotioneffects = true;

        cooldowninfo = 0;
        regentick = 0;
        healtick = 0;

        attackDir = dir;
        armor = 0;
        curArmor = null;
        armorDamageBuffer = 0;
        stamina = maxStamina;
        hunger = maxHunger;

        hungerStamCnt = maxHungerStams[Settings.getIdx("diff")];
        stamHungerTicks = maxHungerTicks;

        if (Game.isMode("creative")) {
            Items.fillCreativeInv(inventory);
        }

        if (previousInstance != null) {
            spawnx = previousInstance.spawnx;
            spawny = previousInstance.spawny;
        }
    }

    public int getMultiplier() {
        return Game.isMode("score") ? multiplier : 1;
    }

    void resetMultiplier() {
        multiplier = 1;
        multipliertime = mtm;
    }

    public void addMultiplier(int value) {
        if (!Game.isMode("score")) return;

        multiplier = Math.min(MAX_MULTIPLIER, multiplier + value);
        multipliertime = Math.max(multipliertime, mtm - 5);
    }

    public void tickMultiplier() {
        if ((!Updater.paused) && multiplier > 1) {
            if (multipliertime != 0) multipliertime--;
            if (multipliertime <= 0) resetMultiplier();
        }
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public void addScore(int points) {
        score += points * getMultiplier();
    }

    /**
     * Adds a new potion effect to the player.
     * 
     * @param type     Type of potion.
     * @param duration How long the effect lasts.
     */
    public void addPotionEffect(PotionType type, int duration) {
        potioneffects.put(type, duration);
    }

    /**
     * Adds a potion effect to the player.
     * 
     * @param type Type of effect.
     */
    public void addPotionEffect(PotionType type) {
        addPotionEffect(type, type.duration);
    }

    /**
     * Returns all the potion effects currently affecting the player.
     * 
     * @return all potion effects on the player.
     */
    public HashMap<PotionType, Integer> getPotionEffects() {
        return potioneffects;
    }

    @Override
    public void tick() {
    	
    	// Don't tick player when is death or removed
        if (level == null || isRemoved()) return;

        // Don't tick player when menu is open
        if (Game.getDisplay() != null) return; 

        // Ticks Mob.java
        super.tick(); 
        
        // PLAYER BURNING
        if (playerBurning == true) {
        	if (tickTime / 16 % 4 == 0 && burnTime != 120) {

                int randX = random.nextInt(10);
                int randY = random.nextInt(9);

                level.add(new FireParticle(x - 4 + randX, y - 4 + randY));

                this.hurt(this, 1);
                burnTime++;
        	}
        } else {
        	burnTime = 0;
        }
        
        // if touch water extinguish the fire
    	if (level.getTile(x / 16, y / 16) == Tiles.get("water")) {
        	burnTime = 0;
        	playerBurning = false;
    	}
   
    	// if burntime is equals to 120 stop
        if (burnTime == 120) {
        	burnTime = 0;
        	playerBurning  = false;
        }

        tickMultiplier();

        if (potioneffects.size() > 0 && !Bed.inBed(this)) {

            // Add potion particles...
            int randY = rnd.nextInt(9);

            if (random.nextInt(18) == 9) {
                level.add(new PotionParticle(x - 1, y - 23 + randY));
            }

            for (PotionType potionType : potioneffects.keySet().toArray(new PotionType[0])) {
                if (potioneffects.get(potionType) <= 1) // if time is zero (going to be set to 0 in a moment)...
                    PotionItem.applyPotion(this, potionType, false); // Automatically removes this potion effect.
                else
                    potioneffects.put(potionType, potioneffects.get(potionType) - 1); // Otherwise, replace it with one less.
            }
        }
        
        if (isSwimming()) {
        	if (tickTime / 8 % 2 == 0) {
        		
            	int randX = rnd.nextInt(10);
            	int randY = rnd.nextInt(9);
        		
        		if (level.getTile(x / 16, y / 16) == Tiles.get("water")) {
        			level.add(new SplashParticle(x - 8 + randX, y - 8 + randY)); // Add water particles
        		} else if (level.getTile(x / 16, y / 16) == Tiles.get("lava")) {
        			level.add(new FireParticle(x - 8 + randX, y - 8 + randY)); // Add fire particles
        		}
        	}
        }
        
        // Display Ferrosite cloud particles when walk
        if (level.getTile(x / 16, y / 16) == Tiles.get("Ferrosite")) {
        	if (tickTime / 8 % 2 == 0) {
        		
        		int randX = rnd.nextInt(12);
        		int randY = rnd.nextInt(12);

             	level.add(new FerrositeParticle(x - 8 + randX, y - 8 + randY));
        	}

            moveSpeed = 3;

        } else {

            if (potioneffects.containsKey(PotionType.Speed) || potioneffects.containsKey(PotionType.xSpeed)) {
                moveSpeed = 2;
            } else {
            	moveSpeed = 1;
            }
        }

        if (level.getTile(x / 16, y / 16) == Tiles.get("Cloud")) {
        	
        	if (tickTime / 8 % 2 == 0) {

			    int randX = rnd.nextInt(12);
			    int randY = rnd.nextInt(12);
			
			    level.add(new CloudParticle(x - 8 + randX, y - 8 + randY));
        	}

        }

        if (isFishing) {
            if (!Bed.inBed(this) && !isSwimming()) {
                fishingTicks--;
                if (fishingTicks <= 0) {
                    goFishing();
                }
            } else {
                isFishing = false;
                fishingTicks = maxFishingTicks;
            }
        }

        if (cooldowninfo > 0)
            cooldowninfo--;

        if (input.getKey("potionEffects").clicked && cooldowninfo == 0) {
            cooldowninfo = 10;
            showpotioneffects = !showpotioneffects;
        }

        Tile onTile = level.getTile(x >> 4, y >> 4); // Gets the current tile the player is on.
        if (onTile == Tiles.get("Stairs Down") || onTile == Tiles.get("Stairs Up")) {
            if (onStairDelay <= 0) { // When the delay time has passed...
                World.scheduleLevelChange((onTile == Tiles.get("Stairs Up")) ? 1 : -1); // Decide whether to go up or
                                                                                        // down.
                onStairDelay = 10; // Resets delay, since the level has now been changed.

                Sound.Mob_player_changelevel.play();

                return; // SKIPS the rest of the tick() method.
            }

            // Resets the delay, if on a stairs tile, but the delay is greater than 0.
            // In other words, this prevents you from ever activating a level change on a stair tile,
            // UNTIL you get off the tile for 10+ ticks.
            onStairDelay = 10; 
            
        } else if (onStairDelay > 0) {
        	// Decrements stairDelay if it's > 0, but not on stair tile... does the player get removed from the tile beforehand, or something?
            onStairDelay--; 
        }


        if (onTile == Tiles.get("Infinite Fall") && !Game.isMode("creative")) {
        	
        	if (fallWarn == false) {
	            Updater.notifyAll("Watch out so you won't slip and fall!");
	            fallWarn = true;
        	}
            
            if (tickTime / 4 % 2 == 0 && fallWarn == true) {
	            if (random.nextInt(48) == 8 && onFallDelay <= 0) {
	                World.scheduleLevelChange(-1);
	                onFallDelay = 40;
	                fallWarn = false;
	
	                return;
	            }
            }

        } else if (onFallDelay > 0) {
            onFallDelay--;
        }
        

        if (Game.isMode("creative")) {
            // Prevent stamina/hunger decay in creative mode.
            stamina = maxStamina;
            hunger = maxHunger;
        }

        // Remember: staminaRechargeDelay is a penalty delay for when the player uses up all their stamina.
        // staminaRecharge is the rate of stamina recharge, in some sort of unknown units.
        if (stamina <= 0 && staminaRechargeDelay == 0 && staminaRecharge == 0) {
            staminaRechargeDelay = 40; // Delay before resuming adding to stamina.
        }

        if (staminaRechargeDelay > 0 && stamina < maxStamina) {
            staminaRechargeDelay--;
        }

        if (staminaRechargeDelay == 0) {
            staminaRecharge++; // Ticks since last recharge, accounting for the time potion effect.

            if (isSwimming() && !potioneffects.containsKey(PotionType.Swim)) {
                staminaRecharge = 0; // Don't recharge stamina while swimming.
            }

            // Recharge a bolt for each multiple of maxStaminaRecharge.
            while (staminaRecharge > maxStaminaRecharge) {
                staminaRecharge -= maxStaminaRecharge;
                if (stamina < maxStamina) {
                    stamina++; // Recharge one stamina bolt per "charge".
                }
            }
        }

        int diffIdx = Settings.getIdx("diff");

        if (hunger < 0) {
            hunger = 0; // Error correction
        }

        if (stamina < maxStamina) {
            stamHungerTicks -= diffIdx; // Affect hunger if not at full stamina; this is 2 levels away from a hunger "burger".
            if (stamina == 0) {
                stamHungerTicks -= diffIdx; // Double effect if no stamina at all.
            }
        }

        /// This if statement encapsulates the hunger system
        if (!Bed.inBed(this)) {
            if (hungerChargeDelay > 0) { // if the hunger is recharging health...
                stamHungerTicks -= 2 + diffIdx; // Penalize the hunger
                if (hunger == 0) {
                    stamHungerTicks -= diffIdx; // Further penalty if at full hunger
                }
            }

            if (Updater.tickCount % Player.hungerTickCount[diffIdx] == 0) {
                if (!Settings.get("diff").equals("Peaceful")) {
                    stamHungerTicks--; // hunger due to time.
                }
            }

            if (stepCount >= Player.hungerStepCount[diffIdx]) {
                stamHungerTicks--; // hunger due to exercise.
                stepCount = 0; // reset.
            }

            if (stamHungerTicks <= 0) {
                stamHungerTicks += maxHungerTicks; // reset stamHungerTicks
                hungerStamCnt--; // enter 1 level away from burger.
            }

            while (hungerStamCnt <= 0) {
                hunger--; // reached burger level.
                hungerStamCnt += maxHungerStams[diffIdx];
            }

            /// System that heals you depending on your hunger
            if (health < maxHealth && hunger > maxHunger / 2) {
                hungerChargeDelay++;
                if (hungerChargeDelay > 20 * Math.pow(maxHunger - hunger + 2, 2)) {
                    health++;
                    hungerChargeDelay = 0;
                }
            } else {
                hungerChargeDelay = 0;
            }

            if (hungerStarveDelay == 0) {
                hungerStarveDelay = 120;
            }

            if (hunger == 0 && health > minStarveHealth[diffIdx]) {
                if (hungerStarveDelay > 0) {
                    hungerStarveDelay--;
                }
                if (hungerStarveDelay == 0) {
					directHurt(1, Direction.NONE); // Do 1 damage to the player
                }
            }
        }

        // Regen health
        if (potioneffects.containsKey(PotionType.Regen)) {
            regentick++;
            if (regentick > 60) {
                regentick = 0;
                if (health < 10) {
                    health++;
                }
            }
        }
        
        if (potioneffects.containsKey(PotionType.Health)) {
            healtick++;
            if (healtick > 4) {
            	healtick = 0;
                if (health < 10) {
                    health++;
                }
            }
        }

        if (Updater.savecooldown > 0 && !Updater.saving) {
            Updater.savecooldown--;
        }

        // Handle player input. Input is handled by the menu if we are in one.
        if (Game.getDisplay() == null && !Bed.inBed(this)) {
            // Create the raw movement vector.
            Vector2 vec = new Vector2(0, 0);

            // Move while we are not falling.
            if (onFallDelay <= 0) {
                if (input.getKey("move-up").down) vec.y--;
                if (input.getKey("move-down").down) vec.y++;
                if (input.getKey("move-left").down) vec.x--;
                if (input.getKey("move-right").down) vec.x++;
            }

            // Executes if not saving; and... essentially halves speed if out of stamina.
            if ((vec.x != 0 || vec.y != 0) && (staminaRechargeDelay % 2 == 0 || isSwimming()) && !Updater.saving) {
                double spd = moveSpeed * (potioneffects.containsKey(PotionType.Speed) ? 1.5D : 1);

                int xd = (int) (vec.x * spd);
                int yd = (int) (vec.y * spd);

                Direction newDir = Direction.getDirection(xd, yd);
                if (newDir == Direction.NONE) {
                    newDir = dir;
                }

                // Move the player
                boolean moved = move(xd, yd); // THIS is where the player moves; part of Mob.java
                if (moved) stepCount++;
            }

            if (isSwimming() && tickTime % 60 == 0 && !potioneffects.containsKey(PotionType.Swim)) { // if drowning... :P
                if (stamina > 0) {
                    stamina--; // Take away stamina
                } else {
    				directHurt(1, Direction.NONE); // If no stamina, take damage.
                }
            }

            if (activeItem != null && (input.getKey("drop-one").clicked || input.getKey("drop-stack").clicked)) {
                Item drop = activeItem.clone();

                if (input.getKey("drop-one").clicked && drop instanceof StackableItem && ((StackableItem) drop).count > 1) {
                    // Drop one from stack
                    ((StackableItem) activeItem).count--;
                    ((StackableItem) drop).count = 1;
                } else if (!Game.isMode("creative")) {
                    activeItem = null; // Remove it from the "inventory"
                }

                level.dropItem(x, y, drop);
            }

            // this only allows attacks or pickups when such action is possible.
            if ((activeItem == null || !activeItem.used_pending) && (input.getKey("attack").clicked || input.getKey("pickup").clicked) && stamina != 0 && onFallDelay <= 0) { 
                if (!potioneffects.containsKey(PotionType.Energy)) {
                    stamina--;
                }

                staminaRecharge = 0;

                if (input.getKey("pickup").clicked && (activeItem == null || !activeItem.used_pending)) {
                    if (!(activeItem instanceof PowerGloveItem)) { // if you are not already holding a power glove (aka in the middle of a separate interaction)...
                        prevItem = activeItem; // Then save the current item...
                        activeItem = new PowerGloveItem(); // and replace it with a power glove.
                    }
                    attack(); // Attack (with the power glove)
                    resolveHeldItem();
                } else {
                    attack();
                }
            }

            if (input.getKey("menu").clicked && activeItem != null) {
                inventory.add(0, activeItem);
                activeItem = null;
            }

            if (Game.getDisplay() == null) {
            	
            	// !use() = no furniture in front of the player; this prevents player inventory from opening (will open furniture inventory instead)
                if (input.getKey("menu").clicked && !use()) {
                    Game.setDisplay(new PlayerInvDisplay(this));
                }
                if (input.getKey("pause").clicked) {
                    Game.setDisplay(new PauseDisplay());
                }
				if (input.getKey("commands").clicked) {
					Game.setDisplay(new CommandsDisplay());
				}
                if (input.getKey("craft").clicked && !use()) {
                    Game.setDisplay(new CraftingDisplay(Recipes.craftRecipes, "Crafting", this, true));
                }
                if (input.getKey("info").clicked) {
                    Game.setDisplay(new InfoDisplay());
                }
                if (input.getKey("quicksave").clicked && !Updater.saving) {
                    Updater.saving = true;
                    LoadingDisplay.setPercentage(0);
                    new Save(WorldSelectDisplay.getWorldName());
                }

                // Debug feature:
                if (Game.debug && input.getKey("shift-p").clicked) { // Remove all potion effects
                    for (PotionType potionType : potioneffects.keySet()) {
                        PotionItem.applyPotion(this, potionType, false);
                    }
                }
            }

            if (attackTime > 0) {
                attackTime--;
                if (attackTime == 0) attackItem = null; // Null the attackItem once we are done attacking.
            }
        }
    }

    /**
     * Removes an held item and places it back into the inventory. Looks complicated
     * to so it can handle the powerglove.
     */
    public void resolveHeldItem() {
        if (!(activeItem instanceof PowerGloveItem)) { // if you are now holding something other than a power glove...
            if (prevItem != null && !Game.isMode("creative")) // and you had a previous item that we should care about...
            	// then add that previous item to your inventory so it isn't lost.
                // if something other than a power glove is being held, but the previous item is
                // null, then nothing happens; nothing added to inventory, and current item
                // remains as the new one.
                inventory.add(0, prevItem); 
        } else {
        	// otherwise, if you're holding a power glove, then the held item didn't change, so we can remove the power glove and make it what it was before.
            activeItem = prevItem; 
        }

        prevItem = null; // this is no longer of use.

        // if, for some odd reason, you are still holding a power glove at this point, then null it because it's useless and shouldn't remain in hand.
        if (activeItem instanceof PowerGloveItem) {
            activeItem = null;
        }
    }

    /* This actually ends up calling another use method down below. */
    private boolean use() {
        return use(getInteractionBox(INTERACT_DIST));
    }

    /**
     * This method is called when we press the attack button.
     */
    protected void attack() {
        // walkDist is not synced, so this can happen for both the client and server.
        walkDist += 8; // increase the walkDist (changes the sprite, like you moved your arm)

        if (isFishing) {
            isFishing = false;
            fishingTicks = maxFishingTicks;
        }

        if (activeItem != null && !activeItem.interactsWithWorld()) {
            attackDir = dir; // make the attack direction equal the current direction
            attackItem = activeItem; // make attackItem equal activeItem
            activeItem.interactOn(Tiles.get("rock"), level, 0, 0, this, attackDir);
            if (!Game.isMode("creative") && activeItem.isDepleted()) {
                activeItem = null;
            }
            return;
        }

        attackDir = dir; // Make the attack direction equal the current direction
        attackItem = activeItem; // make attackItem equal activeItem

		// If we are holding an item.
		if (activeItem != null) {
			attackTime = 10;
			boolean done = false;
			
			// Fire a bow if we have the stamina and an arrow.
			if (activeItem instanceof ToolItem && stamina - 1 >= 0) {
				ToolItem tool = (ToolItem) activeItem;
				if (tool.type == ToolType.Bow && tool.dur > 0 && inventory.count(Items.arrowItem) > 0) {
					
					if (!Game.isMode("creative")) inventory.removeItem(Items.arrowItem);
					level.add(new Arrow(this, attackDir, tool.level));
					attackTime = 10;

					if (!Game.isMode("creative")) tool.dur--;

					// Bow down to me, achievement
					AchievementsDisplay.setAchievement("minicraft.achievement.bow",true);

					return;
				}
			}
			// If the interaction between you and an entity is successful, then return.
			if (interact(getInteractionBox(INTERACT_DIST))) return;
			
			// Attempt to interact with the tile.
			Point t = getInteractionTile();
			
			// If the target coordinates are a valid tile.
			if (t.x >= 0 && t.y >= 0 && t.x < level.w && t.y < level.h) {
				
				// Get any entities (except dropped items) on the tile.
				List<Entity> tileEntities = level.getEntitiesInTiles(t.x, t.y, t.x, t.y, false, ItemEntity.class, Particle.class);
				
				// If there are no other entities than us on the tile.
				if (tileEntities.size() == 0 || tileEntities.size() == 1 && tileEntities.get(0) == this) {
					Tile tile = level.getTile(t.x, t.y);
					
					// If the item successfully interacts with the target tile.
					if (activeItem.interactOn(tile, level, t.x, t.y, this, attackDir)) {
						done = true;
						
					// Returns true if the target tile successfully interacts with the item.
					} else if (tile.interact(level, t.x, t.y, this, activeItem, attackDir)){
						done = true;
					}
				}
				
				if (!Game.isMode("creative") && activeItem.isDepleted()) {
					// If the activeItem has 0 items left, then "destroy" it.
					activeItem = null;
				}
			}
			if (done) return; // Skip the rest if interaction was handled
		}
		
		if (activeItem == null || activeItem.canAttack()) { // If there is no active item, OR if the item can be used to attack...
			attackTime = 5;
			// Attacks the enemy in the appropriate direction.
			boolean used = hurt(getInteractionBox(ATTACK_DIST));
			
			// Attempts to hurt the tile in the appropriate direction.
			Point t = getInteractionTile();
			
			// Check if tile is in bounds of the map.
			if (t.x >= 0 && t.y >= 0 && t.x < level.w && t.y < level.h) {
				Tile tile = level.getTile(t.x, t.y);
				used = tile.hurt(level, t.x, t.y, this, random.nextInt(3) + 1, attackDir) || used;
			}
			
			if (used && activeItem instanceof ToolItem)
				((ToolItem)activeItem).payDurability();
		}
	}

    private Rectangle getInteractionBox(int range) {
        int x = this.x;
        int y = this.y - 2;

        // noinspection UnnecessaryLocalVariable
        int paraClose = 4, paraFar = range;
        int perpClose = 0, perpFar = 8;

        int xClose = x + dir.getX() * paraClose + dir.getY() * perpClose;
        int yClose = y + dir.getY() * paraClose + dir.getX() * perpClose;
        int xFar = x + dir.getX() * paraFar + dir.getY() * perpFar;
        int yFar = y + dir.getY() * paraFar + dir.getX() * perpFar;

        return new Rectangle(Math.min(xClose, xFar), Math.min(yClose, yFar), Math.max(xClose, xFar), Math.max(yClose, yFar), Rectangle.CORNERS);
    }

    private Point getInteractionTile() {
        int x = this.x;
        int y = this.y - 2;

        x += dir.getX() * INTERACT_DIST;
        y += dir.getY() * INTERACT_DIST;

        return new Point(x >> 4, y >> 4);
    }

    private void goFishing() {
        int fcatch = random.nextInt(100);

        boolean caught = false;

        // Figure out which table to roll for
        List<String> data = null;
        if (fcatch > FishingRodItem.getChance(0, fishingLevel)) {
            data = FishingData.fishData;
        } else if (fcatch > FishingRodItem.getChance(1, fishingLevel)) {
            data = FishingData.junkData;
        } else if (fcatch > FishingRodItem.getChance(2, fishingLevel)) {
            data = FishingData.toolData;
        } else if (fcatch >= FishingRodItem.getChance(3, fishingLevel)) {
            data = FishingData.rareData;
        }

        if (data != null) { // If you've caught something
            for (String line : data) {
                // Check all the entries in the data
                // The number is a percent, if one fails, it moves down the list
                // For entries with a "," it chooses between the options
                int chance = Integer.parseInt(line.split(":")[0]);
                String itemData = line.split(":")[1];
                if (random.nextInt(100) + 1 <= chance) {
                    if (itemData.contains(",")) { // If it has multiple items choose between them
                        String[] extendedData = itemData.split(",");
                        int randomChance = random.nextInt(extendedData.length);
                        itemData = extendedData[randomChance];
                    }
                    if (itemData.startsWith(";")) {
                        // For secret messages :=)
                        Game.notifications.add(itemData.substring(1));
                    } else {
                    	
                    	// Go Fish achievement
						if (Items.get(itemData).equals(Items.get("Raw Fish"))) {
							AchievementsDisplay.setAchievement("minicraft.achievement.fish",true);
						}
						
                        level.dropItem(x, y, Items.get(itemData));
                        caught = true;
                        break; // Don't let people catch more than one thing with one use
                    }
                }
            }
        } else {
            caught = true; // End this fishing session
        }

        if (caught) {
            isFishing = false;
        }
        fishingTicks = maxFishingTicks; // If you didn't catch anything, try again in 120 ticks
    }

    /**
     * Called by other use method; this serves as a buffer in case there is no
     * entity in front of the player.
     */
    private boolean use(Rectangle area) {
        List<Entity> entities = level.getEntitiesInRect(area); // Gets the entities within the 4 points
        for (Entity e : entities) {

        	// If the entity is not the player, then call it's use method, and return the result. Only
            if (e instanceof Furniture && ((Furniture) e).use(this)) return true; 
            else if (e instanceof Boat && ((Boat) e).use(this)) return true;
            else if (e instanceof Cleric && ((Cleric) e).use(this)) return true;
            else if (e instanceof Librarian && ((Librarian) e).use(this)) return true;
        }
        return false;
    }

    /** Same, but for interaction. */
    private boolean interact(Rectangle area) {
        List<Entity> entities = level.getEntitiesInRect(area);
        for (int i = 0; i < entities.size(); i++) {
            Entity e = entities.get(i);
            if (e != this && e.interact(this, activeItem, attackDir))
                return true; // This is the ONLY place that the Entity.interact method is actually called.
        }
        return false;
    }

    /** Same, but for attacking. */
    private boolean hurt(Rectangle area) {
        List<Entity> entities = level.getEntitiesInRect(area);
        int maxDmg = 0;
        for (int i = 0; i < entities.size(); i++) {
            Entity e = entities.get(i);
            if (e != this && e instanceof Mob) {
                int dmg = getAttackDamage(e);
                maxDmg = Math.max(dmg, maxDmg);
                ((Mob) e).hurt(this, dmg, attackDir); // Note: this really only does something for mobs.
            }
            if (e != this && e instanceof Furniture)
                e.interact(this, null, attackDir); // Note: this really only does something for mobs.
        }
        return maxDmg > 0;
    }

    /**
     * Calculates how much damage the player will do.
     * 
     * @param e Entity being attacked.
     * @return How much damage the player does.
     */
    private int getAttackDamage(Entity e) {
        int dmg = random.nextInt(2) + 1;
        if (activeItem != null && activeItem instanceof ToolItem) {
            dmg += ((ToolItem) activeItem).getAttackDamageBonus(e); // sword/axe are more effective at dealing damage.
        }
        return dmg;
    }

    @Override
    public void render(Screen screen) {

        // Skin events
        LocalDateTime time = LocalDateTime.now();

        // Halloween skin
        if (Game.IS_Halloween == true) {
            sprites = MobSprite.compileMobSpriteAnimations(0, 60);
            carrySprites = MobSprite.compileMobSpriteAnimations(0, 62);
        }

        // Me??
        if (time.getMonth() == Month.JULY) {
            if (time.getDayOfMonth() == 30) {
                sprites = MobSprite.compileMobSpriteAnimations(0, 64);
                carrySprites = MobSprite.compileMobSpriteAnimations(0, 66);
            }
            if (time.getDayOfMonth() == 29) {
                sprites = MobSprite.compileMobSpriteAnimations(0, 64);
                carrySprites = MobSprite.compileMobSpriteAnimations(0, 66);
            }
            if (time.getDayOfMonth() == 28) {
                sprites = MobSprite.compileMobSpriteAnimations(0, 64);
                carrySprites = MobSprite.compileMobSpriteAnimations(0, 66);
            }
        }

        // Markus skin
        if (time.getMonth() == Month.JUNE) {
            if (time.getDayOfMonth() == 1) {
                sprites = MobSprite.compileMobSpriteAnimations(0, 56);
                carrySprites = MobSprite.compileMobSpriteAnimations(0, 58);
            }
            if (time.getDayOfMonth() == 2) {
                sprites = MobSprite.compileMobSpriteAnimations(0, 56);
                carrySprites = MobSprite.compileMobSpriteAnimations(0, 58);
            }
            if (time.getDayOfMonth() == 3) {
                sprites = MobSprite.compileMobSpriteAnimations(0, 56);
                carrySprites = MobSprite.compileMobSpriteAnimations(0, 58);
            }
        }

        MobSprite[][] spriteSet; // The default, walking sprites.

        if (activeItem instanceof FurnitureItem) {
            spriteSet = skinon ? carrySuitSprites : carrySprites;
        } else {
            spriteSet = skinon ? suitSprites : sprites;
        }
        

        /* Offset locations to start drawing the sprite relative to our position */
        int xo = x - 8; // Horizontal
        int yo = y - 11; // Vertical

        if (isSwimming()) {
            yo += 4; // y offset is moved up by 4

            if (level.getTile(x / 16, y / 16) == Tiles.get("water")) {
				// animation effect
			    if (tickTime / 8 % 2 == 0) {
			    	screen.render(xo + 0, yo + 3, 5 + 2 * 32, 0, 3); // Render the water graphic
			    	screen.render(xo + 8, yo + 3, 5 + 2 * 32, 1, 3); // Render the mirrored water graphic to the right.
			    } else {
			    	screen.render(xo + 0, yo + 3, 13 + 2 * 32, 0, 3);
			    	screen.render(xo + 8, yo + 3, 13 + 2 * 32, 1, 3);
			    }

            } else if (level.getTile(x / 16, y / 16) == Tiles.get("lava")) {
            	
            	// BURN THE PLAYER
            	playerBurning = true;
            	
			    if (tickTime / 8 % 2 == 0) {
			    	screen.render(xo + 0, yo + 3, 6 + 2 * 32, 1, 3); // Render the water graphic
			    	screen.render(xo + 8, yo + 3, 6 + 2 * 32, 0, 3); // Render the mirrored lava graphic to the right.
			    } else {
			    	screen.render(xo + 0, yo + 3, 14 + 2 * 32, 1, 3); // Render the water graphic
			    	screen.render(xo + 8, yo + 3, 14 + 2 * 32, 0, 3); // Render the mirrored lava graphic to the right.
			    }           
            }
        }

        // Renders indicator for what tile the item will be placed on
        if (activeItem instanceof TileItem) {
            Point t = getInteractionTile();
            screen.render(t.x * 16 + 4, t.y * 16 + 4, 3 + 4 * 32, 0, 3);
        }

        if (attackTime > 0 && attackDir == Direction.UP) { // If currently attacking upwards...
            screen.render(xo + 0, yo - 4, 3 + 2 * 32, 0, 3); // Render left half-slash
            screen.render(xo + 8, yo - 4, 3 + 2 * 32, 1, 3); // Render right half-slash (mirror of left).
            if (attackItem != null) { // If the player had an item when they last attacked...
                attackItem.sprite.render(screen, xo + 4, yo - 4, 1); // Then render the icon of the item, mirrored
            }
        }

        // Makes the player white if they have just gotten hurt
        if (hurtTime > playerHurtTime - 10) {
            col = Color.WHITE; // make the sprite white.
        }

        MobSprite curSprite;
        if (onFallDelay > 0) {
            // What this does is make falling look really cool
            float spriteToUse = onFallDelay / 2f;
            while (spriteToUse > spriteSet.length - 1) {
                spriteToUse -= 4;
            }
            curSprite = spriteSet[Math.round(spriteToUse)][(walkDist >> 3) & 1];
        } else {
            curSprite = spriteSet[dir.getDir()][(walkDist >> 3) & 1]; // Gets the correct sprite to render.
        }

        // Render each corner of the sprite
        if (!isSwimming()) { // Don't render the bottom half if swimming.
            curSprite.render(screen, xo, yo - 4 * onFallDelay, -1, shirtColor);
        } else {
            curSprite.renderRow(0, screen, xo, yo, -1, shirtColor);
        }

        // Renders slashes:
        if (attackTime > 0) {
            switch (attackDir) {

            case UP: // if currently attacking upwards...
                screen.render(xo + 0, yo - 4, 3 + 2 * 32, 0, 3); // render left half-slash
                screen.render(xo + 8, yo - 4, 3 + 2 * 32, 1, 3); // render right half-slash (mirror of left).
                if (attackItem != null) { // if the player had an item when they last attacked...
                    attackItem.sprite.render(screen, xo + 4, yo - 4, 1); // then render the icon of the item, mirrored
                }
                break;

            case LEFT: // Attacking to the left... (Same as above)
                screen.render(xo - 4, yo, 4 + 2 * 32, 1, 3);
                screen.render(xo - 4, yo + 8, 4 + 2 * 32, 3, 3);
                if (attackItem != null) {
                    attackItem.sprite.render(screen, xo - 4, yo + 4, 1);
                }
                break;

            case RIGHT: // Attacking to the right (Same as above)
                screen.render(xo + 8 + 4, yo, 4 + 2 * 32, 0, 3);
                screen.render(xo + 8 + 4, yo + 8, 4 + 2 * 32, 2, 3);
                if (attackItem != null) {
                    attackItem.sprite.render(screen, xo + 8 + 4, yo + 4);
                }
                break;

            case DOWN: // Attacking downwards (Same as above)
                screen.render(xo + 0, yo + 8 + 4, 3 + 2 * 32, 2, 3);
                screen.render(xo + 8, yo + 8 + 4, 3 + 2 * 32, 3, 3);
                if (attackItem != null) {
                    attackItem.sprite.render(screen, xo + 4, yo + 8 + 4);
                }
                break;

            case NONE:
                break;
            }
        }

        if (isFishing) {
            switch (dir) {
	            case UP: screen.render(xo + 4, yo - 4, fishingLevel + 11 * 32, 1); break;
	            case LEFT: screen.render(xo - 4, yo + 4, fishingLevel + 11 * 32, 1); break;
	            case RIGHT: screen.render(xo + 8 + 4, yo + 4, fishingLevel + 11 * 32, 0); break;
	            case DOWN: screen.render(xo + 4, yo + 8 + 4, fishingLevel + 11 * 32, 0); break;
	            case NONE: break;
            }
        }

        // Renders the furniture if the player is holding one.
        if (activeItem instanceof FurnitureItem) {
            Furniture furniture = ((FurnitureItem) activeItem).furniture;
            furniture.x = x;
            furniture.y = yo - 4;
            furniture.render(screen);
        }
    }

    /** What happens when the player interacts with a itemEntity */
    public void pickupItem(ItemEntity itemEntity) {
        
		// Random pickup sound
		switch (random.nextInt(4)) {
			case 0: Sound.Mob_player_pickup.play(); break;
			case 1: Sound.Mob_player_pickup.play(); break;
			case 2: Sound.Mob_player_pickup_2.play(); break;
			case 3: Sound.Mob_player_pickup_3.play(); break;
			case 4: Sound.Mob_player_pickup_4.play(); break;
			default: Sound.Mob_player_pickup_4.play(); break;
		}

        itemEntity.remove();
        addScore(1);
        
        if (Game.isMode("creative")) {
            return; // We shall not bother the inventory on creative mode.
        }
        
        // Picked up item equals the one in your hand
        if (itemEntity.item instanceof StackableItem && ((StackableItem) itemEntity.item).stacksWith(activeItem)) {
            ((StackableItem) activeItem).count += ((StackableItem) itemEntity.item).count;
        } else {
            inventory.add(itemEntity.item); // Add item to inventory
        }
    }

    // The player can swim.
    public boolean canSwim() {
        return true;
    }

    // Can walk on wool tiles..? quickly..?
    public boolean canWool() {
        return true;
    }

    /**
     * Finds a starting position for the player.
     * 
     * @param level Level which the player wants to start in.
     * @param spawnSeed Spawnseed.
     */
    public void findStartPos(Level level, long spawnSeed) {
        random.setSeed(spawnSeed);
        findStartPos(level);
    }

    /**
     * Finds the starting position for the player in a level.
     * 
     * @param level The level.
     */
    public void findStartPos(Level level) {
        findStartPos(level, true);
    }

    public void findStartPos(Level level, boolean setSpawn) {
        Point spawnPos;

        List<Point> spawnTilePositions = level.getMatchingTiles(Tiles.get("grass"));

        if (spawnTilePositions.size() == 0) {
            spawnTilePositions.addAll(level.getMatchingTiles((t, x, y) -> t.maySpawn()));
        }

        if (spawnTilePositions.size() == 0) {
            spawnTilePositions.addAll(level.getMatchingTiles((t, x, y) -> t.mayPass(level, x, y, Player.this)));
        }

        // There are no tiles in the entire map which the player is allowed to stand on. not likely.
        if (spawnTilePositions.size() == 0) {
            spawnPos = new Point(random.nextInt(level.w / 4) + level.w * 3 / 8, random.nextInt(level.h / 4) + level.h * 3 / 8);
            level.setTile(spawnPos.x, spawnPos.y, Tiles.get("grass"));
        } else // Gets random valid spawn tile position.
            spawnPos = spawnTilePositions.get(random.nextInt(spawnTilePositions.size()));

        if (setSpawn) {
            // Used to save (tile) coordinates of spawnpoint outside of this method.
            spawnx = spawnPos.x;
            spawny = spawnPos.y;
        }
        // Set (entity) coordinates of player to the center of the tile.
        this.x = spawnPos.x * 16 + 8; // conversion from tile coords to entity coords.
        this.y = spawnPos.y * 16 + 8;
    }

	/**
	 * Finds a location where the player can respawn in a given level.
	 * 
	 * @param level The level.
	 * @return true
	 */
	public void respawn(Level level) {
		if (!level.getTile(spawnx, spawny).maySpawn()) {
			findStartPos(level); // If there's no bed to spawn from, and the stored coordinates don't point to a grass tile, then find a new point.
			
			int x = spawnx;
			int y = spawny;
			
			x = (Math.random() > .5) ? -80 : 80;
			y = (Math.random() > .5) ? -80 : 80;
			
			for (int i = 0; i < 20; i++) // Iterate through diagonal line for possible random spawn.
			{
				if (!level.getTile(spawnx, spawny).maySpawn()) {
					x += 4 * (x / -x);
					y += 4 * (-y / y);
				} else {
					spawnx = x;
					spawny = y;
					break;
				}
			}

		}
		// Move the player to the spawnpoint
		this.x = spawnx * 16 + 8;
		this.y = spawny * 16 + 8;
	}

    /**
     * Uses an amount of stamina to do an action.
     * 
     * @param cost How much stamina the action requires.
     * @return true if the player had enough stamina, false if not.
     */
    public boolean payStamina(int cost) {
    	// If the player has the potion effect for infinite stamina, return true (without subtracting cost).
        if (potioneffects.containsKey(PotionType.Energy)) {
            return true; 
        }
        // If the player doesn't have enough stamina, then return false; failure.
        else if (stamina <= 0) {
            return false; 
        }

        if (cost < 0) {
            cost = 0; // Error correction
        }
        stamina -= Math.min(stamina, cost); // Subtract the cost from the current stamina

        return true; // Success
    }

    /**
     * Gets the player's light radius underground
     */
    @Override
    public int getLightRadius() {
        int r = 5; // The radius of the light.

        // If the player holds a torch, he increases the radius of light that he has
        if (activeItem != null && activeItem.name.equals("Torch")) {
            r = 6;
        }

        if (activeItem != null && activeItem instanceof FurnitureItem) { // If player is holding furniture
            int rr = ((FurnitureItem) activeItem).furniture.getLightRadius(); // Gets furniture light radius
            if (rr > r) {
            	// Brings player light up to furniture light, if less, since the furnture is not
                // yet part of the level and so doesn't emit light even if it should.
                r = rr; 
            }
        }

        return r + super.getLightRadius(); // Return light radius
    }

    /** What happens when the player dies */
    @Override
    public void die() {
        score -= score / 3; // Subtracts score penalty (minus 1/3 of the original score)
        resetMultiplier();

        // Make death chest
        DeathChest dc = new DeathChest(this);

        if (activeItem != null) dc.getInventory().add(activeItem);
        if (curArmor != null) dc.getInventory().add(curArmor);

        Sound.Mob_player_death.play();
        World.levels[Game.currentLevel].add(dc);
        super.die(); // Calls the die() Method in Mob.java
    }

    @Override
	public void onExploded(Tnt tnt, int dmg) {
		super.onExploded(tnt, dmg);
        payStamina(dmg * 2);
    }

    public void hurt(int damage, Direction attackDir) {
        doHurt(damage, attackDir);
    }

    /** What happens when the player is hurt */
    @Override
    protected void doHurt(int damage, Direction attackDir) {
        if (Game.isMode("creative") || hurtTime > 0 || Bed.inBed(this)) {
            return; // Can't get hurt in creative, hurt cooldown, or while someone is in bed
        }

		int healthDam = 0, armorDam = 0;
		if (this == Game.player) {
			if (curArmor == null) { // No armor
				healthDam = damage; // Subtract that amount
			} else { // Has armor

				armorDamageBuffer += damage;
				armorDam += damage;

				
				while (armorDamageBuffer >= curArmor.level+1) {
					armorDamageBuffer -= curArmor.level+1;
					healthDam++;				
				}
			}

			// Adds a text particle telling how much damage was done to the player, and the armor.
			if (armorDam > 0) {
				level.add(new TextParticle("" + damage, x, y, Color.GRAY));
				armor -= armorDam;
				if (armor <= 0) {
					healthDam -= armor; // Adds armor damage overflow to health damage (minus b/c armor would be negative)
					armor = 0;
					armorDamageBuffer = 0; // Ensures that new armor doesn't inherit partial breaking from this armor.
					curArmor = null; // Removes armor
				}
			}
		}

		if (healthDam > 0 || this != Game.player) {
			level.add(new TextParticle("" + damage, x, y, Color.get(-1, 504)));
			if (this == Game.player) super.doHurt(healthDam, attackDir); // Sets knockback, and takes away health.
		}

		Sound.Mob_player_hurt.play();
		hurtTime = playerHurtTime;
	}
    
	/**
	 * Hurt the player directly. Don't use the armor as a shield.
	 * @param damage Amount of damage to do to player
	 * @param attackDir The direction of attack.
	 */
	private void directHurt(int damage, Direction attackDir) {
        if (Game.isMode("creative") || hurtTime > 0 || Bed.inBed(this)) {
            return; // Can't get hurt in creative, hurt cooldown, or while someone is in bed
        }

		int healthDam = 0;
		if (this == Game.player) {
			healthDam = damage; // Subtract that amount
		}

		if (healthDam > 0 || this != Game.player) {
			level.add(new TextParticle("" + damage, x, y, Color.get(-1, 504)));
			if (this == Game.player) super.doHurt(healthDam, attackDir); // Sets knockback, and takes away health.
		}

		Sound.Mob_player_hurt.play();
		hurtTime = playerHurtTime;
	}


    @Override
    public void remove() {
		Logger.trace("Removing player from level " + getLevel());
        super.remove();
    }

    protected String getUpdateString() {
        String updates = super.getUpdateString() + ";";
        updates += "skinon," + skinon + 
        ";shirtColor," + shirtColor + 
        ";armor," + armor + 
        ";stamina," + stamina + 
        ";health," + health + 
        ";hunger," + hunger + 
        ";attackTime," + attackTime + 
        ";attackDir," + attackDir.ordinal() + 
        ";activeItem," + (activeItem == null ? "null" : activeItem.getData()) + 
        ";isFishing," + (isFishing ? "1" : "0");

        return updates;
    }

    @Override
    protected boolean updateField(String field, String val) {
        if (super.updateField(field, val)) {
            return true;
        }
        switch (field) {
	        case "skinon": skinon = Boolean.parseBoolean(val); return true;
	        case "shirtColor": shirtColor = Integer.parseInt(val); return true;
	        case "armor": armor = Integer.parseInt(val); return true;
	        case "stamina": stamina = Integer.parseInt(val); return true;
	        case "health": health = Integer.parseInt(val); return true;
	        case "hunger": hunger = Integer.parseInt(val); return true;
	        case "score": score = Integer.parseInt(val); return true;
	        case "mult": multiplier = Integer.parseInt(val); return true;
	        case "attackTime": attackTime = Integer.parseInt(val); return true;
	        case "attackDir": attackDir = Direction.values[Integer.parseInt(val)]; return true;
	        case "activeItem":
	            activeItem = Items.get(val, true);
	            attackItem = activeItem != null && activeItem.canAttack() ? activeItem : null;
	            return true;
	        case "isFishing":
	            isFishing = Integer.parseInt(val) == 1;
	            return true;
	        case "potioneffects":
	            potioneffects.clear();
	            for (String potion : val.split(":")) {
	                String[] parts = potion.split("_");
	                potioneffects.put(PotionType.values[Integer.parseInt(parts[0])], Integer.parseInt(parts[1]));
	            }
	            return true;
        }

        return false;
    }

    public final String getPlayerData() {
        List<String> datalist = new ArrayList<>();
        StringBuilder playerdata = new StringBuilder();
        playerdata.append(Game.VERSION).append("\n");

        Save.writePlayer(this, datalist);
        for (String str : datalist)
            if (str.length() > 0)
                playerdata.append(str).append(",");
        playerdata = new StringBuilder(playerdata.substring(0, playerdata.length() - 1) + "\n");

        Save.writeInventory(this, datalist);
        for (String str : datalist)
            if (str.length() > 0)
                playerdata.append(str).append(",");
        if (datalist.size() == 0)
            playerdata.append("null");
        else
            playerdata = new StringBuilder(playerdata.substring(0, playerdata.length() - 1));

        return playerdata.toString();
    }

    @Override
    public Inventory getInventory() {
        return inventory;
    }

}
