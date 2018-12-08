package com.joom;

import java.io.*;
import java.util.Random;

@SuppressWarnings("WeakerAccess")
public class FileGenerator {

    private final Random random;

    public FileGenerator() {
        this(new Random());
    }

    public FileGenerator(Random random) {
        this.random = random;
    }

    public static void main(String[] args) throws Exception {
        FileGenerator generator = new FileGenerator();
        generator.generateFile(10000000, 300, new File("./source.txt"));
    }

    public void generateFile(long linesCount, int maxLineLength, File target) throws IOException {
        int a = 'a';
        int maxCode = 'z' - 'a';

        try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(target)))) {
            for (long i = 0; i < linesCount; i++) {
                int length = random.nextInt(maxLineLength - 5) + 5;
                StringBuilder sb = new StringBuilder();
                for (int j = 0; j < length; j++) {
                    sb.append((char) (a + random.nextInt(maxCode)));
                }
                writer.write(sb.toString());
                writer.newLine();
            }
        }
    }
}
