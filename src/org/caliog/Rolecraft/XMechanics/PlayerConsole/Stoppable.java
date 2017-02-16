package org.caliog.Rolecraft.XMechanics.PlayerConsole;

import org.caliog.Rolecraft.Manager;

public abstract class Stoppable implements Runnable {

	private boolean stop = true;
	private int taskID;

	public boolean check() {
		if (stop) {
			Manager.cancelTask(taskID);
			return false;
		} else
			return true;

	}

	public void stop() {
		stop = false;
	}

	public int getTaskID() {
		return taskID;
	}

	public int setTaskID(final int taskID) {
		this.taskID = taskID;
		return taskID;
	}

}
