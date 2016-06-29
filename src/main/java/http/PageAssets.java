package http;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Holds the assets of a page.
 * @author Ravindra Rishudeo.
 */
public class PageAssets {

    private final List<String> linksToOtherPages;
    private final List<String> staticContent;

    public PageAssets() {
        linksToOtherPages = Collections.EMPTY_LIST;
        staticContent = Collections.EMPTY_LIST;
    }

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

    public List<String> getAllAssets() {
        List<String> allAssets = new ArrayList<>();
        allAssets.addAll(linksToOtherPages);
        allAssets.addAll(staticContent);

        return allAssets;
    }
}
