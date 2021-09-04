package uk.m0nom.kml.info;

import uk.m0nom.activity.Activity;
import uk.m0nom.activity.ActivityType;
import uk.m0nom.activity.wwff.WwffInfo;
import uk.m0nom.adif3.contacts.Station;

import java.util.Locale;

public class KmlWwffInfo extends KmlActivityInfo {
    @Override
    public String getInfo(Activity activity) {
        WwffInfo wwffInfo = (WwffInfo) activity;

        String sb = String.format("WWFF: <a href='https://wwff.co/directory/?showRef=%s'>%s</a><br/>", wwffInfo.getRef().toUpperCase(), wwffInfo.getRef()) +
                String.format("%s<br/><br/>", wwffInfo.getName());
        return sb;
    }
}
