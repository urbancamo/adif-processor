package uk.m0nom.adifproc.adif3.label;

import lombok.Data;

import java.util.ArrayList;
import java.util.Collection;

@Data
public class Adif3LabelFormatterResult {
    private String labels = "";
    private Collection<String> callsigns = new ArrayList<>(0);
}
