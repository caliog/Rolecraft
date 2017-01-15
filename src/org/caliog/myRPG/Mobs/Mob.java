package org.caliog.myRPG.Mobs;

import java.util.HashMap;
import java.util.UUID;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Creature;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.ItemStack;
import org.caliog.myRPG.Manager;
import org.caliog.myRPG.Entities.Fighter;
import org.caliog.myRPG.Entities.VolatileEntities;
import org.caliog.myRPG.Utils.ParticleEffect;
import org.caliog.myRPG.Utils.Vector;

public abstract class Mob extends Fighter {
	private final String name;
	private final UUID id;
	protected HashMap<ItemStack, Float> drops = new HashMap<ItemStack, Float>();
	protected HashMap<String, ItemStack> eq = new HashMap<String, ItemStack>();
	private final Vector spawnZone;
	private int taskId = -1;
	private boolean dead = false;
	private UUID attack;

	public Mob(String name, UUID id, Vector m) {
		this.name = name;
		this.id = id;
		this.spawnZone = m;
	}

	@SuppressWarnings("deprecation")
	public static LivingEntity spawnEntity(String name, final Location loc, Vector m) {
		Entity entity = null;
		Mob mob = null;

		EntityType type = new MobInstance(name, null, null).getType();
		entity = loc.getWorld().spawnEntity(loc, type);
		mob = new MobInstance(name, entity.getUniqueId(), m);

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
			VolatileEntities.register(mob);

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

	public String getName() {
		return this.name;
	}

	public abstract int getLevel();

	public boolean fightsBack() {
		return getDamage() > 0;
	}

	public abstract boolean isAgressive();

	public abstract int getExp();

	public abstract HashMap<ItemStack, Float> drops();

	public UUID getId() {
		return this.id;
	}

	public Vector getSpawnZone() {
		return this.spawnZone;
	}

	public abstract int getExtraTime();

	public abstract boolean isPet();

	public void cancel() {
		Manager.cancelTask(Integer.valueOf(taskId));
		taskId = -1;
	}

	public void die() {
		dead = true;
	}

	public boolean isDead() {
		return dead;
	}

	public void setTarget(Entity e, LivingEntity target) {
		if (attack != null)
			return;
		if (!(target instanceof Creature))
			return;
		attack = target.getUniqueId();
		Manager.scheduleTask(new Runnable() {

			@Override
			public void run() {
				attack = null;
			}
		}, 60L);

		NMSMethods.setTarget(e, target);

	}

	public UUID getAttack() {
		return attack;
	}

	public void killedAttack() {
		attack = null;
	}
}
