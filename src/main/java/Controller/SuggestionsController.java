package Controller;


import Entity.Coordinate;
import Entity.GeoNameCity;
import Suggestion.DataManager;
import Suggestion.MatchGenerator;
import Suggestion.SuggestionScore;
import org.springframework.boot.configurationprocessor.json.JSONArray;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


@RestController
public class SuggestionsController
{

    @RequestMapping(value = "/suggestions", method = RequestMethod.GET, produces = "Application/Text")
    public ResponseEntity autoCompleteSuggestions(@RequestParam(name = "q") String query,
                                                  @RequestParam(name = "longitude", required = false) String longitude,
                                                  @RequestParam(name = "latitude", required = false) String latitude) throws Exception
    {
        if (invalidGetParameters(query, longitude, latitude))
        {
            return ResponseEntity.badRequest().body("Invalid Parameters. Given:" + query + ".");
        }

        Coordinate location = new Coordinate(latitude,longitude);
        DataManager localDataManager = DataManager.getDataManagerInstance();
        MatchGenerator mg = new MatchGenerator();

        List<GeoNameCity> filteredCities = mg.reducedList(localDataManager.getCities(), query);
//
//        for (GeoNameCity temp : filteredCities)
//        {
//            System.out.println(temp.getName() + ", " + temp.getTimeZone() + ", " + temp.getId());
//        }
        SuggestionScore sc = new SuggestionScore();
        List<GeoNameCity> storedSuggestion = sc.sortSuggestion(filteredCities, location, query);

        JSONArray result = sc.prepareResultArray(storedSuggestion,location);

        System.out.println("_________________________");

        return ResponseEntity.ok(result);
    }

    /**
     * @param query
     * @param longitude
     * @param latitude
     * @return
     */
    private boolean invalidGetParameters(String query, String longitude, String latitude)
    {
        if (longitude == null && latitude == null)
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
