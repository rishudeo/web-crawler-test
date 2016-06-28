package http;

import exception.FailedToGetPageContentException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Retrieves the content of a web page and parses the content.
 *
 * @author Ravindra Rishudeo.
 */
public class WebPageParser {

    private static final Logger LOGGER = LoggerFactory.getLogger(WebPageParser.class);

    private final HttpClient httpClient;

    /**
     * Constructor.
     * @param httpClient to retrieve the page contents
     */
    public WebPageParser(HttpClient httpClient) {
        this.httpClient = httpClient;
    }

    /**
     * Retrieve the links from a web page.
     *
     * @param url the URL of the web page
     * @return a List of link URLs
     */
    public List<String> getLinksFromPage(String url) {
        try {
            String pageContent = httpClient.getPageContent(url);
            return getLinksFromHtml(pageContent, url);

        } catch (FailedToGetPageContentException e) {
            LOGGER.error("Unable to get page content.", e);
            return new ArrayList<>();
        }
    }

    private List<String> getLinksFromHtml(String pageContent, String baseUrl) {
        Document document = Jsoup.parse(pageContent);
        document.setBaseUri(baseUrl);

        Elements links = document.select("a[href]");

        return links.stream()
                .map(link -> link.attr("abs:href"))
                .collect(Collectors.toList());
    }
}
