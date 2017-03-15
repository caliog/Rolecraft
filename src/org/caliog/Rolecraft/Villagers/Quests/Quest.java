package org.caliog.Rolecraft.Villagers.Quests;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;
import org.caliog.Rolecraft.Entities.Player.ClazzLoader;
import org.caliog.Rolecraft.Entities.Player.RolecraftAbstrPlayer;
import org.caliog.Rolecraft.Entities.Player.RolecraftPlayer;
import org.caliog.Rolecraft.Utils.QuestStatus;
import org.caliog.Rolecraft.Villagers.Chat.CMessage;

public abstract class Quest {

	protected HashMap<Integer, CMessage> messages = new HashMap<Integer, CMessage>();
	protected List<ItemStack> rewards = new ArrayList<ItemStack>();
	protected List<ItemStack> collects = new ArrayList<ItemStack>();
	protected HashMap<String, Integer> mobs = new HashMap<String, Integer>();
	protected final String name;
	protected List<ItemStack> receives = new ArrayList<ItemStack>();

	public Quest(String name) {
		this.name = name;
	}

	public abstract Location getTargetLocation(RolecraftPlayer player);

	public HashMap<Integer, CMessage> getMessages() {
		return messages;
	}

	public abstract int getMessageStart(RolecraftPlayer p);

	public List<ItemStack> getRewards() {
		return rewards;
	}

	public List<ItemStack> getCollects() {
		return collects;
	}

	public List<ItemStack> getReceives() {
		return receives;
	}

	public HashMap<String, Integer> getMobs() {
		return mobs;
	}

	public abstract int getExp();

	public abstract String getClazz();

	public boolean hasClazz() {
		return getClazz() != null && ClazzLoader.isClass(getClazz());
	}

	public abstract int getMinLevel();

	public boolean hasMinLevel() {
		return getMinLevel() > 0;
	}

	public abstract String getDescription();

	public String getName() {
		return name;
	}

	public abstract String getChainQuest();

	public boolean isChainQuest() {
		return getChainQuest() != null && QManager.getQuest(getChainQuest()) != null;
	}

	public abstract QuestStatus hasToReach();

	public boolean couldComplete(RolecraftAbstrPlayer player) {
		if (!player.getUnCompletedQuests().contains(getName()))
			return false;
		if (player.getQuestStatus(getName()).isLowerThan(hasToReach()))
			return false;

		// Attention! QuestStatus SECOND means the player visited the target
		// villager which already took the collect items
		if (!(this instanceof YmlQuest && player.getQuestStatus(getName()).equals(QuestStatus.SECOND)))
			if (getCollects() != null && !getCollects().isEmpty()) {
				for (ItemStack stack : getCollects()) {
					if (!player.getPlayer().getInventory().containsAtLeast(stack, stack.getAmount())) {
						return false;
					}
				}
			}

		if (getMobs() != null && !getMobs().isEmpty())
			if (!QuestKill.isFinished(player.getPlayer(), getName())) {
				return false;
			}

		return true;
	}

}
