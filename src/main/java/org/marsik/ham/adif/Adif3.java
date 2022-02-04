package org.marsik.ham.adif;

import lombok.Data;
import org.marsik.ham.adif.Adif3Record;
import org.marsik.ham.adif.AdifHeader;

import java.util.ArrayList;
import java.util.List;

@Data
public class Adif3 {
    AdifHeader header;
    List<Adif3Record> records = new ArrayList<>();
}
