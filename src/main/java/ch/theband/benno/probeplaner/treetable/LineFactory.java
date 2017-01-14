package ch.theband.benno.probeplaner.treetable;

import ch.theband.benno.probeplaner.model.*;
import com.google.common.collect.Collections2;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.scene.control.TreeItem;

import java.util.Collection;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class LineFactory extends Service<TreeItem<TreeTableRow>> {

	private final Play play;

	public LineFactory(Play play) {
		this.play = play;
	}

	public Collection<String> getHeaders() {
		return Collections2.transform(play.getRoles(), Role::getName);
	}

	public TreeItem<TreeTableRow> createLines() {
		try {
			TreeTableRow value = new PlayTreeTableRow();
			final TreeItem<TreeTableRow> root = new TreeItem<>(value);
			play.getActs().stream().map(act -> toTreeItem(act)).forEach(root.getChildren()::add);
			addNonLeafStuff(value, root);
			return root;
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}

	private TreeItem<TreeTableRow> toTreeItem(Act act) {
		TreeTableRow tr = new PartOfPlayTreeTableRow(act);
		final TreeItem<TreeTableRow> treeItem = new TreeItem<>(tr);
		act.getScenes().stream().map(s -> toTreeItem(s)).forEach(treeItem.getChildren()::add);
		addNonLeafStuff(tr, treeItem);
		return treeItem;
	}

	private TreeItem<TreeTableRow> toTreeItem(Scene scene) {
		TreeTableRow value = new PartOfPlayTreeTableRow(scene);
		TreeItem<TreeTableRow> treeItem = new TreeItem<>(value);
		scene.getPages().stream().map(p -> toTreeItem(p)).forEach(treeItem.getChildren()::add);

		addNonLeafStuff(value, treeItem);
		return treeItem;
	}

	private void addNonLeafStuff(TreeTableRow value, TreeItem<TreeTableRow> treeItem) {
		sumUpChildren(treeItem);
		addEventHandlers(value, treeItem);
	}

	public static void sumUpChildren(TreeItem<TreeTableRow> treeItem) {
		Stream<Entry<String, Integer>> entries = treeItem.getChildren().stream().flatMap(item -> item.getValue().getLines().entrySet().stream());
		Map<String, Integer> map = entries.collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (t, u) -> t + u));

		TreeTableRow treeTableRow = treeItem.getValue();
		treeTableRow.setLines(map);
	}

	public static void addEventHandlers(TreeTableRow value, TreeItem<TreeTableRow> treeItem) {
		treeItem.addEventHandler(TreeItem.branchExpandedEvent(), evt -> {
			if (treeItem.equals(evt.getSource())) {
				value.setShowValues(false);
			}
		});
		treeItem.addEventHandler(TreeItem.branchCollapsedEvent(), evt -> {
			if (treeItem.equals(evt.getSource())) {
				value.setShowValues(true);
			}
		});
		value.setShowValues(true);
	}

	public static TreeItem<TreeTableRow> toTreeItem(Page page) {
		TreeTableRow row = new PageTreeTableRow(page);
		final TreeItem<TreeTableRow> item = new TreeItem<TreeTableRow>(row);

		Map<Role, Integer> original = page.getLines();

		row.getLines().putAll(original.entrySet().stream().collect(Collectors.toMap(e -> e.getKey().getName(), Map.Entry::getValue)));
		return item;
	}

	@Override
	protected Task<TreeItem<TreeTableRow>> createTask() {
		return new Task<TreeItem<TreeTableRow>>() {

			@Override
			protected TreeItem<TreeTableRow> call() throws Exception {
				return createLines();
			}
		};
	}

	public final class PlayTreeTableRow extends TreeTableRow {
		@Override
		public void updateName() {
			setName(play.getName());
		}
	}
}
