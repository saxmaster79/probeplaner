package ch.theband.benno.probeplaner.treetable;

import ch.theband.benno.probeplaner.model.Page;
import ch.theband.benno.probeplaner.model.Role;
import javafx.collections.MapChangeListener;

import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class PageTreeTableRow extends TreeTableRow {

	private final Page page;


	public PageTreeTableRow(Page page) {
		this.page = page;
        this.lines.addListener(new PageMapChangeListener(page.getLines().keySet()));

	}

    public void updateName() {
        setName("S " + page.getNumber() + " (" +
                getNumberOfLines() + ")");
    }

    public int getNumberOfLines() {
        return getLines().values().stream().mapToInt(Integer::intValue).sum();
    }


	public Page getPage() {
		return page;
	}


    private class PageMapChangeListener implements MapChangeListener<String, Integer> {
        private final Map<String, Role> name2Role;

        public PageMapChangeListener(Set<Role> roles) {
            this.name2Role = roles.stream().collect(Collectors.toMap(r -> r.getName(), r -> r));
        }

        @Override
        public void onChanged(Change<? extends String, ? extends Integer> change) {
            page.getLines().put(name2Role.get(change.getKey()), change.getValueAdded());
        }
    }
}
