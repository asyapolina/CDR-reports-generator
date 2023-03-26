package org.test_task;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;

public class Report {
    private static final String prefixDir = "report";
    public enum Tariff {
        TARIFF_06("06"),
        TARIFF_03("03"),
        TARIFF_11("11");
        public final String name;

        private Tariff(String name) {
            this.name = name;
        }
    }
    final public String phoneNumber;
    final public Tariff tariff;
    public float totalCost = 0.f;

    public class CallRecord {
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

        CallRecord(String startTime, String endTime, String callType, Tariff tariff) {
            this.startTime = LocalDateTime.now();
            this.endTime = LocalDateTime.now();
            duration = endTime - startTime;
            this.callType = CallType.valueOf(callType);
        }

        public static float calcCost(Duration duration, CallType callType) {
            return 0;
        }
}
    ArrayList<CallRecord> calls;

    public Report(final String phoneNumber, final String tariff) {
        this.phoneNumber = phoneNumber;
        this.tariff = Tariff.valueOf(tariff);
    }

    public void addRecord(String startTime, String endTime, String callType) {
        CallRecord r = new CallRecord(startTime, endTime, callType, tariff);
        totalCost += r.cost;
        calls.add(r);
    }

    public void saveToFile(String filePath) {

    }

}
