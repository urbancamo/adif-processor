package uk.m0nom.maidenheadlocator;

public class LatLng implements Comparable<LatLng> {
    /// <summary>
    /// Latitude, -90 to +90 (N/S direction)
    /// </summary>
    public double latitude;

    /// <summary>
    /// Longitude, -180 to +180 (W/E direction)
    /// </summary>
    public double longitude;

    @Override
    public String toString() {
        return String.format("%03.3f%s %03.3f%s", longitude, (longitude >= 0 ? "N" : "S"),
                latitude, (latitude >= 0 ? "E" : "W"));
    }

    @Override
    public int compareTo(LatLng to) {
        if (latitude == to.latitude && longitude == to.longitude) return 0;
        return -1;
    }
}
