package Controller;


import Entity.Coordinate;
import Entity.GeoNameCity;
import Suggestion.DataManager;
import Suggestion.MatchGenerator;
import Suggestion.SuggestionScore;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.boot.configurationprocessor.json.JSONArray;
import org.springframework.boot.configurationprocessor.json.JSONException;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


@RestController
public class SuggestionsController
{

    /**
     * Main enpoint. Handles request with and without a location.
     *
     * @param query     for suggestion
     * @param longitude of request
     * @param latitude  request
     * @return JSON Array with suggestion
     * @throws JSONException At response creation
     */
    @RequestMapping(value = "/suggestions", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity autoCompleteSuggestions(@RequestParam(name = "q") String query,
                                                  @RequestParam(name = "longitude", required = false) String longitude,
                                                  @RequestParam(name = "latitude", required = false) String latitude) throws JSONException
    {
        if (invalidGetParameters(query, longitude, latitude))
        {
            return ResponseEntity.badRequest().body("Invalid Parameters. Given: " + query);
        }

        DataManager localDataManager = DataManager.getDataManagerInstance();
        MatchGenerator mg = new MatchGenerator();

        List<GeoNameCity> filteredCities = mg.reducedList(localDataManager.getCities(), query);

        SuggestionScore sc = new SuggestionScore();
        JSONArray result;

        if (latitude != null && longitude != null)
        {
            Coordinate location = new Coordinate(latitude, longitude);
            List<GeoNameCity> storedSuggestion = sc.sortSuggestion(filteredCities, location, query);
            result = sc.prepareResultArray(storedSuggestion, location);
        }
        else
        {
            List<GeoNameCity> storedSuggestion = sc.sortSuggestion(filteredCities, null, query);
            result = sc.prepareResultArray(storedSuggestion, null);
        }

        System.out.println(result.toString(1));
        System.out.println("Request Ended.");

        return ResponseEntity.ok(result.toString());
    }

    /**
     * Returns true if one of the parameters is invalid, e.g. empty string     *
     *
     * @param query     from request
     * @param longitude of request
     * @param latitude  of request
     * @return boolean is the request invalid
     */
    private boolean invalidGetParameters(String query, String longitude, String latitude)
    {
        if (longitude == null && latitude == null)
        {
            return query.length() < 1 ||
                    NumberUtils.isParsable(query) ||
                    !query.matches("\\w+");
        }
        else
        {

            return longitude == null ||
                    latitude == null ||
                    query.length() < 1 ||
                    longitude.length() < 1 ||
                    latitude.length() < 1 ||
                    NumberUtils.isParsable(query) ||
                    !NumberUtils.isParsable(longitude) ||
                    !NumberUtils.isParsable(longitude);
        }
    }
}
