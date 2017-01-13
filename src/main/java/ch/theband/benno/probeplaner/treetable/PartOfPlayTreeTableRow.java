package ch.theband.benno.probeplaner.treetable;

import ch.theband.benno.probeplaner.model.PartOfPlay;

public class PartOfPlayTreeTableRow extends TreeTableRow {

    private final PartOfPlay part;

    public PartOfPlayTreeTableRow(PartOfPlay part) {
        this.part = part;
        updateName();
    }

    @Override
    protected void updateName() {
        setName(part.getName() + " (" + lines.values().stream().mapToInt(Integer::intValue).sum() +
                ")");
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
