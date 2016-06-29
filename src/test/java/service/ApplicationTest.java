package service;

import http.WebPageParser;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import sitemap.Sitemap;

import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


/**
 * Tests for {@link Application}.
 */
@RunWith(MockitoJUnitRunner.class)
public class ApplicationTest {

    @Mock
    private ThreadPoolExecutor executorService;
    @Mock
    private WebPageParser webPageParser;

    private Sitemap sitemap;

    private Application application;

    @Before
    public void setUp() {
        sitemap = new Sitemap();
        this.application = new Application(executorService, sitemap, webPageParser);
    }

    @Test
    public void shouldTriggerACrawlOfTheSite() {
        String startUrl = "http://www.example.com";
        UrlProcessor urlProcessor = new UrlProcessor(executorService, webPageParser, startUrl, sitemap);

        this.application.crawlSite(startUrl);

        verify(executorService, times(1)).submit(urlProcessor);
    }

    @Test
    public void shouldWaitForSiteToBeCrawledAndThenTerminateExecutorService() throws Exception {
        String startUrl = "http://www.example.com";

        when(executorService.getActiveCount()).thenReturn(1).thenReturn(0);

        this.application.crawlSite(startUrl);

        verify(executorService, times(2)).getActiveCount();
        verify(executorService, times(1)).shutdown();
        verify(executorService, times(1)).awaitTermination(15, TimeUnit.SECONDS);
    }

    @Test
    public void shouldHandleInterruptedException() throws Exception {
        String startUrl = "http://www.example.com";

        when(executorService.awaitTermination(15, TimeUnit.SECONDS)).thenThrow(new InterruptedException());

        this.application.crawlSite(startUrl);

        // If no exception has been thrown, then it has been handled correctly.
        assertTrue(true);
    }


}