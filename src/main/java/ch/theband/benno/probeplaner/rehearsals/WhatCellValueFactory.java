package ch.theband.benno.probeplaner.rehearsals;

import ch.theband.benno.probeplaner.model.PartOfPlay;
import ch.theband.benno.probeplaner.model.Rehearsal;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.TableColumn;
import javafx.util.Callback;

import java.util.List;
import java.util.stream.Collectors;

public class WhatCellValueFactory implements Callback<TableColumn.CellDataFeatures<Rehearsal, String>, ObservableValue<String>> {
    @Override
    public ObservableValue<String> call(TableColumn.CellDataFeatures<Rehearsal, String> param) {
        List<PartOfPlay> what = param.getValue().getWhat();
        String result = what.stream().map(pop -> pop.getName()).collect(Collectors.joining(", "));
        return new ReadOnlyStringWrapper(result);
    }
}