package com.fmdkl.pdfreader;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.commons.lang3.StringUtils;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.interactive.annotation.PDAnnotation;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class PDFProcessor {
	private String[] headers = new String[] { "Annotation", "Pages" };

	public void readPDF(String fileName) throws IOException {
		Map<String, List<Integer>> annoPages = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
		PDDocument document = PDDocument.load(new File(fileName));
		for (int i = 0; i < document.getNumberOfPages(); i++) {
			List<PDAnnotation> annotations = document.getPage(i).getAnnotations();
			for (PDAnnotation an : annotations) {
				String value = an.getContents();
				if (StringUtils.isNoneBlank(value)) {
					List<Integer> pages = annoPages.get(value);
					if (pages == null) {
						pages = new ArrayList<>();
						annoPages.put(value, pages);
					}
					if (!pages.contains(i + 1)) {
						pages.add(i + 1);
					}
				}
			}
		}

		XSSFWorkbook workbook = new XSSFWorkbook();
		XSSFSheet sheet = workbook.createSheet("PDF Annotation");
		int colNum = 0;
		Row row = sheet.createRow(0);
		for (String header : headers) {
			Cell cell = row.createCell(colNum++);
			cell.setCellValue(header);
		}
		sheet.setColumnWidth(0, 80*256);
		sheet.setColumnWidth(1, 200*256);
		AtomicInteger rowNum = new AtomicInteger(0);
		annoPages.forEach((value, pages) -> {
			Row rowData = sheet.createRow(rowNum.incrementAndGet());
			Cell cell1 = rowData.createCell(0);
			cell1.setCellValue(value);
			Cell cell2 = rowData.createCell(1);
			cell2.setCellValue(StringUtils.join(pages, " "));
		});

		document.close();

		FileOutputStream outputStream = new FileOutputStream(fileName + ".xlsx");
		System.out.println("Writing result to "+fileName + ".xlsx ...");
		workbook.write(outputStream);
		workbook.close();
		System.out.println("Finished Writing to "+fileName + ".xlsx");

	}
}
