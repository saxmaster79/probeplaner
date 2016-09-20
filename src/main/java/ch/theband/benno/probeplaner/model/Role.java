package ch.theband.benno.probeplaner.model;

import java.io.Serializable;

public class Role implements Serializable {
	private static final long serialVersionUID = 1L;
	private final String name;
	private final String actor;

	public Role(String name, String actor) {
		this.name = name;
		this.actor = actor;
	}

	public Role(String name) {
		this(name, "");
	}

	public String getName() {
		return name;
	}

	public String getActor() {
		return actor;
	}

    @Override
    public String toString() {
        return name;
    }
}
