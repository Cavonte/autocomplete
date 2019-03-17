package Entity;

public class GeoNameCity
{
    private int id;
    private String name;
    private String ascii;
    /**
     * Comma seperated
     */
    private String altName;
    private String latitude;
    private String longitude;
    private String country;
    /**
     * Alt country code name, comma separated
     */
    private String altCountryCode;
    private String timeZone;
    private int population;


    /**
     * Constructor for the City Entity
     * @param id
     * @param name
     * @param ascii
     * @param altName
     * @param latitude
     * @param longitude
     * @param country
     * @param altCountryCode
     * @param population
     */
    public GeoNameCity(int id, String name, String ascii, String altName, String latitude, String longitude, String country, String altCountryCode,String timeZone, int population)
    {
        this.id = id;
        this.name = name;
        this.ascii = ascii;
        this.altName = altName;
        this.latitude = latitude;
        this.longitude = longitude;
        this.country = country;
        this.altCountryCode = altCountryCode;
        this.timeZone = timeZone;
        this.population = population;
    }

    public int getId()
    {
        return id;
    }

    public String getName()
    {
        return name;
    }

    public String getAscii()
    {
        return ascii;
    }

    public String getAltName()
    {
        return altName;
    }

    public String getLatitude()
    {
        return latitude;
    }

    public String getLongitude()
    {
        return longitude;
    }

    public String getCountry()
    {
        return country;
    }

    public String getAltCountryCode()
    {
        return altCountryCode;
    }

    public int getPopulation()
    {
        return population;
    }

    public String getTimeZone()
    {
        return timeZone;
    }
}
