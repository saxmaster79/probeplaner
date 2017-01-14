package ch.theband.benno.probeplaner;

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

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Predicate;
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


    public boolean createAct(TreeItem<TreeTableRow> item) {
        if (!(item.getValue() instanceof PartOfPlayTreeTableRow)) {
            return false;
        }
        PartOfPlay part = ((PartOfPlayTreeTableRow) item.getValue()).getPart();
        if (!(part instanceof Scene)) {
            return false;
        }
        Scene scene = (Scene) part;
        TreeItem<TreeTableRow> actItem = item.getParent();
        final int startIndex = actItem.getChildren().indexOf(item);
        if (startIndex < 0) throw new IllegalStateException("startIndex not found!!!");
        if (startIndex == 0) {
            return false;
        }
        PartOfPlayTreeTableRow oldActRow = (PartOfPlayTreeTableRow) actItem.getValue();
        Act oldAct = (Act) oldActRow.getPart();

        TreeItem<TreeTableRow> playItem = actItem.getParent();

        Act newAct = oldAct.copy();
        PartOfPlayTreeTableRow newActRow = new PartOfPlayTreeTableRow(newAct);
        TreeItem<TreeTableRow> newActItem = new TreeItem<TreeTableRow>(newActRow);
        addEventHandlers(newActRow, newActItem);

        List<Scene> sublist = oldAct.getScenes().subList(startIndex, oldAct.getScenes().size());
        newAct.getScenes().addAll(sublist);
        oldAct.getScenes().removeAll(sublist);

        List<TreeItem<TreeTableRow>> itemSublist = ImmutableList.copyOf(actItem.getChildren().subList(startIndex, actItem.getChildren().size()));
        actItem.getChildren().removeAll(itemSublist);
        newActItem.getChildren().addAll(itemSublist);

        LineFactory.sumUpChildren(newActItem);

        List<TreeItem<TreeTableRow>> actNodes = playItem.getChildren();
        actNodes.add(actNodes.indexOf(actItem) + 1, newActItem);
        List<Act> acts = play.getValue().getActs();
        acts.add(acts.indexOf(oldAct) + 1, newAct);


        LineFactory.sumUpChildren(playItem);
        play.getValue().correctAllSceneNumbers();
        return true;
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

    public void delete(ObservableList<TreeItem<TreeTableRow>> selectedItems) {
        treeItem:
        for (TreeItem<TreeTableRow> item : ImmutableList.copyOf(selectedItems)) {
            TreeTableRow row = item.getValue();
            if (row instanceof PartOfPlayTreeTableRow) {
                PartOfPlay part = ((PartOfPlayTreeTableRow) row).getPart();
                if (play.getValue().getActs().remove(part)) {
                    item.getParent().getChildren().remove(item);
                } else {
                    for (Act act : play.getValue().getActs()) {
                        if (act.getScenes().remove(part)) {
                            item.getParent().getChildren().remove(item);
                        } else throw new IllegalArgumentException("Unknown part " + part);
                    }
                }
            } else if (row instanceof PageTreeTableRow) {
                for (Act act : play.getValue().getActs()) {
                    for (Scene scene : act.getScenes()) {
                        if (scene.getPages().remove(((PageTreeTableRow) row).getPage())) {
                            item.getParent().getChildren().remove(item);
                            continue treeItem;
                        }
                    }
                }
            }
        }

    }

    private void addEventHandlers(PartOfPlayTreeTableRow row, TreeItem<TreeTableRow> item) {
        LineFactory.addEventHandlers(row, item);
        item.setExpanded(true);
        row.setShowValues(false);
    }

    public boolean onlyPages(List<TreeItem<TreeTableRow>> items) {
        Predicate<TreeTableRow> isPageTreeTableRow = row -> (row instanceof PageTreeTableRow);
        return items.stream().map(TreeItem::getValue).allMatch(isPageTreeTableRow);
    }

    public boolean onlyScenes(List<TreeItem<TreeTableRow>> items) {
        Predicate<TreeTableRow> isPartOfPlayTreeTableRow = row -> (row instanceof PartOfPlayTreeTableRow);
        Predicate<TreeTableRow> isPartAScene = row -> ((PartOfPlayTreeTableRow) row).getPart() instanceof Scene;
        return items.stream().map(TreeItem::getValue).allMatch(isPartOfPlayTreeTableRow.and(isPartAScene));
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
