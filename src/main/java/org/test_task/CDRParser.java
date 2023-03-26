package org.test_task;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class CDRParser {
    private HashMap<String, Report> reports = new HashMap<>();
    private String recordsPath;
    public CDRParser(String recordsPath) {
        this.recordsPath = recordsPath;
        read(recordsPath);
    }

    private void read(final String filePath) {
        BufferedReader reader;
        try {
            reader = new BufferedReader(new FileReader(filePath));

            String line = reader.readLine();
            while (line != null) {
                String[] splitString = line.split(", ");
                String callType = splitString[0];
                String phoneNumber = splitString[1];
                String startTime = splitString[2];
                String endTime = splitString[3];
                String tariff = splitString[4];
                if (reports.containsKey(phoneNumber)) {
                    Report r = reports.get(phoneNumber);
                    r.addRecord(startTime, endTime, callType);
                } else {
                    Report r = new Report(phoneNumber, tariff);
                    r.addRecord(startTime, endTime, callType);
                    reports.put(phoneNumber, r);
                }
                line = reader.readLine();
            }

            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public HashMap<String, Report> getReports() {
        return reports;
    }
}
