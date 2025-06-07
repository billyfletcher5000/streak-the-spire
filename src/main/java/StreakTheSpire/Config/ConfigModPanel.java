package StreakTheSpire.Config;

import StreakTheSpire.StreakTheSpire;
import StreakTheSpire.Utils.FixedModLabeledButton;
import StreakTheSpire.Utils.LocalizationHelper;
import basemod.IUIElement;
import basemod.ModPanel;
import com.badlogic.gdx.math.Vector2;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.helpers.FontHelper;
import com.megacrit.cardcrawl.localization.UIStrings;

import java.util.ArrayList;
import java.util.HashMap;

public class ConfigModPanel extends ModPanel {
    /*
    public static final Vector2 PageTopLeft = new Vector2(355.0f * Settings.xScale, 800.0f * Settings.yScale);
    public static final Vector2 PageDimensions = new Vector2((1400.0f - 355.0f) * Settings.xScale, (730.0f - 140.0f) * Settings.yScale);
    public static final float TabLineHeight = 50.0f * Settings.yScale;
    public static final float TitleLineHeight = 60.0f * Settings.yScale;
    public static final float LineHeight = 50.0f * Settings.yScale;
     */

    public static final Vector2 PageTopLeft = new Vector2(355.0f, 800.0f);
    public static final Vector2 PageDimensions = new Vector2((1400.0f - 355.0f), (730.0f - 140.0f));
    public static final float TabLineHeight = 50.0f;
    public static final float TitleLineHeight = 60.0f;
    public static final float LineHeight = 50.0f;


    private ConfigModPanelPage currentPage = null;
    private ArrayList<ConfigModPanelPage> pages = new ArrayList<>();
    private HashMap<ConfigModPanelPage, FixedModLabeledButton> pageButtons = new HashMap<>();
    private ConfigModPanelPage queuedNextPage = null;

    public ConfigModPanelPage getCurrentPage() { return currentPage; }
    public void setPageIndex(int pageIndex) {
        if(pageIndex >= 0 && pageIndex < pages.size()) {
            ConfigModPanelPage page = pages.get(pageIndex);
            if(page != null) {
                setPage(page);
            }
        }
    }

    public void setPage(ConfigModPanelPage page) {
        if (currentPage == page)
            return;

        queuedNextPage = page;
    }

    private void updatePage() {
        if(currentPage != null) {
            ArrayList<IUIElement> elements = currentPage.getElements();
            this.getRenderElements().removeAll(elements);
            this.getUpdateElements().removeAll(elements);

            FixedModLabeledButton modLabeledButton = pageButtons.get(currentPage);
            if(modLabeledButton != null) {
                modLabeledButton.color = Settings.CREAM_COLOR;
            }
        }

        currentPage = queuedNextPage;
        queuedNextPage = null;

        if(currentPage != null) {
            ArrayList<IUIElement> elements = currentPage.getElements();
            for(IUIElement element : elements) {
                addUIElement(element);
            }

            FixedModLabeledButton modLabeledButton = pageButtons.get(currentPage);
            if(modLabeledButton != null) {
                modLabeledButton.color = Settings.GOLD_COLOR;
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
    public void update() {
        super.update();
        if(queuedNextPage != null)
            updatePage();
    }

    @Override
    public void onCreate() {
        Vector2 topLeft = PageTopLeft.cpy();
        Vector2 offsetPageTopLeft = PageTopLeft.cpy().sub(0f, TabLineHeight);
        Vector2 offsetPageDimensions = PageDimensions.cpy().sub(0f, TabLineHeight);

        UIStrings uiStrings = StreakTheSpire.get().getConfigUIStrings();
        float xOffset = 0.0f;

        for(ConfigModPanelPage page : pages) {
            String pageTitle = LocalizationHelper.locDict(uiStrings, page.getTitleLocalizationID());
            FixedModLabeledButton tabButton = new FixedModLabeledButton(
                    pageTitle,
                    topLeft.x + xOffset,
                    topLeft.y,
                    Settings.CREAM_COLOR,
                    Settings.RED_TEXT_COLOR,
                    FontHelper.buttonLabelFont,
                    this,
                    btn -> setPage(page));
            addUIElement(tabButton);
            pageButtons.put(page, tabButton);

            page.initialise(this, offsetPageTopLeft, offsetPageDimensions);

            float tabButtonWidth = Math.max(0.0F, FontHelper.getSmartWidth(FontHelper.buttonLabelFont, pageTitle, 9999.0f, 0.0f) - 18.0f) + (70.0f * Settings.xScale);
            xOffset += (tabButtonWidth * (1.0f / Settings.xScale)) + 10f;
        }

        setPageIndex(0);
    }
}
