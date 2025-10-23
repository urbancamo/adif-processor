/**
 predict4java: An SDP4 / SGP4 library for satellite orbit predictions

 Copyright (C)  2004-2022  David A. B. Johnson, G4DPZ.

 Author: David A. B. Johnson, G4DPZ <dave@g4dpz.me.uk>

 Comments, questions and bug reports should be submitted via
 http://sourceforge.net/projects/websat/
 More details can be found at the project home page:

 http://websat.sourceforge.net

 This program is free software; you can redistribute it and/or modify
 it under the terms of the GNU General Public License as published by
 the Free Software Foundation; either version 2 of the License, or
 (at your option) any later version.

 This program is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU General Public License for more details.

 You should have received a copy of the GNU General Public License
 along with this program; if not, visit http://www.fsf.org/
 */
package uk.me.g4dpz.satellite;

import org.joda.time.DateTime;
import org.junit.Assert;
import org.junit.Test;

public class IlluminationTest extends AbstractSatelliteTestBase {

    public IlluminationTest() {
    }

    @Test
    public void testCalculateSunVector() {

        final TLE tle = new TLE(LEO_TLE);

        Assert.assertFalse(tle.isDeepspace());

        final Satellite satellite = SatelliteFactory.createSatellite(tle);

        DateTime timeNow = new DateTime(EPOCH);

        boolean illuminated = true;

        for (int day = 0; day < 30 * 24 * 60; day++) {
            final SatPos satPos = satellite.getPosition(GROUND_STATION, timeNow.toDate());
            final boolean inSun = !satPos.isEclipsed();
            timeNow = timeNow.plusMinutes(1);
            if (illuminated != inSun) {
                System.out.println(day + ", " + inSun);
                illuminated = inSun;
            }
        }
    }

}
