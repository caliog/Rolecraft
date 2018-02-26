package org.caliog.Rolecraft.Guards;

import java.util.List;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Player;
import org.caliog.Rolecraft.Manager;
import org.caliog.Rolecraft.Villagers.VManager;
import org.caliog.Rolecraft.XMechanics.PlayerConsole.Stoppable;

public class GuardWatcher {

	public static void run() {
		Manager.scheduleTask(new Runnable() {

			@Override
			public void run() {
				List<Guard> guards = GManager.getGuards();
				if (guards != null)
					for (Guard npc : guards) {
						((Player) npc.getNpc().getBukkitEntity()).setFireTicks(0);
						checkForLava(npc.getEntityLocation().getBlock(), false);
						if (npc != null) {
							findAttacks(npc);
							findPlayer(npc);
						}

					}
			}
		});

	}

	private static void checkForLava(Block block, boolean deep) {
		if (block.getType().equals(Material.LAVA)) {
			block.setType(Material.AIR);
			if (!deep) {
				checkForLava(block.getRelative(BlockFace.NORTH), !deep);
				checkForLava(block.getRelative(BlockFace.EAST), !deep);
				checkForLava(block.getRelative(BlockFace.SOUTH), !deep);
				checkForLava(block.getRelative(BlockFace.WEST), !deep);

				checkForLava(block.getRelative(BlockFace.NORTH_WEST), !deep);
				checkForLava(block.getRelative(BlockFace.NORTH_EAST), !deep);
				checkForLava(block.getRelative(BlockFace.SOUTH_WEST), !deep);
				checkForLava(block.getRelative(BlockFace.SOUTH_EAST), !deep);
			}

		}

	}

	private static void findAttacks(final Guard guard) {
		int r = guard.getRadius();
		if (guard.getNpc() != null) {
			if (!guard.isAttacking() && (guard.isAttackMonster() || guard.isAttackAnimal())) {

				List<Entity> entities = guard.getNpc().getBukkitEntity().getNearbyEntities(r, r, r);
				for (Entity e : entities) {
					if (e instanceof LivingEntity) {
						if (!(e instanceof Player) && ((e instanceof Monster && guard.isAttackMonster())
								|| (!(e instanceof Monster) && guard.isAttackAnimal()))) {
							if (VManager.getVillager(e.getUniqueId()) == null && GManager.getGuard(e.getUniqueId()) == null)
								guard.setAttacking((LivingEntity) e);

						}
					}
				}
			} else if (guard.isAttacking() && guard.getEntityLocation().distanceSquared(guard.getAttacking().getLocation()) <= r * r
					&& !guard.getAttacking().isDead()) {

				if (((guard.getAttacking() instanceof Monster) && guard.isAttackMonster())
						|| ((!(guard.getAttacking() instanceof Monster) && guard.isAttackAnimal()))) {
					CheckpointPath path = guard.getPath();
					if (path != null && path.isRun())
						path.setRun(false);

					guard.attack();
				}

			} else if (guard.isAttacking()) {
				guard.getNpc().walkTo(guard.getLocation());
				guard.setAttacking(null);
				Stoppable s = new Stoppable() {

					@Override
					public void run() {
						if (guard.getEntityLocation().distanceSquared(guard.getLocation()) < 1) {
							stop();
						}
					}

					@Override
					public void stop() {
						super.stop();
						Manager.scheduleTask(new Runnable() {

							@Override
							public void run() {
								guard.setRunning(false);
							}
						});
					}
				};
				s.setTaskID(Manager.scheduleRepeatingTask(s, 20L, 10L));
				if (guard.getPath() != null && !guard.getPath().isRun())
					guard.walkPath();

			}

		}

	}

	public static void findPlayer(Guard guard) {

		final int radius = guard.getRadius();

		List<Entity> entities = guard.getNpc().getBukkitEntity().getNearbyEntities(2 * radius, radius, 2 * radius);

		for (Entity entity : entities) {

			if (entity instanceof Player && GManager.getGuard(entity.getUniqueId()) == null) {
				Player player = (Player) entity;

				// LOOK
				if (player.getLocation().distanceSquared(guard.getEntityLocation()) < radius * radius)
					if (guard.isLooking() && !guard.isAttacking() && !guard.isRunning())

					{
						guard.getNpc().lookAtPoint(player.getEyeLocation());
					}

			}
		}

	}

}
