package uk.m0nom.adif3.transform;

import org.apache.commons.lang3.StringUtils;
import org.marsik.ham.adif.Adif3Record;

public class SotaMicrowaveAward {
    public static void addSotaMicrowaveAwardToComment(Adif3Record rec) {
        if (StringUtils.isNotBlank(rec.getSatName())) {
            String additionalComment = "";
            // Are we to use MHL or Coordinates?
            if (rec.getGridsquare() != null) {
                additionalComment = String.format("%s %%QRA%%%s%%", rec.getSatName().toUpperCase(), rec.getGridsquare().toUpperCase());
            } else if (rec.getCoordinates() != null) {
                additionalComment = String.format("%s %%QRA%%%.3f, %.3f%%", rec.getSatName().toUpperCase(), rec.getCoordinates().getLatitude(), rec.getCoordinates().getLongitude());
            }
            if (StringUtils.isNotBlank(additionalComment)) {
                if (StringUtils.isBlank(rec.getComment())) {
                    rec.setComment(additionalComment);
                } else {
                    String newComment = String.format("%s, %s", rec.getComment(), additionalComment);
                    rec.setComment(newComment);
                }
            }
        }
    }
}
