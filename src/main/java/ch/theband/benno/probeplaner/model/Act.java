package ch.theband.benno.probeplaner.model;

import java.util.List;

import com.google.common.collect.Lists;

public class Act extends PartOfPlay {

	private static final long serialVersionUID = 1L;
	private final List<Scene> scenes = Lists.newArrayListWithExpectedSize(8);

	public Act(int number, String name) {
		super(number, name);
	}

	public List<Scene> getScenes() {
		return scenes;
	}
}
