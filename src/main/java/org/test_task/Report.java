package org.test_task;
import java.io.*;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.time.temporal.ChronoUnit;
public class Report {
    private static final String prefixDir = "report";
    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    public enum Tariff {
        TARIFF_06("06", 300, 100, 1),
        TARIFF_03("03", 0,1.5f, 1.5f),
        TARIFF_11("11",100, 0.5f, 1.5f);
        public final String name;
        public final int minutes;
        public final float costIn;
        public final float costOut;

        private Tariff(String name, int minutes, float costIn, float costOut) {
            this.name = name;
            this.minutes = minutes;
            this.costIn = costIn;
            this.costOut = costOut;
        }
    }
    final public String phoneNumber;
    final public Tariff tariff;

    public int minsResidual;
    public float totalCost = 0.f;

    ArrayList<CallRecord> calls = new ArrayList<>();

    public class CallRecord {
        private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
        public enum CallType {
            INCOMING("02"),
            OUTGOING("01");
            public final String name;

            private CallType(String name) {
                this.name = name;
            }
        }
        public final LocalDateTime startTime;
        public final LocalDateTime endTime;
        public final CallType callType;
        public final Duration duration;
        public final float cost;

        CallRecord(String startTime, String endTime, String callType, Tariff tariff, int minsResid) {
            this.startTime = LocalDateTime.parse(startTime, formatter);
            this.endTime = LocalDateTime.parse(endTime, formatter);
            duration = Duration.between(this.startTime, this.endTime);
            if (callType.equals(CallType.INCOMING.name)) {
                this.callType =  CallType.INCOMING;
            } else if (callType.equals(CallType.OUTGOING.name)) {
                this.callType =  CallType.OUTGOING;
            } else {
                throw new IllegalArgumentException("Wrong call type: " + callType);
            }
            cost = calcCost(duration, this.callType, tariff, minsResid);
        }

        public static float calcCost(final Duration duration, final CallType callType, final Tariff tariff, int minsResid) {
            // need to take into account that
            // we should do +1 to minutes every time
            // because there always seconds part of duration exists
            // ex. 00:00:01 - this will count as 1 minute
            int mins = (int) duration.toMinutes() + 1;
            if (tariff == Tariff.TARIFF_03) {
                return mins * tariff.costIn;
            } else if (tariff == Tariff.TARIFF_06) {
                return calcTariff06(mins, minsResid, callType);
            } else if (tariff == Tariff.TARIFF_11) {
                return calcTariff11(mins, minsResid, callType);
            } else {
                throw new IllegalArgumentException("Unknown tariff!");
            }
        }

        private static float calcTariff11(final int mins, final int minsResid, final CallType callType) {
            if (callType == CallType.INCOMING) return 0.f;
            float cost = 0.f;
            if (mins <= minsResid) {
                cost = mins * Tariff.TARIFF_11.costIn;
            } else {
                cost = minsResid * Tariff.TARIFF_11.costIn + (mins - minsResid) * Tariff.TARIFF_11.costOut;
            }
            return cost;
        }
        private static float calcTariff06(final int mins, final int minsResid, final CallType callType) {
            if (mins <= minsResid) {
                return 0.f;
            } else {
                return (mins - minsResid) * Tariff.TARIFF_06.costOut;
            }
        }
}
    public Report(final String phoneNumber, final String tariff) {
        this.phoneNumber = phoneNumber;
        this.tariff = Tariff.valueOf("TARIFF_" + tariff);
        minsResidual = this.tariff.minutes;
        if (this.tariff == Tariff.TARIFF_06) {
            totalCost = 100;
        }
    }

    public void addRecord(String startTime, String endTime, String callType) {
        CallRecord r = new CallRecord(startTime, endTime, callType, tariff, minsResidual);
        totalCost += r.cost;
        minsResidual = minsResidual - (int) r.duration.toMinutes() < 0 ? 0 :
                minsResidual - (int) r.duration.toMinutes();
        calls.add(r);
    }

    public void saveToFile() {
        try {
             File dir = new File(prefixDir);
             dir.mkdir();
             File reportFile = new File(dir,phoneNumber + ".txt");
             reportFile.createNewFile();
             FileOutputStream writer = new FileOutputStream(reportFile, false);
             writer.write(("Tariff index: " + tariff.name + "\n").getBytes());
             writer.write(("----------------------------------------------------------------------------\n").getBytes());
             writer.write(("Report for phone number " + phoneNumber + ":\n").getBytes());
             writer.write(("----------------------------------------------------------------------------\n").getBytes());
             writer.write(("| Call Type |   Start Time        |     End Time        | Duration | Cost  |\n").getBytes());
             writer.write(("----------------------------------------------------------------------------\n").getBytes());
             for (CallRecord r : calls) {
                 String durationHMS = String.format("%02d:%02d:%02d",
                         r.duration.toHours(),
                         r.duration.toMinutesPart(),
                         r.duration.toSecondsPart());
                 writer.write(String.format("|     %2s    | %19s | %19s | %8s | %5s |\n", r.callType.name, r.startTime.format(dateFormatter), r.endTime.format(dateFormatter), durationHMS,  String.format("%.02f",r.cost)).getBytes());
             }
             writer.write(("----------------------------------------------------------------------------\n").getBytes());
             writer.write(String.format("|                                           Total Cost: |%10s rubles |\n", String.format("%.02f",totalCost)).getBytes());
             writer.write(("----------------------------------------------------------------------------\n").getBytes());

        } catch (IOException ex) {
            System.out.println("Can't save file " + ex.getMessage());
        }
    }

}
