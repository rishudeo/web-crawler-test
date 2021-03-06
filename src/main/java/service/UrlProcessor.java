package service;

import http.PageAssets;
import http.WebPageParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sitemap.Sitemap;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Objects;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;

/**
 * Processes the links on a web page.
 *
 * @author Ravindra Rishudeo.
 */
public class UrlProcessor implements Callable<String> {

    private static final Logger LOGGER = LoggerFactory.getLogger(UrlProcessor.class);

    private final ExecutorService executorService;
    private final WebPageParser webPageParser;
    private final String url;
    private final Sitemap sitemap;

    /**
     * Constructor.
     * @param executorService to process the child links retrieved from the web page
     * @param webPageParser the web page HTML parser
     * @param url the URL of the web page to retrieve
     * @param sitemap the sitemap
     */
    public UrlProcessor(ExecutorService executorService, WebPageParser webPageParser, String url, Sitemap sitemap) {
        this.executorService = executorService;
        this.webPageParser = webPageParser;
        this.url = url;
        this.sitemap = sitemap;
    }

    @Override
    public String call() {
        if (!sitemap.hasBeenProcessed(url)) {
            LOGGER.debug("Processing URL: " + url);

            PageAssets assetsFromPage = webPageParser.getAssetsFromPage(url);
            sitemap.addLinks(url, assetsFromPage.getAllAssets());

            assetsFromPage.getLinksToOtherPages().stream()
                    .filter(this::isSameDomain)
                    .map(link -> new UrlProcessor(executorService, webPageParser, link, sitemap))
                    .forEach(executorService::submit);
        }
        return "complete";
    }

    private boolean isSameDomain(String linkUrl) {
        return getDomainName(url).equals(getDomainName(linkUrl));
    }

    private String getDomainName(String url) {
        try {
            URI uri = new URI(url);
            String domain = uri.getHost();
            return domain.startsWith("www.") ? domain.substring(4) : domain;

        } catch (URISyntaxException e) {
            LOGGER.error("The syntax of the link is incorrect, will not crawl.  URL: " + url);
            return "";
        }

    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UrlProcessor that = (UrlProcessor) o;
        return Objects.equals(executorService, that.executorService) &&
                Objects.equals(webPageParser, that.webPageParser) &&
                Objects.equals(url, that.url) &&
                Objects.equals(sitemap, that.sitemap);
    }

    @Override
    public int hashCode() {
        return Objects.hash(executorService, webPageParser, url, sitemap);
    }
}
