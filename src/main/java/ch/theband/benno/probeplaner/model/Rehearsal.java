package ch.theband.benno.probeplaner.model;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

public class Rehearsal implements Serializable {

	private static final long serialVersionUID = 1L;

	private final LocalDateTime startTime;
	private final List<PartOfPlay> what;
    private final String remark;

	public Rehearsal(LocalDateTime startTime, List<PartOfPlay> what, String remark) {
		this.startTime = startTime;
		this.what = what;
		this.remark = remark;
	}

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public List<PartOfPlay> getWhat() {
        return what;
    }

    public String getRemark() {
        return remark;
    }
}
