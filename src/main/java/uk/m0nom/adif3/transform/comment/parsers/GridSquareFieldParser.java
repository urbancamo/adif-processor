package uk.m0nom.adif3.transform.comment.parsers;

import org.marsik.ham.adif.Adif3Record;
import uk.m0nom.adif3.contacts.Qso;
import uk.m0nom.coords.GlobalCoords3D;
import uk.m0nom.coords.LocationSource;
import uk.m0nom.maidenheadlocator.MaidenheadLocatorConversion;

public class GridSquareFieldParser implements CommentFieldParser {
    @Override
    public FieldParseResult parseField(String value, Qso qso) throws CommentFieldParserException {
        Adif3Record rec = qso.getRecord();

        if (MaidenheadLocatorConversion.isAValidGridSquare(value)) {
            switch (value.length()) {
                case 4:
                case 6:
                case 8:
                case 10:
                    if (value.length() > 6) {
                        // Truncate more accurate grid square values to 6 characters to put in the record
                        // as it doesn't support any more accuracy than 6
                        rec.setGridsquare(value.substring(0, 6));
                    } else {
                        rec.setGridsquare(value);
                    }
                    // Use full accuracy to set the coordinates
                    GlobalCoords3D coordinates = MaidenheadLocatorConversion.locatorToCoords(LocationSource.OVERRIDE, value);
                    rec.setCoordinates(coordinates);
                    qso.getTo().setCoordinates(coordinates);
                    break;
                default:
                    throw new CommentFieldParserException(this.getClass().getName(), "parseError", qso, true, value, rec.getCall(), rec.getTimeOn().toString());
            }
        } else {
            throw new CommentFieldParserException(this.getClass().getName(), "parseError", qso, true, value, rec.getCall(), rec.getTimeOn().toString());
        }
        return FieldParseResult.SUCCESS;
    }
}
