package ch.theband.benno.probeplaner;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import ch.theband.benno.probeplaner.model.*;
import ch.theband.benno.probeplaner.treetable.LineFactory;
import ch.theband.benno.probeplaner.treetable.PageTreeTableRow;
import ch.theband.benno.probeplaner.treetable.PartOfPlayTreeTableRow;
import ch.theband.benno.probeplaner.treetable.TreeTableRow;
import com.google.common.collect.ImmutableList;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.ObservableList;
import javafx.scene.control.TreeItem;
import javafx.scene.shape.Line;

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

    public boolean createScene(List<TreeItem<TreeTableRow>> items, TreeItem<TreeTableRow> root) {
        if (!onlyPages(items)) {
            return false;
        }
        if (items.isEmpty()) return false;

        assertConsecutive(items);

        TreeItem<TreeTableRow> firstPageItem = items.get(0);
        TreeItem<TreeTableRow> sceneItem = firstPageItem.getParent();

        final int startIndex = sceneItem.getChildren().indexOf(firstPageItem);
        int endIndex = sceneItem.getChildren().indexOf(items.get(items.size() - 1));
        if (startIndex < 0) throw new IllegalStateException("startIndex not found!!!");

        PartOfPlayTreeTableRow oldSceneRow = (PartOfPlayTreeTableRow) sceneItem.getValue();
        Scene oldScene = (Scene) oldSceneRow.getPart();

        TreeItem<TreeTableRow> actItem = sceneItem.getParent();
        if (startIndex == 0) {
            //new scene starting at the beginning of the scene
        } else {
            Scene newScene = oldScene.copy();
            PartOfPlayTreeTableRow newSceneRow = new PartOfPlayTreeTableRow(newScene);
            TreeItem<TreeTableRow> first = new TreeItem<TreeTableRow>(newSceneRow);
            addEventHandlers(newSceneRow, first);
            List<Page> sublist = oldScene.getPages().subList(0, startIndex);
            newScene.getPages().addAll(sublist);
            oldScene.getPages().removeAll(sublist);

            List<TreeItem<TreeTableRow>> itemSublist = ImmutableList.copyOf(sceneItem.getChildren().subList(0, startIndex));
            sceneItem.getChildren().removeAll(itemSublist);
            first.getChildren().addAll(itemSublist);
            endIndex = endIndex - itemSublist.size();

            LineFactory.sumUpChildren(first);

            List<TreeItem<TreeTableRow>> sceneNodes = actItem.getChildren();
            sceneNodes.add(sceneNodes.indexOf(sceneItem), first);
            List<Scene> scenes = ((Act) ((PartOfPlayTreeTableRow) actItem.getValue()).getPart()).getScenes();
            scenes.add(scenes.indexOf(oldScene), newScene);

        }


        if (endIndex == sceneItem.getChildren().size() - 1) {
            //new scene ending at the end of the scene
        } else {

            Scene last = oldScene.copy();
            PartOfPlayTreeTableRow newSceneRow = new PartOfPlayTreeTableRow(last);
            TreeItem<TreeTableRow> lastItem = new TreeItem<>(newSceneRow);
            addEventHandlers(newSceneRow, lastItem);
            List<Page> subList = oldScene.getPages().subList(endIndex + 1, oldScene.getPages().size());
            last.getPages().addAll(subList);
            oldScene.getPages().removeAll(subList);

            List<TreeItem<TreeTableRow>> itemSublist = ImmutableList.copyOf(sceneItem.getChildren().subList(endIndex + 1, sceneItem.getChildren().size()));
            sceneItem.getChildren().removeAll(itemSublist);
            lastItem.getChildren().addAll(itemSublist);

            LineFactory.sumUpChildren(lastItem);


            List<TreeItem<TreeTableRow>> allNodes = actItem.getChildren();
            allNodes.add(allNodes.indexOf(sceneItem) + 1, lastItem);
            List<Scene> scenes = ((Act) ((PartOfPlayTreeTableRow) actItem.getValue()).getPart()).getScenes();
            scenes.add(scenes.indexOf(oldScene) + 1, last);
        }
        LineFactory.sumUpChildren(sceneItem);
        play.getValue().correctAllSceneNumbers();


        return true;
    }


    private void addEventHandlers(PartOfPlayTreeTableRow row, TreeItem<TreeTableRow> item) {
        LineFactory.addEventHandlers(row, item);
        item.setExpanded(true);
        row.setShowValues(false);
    }

    private void assertConsecutive(List<TreeItem<TreeTableRow>> items) {
    }

    public boolean onlyPages(List<TreeItem<TreeTableRow>> items) {
        boolean otherItemsPresent = items.stream().map(TreeItem::getValue).filter(row -> (!(row instanceof PageTreeTableRow))).findAny().isPresent();
        return !otherItemsPresent;
    }

}
