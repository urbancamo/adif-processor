package uk.m0nom.adifproc.adif3.transform;

public interface ApplicationDefinedFields {
    String APPLICATION_NAME = "APROC";
    String ALT = "APP_" + APPLICATION_NAME + "_ALT";
    String MY_ALT = "APP_" + APPLICATION_NAME  + "_MY_ALT";
    String ANT = "APP_" + APPLICATION_NAME + "_ANT";
}
