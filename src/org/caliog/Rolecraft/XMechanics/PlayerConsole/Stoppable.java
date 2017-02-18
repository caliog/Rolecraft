package org.caliog.Rolecraft.XMechanics.PlayerConsole;

import org.caliog.Rolecraft.Manager;

public abstract class Stoppable implements Runnable {

	private boolean stop = false;
	private int taskID;

	protected boolean check() {
		return !stop;
	}

	protected void stop() {
		Manager.cancelTask(taskID);
		stop = true;
	}

	public int setTaskID(final int taskID) {
		this.taskID = taskID;
		return taskID;
	}

}
