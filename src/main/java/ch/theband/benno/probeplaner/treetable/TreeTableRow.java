package ch.theband.benno.probeplaner.treetable;

import javafx.beans.InvalidationListener;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableMap;

import java.util.Collections;
import java.util.Map;

public abstract class TreeTableRow {
    private final SimpleStringProperty name = new SimpleStringProperty();
    protected final ObservableMap<String, Integer> lines;
    private boolean showValues = true;

	public TreeTableRow() {
		super();
        this.lines = FXCollections.observableHashMap();
        this.lines.addListener((InvalidationListener) evt -> updateName());
    }

    public abstract void updateName();


    public final String getName() {
        return name.get();
    }

    public final SimpleStringProperty nameProperty() {
        return name;
    }

    final void setName(String name) {
        this.name.set(name);
    }

	public Map<String, Integer> getLines() {
		return showValues ? lines : Collections.emptyMap();
	}

	public void setShowValues(boolean showValues) {
		this.showValues = showValues;

	}
	@Override
	public String toString() {
		return "PageTreeTableRow{" +
				getName() +
				'}';
	}

	public void setLines(Map<String,Integer> lines) {
        this.lines.clear();
        this.lines.putAll(lines);
    }
}
