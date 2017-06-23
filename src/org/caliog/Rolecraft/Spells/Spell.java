package org.caliog.Rolecraft.Spells;

import java.io.File;
import java.util.HashMap;

import org.bukkit.configuration.file.YamlConfiguration;
import org.caliog.Rolecraft.Manager;
import org.caliog.Rolecraft.Entities.Player.RolecraftAbstrPlayer;
import org.caliog.Rolecraft.Entities.Player.RolecraftPlayer;
import org.caliog.Rolecraft.XMechanics.Messages.MessageKey;
import org.caliog.Rolecraft.XMechanics.Messages.Msg;
import org.caliog.Rolecraft.XMechanics.Resource.FilePath;
import org.caliog.Rolecraft.XMechanics.Utils.Utils;

public abstract class Spell {
	private final RolecraftPlayer player;
	private boolean active = false;
	private String name;
	private int power = 0;
	private final int maxPower;
	private YamlConfiguration config;
	private HashMap<Integer, Double> damageMap = new HashMap<Integer, Double>();
	private HashMap<Integer, Double> defenseMap = new HashMap<Integer, Double>();

	public Spell(RolecraftPlayer player, String name) {
		this.player = player;
		this.setName(name);
		this.config = YamlConfiguration.loadConfiguration(new File(FilePath.spells + name + ".yml"));
		if (config != null) {
			if (config.isConfigurationSection("damage")) {
				for (String k : config.getConfigurationSection("damage").getKeys(false)) {
					if (Utils.isInteger(k)) {
						damageMap.put(Integer.parseInt(k), config.getDouble("damage." + k));
					}
				}
			}
			if (config.isConfigurationSection("defense")) {
				for (String k : config.getConfigurationSection("defense").getKeys(false)) {
					if (Utils.isInteger(k)) {
						defenseMap.put(Integer.parseInt(k), config.getDouble("defense." + k));
					}
				}
			}
			maxPower = config.getInt("max-power", -1);
		} else
			maxPower = -1;
	}

	public int getMinLevel() {
		if (config != null)
			return config.getInt("min-level", 0);
		return 0;
	}

	public int getFood() {
		return (int) Math.round(Math.sqrt(getPower()) + 1);
	}

	public final int getPower() {
		return power;
	}

	public double getDamage() {
		double damage = 0;
		int lastPower = -1;
		for (int power : damageMap.keySet())
			if (power <= getPower() && power > lastPower) {
				damage = damageMap.get(power);
				lastPower = power;
			}
		return damage;
	}

	public double getDefense() {
		double defense = 0;
		int lastPower = -1;
		for (int power : defenseMap.keySet())
			if (power <= getPower() && power > lastPower) {
				defense = defenseMap.get(power);
				lastPower = power;
			}
		return defense;
	}

	public boolean isActive() {
		return this.active;
	}

	/**
	 * This is an optional method to use in the execute override.<br>
	 * It will tell the plugin that the spell is still active,<br>
	 * while it is active {@link getDamage()},{@link getDefense()} will be added
	 * to player's damage,defense.<br>
	 * 
	 * @param time
	 *            The time (in ticks = 20 * seconds) the spell will be active
	 * 
	 * @return false, if spell is already active
	 */

	public boolean activate(long time) {
		if (this.active) {
			return false;
		}
		this.active = true;

		SpellBarManager.timer(player.getPlayer(), name, time);

		Manager.scheduleTask(new Runnable() {

			@Override
			public void run() {
				active = false;
			}
		}, time);
		return true;
	}

	/**
	 * 
	 * If you override this method, make sure to call super.execute() first.<br>
	 * It will return true if and only if:<br>
	 * <ol>
	 * <li>Player level is greater than or equal to {@link getMinLevel()}</li>
	 * <li>Player food is greater than or equal to {@link getFood()}</li>
	 * <li>the spell is not already active</li>
	 * </ol>
	 * 
	 * @return true, if player could cast this spell
	 */
	public boolean execute() {
		if (this.player.getLevel() < getMinLevel()) {
			Msg.sendMessage(this.player.getPlayer(), MessageKey.SKILL_NEED_LEVEL);
			return false;
		}
		if (this.player.getPlayer().getFoodLevel() - getFood() >= 0) {
			this.player.getPlayer().setFoodLevel(this.player.getPlayer().getFoodLevel() - getFood());
		} else {
			Msg.sendMessage(this.player.getPlayer(), MessageKey.SKILL_NEED_MANA);
			return false;
		}
		if (isActive()) {
			Msg.sendMessage(this.player.getPlayer(), MessageKey.SKILL_ACTIVE);
			return false;
		}
		return true;
	}

	public void reloadPower() {
		this.power = player.getSpellPower(getName());
	}

	public int getMaxPower() {
		return maxPower;
	}

	public RolecraftAbstrPlayer getPlayer() {
		return this.player;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}