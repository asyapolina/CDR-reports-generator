package org.test_task;

import java.util.HashMap;

public class Main {
    public static void main(String[] args) {
        String filePath = "./tests/cdr.txt";
        CDRParser parser = new CDRParser(filePath);
        HashMap<String, Report> reports = parser.getReports();
        for (Report r : reports.values()) {
            r.saveToFile();
        }

    }
}