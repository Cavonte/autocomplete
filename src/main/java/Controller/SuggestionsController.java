package Controller;


import Suggestion.DataManager;
import Suggestion.MatchGenerator;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;


@RestController
public class SuggestionsController
{

    @RequestMapping(value = "/suggestions", method = RequestMethod.GET, produces = "Application/Text")
    public ResponseEntity autoCompleteSuggestions(@RequestParam(name="q")  String query,
                                                            @RequestParam(name="longitude", required = false) String longitude,
                                                            @RequestParam(name="latitude",required = false) String latitude) throws Exception
    {
        //Check parameters
        if(invalidGetParameters(query,longitude,latitude))
        {
            return ResponseEntity.badRequest().body("Invalid Parameters. Given:" + query + ".");
        }

        DataManager localDataManager = DataManager.getDataManagerInstance();

        HashMap local = localDataManager.getCities();

        MatchGenerator temp = new MatchGenerator();

        temp.reducedList(local,query);
//        Generate Entities

//        Create Json Structure

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
