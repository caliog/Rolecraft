package org.caliog.Rolecraft.XMechanics.npclib.v1_12_R1;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_12_R1.entity.CraftEntity;
import org.bukkit.craftbukkit.v1_12_R1.entity.CraftPlayer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.caliog.Rolecraft.Manager;
import org.caliog.Rolecraft.XMechanics.npclib.Moveable;
import org.caliog.Rolecraft.XMechanics.npclib.NMSUtil;
import org.caliog.Rolecraft.XMechanics.npclib.Node;
import org.caliog.Rolecraft.XMechanics.npclib.NMS.BWorld;

import com.mojang.authlib.GameProfile;

import net.minecraft.server.v1_12_R1.AxisAlignedBB;
import net.minecraft.server.v1_12_R1.Block;
import net.minecraft.server.v1_12_R1.BlockPosition;
import net.minecraft.server.v1_12_R1.EntityLiving;
import net.minecraft.server.v1_12_R1.EntityPlayer;
import net.minecraft.server.v1_12_R1.IBlockAccess;
import net.minecraft.server.v1_12_R1.PacketPlayOutPlayerInfo;
import net.minecraft.server.v1_12_R1.PlayerInteractManager;
import net.minecraft.server.v1_12_R1.WorldServer;

public class Util implements NMSUtil {

	public static net.minecraft.server.v1_12_R1.Entity getHandle(Entity e) {
		return ((CraftEntity) e).getHandle();
	}

	public void setYaw(Entity entity, float yaw) {
		while (yaw < -180)
			yaw += 360F;
		while (yaw >= 180)
			yaw -= 360;

		net.minecraft.server.v1_12_R1.Entity e = getHandle(entity);
		e.yaw = yaw;
		EntityLiving ee = (EntityLiving) e;
		ee.aP = yaw;
	}

	public void pathStep(Moveable a) {

		if (a.pathIterator.hasNext()) {
			Node n = (Node) a.pathIterator.next();
			if (n.b.getWorld() != a.getBukkitEntity().getWorld()) {
				a.getBukkitEntity().teleport(n.b.getLocation());
			} else {
				float angle = getHandle(a.getBukkitEntity()).yaw;
				float look = getHandle(a.getBukkitEntity()).pitch;
				if ((a.last == null) || (a.runningPath.checkPath(n, a.last, true))) {
					if (a.last != null && !a.last.b.equals(n.b)) {
						angle = (float) Math.toDegrees(Math.atan2(a.last.b.getX() - n.b.getX(), n.b.getZ() - a.last.b.getZ()));
						look = (float) (Math.toDegrees(Math.asin(a.last.b.getY() - n.b.getY())) / 2.0D);
					}
					getHandle(a.getBukkitEntity()).setPositionRotation(n.b.getX() + 0.5D, n.b.getY(), n.b.getZ() + 0.5D, angle, look);
					setYaw(a.getBukkitEntity(), angle);
				} else {
					a.onFail.run();
				}
			}
			a.last = n;
		} else {
			// getHandle(a.getBukkitEntity()).setPositionRotation(a.runningPath.getEnd().getX(),
			// a.runningPath.getEnd().getY(),
			// a.runningPath.getEnd().getZ(), a.runningPath.getEnd().getYaw(),
			// a.runningPath.getEnd().getPitch());
			// setYaw(a.getBukkitEntity(), a.runningPath.getEnd().getYaw());
			Bukkit.getServer().getScheduler().cancelTask(a.taskid);
			a.taskid = 0;
		}
	}

	@Override
	public org.caliog.Rolecraft.XMechanics.npclib.NPCManager getnpcManager() {
		return new NPCManager(Manager.plugin);
	}

	@SuppressWarnings("deprecation")
	@Override
	public void nodeUpdate(Node node) {

		node.setNotSolid(true);
		if (node.b.getType() != Material.AIR) {

			Block block = Block.getById(node.b.getTypeId());
			IBlockAccess access = null;

			try {
				final AxisAlignedBB box = block.a(Block.getByCombinedId(node.b.getTypeId()), access,
						new BlockPosition(node.b.getX(), node.b.getY(), node.b.getZ()));
				if (box != null) {
					if (Math.abs(box.e - box.b) > 0.2) {
						node.setNotSolid(false);
					}
				}
			} catch (Exception e) {
				// e.printStackTrace();
				node.setNotSolid(false);
			}
			// final AxisAlignedBB box =
			// net.minecraft.server.v1_11_R1.Block.getById(node.b.getTypeId()).a(
			// net.minecraft.server.v1_11_R1.Block.getByCombinedId(node.b.getTypeId()),
			// ((CraftWorld) node.b.getWorld()).getHandle(),
			// new BlockPosition(node.b.getX(), node.b.getY(), node.b.getZ()));

		}
		node.setLiquid(node.getLiquids().contains(node.b.getType()));

	}

	@Override
	public org.bukkit.entity.Entity createNPCEntity(org.caliog.Rolecraft.XMechanics.npclib.NPCManager manager, BWorld world, String name) {
		final NPCEntity npcEntity = new NPCEntity((NPCManager) manager, world, new GameProfile(UUID.randomUUID(), name),
				new PlayerInteractManager((WorldServer) world.getWorldServer()));
		NMSUtil.sendPacketsTo(Bukkit.getOnlinePlayers(),
				new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.ADD_PLAYER, new EntityPlayer[] { npcEntity }));

		return npcEntity.getBukkitEntity();
	}

	@Override
	public Object getPlayerHandle(Player p) {
		return ((CraftPlayer) p).getHandle();
	}

}
