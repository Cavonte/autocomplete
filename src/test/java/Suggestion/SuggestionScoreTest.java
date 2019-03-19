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
import java.util.HashMap;
import java.util.List;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = SuggestionScoreTest.class)
public class SuggestionScoreTest
{
    private ArrayList<GeoNameCity> getInitialList()
    {
        ArrayList<GeoNameCity> testList = new ArrayList<>();

        GeoNameCity Lexington = new GeoNameCity(4941935, "Lexington", "Lexington", "Lexington", "42.44732", "-71.2245", "US", "", "America/New York", 5);
        GeoNameCity Lexington2 = new GeoNameCity(4769339, "Lexington", "Lexington", "Lexington", "37.78402", "-79.44282", "US", "", "America/New York", 5);
        GeoNameCity LexingtonPark = new GeoNameCity(3, "Lexington Park", "Lexington Park", "Lexington Park", "11,11111", "11,11111", "US", "", "America/New York", 5);
        GeoNameCity Lexington4 = new GeoNameCity(4475773, "Lexington", "Lexington", "Lexington", "35.82403", "-80.25338", "US", "", "America/New York", 5);
        GeoNameCity Lexington5 = new GeoNameCity(4475773, "Lexington", "Lexington", "Lexington", "37.98869", "-84.47772", "US", "", "America/New York", 5);
        GeoNameCity LexingtonFayette = new GeoNameCity(6, "Lexington-Fayette", "Lexington-Fayette", "Lexington-Fayette", "11,11111", "11,11111", "US", "", "America/New York", 5);

        testList.add(Lexington);
        testList.add(Lexington2);
        testList.add(LexingtonPark);
        testList.add(Lexington4);
        testList.add(Lexington5);
        testList.add(LexingtonFayette);

        return testList;
    }

    @Test
    public void SortingWithValidCoordinates() throws JSONException
    {
        Coordinate invalidCoordinate = new Coordinate(null, null);
        SuggestionScore sg = new SuggestionScore();
        String query = "lexingt";

        List<GeoNameCity> listOfCities = sg.sortSuggestion(getInitialList(), invalidCoordinate, query);
        JSONArray preparedResultArray = sg.prepareResultArray(listOfCities, invalidCoordinate);

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

}