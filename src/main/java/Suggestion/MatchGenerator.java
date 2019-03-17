package Suggestion;

import Entity.GeoNameCity;
import info.debatty.java.stringsimilarity.Damerau;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


public class MatchGenerator
{
    /**
     *
     * @param cities
     * @param query
     * @return
     */
    public List<GeoNameCity> reducedList(HashMap<String, GeoNameCity> cities, String query)
    {
        Map<String, GeoNameCity> reducedCities = cities.entrySet().stream()
                .filter(e -> eligibleCity(query, e.getKey()))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
        return new ArrayList<>(reducedCities.values());
    }

    /**
     * Damerau -- Word Similarity Tool https://github.com/tdebatty/java-string-similarity
     *
     * @param query
     * @param cityName
     * @return
     */
    public boolean eligibleCity(String query, String cityName)
    {
        Damerau d = new Damerau();
        double minimumChangeTolerance = 2.0;
        String sanitized = query.trim().replaceAll("\\s+", "\\s").toLowerCase();
        cityName = cityName.split(",")[0].toLowerCase();

        return sanitized.matches(cityName)
                || cityName.contains(sanitized)
                || d.distance(cityName, sanitized) < minimumChangeTolerance;
    }

}
