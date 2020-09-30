package Entity;

import org.apache.commons.lang3.math.NumberUtils;

/**
 * Entity class for coordinates.
 *
 */
public class Coordinate
{
    private boolean validCoordinate = false;
    private double longitude = 0.0;
    private double latitude = 0.0;

    /**
     *
     * @param longitude double
     * @param latitude double
     */
    public Coordinate(double longitude, double latitude)
    {
        this.longitude = longitude;
        this.latitude = latitude;
        this.validCoordinate = true;
    }

    /**
     *
     * @param longitude parsable string
     * @param latitude parsable string
     */
    public Coordinate(String longitude, String latitude)
    {
        if(longitude != null && latitude != null && NumberUtils.isParsable(longitude) && NumberUtils.isParsable(latitude))
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
