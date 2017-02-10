package org.caliog.Rolecraft.XMechanics.Commands.Utils;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.bukkit.entity.Player;
import org.caliog.Rolecraft.Manager;
import org.caliog.Rolecraft.Entities.Player.PlayerManager;
import org.caliog.Rolecraft.XMechanics.Debug.Debugger;
import org.caliog.Rolecraft.XMechanics.Debug.Debugger.LogTitle;

public class CommandRegister {

	public Set<Command> cmds = new LinkedHashSet<Command>();

	public CommandRegister() {
		init();
	}

	public boolean executeCommand(String name, String args[], Player player) {
		Object obj = identifyCommand(name, args);
		if (obj instanceof Command) {
			Command cmd = (Command) obj;
			// LOG
			Debugger.info(LogTitle.CMD, player.getName() + " executed: " + name, args);

			if (args.length >= cmd.getMin() && args.length <= cmd.getMax())
				new CommandExe((Command) obj, args, player);
			else {
				new CommandHelp(cmd, player);
			}
			return true;
		} else if (obj instanceof String) {
			new CommandHelp((String) obj, cmds, player);
			return true;
		} else if (obj instanceof Integer) {
			return false;
		}
		return false;
	}

	private Object identifyCommand(String name, String[] args) {
		Command backsave = null;
		if (isCommand(name)) {
			for (Command cmd : cmds) {
				if ((cmd.getFields() == null || cmd.getFields().length == 0) && (args == null || args.length == 0)) {
					if (cmd.getName().equalsIgnoreCase(name))
						return cmd;
					else
						continue;
				} else if (cmd.getFields() == null || cmd.getFields().length == 0)
					continue;
				else if ((cmd.getIdentifiers() == null || cmd.getIdentifiers().length == 0) && CommandExe.checkFields(cmd, args, null)
						&& args.length >= cmd.getMin() && args.length <= cmd.getMax()) {
					if (cmd.getName().equals(name))
						return cmd;
					else
						continue;
				} else if (cmd.getIdentifiers() == null)
					continue;
				else if (args == null || args.length == 0)
					continue;
				boolean identified = true;
				for (int i = 0; i < cmd.getIdentifiers().length && i < args.length; i++) {

					if (!args[i].equalsIgnoreCase(cmd.getIdentifiers()[i].getName()))
						identified = false;
				}
				if (identified && cmd.getName().equalsIgnoreCase(name) && args.length >= cmd.getMin() && args.length <= cmd.getMax())
					return cmd;
				else if (identified && cmd.getName().equalsIgnoreCase(name)) {
					backsave = cmd;
					continue;
				}
			}
			if (backsave == null)
				return name;
			else {
				return backsave;
			}
		} else
			return 0;

	}

	private boolean isCommand(String name) {
		for (Command cmd : cmds)
			if (cmd.getName().equalsIgnoreCase(name))
				return true;
		return false;
	}

	private void init() {
		ClassLoader loader = CommandRegister.class.getClassLoader();
		List<Class<?>> list = new ArrayList<Class<?>>();

		for (String c : Manager.plugin.getDescription().getCommands().keySet()) {
			if (c.equals("!"))
				continue;
			try {
				list.add(loader.loadClass("org.caliog.Rolecraft.XMechanics.Commands.Command" + c));
			} catch (ClassNotFoundException e) {
				Manager.plugin.getLogger().warning("Could not find " + c);
				e.printStackTrace();
			}
		}

		for (Class<?> c : list) {
			try {
				Commands commands = (Commands) c.getConstructor().newInstance();
				cmds.addAll(commands.getCommands());
			} catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException
					| NoSuchMethodException | SecurityException e) {
				e.printStackTrace();
			}
		}

	}

	public boolean playerHasPermissionCMD(Player player, Command cmd) {
		return PlayerManager.getPlayer(player.getUniqueId()).hasPermission(cmd.getPermission());
	}

	public Set<Command> getPermittedCommands(Player player) {
		Set<Command> list = new LinkedHashSet<Command>();
		for (Command cmd : cmds) {
			if (PlayerManager.getPlayer(player.getUniqueId()).hasPermission(cmd.getPermission()))
				list.add(cmd);
		}

		return list;
	}
}
