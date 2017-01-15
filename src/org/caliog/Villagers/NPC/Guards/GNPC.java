package org.caliog.Villagers.NPC.Guards;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.caliog.npclib.NPC;
import org.caliog.npclib.NPCManager;

public abstract class GNPC extends CPMoveable {

	final private int id;
	private String name;
	protected NPC npc;
	private boolean isLooking = false;
	protected int radius;
	private List<String> phrases = new ArrayList<String>();
	private List<String> owners = new ArrayList<String>();

	public GNPC(String name, Location loc, int id) {
		super(loc);
		this.setNpc(NPCManager.npcManager.spawnHumanNPC(ChatColor.translateAlternateColorCodes('$', name), loc, String.valueOf(id)));
		this.setEntity(npc.getBukkitEntity());
		this.id = id;
		this.name = name;
	}

	public int getId() {
		return id;
	}

	public NPC getNpc() {
		return npc;
	}

	public void setNpc(NPC npc) {
		this.npc = npc;
	}

	public boolean isLooking() {
		return this.isLooking;
	}

	public void setIsLooking(boolean b) {
		this.isLooking = b;
	}

	public int getRadius() {
		return this.radius;
	}

	public void setRadius(int radius) {
		this.radius = radius;
	}

	public List<String> getPhrases() {
		return phrases;
	}

	public void setPhrases(List<String> phrases) {
		this.phrases = phrases;
	}

	public String getRandomPhrase() {
		int rnd = (int) (Math.random() * phrases.size());
		return phrases.get((int) rnd);
	}

	public void addPhrase(String string) {
		phrases.add(string);

	}

	public void removePhrase(String string) {
		for (String phrase : phrases) {
			if (phrase.toLowerCase().startsWith(string.toLowerCase())) {
				phrases.remove(phrase);
				break;
			}
		}

	}

	public List<String> getOwners() {
		return owners;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

}
