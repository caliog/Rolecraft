package org.caliog.Rolecraft.XMechanics.npclib;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.caliog.Rolecraft.XMechanics.Utils.VersionControll.Mat;

public class Node { // Holds data about each block we check

	static List<Material> liquids = new ArrayList<>();

	static {
		liquids.add(Material.WATER);
		liquids.add(Mat.STATIONARY_WATER.e());
		// liquids.add(Material.LAVA); Maybe swimming in lava isn't the best
		// idea for npcs
		// liquids.add(Material.STATIONARY_LAVA);
		liquids.add(Material.LADDER); // Trust me it makes sense
	}

	int f, g = 0, h;
	int xPos, yPos, zPos;
	Node parent;
	public Block b;
	private boolean notsolid;
	private boolean liquid;

	public Node(Block b) {
		this.b = b;
		xPos = b.getX();
		yPos = b.getY();
		zPos = b.getZ();
		update();
	}

	public void update() {
		setNotSolid(!b.getType().isSolid());
		setLiquid(getLiquids().contains(b.getType()));
	}

	public boolean isNotsolid() {
		return notsolid;
	}

	public void setNotSolid(boolean notsolid) {
		this.notsolid = notsolid;
	}

	public boolean isLiquid() {
		return liquid;
	}

	public void setLiquid(boolean liquid) {
		this.liquid = liquid;
	}

	public List<Material> getLiquids() {
		return liquids;
	}

}
