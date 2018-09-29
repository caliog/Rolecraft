package org.caliog.Rolecraft.Villagers.NPC;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Set;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.MerchantInventory;
import org.bukkit.inventory.MerchantRecipe;
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

	private static String getCareerLevelField() {
		if (Manager.getServerVersion().equals("v1_12_R1"))
			return "bK";
		else if (Manager.getServerVersion().startsWith("v1_11"))
			return "bJ";
		else
			return "careerId";
	}

	public static boolean openInventory(Trader trader, Player player) {
		Recipe recipe = trader.getRecipe();
		if (recipe.isEmpty())
			return false;
		try {
			Class<?> entityVillager = Reflect.getNMSClass("EntityVillager");
			Class<?> craftPlayer = Reflect.getCraftbukkitClass("entity.CraftPlayer");
			Class<?> craftVillager = Reflect.getCraftbukkitClass("entity.CraftVillager");
			Class<?> entityPlayer = Reflect.getNMSClass("EntityPlayer");
			Class<?> entityHuman = Reflect.getNMSClass("EntityHuman");
			Class<?> merchantRecipeList = Reflect.getNMSClass("MerchantRecipeList");
			Class<?> imerchant = Reflect.getNMSClass("IMerchant");
			Class<?> statistic = Reflect.getNMSClass("Statistic");
			Class<?> statisticList = Reflect.getNMSClass("StatisticList");
			Class<?> minecraftKey = Reflect.getNMSClass("MinecraftKey");
			Class<?> aminecraftKey = Array.newInstance(minecraftKey, 0).getClass();

			Object handle = craftPlayer.getMethod("getHandle").invoke(player);
			Object villager = craftVillager.getMethod("getHandle").invoke(trader.getBukkitEntity());

			Field careerLevelField = entityVillager.getDeclaredField(getCareerLevelField());
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

			// -- VC
			// TODO orga
			if (Manager.getServerVersion().startsWith("v1_13")) {
				Object key = Array.newInstance(minecraftKey, 1);
				Array.set(key, 0, minecraftKey.cast(statisticList.getField("TRADED_WITH_VILLAGER").get(null)));
				entityPlayer.getMethod("a", aminecraftKey).invoke(handle, key);
			} else
				entityPlayer.getMethod("b", statistic).invoke(handle, statisticList.getField("F").get(null));
			//--

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

	public static MerchantRecipe getCurrentRecipe(MerchantInventory inv) {
		try {
			Class<?> craftInventoryMerchant = Reflect.getCraftbukkitClass("inventory.CraftInventoryMerchant");
			Class<?> merchantRecipe = Reflect.getNMSClass("MerchantRecipe");
			Class<?> merchantRecipeList = Reflect.getNMSClass("MerchantRecipeList");
			Class<?> inventoryMerchant = Reflect.getNMSClass("InventoryMerchant");
			Class<?> entityVillager = Reflect.getNMSClass("EntityVillager");

			Method getInventory = craftInventoryMerchant.getMethod("getInventory");
			Field imerchant = inventoryMerchant.getDeclaredField("merchant");
			Field selectedIndex = inventoryMerchant.getField("selectedIndex");
			Field trades = entityVillager.getField("trades");

			Object inventory = getInventory.invoke(craftInventoryMerchant.cast(inv));
			imerchant.setAccessible(true);
			Object merchant = imerchant.get(inventory);
			int index = selectedIndex.getInt(inventory);
			Object recipeList = trades.get(entityVillager.cast(merchant));
			Object recipe = merchantRecipeList.getMethod("get", int.class).invoke(recipeList, index);
			Object bukkitRecipe = merchantRecipe.getMethod("asBukkit").invoke(recipe);
			return (MerchantRecipe) bukkitRecipe;

		} catch (ClassNotFoundException | NoSuchMethodException | SecurityException | IllegalAccessException
				| IllegalArgumentException | InvocationTargetException | NoSuchFieldException e) {
			e.printStackTrace();
		}
		return null;
	}

}
