package com.joom;

import java.io.*;
import java.util.*;

@SuppressWarnings("WeakerAccess")
public class ExternalSorter {

    private static final int STRING_OBJ_OVERHEAD = 60;

    public static void main(String[] args) throws Exception {
        ExternalSorter externalSorter = new ExternalSorter();
        externalSorter.sort(new File("./source.txt"), new File("./target.txt"));
    }

    public void sort(File source, File target) throws IOException {
        sort(source, target, getAvailableMemory() / 2);
    }

    public void sort(File source, File target, long partSize) throws IOException {
        List<File> tempFiles = splitIntoParts(source, partSize);
        mergeFiles(tempFiles, target);
    }

    private List<File> splitIntoParts(File source, long partSize) throws IOException {
        List<File> tempFiles = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(source)))) {
            List<String> buffer = new ArrayList<>();
            try {
                String line = null;
                do {
                    long currentSize = 0;
                    while ((currentSize < partSize) && ((line = reader.readLine()) != null)) {
                        buffer.add(line);
                        currentSize += stringSize(line);
                    }
                    tempFiles.add(sortAndSave(buffer));
                    buffer.clear();
                } while (line != null);

            } catch (EOFException oef) {
                if (buffer.size() > 0) {
                    tempFiles.add(sortAndSave(buffer));
                    buffer.clear();
                }
            }
        }
        return tempFiles;
    }

    private static File sortAndSave(List<String> lines) throws IOException {
        Collections.sort(lines);

        File tempFile = File.createTempFile("extSort", ".txt");
        tempFile.deleteOnExit();

        try (BufferedWriter reader = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(tempFile)))) {
            for (String r : lines) {
                reader.write(r);
                reader.newLine();
            }
        }
        return tempFile;
    }

    private void mergeFiles(List<File> files, File target) throws IOException {
        List<RememberLastReader> readers = new ArrayList<>();

        for (File f : files) {
            readers.add(new RememberLastReader(new BufferedReader(new InputStreamReader(new FileInputStream(f)))));
        }

        try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(target)))) {

            PriorityQueue<RememberLastReader> queue = new PriorityQueue<>(readers.size(), Comparator.comparing(RememberLastReader::getValue));
            for (RememberLastReader reader : readers) {
                if (!reader.isEmpty()) {
                    queue.add(reader);
                }
            }
            while (queue.size() > 0) {
                RememberLastReader reader = queue.poll();

                String line = reader.readLine();
                writer.write(line);
                writer.newLine();

                if (reader.isEmpty()) {
                    reader.close();
                } else {
                    queue.add(reader);
                }
            }

        } finally {
            for (RememberLastReader reader : readers) {
                reader.close();
            }
            for (File f : files) {
                //noinspection ResultOfMethodCallIgnored
                f.delete();
            }
        }
    }

    private static long getAvailableMemory() {
        System.gc();
        Runtime r = Runtime.getRuntime();
        return r.maxMemory() - (r.totalMemory() - r.freeMemory());
    }

    private static long stringSize(String s) {
        return (s.length() * 2) + STRING_OBJ_OVERHEAD;
    }
}
