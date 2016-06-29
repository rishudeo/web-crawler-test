package service;

import http.PageAssets;
import http.WebPageParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sitemap.Sitemap;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutorService;

/**
 * Processes the links on a web page.
 *
 * @author Ravindra Rishudeo.
 */
public class UrlLinkProcessor implements Runnable {

    private static final Logger LOGGER = LoggerFactory.getLogger(UrlLinkProcessor.class);

    private final ExecutorService executorService;
    private final WebPageParser webPageParser;
    private final String url;
    private final Sitemap sitemap;

    /**
     * Constructor.
     *  @param executorService to process the child links retrieved from the web page
     * @param webPageParser
     * @param url the URL of the web page to retrieve
     * @param sitemap the sitemap
     */
    public UrlLinkProcessor(ExecutorService executorService, WebPageParser webPageParser, String url, Sitemap sitemap) {
        this.executorService = executorService;
        this.webPageParser = webPageParser;
        this.url = url;
        this.sitemap = sitemap;
    }

    @Override
    public void run() {
        if (!sitemap.hasBeenProcessed(url)) {
            PageAssets assetsFromPage = webPageParser.getAssetsFromPage(url);
            sitemap.addLinks(url, assetsFromPage.getAllAssets());

            assetsFromPage.getLinksToOtherPages().stream()
                    .filter(this::isSameDomain)
                    .map(link -> new UrlLinkProcessor(executorService, webPageParser, link, sitemap))
                    .forEach(executorService::execute);
        }
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
            LOGGER.error("The syntax of the link is incorrect, will not crawl.  URL: " + url, e);
            return "";
        }

    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UrlLinkProcessor that = (UrlLinkProcessor) o;
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
