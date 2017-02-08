package org.caliog.Rolecraft.Mobs;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.logging.Level;

import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.caliog.Rolecraft.Manager;
import org.caliog.Rolecraft.XMechanics.NMS.NMS;

public class NMSMethods {
	private static Class<?> entityCreature;
	private static Class<?> craftEntity;
	private static Class<?> entityInsentient;
	private static Class<?> pathfinderGoalSelector;
	private static Class<?> pathfinderGoalRandomStroll;
	private static Class<?> pathfinderGoal;
	private static Class<?> pathfinderGoalFloat;
	private static Class<?> pathfinderGoalMeleeAttack;
	private static Class<?> pathfinderGoalMoveTowardsRestriction;
	private static Class<?> pathfinderGoalHurtByTarget;
	private static Class<?> pathfinderGoalNearestAttackableTarget;

	private static boolean init = false;

	public static void init() throws ClassNotFoundException {
		init = true;
		entityCreature = NMS.getNMSClass("EntityCreature");
		craftEntity = NMS.getCraftbukkitNMSClass("entity.CraftEntity");
		entityInsentient = NMS.getNMSClass("EntityInsentient");
		pathfinderGoalSelector = NMS.getNMSClass("PathfinderGoalSelector");
		pathfinderGoal = NMS.getNMSClass("PathfinderGoal");
		pathfinderGoalRandomStroll = NMS.getNMSClass("PathfinderGoalRandomStroll");
		pathfinderGoalFloat = NMS.getNMSClass("PathfinderGoalFloat");
		pathfinderGoalMeleeAttack = NMS.getNMSClass("PathfinderGoalMeleeAttack");
		pathfinderGoalMoveTowardsRestriction = NMS.getNMSClass("PathfinderGoalMoveTowardsRestriction");
		pathfinderGoalHurtByTarget = NMS.getNMSClass("PathfinderGoalHurtByTarget");
		pathfinderGoalNearestAttackableTarget = NMS.getNMSClass("PathfinderGoalNearestAttackableTarget");
	}

	public static void setTarget(Entity e, LivingEntity target) {

		if (e != null)
			try {
				if (!init)
					init();
				Object entity = entityCreature.cast(craftEntity.getMethod("getHandle").invoke(e));
				Object t = entityCreature.cast(craftEntity.getMethod("getHandle").invoke(target));
				Field goalsField = entityInsentient.getDeclaredField("goalSelector");
				goalsField.setAccessible(true);
				Object goals = pathfinderGoalSelector.cast(goalsField.get(entity));

				Field targetField = entityInsentient.getDeclaredField("targetSelector");
				targetField.setAccessible(true);
				Object targetSelector = pathfinderGoalSelector.cast(goalsField.get(entity));

				// TODO field name "b" is variable
				Field listField = pathfinderGoalSelector.getDeclaredField("b");
				listField.setAccessible(true);
				HashSet<?> list = (HashSet<?>) listField.get(goals);
				list.clear();
				// TODO field name "c" is variable
				listField = pathfinderGoalSelector.getDeclaredField("c");
				listField.setAccessible(true);

				list = (HashSet<?>) listField.get(goals);
				list.clear();

				// check which constructor exists:
				Constructor<?> c = null;
				Object pathfinderGoalHurtByTargetInstance = null;
				try {
					c = pathfinderGoalHurtByTarget.getConstructor(entityCreature, boolean.class);
					pathfinderGoalHurtByTargetInstance = c.newInstance(entity, true);

				} catch (Exception e1) {

				}
				if (c == null) {
					Class<?>[] arrayDummy = { t.getClass() };
					try {
						c = pathfinderGoalHurtByTarget.getConstructor(entityCreature, boolean.class, arrayDummy.getClass());
						pathfinderGoalHurtByTargetInstance = c.newInstance(entity, true, arrayDummy);
					} catch (Exception e1) {
						e1.printStackTrace();
					}

				}
				if (c == null) {
					Manager.plugin.getLogger().log(Level.SEVERE,
							"Could not find valid Constructor for PathfinderGoalSelector in NMSMethods.java line 69");
					return;
				}

				// TODO method name "a" is variable
				Method a = pathfinderGoalSelector.getMethod("a", int.class, pathfinderGoal);
				a.invoke(goals, 6, pathfinderGoalRandomStroll.getConstructor(entityCreature, double.class).newInstance(entity, 0D));
				a.invoke(goals, 0, pathfinderGoalFloat.getConstructor(entityInsentient).newInstance(entity));
				a.invoke(goals, 2, pathfinderGoalMeleeAttack.getConstructor(entityCreature, double.class, boolean.class).newInstance(entity,
						1.0D, false));
				a.invoke(goals, 4,
						pathfinderGoalMoveTowardsRestriction.getConstructor(entityCreature, double.class).newInstance(entity, 1.0D));
				a.invoke(goals, 7, pathfinderGoalRandomStroll.getConstructor(entityCreature, double.class).newInstance(entity, 1D));
				a.invoke(targetSelector, 1, pathfinderGoalHurtByTargetInstance);
				a.invoke(targetSelector, 2, pathfinderGoalNearestAttackableTarget.getConstructor(entityCreature, Class.class, boolean.class)
						.newInstance(entity, t.getClass(), false));
			} catch (Exception exc) {
				exc.printStackTrace();
			}

	}

}
