package Suggestion;

import Entity.GeoNameCity;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertTrue;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = MatchGeneratorTest.class)
public class MatchGeneratorTest
{
    /**
     * Helper method.
     *
     * @return list of cities
     */
    private List<GeoNameCity> getInitialList()
    {
        ArrayList<GeoNameCity> testmap = new ArrayList<>();
        GeoNameCity Danville = new GeoNameCity(1, "Danville", "Danville", "Danville", "11,11111", "11,11111", "US", "", "America/New York", 5);
        GeoNameCity Dayton = new GeoNameCity(2, "Dayton", "Dayton", "Dayton", "11,11111", "11,11111", "US", "", "America/New York", 5);
        GeoNameCity Douglass = new GeoNameCity(3, "Douglass", "Douglass", "Douglass", "11,11111", "11,11111", "US", "", "America/New York", 5);
        GeoNameCity Edgewood = new GeoNameCity(4, "Edgewood", "Edgewood", "Edgewood", "11,11111", "11,11111", "US", "", "America/New York", 5);
        GeoNameCity Dayton2 = new GeoNameCity(5, "Dayton", "Dayton", "Dayton", "11,11111", "11,11111", "US", "", "America/New York", 5);
        GeoNameCity Daytona = new GeoNameCity(6, "Daytona", "Daytona", "Daytona", "11,11111", "11,11111", "US", "", "America/New York", 5);
        GeoNameCity Danton3 = new GeoNameCity(7, "Dayton", "Dayton", "Dayton", "11,11111", "11,11111", "US", "", "America/New York", 5);

        testmap.add(Danville);
        testmap.add(Dayton);
        testmap.add(Douglass);
        testmap.add(Edgewood);
        testmap.add(Dayton2);
        testmap.add(Daytona);
        testmap.add(Danton3);

        return testmap;
    }

    /**
     * Validate the stream filter method and the eligible city method at the same time.
     */
    @Test
    public void reducedListTest()
    {
        String query = "dayton";
        int expectedMatches = 4;
        MatchGenerator mg = new MatchGenerator();
        List<GeoNameCity> reducedList = mg.reducedList(getInitialList(), query);

        assertTrue(reducedList.size() == expectedMatches);
    }

    /**
     * Emulates the big list of cities
     *
     * @return random list of cities
     */
    private String[] getListOfCities()
    {
        return new String[]{"Buechel",
                "cimco de mayo",
                "cinco dee mayo",
                "cemtraal",
                "        cinco de mayo        ",
                "CINCO DE MAYO",
                "cinco",
                "Cold Spring",
                "Covington",
                "Cynthiana"};
    }

    /**
     * @return list of queries that should pass due to filtering and word similarity
     */
    private String[] getValidQueries()
    {
        return new String[]{
                "cimco de mayo",
                "cinco dee mayo",
                "        cinco de mayo        ",
                "CINCO DE MAYO",
                "cinco"};
    }

    /**
     * Validates the method used to filter the cities.
     */
    @Test
    public void EligibleCitiesTest()
    {
        MatchGenerator matchGenerator = new MatchGenerator();
        ArrayList<String> filteredCities = new ArrayList<>();
        String cityName = "Cinco de mayo";

        for (String candidate : getListOfCities())
        {
            if (matchGenerator.eligibleCity(candidate, cityName))
                filteredCities.add(candidate);
        }

        assertTrue(filteredCities.size() > 0);
        for (String valid : getValidQueries())
        {
            assertTrue(filteredCities.contains(valid));
        }
    }


}