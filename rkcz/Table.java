package org.rkcz;

public class Table {
	
	private Order industry;
	
	private Abeceda abeceda;

	public Order getIndustry() {
		return industry;
	}
	
	public Abeceda getType() {
		return abeceda;
	}

	public Table(Order industry, Abeceda abeceda) {
		super();
		this.industry = industry;
		this.abeceda = abeceda;
	}
}
