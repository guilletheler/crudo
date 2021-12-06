package com.gt.crudo;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.logging.Level;
import java.util.logging.Logger;

public class CrudoFileWriter {

	public static void writeAll(CrudCreator crudCreator, String pagesSubFolder, boolean replace) {
		writeRepo(crudCreator, replace);
		writeService(crudCreator, replace);
		writeConverter(crudCreator, replace);
		writeListController(crudCreator, replace);
		writeEditController(crudCreator, replace);
		writeListPage(crudCreator, pagesSubFolder, replace);
		writeEditPage(crudCreator, pagesSubFolder, replace);
		crudCreator.generateMenuDefinition(System.out, pagesSubFolder);
		crudCreator.generateEnumsMB(System.out);
	}

	public static void writeRepo(CrudCreator crudCreator, boolean replace) {
		writeFile(new CrudoGenerator() {

			@Override
			public void write(PrintStream printStream) {
				crudCreator.generateRepo(printStream);
			}

			@Override
			public String getFileName() {
				return crudCreator.getRepoFileName();
			}
		}, replace);
	}

	public static void writeService(CrudCreator crudCreator, boolean replace) {
		writeFile(new CrudoGenerator() {

			@Override
			public void write(PrintStream printStream) {
				crudCreator.generateService(printStream);
			}

			@Override
			public String getFileName() {
				return crudCreator.getServiceFileName();
			}
		}, replace);
	}

	public static void writeConverter(CrudCreator crudCreator, boolean replace) {
		writeFile(new CrudoGenerator() {

			@Override
			public void write(PrintStream printStream) {
				crudCreator.generateConverter(printStream);
			}

			@Override
			public String getFileName() {
				return crudCreator.getConverterFileName();
			}
		}, replace);
	}

	public static void writeListController(CrudCreator crudCreator, boolean replace) {
		writeFile(new CrudoGenerator() {

			@Override
			public void write(PrintStream printStream) {
				crudCreator.generateListController(printStream);
			}

			@Override
			public String getFileName() {
				return crudCreator.getListControllerFileName();
			}
		}, replace);
	}

	public static void writeEditController(CrudCreator crudCreator, boolean replace) {
		writeFile(new CrudoGenerator() {

			@Override
			public void write(PrintStream printStream) {
				crudCreator.generateEditController(printStream);
			}

			@Override
			public String getFileName() {
				return crudCreator.getEditControllerFileName();
			}
		}, replace);
	}

	public static void writeListPage(CrudCreator crudCreator, String subFolder, boolean replace) {
		writeFile(new CrudoGenerator() {

			@Override
			public void write(PrintStream printStream) {
				crudCreator.generateListPage(printStream);
			}

			@Override
			public String getFileName() {
				return crudCreator.getListPageFileName(subFolder);
			}
		}, replace);
	}

	public static void writeEditPage(CrudCreator crudCreator, String subFolder, boolean replace) {
		writeFile(new CrudoGenerator() {

			@Override
			public void write(PrintStream printStream) {
				crudCreator.generateEditPage(printStream);
			}

			@Override
			public String getFileName() {
				return crudCreator.getEditPageFileName(subFolder);
			}
		}, replace);
	}

	public static void writeFile(CrudoGenerator csw, boolean replace) {

		File file = new File(csw.getFileName());

		File folder = file.getParentFile();

		folder.mkdirs();

		if (file.exists()) {
			if (!replace) {
				return;
			}
			file.delete();
		}

		PrintStream out;
		try {
			out = new PrintStream(file);
			csw.write(out);
		} catch (FileNotFoundException e) {
			Logger.getLogger(CrudoFileWriter.class.getName()).log(Level.SEVERE, "Error al escribir archivo", e);
		}

	}

}
