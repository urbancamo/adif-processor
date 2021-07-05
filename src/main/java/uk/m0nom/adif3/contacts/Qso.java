package uk.m0nom.adif3.contacts;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.marsik.ham.adif.Adif3Record;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Qso {
    private Station from;
    private Station to;
    private Adif3Record record;

    public boolean isS2S() {
        return     (from.getSotaId() != null && to.getSotaId() != null)
                || (from.getWotaId() != null && to.getWotaId() != null)
                || (from.getPotaId() != null && to.getPotaId() != null)
                || (from.getHemaId() != null && to.getHemaId() != null);
    }
}
