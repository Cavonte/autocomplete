package Suggestion;

import Entity.GeoNameCity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;


public class MatchGenerator
{
    public ArrayList reducedList(HashMap<String, GeoNameCity> cities, String query)
    {
        Map<String, GeoNameCity> reducedCities = cities.entrySet().stream()
                .filter(e -> elligibleCity(query,e.getKey()))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

        for (String temps : reducedCities.keySet())
        {
            System.out.println(temps);
        }
        return new ArrayList();
    }

    /**
     *
     * @param query
     * @param cityName
     * @return
     */
    public boolean elligibleCity(String query,String cityName)
    {
        String sanitized = query.trim().replaceAll("\\s+","\\s");
        return sanitized.matches(cityName) || sanitized.toLowerCase().matches(cityName);
    }


    public Double calculateDistance(String currentLongitude, String currentLatitude, GeoNameCity city)
    {
        return 0.0;
    }

}
