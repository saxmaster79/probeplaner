package ch.theband.benno.probeplaner.treetable;

import ch.theband.benno.probeplaner.model.Page;

public class PageTreeTableRow extends TreeTableRow {

	private final Page page;


	public PageTreeTableRow(Page page) {
		this.page = page;
        //this.lines.addListener((MapChangeListener<String, Integer>) change -> page.getLines().put(change.getKey(), change.getValueAdded()));

	}

    public void updateName() {
        setName("S " + page.getNumber() + " (" +
                getNumberOfLines() + ")");
    }

    public int getNumberOfLines() {
        return getLines().values().stream().mapToInt(Integer::intValue).sum();
    }


	public Page getPage() {
		return page;
	}


}
