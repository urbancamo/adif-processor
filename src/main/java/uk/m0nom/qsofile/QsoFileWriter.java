package uk.m0nom.qsofile;

import org.marsik.ham.adif.Adif3;

import java.io.IOException;

public interface QsoFileWriter {
    void write(String filename, String encoding, Adif3 log) throws IOException;
}
