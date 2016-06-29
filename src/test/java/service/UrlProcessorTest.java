package service;

import http.PageAssets;
import http.WebPageParser;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import sitemap.Sitemap;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutorService;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;


/**
 * Tests for {@link UrlProcessor}
 */
@RunWith(MockitoJUnitRunner.class)
public class UrlProcessorTest {

    @Mock
    private ExecutorService executorService;
    @Mock
    private WebPageParser webPageParser;

    private Sitemap sitemap;

    @Before
    public void setUp() {
        sitemap = new Sitemap();
    }

    @Test
    public void shouldAddAllAssetsToSitemapAfterProcessingPage() {
        String url = "http://www.example.com";
        List<String> links = new ArrayList<>();
        links.add("http://www.example.com/link1");

        List<String> staticContent = new ArrayList<>();
        staticContent.add("http://www.example.com/image1.jpg");

        when(webPageParser.getAssetsFromPage(url)).thenReturn(new PageAssets(links, staticContent));

        UrlProcessor urlProcessor = new UrlProcessor(executorService, webPageParser, url, sitemap);

        urlProcessor.call();

        List<String> siteMapLinks = sitemap.getSiteLinks().get(url);

        assertThat(siteMapLinks.size(), is(2));
        assertThat(siteMapLinks.get(0), is("http://www.example.com/link1"));
        assertThat(siteMapLinks.get(1), is("http://www.example.com/image1.jpg"));
    }

    @Test
    public void shouldProcessLinksFromPage() {
        String url = "http://www.example.com";
        String link1 = "http://www.example.com/link1";
        String link2 = "http://www.example.com/link2";
        List<String> links = new ArrayList<>();
        links.add(link1);
        links.add(link2);

        when(webPageParser.getAssetsFromPage(url)).thenReturn(new PageAssets(links, Collections.EMPTY_LIST));

        UrlProcessor urlProcessor = new UrlProcessor(executorService, webPageParser, url, sitemap);

        urlProcessor.call();

        verify(executorService, times(1)).submit(new UrlProcessor(executorService, webPageParser, link1, sitemap));
        verify(executorService, times(1)).submit(new UrlProcessor(executorService, webPageParser, link2, sitemap));
    }

    @Test
    public void shouldNotProcessPageIfItHasAlreadyBeenProcessed() {
        String url = "http://www.example.com";
        String link1 = "http://www.example.com/link1";
        List<String> links = new ArrayList<>();
        links.add(link1);

        when(webPageParser.getAssetsFromPage(url)).thenReturn(new PageAssets(links, Collections.EMPTY_LIST));

        sitemap.addLinks(url, new ArrayList<>());
        UrlProcessor urlProcessor = new UrlProcessor(executorService, webPageParser, url, sitemap);

        urlProcessor.call();

        verifyZeroInteractions(executorService);
    }

    @Test
    public void shouldNotFollowLinksToOtherDomains() {
        String url = "http://www.example.com";
        String link1 = "http://www.somewhereelse.com/link1";
        List<String> links = new ArrayList<>();
        links.add(link1);

        when(webPageParser.getAssetsFromPage(url)).thenReturn(new PageAssets(links, Collections.EMPTY_LIST));

        UrlProcessor urlProcessor = new UrlProcessor(executorService, webPageParser, url, sitemap);

        urlProcessor.call();

        verifyZeroInteractions(executorService);
    }

    @Test
    public void shouldNotFollowImageLinks() {
        String url = "http://www.example.com";
        String image = "http://www.somewhereelse.com/image.jpg";
        List<String> images = new ArrayList<>();
        images.add(image);

        when(webPageParser.getAssetsFromPage(url)).thenReturn(new PageAssets(Collections.EMPTY_LIST, images));

        UrlProcessor urlProcessor = new UrlProcessor(executorService, webPageParser, url, sitemap);

        urlProcessor.call();

        verifyZeroInteractions(executorService);
    }



}