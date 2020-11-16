package com.gt.crudo;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;

public class CrudoFileWriter {

	public static void writeAll(CrudCreator crudCreator, String pagesSubFolder, Boolean replace) {
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

	private static void writeRepo(CrudCreator crudCreator, Boolean replace) {
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

	private static void writeService(CrudCreator crudCreator, Boolean replace) {
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

	private static void writeConverter(CrudCreator crudCreator, Boolean replace) {
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

	private static void writeListController(CrudCreator crudCreator, Boolean replace) {
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

	private static void writeEditController(CrudCreator crudCreator, Boolean replace) {
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

	private static void writeListPage(CrudCreator crudCreator, String subFolder, Boolean replace) {
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

	private static void writeEditPage(CrudCreator crudCreator, String subFolder, Boolean replace) {
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

	static void writeFile(CrudoGenerator csw, Boolean replace) {

		File file = new File(csw.getFileName());

		File folder = file.getParentFile();

		folder.mkdirs();

		if (replace != null && file.exists()) {
			if (!replace) {
				return;
			}
			file.delete();
		}

		PrintStream out;
		try {
			if (replace == null) {
				out = System.out;
			} else {
				out = new PrintStream(file);
			}
			csw.write(out);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
