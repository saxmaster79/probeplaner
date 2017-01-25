package ch.theband.benno.probeplaner.model;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

public class Rehearsal implements Serializable {

	private static final long serialVersionUID = 1L;

	private final List<PartOfPlay> what;
    private LocalDateTime startTime;
    private String remark;

    private String who;

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

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public String getWho() {
        return who;
    }

    public void setWho(String who) {
        this.who = who;
    }

}
