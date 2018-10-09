package org.caliog.Rolecraft.XMechanics.Commands.Utils;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.bukkit.entity.Player;
import org.caliog.Rolecraft.Manager;
import org.caliog.Rolecraft.Entities.Player.PlayerManager;
import org.caliog.Rolecraft.XMechanics.Utils.IO.Debugger;
import org.caliog.Rolecraft.XMechanics.Utils.IO.Debugger.LogTitle;

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
				CommandHelp.sendCommandHelp(cmd, player);
			}
			return true;
		} else if (obj instanceof String) {
			CommandHelp.sendCommandHelp((String) obj, cmds, player);
			return true;
		} else if (obj instanceof Integer) {
			return false;
		}
		return false;
	}

	private Object identifyCommand(String name, String[] args) {
		List<Command> list = getCommandsByName(name);
		HashMap<Command, Integer> map = new HashMap<Command, Integer>();
		if (list.isEmpty())
			return 0;
		for (Command cmd : list) {
			if (cmd.getFields() == null || cmd.getFields().length == 0) {
				if (args == null || args.length == 0)
					return cmd;
				else
					map.put(cmd, 0);
			} else {
				if (args.length >= cmd.getMin()) {
					boolean identified = true;
					for (int i = 0; i < cmd.getIdentifiers().length && i < args.length; i++) {
						if (!args[i].equalsIgnoreCase(cmd.getIdentifiers()[i].getName()))
							identified = false;
					}
					if (identified && CommandExe.checkFields(cmd, args, null)) {
						map.put(cmd, cmd.getIdentifiers().length);
					} else if (identified) {
						map.put(cmd, cmd.getIdentifiers().length);
					}
				}
			}
		}

		if (map.isEmpty())
			return name;
		else {
			int max = -1;
			Command cmd = null;
			for (Command c : map.keySet()) {
				if (map.get(c) > max) {
					cmd = c;
					max = map.get(c);
				}
			}
			return cmd;
		}
	}

	//	private Object identifyyCommand(String name, String[] args) {
	//		Command backsave = null;
	//		boolean found = false;
	//
	//		for (Command cmd : cmds) {
	//			if (cmd.getName().equalsIgnoreCase(name)) {
	//				found = true;
	//
	//				if ((cmd.getFields() == null || cmd.getFields().length == 0) && (args == null || args.length == 0)) {
	//					if (cmd.getName().equalsIgnoreCase(name))
	//						return cmd;
	//					else
	//						continue;
	//				} else if (cmd.getFields() == null || cmd.getFields().length == 0)
	//					continue;
	//				else if ((cmd.getIdentifiers() == null || cmd.getIdentifiers().length == 0)
	//						&& CommandExe.checkFields(cmd, args, null) && args.length >= cmd.getMin()
	//						&& args.length <= cmd.getMax()) {
	//					if (cmd.getName().equals(name))
	//						return cmd;
	//					else
	//						continue;
	//				} else if (cmd.getIdentifiers() == null)
	//					continue;
	//				else if (args == null || args.length == 0)
	//					continue;
	//				boolean identified = true;
	//				for (int i = 0; i < cmd.getIdentifiers().length && i < args.length; i++) {
	//
	//					if (!args[i].equalsIgnoreCase(cmd.getIdentifiers()[i].getName()))
	//						identified = false;
	//				}
	//				if (identified && cmd.getName().equalsIgnoreCase(name) && args.length >= cmd.getMin()
	//						&& args.length <= cmd.getMax())
	//					return cmd;
	//				else if (identified && cmd.getName().equalsIgnoreCase(name)) {
	//					backsave = cmd;
	//					continue;
	//				}
	//			}
	//		}
	//		if (found)
	//			if (backsave == null)
	//				return name;
	//			else {
	//				return backsave;
	//			}
	//
	//		return 0;
	//
	//	}

	private List<Command> getCommandsByName(String name) {
		ArrayList<Command> list = new ArrayList<Command>();
		for (Command cmd : cmds) {
			if (name.equalsIgnoreCase(cmd.getName())) {
				list.add(cmd);
			}
		}
		return list;
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
			} catch (InstantiationException | IllegalAccessException | IllegalArgumentException
					| InvocationTargetException | NoSuchMethodException | SecurityException e) {
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
