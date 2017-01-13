package ch.theband.benno.probeplaner.model;

import com.google.common.collect.Maps;

import java.io.Serializable;
import java.util.Map;

public class Page implements Serializable {
	private static final long serialVersionUID = 1L;
	private final int number;
	private final Map<Role, Integer> lines = Maps.newHashMap();

	public Page(int number) {
		this.number = number;
	}

	public int getNumber() {
		return number;
	}


	public Map<Role, Integer> getLines() {
		return lines;
    }

    public Page copy() {
        Page copy = new Page(this.number);
        copy.getLines().putAll(this.getLines());
        return copy;
    }

	@Override
	public String toString() {
		return "Page{" +
				"number=" + number +
				", lines=" + lines +
				'}';
	}
}
