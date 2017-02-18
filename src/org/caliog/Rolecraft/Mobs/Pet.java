package org.caliog.Rolecraft.Mobs;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.caliog.Rolecraft.Manager;
import org.caliog.Rolecraft.Entities.VolatileEntities;
import org.caliog.Rolecraft.Entities.Player.Playerface;
import org.caliog.Rolecraft.Entities.Player.RolecraftPlayer;
import org.caliog.Rolecraft.XMechanics.Utils.ParticleEffect;
import org.caliog.Rolecraft.XMechanics.Utils.Utils;
import org.caliog.Rolecraft.XMechanics.Utils.Vector;

import org.bukkit.ChatColor;

public class Pet extends MobInstance {

	private final String c_name;

	public Pet(String name, String c_name, UUID id) {
		super(name, id, new Vector(null));
		this.c_name = c_name;

	}

	@Override
	public String getCustomName() {
		return c_name;
	}

	public void die(RolecraftPlayer player) {
		die(player, true);
	}

	public void die(RolecraftPlayer player, boolean t) {
		super.die();
		VolatileEntities.remove(getUniqueId());
		Pet.givePetEgg(player.getPlayer(), getName(), getCustomName());
		if (t)
			player.getPets().remove(this);
	}

	@SuppressWarnings("deprecation")
	public static Pet spawnPet(String name, String customName, final Location loc) {
		Entity entity = null;
		Pet mob = null;

		EntityType type = new MobInstance(name, null, null).getType();
		entity = loc.getWorld().spawnEntity(loc, type);
		mob = new Pet(name, customName, entity.getUniqueId());

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

			return mob;
		}
		return null;
	}

	public static void givePetEgg(Player player, String mob, String customName) {
		ItemStack egg = new ItemStack(Material.EGG);
		ItemMeta meta = Bukkit.getItemFactory().getItemMeta(Material.EGG);
		if (Utils.isBukkitClass("org.bukkit.inventory.ItemFlag"))
			meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
		meta.setDisplayName(ChatColor.GOLD + customName + "(" + mob + ")");
		List<String> lore = new ArrayList<String>();
		lore.add(ChatColor.GRAY + "Spawns a " + mob + "!");
		meta.setLore(lore);
		egg.setItemMeta(meta);
		Playerface.giveItem(player, egg);
	}

}
