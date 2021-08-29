package uk.m0nom.contest;

import org.marsik.ham.adif.Adif3;
import org.marsik.ham.adif.Adif3Record;
import org.marsik.ham.adif.enums.Band;
import org.marsik.ham.adif.enums.Mode;
import uk.m0nom.activity.ActivityDatabase;
import uk.m0nom.activity.ActivityDatabases;
import uk.m0nom.activity.ActivityType;

import java.util.HashMap;
import java.util.Map;

public class JackOfAllTradesResultCalculator implements ContestResultCalculator {
    private Map<Band, Integer> bandPointsMap = new HashMap<>();
    private Map<Mode, Integer> modePointsMap = new HashMap<>();

    public JackOfAllTradesResultCalculator() {
        bandPointsMap.put(Band.BAND_2190m, 7);
        bandPointsMap.put(Band.BAND_630m, 7);
        bandPointsMap.put(Band.BAND_560m, 7);
        bandPointsMap.put(Band.BAND_160m, 5);
        bandPointsMap.put(Band.BAND_80m, 2);
        bandPointsMap.put(Band.BAND_60m, 2);
        bandPointsMap.put(Band.BAND_40m, 1);
        bandPointsMap.put(Band.BAND_30m, 1);
        bandPointsMap.put(Band.BAND_20m, 1);
        bandPointsMap.put(Band.BAND_17m, 2);
        bandPointsMap.put(Band.BAND_15m, 2);
        bandPointsMap.put(Band.BAND_12m, 2);
        bandPointsMap.put(Band.BAND_10m, 3);
        bandPointsMap.put(Band.BAND_6m, 3);
        bandPointsMap.put(Band.BAND_4m, 4);
        bandPointsMap.put(Band.BAND_2m, 1);
        bandPointsMap.put(Band.BAND_1_25m, 1);
        bandPointsMap.put(Band.BAND_70cm, 2);
        bandPointsMap.put(Band.BAND_33cm, 4);
        bandPointsMap.put(Band.BAND_23cm, 4);
        bandPointsMap.put(Band.BAND_13cm, 6);
        bandPointsMap.put(Band.BAND_9cm, 11);
        bandPointsMap.put(Band.BAND_6cm, 11);
        bandPointsMap.put(Band.BAND_3cm, 11);
        bandPointsMap.put(Band.BAND_1_25cm, 11);
        bandPointsMap.put(Band.BAND_6mm, 11);
        bandPointsMap.put(Band.BAND_4mm, 11);
        bandPointsMap.put(Band.BAND_2_5mm, 11);
        bandPointsMap.put(Band.BAND_2mm, 11);
        bandPointsMap.put(Band.BAND_1mm, 11);

        modePointsMap.put(Mode.AM, 6);
        modePointsMap.put(Mode.ARDOP, 1);
        modePointsMap.put(Mode.ATV, 6);
        modePointsMap.put(Mode.C4FM, 6);
        modePointsMap.put(Mode.CHIP, 1);
        modePointsMap.put(Mode.CLO, 1);
        modePointsMap.put(Mode.CONTESTI, 1);
        modePointsMap.put(Mode.CW, 1);
        modePointsMap.put(Mode.DATA, 3);
        modePointsMap.put(Mode.DIGITALVOICE, 6);
        modePointsMap.put(Mode.DOMINO, 1);
        modePointsMap.put(Mode.DSTAR, 6);
        modePointsMap.put(Mode.FAX, 1);
        modePointsMap.put(Mode.FM, 1);
        modePointsMap.put(Mode.FSK441, 1);
        modePointsMap.put(Mode.FT8, 0);
        modePointsMap.put(Mode.HELL, 1);
        modePointsMap.put(Mode.ISCAT, 1);
        modePointsMap.put(Mode.JT4, 1);
        modePointsMap.put(Mode.JT6M, 1);
        modePointsMap.put(Mode.JT9, 1);
        modePointsMap.put(Mode.JT44, 1);
        modePointsMap.put(Mode.JT65, 1);
        modePointsMap.put(Mode.MFSK, 1);
        modePointsMap.put(Mode.MSK144, 1);
        modePointsMap.put(Mode.MT63, 1);
        modePointsMap.put(Mode.OLIVIA, 1);
        modePointsMap.put(Mode.OPERA, 1);
        modePointsMap.put(Mode.PAC, 1);
        modePointsMap.put(Mode.PAX, 1);
        modePointsMap.put(Mode.PKT, 1);
        modePointsMap.put(Mode.PSK, 1);
        modePointsMap.put(Mode.PSK2K, 1);
        modePointsMap.put(Mode.Q15, 1);
        modePointsMap.put(Mode.QRA64, 1);
        modePointsMap.put(Mode.ROS, 1);
        modePointsMap.put(Mode.RTTY, 3);
        modePointsMap.put(Mode.RTTYM, 3);
        modePointsMap.put(Mode.SSB, 1);
        modePointsMap.put(Mode.SSTV, 6);
        modePointsMap.put(Mode.T10, 1);
        modePointsMap.put(Mode.THOR, 1);
        modePointsMap.put(Mode.THRB, 1);
        modePointsMap.put(Mode.TOR, 1);
        modePointsMap.put(Mode.V4, 1);
        modePointsMap.put(Mode.VOI, 1);
        modePointsMap.put(Mode.WINMOR, 1);
        modePointsMap.put(Mode.WSPR, 0);
    }

    @Override
    public int calculateResult(ActivityDatabases databases, Adif3 log) {
        int totalPoints = 0;
        for (Adif3Record record : log.getRecords()) {
            if (record.getMySotaRef() != null && record.getMySotaRef().getValue().startsWith("G/LD")) {
                totalPoints += calculatePoints(record);
            }
        }
        return totalPoints;
    }

    public String formatResult(int points) {
        return String.format("JackOfAllTrades: %d", points);
    }

    private int calculatePoints(Adif3Record record) {
        int bandPoints = bandPointsMap.get(record.getBand());
        int modePoints = modePointsMap.get(record.getMode());

        return bandPoints * modePoints;
    }
}
