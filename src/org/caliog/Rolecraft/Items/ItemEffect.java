package org.caliog.Rolecraft.Items;

public class ItemEffect {
	private final int power;
	private final ItemEffectType type;

	public static enum ItemEffectType {
		VIT, DEX, STR, INT, CRIT, DODGE;
	}

	public ItemEffect(ItemEffectType type, int p) {
		this.type = type;
		this.power = p;
	}

	public int getPower() {
		return this.power;
	}

	public String getString() {
		switch (this.type) {
		case DODGE:
			return "Dodge Chance: +" + this.power + "%";
		case CRIT:
			return "Critical Chance: +" + this.power + "%";
		case DEX:
			return "Dex +" + this.power;
		case INT:
			return "Int +" + this.power;
		case VIT:
			return "Vit +" + this.power;
		case STR:
			return "Str +" + this.power;
		}
		return null;
	}

	public ItemEffectType getType() {
		return this.type;
	}
}
