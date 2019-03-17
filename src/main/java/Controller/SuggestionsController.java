package Controller;


import Entity.GeoNameCity;
import Suggestion.DataManager;
import Suggestion.MatchGenerator;
import Suggestion.SuggestionScore;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


@RestController
public class SuggestionsController
{

    @RequestMapping(value = "/suggestions", method = RequestMethod.GET, produces = "Application/Text")
    public ResponseEntity autoCompleteSuggestions(@RequestParam(name="q")  String query,
                                                            @RequestParam(name="longitude", required = false) String longitude,
                                                            @RequestParam(name="latitude",required = false) String latitude) throws Exception
    {
        if(invalidGetParameters(query,longitude,latitude))
        {
            return ResponseEntity.badRequest().body("Invalid Parameters. Given:" + query + ".");
        }

        DataManager localDataManager = DataManager.getDataManagerInstance();
        MatchGenerator mg = new MatchGenerator();

        List<GeoNameCity> filteredCities = mg.reducedList(localDataManager.getCities(),query);

        for(Object temp: filteredCities)
        {
            System.out.println(temp);
        }

        SuggestionScore sc = new SuggestionScore();
        List storedSuggestion = sc.sortSuggestion(filteredCities,latitude,longitude,query);

        System.out.println("_________________________");
        for(Object temp: storedSuggestion)
        {
            System.out.println(temp);
        }        //        Create Json Structure

        {
            return ResponseEntity.ok("Temp");
        }
    }

    /**
     *
     * @param query
     * @param longitude
     * @param latitude
     * @return
     */
    private boolean invalidGetParameters(String query, String longitude, String latitude)
    {
        if(longitude == null && latitude == null)
        {
            return !query.matches("\\w+");
        }
        else
        {
            return longitude == null ||
                    latitude == null ||
                    query.matches("\\d+") ||
                    longitude.matches("\\w+") ||
                    latitude.matches("\\w+");
        }
    }
}
