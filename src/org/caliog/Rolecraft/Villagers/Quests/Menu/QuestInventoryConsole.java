package org.caliog.Rolecraft.Villagers.Quests.Menu;

public class QuestInventoryConsole {

	// @formatter:off
/*
	// Let the player choose a quest villager
	public static void chooseQuestVillager(final QuestEditorMenu questInventory, final Player player) {
		player.closeInventory();
		ConsoleReader cr = new ConsoleReader(player) {

			@Override
			public void doWork(String lastLine) {
				// lastLine is only null if it is the first time the method is called
				if (lastLine == null)
					player.sendMessage(ChatColor.GRAY + "Enter the name of the quest villager: (q to quit)");
				else {

					Villager v;
					if ((v = VManager.getVillager(lastLine)) != null) {
						//questInventory.setQuestVillager(v.getName());
						quit();
					} else {
						player.sendMessage(ChatColor.BOLD + lastLine + ChatColor.GRAY + " is not a villager.");
						player.sendMessage(ChatColor.GRAY + "Enter the name of the quest villager: (q to quit)");
					}
				}
			}

			@Override
			public void quit() {
				super.stop();
				player.openInventory(questInventory);
			}
		};
		cr.setTaskID(Manager.scheduleRepeatingTask(cr, 0L, 4L));
	}

	public static void chooseTargetVillager(QuestEditorMenu questInventory, Player player) {
		player.closeInventory();
		ConsoleReader cr = new ConsoleReader(player) {

			@Override
			public void doWork(String lastLine) {
				// lastLine is only null if it is the first time the method is called
				if (lastLine == null)
					player.sendMessage(ChatColor.GRAY + "Enter the name of the target villager: (q to quit)");
				else {

					Villager v;
					if ((v = VManager.getVillager(lastLine)) != null) {
						questInventory.setTargetVillager(v.getName());
						quit();
					} else {
						player.sendMessage(ChatColor.BOLD + lastLine + ChatColor.GRAY + " is not a villager.");
						player.sendMessage(ChatColor.GRAY + "Enter the name of the target villager: (q to quit)");
					}
				}
			}

			@Override
			public void quit() {
				super.stop();
				player.openInventory(questInventory);
			}
		};
		cr.setTaskID(Manager.scheduleRepeatingTask(cr, 0L, 4L));
	}

	public static void chooseMob(QuestEditorMenu questInventory, Player player, int slot, String mobName, boolean isLeftClick) {
		String askMsg = isLeftClick ? "Enter the name of a mob: (q to quit)"
				: "Enter the amount of " + mobName + "s, one has to kill: (q to quit)";
		player.closeInventory();
		ConsoleReader cr = new ConsoleReader(player) {

			@Override
			public void doWork(String lastLine) {
				// lastLine is only null if it is the first time the method is called
				if (lastLine == null)
					player.sendMessage(ChatColor.GRAY + askMsg);
				else {
					if (isLeftClick)
						if (EntityUtils.isMobClass(lastLine)) {
							questInventory.setMob(slot, lastLine);
							quit();
						} else {
							player.sendMessage(ChatColor.BOLD + lastLine + ChatColor.GRAY + " is not a mob.");
							player.sendMessage(ChatColor.GRAY + askMsg);
						}
					else if (Utils.isInteger(lastLine)) {
						questInventory.setMobAmount(slot, Integer.parseInt(lastLine));
						quit();
					} else {
						player.sendMessage(ChatColor.BOLD + lastLine + ChatColor.GRAY + " is not an integer.");
						player.sendMessage(ChatColor.GRAY + askMsg);
					}

				}
			}

			@Override
			public void quit() {
				super.stop();
				player.openInventory(questInventory);
			}
		};
		cr.setTaskID(Manager.scheduleRepeatingTask(cr, 0L, 4L));
	}

	public static void chooseClazz(QuestEditorMenu questInventory, Player player) {
		player.closeInventory();
		ConsoleReader cr = new ConsoleReader(player) {

			@Override
			public void doWork(String lastLine) {
				// lastLine is only null if it is the first time the method is called
				if (lastLine == null)
					player.sendMessage(ChatColor.GRAY + "Enter the required class: (q to quit)");
				else {
					if (ClazzLoader.isClass(lastLine)) {
						questInventory.setClazz(lastLine);
						quit();
					} else {
						player.sendMessage(ChatColor.BOLD + lastLine + ChatColor.GRAY + " is not a class.");
						player.sendMessage(ChatColor.GRAY + "Enter the required class: (q to quit)");
					}
				}
			}

			@Override
			public void quit() {
				super.stop();
				player.openInventory(questInventory);
			}
		};
		cr.setTaskID(Manager.scheduleRepeatingTask(cr, 0L, 4L));

	}*/
	// @formatter:on

}
