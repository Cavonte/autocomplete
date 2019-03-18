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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
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
     * Method Simplifier
     *
     * @param query
     * @param longitude
     * @param latitude
     * @param url
     * @param expected
     * @throws Exception
     */
    private void performCall(String query,
                             String longitude,
                             String latitude,
                             String url,
                             String expected) throws Exception
    {
        this.mockMvc.perform(get(url)
                .param("q", query)
                .param("longitude", longitude)
                .param("latitude", latitude)
                .accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk());
    }

    @Test
    public void validSuggestionRequestWithLocation() throws Exception
    {
        String url = "/suggestions";
        performCall("london", "45.5017", "73.5673", url, "");
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
        String expected = "Invalid Parameters. Given:";

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