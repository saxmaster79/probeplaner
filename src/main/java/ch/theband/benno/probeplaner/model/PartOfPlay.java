package ch.theband.benno.probeplaner.model;

import java.io.Serializable;

public abstract class PartOfPlay implements Serializable {

	private static final long serialVersionUID = 1L;
	protected int number;
	private String name;

	public PartOfPlay(int number, String name) {
		super();
		this.number = number;
		this.name = name;
	}

	public int getNumber() {
		return number;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public abstract int getNumberOfLines();
}
