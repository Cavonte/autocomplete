package Suggestion;

import Entity.Coordinate;
import Entity.GeoNameCity;
import info.debatty.java.stringsimilarity.Damerau;
import org.springframework.boot.configurationprocessor.json.JSONArray;
import org.springframework.boot.configurationprocessor.json.JSONException;
import org.springframework.boot.configurationprocessor.json.JSONObject;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class SuggestionScore
{
     /**
      * Sort the suggestions based on the similarity with the city names and secondly based on the distance
     * https://stackoverflow.com/questions/369512/how-to-compare-objects-by-multiple-fields
     * https://github.com/tdebatty/java-string-similarity  Damerau
     * @param filteredCities list of already reduced cities
     * @param location location of the request
     * @param query initial query
     * @return list of sorted suggestions
     */
    public List<GeoNameCity> sortSuggestion(List<GeoNameCity> filteredCities, Coordinate location, String query)
    {
        Damerau damerau = new Damerau();
        Comparator<GeoNameCity> comparator = Comparator.comparing(city -> damerau.distance(query.trim().toLowerCase(),city.getName().toLowerCase()));
        if(location.isValidCoordinate())
        comparator = comparator.thenComparing(Comparator.comparing(city -> calculateDistance(location,city)));

        Stream<GeoNameCity> cityStream = filteredCities.stream().sorted(comparator);
        return cityStream.collect(Collectors.toList());
    }


    /**
     *  Calculate the score of the suggestion based on the position in the array.
     * @param  scoredList list of the suggested cities
     * @param currentCity city entity in the list.
     * @return double score
     */
    private double calculateScore(List<GeoNameCity> scoredList, GeoNameCity currentCity)
    {
        double index = scoredList.indexOf(currentCity) + 1;
        double adjustedSize = (double)scoredList.size() + 1;
        return 1-(index/adjustedSize);
    }


    /**
     * Calculate the distance between the current location and the target City
     *  http://www.codecodex.com/wiki/Calculate_Distance_Between_Two_Points_on_a_Globe#Java
     * @param location of the request
     * @param targetCity target of the calculation
     * @return distance in kilometers
     */
    private double calculateDistance(Coordinate location, GeoNameCity targetCity)
    {
        double earthRadius = 6371.0;
        double lat2 = Double.parseDouble(targetCity.getLatitude());
        double lon2 = Double.parseDouble(targetCity.getLongitude());
        double lat1 = location.getLatitude();
        double lon1 = location.getLongitude();
        double dLat = Math.toRadians(lat2-lat1);
        double dLon = Math.toRadians(lon2-lon1);
        double a = Math.sin(dLat/2) * Math.sin(dLat/2) +
                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                        Math.sin(dLon/2) * Math.sin(dLon/2);
        double c = 2 * Math.asin(Math.sqrt(a));
        return earthRadius * c;
    }


    /**
     * Extracted the response json method. Creates the json array.
     * //name : city/timezone/ country
     * //distance(in kilometers) : distance
     * //score :  score
     * //id : id of the city
     * //longitude: longitude
     * //latitude: latitude
     * @param sortedList list of sorted list
     * @param coordinate used to calculate the distance
     * @return JSONArray
     * @throws JSONException when creating the json string.
     */
    public JSONArray prepareResultArray(List<GeoNameCity> sortedList, Coordinate coordinate) throws JSONException
    {
        JSONArray result = new JSONArray();
        for (GeoNameCity geoNameCity : sortedList)
        {
            JSONObject cityJson = new JSONObject();
            cityJson.put("name", geoNameCity.getName() + ", " + geoNameCity.getTimeZone() + ", " + geoNameCity.getCountry());
            if(coordinate.isValidCoordinate())
            cityJson.put("distance (in km)", String.format("%.3f", calculateDistance(coordinate,geoNameCity)));
            cityJson.put("score", Double.parseDouble(String.format("%.2f", calculateScore(sortedList, geoNameCity))));
            cityJson.put("id", geoNameCity.getId());
            cityJson.put("longitude", geoNameCity.getLongitude());
            cityJson.put("latitude", geoNameCity.getLatitude());
            result.put(cityJson);
        }
        return result;
    }

}
