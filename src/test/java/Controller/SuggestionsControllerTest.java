package Controller;


import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
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
     * Checks for both the accuracy of the word similarity and the sorting of the distances when the name of the cities are similar.
     * e.g. Multiple lexingtons
     *
     * @throws Exception
     */
    @Test
    public void validSuggestionRequestWithInvalidLocation() throws Exception
    {
        String url = "/suggestions";
        String expected = "Invalid Parameters. Given: lexingt";

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

}