package StreakTheSpire.Config;

import basemod.IUIElement;
import basemod.ModPanel;
import com.badlogic.gdx.math.Vector2;

import java.util.ArrayList;

public class ConfigModPanel extends ModPanel {
    public static final Vector2 PageTopLeft = new Vector2(355.0f, 800.0f);
    public static final Vector2 PageDimensions = new Vector2(1400.0f - 355.0f, 730.0f - 140.0f);
    public static final float TitleLineHeight = 60.0f;
    public static final float LineHeight = 50.0f;

    private ConfigModPanelPage currentPage = null;
    private ArrayList<ConfigModPanelPage> pages = new ArrayList<>();
    private int pageIndex = -1;

    public ConfigModPanelPage getCurrentPage() { return currentPage; }
    public void setPageIndex(int pageIndex) {
        if(pageIndex != this.pageIndex && pageIndex >= 0 && pageIndex < pages.size()) {
            if(currentPage != null) {
                ArrayList<IUIElement> elements = currentPage.getElements();
                this.getRenderElements().removeAll(elements);
                this.getUpdateElements().removeAll(elements);
                currentPage = null;
            }

            this.pageIndex = pageIndex;
            currentPage = pages.get(pageIndex);

            if(currentPage != null) {
                ArrayList<IUIElement> elements = currentPage.getElements();
                for(IUIElement element : elements) {
                    addUIElement(element);
                }
            }
        }
    }

    public void addPage(ConfigModPanelPage page) {
        pages.add(page);
    }

    public void removePage(ConfigModPanelPage page) {
        pages.remove(page);
    }

    public ConfigModPanel() {
        super();
    }

    @Override
    public void onCreate() {
        for(ConfigModPanelPage page : pages) {
            page.initialise(this);
        }

        setPageIndex(0);
    }
}
