package Suggestion;

import Entity.GeoNameCity;
import com.univocity.parsers.common.record.Record;
import com.univocity.parsers.tsv.TsvParser;
import com.univocity.parsers.tsv.TsvParserSettings;

import java.io.InputStream;
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

    private HashMap<String, GeoNameCity> cities;
    private final String FILE_PATH = "/data/cities_canada-usa.tsv";

    private DataManager()
    {
        cities = new HashMap<>();

        TsvParserSettings settings = new TsvParserSettings();
        settings.getFormat().setLineSeparator("\n");
        settings.setHeaderExtractionEnabled(true);

        TsvParser parser = new TsvParser(settings);

        List<Record> allRecords = parser.parseAllRecords(getReader(FILE_PATH));
        for (Record record : allRecords)
        {
            GeoNameCity tempCity = new GeoNameCity(record.getInt("id"),
                    record.getString("name"),
                    record.getString("ascii"),
                    record.getString("alt_name"),
                    record.getString("lat"),
                    record.getString("long"),
                    record.getString("country"),
                    record.getString("cc2"),
                    record.getInt("population"));
            String key = record.getString("name") + ", " + record.getString("country") + ", " + record.getInt("id");
            cities.put(key, tempCity);
        }

    }

    public HashMap<String, GeoNameCity> getCities()
    {
        return cities;
    }

    public static DataManager getDataManagerInstance()
    {
        if (null == singleInstance)
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
     * @return a reader of the resource
     */
    private Reader getReader(String relativePath)
    {

        try
        {
            InputStream stream = getClass().getResourceAsStream(relativePath);
            return new InputStreamReader(stream, "UTF-8");
        } catch (UnsupportedEncodingException e)
        {
            throw new IllegalStateException("Unable to read input", e);
        }
    }


}
