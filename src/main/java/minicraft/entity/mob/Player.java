package minicraft.entity.mob;

import minicraft.core.Game;
import minicraft.core.Updater;
import minicraft.core.World;
import minicraft.core.io.InputHandler;
import minicraft.core.io.Settings;
import minicraft.core.io.Sound;
import minicraft.entity.*;
import minicraft.entity.furniture.Bed;
import minicraft.entity.furniture.DeathChest;
import minicraft.entity.furniture.Furniture;
import minicraft.entity.furniture.Tnt;
import minicraft.entity.particle.FireParticle;
import minicraft.entity.particle.Particle;
import minicraft.entity.particle.SplashParticle;
import minicraft.entity.particle.TextParticle;
import minicraft.graphic.*;
import minicraft.item.*;
import minicraft.level.Level;
import minicraft.level.tile.LavaTile;
import minicraft.level.tile.Tile;
import minicraft.level.tile.Tiles;
import minicraft.level.tile.WaterTile;
import minicraft.saveload.Save;
import minicraft.screen.*;
import minicraft.util.FishingData;
import minicraft.util.FlatVector;
import minicraft.util.TimeData;
import org.jetbrains.annotations.Nullable;
import org.tinylog.Logger;

import java.time.Month;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Player extends Mob implements ItemHolder, ClientTickable {
	protected final InputHandler input;

	// Attack static variables
	private static final int playerHurtTime = 30;
	private static final int playerInteractDistance = 12;
	private static final int playerAttackDistance = 20;

	// Score static variables
	private static final int mtm = 300; // time given to increase multiplier before it goes back to 1.
	public static final int MAX_MULTIPLIER = 50; // maximum score multiplier.

	public double moveSpeed = 1.0D; // The number of coordinate squares to move; each tile is 16x16.

	// Score variables
	private int score; // the player's score
	private int multipliertime = mtm; // Time left on the current multiplier.
	private int multiplier = 1; // Score multiplier

	// These 2 ints are ints saved from the first spawn - this way the spawn pos is always saved.
	public int spawnx = 0, spawny = 0; // these are stored as tile coordinates, not entity coordinates.
	// public boolean bedSpawn = false;

	public boolean suitOn;

	// The maximum stats that the player can have.
	public static final int maxStat = 10;
	public static final int maxHealth = maxStat;
	public static final int maxStamina = maxStat;
	public static final int maxHunger = maxStat;
	public static final int maxArmor = 100;

	// Player sprite variables
	public static MobSprite[][] sprites = MobSprite.compileMobSpriteAnimations(0, 20); // Normal player sprites
	private static MobSprite[][] carrySprites = MobSprite.compileMobSpriteAnimations(0, 22); // The sprites while carrying something.
	private static MobSprite[][] suitSprites = MobSprite.compileMobSpriteAnimations(0, 24); // The "airwizard suit" sprites.
	private static MobSprite[][] carrySuitSprites = MobSprite.compileMobSpriteAnimations(0, 26); // The "airwizard suit" sprites.

	private Inventory playerInventory;

	public Item activeItem; // Holds the player current item in hand
	private Item previousItem; // Holds the item held before using the POW glove.
	Item attackItem; // attackItem is useful again b/c of the power glove.

	int attackTime;
	public Direction attackDir;

	private int onStairDelay; // The delay before changing levels.
	private int onFallDelay; // The delay before falling b/c we're on an InfiniteFallTile

	public int hunger, stamina, armor; // The current stats
	public int armorDamageBuffer;

	@Nullable
	public ArmorItem currentArmor; // The color/type of armor to be displayed.

	private int staminaRecharge; // The ticks before charging a bolt of the player's stamina
	private static final int maxStaminaRecharge = 10; // Cutoff value for staminaRecharge
	public int staminaRechargeDelay; // The recharge delay ticks when the player uses up their stamina.

	private int hungerStamCount, stamHungerTicks; // Tiers of hunger penalties before losing a burger.
	private static final int maxHungerTicks = 400; // The cutoff value for stamHungerTicks

	private static final int[] maxHungerStams =  { 16,  14,  12,  8  }; // hungerStamCount required to lose a burger.
	private static final int[] hungerTickCount = { 160, 140, 120, 80 }; // Ticks before decrementing stamHungerTicks.
	private static final int[] hungerStepCount = { 32,  28,  24,  16 }; // Steps before decrementing stamHungerTicks.
	private static final int[] minStarveHealth = { 8,   7,   5,   3  }; // min hearts required for hunger to hurt you.

	private int stepCount; // Used to penalize hunger for movement.
	private int hungerChargeDelay; // The delay between each time the hunger bar increases your health
	private int hungerStarveDelay; // The delay between each time the hunger bar decreases your health

	public final HashMap<PotionType, Integer> potionEffects; // The potion effects currently applied to the player
	public boolean showPotionEffects; // Whether to display the current potion effects on screen
	private int cooldowninfo; // Prevents you from toggling the info pane on and off super fast.
	private int regentick; // Counts time between each time the regen potion effect heals you.
	private int healtick; // Used for the heal potion healing animation

	// private final int acs = 25; // Default ("start") arrow count
	public int shirtColor = Color.get(1, 51, 51, 0); // Player shirt color.

	public boolean isFishing = false;

	public final int maxFishingTicks = 120;
	public int fishingTicks = maxFishingTicks;
	public int fishingLevel;

	public boolean fallWarn = false;

    // NICE NIGHT STUFF
    public boolean isNiceNight = false; // Spawn mobs or spaw fireflyes?
    public int nightCount = 0; // NIGHT PROBABILITY
    public int nightTick = 0;

	public List<String> chatMessages = new ArrayList<String>();

	public Player(@Nullable Player previousInstance, InputHandler input) {
		super(sprites, Player.maxHealth);
		x = 24; y = 24;

		updatePlayerSkin();

		this.input = input;
		playerInventory = new Inventory() {
			@Override
			public void add(int idx, Item item) {
				if (Game.isMode("Creative")) {
					if (count(item) > 0) return;

					item = item.clone();
					if (item instanceof StackableItem) ((StackableItem) item).count = 1;
				}
				super.add(idx, item);
			}

			@Override
			public Item remove(int idx) {
				if (Game.isMode("Creative")) {
					Item currentItem = get(idx);
					if (currentItem instanceof StackableItem) ((StackableItem) currentItem).count = 1;
					if (count(currentItem) == 1) {
						super.remove(idx);
						super.add(0, currentItem);
						return currentItem.clone();
					}
				}
				return super.remove(idx);
			}
		};

		// if (previousInstance == null)
		// inventory.add(Items.arrowItem, acs);

		potionEffects = new HashMap<>();
		showPotionEffects = true;

		cooldowninfo = 0;
		regentick = 0;
		healtick = 0;

		attackDir = dir;
		armor = 0;
		currentArmor = null;
		armorDamageBuffer = 0;
		stamina = maxStamina;
		hunger = maxHunger;

		hungerStamCount = maxHungerStams[Settings.getIndex("diff")];
		stamHungerTicks = maxHungerTicks;

		if (Game.isMode("Creative")) {
			Items.fillCreativeInventory(playerInventory);
		}

		if (previousInstance != null) {
			spawnx = previousInstance.spawnx;
			spawny = previousInstance.spawny;
		}

		suitOn = Settings.getBoolean("skinon");

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
		potionEffects.put(type, duration);
	}

	/**
	 * Adds a potion effect to the player.
	 *
	 * @param type Type of effect.
	 */
	public void addPotionEffect(PotionType type) {
		addPotionEffect(type, type.displayTime);
	}

	/**
	 * Returns all the potion effects currently affecting the player.
	 *
	 * @return all potion effects on the player.
	 */
	public HashMap<PotionType, Integer> getPotionEffects() {
		return potionEffects;
	}

	public void sendMessage(String message) {
		chatMessages.add(message);
    }


	@Override
	public void tick() {
		// Don't tick player when is death or removed, or when menu is open
		if (level == null || isRemoved() || Game.getDisplay() != null) return;

		// Ticks Mob.java
		super.tick();

		Level level = Game.levels[Game.currentLevel];
		Tile onTile = level.getTile(x >> 4, y >> 4); // Gets the current tile the player is on.

        if (Updater.tickCount == 16000) {
            nightCount = (nightCount + 1) % 5;
        }
        isNiceNight = (nightCount == 4);

		tickMultiplier();

		if (!potionEffects.isEmpty() && !Bed.inBed(this)) {
			for (PotionType potionType : potionEffects.keySet().toArray(new PotionType[0])) {
				if (potionEffects.get(potionType) <= 1) { // if time is zero (going to be set to 0 in a moment)...
					PotionItem.applyPotion(this, potionType, false); // Automatically removes this potion effect.
				} else {
					potionEffects.put(potionType, potionEffects.get(potionType) - 1); // Otherwise, replace it with one less.
				}
			}
		}

		// If player is swimming on a fluid
		if (isSwimming()) {
			// Renders water or lava particles if the player is in movement and have particles activated
			if (tickTime / 8 % 2 == 0 && (Settings.get("Particles").equals(true))) {

				// Add water particles and fire particles when the player swim
				if (onTile instanceof WaterTile) {
					level.add(new SplashParticle(x - 4 , y - 4));

				} else if (onTile instanceof LavaTile) {
					level.add(new FireParticle(x - 8 + random.nextInt(10), y - 8 + random.nextInt(9)));
				}
			}
		}

		// If player is walking ...
		if (inMovement()) {
			// If the player is steppeing Ferrosite, incsrease move speed to 2
			if (onTile == Tiles.get("Ferrosite")) {
				moveSpeed = 2.0D;
			} else { // If stepping other tile...
				// If have a speed potion effect, restore the move speed
				if (potionEffects.containsKey(PotionType.Speed) || potionEffects.containsKey(PotionType.xSpeed)) {
					moveSpeed = 1.5D;
				} else {
					moveSpeed = 1.0D;
				}
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

		if (cooldowninfo > 0) {
			cooldowninfo--;
		}

		if (input.getKey("potionEffects").clicked && cooldowninfo == 0) {
			cooldowninfo = 10;
			showPotionEffects = !showPotionEffects;
		}


		if (onTile == Tiles.get("Stairs Down") || onTile == Tiles.get("Stairs Up")) {
			if (onStairDelay <= 0) { // When the delay time has passed...
				World.scheduleLevelChange((onTile == Tiles.get("Stairs Up")) ? 1 : -1); // Decide whether to go up or down.
				onStairDelay = 10; // Resets delay, since the level has now been changed.

				Sound.play("playerChangeLevel");

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

		if (onTile == Tiles.get("Infinite Fall") && !Game.isMode("Creative") && tickTime / 4 % 2 == 0) {
			if (fallWarn) {
				if (onFallDelay <= 0) {
					World.scheduleLevelChange(-1);
					onFallDelay = 40;
					// TODO: do hurt player with 5 damage when touch the ground
					fallWarn = false;

					return;
				}
			}

		} else if (onFallDelay > 0) {
			onFallDelay--;
		}


        if (Game.isMode("Creative")) {
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

            if (isSwimming() && !(potionEffects.containsKey(PotionType.Swim) || potionEffects.containsKey(PotionType.xSwim))) {
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

        int diffIdx = Settings.getIndex("diff");

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
                hungerStamCount--; // enter 1 level away from burger.
            }

            while (hungerStamCount <= 0) {
                hunger--; // reached burger level.
                hungerStamCount += maxHungerStams[diffIdx];
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
        if (potionEffects.containsKey(PotionType.Regen) || potionEffects.containsKey(PotionType.xRegen)) {
        	regentick++;
        	if (regentick > 60) {
        		regentick = 0;
        		if (health < 10) {
        			health++;
        		}
        	}
        }

        if (potionEffects.containsKey(PotionType.Health) || potionEffects.containsKey(PotionType.xHealth)) {
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
        	FlatVector vec = new FlatVector(0, 0);

        	// Move while we are not falling.
        	if (onFallDelay <= 0) {

        		// Get the key and move the player
        		if (input.getKey("move-up").down) vec.y--;
        		if (input.getKey("move-down").down) vec.y++;
        		if (input.getKey("move-left").down) vec.x--;
        		if (input.getKey("move-right").down) vec.x++;
        	}

        	// Executes if not saving; and... essentially halves speed if out of stamina.
        	if ((vec.x != 0 || vec.y != 0) && (staminaRechargeDelay % 2 == 0 || isSwimming()) && !Updater.saving) {
        		double spd = moveSpeed * (potionEffects.containsKey(PotionType.Speed) || potionEffects.containsKey(PotionType.xSpeed) ? 1.5D : 1.0D);

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

        	if (isSwimming() && tickTime % 60 == 0) { // if drowning... :P
        		if (!(potionEffects.containsKey(PotionType.Swim) || potionEffects.containsKey(PotionType.xSwim))) {
	        		if (stamina > 0) {
	        			stamina--; // Take away stamina
	        		} else {
	        			directHurt(1, Direction.NONE); // If no stamina, take damage.
	        		}
        		}
        	}

        	if (activeItem != null && (input.getKey("drop-one").clicked || input.getKey("drop-stack").clicked)) {
        		Item drop = activeItem.clone();

        		if (input.getKey("drop-one").clicked && drop instanceof StackableItem && ((StackableItem) drop).count > 1) {
        			// Drop one from stack
        			((StackableItem) activeItem).count--;
        			((StackableItem) drop).count = 1;
        		} else if (!Game.isMode("Creative")) {
        			activeItem = null; // Remove it from the "inventory"
        		}

        		level.dropItem(x, y, drop);
        	}

            // this only allows attacks or pickups when such action is possible.
            if ((activeItem == null || !activeItem.usedPending) && (input.getKey("attack").clicked || input.getKey("pickup").clicked) && stamina != 0 && onFallDelay <= 0) {
                if (!(potionEffects.containsKey(PotionType.Energy) || potionEffects.containsKey(PotionType.xEnergy))) {
                    stamina--;
                }

                staminaRecharge = 0;

                if (input.getKey("pickup").clicked && (activeItem == null || !activeItem.usedPending)) {
                    if (!(activeItem instanceof PowerGloveItem)) { // if you are not already holding a power glove (aka in the middle of a separate interaction)...
                        previousItem = activeItem; // Then save the current item...
                        activeItem = new PowerGloveItem(); // and replace it with a power glove.
                    }
                    attack(); // Attack (with the power glove)
                    resolveHeldItem();
                } else {
                    attack();
                }
            }

            if ((input.getKey("menu").clicked || input.getKey("craft").clicked) && activeItem != null) {
                playerInventory.add(0, activeItem);
                activeItem = null;
            }

            if (Game.getDisplay() == null) {

            	// !use() = no furniture in front of the player; this prevents player inventory from opening (will open furniture inventory instead)
                if (input.getKey("menu").clicked && !use()) {
                    Game.setDisplay(new PlayerInvDisplay(this));

                } else if (input.getKey("pause").clicked) {
                    Game.setDisplay(new PauseDisplay());

                } else if (input.getKey("commands").clicked) {
					if ((boolean) Settings.get("cheats")) {
						Game.setDisplay(new ChatDisplay());
					} else {
						Game.notifications.add("Cheats are disabled!");
					}

				} else if (input.getKey("craft").clicked && !use()) {
                    Game.setDisplay(new CraftingDisplay(Recipes.craftRecipes, "Crafting", this, true));

                } else if (input.getKey("info").clicked) {
                    Game.setDisplay(new InfoDisplay());
                }

                if (input.getKey("quicksave").clicked && !Updater.saving) {
                    Updater.saving = true;
                    LoadingDisplay.setPercentage(0);
                    new Save(WorldSelectDisplay.getWorldName());
                }

                // Debug feature:
                if (Game.debug && input.getKey("shift-p").clicked) { // Remove all potion effects
                    for (PotionType potionType : potionEffects.keySet()) {
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
		// if you are now holding something other than a power glove...
		if (!(activeItem instanceof PowerGloveItem)) {
			// and you had a previous item that we should care about...
			if (previousItem != null && !Game.isMode("Creative"))
				// then add that previous item to your inventory so it isn't lost.
				// if something other than a power glove is being held, but the previous item is
				// null, then nothing happens; nothing added to inventory, and current item
				// remains as the new one.
				playerInventory.add(0, previousItem);
		} else {
			// otherwise, if you're holding a power glove, then the held item didn't change, so we can remove the power glove and make it what it was before.
			activeItem = previousItem;
		}

        previousItem = null; // this is no longer of use.

        // if, for some odd reason, you are still holding a power glove at this point, then null it because it's useless and shouldn't remain in hand.
        if (activeItem instanceof PowerGloveItem) {
            activeItem = null;
        }
    }

    /* This actually ends up calling another use method down below. */
    private boolean use() {
        return use(getInteractionBox(playerInteractDistance));
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
    		activeItem.interactOn(Tiles.get("Rock"), level, 0, 0, this, attackDir);
    		activeItem.interactOn(Tiles.get("Up rock"), level, 0, 0, this, attackDir);
    		if (!Game.isMode("Creative") && activeItem.isDepleted()) {
    			activeItem = null;
    		}
    		return;
    	}

    	attackDir = dir; // Make the attack direction equal the current direction
    	attackItem = activeItem; // make attackItem equal activeItem

    	// If we are holding an item.
    	if (activeItem != null) {
    		attackTime = 10;
    		boolean interactionDone = false;

    		// Fire a bow if we have the stamina and an arrow.
    		if (activeItem instanceof ToolItem && stamina - 1 >= 0) {
    			ToolItem tool = (ToolItem) activeItem;
    			if (tool.type == ToolType.Bow && tool.durability > 0 && playerInventory.count(Items.arrowItem) > 0) {

    				if (!Game.isMode("Creative")) playerInventory.removeItem(Items.arrowItem);
    				level.add(new Arrow(this, attackDir, tool.level));
    				attackTime = 10;

    				if (!Game.isMode("Creative")) tool.durability--;

    				AchievementsDisplay.setAchievement("minicraft.achievement.bow", true);

    				return;
    			}
    		}

    		// If the interaction between you and an entity is successful, then return.
    		if (interact(getInteractionBox(playerInteractDistance))) {
				if (activeItem.isDepleted()) {
					activeItem = null;
				}
				return;
			}

    		// Attempt to interact with the tile.
    		Point interactionTile = getInteractionTile();

    		// If the target coordinates are a valid tile.
    		if (interactionTile.x >= 0 && interactionTile.y >= 0 && interactionTile.x < level.w && interactionTile.y < level.h) {

    			// Get any entities (except dropped items) on the tile.
    			List<Entity> tileEntities = level.getEntitiesInTiles(interactionTile.x, interactionTile.y, interactionTile.x, interactionTile.y, false, ItemEntity.class, Particle.class);

    			// If there are no other entities than us on the tile.
    			if (tileEntities.isEmpty() || tileEntities.size() == 1 && tileEntities.get(0) == this) {
    				Tile targetTile = level.getTile(interactionTile.x, interactionTile.y);

    				// If the item successfully interacts with the target tile.
    				if (activeItem.interactOn(targetTile, level, interactionTile.x, interactionTile.y, this, attackDir)) {
    					interactionDone = true;

    				// Returns true if the target tile successfully interacts with the item.
    				} else if (targetTile.interact(level, interactionTile.x, interactionTile.y, this, activeItem, attackDir)){
    					interactionDone = true;
    				}
    			}

    			if (!Game.isMode("Creative") && activeItem.isDepleted()) {
    				// If the activeItem has 0 items left, then "destroy" it.
    				activeItem = null;
    			}
    		}
    		if (interactionDone) return; // Skip the rest if interaction was handled
    	}

		if (activeItem == null || activeItem.canAttack()) { // If there is no active item, OR if the item can be used to attack...
			attackTime = 5;

			// Attacks the enemy in the appropriate direction.
			boolean usedToolItem;

			// Attempts to hurt the tile in the appropriate direction.
			Point interactionTile;

			if (activeItem instanceof ToolItem) {
			    ToolItem tool = (ToolItem) activeItem;
			    ToolType toolType = tool.type;

			    if (toolType == ToolType.Spear) {
					usedToolItem = hurt(getInteractionBox((playerAttackDistance + ((tool.level + 1) * 2) - 1)));
					interactionTile = getInteractionTile(((tool.level + 1) * 2) - 2);
			    } else {
			    	usedToolItem = hurt(getInteractionBox(playerAttackDistance));
			    	interactionTile = getInteractionTile();
			    }
			} else {
				usedToolItem = hurt(getInteractionBox(playerAttackDistance));
				interactionTile = getInteractionTile();
			}

			// Check if tile is in bounds of the map.
			if (interactionTile.x >= 0 && interactionTile.y >= 0 && interactionTile.x < level.w && interactionTile.y < level.h) {
				Tile targetTile = level.getTile(interactionTile.x, interactionTile.y);
				usedToolItem = targetTile.hurt(level, interactionTile.x, interactionTile.y, this, random.nextInt(3) + 1, attackDir) || usedToolItem;
			}

			if (usedToolItem && activeItem instanceof ToolItem) {
				ToolItem tool = (ToolItem) activeItem;
				tool.payDurability();

			    if (tool.type == ToolType.Spear) {
			    	payStamina(tool.level + 3);
			    }
			}
		}
    }

    private Rectangle getInteractionBox(int range) {
    	int x = this.x;
    	int y = this.y - 2;

    	int paraClose = 4;
        int perpClose = 0, perpFar = 8;

    	int xClose = x + dir.getX() * paraClose + dir.getY() * perpClose;
    	int yClose = y + dir.getY() * paraClose + dir.getX() * perpClose;
    	int xFar = x + dir.getX() * range + dir.getY() * perpFar;
    	int yFar = y + dir.getY() * range + dir.getX() * perpFar;

    	return new Rectangle(Math.min(xClose, xFar), Math.min(yClose, yFar), Math.max(xClose, xFar), Math.max(yClose, yFar), Rectangle.CORNERS);
    }

    private Point getInteractionTile() {
        int x = this.x;
        int y = this.y - 2;

        x += dir.getX() * playerInteractDistance;
        y += dir.getY() * playerInteractDistance;

        return new Point(x >> 4, y >> 4);
    }

    private Point getInteractionTile(int r) {
        int x = this.x;
        int y = this.y - 2;

        x += dir.getX() * (playerInteractDistance + r);
        y += dir.getY() * (playerInteractDistance + r);

        return new Point(x >> 4, y >> 4);
    }

    private void goFishing() {
    	int fishCatch = random.nextInt(100);

    	boolean fishCaught = false;

    	// Figure out which table to roll for
    	List<String> data = null;
    	if (fishCatch > FishingRodItem.getChance(0, fishingLevel)) data = FishingData.fishData;
    	else if (fishCatch > FishingRodItem.getChance(1, fishingLevel)) data = FishingData.junkData;
    	else if (fishCatch > FishingRodItem.getChance(2, fishingLevel)) data = FishingData.toolData;
    	else if (fishCatch >= FishingRodItem.getChance(3, fishingLevel)) data = FishingData.rareData;

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
    						AchievementsDisplay.setAchievement("minicraft.achievement.fish", true);
    					}

    					level.dropItem(x, y, Items.get(itemData));
    					fishCaught = true;
    					break; // Don't let people catch more than one thing with one use
    				}
    			}
    		}
    	} else {
    		fishCaught = true; // End this fishing session
    	}

    	if (fishCaught) {
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
        for (Entity entity : entities) {

        	// If the entity is not the player, then call it's use method, and return the result. Only
            if (entity instanceof Furniture && ((Furniture) entity).use(this)) return true;
            else if (entity instanceof Boat && ((Boat) entity).use(this)) return true;
            else if (entity instanceof Cleric && ((Cleric) entity).use(this)) return true;
            else if (entity instanceof Librarian && ((Librarian) entity).use(this)) return true;
        }
        return false;
    }

    private boolean inMovement() {
        if (Game.input.getKey("move-up").down) return true;
        else if (Game.input.getKey("move-down").down) return true;
        else if (Game.input.getKey("move-left").down) return true;
        else if (Game.input.getKey("move-right").down) return true;
        else return false;
    }

    /** Same, but for interaction. */
	private boolean interact(Rectangle area) {
	    List<Entity> entities = level.getEntitiesInRect(area);
	    for (Entity entity : entities) {
	        if (entity != this && entity.interact(this, activeItem, attackDir)) {
	            return true; // This is the ONLY place that the Entity.interact method is actually called.
	        }
	    }
	    return false;
	}

    /** Same, but for attacking. */
    private boolean hurt(Rectangle area) {
    	int maxHurtDamage = 0;

    	List<Entity> entities = level.getEntitiesInRect(area);
    	for (Entity entity : entities) {
    		if (entity != this && entity instanceof Mob) {
    			int hurtDamage = getAttackDamage(entity);
    			maxHurtDamage = Math.max(hurtDamage, maxHurtDamage);
    			((Mob) entity).hurt(this, hurtDamage, attackDir); // Note: this really only does something for mobs.
    		}
    		if (entity != this && entity instanceof Furniture) {
    			entity.interact(this, null, attackDir); // Note: this really only does something for mobs.
    		}
    	}
    	return maxHurtDamage > 0;
    }

    /**
     * Calculates how much damage the player will do.
     *
     * @param entity    Entity being attacked.
     * @return          How much damage the player does.
     */
    private int getAttackDamage(Entity entity) {
        int damage = random.nextInt(2) + 1;
        if (activeItem != null && activeItem instanceof ToolItem) {
            damage += ((ToolItem) activeItem).getAttackDamageBonus(entity); // sword/axe are more effective at dealing damage.
        }
        return damage;
    }

    private void updatePlayerSkin() {
        // Notch skin
        if (TimeData.month() == Month.JUNE) {
            if (TimeData.day() == 1 || TimeData.day() == 2 || TimeData.day() == 3) {
        		sprites = MobSprite.compileMobSpriteAnimations(10, 20);
        		carrySprites = MobSprite.compileMobSpriteAnimations(10, 22);
        		suitSprites = MobSprite.compileMobSpriteAnimations(10, 24);
        		carrySuitSprites = MobSprite.compileMobSpriteAnimations(10, 26);
            }
        }

    	// Halloween skin
    	if (TimeData.month() == Month.OCTOBER) {
    		if (TimeData.day() == 28 || TimeData.day() == 29 || TimeData.day() == 30) {
	    		sprites = MobSprite.compileMobSpriteAnimations(20, 20);
	    		carrySprites = MobSprite.compileMobSpriteAnimations(20, 22);
	    		suitSprites = MobSprite.compileMobSpriteAnimations(20, 24);
	    		carrySuitSprites = MobSprite.compileMobSpriteAnimations(20, 26);
    		}
    	}
    }

    @Override
    public void render(Screen screen) {
        MobSprite[][] spriteSet; // The default, walking sprites.

        if (activeItem instanceof FurnitureItem) {
            spriteSet = suitOn ? carrySuitSprites : carrySprites;
        } else {
            spriteSet = suitOn ? suitSprites : sprites;
        }

        /* Offset locations to start drawing the sprite relative to our position */
        int xo = x - 8; // Horizontal
        int yo = y - 11; // Vertical

        if (isSwimming() && onFallDelay <= 0) {
        	yo += 4; // y offset is moved up by 4

        	Tile onTile = level.getTile(x >> 4, y >> 4);

        	if (onTile instanceof WaterTile) {

        		// Animation effect
        		if (tickTime / 8 % 2 == 0) {
        			screen.render(xo + 0, yo + 3, 5 + (2 << 5), 0, 3); // Render the water graphic
        			screen.render(xo + 8, yo + 3, 5 + (2 << 5), 1, 3); // Render the mirrored water graphic to the right.
        		} else {
        			screen.render(xo + 0, yo + 3, 5 + (4 << 5), 0, 3);
        			screen.render(xo + 8, yo + 3, 5 + (4 << 5), 1, 3);
        		}

        	} else if (onTile instanceof LavaTile) {

        		// Animation effect
        		if (tickTime / 16 % 4 == 0) {
        			screen.render(xo + 0, yo + 3, 6 + (2 << 5), 1, 3); // Render the lava graphic
        			screen.render(xo + 8, yo + 3, 6 + (2 << 5), 0, 3); // Render the mirrored lava graphic to the right.
        		} else {
        			screen.render(xo + 0, yo + 3, 6 + (4 << 5), 1, 3);
        			screen.render(xo + 8, yo + 3, 6 + (4 << 5), 0, 3);
        		}
        	}
        }


        // Renders indicator for what tile the item will be placed on
        if (activeItem instanceof TileItem && !isSwimming()) {
            Point interactionTile = getInteractionTile();
            screen.render(interactionTile.x << 4, interactionTile.y << 4, 3 + (4 << 5), 0, 3);
            screen.render((interactionTile.x << 4) + 8, interactionTile.y << 4, 3 + (4 << 5), 1, 3);
            screen.render(interactionTile.x << 4, (interactionTile.y << 4) + 8, 3 + (4 << 5), 2, 3);
            screen.render((interactionTile.x << 4) + 8, (interactionTile.y << 4) + 8, 3 + (4 << 5), 3, 3);
        }

        if (attackTime > 0 && attackDir == Direction.UP) { // If currently attacking upwards...
            screen.render(xo + 0, yo - 4, 3 + (2 << 5), 0, 3); // Render left half-slash
            screen.render(xo + 8, yo - 4, 3 + (2 << 5), 1, 3); // Render right half-slash (mirror of left).
            if (attackItem != null) { // If the player had an item when they last attacked...
                attackItem.sprite.render(screen, xo + 4, yo - 4, 1); // Then render the icon of the item, mirrored
            }
        }

        // Makes the player white if they have just gotten hurt
        if (hurtTime > playerHurtTime - 10) {
            color = Color.WHITE; // make the sprite white.
        }

        MobSprite currentSprite;
        if (onFallDelay > 0) {
            // What this does is make falling look really cool
            float spriteToUse = onFallDelay / 2f;
            while (spriteToUse > spriteSet.length - 1) {
                spriteToUse -= 4;
            }
            currentSprite = spriteSet[Math.round(spriteToUse)][(walkDist >> 3) & 1];
        } else {
            currentSprite = spriteSet[dir.getDir()][(walkDist >> 3) & 1]; // Gets the correct sprite to render.
        }

        // Render each corner of the sprite
        if (!isSwimming()) { // Don't render the bottom half if swimming.
            currentSprite.render(screen, xo, yo - 4 * onFallDelay, -1, shirtColor);
        } else {
            currentSprite.renderRow(0, screen, xo, yo, -1, shirtColor);
        }

        // Renders slashes:
        if (attackTime > 0) {
            switch (attackDir) {

            case UP: // if currently attacking upwards...
                screen.render(xo + 0, yo - 4, 3 + (2 << 5), 0, 3); // render left half-slash
                screen.render(xo + 8, yo - 4, 3 + (2 << 5), 1, 3); // render right half-slash (mirror of left).
                if (attackItem != null) { // if the player had an item when they last attacked...
                    attackItem.sprite.render(screen, xo + 4, yo - 4, 1); // then render the icon of the item, mirrored
                }
                break;

            case LEFT: // Attacking to the left... (Same as above)
                screen.render(xo - 4, yo, 4 + (2 << 5), 1, 3);
                screen.render(xo - 4, yo + 8, 4 + (2 << 5), 3, 3);
                if (attackItem != null) {
                    attackItem.sprite.render(screen, xo - 4, yo + 4, 1);
                }
                break;

            case RIGHT: // Attacking to the right (Same as above)
                screen.render(xo + 8 + 4, yo, 4 + (2 << 5), 0, 3);
                screen.render(xo + 8 + 4, yo + 8, 4 + (2 << 5), 2, 3);
                if (attackItem != null) {
                    attackItem.sprite.render(screen, xo + 8 + 4, yo + 4);
                }
                break;

            case DOWN: // Attacking downwards (Same as above)
                screen.render(xo + 0, yo + 8 + 4, 3 + (2 << 5), 2, 3);
                screen.render(xo + 8, yo + 8 + 4, 3 + (2 << 5), 3, 3);
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
    	// pickup sound
    	Sound.playAt("playerPickup", itemEntity.x, itemEntity.y);

    	// remove the picked-up item
    	itemEntity.remove();
    	addScore(1);

    	// We shall not bother the inventory on creative mode.
    	if (Game.isMode("Creative")) {
    		return;
    	}

    	// Picked up item equals the one in your hand
    	if (itemEntity.item instanceof StackableItem && ((StackableItem) itemEntity.item).stacksWith(activeItem)) {
    		((StackableItem) activeItem).count += ((StackableItem) itemEntity.item).count;
    	} else {
    		playerInventory.add(itemEntity.item); // Add item to inventory
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
     * @param spawnSeed Spawn seed.
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

        List<Point> spawnTilePositions = level.getMatchingTiles(Tiles.get("Grass"), Tiles.get("Sand"), Tiles.get("Snow"));

        if (spawnTilePositions.isEmpty()) {
            spawnTilePositions.addAll(level.getMatchingTiles((t, x, y) -> t.maySpawn()));
        }

        if (spawnTilePositions.isEmpty()) {
            spawnTilePositions.addAll(level.getMatchingTiles((t, x, y) -> t.mayPass(level, x, y, Player.this)));
        }

        // There are no tiles in the entire map which the player is allowed to stand on. not likely.
        if (spawnTilePositions.isEmpty()) {
            spawnPos = new Point(random.nextInt(level.w / 4) + level.w * 3 / 8, random.nextInt(level.h / 4) + level.h * 3 / 8);
            level.setTile(spawnPos.x, spawnPos.y, Tiles.get("Grass"));
        } else { // Gets random valid spawn tile position.
            spawnPos = spawnTilePositions.get(random.nextInt(spawnTilePositions.size()));
        }

        if (setSpawn) {
            // Used to save (tile) coordinates of spawn point outside of this method.
            spawnx = spawnPos.x;
            spawny = spawnPos.y;
        }

        // Set (entity) coordinates of player to the center of the tile.
        this.x = (spawnPos.x * 16) + 8; // conversion from tile coords to entity coords.
        this.y = (spawnPos.y * 16) + 8;
    }

	/**
	 * Finds a location where the player can respawn in a given level.
	 *
	 * @param level The level.
	 */
    public void respawn(Level level) {
        // If there's no bed to spawn from, and the stored coordinates don't point to a grass tile, then find a new point.
    	if (!level.getTile(spawnx, spawny).maySpawn()) {
    		findStartPos(level);
    	}

    	// Move the player to the spawnpoint
    	this.x = (spawnx * 16) + 8;
    	this.y = (spawny * 16) + 8;
    }

    /**
     * Uses an amount of stamina to do an action.
     *
     * @param cost How much stamina the action requires.
     * @return true if the player had enough stamina, false if not.
     */
    public boolean payStamina(int cost) {
    	// If the player has the potion effect for infinite stamina, return true (without subtracting cost).
    	if (potionEffects.containsKey(PotionType.Energy) || potionEffects.containsKey(PotionType.xEnergy)) {
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
    	int playerLightRadius = 4; // The radius of the light.

    	// If the player holds a torch, he increases the radius of light that he has
    	if (activeItem != null && activeItem.name.equals("Torch")) {
    		playerLightRadius = 6;
    	}

    	if (activeItem != null && activeItem instanceof FurnitureItem) { // If player is holding furniture
    		int furnitureLightRadius = ((FurnitureItem) activeItem).furniture.getLightRadius(); // Gets furniture light radius
    		if (furnitureLightRadius > playerLightRadius) {
    			// Brings player light up to furniture light, if less, since the furnture is not
    			// yet part of the level and so doesn't emit light even if it should.
    			playerLightRadius = furnitureLightRadius;
    		}
    	}

    	 // Return light radius
    	return (playerLightRadius + super.getLightRadius());
    }

    /** What happens when the player dies */
    @Override
    public void die() {
    	score -= score / 3; // Subtracts score penalty (minus 1/3 of the original score)
    	resetMultiplier();

    	// Make death chest
    	DeathChest deathChest = new DeathChest(this);

    	if (activeItem != null) deathChest.getInventory().add(activeItem);
    	if (currentArmor != null) deathChest.getInventory().add(currentArmor);

    	Sound.playAt("playerDeath", this.x, this.y);

        // Add the death chest to the world.
    	World.levels[Game.currentLevel].add(deathChest);

    	super.die(); // Calls the die() Method in Mob.java
    }

    @Override
    public void onExploded(Tnt tnt, int damage) {
    	super.onExploded(tnt, damage);
    	payStamina(damage * 2);
    }

    public void hurt(int damage, Direction attackDir) {
    	doHurt(damage, attackDir);
    }

    /** What happens when the player is hurt */
    @Override
    protected void doHurt(int damage, Direction attackDir) {
    	if (Game.isMode("Creative") || hurtTime > 0 || Bed.inBed(this)) {
    		return; // Can't get hurt in creative, hurt cooldown, or while someone is in bed
    	}

    	int healthDamage = 0;
    	int armorDamage = 0;

    	if (this == Game.player) {
    		if (currentArmor == null) { // No armor
    			healthDamage = damage; // Subtract that amount

    		} else { // Has armor
    			armorDamageBuffer += damage;
    			armorDamage += damage;

    			while (armorDamageBuffer >= currentArmor.level+1) {
    				armorDamageBuffer -= currentArmor.level+1;
    				healthDamage++;
    			}
    		}

    		// Adds a text particle telling how much damage was done to the player, and the armor.
    		if (armorDamage > 0) {
    			level.add(new TextParticle("" + damage, x, y, Color.GRAY));
    			armor -= armorDamage;
    			if (armor <= 0) {
    				healthDamage -= armor; // Adds armor damage overflow to health damage (minus b/c armor would be negative)
    				armor = 0;
    				armorDamageBuffer = 0; // Ensures that new armor doesn't inherit partial breaking from this armor.
    				currentArmor = null; // Removes armor
    			}
    		}
    	}

    	if (healthDamage > 0 || this != Game.player) {
    		level.add(new TextParticle("" + damage, x, y, Color.get(-1, 504)));
    		if (this == Game.player) {
    			super.doHurt(healthDamage, attackDir); // Sets knockback, and takes away health.
    		}
    	}

    	Sound.playAt("playerHurt", this.x, this.y);
    	hurtTime = playerHurtTime;
    }

    /**
     * Hurt the player directly. Don't use the armor as a shield.
     * @param damage Amount of damage to do to player
     * @param attackDir The direction of attack.
     */
    private void directHurt(int damage, Direction attackDir) {
    	if (Game.isMode("Creative") || hurtTime > 0 || Bed.inBed(this)) {
    		return; // Can't get hurt in creative, hurt cooldown, or while someone is in bed
    	}

    	int healthDamage = 0;
    	if (this == Game.player) {
    		healthDamage = damage; // Subtract that amount
    	}

    	if (healthDamage > 0 || this != Game.player) {
    		level.add(new TextParticle("" + damage, x, y, Color.get(-1, 504)));
    		if (this == Game.player) {
    			super.doHurt(healthDamage, attackDir); // Sets knockback, and takes away health.
    		}
    	}

    	Sound.playAt("playerHurt", this.x, this.y);
    	hurtTime = playerHurtTime;
    }

    @Override
    public void remove() {
    	Logger.trace("Removing player from level {} ...", getLevel());
    	super.remove();
    }

    @Override
    public Inventory getInventory() {
    	return playerInventory;
    }

	// Note: the player's health & max health are inherited from Mob.java
	public String getDebugHunger() {
		return hungerStamCount + "_" + stamHungerTicks;
	}

}
