package org.caliog.Rolecraft.Entities.Player;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.caliog.Rolecraft.Manager;
import org.caliog.Rolecraft.Items.CustomItem;
import org.caliog.Rolecraft.Items.ItemEffect;
import org.caliog.Rolecraft.Items.ItemEffect.ItemEffectType;
import org.caliog.Rolecraft.Mobs.Pet;
import org.caliog.Rolecraft.Spells.InvisibleSpell;
import org.caliog.Rolecraft.Spells.Spell;
import org.caliog.Rolecraft.Spells.SpellBarManager;
import org.caliog.Rolecraft.XMechanics.Bars.BottomBar.BottomBar;
import org.caliog.Rolecraft.XMechanics.Logging.LOG;
import org.caliog.Rolecraft.XMechanics.Logging.LOG.LogLevel;
import org.caliog.Rolecraft.XMechanics.Logging.LOG.LogTitle;
import org.caliog.Rolecraft.XMechanics.Resource.FilePath;

public class RolecraftPlayer extends RolecraftAbstrPlayer {

	private int strength;
	private int intelligence;
	private int dexterity;
	private int vitality;
	protected int[] spell = { -1, -1, -1 };
	private HashMap<String, Spell> spells = new HashMap<String, Spell>();
	private final String type;
	private int spellTask = -1;
	private Set<Pet> pets = new HashSet<Pet>();
	private boolean loaded = false;

	public RolecraftPlayer(Player player, String type) {
		super(player);
		this.type = type;
		setLoaded(load());
		setHealth(getMaxHealth());
	}

	@SuppressWarnings("deprecation")
	public double getMaxHealth() {
		double h = super.getMaximumHealth();
		h += (getRVitality() / 100.0F) * 20.0F;
		if (getPlayer().getMaxHealth() != h)
			getPlayer().setMaxHealth(h);
		return h;
	}

	public void addHealth(double d) {
		if (getHealth() + d > getMaxHealth()) {
			setHealth(getMaxHealth());
		} else {
			setHealth(d + getHealth());
		}
	}

	public double getDefense() {
		double defense = super.getDefense();
		double p = 1.0D + (getRStrength() + getRDexterity()) / 200.0D;
		for (Spell s : this.spells.values()) {
			if (s.isActive()) {
				defense += s.getDefense();
			}
		}
		return p * defense;
	}

	public double getDamage() {
		double damage = super.getDamage();
		double p = 1.0D + getRStrength() / 100.0D;
		if ((getCritical() > 0) && ((getRIntelligence() / 20.0F + getCritical()) / 100.0F > Math.random())) {
			p = 2.0D;
		}
		for (Spell s : this.spells.values())
			if (s.isActive())
				damage += s.getDamage();

		return p * damage;
	}

	public int getRStrength() {
		int a = 0;
		for (CustomItem item : this.getEquipment()) {
			for (ItemEffect effect : item.getEffects()) {
				if (effect.getType().equals(ItemEffectType.STR))
					a += effect.getPower();
			}
		}
		return getStrength() + a;
	}

	public int getStrength() {
		return this.strength;
	}

	public boolean skillStrength(int a) {
		if (getStrength() + a <= 100) {
			setStrength(getStrength() + a);
			return true;
		}
		return false;
	}

	public void setStrength(int strength) {
		if (strength <= 100) {
			this.strength = strength;
		}
	}

	public int getRIntelligence() {
		int a = 0;
		for (CustomItem item : this.getEquipment()) {
			for (ItemEffect effect : item.getEffects()) {
				if (effect.getType().equals(ItemEffectType.INT))
					a += effect.getPower();
			}
		}
		return getIntelligence() + a;
	}

	public int getIntelligence() {
		return this.intelligence;
	}

	public boolean skillIntelligence(int a) {
		if (getIntelligence() + a <= 100) {
			setIntelligence(getIntelligence() + a);
			return true;
		}
		return false;
	}

	public void setIntelligence(int intelligence) {
		if (intelligence <= 100) {
			this.intelligence = intelligence;
		}
	}

	public int getRDexterity() {
		int a = 0;
		for (CustomItem item : this.getEquipment()) {
			for (ItemEffect effect : item.getEffects()) {
				if (effect.getType().equals(ItemEffectType.DEX))
					a += effect.getPower();
			}
		}
		return getDexterity() + a;
	}

	public int getDexterity() {
		return this.dexterity;
	}

	public boolean skillDexterity(int a) {
		if (getDexterity() + a <= 100) {
			setDexterity(getDexterity() + a);
			return true;
		}
		return false;
	}

	public void setDexterity(int dexterity) {
		if (dexterity <= 100) {
			this.dexterity = dexterity;
		}
	}

	public int getRVitality() {
		int a = 0;
		for (CustomItem item : this.getEquipment()) {
			for (ItemEffect effect : item.getEffects()) {
				if (effect.getType().equals(ItemEffectType.VIT))
					a += effect.getPower();
			}
		}
		return getVitality() + a;
	}

	public int getVitality() {
		return this.vitality;
	}

	public boolean skillVitality(int a) {
		if (getVitality() + a <= 100) {
			setVitality(getVitality() + a);
			return true;
		}
		return false;
	}

	public void setVitality(int vitality) {
		if (vitality <= 100) {
			this.vitality = vitality;
		}
	}

	public String getType() {
		return this.type;
	}

	public int getLevel() {
		return getPlayer().getLevel();
	}

	private int getCritical() {
		int a = 0;
		for (CustomItem item : this.getEquipment()) {
			for (ItemEffect effect : item.getEffects()) {
				if (effect.getType().equals(ItemEffectType.CRIT))
					a += effect.getPower();
			}
		}
		return a;
	}

	public int getDodge() {
		int a = 0;
		for (CustomItem item : this.getEquipment()) {
			for (ItemEffect effect : item.getEffects()) {
				if (effect.getType().equals(ItemEffectType.DODGE))
					a += effect.getPower();
			}
		}
		return a;
	}

	public void register(Action action) {
		int s = -1;
		switch (action) {
		case PHYSICAL:
			s = 1;
			break;
		case LEFT_CLICK_AIR:
			s = 1;
			break;
		case RIGHT_CLICK_BLOCK:
			s = 0;
			return;
		case RIGHT_CLICK_AIR:
			s = 0;
			break;
		case LEFT_CLICK_BLOCK:
			if (getPlayer().isSneaking())
				s = 1;
			return;
		default:
			return;
		}
		if ((this.spell[0] == -1) && (this.getPlayer().getInventory().getItemInMainHand().getType().equals(Material.BOW) ? s == 0 : s == 1)
				&& (!getPlayer().isSneaking())) {
			return;
		}
		for (int i = 0; i < this.spell.length; i++) {
			if (this.spell[i] == -1) {
				this.spell[i] = s;

				String cc = Playerface.spell(spell);
				SpellBarManager.register(getPlayer(), "#castcode#", 30L);
				BottomBar.display(getPlayer(), cc);

				if (i == this.spell.length - 1) {
					castSpell();
				}
				if (this.spellTask != -1) {
					Manager.cancelTask(Integer.valueOf(this.spellTask));
				}
				this.spellTask = Manager.scheduleTask(new Runnable() {
					public void run() {
						for (int i = 0; i < spell.length; i++) {
							spell[i] = -1;
						}
					}
				}, 30L);

				return;
			}
		}
	}

	protected void castSpell() {
		for (String id : spells.keySet())
			if (id.equals(String.valueOf(spell[0]) + String.valueOf(spell[1]) + String.valueOf(spell[2]))) {
				Spell spell = spells.get(id);
				if (spell != null) {
					LOG.log(LogLevel.INFO, LogTitle.SPELL, getPlayer().getName() + " is casting spell:", spell.getName());
					spell.execute();
					BottomBar.display(getPlayer(), ChatColor.GOLD + spell.getName());
					return;
				}
			}
		LOG.log(LogLevel.EXCEPTION, LogTitle.SPELL, getPlayer().getName() + " tried to cast spell:", String.valueOf(spell[0]),
				String.valueOf(spell[1]), String.valueOf(spell[2]));
		BottomBar.display(getPlayer(), ChatColor.RED + "" + ChatColor.MAGIC + "Uups");
	}

	public void regainFood() {
		if (getPlayer().getFoodLevel() < 20) {
			getPlayer().setFoodLevel(getPlayer().getFoodLevel() + 1);
		}
	}

	public void setLevel(int level) {
		PlayerManager.changedClass.add(getPlayer().getUniqueId());
		getPlayer().setLevel(level);
	}

	public void save() throws IOException {
		File folder = new File(FilePath.players + this.type);
		folder.mkdir();
		File file = new File(folder.getAbsolutePath() + "/" + getPlayer().getName() + ".yml");
		YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
		config.set("level", Integer.valueOf(getLevel()));
		config.set("exp", Float.valueOf(getPlayer().getExp()));
		config.set("int", Integer.valueOf(getIntelligence()));
		config.set("vit", Integer.valueOf(getVitality()));
		config.set("str", Integer.valueOf(getStrength()));
		config.set("dex", Integer.valueOf(getDexterity()));
		config.set("quests", getQString());
		config.save(file);
		despawnPets();
	}

	public boolean load() {
		File folder = new File(FilePath.players + this.type);
		folder.mkdir();
		File file = new File(folder.getAbsolutePath() + "/" + getPlayer().getName() + ".yml");
		if (file.exists()) {
			YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
			int l = config.getInt("level");
			if (l <= 0)
				l = 1;
			setLevel(l);
			getPlayer().setExp((float) config.getDouble("exp"));
			setIntelligence(config.getInt("int"));
			setVitality(config.getInt("vit"));
			setStrength(config.getInt("str"));
			setDexterity(config.getInt("dex"));
			setQuest(config.getString("quests"));

			return true;
		} else
			return false;
	}

	public boolean isInvisible() {
		for (Spell spell : this.spells.values()) {
			if (((spell instanceof InvisibleSpell)) && (spell.isActive())) {
				return true;
			}
		}
		return false;
	}

	@Override
	public void reset() {
		super.reset();
		this.dexterity = 0;
		this.intelligence = 0;
		this.vitality = 0;
		this.strength = 0;

	}

	public boolean isLoaded() {
		return loaded;
	}

	public void setLoaded(boolean loaded) {
		this.loaded = loaded;
	}

	public void addSpell(String id, Spell spell) {
		spells.put(id, spell);
	}

	public boolean spawnPet(Location loc, String name, String customName) {
		Pet pet = Pet.spawnPet(name, customName, loc);
		LOG.log(LogLevel.INFO, LogTitle.PET, getPlayer().getName() + " is trying to spawn his pet: (name=" + name + ")", customName);
		if (pet == null)
			return false;
		LOG.log(LogLevel.INFO, LogTitle.PET, getPlayer().getName() + " successfully spawned: (name=" + name + ")", customName);
		pets.add(pet);
		return true;
	}

	public void despawnPets() {
		for (Pet p : pets)
			if (p != null)
				p.die(this, false);
		pets.clear();
	}

	public Set<Pet> getPets() {
		return pets;
	}

	@Override
	public UUID getUniqueId() {
		return getPlayer().getUniqueId();
	}

}
