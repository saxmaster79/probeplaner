package ch.theband.benno.probeplaner;

import ch.theband.benno.probeplaner.model.*;
import ch.theband.benno.probeplaner.treetable.LineFactory;
import ch.theband.benno.probeplaner.treetable.PageTreeTableRow;
import ch.theband.benno.probeplaner.treetable.PartOfPlayTreeTableRow;
import ch.theband.benno.probeplaner.treetable.TreeTableRow;
import com.google.common.collect.ImmutableList;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.control.TreeItem;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

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

    public boolean createScene(TreeItem<TreeTableRow> item) {
        if (!onlyPages(Collections.singletonList(item))) {
            return false;
        }

        TreeItem<TreeTableRow> sceneItem = item.getParent();

        final int startIndex = sceneItem.getChildren().indexOf(item);
        if (startIndex < 0) throw new IllegalStateException("startIndex not found!!!");
        if (startIndex == 0) {
            return false;
        }
        PartOfPlayTreeTableRow oldSceneRow = (PartOfPlayTreeTableRow) sceneItem.getValue();
        Scene oldScene = (Scene) oldSceneRow.getPart();

        TreeItem<TreeTableRow> actItem = sceneItem.getParent();

        Scene newScene = oldScene.copy();
        PartOfPlayTreeTableRow newSceneRow = new PartOfPlayTreeTableRow(newScene);
        TreeItem<TreeTableRow> newSceneItem = new TreeItem<TreeTableRow>(newSceneRow);
        addEventHandlers(newSceneRow, newSceneItem);
        //Erste Seite wird kopiert
        Page pageCopy = oldScene.getPages().get(startIndex).copy();
        newScene.getPages().add(pageCopy);

        List<Page> sublist = oldScene.getPages().subList(startIndex + 1, oldScene.getPages().size());
        newScene.getPages().addAll(sublist);
        oldScene.getPages().removeAll(sublist);

        List<TreeItem<TreeTableRow>> itemSublist = ImmutableList.copyOf(sceneItem.getChildren().subList(startIndex + 1, sceneItem.getChildren().size()));
        sceneItem.getChildren().removeAll(itemSublist);
        newSceneItem.getChildren().add(LineFactory.toTreeItem(pageCopy));
        newSceneItem.getChildren().addAll(itemSublist);

        LineFactory.sumUpChildren(newSceneItem);

        List<TreeItem<TreeTableRow>> sceneNodes = actItem.getChildren();
        sceneNodes.add(sceneNodes.indexOf(sceneItem) + 1, newSceneItem);
        List<Scene> scenes = ((Act) ((PartOfPlayTreeTableRow) actItem.getValue()).getPart()).getScenes();
        scenes.add(scenes.indexOf(oldScene) + 1, newScene);


        LineFactory.sumUpChildren(sceneItem);
        play.getValue().correctAllSceneNumbers();

        return true;
    }


    private void addEventHandlers(PartOfPlayTreeTableRow row, TreeItem<TreeTableRow> item) {
        LineFactory.addEventHandlers(row, item);
        item.setExpanded(true);
        row.setShowValues(false);
    }

    public boolean onlyPages(List<TreeItem<TreeTableRow>> items) {
        boolean otherItemsPresent = items.stream().map(TreeItem::getValue).filter(row -> (!(row instanceof PageTreeTableRow))).findAny().isPresent();
        return !otherItemsPresent;
    }

    public List<PartOfPlay> getScenes(List<TreeItem<TreeTableRow>> selectedItems) {
        List<TreeTableRow> rows = selectedItems.stream().map(item -> item.getValue()).collect(Collectors.toList());
        List<PartOfPlay> parts = new ArrayList<>();
        rows.stream().forEach(e -> e.toString());
        for (TreeTableRow row : rows) {
            if (row instanceof PageTreeTableRow) {
                System.err.println("Not yet implemented:::: PageTreeTableRow");
            } else if (row instanceof PartOfPlayTreeTableRow) {
                parts.add(((PartOfPlayTreeTableRow) row).getPart());
            }
        }
        return parts;
    }

    public void sumUpParents(TreeItem<TreeTableRow> value) {
        TreeItem<TreeTableRow> item = value;
        while (item.getParent() != null) {
            item = item.getParent();
            LineFactory.sumUpChildren(item);
        }
    }
}
