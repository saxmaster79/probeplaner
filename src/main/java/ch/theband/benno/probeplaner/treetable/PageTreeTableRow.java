package ch.theband.benno.probeplaner.treetable;

import ch.theband.benno.probeplaner.model.Page;

public class PageTreeTableRow extends TreeTableRow {

	private final Page page;

	public PageTreeTableRow(Page page) {
		this.page = page;
	}

	@Override
	public String getName() {
		return "S " + page.getNumber();
	}

	public Page getPage() {
		return page;
	}


}
