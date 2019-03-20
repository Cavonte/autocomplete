package Controller;


import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.math.NumberUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.hamcrest.Matchers.containsString;
import static org.junit.Assert.assertTrue;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class SuggestionsControllerTest
{
    @Autowired
    private MockMvc mockMvc;

    /**
     * See method name.
     *
     * @throws Exception
     */
    @Test
    public void validSuggestionRequestWithNonExistentCity() throws Exception
    {
        String url = "/suggestions";
        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders
                .get(url)
                .param("q", "St-Coincoin des Meumeux")
                .param("latitude", "45.5017")
                .param("longitude", "73.5673")
                .accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk())
                .andReturn();

        ObjectMapper mapper = new ObjectMapper();
        JsonNode[] suggestions = mapper.readValue(mvcResult.getResponse().getContentAsString(), JsonNode[].class);
        assertTrue(suggestions.length == 0);
    }

    /**
     * See method name.
     *
     * @throws Exception
     */
    @Test
    public void validSuggestionRequestWithEdgeCases() throws Exception
    {
        String url = "/suggestions";
        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders
                .get(url)
                .param("q", "St----Coincoin ボーリング des Meumeux")
                .param("latitude", "45.5011231237")
                .param("longitude", "73.567312312312")
                .accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk())
                .andReturn();

        ObjectMapper mapper = new ObjectMapper();
        JsonNode[] suggestions = mapper.readValue(mvcResult.getResponse().getContentAsString(), JsonNode[].class);
        assertTrue(suggestions.length == 0);
    }

    /**
     * Should fail if there are coordinates but one of them is invalid.
     * e.g. Multiple lexingtons
     *
     * @throws Exception
     */
    @Test
    public void validSuggestionRequestWithInvalidLocation() throws Exception
    {
        String url = "/suggestions";
        mockMvc.perform(MockMvcRequestBuilders
                .get(url)
                .param("q", "lexingt")
                .param("latitude", "45.5017")
                .param("longitude", "invalid")
                .accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isBadRequest());
    }

    /**
     * Checks for both the accuracy of the word similarity and the sorting of the distances when the name of the cities are similar.
     * e.g. Multiple lexingtons
     * Checks for requirements:
     * The endpoint returns a JSON response with an array of scored suggested matches
     * The suggestions are sorted by descending score
     *
     * @throws Exception
     */
    @Test
    public void validSuggestionRequestWithLocation() throws Exception
    {
        String url = "/suggestions";
        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders
                .get(url)
                .param("q", "lexingt")
                .param("latitude", "45.5017")
                .param("longitude", "73.5673")
                .accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk())
                .andReturn();

        ObjectMapper mapper = new ObjectMapper();
        JsonNode[] suggestions = mapper.readValue(mvcResult.getResponse().getContentAsString(), JsonNode[].class);

        assertTrue(suggestions[0].get("name").asText().toLowerCase().contains("lexington"));

        assertTrue(suggestions[0].get("distance (in km)").asDouble() < suggestions[1].get("distance (in km)").asDouble());
        assertTrue(suggestions[1].get("distance (in km)").asDouble() < suggestions[2].get("distance (in km)").asDouble());
        assertTrue(suggestions[2].get("distance (in km)").asDouble() < suggestions[3].get("distance (in km)").asDouble());
    }

    /**
     * Default behavior
     * Check for requirements:
     * The endpoint returns a JSON response with an array of scored suggested matches
     * Each suggestion has a name which can be used to disambiguate between similarly named locations
     * Each suggestion has a latitude and longitude
     * @throws Exception
     */
    @Test
    public void validSuggestionRequestWithNoLocation() throws Exception
    {
        String url = "/suggestions";

        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders
                .get(url)
                .param("q", "montr")
                .accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk())
                .andReturn();

        ObjectMapper mapper = new ObjectMapper();
        JsonNode[] suggestions = mapper.readValue(mvcResult.getResponse().getContentAsString(), JsonNode[].class);

        assertTrue(suggestions[0].get("name").asText().equalsIgnoreCase("Montréal, America/Montreal, CA"));
        assertTrue(suggestions[0].get("id").asText().equalsIgnoreCase("6077243"));
        assertTrue(suggestions[0].get("score").asText().equalsIgnoreCase("0.86"));
        assertTrue(NumberUtils.isParsable(suggestions[0].get("longitude").asText()));
        assertTrue(NumberUtils.isParsable(suggestions[0].get("latitude").asText()));
    }

    @Test
    public void validSuggestionRequestWithSmallLimit() throws Exception
    {
        String url = "/suggestions";

        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders
                .get(url)
                .param("q", "montr")
                .param("limit","2")
                .accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk())
                .andReturn();

        ObjectMapper mapper = new ObjectMapper();
        JsonNode[] suggestions = mapper.readValue(mvcResult.getResponse().getContentAsString(), JsonNode[].class);

        assertTrue(suggestions[0].get("name").asText().equalsIgnoreCase("Montréal, America/Montreal, CA"));
        assertTrue(suggestions[0].get("id").asText().equalsIgnoreCase("6077243"));
        assertTrue(suggestions[0].get("score").asText().equalsIgnoreCase("0.86"));
        assertTrue(suggestions.length == 2);
    }

    @Test
    public void invalidSuggestionRequestNoParam() throws Exception
    {
        String url = "/suggestions";

        mockMvc.perform(MockMvcRequestBuilders
                .get(url)
                .accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isBadRequest())
                .andExpect(status().reason(containsString("Required String parameter 'q' is not present")));
    }

    @Test
    public void invalidSuggestionRequestInvalidParam() throws Exception
    {
        String url = "/suggestions";
        String expected = "Invalid Parameters. Given: 11111111";

        mockMvc.perform(MockMvcRequestBuilders
                .get(url)
                .param("q", "11111111")
                .accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString(expected)));
    }

    @Test
    public void invalidSuggestionRequestInvalidLimitParam() throws Exception
    {
        String url = "/suggestions";
        String expected = "Invalid Parameters. Given: londo";

        mockMvc.perform(MockMvcRequestBuilders
                .get(url)
                .param("q", "londo")
                .param("limit","string")
                .accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString(expected)));
    }

    /**
     * Should fail if there are coordinates but one of them is invalid.
     * e.g. Multiple lexingtons
     *
     * @throws Exception
     */
    @Test
    public void invalidSuggestionByCountryRequest() throws Exception
    {
        String url = "/suggestionsByCountry";
        mockMvc.perform(MockMvcRequestBuilders
                .get(url)
                .param("q", "lexingt")
                .accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void validSuggestionByCountryRequest() throws Exception
    {
        String url = "/suggestionsByCountry";
        String validCountry = "CA";

        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders
                .get(url)
                .param("q", "montr")
                .param("latitude", "45.5017")
                .param("longitude", "73.5673")
                .accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk())
                .andReturn();

        ObjectMapper mapper = new ObjectMapper();
        JsonNode[] suggestions = mapper.readValue(mvcResult.getResponse().getContentAsString(), JsonNode[].class);

        assertTrue(suggestions[0].get("name").asText().equalsIgnoreCase("Montréal, America/Montreal, CA"));
        assertTrue(suggestions[0].get("id").asText().equalsIgnoreCase("6077243"));
        assertTrue(suggestions[0].get("score").asText().equalsIgnoreCase("0.67"));

        for(JsonNode node: suggestions)
        {
            String nodeCountry = node.get("name").asText().split(",")[2].trim();
            assertTrue(nodeCountry.equalsIgnoreCase(validCountry));
        }
    }
}