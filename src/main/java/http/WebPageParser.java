package http;

import exception.FailedToGetPageContentException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.stream.Collectors;

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
    public PageAssets getAssetsFromPage(String url) {
        try {
            String pageContent = httpClient.getPageContent(url);
            return getAssetsFromHtml(pageContent, url);

        } catch (FailedToGetPageContentException e) {
            LOGGER.error("Unable to get page content.", e);
            return new PageAssets();
        }
    }

    private PageAssets getAssetsFromHtml(String pageContent, String baseUrl) {
        Document document = Jsoup.parse(pageContent);
        document.setBaseUri(baseUrl);

        List<String> links = getLinksFromHtml(document);
        List<String> images = getImagesFromHtml(document);

        return new PageAssets(links, images);
    }

    private List<String> getLinksFromHtml(Document document) {
        Elements links = document.select("a[href]");

        return links.stream()
                .map(link -> link.attr("abs:href"))
                .collect(Collectors.toList());
    }

    private List<String> getImagesFromHtml(Document document) {
        Elements images = document.select("img[src~=(?i)\\.(png|jpe?g|gif)]");

        return images.stream()
                .map(image -> image.attr("src"))
                .collect(Collectors.toList());
    }


}
