package uk.m0nom.adifproc.qsofile;

import org.marsik.ham.adif.Adif3;

import java.io.IOException;

public interface QsoFileReader {
    Adif3 read(String filename, String encoding, boolean sort) throws IOException;
}
