package org.caliog.myRPG.Entities;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.caliog.Villagers.Quests.QManager;
import org.caliog.Villagers.Quests.Quest;
import org.caliog.myRPG.Manager;
import org.caliog.myRPG.Commands.Utils.Permissions;
import org.caliog.myRPG.Items.Armor;
import org.caliog.myRPG.Items.CustomItem;
import org.caliog.myRPG.Items.Weapon;
import org.caliog.myRPG.Utils.QuestStatus;

public abstract class myPlayer extends Fighter {
	private final Player player;
	private HashMap<String, QuestStatus> quests = new HashMap<String, QuestStatus>();
	private Set<String> permissions;

	public myPlayer(final Player player) {
		this.player = player;
		Manager.scheduleTask(new Runnable() {

			@Override
			public void run() {
				setPermissions(Permissions.getPermissions(player));
			}
		}, 30L);

	}

	public double getMaximumHealth() {
		int l = getLevel();
		double h = 4 * Math.round((Math.sqrt(l)));
		return h;
	}

	public void giveExp(int exp) {
		Playerface.giveExp(getPlayer(), exp);
	}

	public Player getPlayer() {
		return this.player;
	}

	public double getDefense() {
		int defense = 0;
		ArrayList<ItemStack> list = new ArrayList<ItemStack>();
		for (ItemStack stack : player.getInventory().getArmorContents())
			list.add(stack);
		list.add(player.getInventory().getItemInOffHand());
		for (ItemStack s : list) {
			if (Armor.isArmor(s) && (!s.getType().equals(Material.SHIELD) || this.getPlayer().isBlocking())) {
				Armor armor = Armor.getInstance(s);
				defense += armor.getDefense();
			}
		}
		return defense;
	}

	public double getDamage() {
		int damage = 0;
		if ((Weapon.isWeapon((myClass) this, getPlayer().getInventory().getItemInMainHand()))) {
			damage += Weapon.getInstance((myClass) this, getPlayer().getInventory().getItemInMainHand()).getRandomDamage();
		} else if (damage == 0) {
			damage++;
		}
		return damage;
	}

	public String getName() {
		return this.player.getName();
	}

	public int getLevel() {
		return this.player.getLevel();
	}

	public List<CustomItem> getEquipment() {
		List<CustomItem> s = new ArrayList<CustomItem>();
		for (ItemStack stack : this.player.getEquipment().getArmorContents()) {
			if (Armor.isArmor(stack)) {
				s.add(Armor.getInstance(stack));
			}
		}
		if (Weapon.isWeapon((myClass) this, this.player.getInventory().getItemInMainHand())) {
			s.add(Weapon.getInstance((myClass) this, this.player.getInventory().getItemInMainHand()));
		}
		while (s.contains(null)) {
			s.remove(null);
		}
		return s;
	}

	public void newQuest(String name) {
		this.quests.put(name, QuestStatus.FIRST);
	}

	public void completeQuest(String name) {
		Quest q = QManager.getQuest(name);
		QManager.updateQuestBook(PlayerManager.getPlayer(this.getPlayer().getUniqueId()));
		if (q == null)
			return;
		Playerface.takeItem(getPlayer(), q.getCollects());
		giveExp(q.getExp());
		Playerface.giveItem(getPlayer(), q.getRewards());
		this.quests.put(name, QuestStatus.COMPLETED);
	}

	public void raiseQuestStatus(String name) {
		if (this.quests.containsKey(name)) {
			this.quests.put(name, ((QuestStatus) this.quests.get(name)).raise());
		}
	}

	public List<String> getCompletedQuests() {
		List<String> l = new ArrayList<String>();
		for (String q : quests.keySet())
			if (quests.get(q).equals(QuestStatus.COMPLETED))
				l.add(q);
		return l;
	}

	public List<String> getUnCompletedQuests() {
		List<String> l = new ArrayList<String>();
		for (String q : quests.keySet())
			if (!quests.get(q).equals(QuestStatus.COMPLETED))
				l.add(q);
		return l;
	}

	public boolean isCompleted(String id) {
		if (this.quests.containsKey(id)) {
			return ((QuestStatus) this.quests.get(id)).equals(QuestStatus.COMPLETED);
		}
		return false;
	}

	public QuestStatus getQuestStatus(String string) {
		if (this.quests.containsKey(string)) {
			return (QuestStatus) this.quests.get(string);
		}
		return QuestStatus.UNACCEPTED;
	}

	public String getQString() {
		String r = "";
		if (quests.isEmpty())
			return null;
		for (String q : quests.keySet())
			r = r + q + ":" + quests.get(q).getInt() + ";";

		return (r + "..").replace(";..", "");
	}

	public void setQuest(String a) {
		if (a == null)
			return;
		String[] es = a.split(";");
		for (String e : es) {
			this.quests.put(e.split(":")[0], QuestStatus.fromInt(Integer.parseInt(e.split(":")[1])));
		}
	}

	public void reset() {
		this.player.setLevel(1);
		this.player.setExp(0F);
	}

	public Set<String> getPermissions() {
		return permissions;
	}

	public void setPermissions(Set<String> set) {
		this.permissions = set;
	}

	public boolean hasPermission(String permission) {
		if (permission == null) {
			return true;
		}
		return permissions.contains(permission);

	}
}