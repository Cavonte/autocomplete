package Entity;

/**
 * Entity class for coordinates.
 *
 */
public class Coordinate
{

    private boolean validCoordinate = false;
    private double longitude = 0.0;
    private double latitude = 0.0;

    public Coordinate(double longitude, double latitude)
    {
        this.longitude = longitude;
        this.latitude = latitude;
        this.validCoordinate = true;
    }

    public Coordinate(String longitude, String latitude)
    {
        if(longitude != null && latitude != null)
        {
            this.longitude = Double.parseDouble(longitude);
            this.latitude = Double.parseDouble(latitude);
            this.validCoordinate = true;
        }
    }

    public double getLongitude()
    {
        return longitude;
    }

    public void setLongitude(double longitude)
    {
        this.longitude = longitude;
    }

    public boolean isValidCoordinate()
    {
        return validCoordinate;
    }

    public void setValidCoordinate(boolean validCoordinate)
    {
        this.validCoordinate = validCoordinate;
    }

    public double getLatitude()
    {
        return latitude;
    }
    public void setLatitude(double latitude)
    {
        this.latitude = latitude;
    }





}
