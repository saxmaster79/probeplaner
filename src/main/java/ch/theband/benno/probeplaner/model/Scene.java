package ch.theband.benno.probeplaner.model;

import com.google.common.collect.Lists;

import java.util.List;

public class Scene extends PartOfPlay {
	private static final long serialVersionUID = 1L;
	private final List<Page> pages = Lists.newArrayListWithExpectedSize(5);

	public Scene(int number, String name) {
		super(number, name);
	}

	@Override
	public int getNumberOfLines() {
		return pages.stream().flatMap(p->p.getLines().values().stream()).mapToInt(Integer::intValue).sum();
	}

	public List<Page> getPages() {
		return pages;
	}

	public Scene copy(){
	    return new Scene(number, getName());
    }

	@Override
	public String toString() {
		return "Scene{" +
				"pages=" + pages +
				'}';
	}
}
