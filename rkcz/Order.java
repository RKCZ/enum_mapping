package org.rkcz;

public enum Order {
	FIRST, SECOND, THIRD;
	
	@Override
	public String toString() {
		switch (this) {
		case FIRST:
			return "First";
		case SECOND:
			return "Second";
		case THIRD:
			return "Third";
		default:
			return null;
		}
	}

}
