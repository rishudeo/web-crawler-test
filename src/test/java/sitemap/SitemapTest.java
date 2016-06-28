package sitemap;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * Tests for {@link Sitemap}.
 */
public class SitemapTest {

    private Sitemap sitemap;

    @Before
    public void setUp() {
        sitemap = new Sitemap();
    }

    @Test
    public void shouldIndicateThatAPageHasNotBeenProcessedForANewUrl() {
        boolean hasBeenProcessed = sitemap.hasBeenProcessed("http://www.example.com/link1");
        assertThat(hasBeenProcessed, is(false));
    }

    @Test
    public void shouldIndicateThatAPageHasBeenProcessedForADuplicateUrl() {
        String url = "http://www.example.com/link1";
        sitemap.addLinks(url, new ArrayList<>());

        boolean hasBeenProcessed = sitemap.hasBeenProcessed(url);

        assertThat(hasBeenProcessed, is(true));
    }

    @Test
    public void shouldIndicateThatAPageHasBeenProcessedIfCheckedMoreThanOnce() {
        String url = "http://www.example.com/link1";

        boolean check1 = sitemap.hasBeenProcessed(url);
        boolean check2 = sitemap.hasBeenProcessed(url);

        assertThat(check1, is(false));
        assertThat(check2, is(true));
    }

}