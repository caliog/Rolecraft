package org.caliog.Rolecraft;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.java.JavaPlugin;
import org.caliog.Rolecraft.Entities.Player.ClazzLoader;
import org.caliog.Rolecraft.Mobs.MobSpawner;
import org.caliog.Rolecraft.XMechanics.RolecraftConfig;
import org.caliog.Rolecraft.XMechanics.Commands.Utils.CommandRegister;
import org.caliog.Rolecraft.XMechanics.Listeners.DamageListener;
import org.caliog.Rolecraft.XMechanics.Listeners.DeathListener;
import org.caliog.Rolecraft.XMechanics.Listeners.MenuListener;
import org.caliog.Rolecraft.XMechanics.Listeners.RolecraftListener;
import org.caliog.Rolecraft.XMechanics.Listeners.VillagerListener;
import org.caliog.Rolecraft.XMechanics.Messages.Msg;
import org.caliog.Rolecraft.XMechanics.Resource.DataFolder;
import org.caliog.Rolecraft.XMechanics.Resource.FileCreator;
import org.caliog.Rolecraft.XMechanics.Resource.FilePath;
import org.caliog.Rolecraft.XMechanics.Utils.Utils;
import org.caliog.Rolecraft.XMechanics.Utils.IO.Debugger;
import org.caliog.Rolecraft.XMechanics.Utils.IO.Debugger.LogTitle;
import org.caliog.Rolecraft.XMechanics.Utils.IO.Metrics;
import org.caliog.Rolecraft.XMechanics.Utils.IO.Updater;
import org.caliog.Rolecraft.XMechanics.Utils.IO.Updater.UpdateCallback;
import org.caliog.Rolecraft.XMechanics.Utils.IO.Updater.UpdateType;

public class RolecraftPlugin extends JavaPlugin {
	public CommandRegister cmdReg;
	private String serverVersion;
	private FileCreator fc = new FileCreator();
	private int backupTask;

	private String needsUpdate = null;

	public void onEnable() {
		serverVersion = Bukkit.getServer().getClass().getPackage().getName().substring(23);
		if (!serverVersion.equalsIgnoreCase("v1_12_R1") && !serverVersion.equalsIgnoreCase("v1_11_R1")
				&& !serverVersion.equalsIgnoreCase("v1_13_R2")) {
			getLogger().warning("\u001B[31mGuards will not work with your bukkit version. \u001B[0m");
		}

		mkdir();

		Manager.plugin = this;

		cmdReg = new CommandRegister();

		RolecraftConfig.init();
		Debugger.info(LogTitle.NONE, "Enabled :", getDescription().getFullName());
		Debugger.info(LogTitle.NONE, "Server version:", serverVersion);

		createSpellCollection();

		Manager.load();

		getServer().getPluginManager().registerEvents(new RolecraftListener(), this);
		getServer().getPluginManager().registerEvents(new DamageListener(), this);
		getServer().getPluginManager().registerEvents(new DeathListener(), this);
		getServer().getPluginManager().registerEvents(new VillagerListener(), this);
		getServer().getPluginManager().registerEvents(new MenuListener(), this);

		Manager.scheduleRepeatingTask(Manager.getTask(), 20L, 1L);

		if (RolecraftConfig.getBackupTime() > 0)
			backupTask = Manager.scheduleRepeatingTask(DataFolder.backupTask(),
					20L * 60L * RolecraftConfig.getBackupTime(), 20L * 60L * RolecraftConfig.getBackupTime());

		try {
			new Metrics(this);
		} catch (Exception e) {
			e.printStackTrace();
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

	public String getServerVersion() {
		return serverVersion;
	}

	@SuppressWarnings("resource")
	private void createSpellCollection() {
		if (RolecraftConfig.isSpellCollectionEnabled()) {
			try {
				File file = new File(FilePath.spellCollection);
				if (!file.exists()) {
					file.createNewFile();
					fc.copyFile(FilePath.spellCollection, "SpellCollection.jar");
				} else {
					JarFile jar = new JarFile(file);
					Enumeration<JarEntry> entries = jar.entries();
					boolean versionNotFound = true;
					while (entries.hasMoreElements()) {
						JarEntry e = entries.nextElement();
						if (e.getName().equalsIgnoreCase("spell.info")) {
							BufferedReader reader = new BufferedReader(new InputStreamReader(jar.getInputStream(e)));
							String line;
							while ((line = reader.readLine()) != null) {
								if (line.startsWith("version:")) {
									versionNotFound = false;
									if (line.split(":")[1].equals(this.getDescription().getVersion())) {
										file.delete();
										fc.copyFile(FilePath.spellCollection, "SpellCollection.jar");
									}
									break;
								}
							}
							reader.close();
							break;
						}
					}
					if (versionNotFound) {
						file.delete();
						fc.copyFile(FilePath.spellCollection, "SpellCollection.jar");
					}
				}

			} catch (IOException e) {
				getLogger().warning("Failed to create SpellCollection.jar!");
			}
		}

	}

	// why not disable / enable ?
	public void reload() {
		Debugger.info(LogTitle.NONE, "Reloading Rolecraft version:", this.getDescription().getVersion());
		RolecraftConfig.config = YamlConfiguration.loadConfiguration(new File(FilePath.config));
		Msg.file = YamlConfiguration.loadConfiguration(new File(FilePath.messages));
		ClazzLoader.classes = YamlConfiguration.loadConfiguration(new File(FilePath.classes));
		MobSpawner.loadMobs();
		Manager.cancelTask(backupTask);
		if (RolecraftConfig.getBackupTime() > 0)
			backupTask = Manager.scheduleRepeatingTask(DataFolder.backupTask(),
					20L * 60L * RolecraftConfig.getBackupTime(), 20L * 60L * RolecraftConfig.getBackupTime());
	}

	private void searchForNewVersion() {
		Manager.scheduleTask(new Runnable() {

			@Override
			public void run() {
				if (RolecraftConfig.isUpdateEnabled()) {
					Updater upd = new Updater(Manager.plugin, 45030, Manager.plugin.getFile(), UpdateType.NO_DOWNLOAD,
							new UpdateCallback() {

								@Override
								public void onFinish(Updater updater) {
									if (updater.getResult().equals(Updater.UpdateResult.UPDATE_AVAILABLE)) {
										String update = "There is a new version ("
												+ updater.getLatestName().replace("Rolecraft", "").trim()
												+ ") of Rolecraft available!";
										getLogger().info(update);
										getLogger().info(
												"Download the latest version here:\n>>>>>>>>>>>>>>>>>>>>>>>>>>>>>https://dev.bukkit.org/projects/rolecraft");
									}

								}
							});
					Manager.plugin.needsUpdate = upd.getResult().equals(Updater.UpdateResult.UPDATE_AVAILABLE)
							? ("There is a new version (" + upd.getLatestName().replace("Rolecraft", "").trim()
									+ ") of Rolecraft available!")
							: null;
				}

			}
		});
	}

	public void updateMessage(Player p) {
		if (needsUpdate != null && p != null && p.isOp()) {
			p.sendMessage(needsUpdate);
		}
	}
}
