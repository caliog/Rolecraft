package org.caliog.Rolecraft.Mobs.Pets;

import org.bukkit.entity.Entity;
import org.caliog.Rolecraft.Manager;
import org.caliog.Rolecraft.Entities.EntityUtils;
import org.caliog.Rolecraft.Entities.Player.PlayerManager;
import org.caliog.Rolecraft.Entities.Player.RolecraftPlayer;

public class PetController {

	public static void controll() {

		for (final RolecraftPlayer player : PlayerManager.getPlayers()) {
			if (player.getPets().isEmpty())
				continue;
			for (final Pet pet : player.getPets()) {
				Entity entity = EntityUtils.getEntity(pet.getUniqueId(), player.getPlayer().getWorld());
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
