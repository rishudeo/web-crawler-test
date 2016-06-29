package http;


import exception.FailedToGetPageContentException;
import org.apache.http.HttpEntity;
import org.apache.http.ParseException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.util.EntityUtils;

import java.io.IOException;

/**
 * Retrieves the content of a web page.
 *
 * @author Ravindra Rishudeo.
 */
public class HttpClient {

    private final CloseableHttpClient httpClient;

    /**
     * Constructor.
     * @param httpClient the Apache HTTP Client with connection pooling
     */
    public HttpClient(CloseableHttpClient httpClient) {
        this.httpClient = httpClient;
    }


    /**
     * Retrieves the contents of a web page.
     *
     * @param url the URL of the web page
     * @return the content of the web page as a string.
     */
    public String getPageContent(String url) throws FailedToGetPageContentException {
        HttpGet httpGet = new HttpGet(url);

        try (CloseableHttpResponse response = httpClient.execute(httpGet)) {
            if (response.getStatusLine().getStatusCode() == 200) {
                HttpEntity entity = response.getEntity();

                return EntityUtils.toString(entity);

            } else {
                String errorMessage = String.format("Unable to retrieve page content for URL: %s.  Response code is: %d", url,
                        response.getStatusLine().getStatusCode());
                throw new FailedToGetPageContentException(errorMessage);
            }
        } catch (IOException ioe) {
            throw new FailedToGetPageContentException("Unable to get Page Content for URL: " + url, ioe);
        } catch (ParseException pe) {
            throw new FailedToGetPageContentException("Unable to parse Content for URL: " + url, pe);
        }
    }




}
