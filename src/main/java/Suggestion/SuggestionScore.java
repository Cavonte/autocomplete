package Suggestion;

import Entity.Coordinate;
import Entity.GeoNameCity;
import info.debatty.java.stringsimilarity.Damerau;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.boot.configurationprocessor.json.JSONArray;
import org.springframework.boot.configurationprocessor.json.JSONException;
import org.springframework.boot.configurationprocessor.json.JSONObject;

import java.security.InvalidParameterException;
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
        if(query.isEmpty() || query.matches("\\d+"))
            throw new InvalidParameterException("Validate query. Given " + query);

        Damerau damerau = new Damerau();
        Comparator<GeoNameCity> comparator = Comparator.comparing(city -> damerau.distance(query.trim().toLowerCase(),city.getName().toLowerCase()));
        if(location.isValidCoordinate())
        comparator = comparator.thenComparing(Comparator.comparing(city -> calculateDistance(location,city)));

        Stream<GeoNameCity> cityStream = filteredCities.stream().sorted(comparator);
        return cityStream.collect(Collectors.toList());
    }


    public List<GeoNameCity> filterByCountry(List<GeoNameCity> filteredCities, Coordinate location, List<GeoNameCity> allCities)
    {
        if(allCities.isEmpty())
            throw new InvalidParameterException("Validate input parameters.");

        GeoNameCity currentCity = identifyClosestCity(location,allCities);

        return filteredCities.stream()
                .filter(e -> currentCity.getCountry().equalsIgnoreCase(e.getCountry()))
                .collect(Collectors.toList());
    }


    /**
     *  Identify the closest city in order to identify the country.
     * @param location of the request
     * @param allCities found in the tsv file
     * @return the name of the city closest to the coordinate passed.
     */
    public GeoNameCity identifyClosestCity(Coordinate location, List<GeoNameCity> allCities)
    {
        if(!location.isValidCoordinate() || allCities.isEmpty())
            throw new InvalidParameterException("Invalid location parameters.");

        double smallestDistance = 3000000.0;
        GeoNameCity closestCity = allCities.get(0);

        for (GeoNameCity city: allCities)
        {
            double distance = calculateDistance(location,city);
            if(distance<smallestDistance)
            {
                smallestDistance = distance;
                closestCity = city;
            }
        }

        return  closestCity;
    }

    /**
     *  Calculate the score of the suggestion based on the position in the array.
     * @param  scoredList list of the suggested cities
     * @param currentCity city entity in the list.
     * @return double score
     */
    private double calculateScore(List<GeoNameCity> scoredList, GeoNameCity currentCity)
    {
        if(scoredList.isEmpty())
            throw new InvalidParameterException("Invalid list of cities.");

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
        if(!location.isValidCoordinate())
            throw new InvalidParameterException("Invalid location provided.");

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
    public JSONArray prepareResultArray(List<GeoNameCity> sortedList, Coordinate coordinate, String limit) throws JSONException
    {
        if(!NumberUtils.isParsable(limit))
         throw new InvalidParameterException("Ensure Limit is parsable as a number");

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

            if(result.length()== NumberUtils.createInteger(limit))
            {
                break;
            }
        }
        return result;
    }

}
