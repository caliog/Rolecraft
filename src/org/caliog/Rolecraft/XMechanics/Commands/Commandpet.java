package org.caliog.Rolecraft.XMechanics.Commands;

import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.caliog.Rolecraft.Entities.EntityUtils;
import org.caliog.Rolecraft.Mobs.MobInstance;
import org.caliog.Rolecraft.Mobs.Pets.Pet;
import org.caliog.Rolecraft.XMechanics.Commands.Utils.Command;
import org.caliog.Rolecraft.XMechanics.Commands.Utils.CommandExecutable;
import org.caliog.Rolecraft.XMechanics.Commands.Utils.CommandField;
import org.caliog.Rolecraft.XMechanics.Commands.Utils.Commands;
import org.caliog.Rolecraft.XMechanics.Commands.Utils.CommandField.FieldProperty;

public class Commandpet extends Commands {

	@Override
	public List<Command> getCommands() {
		/*
		 * Name: pet
		 * 
		 * SubName: create
		 * 
		 * Permission: rc.pet.create
		 * 
		 * Usage: /pet create <mob> <name>
		 */
		cmds.add(new Command("pet", "rc.pet.create", new CommandExecutable() {

			@Override
			public void execute(String[] args, Player player) {
				if (EntityUtils.isMobClass(args[1])) {
					MobInstance mob = new MobInstance(args[1], null, null);
					if (mob.isPet()) {
						if (args[2].length() >= 17)
							args[2] = args[2].substring(0, 17);
						String name = args[2];
						Pet.givePetEgg(player, args[1], name);
					} else
						player.sendMessage(ChatColor.GOLD + args[1] + "is not a pet!");
				} else
					player.sendMessage(ChatColor.GOLD + args[1] + " is not a mob!");
			}
		}, new CommandField("create", FieldProperty.IDENTIFIER), new CommandField("mob", FieldProperty.REQUIRED),
				new CommandField("name", FieldProperty.REQUIRED)));

		return cmds;
	}

}
