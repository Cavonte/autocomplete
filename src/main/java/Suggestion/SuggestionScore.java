package Suggestion;

import Entity.GeoNameCity;
import info.debatty.java.stringsimilarity.NormalizedLevenshtein;
import info.debatty.java.stringsimilarity.WeightedLevenshtein;

import java.util.ArrayList;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class SuggestionScore
{
     /**
     *      * https://stackoverflow.com/questions/369512/how-to-compare-objects-by-multiple-fields
     * https://github.com/tdebatty/java-string-similarity  NormalizedLevenshtein
     * @param filteredCities
     * @param currentLatitude
     * @param currentLongitude
     * @param query
     * @return
     */
    public List sortSuggestion(List<GeoNameCity> filteredCities, String currentLatitude, String currentLongitude, String query)
    {
        NormalizedLevenshtein levenshtein = new NormalizedLevenshtein();
        Comparator<GeoNameCity> comparator = Comparator.comparing(city -> levenshtein.distance(query,city.getName()));
        comparator = comparator.thenComparing(Comparator.comparing(city -> calculateDistance(currentLatitude,currentLongitude,city)));

        Stream<GeoNameCity> cityStream = filteredCities.stream().sorted(comparator);
        return cityStream.collect(Collectors.toList());
    }


    /**
     *
     * @param scoredList
     * @param currentCity
     * @return
     */
    public double calculateScore(ArrayList scoredList, GeoNameCity currentCity)
    {
        return scoredList.indexOf(currentCity)/scoredList.size();
    }


    /**
     * http://www.codecodex.com/wiki/Calculate_Distance_Between_Two_Points_on_a_Globe#Java
     * @param currentLatitude
     * @param CurrentLongitude
     * @param targetCity
     * @return
     */
    public double calculateDistance(String currentLatitude, String CurrentLongitude, GeoNameCity targetCity)
    {
        double earthRadius = 6371.0;
        double lat1 = Double.parseDouble(currentLatitude)/1E6;
        double lat2 = Double.parseDouble(targetCity.getLatitude())/1E6;
        double lon1 = Double.parseDouble(CurrentLongitude)/1E6;
        double lon2 = Double.parseDouble(targetCity.getLongitude())/1E6;
        double dLat = Math.toRadians(lat2-lat1);
        double dLon = Math.toRadians(lon2-lon1);
        double a = Math.sin(dLat/2) * Math.sin(dLat/2) +
                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                        Math.sin(dLon/2) * Math.sin(dLon/2);
        double c = 2 * Math.asin(Math.sqrt(a));
        return earthRadius * c;
    }
}
