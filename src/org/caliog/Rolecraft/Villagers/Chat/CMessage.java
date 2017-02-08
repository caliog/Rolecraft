package org.caliog.Rolecraft.Villagers.Chat;

import org.caliog.Rolecraft.Entities.Player.RolecraftPlayer;
import org.caliog.Rolecraft.Villagers.NPC.Villager;

public class CMessage {

	public enum MessageType {
		TEXT, QUESTION, END;
	}

	private final int target;
	private final String message;
	private final MessageType type;
	private ChatTask task = null;

	public CMessage(String message) {
		this(message, MessageType.TEXT, 0);
	}

	public CMessage(String message, MessageType type) {
		this(message, type, 0);
	}

	public CMessage(String message, MessageType type, int target) {
		this.message = message;
		this.type = type;
		this.target = target;
	}

	public int getTarget() {
		return target;
	}

	public String getMessage() {
		return message;
	}

	public MessageType getType() {
		return type;
	}

	public String toString() {
		return message + "#" + type.name() + "#" + target;
	}

	public static CMessage fromString(String text, int id) {
		if (text.contains("#") && text.split("#").length == 3) {
			return new CMessage(text.split("#")[0], MessageType.valueOf(text.split("#")[1]), Integer.parseInt(text.split("#")[2]));
		} else if (text.contains("#") && text.split("#").length == 2)
			return new CMessage(text.split("#")[0], MessageType.valueOf(text.split("#")[1]), id + 1);
		return null;
	}

	public void execute(RolecraftPlayer player, Villager villager) {
		if (task == null || player == null || villager == null) {
			return;
		} else
			task.execute(player, villager);
	}

	public void setTask(ChatTask task) {
		this.task = task;
	}

	public long getTime() {
		long time = 40L;
		time += 3 * (getMessage().length() - getMessage().replaceAll(" ", "").length());
		return time;
	}
}
