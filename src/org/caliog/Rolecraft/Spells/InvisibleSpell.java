package org.caliog.Rolecraft.Spells;

import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.caliog.Rolecraft.Manager;
import org.caliog.Rolecraft.Entities.Player.RolecraftPlayer;
import org.caliog.Rolecraft.Spells.Mechanics.Spell;

public class InvisibleSpell extends Spell {
	public InvisibleSpell(RolecraftPlayer player) {
		super(player, "Vanish");
	}

	public boolean execute() {
		if (!super.execute()) {
			return false;
		}
		if (getPlayer().isFighting()) {
			return false;
		}
		int time = Math.round(getPower() * 20L);
		activate(time);
		Manager.scheduleRepeatingTask(new Runnable() {
			public void run() {
				getPlayer().getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, 1200, 2));
			}
		}, 0L, 200L, time);
		Manager.scheduleTask(new Runnable() {
			public void run() {
				getPlayer().getPlayer().removePotionEffect(PotionEffectType.INVISIBILITY);
			}
		}, time);

		return true;
	}

	public int getMinLevel() {
		return 5;
	}

	public int getFood() {
		return Math.round(getPower() / (float) getMaxPower() * 12.0F);
	}

	public double getDamage() {
		return 0;
	}

	public double getDefense() {
		return 0;
	}
}
