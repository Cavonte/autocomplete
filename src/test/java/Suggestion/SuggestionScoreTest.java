package Suggestion;

import Entity.Coordinate;
import Entity.GeoNameCity;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.configurationprocessor.json.JSONArray;
import org.springframework.boot.configurationprocessor.json.JSONException;
import org.springframework.boot.configurationprocessor.json.JSONObject;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertTrue;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = SuggestionScoreTest.class)
public class SuggestionScoreTest
{
    /**
     * Helper method.
     * @return list of cities.
     */
    private ArrayList<GeoNameCity> getInitialList()
    {
        ArrayList<GeoNameCity> testList = new ArrayList<>();

        GeoNameCity Lexington = new GeoNameCity(4941935, "Lexington", "Lexington", "Lexington", "42.44732", "-71.2245", "US", "", "America/New York", 5);
        GeoNameCity Lexington2 = new GeoNameCity(4769339, "Lexington", "Lexington", "Lexington", "37.78402", "-79.44282", "US", "", "America/New York", 5);
        GeoNameCity LexingtonPark = new GeoNameCity(3, "Lexington Park", "Lexington Park", "Lexington Park", "12.34567", "12.34567", "MX", "", "America/New York", 5);
        GeoNameCity Lexington4 = new GeoNameCity(4475773, "Lexington", "Lexington", "Lexington", "35.82403", "-80.25338", "US", "", "America/New York", 5);
        GeoNameCity Lexington5 = new GeoNameCity(4475773, "Lexington", "Lexington", "Lexington", "37.98869", "-84.47772", "US", "", "America/New York", 5);
        GeoNameCity LexingtonFayette = new GeoNameCity(6, "Lexington-Fayette", "Lexington-Fayette", "Lexington-Fayette", "12.34567", "12.34567", "CA", "", "America/Montreal", 5);

        testList.add(Lexington);
        testList.add(Lexington2);
        testList.add(LexingtonPark);
        testList.add(Lexington4);
        testList.add(Lexington5);
        testList.add(LexingtonFayette);

        return testList;
    }

    /**
     * Test the default request with no coordinate.
     * @throws JSONException when creating the array.
     */
    @Test
    public void SortingWithInValidCoordinates() throws JSONException
    {
        Coordinate invalidCoordinate = new Coordinate(null, null);
        SuggestionScore sg = new SuggestionScore();
        String query = "lexingt";
        String limit = "20";

        List<GeoNameCity> listOfCities = sg.sortSuggestion(getInitialList(), invalidCoordinate, query);
        JSONArray preparedResultArray = sg.prepareResultArray(listOfCities, invalidCoordinate, limit);

        Double currentDistance = 0.0;
        String cityName = "Lexington";
        for (int i = 0; i < preparedResultArray.length(); i++)
        {
            JSONObject localJsonObject = preparedResultArray.getJSONObject(i);

            if (!localJsonObject.getString("name").equalsIgnoreCase(cityName))
            {
                currentDistance = 0.0;
                continue;
            }
            assertTrue(localJsonObject.getDouble("distance (in km)") > currentDistance);
        }
    }

    /**
     * Test the country filter.
     *
     * @throws JSONException when creatong the array
     */
    @Test
    public void filterByCountry() throws JSONException
    {
        //Vestavia Hills -> 33.4487° N, 86.7878° W -> United States
        Coordinate validCoordinate = new Coordinate("-86.7878", "33.4487");
        SuggestionScore sg = new SuggestionScore();
        String query = "lexing";
        String limit = "20";

        List<GeoNameCity> listOfCities = sg.sortSuggestion(getInitialList(), validCoordinate, query);
        listOfCities = sg.filterByCountry(listOfCities, validCoordinate, DataManager.getDataManagerInstance().getCities());
        JSONArray preparedResultArray = sg.prepareResultArray(listOfCities, validCoordinate, limit);

        String validCountry = "US";

        for (int i = 0; i < preparedResultArray.length(); i++)
        {
            JSONObject localJsonObject = preparedResultArray.getJSONObject(i);
            String countryName = localJsonObject.getString("name").split(",")[2].trim();
            assertTrue(countryName.equalsIgnoreCase(validCountry));
        }
    }

    /**
     * Test the closest city method, due to potential issues with location and distance calculation.
     */
    @Test
    public void identifyClosestCity()
    {
        SuggestionScore sg = new SuggestionScore();

        //        Vestavia Hills -> 33.4487° N, 86.7878° W -> United States
        Coordinate validCoordinate = new Coordinate("-86.7666", "33.4487");
        String closestCity = "Vestavia Hills";
        GeoNameCity vestaviaHills = sg.identifyClosestCity(validCoordinate, DataManager.getDataManagerInstance().getCities());
        assertTrue(vestaviaHills.getCountry().equalsIgnoreCase("US"));
        assertTrue(vestaviaHills.getName().equalsIgnoreCase(closestCity));

        //      Montreal, Canada ->  45.5017° N, 73.5673° W -> Canada
        Coordinate closeCoordinate = new Coordinate("-73.5555", "45.5017");
        String closestCityCanada = "Montréal";
        GeoNameCity montreal = sg.identifyClosestCity(closeCoordinate, DataManager.getDataManagerInstance().getCities());
        assertTrue(montreal.getCountry().equalsIgnoreCase("CA"));
        assertTrue(montreal.getName().equalsIgnoreCase(closestCityCanada));

    }

}