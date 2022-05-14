package uk.m0nom.adifproc.irishgrid;

import org.gavaghan.geodesy.GlobalCoordinates;
import org.junit.jupiter.api.Test;
import uk.m0nom.adifproc.coords.GlobalCoords3D;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class IrishGridConverterTest {
    @Test
    public void converterTWsg84ToIrish() {
        IrishGridConverter converter = new IrishGridConverter();
        GlobalCoords3D coords3D = new GlobalCoords3D(52.92270609327906, -5.435695822166423);
        String irishGridRef = converter.getIrishGridRef(coords3D, true);
        assertThat(irishGridRef).isEqualTo("T 72533 88828");
    }

    @Test
    public void convertIrishGridRefToWgs84() {
        checkIrishGridRefToWgs84("T 72533 88828", 52.92270609327906, -5.435695822166423);
    }

    @Test
    public void convertIrishToWgs84MountTemple() {
        checkIrishGridRefToWgs84("N 14755 42077", 53.42862321061858, -7.779162065291757);
    }

    @Test
    public void convertIrishToWgs84Eris() {
        checkIrishGridRefToWgs84("F 64948 35044", 54.24623291611499, -10.072957743362878);
    }

    @Test
    public void convertIrishToWgs84CarnsorePoint() {
        checkIrishGridRefToWgs84("T 11998 03679", 52.17386968664752, -6.363973226398969);
    }

    private void checkIrishGridRefToWgs84(String irishGridRef, double expectedLat, double expectedLon) {
        IrishGridConverter converter = new IrishGridConverter();
        assertThat(converter.parseGridRef(irishGridRef)).isTrue();
        GlobalCoordinates result = converter.getWGS84(true);
        assertThat(result.getLatitude()).isEqualTo(expectedLat);
        assertThat(result.getLongitude()).isEqualTo(expectedLon);
    }
}
