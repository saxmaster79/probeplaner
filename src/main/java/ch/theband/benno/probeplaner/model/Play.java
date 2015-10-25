package ch.theband.benno.probeplaner.model;

import java.io.Serializable;
import java.util.List;
import java.util.Set;

public class Play implements Serializable {
	private static final long serialVersionUID = 1L;
	private final String name;
	private final Set<Role> roles;
	private final List<Act> acts;

	public Play(String name, Set<Role> roles, List<Act> acts) {
		super();
		this.name = name;
		this.roles = roles;
		this.acts = acts;
	}

	public String getName() {
		return name;
	}

	public Set<Role> getRoles() {
		return roles;
	}

	public List<Act> getActs() {
		return acts;
	}
}
