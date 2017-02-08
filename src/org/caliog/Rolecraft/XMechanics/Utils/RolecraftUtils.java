package org.caliog.Rolecraft.XMechanics.Utils;

import org.bukkit.Material;
import org.caliog.Rolecraft.XMechanics.Commands.Utils.CommandField;

public class RolecraftUtils {

	public static int[] addElementToArray(int[] array, int element) {
		if (array != null) {
			int[] newarray = new int[array.length + 1];
			for (int i = 0; i < array.length; i++) {
				newarray[i] = array[i];
			}

			newarray[array.length] = element;
			return newarray;
		} else {
			int[] newarray = { element };
			return newarray;
		}
	}

	public static boolean isNotNegativeInteger(String string) {
		if (isInteger(string))
			if (Integer.parseInt(string) >= 0)
				return true;
		return false;
	}

	public static boolean isPositiveInteger(String string) {
		if (isInteger(string))
			if (Integer.parseInt(string) > 0)
				return true;
		return false;
	}

	public static boolean isInteger(String string) {
		if (string == null)
			return false;
		try {
			Integer.parseInt(string);
		} catch (NumberFormatException nfe) {
			return false;
		}
		return true;
	}

	public static CommandField[] addElementToArray(CommandField[] a, CommandField e) {
		if (a == null) {
			CommandField[] array = { e };
			return array;
		} else {
			CommandField[] array = new CommandField[a.length + 1];
			System.arraycopy(a, 0, array, 0, a.length);
			array[array.length - 1] = e;
			return array;
		}
	}

	public static String[] removeNull(String[] a) {
		int counter = 0;
		for (int i = 0; i < a.length; i++)
			if (a[i] == null)
				counter++;
		String b[] = new String[a.length - counter];
		int j = 0;
		for (int i = 0; i < a.length; i++) {
			if (a[i] != null) {
				b[j] = a[i];
				j++;
			}
		}

		return b;
	}

	public static String readable(Material mat) {
		String[] split = mat.name().split("_");
		String name = "";
		for (String s : split)
			name += s.substring(0, 1) + s.substring(1).toLowerCase() + " ";
		return name.trim();

	}
}
