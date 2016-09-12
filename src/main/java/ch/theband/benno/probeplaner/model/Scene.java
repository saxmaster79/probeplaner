package ch.theband.benno.probeplaner.model;

import java.util.List;

import com.google.common.collect.Lists;

public class Scene extends PartOfPlay {
	private static final long serialVersionUID = 1L;
	private final List<Page> pages = Lists.newArrayListWithExpectedSize(5);

	public Scene(int number, String name) {
		super(number, name);
	}

	public List<Page> getPages() {
		return pages;
	}

	public void setNumber(int number){
		this.number=number;
	}
}
