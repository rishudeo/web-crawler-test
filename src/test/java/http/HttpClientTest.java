package http;

import exception.FailedToGetPageContentException;
import org.apache.http.HttpEntity;
import org.apache.http.ParseException;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

/**
 * Tests for {@link HttpClient}
 */
@RunWith(MockitoJUnitRunner.class)
public class HttpClientTest {

    @Mock
    private CloseableHttpClient closeableHttpClient;
    @Mock
    private CloseableHttpResponse httpResponse;
    @Mock
    private HttpEntity httpEntity;
    @Mock
    private StatusLine statusLine;

    private HttpClient httpClient;


    @Before
    public void setUp() {
        httpClient = new HttpClient(closeableHttpClient);
    }

    @Test
    public void shouldReturnThePageContentAsAStringForAValidUrl() throws Exception {
        String url = "www.example.com";
        String expectedContent = "<html>example</html>";

        when(closeableHttpClient.execute(any(HttpGet.class))).thenReturn(httpResponse);
        when(httpResponse.getStatusLine()).thenReturn(statusLine);
        when(statusLine.getStatusCode()).thenReturn(200);
        when(httpResponse.getEntity()).thenReturn(httpEntity);
        when(httpEntity.getContent()).thenReturn(new ByteArrayInputStream(expectedContent.getBytes()));

        String content = httpClient.getPageContent(url);

        assertThat(content, is(expectedContent));
    }

    @Test(expected = FailedToGetPageContentException.class)
    public void shouldHandleNon200Response() throws Exception {
        when(closeableHttpClient.execute(any(HttpGet.class))).thenReturn(httpResponse);
        when(httpResponse.getStatusLine()).thenReturn(statusLine);
        when(statusLine.getStatusCode()).thenReturn(500);

        httpClient.getPageContent("");
    }

    @Test(expected = FailedToGetPageContentException.class)
    public void shouldHandleIOException() throws Exception {
        when(closeableHttpClient.execute(any(HttpGet.class))).thenThrow(new IOException());
        httpClient.getPageContent("");
    }

    @Test(expected = FailedToGetPageContentException.class)
    public void shouldHandleClientProtocolException() throws Exception {
        when(closeableHttpClient.execute(any(HttpGet.class))).thenThrow(new ClientProtocolException());
        httpClient.getPageContent("");
    }

    @Test(expected = FailedToGetPageContentException.class)
    public void shouldHandleParseException() throws Exception {
        when(closeableHttpClient.execute(any(HttpGet.class))).thenThrow(new ParseException());
        httpClient.getPageContent("");
    }
}