package com.component.benjamin.utils;

import java.io.File;
import java.util.Set;

import com.component.utils.FileFinder;

public class RenameUtil {

	public static void main(String[] args) {
		renamePartially();
	}

	public static void renameTo(String[] args) {

		String match = "C11";
		String department = "C21";
		Set<File> files = FileFinder.find(
				new File("\\\\192.168.0.250\\TEMP Version\\TelwareR01V02D22\\C_UP\\" + department),
				".*" + match + ".*");

		for (File file : files) {
			// System.out.println("got file: " + file);
			String newName = file.getName().replace(match, department);
			boolean result = file.renameTo(new File(file.getParentFile(), newName));
			System.out.println("rename " + file + " to " + newName + " " + (result ? "SUCCESS" : "FIALED"));
		}
	}

	public static void renamePartially() {

		String match = "IMG_01";
		String replaceTo = "IMG_40";
		// 将路径下对应的部分替换
		Set<File> files = FileFinder.find(new File("G:\\Documents\\My Photos\\20170112公司年会"),
				"(.*\\\\)+" + match + ".*\\.JPG");

		System.out.println("got total " + files.size() + " files");
		for (File file : files) {
			// System.out.println("got file: " + file);
			String newName = file.getName().replace(match, replaceTo);
			boolean result = file.renameTo(new File(file.getParentFile(), newName));
			System.out.println("rename " + file + " to " + newName + " " + (result ? "SUCCESS" : "FIALED"));
		}

	}

}
