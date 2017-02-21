package org.caliog.Rolecraft.Mobs;

import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.caliog.Rolecraft.Manager;
import org.caliog.Rolecraft.Entities.EntityManager;
import org.caliog.Rolecraft.XMechanics.RolecraftConfig;
import org.caliog.Rolecraft.XMechanics.Utils.Vector;

public class MobSpawnZone {
	private Vector m;
	private String world;
	private int radius;
	private int maxAmount;
	private String mob;
	private int scheduled = 0;

	public MobSpawnZone(Location loc, int radius, int maxAmount, String c) {
		this.m = new Vector(loc);
		this.world = loc.getWorld().getName();
		this.radius = radius;
		this.maxAmount = maxAmount;
		this.mob = c;
	}

	public MobSpawnZone(Vector m, String mob, int r, int a) {
		this.m = m;
		this.world = m.getWorld();
		this.radius = r;
		this.maxAmount = a;
		this.mob = mob;
	}

	public void askForSpawn() {
		int a = this.maxAmount - (countMobs() + this.scheduled);
		if (a < 1) {
			return;
		}
		scheduleMobSpawn(0);
	}

	public void askForSpawn(int t) {
		int a = this.maxAmount - (countMobs() + this.scheduled);
		if (a < 1) {
			return;
		}
		scheduleMobSpawn(t);
	}

	private void scheduleMobSpawn(int t) {
		Random r = new Random();
		int d = 0;
		if (t > 0)
			d = (int) (r.nextInt(t) - t / 2.);
		long time = 20L * (RolecraftConfig.getDefaultSpawnTime() + (t <= 0 ? 0 : t) + d);
		this.scheduled += 1;
		Manager.scheduleTask(new Runnable() {
			public void run() {
				if (spawnMob()) {
					scheduled -= 1;
				}
			}
		}, time);
	}

	private boolean spawnMob() {
		if (RolecraftConfig.getDisabledWorlds().contains(world))
			return false;
		final int maxHeight = 10;

		int x = this.radius - (int) (Math.random() * this.radius * 2.0D);
		int z = this.radius - (int) (Math.random() * this.radius * 2.0D);
		long d = x * x + z * z;
		if (d >= radius * radius) {
			x *= radius / (2 * d);
			z *= radius / (2 * d);
		}
		Location l1 = new Location(Bukkit.getWorld(this.world), this.m.getX() + x, this.m.getY(), this.m.getZ() + z);
		int p = l1.getBlock().getType().equals(Material.AIR) ? -1 : 1;
		for (int h = 0; h < maxHeight; h++) {
			l1.setY(this.m.getY() + h * p);
			if ((!l1.getBlock().getType().equals(Material.AIR))
					&& (l1.getBlock().getRelative(BlockFace.UP).getType().equals(Material.AIR))) {
				l1.setY(l1.getY() + 2.0D);
				return Mob.spawnEntity(this.mob, l1, this.m) != null;
			}
		}
		return false;
	}

	protected int countMobs() {
		int counter = 0;

		for (Mob m : EntityManager.getMobs()) {
			if (!m.isDead() && !m.getSpawnZone().isNull())
				if ((m != null) && (m.getSpawnZone().equals(this.m))) {
					counter++;
				}
		}

		return counter;
	}

	public boolean isInside(Location location) {
		if (this.m.distanceSquared(new Vector(location)) <= this.radius * this.radius) {
			return true;
		}
		return false;
	}

	public int getAmount() {
		return this.maxAmount;
	}

	public String getMob() {
		return this.mob;
	}

	public int getRadius() {
		return this.radius;
	}

	public Vector getM() {
		return this.m;
	}

	public String getWorld() {
		return this.world;
	}

}
