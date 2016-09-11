package ch.theband.benno.probeplaner.service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.*;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import com.google.common.base.MoreObjects;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.util.PDFTextStripper;

import com.google.common.collect.Collections2;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.google.common.collect.MultimapBuilder.ListMultimapBuilder;

import ch.theband.benno.probeplaner.model.Act;
import ch.theband.benno.probeplaner.model.Play;
import ch.theband.benno.probeplaner.model.Role;
import ch.theband.benno.probeplaner.model.Scene;
import javafx.concurrent.Service;
import javafx.concurrent.Task;

public class PdfFileExtractor extends Service<Play> {
    private Set<Role> roles;
    private final String path;
    private final int pageOffset;

    public PdfFileExtractor() {
        this("C:\\Users\\benno.bennoCompi\\Dropbox\\Georgsbühne\\No Body Like Jimmy.pdf", 5,
                ImmutableSet.copyOf(Collections2.transform(Arrays.asList("Harri", "Rolf", "Sarah", "Bobi", "Nick", "Jimmy", "Manuel",  "Fabienne", "Viola", "Schöttli"), Role::new)));
    }

    public PdfFileExtractor(String path, int pageOffset, Set<Role> roles) {
        this.path = path;
        this.pageOffset = pageOffset;
        this.roles = roles;
    }



    private List<Act> convert(Multimap<Integer, Page> pages) {
        Act act = new Act(1, "1. Akt");
        int previousPage = -1;
        int sceneNumber = 0;
        Scene scene = new Scene(sceneNumber, ". Szene");
        act.getScenes().add(scene);//first scene is usually garbage...
        for (Entry<Integer, Page> entry : pages.entries()) {
            ch.theband.benno.probeplaner.model.Page page = new ch.theband.benno.probeplaner.model.Page(entry.getKey());
            page.getLines().putAll(entry.getValue().linesPerRole);
            if (previousPage == entry.getKey()) {
                sceneNumber++;
                scene = new Scene(sceneNumber, sceneNumber + ". Szene");
                act.getScenes().add(scene);
            } else {
                previousPage = entry.getKey();
            }
            scene.getPages().add(page);
        }

        return Collections.singletonList(act);
    }

    private Page getPage(int i, int j, Multimap<Integer, Page> pages) {
        Page page;
        if (pages.get(i).size() <= j) {
            page = new Page(i, j);
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
                PDDocument pdDoc = PDDocument.load(path);
                int numberOfPages = pdDoc.getNumberOfPages();
                Multimap<Integer, Page> pages = ListMultimapBuilder.treeKeys().linkedListValues().build();
                List<String> rows = new ArrayList<String>();
                // rows.add(SZENE);
                // rows.add(SEITE);
                rows.addAll(roles.stream().map(x -> x.getName()).collect(Collectors.toList()));
                int count=0;
                for (Role role : roles) {
                    String name = role.getName() + ":";
                    for (int i = pageOffset; i <= numberOfPages; i++) {
                        PDFTextStripper stripper = new PDFTextStripper();
                        stripper.setStartPage(i);
                        stripper.setEndPage(i);
                        ByteArrayOutputStream stream = new ByteArrayOutputStream();
                        try (OutputStreamWriter writer = new OutputStreamWriter(stream)) {
                            stripper.writeText(pdDoc, writer);
                            writer.flush();
                        }
                        String[] szenen = stream.toString().split("Szene:");

                        for (int j = 0; j < szenen.length; j++) {
                            String daString = szenen[j];
                            int number = 0;

                            number = countMatches(daString, name);

                            Page p = getPage(i, j, pages);
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
        private final int sceneIndex;
        private boolean changeOfScene;

        public Page(int number, int sceneIndex) {
            this.number = number;
            this.sceneIndex = sceneIndex;
        }

        @Override
        public String toString() {
            return MoreObjects.toStringHelper(this).add("number", number).toString();
        }
    }
}
