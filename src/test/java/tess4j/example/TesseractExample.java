package tess4j.example;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;

import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;

/**
 * Created by Benjamin on 2017/7/6.
 */
public class TesseractExample {
    public static void main(String[] args) {
        // System.setProperty("user.language", "en");
        System.setProperty("jna.debug_load", "true");
        System.setProperty("jna.library.path",
                "C:\\Users\\ccchd\\AppData\\Local\\Temp\\tess4j\\win32-x86-64");

        try {
            FileUtils.copyDirectory(
                    new File(
                            "E:\\IdeaProjects\\components\\MyTest\\lib\\win32-x86-64"),
                    new File(
                            "C:\\Users\\ccchd\\AppData\\Local\\Temp\\tess4j\\win32-x86-64"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println(System.getProperty("user.language"));
        System.out.println(System.getProperty("jna.library.path"));

        File imageFile = new File(
                "E:\\IdeaProjects\\components\\MyTest\\src\\main\\resources\\jcaptcha.jpg");
        Tesseract instance = new Tesseract(); // JNA Interface Mapping
        // ITesseract instance = new Tesseract1(); // JNA Direct Mapping
        // File tessDataFolder = LoadLibs.extractTessResources("tessdata"); //
        // Maven build bundles English data
        // instance.setDatapath(tessDataFolder.getParent());

        try {
            String result = instance.doOCR(imageFile);
            System.out.println(result);
        } catch (TesseractException e) {
            System.err.println(e.getMessage());
        }
    }
}
