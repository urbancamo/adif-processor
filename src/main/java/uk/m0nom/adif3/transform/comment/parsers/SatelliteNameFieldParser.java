package uk.m0nom.adif3.transform.comment.parsers;

import uk.m0nom.adif3.contacts.Qso;
import uk.m0nom.satellite.ApSatellites;

public class SatelliteNameFieldParser implements CommentFieldParser {
    private final ApSatellites apSatellites;

    public SatelliteNameFieldParser(ApSatellites apSatellites) {
        this.apSatellites = apSatellites;
    }

    @Override
    public FieldParseResult parseField(String value, Qso qso) throws CommentFieldParserException {
        if (apSatellites.getSatellite(value.toUpperCase()) != null) {
            qso.getRecord().setSatName(value.toUpperCase());
        } else {
            throw new CommentFieldParserException(this.getClass().getName(), "unknownSatellite", qso, false, value);
        }

        return FieldParseResult.SUCCESS;
    }
}
