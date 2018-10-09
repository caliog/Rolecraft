package org.caliog.Rolecraft.Entities.Player;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
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
import org.caliog.Rolecraft.Mobs.Pets.Pet;
import org.caliog.Rolecraft.Items.Books.Spellbook;
import org.caliog.Rolecraft.Spells.InvisibleSpell;
import org.caliog.Rolecraft.Spells.Mechanics.Spell;
import org.caliog.Rolecraft.Spells.Mechanics.SpellBarManager;
import org.caliog.Rolecraft.Spells.Mechanics.SpellLoader;
import org.caliog.Rolecraft.XMechanics.Bars.BottomBar.BottomBar;
import org.caliog.Rolecraft.XMechanics.Messages.Key;
import org.caliog.Rolecraft.XMechanics.Messages.Msg;
import org.caliog.Rolecraft.XMechanics.Resource.FilePath;
import org.caliog.Rolecraft.XMechanics.Utils.Pair;
import org.caliog.Rolecraft.XMechanics.Utils.IO.Debugger;
import org.caliog.Rolecraft.XMechanics.Utils.IO.Debugger.LogTitle;

public class RolecraftPlayer extends RolecraftAbstrPlayer {

	private int strength;
	private int intelligence;
	private int dexterity;
	private int vitality;
	protected int[] spell = { -1, -1, -1 };
	private HashMap<String, Pair<Spell, Integer>> spells = new HashMap<String, Pair<Spell, Integer>>();
	private int spellPoints;
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

	public void addHealth(double d, boolean b) {
		if (getHealth() + d > getMaxHealth()) {
			setHealth(getMaxHealth());
		} else {
			setHealth(d + getHealth());
		}
		if (b)
			getPlayer().setHealth(getHealth());
	}

	public void resetHealth() {
		setHealth(getMaxHealth());
	}

	public double getDefense() {
		double defense = super.getDefense();
		double p = 1.0D + (0.8F * getRStrength() + getRDexterity()) / 200.0D;
		for (Pair<Spell, Integer> value : this.spells.values()) {
			if (value.first.isActive()) {
				defense += value.first.getDefense();
			}
		}
		return p * defense;
	}

	public double getDamage() {
		double damage = super.getDamage();
		double p = 1.0D + getRStrength() / 100.0D;
		if ((getCritical() > 0) && ((getRIntelligence() / 500.0F + getCritical()) / 200.0F > Math.random())) {
			p = 2.0D;
		}
		for (Pair<Spell, Integer> value : this.spells.values())
			if (value.first.isActive())
				damage += value.first.getDamage();

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
		List<String> possible = ClazzLoader.getSpells(this.type);
		for (String id : spells.keySet()) {
			if (!possible.contains(spells.get(id).first.getName()))
				continue;
			if (id.equals(String.valueOf(spell[0]) + String.valueOf(spell[1]) + String.valueOf(spell[2]))) {
				Spell spell = spells.get(id).first;
				if (spell != null)
					if (spells.get(id).second > 0) {
						Debugger.info(LogTitle.SPELL, "%s is casting spell:", getPlayer().getName(), spell.getName());
						spell.execute();
						BottomBar.display(getPlayer(), ChatColor.GOLD + spell.getName());
						return;
					} else {
						BottomBar.display(getPlayer(), ChatColor.RED + Msg.getMessage(Key.SPELL_NO_POWER));
						return;
					}
			}
		}
		Debugger.error(LogTitle.SPELL, "%s tried to cast spell:", getPlayer().getName(), String.valueOf(spell[0]), String.valueOf(spell[1]),
				String.valueOf(spell[2]));
		BottomBar.display(getPlayer(), ChatColor.RED + "" + ChatColor.MAGIC + "Uups");
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
		config.set("spell-points", spellPoints);
		config.set("quests", getQString());
		if (!config.isConfigurationSection("spells"))
			config.createSection("spells");
		for (Pair<Spell, Integer> value : spells.values()) {
			config.set("spells." + value.first.getName(), value.second);
		}

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
			spellPoints = config.getInt("spell-points", 0);
			setQuest(config.getString("quests"));
			if (config.isConfigurationSection("spells")) {
				for (String spell : config.getConfigurationSection("spells").getKeys(false)) {
					int power = config.getInt("spells." + spell, -1);
					Spell s = SpellLoader.load(this, spell);
					if (s != null) {
						spells.put(spell, new Pair<Spell, Integer>(s, power));
						s.reloadPower();
					}
				}
			}

			return true;
		} else
			return false;
	}

	public boolean isInvisible() {
		for (Pair<Spell, Integer> spell : this.spells.values()) {
			if (((spell.first instanceof InvisibleSpell)) && (spell.first.isActive())) {
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
		this.spells.clear();
	}

	public boolean isLoaded() {
		return loaded;
	}

	public void setLoaded(boolean loaded) {
		this.loaded = loaded;
	}

	public void addSpell(String id, String spell) {
		Pair<Spell, Integer> pair = null;
		if (spells.containsKey(spell)) // in case there is a loaded spell dummy
			pair = spells.get(spell);
		else {
			Spell s = SpellLoader.load(this, spell);
			if (s != null) {
				pair = new Pair<Spell, Integer>(s, 0);
				s.reloadPower();
			}
		}
		if (pair != null)
			spells.put(id, pair);
		else
			Debugger.warning(LogTitle.SPELL, "%s gave a null loaded spell with spell=%s (in RolecraftPlayer.addSpell)", getName(), spell);
		spells.remove(spell); // removing spell dummy
	}

	public int getSpellPower(String spellName) {
		for (Pair<Spell, Integer> value : spells.values()) {
			if (value.first.getName().equals(spellName))
				return value.second;
		}
		return -1;
	}

	public HashMap<String, Pair<Spell, Integer>> getSpells() {
		return spells;
	}

	public int getSpellPoints() {
		return spellPoints;
	}

	public boolean spawnPet(Location loc, String name, String customName) {
		Pet pet = Pet.spawnPet(name, customName, loc);
		Debugger.info(LogTitle.PET, "%s is trying to spawn his pet: (name=%s)", getPlayer().getName(), name, customName);
		if (pet == null)
			return false;
		Debugger.info(LogTitle.PET, "%s successfully spawned: (name=%s)", getPlayer().getName(), name, customName);
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

	public void powerUpSpell(String name) {
		if (spellPoints <= 0)
			return;
		for (String k : spells.keySet()) {
			if (spells.get(k).first.getName().equals(name)) {
				Pair<Spell, Integer> p = new Pair<Spell, Integer>(spells.get(k).first, spells.get(k).second + 1);
				spells.put(k, p);
				spellPoints--;
				break;
			}
		}
	}

	public void giveSpellPoint() {
		this.spellPoints++;
		Spellbook.giveSpellbookToPlayer(this);
	}

}
