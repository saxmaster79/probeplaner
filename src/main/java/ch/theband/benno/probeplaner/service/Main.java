package ch.theband.benno.probeplaner.service;

import ch.theband.benno.probeplaner.model.*;
import com.google.common.collect.*;
import javafx.application.Platform;
import javafx.concurrent.Task;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.awt.*;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

public class Main {

    public static final String S = "\t";

    public static void main(String[] args) throws ExecutionException, InterruptedException {

        Platform.startup(() -> {
            final PdfFileExtractor ex = new PdfFileExtractor("C:\\Users\\benno.bennoCompi\\Dropbox\\Georgsbühne\\C1086 Alles uf Chrankeschiin_Klein.pdf", 3,
                    ImmutableSet.copyOf(Collections2.transform(Arrays.asList("Leo", "Rosmarie", "Balz", "Vögeli", "Gertrud", "Amalie",
                            "Rita", "Felix", "Franz", "Polizist", "Nurella", "Köbeli"), Role::new)));

            Task<Play> task = ex.createTask();
            task.run();
            Play play = null;
            try {
                play = task.get();
            } catch (InterruptedException | ExecutionException e) {
                throw new RuntimeException(e);
            }
            Table<Integer, String, String> table = HashBasedTable.create();
            int rowIndex = 0;
            for (Act act : play.getActs()) {
                for (Scene scene : act.getScenes()) {
                    for (Page p : scene.getPages()) {

                        table.put(rowIndex, "Akt", act.getName());
                        table.put(rowIndex, "Szene", scene.getName());
                        table.put(rowIndex, "S.", String.valueOf(p.getNumber()));
                        for (Role r : play.getRoles()) {
                            table.put(rowIndex, r.getName(), String.valueOf(p.getLines().get(r)));
                        }
                        rowIndex++;
                    }
                }
            }
            System.out.println("Wir haben Pages: " + rowIndex);

            ImmutableList.Builder<String> b = ImmutableList.builder();
            b.add("Akt", "Szene", "S.");
            b.addAll(play.getRoles().stream().map(r -> r.getName()).collect(Collectors.toList()));
            writeXLSXFile(table, b.build());
        });
        Platform.exit();
    }

    public static void writeXLSXFile(Table<Integer, String, String> table, ImmutableList<String> rowKeys) {

        final String excelFileName = "C:\\Users\\benno.bennoCompi\\Dropbox\\Georgsbühne\\pdfeinsaetze.xlsx";//name of excel file
        final String sheetName = "Einsätze";//name of sheet

        final XSSFWorkbook wb = createWorkBook(table, rowKeys, sheetName);

        writeFile(excelFileName, wb);

        if (Desktop.isDesktopSupported()) {
            try {
                Desktop.getDesktop().open(new File(excelFileName));
            } catch (IOException e) {
                System.err.println(e.getMessage());
            }
        }
    }

    private static void writeFile(String excelFileName, XSSFWorkbook wb) {
        try (FileOutputStream fileOut = new FileOutputStream(excelFileName)) {
            //write this workbook to an Outputstream.
            wb.write(fileOut);
            fileOut.flush();
            fileOut.close();
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
    }

    private static XSSFWorkbook createWorkBook(Table<Integer, String, String> table, ImmutableList<String> rowKeys, String sheetName) {

        final XSSFWorkbook wb = new XSSFWorkbook();
        final XSSFSheet sheet = wb.createSheet(sheetName);
        final String zeroString = String.valueOf(0);
        int rownum = 0;
        //iterating r number of rows
        for (String role : rowKeys) {
            Map<Integer, String> pageLines = table.columnMap().get(role);
            XSSFRow row = sheet.createRow(rownum);

            int colnum = 0;
            row.createCell(colnum++).setCellValue(role);

            String oldValue = "";
            int colSpanStart = colnum;
            int colSpanEnd = colSpanStart;
            for (Entry<Integer, String> entry : pageLines.entrySet()) {
                XSSFCell cell = row.createCell(colnum++);
                String value = entry.getValue();
                if (!zeroString.equals(value)) {
                    try {
                        cell.setCellValue(Integer.valueOf(value));
                    } catch (NumberFormatException e) {
                        cell.setCellValue(value);
                    }
                    if ("Szene".equals(role) || "Akt".equals(role)) {
                        if (oldValue.equals(value)) {
                            colSpanEnd = colnum - 1;
                        } else {
                            if (colSpanStart != colSpanEnd) {
                                sheet.addMergedRegion(new CellRangeAddress(rownum, rownum, colSpanStart, colSpanEnd));
                            }
                            colSpanStart = colnum - 1;
                            colSpanEnd = colnum - 1;
                        }
                    }

                    oldValue = value;

                }
            }
            if ("Szene".equals(role) || "Akt".equals(role)) {
                if (colSpanStart != colSpanEnd) {
                    sheet.addMergedRegion(new CellRangeAddress(rownum, rownum, colSpanStart, colSpanEnd));
                }
            }
            rownum++;
        }
        return wb;
    }
}
