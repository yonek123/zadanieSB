package org.example;

import java.io.File;
import java.io.FileNotFoundException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class Main {
    public static void main(String[] args) {
        //// Step 1: check if D:/logs exists

        String path = "D:\\logs";
        Path directory = Paths.get(path);
        if (!Files.exists(directory)) {
            System.out.println("Path " + path + " does not exist!");
            System.exit(0);
        }


        //// Step 2: list files in lastModified descending order

        //List all log files
        File[] files = directory.toFile().listFiles((dir, name) -> name.endsWith(".log"));
        if (files.length == 0) { //If no log file found exit
            System.out.println("No log files in directory!");
            System.exit(0);
        }

        //Sort files by lastModified descending
        Arrays.sort(files, (f1, f2) -> Long.compare(f2.lastModified(), f1.lastModified()));


        //// Step 3: read all listed files

        List<Severity> severityType = new ArrayList<>(); //List of severity types in files
        List<Integer> severityCount = new ArrayList<>(); //Number of logs of specific severity
        List<String> libraryName = new ArrayList<>(); //List of libraries from log
        List<Integer> libraryCount = new ArrayList<>(); //Number of logs with specific library
        for (File f : files) {
            List<LocalDateTime> timestamp = new ArrayList<>(); //List of logs timestamps in a file
            List<Severity> severityTypeInFile = new ArrayList<>(); //List of severity types in a file
            List<Integer> severityCountInFile = new ArrayList<>(); //Number of logs of specific severity in a file
            List<String> libraryNameInFile = new ArrayList<>(); //List of libraries from log in a file
            List<Integer> libraryCountInFile = new ArrayList<>(); //Number of logs with specific library in a file
            long start = System.currentTimeMillis();
            try {
                Scanner scanner = new Scanner(f);
                while (scanner.hasNextLine()) {
                    String tmpString = scanner.nextLine();
                    String[] strDate = tmpString.split("\\s+", 6); //Separate line into sections: date, time, severity, libraries, thread, error details
                    if (strDate.length > 1 && isValid(strDate[0] + " " + strDate[1])) { //If a line starts with a date it is a new log entry
                        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss,SSS");
                        timestamp.add(LocalDateTime.parse(strDate[0] + " " + strDate[1], dateTimeFormatter));

                        if (severityTypeInFile.stream().anyMatch(p -> p.name.equals(strDate[2]))) { //If severity type in severityTypeInFile increase both severity counters
                            severityCountInFile.set(severityTypeInFile.indexOf(Severity.valueOf(strDate[2])), severityCountInFile.get(severityTypeInFile.indexOf(Severity.valueOf(strDate[2]))) + 1);
                            severityCount.set(severityType.indexOf(Severity.valueOf(strDate[2])), severityCount.get(severityType.indexOf(Severity.valueOf(strDate[2]))) + 1);
                        } else if (severityType.stream().anyMatch(p -> p.name.equals(strDate[2]))) { //If severity type in severityType increase severity counter and add severity type in file counter
                            severityTypeInFile.add(Severity.valueOf(strDate[2]));
                            severityCountInFile.add(1);
                            severityCount.set(severityType.indexOf(Severity.valueOf(strDate[2])), severityCount.get(severityType.indexOf(Severity.valueOf(strDate[2]))) + 1);
                        } else if (Arrays.asList(Severity.values()).stream().anyMatch(p -> p.name.equals(strDate[2]))) { //If severity type not encountered previously but in the standard add it to both counters
                            severityTypeInFile.add(Severity.valueOf(strDate[2]));
                            severityCountInFile.add(1);
                            severityType.add(Severity.valueOf(strDate[2]));
                            severityCount.add(1);
                        } else { //If severity type not in the standard...
                            if (severityTypeInFile.stream().anyMatch(p -> p.name.equals("OTHER"))) { //... but OTHER type in severityTypeInFile increase both severity counters
                                severityCountInFile.set(severityTypeInFile.indexOf(Severity.valueOf("OTHER")), severityCountInFile.get(severityTypeInFile.indexOf(Severity.valueOf("OTHER"))) + 1);
                                severityCount.set(severityType.indexOf(Severity.valueOf("OTHER")), severityCount.get(severityType.indexOf(Severity.valueOf("OTHER"))) + 1);
                            } else if (severityType.stream().anyMatch(p -> p.name.equals("OTHER"))) { //... but OTHER type in severityType increase severity counter and add severity type for in file counter
                                severityTypeInFile.add(Severity.valueOf("OTHER"));
                                severityCountInFile.add(1);
                                severityCount.set(severityType.indexOf(Severity.valueOf("OTHER")), severityCount.get(severityType.indexOf(Severity.valueOf("OTHER"))) + 1);
                            } else { //... and OTHER not encountered before add OTHER
                                severityTypeInFile.add(Severity.valueOf("OTHER"));
                                severityCountInFile.add(1);
                                severityType.add(Severity.valueOf("OTHER"));
                                severityCount.add(1);
                            }
                        }

                        if (libraryNameInFile.contains(strDate[3])) { //If library in libraryNameInFile increase both library counters
                            libraryCountInFile.set(libraryNameInFile.indexOf(strDate[3]), libraryCountInFile.get(libraryNameInFile.indexOf(strDate[3])) + 1);
                            libraryCount.set(libraryName.indexOf(strDate[3]), libraryCount.get(libraryName.indexOf(strDate[3])) + 1);
                        } else if (libraryName.contains(strDate[3])) { //If library in LibraryName increase library counter and add library in file counter
                            libraryNameInFile.add(strDate[3]);
                            libraryCountInFile.add(1);
                            libraryCount.set(libraryName.indexOf(strDate[3]), libraryCount.get(libraryName.indexOf(strDate[3])) + 1);
                        } else { //If library not encountered previously
                            libraryNameInFile.add(strDate[3]);
                            libraryCountInFile.add(1);
                            libraryName.add(strDate[3]);
                            libraryCount.add(1);
                        }
                    } //else { //If a line does not start with a date it is a continuation of previous log }
                }
                scanner.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }

            if (!timestamp.isEmpty()) { //If log has dates get newest and oldest log DateTime, calculate the difference
                LocalDateTime minDate = timestamp.stream()
                        .min(LocalDateTime::compareTo)
                        .get();
                LocalDateTime maxDate = timestamp.stream()
                        .max(LocalDateTime::compareTo)
                        .get();
                Period period = Period.between(minDate.toLocalDate(), maxDate.toLocalDate());
                Duration duration = Duration.between(minDate, maxDate);

                //All operations on data finished, get running time and print results
                long finish = System.currentTimeMillis();
                long timeElapsed = finish - start;
                System.out.println("File name: " + f.getName());
                System.out.println("Reading time: " + timeElapsed + "ms");

                System.out.println("Date range of logs: years : " + period.getYears() +
                        ", months: " + period.getMonths() +
                        ", days: " + period.getDays() +
                        ", hours: " + duration.toHours() % 24 +
                        ", minutes: " + duration.toMinutes() % 60 +
                        ", seconds: " + duration.getSeconds() % 60 +
                        ", milliseconds: " + duration.toMillis() % 1000);

                int errorFatalCounterInFile = 0; //Amount of logs with severity ERROR or higher in a file
                int logsCounterInFile = 0; //Total amount of logs in a file
                System.out.println("Number of logs in " + f.getName() + " depending on severity:");
                for (int i = 0; i < severityTypeInFile.size(); i++) {
                    System.out.println("\t" + severityTypeInFile.get(i).name + ": " + severityCountInFile.get(i));
                    logsCounterInFile += severityCountInFile.get(i);
                    if (severityTypeInFile.get(i).value <= 200) {
                        errorFatalCounterInFile += severityCountInFile.get(i);
                    }
                }
                if (severityTypeInFile.stream().anyMatch(p -> p.value <= 200)) {
                    System.out.println("Ratio of the number of logs in " + f.getName() + " with severity ERROR or higher to all logs: " + errorFatalCounterInFile + ":" + logsCounterInFile);
                }

                System.out.println("Number of different libraries in logs in " + f.getName() + ":");
                for (int i = 0; i < libraryNameInFile.size(); i++) {
                    System.out.println("\t" + libraryNameInFile.get(i).substring(0, libraryNameInFile.get(i).length() - 1).substring(1) + ": " + libraryCountInFile.get(i));
                }
            } else { //Log empty, get running time and print results
                long finish = System.currentTimeMillis();
                long timeElapsed = finish - start;
                System.out.println("File name: " + f.getName());
                System.out.println("Reading time: " + timeElapsed + "ms");
                System.out.println("Log file empty or invalid!");
            }
            System.out.println();
        }

        if (severityType.size() > 0) { //If logs had no severity ignore
            int errorFatalCounter = 0; //Amount of logs with severity ERROR or higher
            int logsCounter = 0; //Total amount of logs
            System.out.println("Number of logs in all files depending on severity:");
            for (int i = 0; i < severityType.size(); i++) {
                System.out.println("\t" + severityType.get(i).name + ": " + severityCount.get(i));
                logsCounter += severityCount.get(i);
                if (severityType.get(i).value <= 200) {
                    errorFatalCounter += severityCount.get(i);
                }
            }
            if (severityType.stream().anyMatch(p -> p.value <= 200)) {
                System.out.println("Ratio of the number of logs with severity ERROR or higher to all logs: " + errorFatalCounter + ":" + logsCounter);
            }
        }

        if (libraryName.size() > 0) { //If logs had no libraries ignore
            System.out.println("Number of different libraries in all files:");
            for (int i = 0; i < libraryName.size(); i++) {
                System.out.println("\t" + libraryName.get(i).substring(0, libraryName.get(i).length() - 1).substring(1) + ": " + libraryCount.get(i));
            }
        }
    }

    //Check if string is a DateTime
    static boolean isValid(String strDate) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss,SSS");
        simpleDateFormat.setLenient(false);
        try {
            simpleDateFormat.parse(strDate);
        } catch (ParseException pe) {
            return false;
        }
        return true;
    }

    public enum Severity { //Based on log4j built-in log levels, OTHER to handle non standard ones
        OFF(0, "OFF"),
        FATAL(100, "FATAL"),
        ERROR(200, "ERROR"),
        WARN(300, "WARN"),
        INFO(400, "INFO"),
        DEBUG(500, "DEBUG"),
        TRACE(600, "TRACE"),
        OTHER(10000, "OTHER");

        public final Integer value;
        public final String name;

        private Severity(Integer value, String name) {
            this.value = value;
            this.name = name;
        }
    }
}