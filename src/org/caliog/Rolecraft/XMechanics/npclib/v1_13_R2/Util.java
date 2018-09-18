package org.caliog.Rolecraft.XMechanics.npclib.v1_13_R2;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.caliog.Rolecraft.Manager;
import org.caliog.Rolecraft.XMechanics.npclib.NMSUtil;
import org.caliog.Rolecraft.XMechanics.npclib.NMS.BWorld;

import com.mojang.authlib.GameProfile;

import net.minecraft.server.v1_13_R2.EntityLiving;
import net.minecraft.server.v1_13_R2.EntityPlayer;
import net.minecraft.server.v1_13_R2.PacketPlayOutPlayerInfo;
import net.minecraft.server.v1_13_R2.PlayerInteractManager;
import net.minecraft.server.v1_13_R2.WorldServer;

public class Util extends NMSUtil {

	@Override
	public void setYaw(Entity entity, float yaw) {
		while (yaw < -180)
			yaw += 360F;
		while (yaw >= 180)
			yaw -= 360;

		net.minecraft.server.v1_13_R2.Entity e = (net.minecraft.server.v1_13_R2.Entity) getHandle(entity);
		e.yaw = yaw;
		EntityLiving ee = (EntityLiving) e;
		ee.aS = yaw;
	}

	@Override
	public org.caliog.Rolecraft.XMechanics.npclib.NPCManager getnpcManager() {
		return new NPCManager(Manager.plugin);
	}

	@Override
	public org.bukkit.entity.Entity createNPCEntity(org.caliog.Rolecraft.XMechanics.npclib.NPCManager manager,
			BWorld world, String name) {
		final NPCEntity npcEntity = new NPCEntity((NPCManager) manager, world, new GameProfile(UUID.randomUUID(), name),
				new PlayerInteractManager((WorldServer) world.getWorldServer()));
		NMSUtil.sendPacketsTo(Bukkit.getOnlinePlayers(), new PacketPlayOutPlayerInfo(
				PacketPlayOutPlayerInfo.EnumPlayerInfoAction.ADD_PLAYER, new EntityPlayer[] { npcEntity }));

		return npcEntity.getBukkitEntity();
	}

}
