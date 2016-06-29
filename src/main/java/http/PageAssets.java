package http;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Holds the assets of a page.
 *
 * @author Ravindra Rishudeo.
 */
public class PageAssets {

    private final List<String> linksToOtherPages;
    private final List<String> staticContent;

    /**
     * Constructor.
     *
     * Creates a PageAssets object with empty links and static content.
     */
    public PageAssets() {
        linksToOtherPages = Collections.EMPTY_LIST;
        staticContent = Collections.EMPTY_LIST;
    }

    /**
     * Constructor.
     *
     * @param linksToOtherPages the links to other pages
     * @param staticContent the static content
     */
    public PageAssets(List<String> linksToOtherPages, List<String> staticContent) {
        this.linksToOtherPages = linksToOtherPages;
        this.staticContent = staticContent;
    }

    public List<String> getLinksToOtherPages() {
        return linksToOtherPages;
    }

    public List<String> getStaticContent() {
        return staticContent;
    }

    /**
     * Retireies all assets - both page links and static content.
     *
     * @return all assets
     */
    public List<String> getAllAssets() {
        List<String> allAssets = new ArrayList<>();
        allAssets.addAll(linksToOtherPages);
        allAssets.addAll(staticContent);

        return allAssets;
    }
}
