package ch.theband.benno.probeplaner.service;

import ch.theband.benno.probeplaner.model.Act;
import ch.theband.benno.probeplaner.model.Play;
import ch.theband.benno.probeplaner.model.Role;
import ch.theband.benno.probeplaner.model.Scene;
import com.google.common.base.MoreObjects;
import com.google.common.collect.*;
import com.google.common.collect.MultimapBuilder.ListMultimapBuilder;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

public class PdfFileExtractor extends Service<Play> {
    private ImmutableSet<Role> roles;
    private final String path;
    private final int pageOffset;

    public PdfFileExtractor(String path, int pageOffset, ImmutableSet<Role> roles) {
        this.path = path;
        this.pageOffset = pageOffset;
        this.roles = roles;
    }


    private List<Act> convert(Multimap<Integer, Page> pages) {
        int actNumber = 1;
        Act act = new Act(actNumber, actNumber + ". Akt");
        List<Act> acts = Lists.newArrayList(act);
        int previousPage = -1;
        int sceneNumber = 0;//first scene is usually garbage...
        Scene scene = new Scene(sceneNumber, sceneNumber + ". Szene");
        for (Entry<Integer, Page> entry : pages.entries()) {
            ch.theband.benno.probeplaner.model.Page page = new ch.theband.benno.probeplaner.model.Page(entry.getKey());
            final Page internalPage = entry.getValue();
            if (internalPage.newAct) {
                act = new Act(++actNumber, actNumber + ". Akt");
                acts.add(act);
            }
            page.getLines().putAll(internalPage.linesPerRole);
            if (previousPage == entry.getKey() || internalPage.newAct) {
                if (internalPage.newAct) {
                    sceneNumber = 1;
                } else {
                    sceneNumber++;
                }
                scene = new Scene(sceneNumber, sceneNumber + ". Szene");
                act.getScenes().add(scene);
            } else {
                previousPage = entry.getKey();
            }
            scene.getPages().add(page);
        }

        return acts;
    }

    private Page getPage(int i, int j, Multimap<Integer, Page> pages, boolean newAct) {
        Page page;
        if (pages.get(i).size() <= j) {
            page = new Page(i, newAct);
            pages.put(i, page);
        } else {
            page = Iterables.get(pages.get(i), j);
        }
        return page;
    }

    private int countMatches(String string, String findStr) {
        return string.split(findStr, -1).length - 1;
    }

    @Override
    protected Task<Play> createTask() {
        return new Task<Play>() {

            @Override
            protected Play call() throws Exception {
                Multimap<Integer, Page> pages = processDoc();
                List<Act> acts = convert(pages);
                return new Play("No Body Like Jimmy", roles, acts);
            }

            Multimap<Integer, Page> processDoc() throws IOException {
                System.out.println(path);
                // Reads in pdf document
                PDDocument pdDoc = PDDocument.load(new File(path));
                int numberOfPages = pdDoc.getNumberOfPages();
                Multimap<Integer, Page> pages = ListMultimapBuilder.treeKeys().linkedListValues().build();
                List<String> rows = new ArrayList<String>();

                rows.addAll(roles.stream().map(x -> x.getName()).collect(Collectors.toList()));
                int count = 0;
                for (Role role : roles) {
                    String name = role.getName() + ":";
                    for (int pageNumber = pageOffset; pageNumber <= numberOfPages; pageNumber++) {
                        PDFTextStripper stripper = new PDFTextStripper();
                        stripper.setStartPage(pageNumber);
                        stripper.setEndPage(pageNumber);
                        ByteArrayOutputStream stream = new ByteArrayOutputStream();
                        try (OutputStreamWriter writer = new OutputStreamWriter(stream)) {
                            stripper.writeText(pdDoc, writer);
                            writer.flush();
                        }
                        final String pageContent = stream.toString();
                        String[] szenen = pageContent.split("Szene:");

                        for (int sceneIndex = 0; sceneIndex < szenen.length; sceneIndex++) {
                            String daString = szenen[sceneIndex];
                            int number = 0;

                            number = countMatches(daString, name);

                            Page p = getPage(pageNumber, sceneIndex, pages, daString.contains(". AKT"));
                            p.linesPerRole.put(role, number);
                        }
                    }
                    updateProgress(count++, roles.size());
                    System.out.println("Finished Role " + role.getName() + ", ");
                }
                System.out.println("Pages" + pages);
                return pages;
            }
        };
    }

    private static final class Page {
        private final Map<Role, Integer> linesPerRole = Maps.newHashMap();
        private final int number;
        private final boolean newAct;

        public Page(int number, boolean newAct) {
            this.number = number;
            this.newAct = newAct;
        }

        @Override
        public String toString() {
            return MoreObjects.toStringHelper(this).add("number", number).toString();
        }
    }
}
