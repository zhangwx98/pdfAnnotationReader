package com.fmdkl.pdfreader;

import java.io.IOException;

public class PdfReaderApplication {

	public static void main(String[] args) throws IOException {
		if (args.length > 0) {
			PDFProcessor processor = new PDFProcessor();
			for (String pdfFile : args) {
				System.out.println("Processing the file: " + pdfFile);
				processor.readPDF(pdfFile);
			}
		} else {
			System.out.println("Please input the PDF file paths to be proccessed");
		}
	}
}
