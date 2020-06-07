package minicraft.item;

import minicraft.Game;
import minicraft.entity.Player;
import minicraft.level.Level;

public enum PotionType {
	None (5, 0),
	
	Speed (10, 4200) {
		public boolean toggleEffect(Player player, boolean addEffect) {
			player.moveSpeed += (double)( addEffect ? 1 : (player.moveSpeed > 1 ? -1 : 0) );
			return true;
		}
	},
	
	SpeedII (10, 4200) {
		public boolean toggleEffect(Player player, boolean addEffect) {
			player.moveSpeed += (double)( addEffect ? 2 : (player.moveSpeed > 2 ? -2 : 0) );
			return true;
		}
	},
	
	Light (440, 6000),
	Swim (3, 4800),
	Energy (510, 8400),
	Regen (504, 1800),
	RegenII (504, 12000),
	Health (501, 0) {
		public boolean toggleEffect(Player player, boolean addEffect) {
			if(addEffect) player.heal(5);
			return true;
		}
	},
	
	HealthII (501, 0) {
		public boolean toggleEffect(Player player, boolean addEffect) {
			if(addEffect) player.heal(10);
			return true;
		}
	},
	
	Time (222, 1800) {
		public boolean toggleEffect(Player player, boolean addEffect) {
			Game.gamespeed *= (addEffect ? 0.5f : 2);
			return true;
		}
	},
	
	
	Lava (400, 7200),
	LavaII (400, 12800),
	Shield (115, 5400),
	ShieldII (115, 12800),
	Haste (303, 4800),

	Blind (534, 3000) {
		public boolean toggleEffect(Player player, boolean addEffect) {
			Game.setTime(32000);			
			if(Blind != null && addEffect){
			Game.setTime(600000);
			}
			
			//This function reverses the polarity of the effect.
			
			return true;
		}
		
	};

	
	public int dispColor, duration;
	public String name;
	
	private PotionType(int col, int dur) {
		dispColor = col;
		duration = dur;
		if(this.toString().equals("None")) name = "Potion";
		else name = this + " Potion";
	}
	

	

	public boolean toggleEffect(Player player, boolean addEffect) {
		return true;
	}
}
