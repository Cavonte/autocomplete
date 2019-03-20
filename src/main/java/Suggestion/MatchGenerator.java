package Suggestion;

import Entity.GeoNameCity;
import info.debatty.java.stringsimilarity.Damerau;

import java.util.List;
import java.util.stream.Collectors;

public class MatchGenerator
{
    /**
     * Filter the list of cities based on the query.
     * @param cities untouched list of cities
     * @param query query passed by user.
     * @return list of cities that are eligible
     */
    public List<GeoNameCity> reducedList(List<GeoNameCity> cities, String query)
    {
        return cities.stream()
                .filter(e -> eligibleCity(query, e.getName()))
                .collect(Collectors.toList());
    }

    /**
     * Use string comparison and string similarity tools to filter eligible cities.
     * Damerau -- Word Similarity Tool https://github.com/tdebatty/java-string-similarity
     *
     * @param query from the request
     * @param cityName cityName being compared
     * @return a boolean for the filter method
     */
    public boolean eligibleCity(String query, String cityName)
    {
        Damerau damerau = new Damerau();
        double minimalChangeTolerance = 2.0;
        String sanitizedQuery = query.trim().replaceAll("\\s+", " ").toLowerCase();
        cityName = cityName.toLowerCase();

        return sanitizedQuery.matches(cityName)
                || cityName.contains(sanitizedQuery)
                || damerau.distance(cityName, sanitizedQuery) < minimalChangeTolerance;
    }

}
