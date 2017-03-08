package org.caliog.Rolecraft.XMechanics.Listeners;

import java.util.HashMap;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.EntityEffect;
import org.bukkit.entity.Damageable;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityTargetEvent;
import org.caliog.Rolecraft.Manager;
import org.caliog.Rolecraft.Entities.EntityManager;
import org.caliog.Rolecraft.Entities.EntityUtils;
import org.caliog.Rolecraft.Entities.Fighter;
import org.caliog.Rolecraft.Entities.Player.PlayerManager;
import org.caliog.Rolecraft.Entities.Player.RolecraftPlayer;
import org.caliog.Rolecraft.Items.CustomItem;
import org.caliog.Rolecraft.Items.ItemUtils;
import org.caliog.Rolecraft.Mobs.Mob;
import org.caliog.Rolecraft.Mobs.Pet;
import org.caliog.Rolecraft.XMechanics.RolecraftConfig;

public class DamageListener implements Listener {

	private HashMap<Integer, Integer> entityTasks = new HashMap<Integer, Integer>();

	@EventHandler(priority = EventPriority.NORMAL)
	public void entityTargetPlayer(EntityTargetEvent event) {
		Mob mob = EntityManager.getMob(event.getEntity().getUniqueId());
		if ((mob == null) || (event.getTarget() == null)) {
			return;
		}
		RolecraftPlayer player = PlayerManager.getPlayer(event.getTarget().getUniqueId());
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

	@EventHandler(priority = EventPriority.HIGHEST)
	public void mobTargetMob(EntityTargetEvent event) {
		Mob mob = EntityManager.getMob(event.getEntity().getUniqueId());
		if ((mob == null) || (event.getTarget() == null)) {
			return;
		}
		Mob target = EntityManager.getMob(event.getTarget().getUniqueId());
		if (target == null)
			return;
		if (!(mob instanceof Pet) && !(target instanceof Pet))
			event.setCancelled(true);
		if (!mob.getAttack().contains(target.getUniqueId()))
			event.setCancelled(true);

	}

	@EventHandler(priority = EventPriority.HIGH)
	public void onDamage(EntityDamageEvent event) {
		if (RolecraftConfig.isWorldDisabled(event.getEntity().getWorld()))
			return;
		if (!(event.getEntity() instanceof Player) && !EntityManager.isRegistered(event.getEntity().getUniqueId()))
			return;
		if (event.isCancelled())
			return;
		RolecraftPlayer player = PlayerManager.getPlayer(event.getEntity().getUniqueId());
		Mob mob = EntityManager.getMob(event.getEntity().getUniqueId());
		// prevent custom mobs from fire damage
		if ((mob != null) && ((event.getCause().equals(EntityDamageEvent.DamageCause.FIRE))
				|| (event.getCause().equals(EntityDamageEvent.DamageCause.FIRE_TICK)))) {
			event.setCancelled(true);
			event.getEntity().setFireTicks(0);
		}

		if (event instanceof EntityDamageByEntityEvent) {
			Fighter attacker = getAttacker((EntityDamageByEntityEvent) event);
			if (attacker == null)
				return;

			if (attacker instanceof RolecraftPlayer) {
				if (!validWeapons((RolecraftPlayer) attacker)) {
					event.setCancelled(true);
					return;
				}
				if (mob != null && player == null)
					onMobDamagedByPlayer((EntityDamageByEntityEvent) event, (RolecraftPlayer) attacker, mob);
			} else if (mob != null && player == null && attacker instanceof Mob)
				onMobDamagedByMob((EntityDamageByEntityEvent) event, mob, (Mob) attacker);
			// general case attacker could be player or mob
			if (player != null)
				onPlayerDamagedByEntity((EntityDamageByEntityEvent) event, player, attacker);
		}

	}

	private void onMobDamagedByMob(EntityDamageByEntityEvent event, Mob target, Mob attacker) {
		target.setTarget((LivingEntity) event.getDamager());
		target.fight();
		if (target.damage(event.getDamage() - target.getDefense())) {
			target.setKiller(event.getDamager().getUniqueId());
			((Damageable) event.getEntity()).setHealth(0.0D);
			attacker.killedAttack(target.getUniqueId());
		}
		event.setDamage(0D);
	}

	private void onPlayerDamagedByEntity(EntityDamageByEntityEvent event, RolecraftPlayer target, Fighter attacker) {
		double damage = event.getDamage();
		damage -= target.getDefense();
		if (damage < 0)
			damage = 0;
		target.fight();
		if (target.getDodge() / 100F > Math.random()) {
			damage = 0;
			event.setCancelled(true);
		}
		LivingEntity damagerEntity = (LivingEntity) Bukkit.getEntity(attacker.getUniqueId());

		// pets
		Set<Pet> pets = target.getPets();
		for (Pet p : pets)
			if (p.fightsBack())
				p.setTarget(damagerEntity);

		// pets end

		// dealing damage and in case player dies...
		double health = target.getPlayer().getHealth() - damage;
		health = health < 0 ? 0 : health;
		final double h = health;
		Manager.scheduleTask(new Runnable() {

			@Override
			public void run() {
				target.getPlayer().setHealth(h);
			}
		});
		if (target.damage(damage)) {
			target.setKiller(damagerEntity.getUniqueId());
			event.getEntity().setFireTicks(0);
		}
		event.setDamage(0D);
	}

	private void onMobDamagedByPlayer(EntityDamageByEntityEvent event, RolecraftPlayer attacker, Mob mob) {
		double damage = event.getDamage();
		Player player = attacker.getPlayer();
		attacker.fight();
		// b = true, if player bears usual minecraft weapon
		boolean b = !CustomItem.isCustomItem(player.getInventory().getItemInMainHand());
		// allow usual minecraft damage (in case b=true)
		if (!b)
			damage = attacker.getDamage();

		damage -= mob.getDefense();
		mob.fight();

		final LivingEntity targetEntity = (LivingEntity) event.getEntity();

		// pets
		// if player attacks mob, his pet shall attack the mob too
		Set<Pet> pets = attacker.getPets();
		for (Pet p : pets)
			if (p.fightsBack())
				p.setTarget(targetEntity);

		// pets end
		if (damage < 0)
			damage = 0D;

		targetEntity.setCustomName(EntityUtils.getBar(mob.getHealth() - damage, mob.getHP()));

		// cancel old task
		if (this.entityTasks.containsKey(Integer.valueOf(event.getEntity().getEntityId())))
			Manager.cancelTask((Integer) this.entityTasks.get(Integer.valueOf(event.getEntity().getEntityId())));
		// create new task, to reset mob name
		this.entityTasks.put(Integer.valueOf(event.getEntity().getEntityId()), Integer.valueOf(Manager.scheduleTask(new Runnable() {
			public void run() {
				targetEntity.setCustomName(mob.getCustomName());
			}
		}, 100L)));

		event.getEntity().playEffect(EntityEffect.HURT);
		// dealing damage
		if (mob.damage(damage)) {
			mob.setKiller(player.getUniqueId());
			targetEntity.setHealth(0.0D);
		}
		event.setDamage(0.0D);
	}

	private Fighter getAttacker(EntityDamageByEntityEvent event) {
		boolean shooter = false;
		Entity damager = event.getDamager();
		if (damager != null && (damager instanceof Projectile) && (((Projectile) damager).getShooter() != null)
				&& ((((Projectile) damager).getShooter() instanceof LivingEntity))) {
			shooter = true;
		}
		if ((!(damager instanceof LivingEntity)) && (!shooter)) {
			return null;
		}
		LivingEntity damagerEntity;
		if (shooter) {
			damagerEntity = (LivingEntity) ((Projectile) event.getDamager()).getShooter();
		} else {
			damagerEntity = (LivingEntity) event.getDamager();
		}

		Fighter d = PlayerManager.getPlayer(damagerEntity.getUniqueId());
		if (d == null) {
			d = EntityManager.getMob(damagerEntity.getUniqueId());
		}
		return d;
	}

	private boolean validWeapons(RolecraftPlayer attacker) {
		return ItemUtils.checkForUse(attacker.getPlayer(), attacker.getPlayer().getInventory().getItemInMainHand());

		// dont destroy weapons !?

		/*
		 * final Player p = attacker.getPlayer(); final short d =
		 * p.getInventory().getItemInMainHand().getDurability(); if
		 * (CustomItem.isCustomItem(p.getInventory().getItemInMainHand())) {
		 * 
		 * Manager.scheduleTask(new Runnable() { public void run() {
		 * p.getInventory().getItemInMainHand().setDurability(d); } }); }
		 * 
		 * }
		 */

	}
}
