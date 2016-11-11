package com.component.benjamin;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

public class MavenJarExport {

	// 可以直接使用下面的maven命令
	 // mvn dependency:copy-dependencies -DoutputDirectory=lib -DincludeScope=compile
	public static void main(String[] args) {
		try {
			List<String> list = IOUtils.readLines(new FileInputStream(new File("./src/test/resources/mavenDependencis.txt")), "utf-8");
			for (String string : list) {
				
				File orginFile = new File(string);
				FileUtils.copyFile(orginFile, new File("./mavenDependencies/" + orginFile.getName()));
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
