package org.caliog.Rolecraft.XMechanics.npclib;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.HumanEntity;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.caliog.Rolecraft.XMechanics.Reflection.Reflect;
import org.caliog.Rolecraft.XMechanics.npclib.NMS.NPCUtils;
import org.caliog.Rolecraft.XMechanics.npclib.v1_12_R1.NPCEntity;

public class NPC extends Moveable {

	// enumItemSlot, ItemStack
	private HashMap<Object, Object> previousEquipment = new HashMap<Object, Object>();
	private Class<?> entityClass, worldClass, worldServerClass, entityTrackerClass, packetPlayOutAnimationClass,
			itemstackClass, enumItemSlotClass, entityPlayerClass, packetPlayOutEntityEquipmentClass, packetClass;

	public NPC(Entity bukkitEntity) {
		try {
			entityClass = Reflect.getNMSClass("Entity");
			worldClass = Reflect.getNMSClass("World");
			worldServerClass = Reflect.getNMSClass("WorldServer");
			entityTrackerClass = Reflect.getNMSClass("EntityTracker");
			packetClass = Reflect.getNMSClass("Packet");
			packetPlayOutAnimationClass = Reflect.getNMSClass("PacketPlayOutAnimation");
			itemstackClass = Reflect.getNMSClass("ItemStack");
			enumItemSlotClass = Reflect.getNMSClass("EnumItemSlot");
			entityPlayerClass = Reflect.getNMSClass("EntityPlayer");
			packetPlayOutEntityEquipmentClass = Reflect.getNMSClass("PacketPlayOutEntityEquipment");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		this.bukkitEntity = bukkitEntity;
	}

	public void animateArmSwing() {
		try {
			Object entity = getEntity();
			Object world = entityClass.getField("world").get(entity);
			Object tracker = worldServerClass.getMethod("getTracker").invoke(worldServerClass.cast(world));
			Object packet = packetPlayOutAnimationClass.getConstructor(entityClass, int.class).newInstance(entity, 0);
			entityTrackerClass.getMethod("a", entityClass, packetClass).invoke(tracker, entity, packet);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void actAsHurt() {
		try {
			Object entity = getEntity();
			Object world = entityClass.getField("world").get(entity);
			worldServerClass.getMethod("broadcastEntityEffect", entityClass, byte.class)
					.invoke(worldServerClass.cast(world), entity, (byte) 2);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void setItemInHand(Material m) {
		setItemInMainHand(m, (short) 0);
	}

	@SuppressWarnings("deprecation")
	public void setItemInMainHand(Material m, short damage) {
		try {
			Object entity = getEntity();
			HumanEntity hEntity = (HumanEntity) entityClass.getMethod("getBukkitEntity").invoke(entity);
			hEntity.getInventory().setItemInMainHand(new ItemStack(m, 1, damage));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void setName(String name) {
		try {
			final Class<?> clazz = Reflect.getNMSClass("EntityHuman");
			final Field nameField = clazz.getDeclaredField("name");
			nameField.setAccessible(true);
			nameField.set(getEntity(), name);
		} catch (final Exception e) {
			e.printStackTrace();
		}
	}

	// not independent
	public String getName() {
		return ((NPCEntity) getEntity()).getName();
	}

	public PlayerInventory getInventory() {
		Object entity = getEntity();
		HumanEntity hEntity;
		try {
			hEntity = (HumanEntity) entityClass.getMethod("getBukkitEntity").invoke(entity);
			return hEntity.getInventory();
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException
				| SecurityException e) {
			e.printStackTrace();
			return null;
		}
	}

	public void lookAtPoint(Location point) {
		Object entity = getEntity();
		try {
			HumanEntity hEntity = (HumanEntity) entityClass.getMethod("getBukkitEntity").invoke(entity);
			if (hEntity.getWorld() != point.getWorld()) {
				return;
			}
			final Location npcLoc = hEntity.getEyeLocation();
			final double xDiff = point.getX() - npcLoc.getX();
			final double yDiff = point.getY() - npcLoc.getY();
			final double zDiff = point.getZ() - npcLoc.getZ();
			final double DistanceXZ = Math.sqrt(xDiff * xDiff + zDiff * zDiff);
			final double DistanceY = Math.sqrt(DistanceXZ * DistanceXZ + yDiff * yDiff);
			double newYaw = Math.toDegrees(Math.acos(xDiff / DistanceXZ));
			final double newPitch = Math.toDegrees(Math.acos(yDiff / DistanceY)) - 90D;
			if (zDiff < 0.0) {
				newYaw += Math.abs(180D - newYaw) * 2D;
			}
			newYaw -= 90D;
			entityClass.getField("pitch").set(entity, (float) newPitch);
			setYaw((float) newYaw);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	@Override
	public void moveTo(Location l) {
		getBukkitEntity().teleport(l);
		Object entity = getEntity();
		try {
			entityClass.getMethod("setPositionRotation", double.class, double.class, double.class, float.class,
					float.class).invoke(entity, l.getX(), l.getY(), l.getZ(), l.getYaw(), l.getPitch());
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException
				| SecurityException e) {
			e.printStackTrace();
		}
		setYaw(l.getYaw());
	}

	public void updateEquipment() {
		try {
			Method getEquipment = entityPlayerClass.getMethod("getEquipment", enumItemSlotClass);
			Constructor<?> packetConstr = packetPlayOutEntityEquipmentClass.getConstructor(int.class, enumItemSlotClass,
					itemstackClass);
			Object entity = getEntity();
			int changes = 0;
			HashMap<Object, Object> newI = new HashMap<Object, Object>();
			// could be invalid cast (array of EnumItemSlot)
			Object[] values = (Object[]) enumItemSlotClass.getMethod("values").invoke(null);
			for (Object i : values) {
				// both ItemStack
				Object previous = previousEquipment.get(i);
				Object current = getEquipment.invoke(entityPlayerClass.cast(entity), i);
				newI.put(i, current);
				if (current == null) {
					if (previous != null) {
						Object packet = packetConstr.newInstance(entityClass.getMethod("getId").invoke(entity), i,
								current);
						NPCUtils.sendPacketNearby(getBukkitEntity().getLocation(), packet);
						++changes;
					}
				} else {
					if (previous == null || !previous.equals(current)) {
						Object packet = packetConstr.newInstance(entityClass.getMethod("getId").invoke(entity), i,
								current);
						NPCUtils.sendPacketNearby(getBukkitEntity().getLocation(), packet);
						++changes;
					}
				}
			}

			if (changes > 0) {
				previousEquipment = newI;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		/**/
	}

	public void removeFromWorld() {
		try {
			Object entity = getEntity();
			Object world = entityClass.getField("world").get(entity);
			worldClass.getMethod("removeEntity", entityClass).invoke(world, entity);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public Object getEntity() {
		if (NMSUtil.getUtil() != null)
			return NMSUtil.getUtil().getHandle(getBukkitEntity());
		return null;
	}

}
