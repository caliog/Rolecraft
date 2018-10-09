package org.caliog.Rolecraft.XMechanics.Commands.Utils;

public class CommandField {
	public enum FieldProperty {
		OPTIONAL, REQUIRED, IDENTIFIER;
	}

	private FieldProperty property;
	private String name;
	private String type;

	public CommandField(String name, FieldProperty property) {
		this(name, "string", property);
	}

	public CommandField(String name, String type, FieldProperty property) {
		this.setProperty(property);
		this.setName(name);
		this.setType(type);
	}

	public FieldProperty getProperty() {
		return property;
	}

	public void setProperty(FieldProperty property) {
		this.property = property;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public boolean isRequired() {
		if (this.property.equals(FieldProperty.IDENTIFIER) || this.property.equals(FieldProperty.REQUIRED))
			return true;
		else
			return false;
	}

	public boolean isOptional() {
		return !isRequired();
	}

	public boolean isIdentifier() {
		if (this.property.equals(FieldProperty.IDENTIFIER))
			return true;
		else
			return false;
	}
}
