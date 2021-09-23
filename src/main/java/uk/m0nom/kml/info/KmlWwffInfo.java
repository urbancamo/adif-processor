package uk.m0nom.kml.info;

import uk.m0nom.activity.Activity;
import uk.m0nom.activity.wwff.WwffInfo;

public class KmlWwffInfo extends KmlActivityInfo {
    @Override
    public String getInfo(Activity activity) {
        WwffInfo wwffInfo = (WwffInfo) activity;

        return String.format("WWFF: <a href='https://wwff.co/directory/?showRef=%s'>%s</a><br/>", wwffInfo.getRef().toUpperCase(), wwffInfo.getRef()) +
                String.format("%s<br/><br/>", wwffInfo.getName());
    }
}
