package org.caliog.Rolecraft.XMechanics.npclib.v1_12_R1;

import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_12_R1.entity.CraftEntity;
import org.bukkit.event.entity.EntityTargetEvent;
import org.caliog.Rolecraft.XMechanics.npclib.NpcEntityTargetEvent;
import org.caliog.Rolecraft.XMechanics.npclib.NMS.BWorld;

import com.mojang.authlib.GameProfile;

import net.minecraft.server.v1_12_R1.Entity;
import net.minecraft.server.v1_12_R1.EntityHuman;
import net.minecraft.server.v1_12_R1.EntityPlayer;
import net.minecraft.server.v1_12_R1.EnumGamemode;
import net.minecraft.server.v1_12_R1.EnumMoveType;
import net.minecraft.server.v1_12_R1.MinecraftServer;
import net.minecraft.server.v1_12_R1.PlayerInteractManager;
import net.minecraft.server.v1_12_R1.WorldServer;

public class NPCEntity extends EntityPlayer {

	@SuppressWarnings("unused")
	private int lastTargetId;
	private long lastBounceTick;
	private int lastBounceId;

	public NPCEntity(NPCManager npcManager, BWorld world, GameProfile s, PlayerInteractManager itemInWorldManager) {
		super((MinecraftServer) npcManager.getServer().getMCServer(), (WorldServer) world.getWorldServer(), s, itemInWorldManager);

		itemInWorldManager.b(EnumGamemode.SURVIVAL);

		playerConnection = new NPCPlayerConnection(npcManager, this);
		lastTargetId = -1;
		lastBounceId = -1;
		lastBounceTick = 0;

		fauxSleeping = true;
	}

	public void setBukkitEntity(org.bukkit.entity.Entity entity) {
		bukkitEntity = (CraftEntity) entity;
	}

	@Override
	public boolean a(EntityHuman entity) {
		final EntityTargetEvent event = new NpcEntityTargetEvent(getBukkitEntity(), entity.getBukkitEntity(),
				NpcEntityTargetEvent.NpcTargetReason.NPC_RIGHTCLICKED);
		Bukkit.getPluginManager().callEvent(event);

		return super.a(entity);
	}

	/*
	 * 
	 * @Override public void b_(EntityHuman entity) { if ((lastBounceId !=
	 * entity.getId() || System.currentTimeMillis() - lastBounceTick > 1000) &&
	 * entity.getBukkitEntity().getLocation().distanceSquared(getBukkitEntity().
	 * getLocation()) <= 1) { final EntityTargetEvent event = new
	 * NpcEntityTargetEvent(getBukkitEntity(), entity.getBukkitEntity(),
	 * NpcEntityTargetEvent.NpcTargetReason.NPC_BOUNCED);
	 * Bukkit.getPluginManager().callEvent(event);
	 * 
	 * lastBounceTick = System.currentTimeMillis(); lastBounceId =
	 * entity.getId(); }
	 * 
	 * if (lastTargetId == -1 || lastTargetId != entity.getId()) { final
	 * EntityTargetEvent event = new NpcEntityTargetEvent(getBukkitEntity(),
	 * entity.getBukkitEntity(),
	 * NpcEntityTargetEvent.NpcTargetReason.CLOSEST_PLAYER);
	 * Bukkit.getPluginManager().callEvent(event); lastTargetId =
	 * entity.getId(); }
	 * 
	 * super.b_(entity); }
	 */
	@Override
	public void c(Entity entity) {
		if (lastBounceId != entity.getId() || System.currentTimeMillis() - lastBounceTick > 1000) {
			final EntityTargetEvent event = new NpcEntityTargetEvent(getBukkitEntity(), entity.getBukkitEntity(),
					NpcEntityTargetEvent.NpcTargetReason.NPC_BOUNCED);
			Bukkit.getPluginManager().callEvent(event);

			lastBounceTick = System.currentTimeMillis();
		}

		lastBounceId = entity.getId();

		super.c(entity);
	}

	@Override
	public void move(EnumMoveType type, double arg0, double arg1, double arg2) {
		setPosition(arg0, arg1, arg2);
	}

}
