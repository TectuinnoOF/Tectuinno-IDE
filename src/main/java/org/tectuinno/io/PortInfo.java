package org.tectuinno.io;

public record PortInfo(String systemName, String descriptiveName) {
	@Override
	public String toString() {
		return descriptiveName != null && !descriptiveName.isBlank()
                ? descriptiveName + " (" + systemName + ")"
                : systemName;
	}
}
