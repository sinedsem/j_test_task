package com.joom;

import java.io.BufferedReader;
import java.io.IOException;

@SuppressWarnings("WeakerAccess")
class RememberLastReader {

    private BufferedReader reader;
    private String cache;

    RememberLastReader(BufferedReader reader) throws IOException {
        this.reader = reader;
        readLineInternal();
    }

    void close() throws IOException {
        this.reader.close();
    }

    boolean isEmpty() {
        return this.cache == null;
    }

    String getValue() {
        return this.cache;
    }

    String readLine() throws IOException {
        String result = this.cache;
        readLineInternal();
        return result;
    }

    private void readLineInternal() throws IOException {
        this.cache = this.reader.readLine();
    }


}