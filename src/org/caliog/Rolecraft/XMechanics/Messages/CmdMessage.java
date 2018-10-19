package org.caliog.Rolecraft.XMechanics.Messages;

import org.bukkit.ChatColor;

public class CmdMessage {

	public static String notKnownItem = ChatColor.RED + "I don't know this item...check its name again.";
	public static String createdMSZ = ChatColor.GOLD + "Created a mob spawn-zone here!";
	public static String removedMSZ = ChatColor.GOLD + "Removed this spawn-zone!";
	public static String hereIsNoMSZ = ChatColor.GOLD + "I cannot find a spawn-zone near you!";
	public static String noVillager = ChatColor.RED + "There is no villager around you!";
	public static String noGuard = ChatColor.RED + "There is no guard around you!";
	public static String savedItemMob = ChatColor.GOLD + "Saved as %A% to your files.";
	public static String failedSaveItemMob = ChatColor.RED + "Couldn't save that, missing info.";

}
