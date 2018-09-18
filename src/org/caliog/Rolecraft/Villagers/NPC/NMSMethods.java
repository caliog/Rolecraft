package org.caliog.Rolecraft.Villagers.NPC;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Set;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.caliog.Rolecraft.Manager;
import org.caliog.Rolecraft.Villagers.Utils.Recipe;
import org.caliog.Rolecraft.XMechanics.Debug.Debugger;
import org.caliog.Rolecraft.XMechanics.Reflection.Reflect;

public class NMSMethods {

	public static void initVillager(VillagerNPC npc) {
		try {
			Class<?> entityInsentient = Reflect.getNMSClass("EntityInsentient");
			Class<?> entityCreature = Reflect.getNMSClass("EntityCreature");
			Class<?> craftEntity = Reflect.getCraftbukkitClass("entity.CraftEntity");
			Class<?> entityHuman = Reflect.getNMSClass("EntityHuman");
			Class<?> pathfinderGoalSelector = Reflect.getNMSClass("PathfinderGoalSelector");
			Class<?> pathfinderGoalRandomStroll = Reflect.getNMSClass("PathfinderGoalRandomStroll");
			Class<?> pathfinderGoalLookAtPlayer = Reflect.getNMSClass("PathfinderGoalLookAtPlayer");
			Class<?> pathfinderGoal = Reflect.getNMSClass("PathfinderGoal");

			Object entity = entityInsentient.cast(craftEntity.getMethod("getHandle").invoke(npc.getBukkitEntity()));
			Field goalsField = entityInsentient.getDeclaredField("goalSelector");
			goalsField.setAccessible(true);
			Object goals = pathfinderGoalSelector.cast(goalsField.get(entity));

			// TODO field name "b" is variable
			Field listField = pathfinderGoalSelector.getDeclaredField("b");
			listField.setAccessible(true);
			Set<?> list = (Set<?>) listField.get(goals);
			list.clear();
			// TODO field name "c" is variable
			listField = pathfinderGoalSelector.getDeclaredField("c");
			listField.setAccessible(true);

			list = (Set<?>) listField.get(goals);
			list.clear();

			// TODO method name "a" is variable
			Method a = pathfinderGoalSelector.getMethod("a", int.class, pathfinderGoal);
			a.invoke(goals, 6,
					pathfinderGoalRandomStroll.getConstructor(entityCreature, double.class).newInstance(entity, 0D));
			a.invoke(goals, 7, pathfinderGoalLookAtPlayer.getConstructor(entityInsentient, Class.class, float.class)
					.newInstance(entityInsentient.cast(entity), entityHuman, 8F));

		} catch (Exception e) {
			Debugger.exception("NPC.NMSMethods in initVillager threw exception:", e.getMessage());
			e.printStackTrace();
		}

	}

	public static String getRecipeListFieldName() {
		if (Manager.getServerVersion().equals("v1_12_R1"))
			return "bK";
		else
			return "bJ";
	}

	public static boolean openInventory(Trader trader, Player player) {
		Recipe recipe = trader.getRecipe();
		if (recipe.isEmpty())
			return false;
		try {
			Class<?> entityVillager = Reflect.getNMSClass("EntityVillager");
			Class<?> world = Reflect.getNMSClass("World");
			Class<?> craftPlayer = Reflect.getCraftbukkitClass("entity.CraftPlayer");
			Class<?> entityPlayer = Reflect.getNMSClass("EntityPlayer");
			Class<?> entityHuman = Reflect.getNMSClass("EntityHuman");
			Class<?> merchantRecipeList = Reflect.getNMSClass("MerchantRecipeList");
			Class<?> imerchant = Reflect.getNMSClass("IMerchant");
			Class<?> statistic = Reflect.getNMSClass("Statistic");
			Class<?> statisticList = Reflect.getNMSClass("StatisticList");

			Object handle = craftPlayer.getMethod("getHandle").invoke(player);
			Object villager = entityVillager.getConstructor(world, int.class)
					.newInstance(entityPlayer.getField("world").get(handle), 0);
			if ((trader.getName() != null)) {
				entityVillager.getMethod("setCustomName", String.class).invoke(villager, trader.getName());
			}

			Field careerLevelField = entityVillager.getDeclaredField(getRecipeListFieldName());
			careerLevelField.setAccessible(true);
			careerLevelField.set(villager, Integer.valueOf(10));

			Field recipeListField = entityVillager.getDeclaredField("trades");
			recipeListField.setAccessible(true);
			Object recipeList = merchantRecipeList.cast(recipeListField.get(villager));
			if (recipeList == null) {
				recipeList = merchantRecipeList.getConstructor().newInstance();
				recipeListField.set(villager, recipeList);
			}
			merchantRecipeList.getMethod("clear").invoke(recipeList);

			for (org.bukkit.inventory.ItemStack[] rec : recipe.getRecipe()) {
				if (rec[2] == null || rec[2].getType().equals(Material.AIR))
					continue;
				merchantRecipeList.getMethod("add", Object.class).invoke(recipeList,
						createRecipe(rec[0], rec[1], rec[2]));
			}
			recipeListField.set(villager, recipeList);
			entityVillager.getMethod("setTradingPlayer", entityHuman).invoke(villager, handle);
			entityPlayer.getMethod("openTrade", imerchant).invoke(handle, villager);

			// TODO method name "b" and field name "F" are variable
			entityPlayer.getMethod("b", statistic).invoke(handle, statisticList.getField("F").get(null));
			return true;
		} catch (Exception e) {
			Debugger.exception("NPC.NMSMethods in openInventory threw exception:", e.getMessage());
			e.printStackTrace();
		}
		return false;

	}

	private static Object createRecipe(ItemStack item1, ItemStack item2, ItemStack item3) {
		Object recipe = null;
		Field maxUsesField;
		if (item2 == null)
			item2 = new ItemStack(Material.AIR);
		try {
			Class<?> merchantRecipe = Reflect.getNMSClass("MerchantRecipe");
			Class<?> itemStack = Reflect.getNMSClass("ItemStack");
			recipe = merchantRecipe.getConstructor(itemStack, itemStack, itemStack).newInstance(getHandle(item1),
					getHandle(item2), getHandle(item3));

			maxUsesField = merchantRecipe.getDeclaredField("maxUses");
			maxUsesField.setAccessible(true);
			maxUsesField.set(recipe, Integer.valueOf(99999));
		} catch (Exception e) {
			Debugger.exception("NPC.NMSMethods in createRecipe threw exception:", e.getMessage());
			e.printStackTrace();
		}
		return recipe;
	}

	private static Object getHandle(ItemStack item) throws Exception {
		if (item == null)
			return null;
		Class<?> craftItemStack = Reflect.getCraftbukkitClass("inventory.CraftItemStack");
		return craftItemStack.getMethod("asNMSCopy", org.bukkit.inventory.ItemStack.class).invoke(null, item);
	}

}
