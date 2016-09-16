package ch.theband.benno.probeplaner.treetable;

import ch.theband.benno.probeplaner.model.PartOfPlay;

public class PartOfPlayTreeTableRow extends TreeTableRow {

	private final PartOfPlay part;

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

	public PartOfPlay getPart() {
		return part;
	}

	@Override
	public String toString() {
		return "PartOfPlayTreeTableRow{" +
				"part=" + part +
				'}';
	}
}
