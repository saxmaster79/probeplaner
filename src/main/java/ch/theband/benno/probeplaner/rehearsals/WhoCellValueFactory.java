package ch.theband.benno.probeplaner.rehearsals;

import ch.theband.benno.probeplaner.model.*;
import com.google.common.collect.Ordering;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.TableColumn;
import javafx.util.Callback;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class WhoCellValueFactory implements Callback<TableColumn.CellDataFeatures<Rehearsal, String>, ObservableValue<String>> {

    private Comparator<? super Role> comparator = Ordering.usingToString();

    @Override
    public ObservableValue<String> call(TableColumn.CellDataFeatures<Rehearsal, String> param) {
        List<PartOfPlay> what = param.getValue().getWhat();
        final String value = what.stream().flatMap(this::getRoles).distinct().sorted(comparator).map(Role::getName).collect(Collectors.joining(", "));

        return new ReadOnlyStringWrapper(value);
    }

    private Stream<Role> getRoles(PartOfPlay partOfPlay) {
        if (partOfPlay instanceof Scene) {
            return getRoles((Scene) partOfPlay);
        }
        if (partOfPlay instanceof Act) {
            return getRoles((Act) partOfPlay);
        }
        return Stream.empty();
    }

    private Stream<Role> getRoles(Scene scene) {
        return scene.getPages().stream().flatMap(p -> getRoles(p).stream());
    }

    private Stream<Role> getRoles(Act act) {
        return act.getScenes().stream().flatMap(this::getRoles);
    }

    private Collection<Role> getRoles(Page page) {
        return page.getLines().keySet();
    }

    public void setComparator(Comparator<? super Role> comparator) {
        this.comparator = comparator;
    }
}
