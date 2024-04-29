package minicraft.item;

import minicraft.core.Game;
import minicraft.core.World;
import minicraft.entity.mob.Player;
import minicraft.graphic.Color;
import minicraft.level.Level;

public enum PotionType {
    None(Color.get(1, 22, 22, 137), 0),

    Speed(Color.get(1, 23, 46, 23), 4200) {
        public boolean toggleEffect(Player player, boolean addEffect) {
            player.moveSpeed += (double) (addEffect ? 1 : ((player.moveSpeed > 1) ? -1 : 0));
            return true;
        }
    },

    xSpeed(Color.get(1, 48, 68, 34), 6200) {
        public boolean toggleEffect(Player player, boolean addEffect) {
            player.moveSpeed += (double) (addEffect ? 1 : (player.moveSpeed > 1 ? -1 : 0));
            return true;
        }
    },

    Light(Color.get(1, 183, 183, 91), 6000), xLight(Color.get(1, 211, 211, 105), 12000),
    Swim(Color.get(1, 17, 17, 85), 4800), xSwim(Color.get(1, 26, 26, 130), 9600),
    Energy(Color.get(1, 172, 80, 57), 8400), xEnergy(Color.get(1, 198, 91, 67), 16400),
    Regen(Color.get(1, 168, 54, 146), 1800), xRegen(Color.get(1, 191, 63, 112), 2800),
    Health(Color.get(1, 161, 46, 69), 100),
    xHealth(Color.get(1, 255, 63, 110), 0) {
        public boolean toggleEffect(Player player, boolean addEffect) {
            if (addEffect) {
                player.heal(10);
            }
            return true;
        }
    },

    Time(Color.get(1, 102), 1800),
    Lava(Color.get(1, 129, 37, 37), 7200),
    xLava(Color.get(1, 204, 59, 59), 14200),
    Shield(Color.get(1, 65, 65, 157), 5400),
    xShield(Color.get(1, 65, 65, 157), 10400),
    Haste(Color.get(1, 106, 37, 106), 4800),

    Escape(Color.get(1, 85, 62, 62), 0) {
        public boolean toggleEffect(Player player, boolean addEffect) {
            if (addEffect) {
                int playerDepth = player.getLevel().depth;

                if (playerDepth == 0) {
                    // player is in overworld
                	Game.notifications.add("You can't escape from here!");
                    return false;
                }

                int depthDiff = playerDepth > 0 ? -1 : 1;

                World.scheduleLevelChange(depthDiff, () -> {
                    Level playerLevel = World.levels[World.levelIndex(playerDepth + depthDiff)];
                    if (playerLevel != null && !playerLevel.getTile(player.x >> 4, player.y >> 4).mayPass(playerLevel, player.x >> 4, player.y >> 4, player)) {
                        player.findStartPos(playerLevel, false);
                    }
                });
            }
            return true;
        }
    };

    public int displayColor, displayTime;
    public String name;

    PotionType(int color, int time) {
        displayColor = color;
        displayTime = time;
        if (this.toString().equals("None")) {
            name = "Potion";
        } else {
            name = this + " Potion";
        }
    }

    public boolean toggleEffect(Player player, boolean addEffect) {
        return displayTime > 0; // if you have no duration and do nothing, then you can't be used.
    }

    public boolean transmitEffect() {
        return true; // any effect which could be duplicated and result poorly should not be sent to the server.
        // for the case of the Health potion, the player health is not transmitted
        // separately until after the potion effect finishes, so having it send just
        // gets the change there earlier.
    }

    public static final PotionType[] values = PotionType.values();
}
