package org.caliog.Villagers.Quests;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;
import org.caliog.Villagers.Chat.CMessage;
import org.caliog.myRPG.Entities.ClazzLoader;
import org.caliog.myRPG.Entities.myClass;
import org.caliog.myRPG.Utils.QuestStatus;

public abstract class Quest {

	protected List<ItemStack> rewards = new ArrayList<ItemStack>();
	protected List<ItemStack> collects = new ArrayList<ItemStack>();
	protected HashMap<String, Integer> mobs = new HashMap<String, Integer>();
	protected final String name;

	public Quest(String name) {
		this.name = name;
	}

	public abstract Location getTargetLocation(myClass player);

	public abstract HashMap<Integer, CMessage> getMessages();

	public abstract int getMessageStart(myClass p);

	public abstract List<ItemStack> getRewards();

	public abstract List<ItemStack> getCollects();

	public abstract ItemStack getReceive();

	public abstract HashMap<String, Integer> getMobs();

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

	public boolean couldComplete(myClass player) {
		if (!player.getUnCompletedQuests().contains(getName()))
			return false;
		if (player.getQuestStatus(getName()).isLowerThan(hasToReach()))
			return false;

		if (getCollects() != null && !getCollects().isEmpty()) {
			for (ItemStack stack : getCollects()) {
				if (!player.getPlayer().getInventory().containsAtLeast(stack, stack.getAmount())) {
					return false;
				}
			}
		}

		if (getMobs() != null && !getMobs().isEmpty())
			for (String c : getMobs().keySet()) {
				if (QuestKill.getKilled(player.getPlayer(), c) < getMobs().get(c)) {
					return false;
				}
			}

		return true;
	}
}
