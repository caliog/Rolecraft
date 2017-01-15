package org.caliog.myRPG.Listeners;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.EntityEffect;
import org.bukkit.FireworkEffect;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Creature;
import org.bukkit.entity.Damageable;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Ghast;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.Slime;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkMeta;
import org.caliog.myRPG.Manager;
import org.caliog.myRPG.myConfig;
import org.caliog.myRPG.Entities.Fighter;
import org.caliog.myRPG.Entities.PlayerManager;
import org.caliog.myRPG.Entities.Playerface;
import org.caliog.myRPG.Entities.VolatileEntities;
import org.caliog.myRPG.Entities.myClass;
import org.caliog.myRPG.Items.CustomItem;
import org.caliog.myRPG.Items.ItemUtils;
import org.caliog.myRPG.Items.Weapon;
import org.caliog.myRPG.Messages.Msg;
import org.caliog.myRPG.Mobs.Mob;
import org.caliog.myRPG.Mobs.MobSpawnZone;
import org.caliog.myRPG.Mobs.MobSpawner;
import org.caliog.myRPG.Mobs.Pet;
import org.caliog.myRPG.Utils.EntityUtils;
import org.caliog.myRPG.Utils.Utils;

public class DamageListener implements Listener {

	private HashMap<Integer, Integer> entityTasks = new HashMap<Integer, Integer>();
	private HashMap<UUID, UUID> damaged = new HashMap<UUID, UUID>();

	@EventHandler(priority = EventPriority.HIGH)
	public void onDamage(EntityDamageEvent event) {
		if (myConfig.isWorldDisabled(event.getEntity().getWorld()))
			return;
		if (!(event.getEntity() instanceof Player) && !VolatileEntities.isRegistered(event.getEntity().getUniqueId()))
			return;
		if (event.isCancelled())
			return;
		if (((event.getCause().equals(EntityDamageEvent.DamageCause.FALL)) || (event.getCause().equals(EntityDamageEvent.DamageCause.FIRE))
				|| (event.getCause().equals(EntityDamageEvent.DamageCause.FIRE_TICK))) && ((event.getEntity() instanceof Player))) {
			if (PlayerManager.getPlayer(event.getEntity().getUniqueId()).damage(event.getDamage())) {
				playerDeathEvent(new PlayerDeathEvent((Player) event.getEntity(), new ArrayList<ItemStack>(), 0, ""));
				respawn((Player) event.getEntity());
			}
			event.setDamage(0.0D);
		}
		if ((VolatileEntities.getMob(event.getEntity().getUniqueId()) != null)
				&& ((event.getCause().equals(EntityDamageEvent.DamageCause.FIRE))
						|| (event.getCause().equals(EntityDamageEvent.DamageCause.FIRE_TICK)))) {
			event.setCancelled(true);
			event.getEntity().setFireTicks(0);
		}
		myClass player = PlayerManager.getPlayer(event.getEntity().getUniqueId());
		if ((player != null) && (!(event instanceof EntityDamageByEntityEvent))) {
			if (player.damage(event.getDamage())) {
				Bukkit.getPluginManager().callEvent(new PlayerDeathEvent((Player) event.getEntity(), new ArrayList<ItemStack>(), 0, ""));
				player.getPlayer().setFireTicks(0);
			}
			event.setDamage(0.0D);
		}
	}

	@EventHandler(priority = EventPriority.HIGH)
	public void onDamageByPlayer(EntityDamageByEntityEvent event) {
		if (myConfig.isWorldDisabled(event.getEntity().getWorld()))
			return;
		if (!(event.getEntity() instanceof Player) && !VolatileEntities.isRegistered(event.getEntity().getUniqueId()))
			return;
		if (((event.getDamager() instanceof Player)) && (PlayerManager.getPlayer(event.getDamager().getUniqueId()) != null)) {
			if (!ItemUtils.checkForUse((Player) event.getDamager(), ((Player) event.getDamager()).getInventory().getItemInMainHand())) {
				event.setCancelled(true);
				return;
			} else {
				final Player p = (Player) event.getDamager();
				final short d = p.getInventory().getItemInMainHand().getDurability();
				if (CustomItem.isCustomItem(p.getInventory().getItemInMainHand())) {

					Manager.scheduleTask(new Runnable() {
						public void run() {
							p.getInventory().getItemInMainHand().setDurability(d);
						}
					});
				}
			}
		}
		onEntityDamageByEntity(event);
		onMobDamageByPlayer(event);
		// event.setDamage(0.0D);
	}

	public void onEntityDamageByEntity(final EntityDamageByEntityEvent event) {
		if (!(event.getEntity() instanceof Player) && !VolatileEntities.isRegistered(event.getEntity().getUniqueId()))
			return;
		if (myConfig.isWorldDisabled(event.getEntity().getWorld()))
			return;
		if (event.isCancelled()) {
			return;
		}
		if (!(event.getEntity() instanceof LivingEntity)) {
			return;
		}
		boolean shooter = false;
		if ((event.getDamager() != null) && ((event.getDamager() instanceof Projectile))
				&& (((Projectile) event.getDamager()).getShooter() != null)
				&& ((((Projectile) event.getDamager()).getShooter() instanceof LivingEntity))) {
			shooter = true;
		}
		if ((!(event.getDamager() instanceof LivingEntity)) && (!shooter)) {
			return;
		}
		LivingEntity mdamager;
		if (shooter) {
			mdamager = (LivingEntity) ((Projectile) event.getDamager()).getShooter();
		} else {
			mdamager = (LivingEntity) event.getDamager();
		}

		Fighter damager = PlayerManager.getPlayer(mdamager.getUniqueId());
		if (damager == null) {
			damager = VolatileEntities.getMob(mdamager.getUniqueId());
		}
		double damage = event.getDamage();
		if (damager != null) {
			if ((this.damaged.containsKey(event.getEntity().getUniqueId()))
					&& (((UUID) this.damaged.get(event.getEntity().getUniqueId())).equals(mdamager.getUniqueId()))) {
				event.setCancelled(true);
				return;
			}
			this.damaged.put(event.getEntity().getUniqueId(), mdamager.getUniqueId());

			Manager.scheduleTask(new Runnable() {
				public void run() {
					damaged.remove(event.getEntity().getUniqueId());
				}
			}, 2L);
			damager.fight();
			// b = true, if player bears usual minecraft weapon
			boolean b = (event.getDamager() instanceof Player)
					&& !CustomItem.isCustomItem(((Player) event.getDamager()).getInventory().getItemInMainHand());
			if (!b)
				damage = damager.getDamage();

		}
		Mob mob;
		if ((mob = VolatileEntities.getMob(event.getEntity().getUniqueId())) != null) {
			// mob damaged by entity
			damage -= mob.getDefense();
			mob.fight();
			// mob damaged by mob
			if (damager instanceof Mob) {
				mob.setTarget(event.getEntity(), (LivingEntity) event.getDamager());
				if (mob.damage(damage)) {
					mob.setKiller(event.getDamager().getUniqueId());
					((Damageable) event.getEntity()).setHealth(0.0D);
					((Mob) damager).killedAttack();

				}
				damage = 0;
			}
		} else {
			myClass entity;
			// player damaged by mob
			if ((entity = PlayerManager.getPlayer(event.getEntity().getUniqueId())) != null) {
				damage -= entity.getDefense();
				entity.fight();
				if (entity.getDodge() / 100F > Math.random()) {
					event.setCancelled(true);
				}

				// pets
				Set<Pet> pets = entity.getPets();
				for (Pet p : pets) {
					if (p.fightsBack()) {
						for (Entity en : event.getEntity().getNearbyEntities(12, 5, 12))
							if (en.getUniqueId().equals(p.getId())) {
								p.setTarget(en, mdamager);
								break;
							}

					}
				}

			}
		}
		if (((event.getEntity() instanceof Player)) && (PlayerManager.getPlayer(event.getEntity().getUniqueId()) != null)
				&& (PlayerManager.getPlayer(event.getEntity().getUniqueId()).damage(damage))) {
			PlayerManager.getPlayer(event.getEntity().getUniqueId()).setKiller(mdamager.getUniqueId());
			playerDeathEvent(
					new PlayerDeathEvent((Player) event.getEntity(), new ArrayList<ItemStack>(), 0, ChatColor.GOLD + "You were killed!"));
			respawn((Player) event.getEntity());
			damage = 0.0D;
			event.getEntity().setFireTicks(0);
		}
		event.setDamage(damage);
	}

	public void onMobDamageByPlayer(final EntityDamageByEntityEvent event) {
		if (myConfig.isWorldDisabled(event.getEntity().getWorld()))
			return;
		if (event.isCancelled())
			return;
		if (!(event.getEntity() instanceof Damageable))
			return;
		if (!(event.getEntity() instanceof LivingEntity))
			return;
		if (!(event.getEntity() instanceof Player) && !VolatileEntities.isRegistered(event.getEntity().getUniqueId()))
			return;

		boolean shooterisplayer = false;
		if ((event.getDamager() != null) && ((event.getDamager() instanceof Projectile))
				&& (((Projectile) event.getDamager()).getShooter() != null)
				&& (((Projectile) event.getDamager()).getShooter() instanceof Player)) {
			shooterisplayer = true;
		}
		if ((!(event.getDamager() instanceof Player)) && (!shooterisplayer)) {
			return;
		}
		Player player = null;
		if (shooterisplayer) {
			player = (Player) ((Projectile) event.getDamager()).getShooter();
		} else {
			player = (Player) event.getDamager();
		}
		if (this.entityTasks.containsKey(Integer.valueOf(event.getEntity().getEntityId()))) {
			Manager.cancelTask((Integer) this.entityTasks.get(Integer.valueOf(event.getEntity().getEntityId())));
		}
		final LivingEntity e = (LivingEntity) event.getEntity();
		final Mob mob = VolatileEntities.getMob(e.getUniqueId());
		if (mob == null) {
			return;
		}

		// pets
		Set<Pet> pets = PlayerManager.getPlayer(player.getUniqueId()).getPets();
		for (Pet p : pets) {
			if (p.fightsBack()) {
				for (Entity en : event.getEntity().getNearbyEntities(12, 5, 12))
					if (en.getUniqueId().equals(p.getId())) {
						p.setTarget(en, e);
						break;
					}

			}
		}

		// pets end
		double damage = event.getDamage();
		if (damage < 0.0D) {
			damage = 0.0D;
		}
		e.setCustomName(EntityUtils.getBar(mob.getHealth() - damage, mob.getHP()));
		this.entityTasks.put(Integer.valueOf(event.getEntity().getEntityId()), Integer.valueOf(Manager.scheduleTask(new Runnable() {
			public void run() {
				e.setCustomName(mob.getCustomName());
			}
		}, 100L)));
		event.getEntity().playEffect(EntityEffect.HURT);
		if (mob.damage(damage)) {
			mob.setKiller(player.getUniqueId());
			e.setHealth(0.0D);
		}
		event.setDamage(0.0D);
	}

	@EventHandler(priority = EventPriority.NORMAL)
	public void onMobDeath(final EntityDeathEvent event) {
		if (myConfig.isWorldDisabled(event.getEntity().getWorld()))
			return;
		if (!(event.getEntity() instanceof Player) && !VolatileEntities.isRegistered(event.getEntity().getUniqueId()))
			return;
		event.setDroppedExp(0);
		event.getDrops().clear();
		if ((!(event.getEntity() instanceof Creature)) && (!(event.getEntity() instanceof Slime))
				&& (!(event.getEntity() instanceof Ghast))) {
			return;
		}
		final Mob mob = VolatileEntities.getMob(event.getEntity().getUniqueId());
		if (mob == null) {
			return;
		}
		mob.die();
		Manager.scheduleTask(new Runnable() {
			public void run() {
				VolatileEntities.remove(event.getEntity().getUniqueId());
				for (MobSpawnZone z : MobSpawner.zones) {
					if (z.getM().equals(mob.getSpawnZone())) {
						z.askForSpawn(mob.getExtraTime());
						break;
					}
				}
			}
		}, 25L);

		// Player related
		final myClass player = PlayerManager.getPlayer(mob.getKillerId());
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
				Msg.sendMessage(player.getPlayer(), "level-weapon", a, b);
			} else {
				w.kill(player.getPlayer());
			}
		}
	}

	@EventHandler(priority = EventPriority.NORMAL)
	public void playerDeathEvent(PlayerDeathEvent event) {
		if (myConfig.isWorldDisabled(event.getEntity().getWorld()))
			return;
		if (myConfig.isFireworkEnabled()) {
			Firework firework = (Firework) event.getEntity().getWorld().spawn(event.getEntity().getLocation(), Firework.class);
			FireworkMeta data = firework.getFireworkMeta();
			data.addEffects(new FireworkEffect[] { FireworkEffect.builder().flicker(false).withColor(Color.RED).withFade(Color.FUCHSIA)
					.with(FireworkEffect.Type.CREEPER).build() });
			data.setPower(new Random().nextInt(2) + 1);
			firework.setFireworkMeta(data);
		}

		float newExp = event.getEntity().getExp() - myConfig.getExpLoseRate() * event.getEntity().getExp();
		if (newExp < 0) {
			newExp = 0F;
		}
		event.getEntity().setExp(newExp);

		if (Utils.isBukkitMethod("org.bukkit.event.entity.PlayerDeathEvent", "setKeepInventory", Boolean.class))
			event.setKeepInventory(myConfig.keepInventory());
		else if (myConfig.keepInventory())
			event.getDrops().clear();
		Msg.sendMessage(event.getEntity(), "dead-message");
	}

	@EventHandler(priority = EventPriority.NORMAL)
	public void entityTargetPlayer(EntityTargetEvent event) {
		Mob mob = VolatileEntities.getMob(event.getEntity().getUniqueId());
		if ((mob == null) || (event.getTarget() == null)) {
			return;
		}
		myClass player = PlayerManager.getPlayer(event.getTarget().getUniqueId());
		if (player == null)
			return;
		if ((player != null) && (player.isInvisible()) && ((!player.isFighting()) || (!mob.isFighting()))) {
			event.setCancelled(true);
		}
		if ((!mob.isAgressive()) && (!mob.isFighting())) {
			event.setCancelled(true);
		}

		if (mob instanceof Pet && player.getPets().contains(mob)) {
			event.setCancelled(true);
		}
	}

	@EventHandler(priority = EventPriority.NORMAL)
	public void mobTargetMob(EntityTargetEvent event) {
		Mob mob = VolatileEntities.getMob(event.getEntity().getUniqueId());
		if ((mob == null) || (event.getTarget() == null)) {
			return;
		}
		Mob target = VolatileEntities.getMob(event.getTarget().getUniqueId());
		if (target == null)
			return;
		if (!(mob instanceof Pet) && !(target instanceof Pet))
			event.setCancelled(true);
		UUID attack = mob.getAttack();
		if (attack == null || !attack.equals(target.getId()))
			event.setCancelled(true);
	}

	public void respawn(Player player) {
		Location l = player.getBedSpawnLocation();
		if (l == null)
			l = player.getWorld().getSpawnLocation();
		PlayerManager.getPlayer(player.getUniqueId()).resetHealth();
		player.teleport(l);
	}
}
