package org.test_task;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class CDRParser {
    private String recordsPath;
    public CDRParser(String recordsPath) {
        this.recordsPath = recordsPath;

    }

    private read() {
        BufferedReader reader;

        try {
            reader = new BufferedReader(new FileReader("sample.txt"));
            String line = reader.readLine();

            while (line != null) {
                System.out.println(line);
                // read next line
                line = reader.readLine();
            }

            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
