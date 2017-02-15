package com.component.benjamin.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.apache.commons.io.IOUtils;

public class DuplicateContentGenerator {

	public static void main(String[] args) {
		File file = new File("./src/test/resources/bi.txt");

		try {
			byte[] contentByte = new byte[(int) file.length()];
			IOUtils.readFully(new FileInputStream(file), contentByte);

			String content = new String(contentByte, "utf-8");

			for (int i = 1; i <= 20; i++) {
				System.out.println(content.replace("1", "" + i));
			}

		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String a = "";
	}
}
