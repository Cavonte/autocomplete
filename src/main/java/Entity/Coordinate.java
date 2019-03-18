package Entity;

/**
 * Entity class for coordinates.
 *
 */
public class Coordinate
{
    public double getLongitude()
    {
        return longitude;
    }

    public void setLongitude(double longitude)
    {
        this.longitude = longitude;
    }

    public double getLatitude()
    {
        return latitude;
    }

    public void setLatitude(double latitude)
    {
        this.latitude = latitude;
    }

    private double longitude;
    private double latitude;

    public Coordinate(double longitude, double latitude)
    {
        this.longitude = longitude;
        this.latitude = latitude;
    }

    public Coordinate(String longitude, String latitude)
    {
        this.longitude = Double.parseDouble(longitude);
        this.latitude = Double.parseDouble(latitude);
    }





}
