package com.nalyutin.util;

import java.io.File;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import android.os.Environment;

/**
 * Created by pine on 29.10.2014.
 */
public class TextFileReader {

    public static String getData(String fileName) {
        File sdCard = Environment.getExternalStorageDirectory();

        File file = new File(sdCard,fileName);

        StringBuilder text = new StringBuilder();


        try {
            String line;
            BufferedReader br = new BufferedReader(new FileReader(file));

            while ((line = br.readLine()) != null) {
                text.append(line);
                text.append('\n');
            }
        }
        catch (IOException e) {
            text.append(e.getMessage());//You'll need to add proper error handling here
        }

        return text.toString();
    }
}
