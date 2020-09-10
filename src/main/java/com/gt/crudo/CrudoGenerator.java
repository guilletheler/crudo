package com.gt.crudo;

import java.io.PrintStream;

public interface CrudoGenerator {
	String getFileName();
	void write(PrintStream printStream);
}
