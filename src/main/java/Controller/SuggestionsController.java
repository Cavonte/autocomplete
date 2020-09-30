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
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.List;


@RestController
public class SuggestionsController
{

    /**
     * Main endpoint. Handles request with and without a location.
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
                                                  @RequestParam(name = "latitude", required = false) String latitude,
                                                  @RequestParam(name = "limit", required = false) String limit) throws JSONException
    {
        String defaultLimit = "20";
        if (limit == null)
        {
            limit = defaultLimit;
        }

        if (invalidGetParameters(query, longitude, latitude, limit))
        {
            HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
            return ResponseEntity.badRequest().body("Invalid Parameters. Given: " + request.getQueryString());
        }

        DataManager localDataManager = DataManager.getDataManagerInstance();
        MatchGenerator matchGenerator = new MatchGenerator();
        List<GeoNameCity> filteredCities = matchGenerator.reducedList(localDataManager.getCities(), query);

        Coordinate location = new Coordinate(longitude,latitude);
        SuggestionScore suggestionScore = new SuggestionScore();
        List<GeoNameCity> storedSuggestion = suggestionScore.sortSuggestion(filteredCities, location, query);
        JSONArray result = suggestionScore.prepareResultArray(storedSuggestion, location, limit);

        System.out.println(result.toString());
        System.out.println("Request Ended.");

        return ResponseEntity.ok(result.toString());
    }

    /**
     * Returns true if one of the parameters is invalid, e.g. empty string
     *
     * @param query     from request
     * @param longitude of request
     * @param latitude  of request
     * @return boolean is the request invalid
     */
    private boolean invalidGetParameters(String query, String longitude, String latitude, String limit)
    {
        if (longitude == null && latitude == null)
        {
            return query.length() < 1 ||
                    NumberUtils.isParsable(query) ||
                    query.matches("\\d+") ||
                    !NumberUtils.isParsable(limit);
        }
        else
        {
            return longitude == null ||
                    latitude == null ||
                    query.length() < 1 ||
                    longitude.length() < 1 ||
                    latitude.length() < 1 ||
                    longitude.length() > 10 ||
                    latitude.length() > 10 ||
                    query.matches("\\d+") ||
                    NumberUtils.isParsable(query) ||
                    !NumberUtils.isParsable(longitude) ||
                    !NumberUtils.isParsable(latitude) ||
                    limit.length() > 8 ||
                    !NumberUtils.isParsable(limit);
        }
    }

    /**
     * Alternated endpoint. Result are filtered by figuring out the country of the request using the coordinates.
     *
     * @param query     for suggestion
     * @param longitude of request
     * @param latitude  request
     * @return JSON Array with suggestion
     * @throws JSONException At response creation
     */
    @RequestMapping(value = "/suggestionsByCountry", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity autoCompleteSuggestionsCountry(@RequestParam(name = "q") String query,
                                                         @RequestParam(name = "longitude") String longitude,
                                                         @RequestParam(name = "latitude") String latitude,
                                                         @RequestParam(name = "limit", required = false) String limit) throws JSONException
    {
        String defaultLimit = "20";
        if (limit == null)
        {
            limit = defaultLimit;
        }
        if (invalidGetParameters(query, longitude, latitude, limit))
        {
            HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
            return ResponseEntity.badRequest().body("Invalid Parameters. Given: " + request.getQueryString());
        }

        DataManager localDataManager = DataManager.getDataManagerInstance();
        MatchGenerator matchGenerator = new MatchGenerator();
        List<GeoNameCity> filteredCities = matchGenerator.reducedList(localDataManager.getCities(), query);

        Coordinate location = new Coordinate(longitude,latitude);
        SuggestionScore suggestionScore = new SuggestionScore();
        List<GeoNameCity> storedSuggestion = suggestionScore.sortSuggestion(filteredCities, location, query);
        storedSuggestion = suggestionScore.filterByCountry(storedSuggestion, location, localDataManager.getCities());

        JSONArray result = suggestionScore.prepareResultArray(storedSuggestion, location, limit);

        System.out.println(result.toString());
        System.out.println("Request Ended.");

        return ResponseEntity.ok(result.toString());
    }
}
