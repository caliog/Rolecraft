package org.caliog.Rolecraft.Mobs;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.ItemStack;
import org.caliog.Rolecraft.Manager;
import org.caliog.Rolecraft.Entities.EntityManager;
import org.caliog.Rolecraft.Entities.Fighter;
import org.caliog.Rolecraft.XMechanics.Utils.ParticleEffect;
import org.caliog.Rolecraft.XMechanics.Utils.Vector;

public abstract class Mob extends Fighter {
	private final String identifier;
	private final UUID id;
	protected HashMap<ItemStack, Float> drops = new HashMap<ItemStack, Float>();
	protected HashMap<String, ItemStack> eq = new HashMap<String, ItemStack>();
	private final Vector spawnZone;
	private int taskId = -1;
	private boolean dead = false;
	private Set<UUID> attack = new HashSet<UUID>();

	public Mob(String ident, UUID id, Vector m) {
		this.identifier = ident;
		this.id = id;
		this.spawnZone = m;
	}

	@SuppressWarnings("deprecation")
	public static LivingEntity spawnEntity(String ident, final Location loc, Vector m) {
		Entity entity = null;
		Mob mob = null;

		EntityType type = new MobInstance(ident, null, null).getType();
		entity = loc.getWorld().spawnEntity(loc, type);
		mob = new MobInstance(ident, entity.getUniqueId(), m);

		Manager.scheduleRepeatingTask(new Runnable() {
			public void run() {
				ParticleEffect.SMOKE_NORMAL.display(0.1F, 0.3F, 0.1F, 0.25F, 10, loc, 30);

			}
		}, 0L, 2L, 8L);
		if ((entity instanceof LivingEntity)) {
			LivingEntity e = (LivingEntity) entity;
			e.setCustomName(mob.getCustomName());
			e.setCustomNameVisible((mob.getCustomName() != null) && (!mob.getCustomName().isEmpty()));
			e.setCanPickupItems(false);
			e.setMaxHealth(mob.getHP());
			e.setHealth(mob.getHP());
			if ((mob.eq() != null) && (!mob.eq().isEmpty())) {
				e.getEquipment().setItemInMainHand((ItemStack) mob.eq().get("HAND"));
				e.getEquipment().setItemInMainHandDropChance(0.0F);
				e.getEquipment().setHelmet((ItemStack) mob.eq().get("HELMET"));
				e.getEquipment().setHelmetDropChance(0.0F);
				e.getEquipment().setChestplate((ItemStack) mob.eq().get("CHESTPLATE"));
				e.getEquipment().setChestplateDropChance(0.0F);
				e.getEquipment().setLeggings((ItemStack) mob.eq().get("LEGGINGS"));
				e.getEquipment().setLeggingsDropChance(0.0F);
				e.getEquipment().setBoots((ItemStack) mob.eq().get("BOOTS"));
				e.getEquipment().setBootsDropChance(0.0F);
			}
			EntityManager.register(mob);

			return e;
		}
		return null;
	}

	public abstract HashMap<String, ItemStack> eq();

	public abstract double getHP();

	public String getCustomName() {
		return (fightsBack() ? ChatColor.RED : ChatColor.BLUE) + "" + (isAgressive() ? ChatColor.ITALIC : "") + getName() + " Lv "
				+ getLevel();
	}

	public abstract EntityType getType();

	public String getIdentifier() {
		return this.identifier;
	}

	public abstract String getName();

	public abstract int getLevel();

	public boolean fightsBack() {
		return getDamage() > 0;
	}

	public abstract boolean isAgressive();

	public abstract int getExp();

	public abstract HashMap<ItemStack, Float> drops();

	@Override
	public UUID getUniqueId() {
		return this.id;
	}

	public Vector getSpawnZone() {
		return this.spawnZone;
	}

	public abstract int getExtraTime();

	public abstract boolean isPet();

	private void cancel() {
		if (taskId != -1)
			Manager.cancelTask(taskId);
		taskId = -1;
	}

	public void die() {
		dead = true;
	}

	public boolean isDead() {
		return dead;
	}

	public void setTarget(LivingEntity target) {
		// only add living entities
		if (attack.contains(target.getUniqueId()))
			return;
		attack.add(target.getUniqueId());
		if (taskId == -1 || Bukkit.getScheduler().isCurrentlyRunning(taskId))
			startNewAttackThread();
	}

	public void startNewAttackThread() {
		cancel();
		if (dead)
			return;
		final Entity en = Bukkit.getEntity(getUniqueId());
		if (en == null)
			return;
		taskId = Manager.scheduleRepeatingTask(new Runnable() {

			@Override
			public void run() {
				if (!dead) {
					UUID target = getFirstAttack();
					if (target != null) {
						LivingEntity targetEntity = (LivingEntity) Bukkit.getEntity(target);
						if (targetEntity == null || targetEntity.isDead()) {
							attack.remove(target);
						} else {
							NMSMethods.setTarget(en, targetEntity);
						}
					} else
						cancel();// cancel if attack is empty

				} else
					cancel(); // cancel if mob is dead

			}
		}, 0L, 5L);

	}

	public Set<UUID> getAttack() {
		return attack;
	}

	public UUID getFirstAttack() {
		if (attack.isEmpty())
			return null;
		return (UUID) attack.toArray()[0];
	}

	public void killedAttack(UUID a) {
		attack.remove(a);
		cancel();
	}
}
