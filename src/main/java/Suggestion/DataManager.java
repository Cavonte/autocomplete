package Suggestion;

import Entity.GeoNameCity;
import com.univocity.parsers.common.record.Record;
import com.univocity.parsers.tsv.TsvParser;
import com.univocity.parsers.tsv.TsvParserSettings;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.List;
/**
 *
 */
public class DataManager
{
    private static DataManager singleInstance = null;

    private HashMap<String, GeoNameCity>  cities;
    private final String FILE_PATH = "data/cities_canada-usa.tsv";

    private DataManager()
    {
        cities = new HashMap<>();

        TsvParserSettings settings = new TsvParserSettings();
        settings.getFormat().setLineSeparator("\n");

        TsvParser parser = new TsvParser(settings);

        List<Record> allRecords = parser.parseAllRecords(getReader(FILE_PATH));
        for(Record record : allRecords){
//            print("Year: " + record.getValue("year", 2000)); //defaults year to 2000 if value is null.
//            print(", Model: " + record.getString("model"));
//            println(", Price: " + record.getBigDecimal("price"));
//            id
//            name
//            ascii
//            alt_name
//            lat
//            long
//            country
//            cc2
//            population
//            elevation
            GeoNameCity tempCity =  new GeoNameCity(record.getInt("id"),
                    record.getString("name"),
                    record.getString("ascii"),
                    record.getString("alt_name"),
                    record.getString("lat"),
                    record.getString("long"),
                    record.getString("country"),
                    record.getString("cc2"),
                    record.getInt("population"),
                    record.getInt("elevation"));
            cities.put(record.getString("name"),tempCity);
        }

        for(String cityName: cities.keySet())
        {
            System.out.println(cityName + "  added.");
        }
    }

    public HashMap<String, GeoNameCity> getCities()
    {
        return cities;
    }

    public static DataManager getDataManagerInstance()
    {
        if(null == singleInstance)
        {
            singleInstance = new DataManager();
        }

        return singleInstance;
    }

    /**
     * Credits https://github.com/uniVocity/univocity-parsers/blob/master/src/test/java/com/univocity/parsers/examples/Example.java
     * Creates a reader for a resource in the relative path
     *
     * @param relativePath relative path of the resource to be read
     *
     * @return a reader of the resource
     */
    private static Reader getReader(String relativePath) {
        try {
            return new InputStreamReader(DataManager.class.getResourceAsStream(relativePath), "UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new IllegalStateException("Unable to read input", e);
        }
    }



}
