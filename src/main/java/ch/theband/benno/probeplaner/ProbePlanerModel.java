package ch.theband.benno.probeplaner;

import java.io.File;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import ch.theband.benno.probeplaner.model.*;
import ch.theband.benno.probeplaner.treetable.PageTreeTableRow;
import ch.theband.benno.probeplaner.treetable.TreeTableRow;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.control.TreeItem;

public class ProbePlanerModel {
    private final SimpleObjectProperty<Play> play = new SimpleObjectProperty<>();
    private final SimpleObjectProperty<File> savedFile = new SimpleObjectProperty<>();

    public Play getPlay() {
        return play.get();
    }

    public void setPlay(Play play) {
        this.play.set(play);
    }

    public ObjectProperty<Play> playProperty() {
        return play;
    }

    public File getSavedFile() {
        return savedFile.get();
    }

    public void setSavedFile(File savedFile) {
        this.savedFile.set(savedFile);
    }

    public ObjectProperty<File> savedFileProperty() {
        return savedFile;
    }

    public boolean createScene(List<TreeItem<TreeTableRow>> items) {
        if (items.stream().map(TreeItem::getValue).filter(row -> (!(row instanceof PageTreeTableRow))).findAny().isPresent()) {
            return false;
        }

        List<Page> pages = items.stream().map(TreeItem::getValue).map(row -> (PageTreeTableRow) row).map(pttr -> pttr.getPage()).collect(Collectors.toList());
        play.getValue().createScene(pages);

        return true;
    }

}
