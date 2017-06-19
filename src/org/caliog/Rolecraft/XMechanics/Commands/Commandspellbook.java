package org.caliog.Rolecraft.XMechanics.Commands;

import java.util.List;

import org.bukkit.entity.Player;
import org.caliog.Rolecraft.Entities.Player.PlayerManager;
import org.caliog.Rolecraft.Entities.Player.RolecraftPlayer;
import org.caliog.Rolecraft.Items.Books.Spellbook;
import org.caliog.Rolecraft.XMechanics.Commands.Utils.Command;
import org.caliog.Rolecraft.XMechanics.Commands.Utils.CommandExecutable;
import org.caliog.Rolecraft.XMechanics.Commands.Utils.Commands;

public class Commandspellbook extends Commands {

	@Override
	public List<Command> getCommands() {

		cmds.add(new Command("spellbook", null, new CommandExecutable() {

			@Override
			public void execute(String[] args, Player player) {
				RolecraftPlayer p = PlayerManager.getPlayer(player.getUniqueId());
				if (p != null)
					Spellbook.giveSpellbookToPlayer(p);
			}
		}));

		return cmds;
	}

}
