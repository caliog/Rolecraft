package org.caliog.Rolecraft;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.java.JavaPlugin;
import org.caliog.Rolecraft.Entities.Player.ClazzLoader;
import org.caliog.Rolecraft.XMechanics.RolecraftConfig;
import org.caliog.Rolecraft.XMechanics.Commands.Utils.CommandRegister;
import org.caliog.Rolecraft.XMechanics.Debug.Debugger;
import org.caliog.Rolecraft.XMechanics.Debug.Debugger.LogTitle;
import org.caliog.Rolecraft.XMechanics.Listeners.DamageListener;
import org.caliog.Rolecraft.XMechanics.Listeners.DeathListener;
import org.caliog.Rolecraft.XMechanics.Listeners.MenuListener;
import org.caliog.Rolecraft.XMechanics.Listeners.RolecraftListener;
import org.caliog.Rolecraft.XMechanics.Listeners.VillagerListener;
import org.caliog.Rolecraft.XMechanics.Messages.Msg;
import org.caliog.Rolecraft.XMechanics.Resource.DataFolder;
import org.caliog.Rolecraft.XMechanics.Resource.FileCreator;
import org.caliog.Rolecraft.XMechanics.Resource.FilePath;
import org.caliog.Rolecraft.XMechanics.Utils.Metrics;
import org.caliog.Rolecraft.XMechanics.Utils.Updater;
import org.caliog.Rolecraft.XMechanics.Utils.Updater.UpdateCallback;
import org.caliog.Rolecraft.XMechanics.Utils.Updater.UpdateType;
import org.caliog.Rolecraft.XMechanics.Utils.Utils;

public class RolecraftPlugin extends JavaPlugin {
	public CommandRegister cmdReg;
	private String bukkitVersion;
	private FileCreator fc = new FileCreator();
	int backupTask;

	public void onEnable() {
		String pN = Bukkit.getServer().getClass().getPackage().getName();
		bukkitVersion = pN.substring(pN.lastIndexOf(".") + 1);

		if (!bukkitVersion.equalsIgnoreCase("v1_12_R1") && !bukkitVersion.equalsIgnoreCase("v1_11_R1")) {
			getLogger().warning("\u001B[31mGuards will not work with your bukkit version. \u001B[0m");
		}

		mkdir();

		Manager.plugin = this;

		cmdReg = new CommandRegister();

		RolecraftConfig.init();
		Debugger.info(LogTitle.NONE, "Enabled Rolecraft version:", bukkitVersion);

		createMIC();
		createSpellCollection();

		Manager.load();

		getServer().getPluginManager().registerEvents(new RolecraftListener(), this);
		getServer().getPluginManager().registerEvents(new DamageListener(), this);
		getServer().getPluginManager().registerEvents(new DeathListener(), this);
		getServer().getPluginManager().registerEvents(new VillagerListener(), this);
		getServer().getPluginManager().registerEvents(new MenuListener(), this);

		Manager.scheduleRepeatingTask(Manager.getTask(), 20L, 1L);

		if (RolecraftConfig.getBackupTime() > 0)
			backupTask = Manager.scheduleRepeatingTask(DataFolder.backupTask(), 20L * 60L * RolecraftConfig.getBackupTime(),
					20L * 60L * RolecraftConfig.getBackupTime());

		try {
			Metrics metrics = new Metrics(this);
			metrics.start();
		} catch (IOException e) {
		}
		searchForNewVersion();
		getLogger().info(getDescription().getFullName() + " enabled!");
	}

	public void onDisable() {
		Manager.save();

		Manager.cancelAllTasks();
		HandlerList.unregisterAll(this);
		getLogger().info(getDescription().getFullName() + " disabled!");
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		Player player = null;
		if (sender instanceof Player)
			player = (Player) sender;
		else {
			sender.sendMessage(ChatColor.RED + "Only for players, sorry!");
			return false;
		}

		String command = "";
		for (String a : args)
			command += a + " ";
		command = command.trim();
		int count = command.length() - command.replace("\"", "").length();
		String a[] = new String[args.length];
		if (count < 2) {
			a = args;
		} else {

			int counter = 0;
			for (int i = 0; i < args.length; i++) {

				if ((args[i].startsWith("\""))) {
					a[counter] = "";
					while (i < args.length && !args[i].endsWith("\"")) {
						a[counter] += " " + args[i].replace("\"", "");
						i++;
					}
					if (i < args.length)
						a[counter] += " " + args[i].replace("\"", "");
					a[counter] = a[counter].trim();
					counter++;
				} else if (counter < a.length) {
					a[counter] = args[i];
					counter++;
				}

			}
		}
		return cmdReg.executeCommand(cmd.getName(), Utils.removeNull(a), player);

	}

	private void mkdir() {
		for (Field f : FilePath.class.getFields()) {
			String value;
			try {
				value = (String) f.get(this);
			} catch (IllegalArgumentException | IllegalAccessException e1) {
				e1.printStackTrace();
				continue;
			}
			if (value.equals(FilePath.mic))
				continue;
			if (value.equals(FilePath.spellCollection))
				continue;
			if (value.equals(FilePath.messages))
				continue;
			String[] split = value.split("/");
			String name = split[split.length - 1];
			File file = new File(value);
			if (!file.exists()) {
				try {
					if (value.endsWith("/")) {
						file.mkdir();
					} else {
						file.createNewFile();
						fc.copyFile(value, name);
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

	}

	public String getBukkitVersion() {
		return bukkitVersion;
	}

	private void createSpellCollection() {
		if (RolecraftConfig.isSpellCollectionEnabled()) {
			try {
				File file = new File(FilePath.spellCollection);
				if (!file.exists()) {
					file.createNewFile();
					fc.copyFile(FilePath.spellCollection, "SpellCollection.jar");
				}

			} catch (IOException e) {
				getLogger().warning("Failed to create SpellCollection.jar!");
			}
		}

	}

	public boolean createMIC() {
		return createMIC(null);
	}

	public boolean createMIC(final Player player) {
		final File micFile = new File(FilePath.mic);
		if (!micFile.exists() || (micFile.exists() && System.currentTimeMillis() - micFile.lastModified() > 1e6))
			if (!RolecraftConfig.isMICDisabled()) {
				try {
					micFile.createNewFile();
					fc.copyFile(FilePath.mic, "MIC.jar");
					if (player != null)
						player.sendMessage(ChatColor.GOLD + "Created MIC.jar in your Rolecraft folder!");
				} catch (IOException e) {
					e.printStackTrace();
					if (player != null)
						player.sendMessage(ChatColor.GOLD + "Something went wrong..!");
				}
			} else {
				if (player != null)
					player.sendMessage(ChatColor.GOLD + "You have to set 'disable-mic' to false.");
			}

		return true;
	}

	public void reload() {
		Debugger.info(LogTitle.NONE, "Reloading Rolecraft version:", bukkitVersion);
		RolecraftConfig.config = YamlConfiguration.loadConfiguration(new File(FilePath.config));
		Msg.file = YamlConfiguration.loadConfiguration(new File(FilePath.messages));
		ClazzLoader.classes = YamlConfiguration.loadConfiguration(new File(FilePath.classes));
		Manager.cancelTask(backupTask);
		if (RolecraftConfig.getBackupTime() > 0)
			backupTask = Manager.scheduleRepeatingTask(DataFolder.backupTask(), 20L * 60L * RolecraftConfig.getBackupTime(),
					20L * 60L * RolecraftConfig.getBackupTime());
	}

	private void searchForNewVersion() {
		if (RolecraftConfig.isUpdateEnabled()) {
			new Updater(this, 45030, this.getFile(), UpdateType.NO_DOWNLOAD, new UpdateCallback() {

				@Override
				public void onFinish(Updater updater) {
					if (updater.getResult().equals(Updater.UpdateResult.UPDATE_AVAILABLE))
						getLogger().info("There is a new version (" + updater.getLatestName().replace("Rolecraft", "").trim()
								+ ") of Rolecraft available!");

				}
			});
		}
	}
}
