package ch.theband.benno.probeplaner.model;

import com.google.common.collect.Lists;

import java.util.List;

public class Act extends PartOfPlay {

	private static final long serialVersionUID = 1L;
	private final List<Scene> scenes = Lists.newArrayListWithExpectedSize(8);

	public Act(int number, String name) {
		super(number, name);
	}

	@Override
	public int getNumberOfLines() {
		return scenes.stream().mapToInt(scene -> scene.getNumberOfLines()).sum();
	}

	public List<Scene> getScenes() {
		return scenes;
	}

	public Act copy() {
		return new Act(number++, getName());
	}
}
