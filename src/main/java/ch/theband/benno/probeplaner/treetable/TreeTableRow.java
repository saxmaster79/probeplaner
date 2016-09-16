package ch.theband.benno.probeplaner.treetable;

import java.util.Collections;
import java.util.Map;

import com.google.common.collect.Maps;

public abstract class TreeTableRow {

	private Map<String, Integer> lines;
	private boolean showValues = true;

	public TreeTableRow() {
		super();
		this.lines = Maps.newHashMap();
	}

	public abstract String getName();

	public void

	setName(String name) {
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
		this.lines = lines;
	}
}
