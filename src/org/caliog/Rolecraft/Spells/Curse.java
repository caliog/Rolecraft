package org.caliog.Rolecraft.Spells;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.util.Vector;
import org.caliog.Rolecraft.Manager;
import org.caliog.Rolecraft.Entities.EntityManager;
import org.caliog.Rolecraft.Entities.Player.RolecraftPlayer;
import org.caliog.Rolecraft.XMechanics.Debug.Debugger;
import org.caliog.Rolecraft.XMechanics.Debug.Debugger.LogTitle;
import org.caliog.Rolecraft.XMechanics.Resource.FilePath;
import org.caliog.Rolecraft.XMechanics.Utils.ParticleEffect;

public class Curse extends Spell {

	// introduce static variables which will be loaded from file
	public enum CurseType {
		BLACK(ParticleEffect.SPELL_MOB, 0F), COLORFUL(ParticleEffect.SPELL_MOB, 0.5F), GREEN(ParticleEffect.VILLAGER_HAPPY, 100F);

		public final ParticleEffect effect;
		public final float color;

		CurseType(ParticleEffect e, float c) {
			effect = e;
			color = c;
		}
	}

	// TODO edit names
	public enum Direction {
		RAY, CONE, FULL, AUTO_TARGET;
	}

	private CurseType type;
	private Direction dir;

	public Curse(RolecraftPlayer player, String name) {
		super(player, name);
		String[] a = load();
		if (a == null) {
			String b[] = { CurseType.BLACK.name(), Direction.RAY.name() };
			a = b;
			Debugger.warning(LogTitle.SPELL, "Returned null while trying to load curse: ", name);
		}
		try {
			type = CurseType.valueOf(a[0]);
			dir = Direction.valueOf(a[1]);
		} catch (IllegalArgumentException e) {
			type = CurseType.BLACK;
			dir = Direction.RAY;
			e.printStackTrace();
		}
	}

	private String[] load() {
		File f = new File(FilePath.spells + getName() + ".yml");
		if (!f.exists())
			return null;
		YamlConfiguration config = YamlConfiguration.loadConfiguration(f);
		ConfigurationSection section = config.getConfigurationSection("curse");
		String c = section.getString("color");
		String d = section.getString("direction");
		String[] a = { c, d };
		return a;
	}

	@Override
	public boolean execute() {
		if (!super.execute())
			return false;
		Manager.scheduleTask(new Runnable() {

			@Override
			public void run() {
				effects();
			}
		});

		return true;
	}

	@SuppressWarnings("deprecation")
	private void effects() {
		final int distance = 20;
		List<Vector> vectorList = calculateVectors();
		final Location center = getPlayer().getPlayer().getEyeLocation();
		center.add(0, -0.3, 0);
		final int a = (int) (20 * ((float) getPower()) / getMaxPower());

		// entities
		Collection<Entity> entities = center.getWorld().getNearbyEntities(center, distance, distance / 4, distance);
		entities.remove(getPlayer().getPlayer());
		Collection<Entity> temp = new ArrayList<Entity>();
		// collect entities which a close to fireline
		for (final Vector v : vectorList) {
			for (Entity entity : entities) {
				if (temp.contains(entity))
					continue;
				if (entity.getType().equals(EntityType.PLAYER) || EntityManager.isRegistered(entity.getUniqueId())) {
					Vector b = entity.getLocation().toVector().subtract(getPlayer().getPlayer().getLocation().toVector());
					if (v.crossProduct(b).lengthSquared() < 0.28F) {
						temp.add(entity);
					}

				}
			}
			for (int i = 1; i < distance; i++) {
				Location loc = center.clone().add(v.clone().multiply(i));
				type.effect.display(0.1F, 0.1F, 0.1F, type.color, 12 + a, loc, 50D);
				for (Entity t : temp) {
					if (t.getLocation().distanceSquared(loc) < 400) {
						// TODO not tested yet
						EntityDamageByEntityEvent event = new EntityDamageByEntityEvent(this.getPlayer().getPlayer(), t, DamageCause.CUSTOM,
								getDamage());
						Bukkit.getPluginManager().callEvent(event);
					}
				}
			}
		}

	}

	// TODO add more directions
	private List<Vector> calculateVectors() {
		List<Vector> vectorList = new ArrayList<Vector>();

		double pitch = (getPlayer().getPlayer().getLocation().getPitch() + 90.0F) * 3.141592653589793D / 180.0D;
		double yaw = (getPlayer().getPlayer().getLocation().getYaw() + 90.0F) * 3.141592653589793D / 180.0D;
		double x = Math.sin(pitch) * Math.cos(yaw);
		double y = Math.sin(pitch) * Math.sin(yaw);
		double z = Math.cos(pitch);
		Vector look = new Vector(x, z, y);

		if (dir.equals(Direction.CONE) || dir.equals(Direction.RAY))
			vectorList.add(look);

		final double angle = 3.141592653589793D / 8D;

		if (dir.equals(Direction.CONE)) {
			x = Math.sin(pitch) * Math.cos(yaw + angle);
			y = Math.sin(pitch) * Math.sin(yaw + angle);
			z = Math.cos(pitch);
			look = new Vector(x, z, y);
			vectorList.add(look);
			x = Math.sin(pitch) * Math.cos(yaw - angle);
			y = Math.sin(pitch) * Math.sin(yaw - angle);
			z = Math.cos(pitch);
			look = new Vector(x, z, y);
			vectorList.add(look);
			x = Math.sin(pitch + angle) * Math.cos(yaw);
			y = Math.sin(pitch + angle) * Math.sin(yaw);
			z = Math.cos(pitch + angle);
			look = new Vector(x, z, y);
			vectorList.add(look);
			x = Math.sin(pitch - angle) * Math.cos(yaw);
			y = Math.sin(pitch - angle) * Math.sin(yaw);
			z = Math.cos(pitch - angle);
			look = new Vector(x, z, y);
			vectorList.add(look);
		} else if (dir.equals(Direction.FULL)) {
			for (int i = 0; i < 8; i++) {
				x = Math.cos(i * Math.PI / 4D);
				y = Math.sin(i * Math.PI / 4D);
				look = new Vector(x, 0, y);
				vectorList.add(look);
			}
		}
		return vectorList;
	}

}
