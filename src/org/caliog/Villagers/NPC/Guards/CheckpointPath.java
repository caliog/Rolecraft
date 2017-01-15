package org.caliog.Villagers.NPC.Guards;

import java.io.File;
import java.io.IOException;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.caliog.myRPG.Manager;
import org.caliog.myRPG.Utils.FilePath;

public class CheckpointPath {

	private World world;
	private String name;
	private int initDelay;
	private int cpDelay;
	private int currentCP = 0;
	public static final int maxCheckpoints = 100;
	private Location[] checkpoints = new Location[maxCheckpoints];
	private boolean isLoaded = true;
	private final long sleep = 5;
	private int sleepRound = 0;
	private boolean stopSleeping = false;
	private int loadedCP = 0;
	private int taskID;
	private boolean run = true;
	final String filePath = FilePath.villagerDataPathsFile;

	public CheckpointPath(String name) {
		this.name = name;
		if (name.length() < 1)
			this.setLoaded(false);
		else
			this.setLoaded(readFile());
	}

	public CheckpointPath(String name, int initDelay, int cpDelay, Location loc) {
		this.name = name;
		this.initDelay = initDelay;
		this.cpDelay = cpDelay;
		this.world = loc.getWorld();
		writeFile();
	}

	private void writeFile() {

		if (getWorld() != null) {
			new YamlConfiguration();
			FileConfiguration config = YamlConfiguration.loadConfiguration(new File(filePath));

			ConfigurationSection section = config.createSection(name);

			section.set("world-name", getWorld().getName());
			section.set("initial-delay", initDelay);
			section.set("checkpoint-delay", cpDelay);
			if (checkpoints != null) {
				for (int i = 1; i < maxCheckpoints; i++) {
					if (isLoaded && checkpoints[i] != null) {
						section.set("checkpoint" + i + ".x", checkpoints[i].getX());
						section.set("checkpoint" + i + ".y", checkpoints[i].getY());
						section.set("checkpoint" + i + ".z", checkpoints[i].getZ());
					}
				}
			}

			try {
				config.save(new File(filePath));
			} catch (IOException e) {

				e.printStackTrace();
			}
		}

	}

	public boolean readFile() {
		FileConfiguration config = YamlConfiguration.loadConfiguration(new File(filePath));
		if (config.isConfigurationSection(name)) {
			ConfigurationSection section = config.getConfigurationSection(name);
			this.world = Manager.plugin.getServer().getWorld(section.getString("world-name"));
			if (world == null)
				return false;
			this.initDelay = section.getInt("initial-delay");
			this.cpDelay = section.getInt("checkpoint-delay");
			int i = 0;
			for (i = 1; i < maxCheckpoints; i++) {
				if (section.isConfigurationSection("checkpoint" + i)) {
					ConfigurationSection cp = section.getConfigurationSection("checkpoint" + i);
					Location loc = new Location(world, cp.getDouble("x"), cp.getDouble("y"), cp.getDouble("z"));

					this.checkpoints[i] = loc;

				}
			}
			loadedCP = i;
			return true;
		} else
			return false;

	}

	public boolean walkPath(final CPMoveable abstractNPC) {
		readFile();
		setRun(true);
		currentCP = 0;
		sleepRound = 0;
		stopSleeping = false;
		checkpoints[0] = abstractNPC.getLocation();
		if (checkpoints[1] == null)
			return false;
		if (checkpoints == null)
			return false;

		this.setTaskID(Bukkit.getScheduler().scheduleSyncRepeatingTask(Manager.plugin, new Runnable() {

			@Override
			public void run() {
				if (isRun())
					if (checkpoints.length > currentCP && checkpoints[currentCP] != null) {
						Location l1 = abstractNPC.getEntityLocation();
						Block b1 = l1.getBlock();
						Location l2 = checkpoints[currentCP];
						Block b2 = l2.getBlock();
						if (l1 != null && l2 != null && (b1.getLocation().distanceSquared(b2.getLocation()) <= 0.0001)) {
							float delay;
							if (currentCP == 0)
								delay = initDelay;
							else
								delay = cpDelay;

							if (!stopSleeping) {
								sleepRound++;
								abstractNPC.setRunning(false);
								if (sleepRound * ((sleep / 20F)) >= delay)
									stopSleeping = true;

							} else {
								readFile();
								stopSleeping = false;
								sleepRound = 0;
								currentCP++;

								if (currentCP > maxCheckpoints - 1 || checkpoints[currentCP] == null || currentCP > loadedCP)
									currentCP = 0;

								abstractNPC.walkTo(checkpoints[currentCP], 13000);
								abstractNPC.setRunning(true);
							}

						} else {
							// waiting

						}
					} else {
						currentCP = 0;
					}
				else if (!run) {
					abstractNPC.moveTo(checkpoints[0]);
					Bukkit.getScheduler().cancelTask(taskID);
					abstractNPC.setRunning(false);
				}
			}

		}, 0L, sleep));

		return true;
	}

	public World getWorld() {
		return world;
	}

	public void setWorld(World world) {
		this.world = world;
		writeFile();
	}

	public Location[] getCheckpoints() {
		return checkpoints;
	}

	public void replaceCheckpoint(int id, Location loc) {
		if (id <= 0 || id > maxCheckpoints - 1 || !loc.getWorld().equals(getWorld()))
			return;

		for (int i = 1; i < id; i++)
			if (checkpoints[i] == null)
				checkpoints[i] = loc;
		checkpoints[id] = loc;

		writeFile();
	}

	public void replaceCheckpoint(Location loc, Location newLoc) {
		if (checkpoints != null)
			for (int i = 0; i < checkpoints.length; i++)
				if (checkpoints[i].equals(loc)) {
					replaceCheckpoint(i, newLoc);
					return;
				}
	}

	public int getInitDelay() {
		return initDelay;
	}

	public void setInitDelay(int initDelay) {
		this.initDelay = initDelay;
		writeFile();
	}

	public int getCpDelay() {
		return cpDelay;
	}

	public void setCpDelay(int cpDelay) {
		this.cpDelay = cpDelay;
		writeFile();
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
		writeFile();
	}

	public boolean isLoaded() {
		return isLoaded;
	}

	public void setLoaded(boolean isLoaded) {
		this.isLoaded = isLoaded;
	}

	public void setCheckpoint(Location l, int id) {
		replaceCheckpoint(id, l);

	}

	public void removePath() {
		FileConfiguration config = YamlConfiguration.loadConfiguration(new File(filePath));

		config.set(name, null);
		try {
			config.save(new File(filePath));
		} catch (IOException e) {

			e.printStackTrace();
		}

	}

	public int getTaskID() {
		return taskID;
	}

	public void setTaskID(int taskID) {
		this.taskID = taskID;
	}

	public boolean isRun() {
		return run;
	}

	public void setRun(boolean run) {
		this.run = run;
	}
}
