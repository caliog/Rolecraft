package org.caliog.Rolecraft.Villagers.Quests;

import java.util.HashMap;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;
import org.caliog.Rolecraft.Entities.Player.Playerface;
import org.caliog.Rolecraft.Entities.Player.RolecraftPlayer;
import org.caliog.Rolecraft.Utils.QuestStatus;
import org.caliog.Rolecraft.Villagers.Chat.CMessage;
import org.caliog.Rolecraft.Villagers.NPC.Villager;

public class SimpleQuest extends Quest {

	private String requiredQuest;
	private int minLevel;
	private String clazz;
	private int exp;
	private ItemStack receive;
	private Villager target;

	public SimpleQuest(String name) {
		super(name);
	}

	@Override
	public Location getTargetLocation(RolecraftPlayer player) {
		if (player.getQuestStatus(getName()).equals(QuestStatus.FIRST))
			if (target != null) {

				if (target != null) {
					return target.getEntityLocation();
				}
			}

		return null;

	}

	@Override
	public HashMap<Integer, CMessage> getMessages() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int getMessageStart(RolecraftPlayer p) {
		// TODO
		if (getTargetLocation(p) != null) {
			return -1;
		} else if (this.couldComplete(p)) {
			p.completeQuest(getName());
			return -1;
		} else if (!p.getQuestStatus(getName()).equals(QuestStatus.UNACCEPTED)
				&& p.getQuestStatus(this.getName()).isLowerThan(QuestStatus.COMPLETED)) {
			return -1;
		} else {

			return 0;// default start with id 0
		}
	}

	@Override
	public List<ItemStack> getRewards() {
		return rewards;
	}

	@Override
	public List<ItemStack> getCollects() {
		return collects;
	}

	@Override
	public ItemStack getReceive() {
		return receive;
	}

	@Override
	public HashMap<String, Integer> getMobs() {
		return mobs;
	}

	@Override
	public int getExp() {
		return (int) (Playerface.getExpDifference(getMinLevel(), getMinLevel() + 1) * (exp / 100F));
	}

	@Override
	public String getClazz() {
		return clazz;
	}

	@Override
	public int getMinLevel() {
		return minLevel;
	}

	@Override
	public String getDescription() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getChainQuest() {
		return requiredQuest;
	}

	@Override
	public QuestStatus hasToReach() {
		return QuestStatus.FIRST;
	}

}
