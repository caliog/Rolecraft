package org.caliog.Rolecraft.XMechanics.npclib;

import java.util.ArrayList;
import java.util.Iterator;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Entity;

public abstract class Moveable {

	protected Entity bukkitEntity;

	private NPCPathFinder path;
	public Iterator<Node> pathIterator;
	public Node last;
	public NPCPath runningPath;
	public int taskid;
	public Runnable onFail;

	public void moveTo(Location l) {
		getBukkitEntity().teleport(l);
	}

	public void pathFindTo(Location l, PathReturn callback) {
		pathFindTo(l, 3000, callback);
	}

	public void pathFindTo(Location l, int maxIterations, PathReturn callback) {
		if (this.path != null) {
			this.path.cancel = true;
		}
		if (l.getWorld() != getBukkitEntity().getWorld()) {
			ArrayList<Node> pathList = new ArrayList<Node>();
			pathList.add(new Node(l.getBlock()));
			callback.run(new NPCPath(null, pathList, l));
		} else {
			this.path = new NPCPathFinder(getBukkitEntity().getLocation(), l, maxIterations, callback);
			if (NPCManager.npcManager != null)
				Bukkit.getScheduler().scheduleSyncDelayedTask(NPCManager.npcManager.getPlugin(), this.path);
		}
	}

	public void walkTo(Location l) {
		walkTo(l, 3000);
	}

	public void walkTo(final Location l, final int maxIterations) {
		pathFindTo(l, maxIterations, new PathReturn() {
			public void run(NPCPath path) {
				usePath(path, new Runnable() {
					public void run() {
						walkTo(l, maxIterations);
					}
				});
			}
		});
	}

	public void usePath(NPCPath path) {
		usePath(path, new Runnable() {
			public void run() {
				walkTo(runningPath.getEnd(), 3000);
			}
		});
	}

	public void usePath(NPCPath path, Runnable onFail) {
		final Moveable a = this;
		if (NPCManager.npcManager == null)
			return;
		if (this.taskid == 0) {
			this.taskid = Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(NPCManager.npcManager.getPlugin(), new Runnable() {
				public void run() {
					NMSUtil util = NMS.getUtil();
					if (util != null)
						util.pathStep(a);
				}
			}, 10L, 8L);
		}
		this.pathIterator = path.getPath().iterator();
		this.runningPath = path;
		this.onFail = onFail;
	}

	public void setYaw(float yaw) {
		NMSUtil util = NMS.getUtil();
		if (util != null)
			util.setYaw(getBukkitEntity(), yaw);
	}

	public boolean isRunning() {
		return taskid != 0;
	}

	public Location getEntityLocation() {
		return bukkitEntity.getLocation();
	}

	public Entity getBukkitEntity() {
		return bukkitEntity;
	}

	public void setEntity(Entity entity) {
		this.bukkitEntity = entity;
	}

}
