package com.joom;

import org.junit.Test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.Random;

import static org.junit.Assert.assertTrue;

public class ExternalSorterTest {

    @Test
    public void testBasic() throws Exception {
        File source = File.createTempFile("test", "source");
        File target = File.createTempFile("test", "target");

        FileGenerator generator = new FileGenerator(new Random(1));
        generator.generateFile(1000, 100, source);

        ExternalSorter externalSorter = new ExternalSorter();
        externalSorter.sort(source, target, 10_000);

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(target)))) {
            String prevLine = reader.readLine();
            String line;
            while ((line = reader.readLine()) != null) {
                assertTrue(prevLine.compareTo(line) < 0);
                prevLine = line;
            }
        }

    }

}