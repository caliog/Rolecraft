package org.caliog.Villagers.NPC;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Villager.Profession;
import org.caliog.Villagers.Chat.CMessage;
import org.caliog.Villagers.Chat.CMessage.MessageType;
import org.caliog.Villagers.Chat.ChatTask;
import org.caliog.myRPG.Entities.ClazzLoader;
import org.caliog.myRPG.Entities.PlayerManager;
import org.caliog.myRPG.Entities.myClass;
import org.caliog.myRPG.Messages.Msg;

public class Priest extends Villager {

	Set<UUID> players = new HashSet<UUID>();

	public Priest(org.bukkit.entity.Villager entity, Location location, String name) {
		super(entity, VillagerType.PRIEST, location, name);
		this.profession = Profession.PRIEST;
		String msg = Msg.getMessage("class-change-offer", Msg.CLASS, getClassType());
		addCMessage(1, new CMessage(msg, MessageType.QUESTION, 3));
		CMessage message = new CMessage(Msg.getMessage("class-changed", Msg.CLASS, getClassType()), MessageType.END);
		message.setTask(new ChatTask() {

			@Override
			public void execute(myClass player, Villager villager) {
				PlayerManager.changeClass(player.getPlayer(), ChatColor.stripColor(getClassType()));

			}
		});
		addCMessage(2, message);
		addCMessage(3, new CMessage("Ok,bye!", MessageType.END));
	}

	private String getClassType() {
		String n = ((LivingEntity) this.getBukkitEntity()).getCustomName();
		if (ClazzLoader.isClass(ChatColor.stripColor(n)))
			return n;
		else
			return null;

	}

}
