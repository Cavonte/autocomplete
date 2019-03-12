package Controller;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.util.NestedServletException;

import static org.junit.Assert.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;


@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class SuggestionsControllerTest
{

    @Autowired
    private MockMvc mockMvc;


    /**
     * Method Simplifier
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

        try
        {
            this.mockMvc.perform(get(url).param("q",query).param("longitude",longitude).param("latitude",latitude));
        } catch (NestedServletException e)
        {
            System.out.println("Expected : " + expected);
            System.out.println("Actual : " + e.getCause().getMessage());
            assertTrue(e.getCause().getMessage().equals(expected));
        }
    }

    @Test
    public void validSuggestionRequestWithLocation() throws Exception
    {
        String url = "/suggestions";
        performCall("london","42.98339","42.98339", url,"");
    }

    @Test
    public void validSuggestionRequestWithNoLocation() throws Exception
    {
        String url = "/suggestions";
        performCall("london","42.98339","42.98339", url,"");
    }

    @Test
    public void invalidSuggestionRequestNoParam() throws Exception
    {
        String url = "/suggestions";
        String expected = "";
        try
        {
            this.mockMvc.perform(get(url).param("q",""));
        } catch (NestedServletException e)
        {
//            System.out.println("Expected : " + expected);
//            System.out.println("Actual : " + e.getCause().getMessage());
            assertTrue(e.getCause().getMessage().equals(expected));
        }
    }

    @Test
    public void invalidSuggestionRequestInvalidParam() throws Exception
    {
        String url = "/suggestions";
        String expected = "";

        try
        {
            this.mockMvc.perform(get(url).param("q","11111111"));
        } catch (NestedServletException e)
        {
            System.out.println(e.getMessage());
//            System.out.println("Expected : " + expected);
//            System.out.println("Actual : " + e.getCause().getMessage());
            assertTrue(e.getCause().getMessage().equals(expected));
        }
    }

}