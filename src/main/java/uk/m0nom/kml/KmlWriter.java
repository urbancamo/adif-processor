package uk.m0nom.kml;

import de.micromata.opengis.kml.v_2_2_0.*;
import org.gavaghan.geodesy.GlobalCoordinates;
import uk.m0nom.adif3.args.TransformControl;
import uk.m0nom.adif3.contacts.Qso;
import uk.m0nom.adif3.contacts.Qsos;
import uk.m0nom.adif3.transform.TransformResults;
import uk.m0nom.comms.Ionosphere;
import uk.m0nom.activity.ActivityDatabases;
import uk.m0nom.kml.activity.KmlLocalActivities;
import uk.m0nom.kml.comms.KmlCommsUtils;
import uk.m0nom.kml.station.KmlStationUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.logging.Logger;

public class KmlWriter {
    private static final Logger logger = Logger.getLogger(KmlWriter.class.getName());
    private ActivityDatabases activities;
    private TransformControl control;
    private KmlBandLineStyles bandLineStyles;

    public KmlWriter(TransformControl control) {
        this.control = control;
        bandLineStyles = new KmlBandLineStyles(control.getKmlContactWidth(), control.getKmlContactTransparency());
    }

    public void write(String pathname, String name, ActivityDatabases activities, Qsos qsos, TransformResults results) {
        KmlLocalActivities kmlLocalActivities = new KmlLocalActivities();
        KmlCommsUtils kmlCommsUtils = new KmlCommsUtils(control, activities);

        this.activities = activities;

        final Kml kml = new Kml();
        Document doc = kml.createAndSetDocument().withName(name).withOpen(true);

        // create a Folder
        Folder folder = doc.createAndAddFolder();
        folder.withName("Contacts").withOpen(true);

        // create Placemark elements
        boolean first = true;
        for (Qso qso : qsos.getQsos()) {
            if (first) {
                String error = KmlStationUtils.addMyStationToMap(control, doc, folder, qso);
                if (error != null) {
                    results.setError(error);
                }
                if (qso.getFrom().hasActivity() && control.getKmlShowLocalActivationSites()) {
                    kmlLocalActivities.addLocalActivities(doc, folder, qso.getFrom(), control.getKmlLocalActivationSitesRadius(), activities);
                }
                first = false;
            }
            Folder contactFolder = folder.createAndAddFolder().withName(qso.getTo().getCallsign()).withOpen(false);
            GlobalCoordinates coords = qso.getRecord().getCoordinates();
            if (coords != null) {
                String error = KmlStationUtils.createStationMarker(control, doc, contactFolder, qso);
                if (error != null) {
                    results.setError(error);
                }

                if (qso.getTo().hasActivity() && control.getKmlShowLocalActivationSites()) {
                    Folder localActivityFolder = contactFolder.createAndAddFolder().withName("Local Activity").withOpen(false);
                    kmlLocalActivities.addLocalActivities(doc, localActivityFolder, qso.getTo(), control.getKmlLocalActivationSitesRadius(), activities);
                }
                error = kmlCommsUtils.createCommsLink(this, doc, contactFolder, qso, control);
                if (error != null) {
                    results.setError(error);
                }
            } else {
                results.addContactWithoutLocation(qso.getTo().getCallsign());
                logger.warning(String.format("Cannot determine communication link, no location data for: %s", qso.getTo().getCallsign()));
            }
        }

        try {
            logger.info(String.format("Writing KML to: %s", pathname));
            kml.marshal(new File(pathname));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }


}
