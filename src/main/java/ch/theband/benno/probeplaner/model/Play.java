package ch.theband.benno.probeplaner.model;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Ordering;

import java.io.Serializable;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

public class Play implements Serializable {
	private static final long serialVersionUID = 1L;
	private final String name;
	private final Set<Role> roles;
	private final List<Act> acts;

	public Play(String name, ImmutableSet<Role> roles, List<Act> acts) {
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

	public void correctAllActNumbers() {
		for (int i = 0; i < acts.size(); i++) {
			acts.get(i).setNumber(i + 1);
			acts.get(i).setName((i + 1) + ". Akt");
		}
		correctAllSceneNumbers();
	}

    public Comparator<Role> rolesComparator(){
    	return Ordering.explicit(ImmutableList.copyOf(roles));
	}


}
