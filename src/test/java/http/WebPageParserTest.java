package http;

import exception.FailedToGetPageContentException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.when;

/**
 * Tests for {@link WebPageParser}
 */
@RunWith(MockitoJUnitRunner.class)
public class WebPageParserTest {

    @Mock
    private HttpClient httpClient;

    private WebPageParser webPageParser;

    @Before
    public void setUp() {
        webPageParser = new WebPageParser(httpClient);
    }

    @Test
    public void shouldReturnASingleLinkFromAPage() throws Exception {
        String url = "http://www.example.com";
        String content = new StringBuilder()
                .append("<html>")
                .append("<a href=\"http://www.example.com/link1\">link</a>")
                .append("</html>")
                .toString();

        when(httpClient.getPageContent(url)).thenReturn(content);

        List<String> linksFromPage = webPageParser.getAssetsFromPage(url).getLinksToOtherPages();

        assertThat(linksFromPage.size(), is(1));
        assertThat(linksFromPage.get(0), is("http://www.example.com/link1"));
    }

    @Test
    public void shouldReturnMultipleLinksFromAPage() throws Exception {
        String url = "http://www.example.com";
        String content = new StringBuilder()
                .append("<html>")
                .append("<a href=\"http://www.example.com/link1\">link</a>")
                .append("<a href=\"http://www.example.com/link2\">link</a>")
                .append("</html>")
                .toString();

        when(httpClient.getPageContent(url)).thenReturn(content);

        List<String> linksFromPage = webPageParser.getAssetsFromPage(url).getLinksToOtherPages();

        assertThat(linksFromPage.size(), is(2));
        assertThat(linksFromPage.get(0), is("http://www.example.com/link1"));
        assertThat(linksFromPage.get(1), is("http://www.example.com/link2"));
    }

    @Test
    public void shouldReturnEmptyListFromAPageWithoutLinks() throws Exception {
        String url = "http://www.example.com";
        String content = new StringBuilder()
                .append("<html>")
                .append("</html>")
                .toString();

        when(httpClient.getPageContent(url)).thenReturn(content);

        List<String> linksFromPage = webPageParser.getAssetsFromPage(url).getLinksToOtherPages();

        assertThat(linksFromPage.size(), is(0));
    }

    @Test
    public void shouldReturnAFullUrlFromARelativeLinkUrl() throws Exception {
        String url = "http://www.example.com";
        String content = new StringBuilder()
                .append("<html>")
                .append("<a href=\"/link1\">link</a>")
                .append("</html>")
                .toString();

        when(httpClient.getPageContent(url)).thenReturn(content);

        List<String> linksFromPage = webPageParser.getAssetsFromPage(url).getLinksToOtherPages();

        assertThat(linksFromPage.size(), is(1));
        assertThat(linksFromPage.get(0), is("http://www.example.com/link1"));
    }

    @Test
    public void shouldReturnAFullUrlFromARelativeLinkUrlThatIsNotTheRootUrl() throws Exception {
        String url = "http://www.example.com/link1";
        String content = new StringBuilder()
                .append("<html>")
                .append("<a href=\"/link2\">link</a>")
                .append("</html>")
                .toString();

        when(httpClient.getPageContent(url)).thenReturn(content);

        List<String> linksFromPage = webPageParser.getAssetsFromPage(url).getLinksToOtherPages();

        assertThat(linksFromPage.size(), is(1));
        assertThat(linksFromPage.get(0), is("http://www.example.com/link2"));
    }

    @Test
    public void shouldReturnAFullUrlFromARelativeImageUrl() throws Exception {
        String url = "http://www.example.com";
        String content = new StringBuilder()
                .append("<html>")
                .append("<img src=\"/image.jpg\"")
                .append("</html>")
                .toString();

        when(httpClient.getPageContent(url)).thenReturn(content);

        List<String> staticContent = webPageParser.getAssetsFromPage(url).getStaticContent();

        assertThat(staticContent.size(), is(1));
        assertThat(staticContent.get(0), is("http://www.example.com/image.jpg"));
    }

    @Test
    public void shouldReturnAnEmptyListWhenUnableToGetThePageContent() throws Exception {
        String url = "http://www.example.com";

        when(httpClient.getPageContent(url)).thenThrow(new FailedToGetPageContentException(""));

        List<String> linksFromPage = webPageParser.getAssetsFromPage(url).getLinksToOtherPages();

        assertThat(linksFromPage.size(), is(0));
    }

    @Test
    public void shouldReturnImagesFromAPage() throws Exception {
        String url = "http://www.example.com";
        String content = new StringBuilder()
                .append("<html>")
                .append("<img src=\"http://www.example.com/image.jpg\"")
                .append("</html>")
                .toString();

        when(httpClient.getPageContent(url)).thenReturn(content);

        List<String> linksFromPage = webPageParser.getAssetsFromPage(url).getStaticContent();

        assertThat(linksFromPage.size(), is(1));
        assertThat(linksFromPage.get(0), is("http://www.example.com/image.jpg"));
    }

}