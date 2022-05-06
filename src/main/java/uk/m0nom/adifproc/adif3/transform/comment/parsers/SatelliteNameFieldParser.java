package uk.m0nom.adifproc.adif3.transform.comment.parsers;

import org.marsik.ham.adif.enums.Propagation;
import uk.m0nom.adifproc.adif3.contacts.Qso;
import uk.m0nom.adifproc.satellite.ApSatelliteService;

public class SatelliteNameFieldParser implements CommentFieldParser {
    private final ApSatelliteService apSatelliteService;

    public SatelliteNameFieldParser(ApSatelliteService apSatelliteService) {
        this.apSatelliteService = apSatelliteService;
    }

    @Override
    public FieldParseResult parseField(String value, Qso qso) throws CommentFieldParserException {
        if (apSatelliteService.getSatellite(value, qso.getRecord().getQsoDate()) != null) {
            qso.getRecord().setSatName(value.toUpperCase());
            qso.getRecord().setPropMode(Propagation.SATELLITE);
        } else {
            throw new CommentFieldParserException(this.getClass().getName(), "unknownSatellite", qso, false, value);
        }

        return FieldParseResult.SUCCESS;
    }
}
