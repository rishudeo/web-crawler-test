package sitemap;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import com.google.common.collect.ImmutableMap;

/**
 * Contains the sitemap information.
 *
 * @author Ravindra Rishudeo.
 */
public class Sitemap {

    private final Map<String, List<String>> siteLinks;

    /**
     * Constructor.
     */
    public Sitemap() {
        this.siteLinks = new ConcurrentHashMap<>();
    }

    /**
     * Add links to the sitemap.
     *
     * @param url the url of the page
     * @param links the links from the page
     */
    public void addLinks(String url, List<String> links) {
        siteLinks.put(url, links);
    }

    /**
     * Indicates whether the links of a page have been retrieved already.
     *
     * After calling this method it is assumed that the caller will be processing the
     * page links for this URL.  Calling this method again with the same URL will
     * indicate that the page has been processed.  This means that if another thread tries to
     * process the same URL, the program will not process it twice.
     *
     * @param url the URL of the page
     * @return whether the links for this page have been processed
     */
    public boolean hasBeenProcessed(String url) {
        List<String> previousValue = siteLinks.putIfAbsent(url, Collections.emptyList());
        return previousValue != null;
    }


    public Map<String, List<String>> getSiteLinks() {
        return ImmutableMap.copyOf(siteLinks);
    }
}
