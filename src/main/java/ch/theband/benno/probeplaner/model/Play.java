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

	public void createScene(List <Page> pages){
		if(pages.isEmpty())return;
		assertConsecutive(pages);

		Act actForNewScene=null;
		for (Act act:acts){
            List<Scene> newScenes = new ArrayList<Scene>();
			for (Scene scene: ImmutableList.copyOf(act.getScenes())){
				final int startIndex=scene.getPages().indexOf(pages.get(0));
				if(startIndex<0) continue;

				if(startIndex==0) {
					//new scene starting at the beginning of the scene
				}else{
                    Scene first = new Scene(scene.getNumber(), scene.getName());
                    List<Page> sublist = scene.getPages().subList(0, startIndex);
                    first.getPages().addAll(sublist);
                    scene.getPages().removeAll(sublist);
                    newScenes.add(first);
                    act.getScenes().add(act.getScenes().indexOf(scene), first);
				}
                final int endIndex=scene.getPages().indexOf(pages.get(pages.size()-1));

				if(endIndex==scene.getPages().size()-1) {
                    //new scene ending at the end of the scene
                }else{
                    Scene last = new Scene(scene.getNumber(), scene.getName());
                    List<Page> subList = scene.getPages().subList(endIndex + 1, scene.getPages().size());
                    last.getPages().addAll(subList);
                    scene.getPages().removeAll(subList);
                    newScenes.add(last);
                    act.getScenes().add(act.getScenes().indexOf(scene)+1, last);
                }

			}
		}
		correctAllSceneNumbers();
	}

    private void correctAllSceneNumbers() {
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
