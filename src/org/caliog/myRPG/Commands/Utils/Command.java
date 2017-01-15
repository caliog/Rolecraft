package org.caliog.myRPG.Commands.Utils;

import org.bukkit.entity.Player;
import org.caliog.myRPG.Utils.myUtils;

public class Command {

	private int min, max;
	private String cmd;
	private CommandField[] fields;
	private String permission;
	private CommandExecutable exe;

	private Command(String cmd, String permission, CommandField... fields) {
		this.fields = fields;
		this.min = calcMin(fields);
		this.max = calcMax(fields);
		this.cmd = cmd;
		if (permission == null)
			this.permission = null;
		else {
			this.permission = new String(permission);
			Permissions.add(this.permission);
		}
	}

	private int calcMax(CommandField[] fields2) {
		if (fields2 == null)
			return 0;
		return fields2.length;
	}

	public Command(String name, String permission, CommandExecutable executor, int max, CommandField... fields) {
		this(name, permission, executor, fields);
		this.setMax(max);
	}

	public Command(String name, String permission, CommandExecutable executor, CommandField... fields) {
		this(name, permission, fields);
		this.setExe(executor);

	}

	private int calcMin(CommandField[] fields2) {
		int r = 0;
		if (fields2 == null)
			return 0;
		for (CommandField field : fields2) {
			if (field.isRequired()) {
				r++;
			}
		}
		return r;
	}

	public CommandField[] getIdentifiers() {
		CommandField[] ident = null;
		for (CommandField field : fields)
			if (!field.isIdentifier())
				continue;
			else
				ident = (CommandField[]) myUtils.addElementToArray(ident, field);

		return ident;
	}

	public void setMax(int max) {
		this.max = max;
	}

	public int getMax() {
		return max;
	}

	public int getMin() {
		return min;
	}

	public void setMin(int min) {
		this.min = min;
	}

	public CommandField[] getFields() {
		return fields;
	}

	public void setFields(CommandField[] fields) {
		this.fields = fields;
	}

	public String getPermission() {
		return permission;
	}

	public void setPermission(String permission) {
		this.permission = permission;
	}

	public String getName() {
		return cmd;
	}

	public void setName(String name) {
		this.cmd = name;
	}

	public CommandExecutable getExe() {
		return exe;
	}

	public void setExe(CommandExecutable exe) {
		this.exe = exe;
	}

	public String getUsage() {
		String fields = "";
		for (CommandField field : this.fields)
			if (field.isIdentifier())
				fields += field.getName() + " ";
			else if (field.isRequired())
				fields += "<" + field.getName() + "> ";
			else if (field.isOptional())
				fields += "[" + field.getName() + "] ";

		return "/" + this.cmd + " " + fields.trim();
	}

	public void execute(String[] args, Player player) {
		getExe().execute(args, player);

	}
}
