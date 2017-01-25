package ch.theband.benno.probeplaner.model;

import java.io.Serializable;
import java.util.List;

/**
 * The root Model Object
 */
public final class ProbePlanerData implements Serializable {
    private static final long serialVersionUID = 1L;
    private final Play play;
    private final List<Rehearsal> reherarsals;

    public ProbePlanerData(Play play, List<Rehearsal> reherarsals) {
        this.play = play;
        this.reherarsals = reherarsals;
    }

    public Play getPlay() {
        return play;
    }

    public List<Rehearsal> getReherarsals() {
        return reherarsals;
    }
}
