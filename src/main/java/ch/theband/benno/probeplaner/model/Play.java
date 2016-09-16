package ch.theband.benno.probeplaner.model;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
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



    public void correctAllSceneNumbers() {
        for (Act act:acts){
            for (int i = 0; i < act.getScenes().size(); i++) {
                act.getScenes().get(i).setNumber(i+1);
                act.getScenes().get(i).setName((i+1)+". Szene");
            }
        }
    }

    private void assertConsecutive(List<Page> pages) {
	}
}
