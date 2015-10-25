package ch.theband.benno.probeplaner.treetable;

import ch.theband.benno.probeplaner.model.PartOfPlay;

public class PartOfPlayTreeTableRow extends TreeTableRow {

	private PartOfPlay part;

	public PartOfPlayTreeTableRow(PartOfPlay part) {
		this.part = part;
	}

	@Override
	public String getName() {
		return part.getName();
	}

	public void setName(String name) {
		part.setName(name);
	}

}
