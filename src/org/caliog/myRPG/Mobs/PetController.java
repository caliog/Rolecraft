package org.caliog.myRPG.Mobs;

import org.bukkit.entity.Entity;
import org.caliog.myRPG.Manager;
import org.caliog.myRPG.Entities.PlayerManager;
import org.caliog.myRPG.Entities.myClass;
import org.caliog.myRPG.Utils.EntityUtils;

public class PetController {

	public static void controll() {

		for (final myClass player : PlayerManager.getPlayers()) {
			if (player.getPets().isEmpty())
				continue;
			for (final Pet pet : player.getPets()) {
				Entity entity = EntityUtils.getEntity(pet.getId(), player.getPlayer().getWorld());
				if (entity == null)
					continue;
				if (entity.getLocation().distanceSquared(player.getPlayer().getLocation()) > 512) {
					Manager.scheduleTask(new Runnable() {

						@Override
						public void run() {
							pet.die(player);
						}
					});
				}
			}
		}

	}
}
