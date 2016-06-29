package service;

import http.HttpClient;
import http.WebPageParser;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sitemap.Sitemap;

import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Application Entry point.
 *
 * @author Ravindra Rishudeo
 */
public class Application {

    private static final Logger LOGGER = LoggerFactory.getLogger(Application.class);

    private static final int NUMBER_OF_THREADS_IN_POOL = 10;
    private static final int NUMBER_OF_CONECTIONS_IN_POOL = 10;

    private final ThreadPoolExecutor executorService;
    private final Sitemap sitemap;
    private final WebPageParser webPageParser;

    /**
     * Constructor.
     *
     * NB. If I did not have any time constraints then I would use
     * wire the beans with a framework.  Possibly Spring.
     */
    public Application() {
        this.executorService = createExecutorService();
        this.webPageParser = createWebPageParser();
        this.sitemap = new Sitemap();
    }

    Application(ThreadPoolExecutor executorService, Sitemap sitemap, WebPageParser webPageParser) {
        this.executorService = executorService;
        this.sitemap = sitemap;
        this.webPageParser = webPageParser;
    }

    /**
     * Crawls the site starting with the specified URL.
     *
     * @param startUrl the URL to start crawling
     */
    public void crawlSite(String startUrl) {
        startCrawlingSite(startUrl);

        waitUntilSiteHasBeenCrawled();

        shutDownExecutor();

        LOGGER.info(sitemap.toString());
    }

    private void startCrawlingSite(String startUrl) {
        UrlProcessor urlProcessor = new UrlProcessor(executorService, webPageParser, startUrl, sitemap);
        executorService.submit(urlProcessor);
    }

    private void shutDownExecutor() {
        try {
            executorService.shutdown();
            executorService.awaitTermination(15, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            LOGGER.error("Stopping executor service has been interrupted.", e);
        }
    }

    private void waitUntilSiteHasBeenCrawled() {
        while (executorService.getActiveCount() > 0) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                LOGGER.error("Crawling has been interrupted.", e);
            }
        }
    }

    private WebPageParser createWebPageParser() {
        return new WebPageParser(createHttpClient());
    }

    private HttpClient createHttpClient() {
        PoolingHttpClientConnectionManager connectionManager = new PoolingHttpClientConnectionManager();
        connectionManager.setMaxTotal(NUMBER_OF_CONECTIONS_IN_POOL);
        connectionManager.setDefaultMaxPerRoute(NUMBER_OF_CONECTIONS_IN_POOL);

        CloseableHttpClient apacheHttpClient = HttpClients.custom()
                .setConnectionManager(connectionManager)
                .build();

        return new HttpClient(apacheHttpClient);
    }

    private ThreadPoolExecutor createExecutorService() {
        return (ThreadPoolExecutor) Executors.newFixedThreadPool(NUMBER_OF_THREADS_IN_POOL);
    }

    public static void main(String[] args) {
        String startUrl = "http://www.nice.agency";

        Application application = new Application();
        application.crawlSite(startUrl);

    }
}
