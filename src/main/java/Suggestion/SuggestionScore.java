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
     *      * https://stackoverflow.com/questions/369512/how-to-compare-objects-by-multiple-fields
     * https://github.com/tdebatty/java-string-similarity  NormalizedLevenshtein
     * @param filteredCities
     * @param currentLatitude
     * @param currentLongitude
     * @param query
     * @return
     */
    public List<GeoNameCity> sortSuggestion(List<GeoNameCity> filteredCities, Coordinate location, String query)
    {
        Damerau damerau = new Damerau();
        Comparator<GeoNameCity> comparator = Comparator.comparing(city -> damerau.distance(query,city.getName()));
        comparator = comparator.thenComparing(Comparator.comparing(city -> calculateDistance(location,city)));

        Stream<GeoNameCity> cityStream = filteredCities.stream().sorted(comparator);
        return cityStream.collect(Collectors.toList());
    }


    /**
     *
     * @param scoredList
     * @param currentCity
     * @return
     */
    public double calculateScore(List scoredList, GeoNameCity currentCity)
    {
        double index = scoredList.indexOf(currentCity) + 1;
        double adjustedSize = (double)scoredList.size() + 1;
        return 1-(index/adjustedSize);
    }


    /**
     *  http://www.codecodex.com/wiki/Calculate_Distance_Between_Two_Points_on_a_Globe#Java
     * @param location
     * @param targetCity
     * @return
     */
    public double calculateDistance(Coordinate location, GeoNameCity targetCity)
    {
        double earthRadius = 6371.0;
        double lat2 = Double.parseDouble(targetCity.getLatitude())/1E6;
        double lon2 = Double.parseDouble(targetCity.getLongitude())/1E6;
        double lat1 = location.getLatitude()/1E6;
        double lon1 = location.getLongitude()/1E6;
        double dLat = Math.toRadians(lat2-lat1);
        double dLon = Math.toRadians(lon2-lon1);
        double a = Math.sin(dLat/2) * Math.sin(dLat/2) +
                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                        Math.sin(dLon/2) * Math.sin(dLon/2);
        double c = 2 * Math.asin(Math.sqrt(a));
        return earthRadius * c;
    }


    public JSONArray prepareResultArray(List<GeoNameCity> scoredList, Coordinate coordinate) throws JSONException
    {
        JSONArray result = new JSONArray();
        for (GeoNameCity geoNameCity : scoredList)
        {
            //name : city/timezone/ country
            //distance(in kilometers) : distance
            //score : current score
            //id : id of the xity
            JSONObject cityJson = new JSONObject();
            cityJson.put("name", geoNameCity.getName() + ", " + geoNameCity.getTimeZone() + "," + geoNameCity.getCountry());
            cityJson.put("distance (in km)", String.format("%.3f", calculateDistance(coordinate,geoNameCity)));
            cityJson.put("score", String.format("%.2f", calculateScore(scoredList, geoNameCity)));
            cityJson.put("id", geoNameCity.getId());
            result.put(cityJson);
        }
        return result;
    }

}
