package org.caliog.Rolecraft.XMechanics.Utils;

import org.caliog.Rolecraft.Manager;

public class VectorFormatException extends Exception {

	private static final long serialVersionUID = -2845147657208755888L;

	@Override
	public void printStackTrace() {
		Manager.plugin.getLogger().warning("Could not load a vector in one of your files!");
		Manager.plugin.getLogger().warning("If this happens again, contact the Rolecraft support.");
	}

	public void questError(String f) {
		Manager.plugin.getLogger().warning("Could not load a quest in file " + f + "!");
		Manager.plugin.getLogger().warning("Caused by VectorFormatException!");
	}
}
