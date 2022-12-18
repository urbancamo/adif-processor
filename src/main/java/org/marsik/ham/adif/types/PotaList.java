package org.marsik.ham.adif.types;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Data
public class PotaList implements AdifType {
    private List<Pota> potaList;

    public PotaList() {
        this.potaList = new ArrayList<>();
    }

    public PotaList(List<Pota> potaList) {
        this.potaList = potaList;
    }

    public void addPota(Pota pota) {
        if (potaList == null) {
            setPotaList(new ArrayList<>(1));
        }
        potaList.add(pota);
    }
    @Override
    public String getValue() {
        return potaList.stream().map(Pota::getValue).collect(Collectors.joining(","));
    }

    public static PotaList valueOf(String s) {
        String[] potas = s.replaceAll("\\s", "").split(",");
        List<Pota> rtn = new ArrayList<>(potas.length);
        for (String pota: potas) {
            rtn.add(Pota.valueOf(pota));
        }
        return new PotaList(rtn);
    }
}