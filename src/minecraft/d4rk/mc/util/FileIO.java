package d4rk.mc.util;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import d4rk.mc.Hack;

public class FileIO {
	public static FileWriter createWriter(String filename, boolean append) throws IOException {
		File file = new File(filename);
		try {
			file.getParentFile().mkdirs();
		} catch(Exception e) {}
		return new FileWriter(file, append);
	}
}
