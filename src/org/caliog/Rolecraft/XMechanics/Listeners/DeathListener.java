package org.caliog.Rolecraft.XMechanics.Listeners;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.Sound;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkMeta;
import org.caliog.Rolecraft.Manager;
import org.caliog.Rolecraft.Entities.EntityManager;
import org.caliog.Rolecraft.Entities.Player.PlayerManager;
import org.caliog.Rolecraft.Entities.Player.Playerface;
import org.caliog.Rolecraft.Entities.Player.RolecraftPlayer;
import org.caliog.Rolecraft.Items.Weapon;
import org.caliog.Rolecraft.Mobs.Mob;
import org.caliog.Rolecraft.Mobs.MobSpawnZone;
import org.caliog.Rolecraft.Mobs.MobSpawner;
import org.caliog.Rolecraft.XMechanics.RolecraftConfig;
import org.caliog.Rolecraft.XMechanics.Messages.Key;
import org.caliog.Rolecraft.XMechanics.Messages.Msg;
import org.caliog.Rolecraft.XMechanics.Utils.Utils;

public class DeathListener implements Listener {

	@EventHandler(priority = EventPriority.NORMAL)
	public void onMobDeath(final EntityDeathEvent event) {
		if (RolecraftConfig.isWorldDisabled(event.getEntity().getWorld()))
			return;
		if (!(event.getEntity() instanceof Player) && !EntityManager.isRegistered(event.getEntity().getUniqueId()))
			return;
		event.setDroppedExp(0);
		event.getDrops().clear();
		final Mob mob = EntityManager.getMob(event.getEntity().getUniqueId());
		if (mob == null) {
			return;
		}
		mob.die();
		Manager.scheduleTask(new Runnable() {
			public void run() {
				EntityManager.remove(event.getEntity().getUniqueId());
				for (MobSpawnZone z : MobSpawner.zones) {
					if (z.getM().equals(mob.getSpawnZone())) {
						z.askForSpawn(mob.getExtraTime());
						break;
					}
				}
			}
		}, 25L);

		// Player related
		final RolecraftPlayer player = PlayerManager.getPlayer(mob.getKillerId());
		if (player == null) {
			return;
		}
		mob.setKiller(null);

		// Player related
		double diff = player.getLevel() - mob.getLevel();
		if (diff < 3.0D) {
			diff = 1.0D;
		} else if (diff < 6.0D) {
			diff = 1.5D;
		} else if (diff < 10.0D) {
			diff = 2.0D;
		} else if (diff < 25.0D) {
			diff = 4.0D;
		} else if (diff < 50.0D) {
			diff = 10.0D;
		} else if (diff < 100.0D) {
			diff = 100.0D;
		}
		event.getEntity().setCustomName(ChatColor.BLACK + "[  " + ChatColor.YELLOW + "+ " + Playerface.killed(player.getPlayer(), mob)
				+ " XP  " + ChatColor.BLACK + "]");
		List<ItemStack> stacks = new ArrayList<ItemStack>();
		for (ItemStack stack : mob.drops().keySet()) {
			if (Math.random() * diff < ((Float) mob.drops().get(stack)).floatValue()) {
				if (stack != null)
					stacks.add(stack);
			}
		}
		Playerface.dropItem(player.getPlayer(), event.getEntity().getLocation(), stacks);
		player.getPlayer().playSound(event.getEntity().getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 0.6F, 2.0F);
		if ((player.getLevel() - mob.getLevel() < 4) && (Weapon.isWeapon(player, player.getPlayer().getInventory().getItemInMainHand()))) {
			final ItemStack hand = player.getPlayer().getInventory().getItemInMainHand();
			Weapon w = Weapon.getInstance(player, hand);
			int level = w.getLevel();
			int mLevel = w.getMinLevel();
			int max = (level + 2) * (mLevel + 2);
			int current = w.getKills();
			current++;
			if ((current == max) && (w.getLevel() != 9)) {
				w.raiseLevel(player.getPlayer());
				String[] a = { Msg.WEAPON, Msg.LEVEL }, b = { w.getName(), String.valueOf(w.getLevel()) };
				Msg.sendMessage(player.getPlayer(), Key.WEAPON_LEVEL, a, b);
			} else {
				w.kill(player.getPlayer());
			}
		}
	}

	@EventHandler(priority = EventPriority.HIGH)
	public void playerDeathEvent(PlayerDeathEvent event) {
		if (RolecraftConfig.isWorldDisabled(event.getEntity().getWorld()))
			return;
		Manager.scheduleTask(new Runnable() {

			@Override
			public void run() {
				if (RolecraftConfig.isFireworkEnabled()) {
					Firework firework = (Firework) event.getEntity().getWorld().spawn(event.getEntity().getLocation(), Firework.class);
					FireworkMeta data = firework.getFireworkMeta();
					data.addEffects(new FireworkEffect[] { FireworkEffect.builder().flicker(false).withColor(Color.RED)
							.withFade(Color.FUCHSIA).with(FireworkEffect.Type.CREEPER).build() });
					data.setPower(new Random().nextInt(2) + 1);
					firework.setFireworkMeta(data);
				}

				float newExp = event.getEntity().getExp() - RolecraftConfig.getExpLoseRate() * event.getEntity().getExp();
				if (newExp < 0) {
					newExp = 0F;
				}
				event.getEntity().setExp(newExp);
			}
		});
		if (Utils.isBukkitMethod("org.bukkit.event.entity.PlayerDeathEvent", "setKeepInventory", boolean.class))
			event.setKeepInventory(RolecraftConfig.keepInventory());
		else if (RolecraftConfig.keepInventory())
			event.getDrops().clear();
	}

}
