package StreakTheSpire.Config;

import StreakTheSpire.Controllers.PlayerStreakStoreController;
import StreakTheSpire.Models.DisplayPreferencesModel;
import StreakTheSpire.Models.PlayerStreakStoreModel;
import StreakTheSpire.Models.StreakCriteriaModel;
import StreakTheSpire.StreakTheSpire;
import StreakTheSpire.Utils.FixedModLabel;
import StreakTheSpire.Utils.FixedModLabeledToggleButton;
import StreakTheSpire.Utils.LocalizationConstants;
import basemod.IUIElement;
import basemod.ModPanel;
import com.badlogic.gdx.math.Vector2;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.helpers.FontHelper;
import com.megacrit.cardcrawl.localization.UIStrings;

import java.util.HashMap;

public class CriteriaModPanelPage extends ConfigModPanelPage {
    private boolean isInitialised = false;

    private HashMap<DisplayPreferencesModel.RenderLayer, FixedModLabeledToggleButton> renderLayerToToggleButton = new HashMap<>();

    @Override
    public void initialise(ModPanel modPanel) {
        if(isInitialised)
            return;

        UIStrings uiStrings = StreakTheSpire.get().getConfigUIStrings();
        StreakCriteriaModel criteriaModel = StreakTheSpire.get().getStreakCriteriaModel();

        Vector2 elementPosition = ConfigModPanel.PageTopLeft.cpy();

        FixedModLabel title = new FixedModLabel(
                uiStrings.TEXT_DICT.get(LocalizationConstants.Config.CriteriaSettingsTitle),
                elementPosition.x,
                elementPosition.y,
                modPanel,
                (label) -> {}
        );
        addElement(title);


        elementPosition.y -= ConfigModPanel.TitleLineHeight;
        IUIElement requireHeartKillButton = createBooleanPropElement(
                elementPosition.x,
                elementPosition.y,
                criteriaModel.requireHeartKill,
                uiStrings.TEXT_DICT.get(LocalizationConstants.Config.RequireHeartKillLabel),
                modPanel,
                (val) -> recalculateStreaks()
        );
        addElement(requireHeartKillButton);

        elementPosition.y -= ConfigModPanel.LineHeight;
        IUIElement[] requiredAscensionInputElements = createIntPropElements(
                elementPosition.x,
                elementPosition.y,
                50.0f,
                ConfigModPanel.LineHeight * 0.8f,
                criteriaModel.requiredAscensionLevel,
                0,
                20,
                uiStrings.TEXT_DICT.get(LocalizationConstants.Config.RequiredAscensionLevelLabel),
                modPanel,
                (val) -> recalculateStreaks()
        );
        for(IUIElement element : requiredAscensionInputElements)
            addElement(element);

        elementPosition.y -= ConfigModPanel.LineHeight;
        IUIElement trackRotatingButton = createBooleanPropElement(
                elementPosition.x,
                elementPosition.y,
                criteriaModel.trackRotating,
                uiStrings.TEXT_DICT.get(LocalizationConstants.Config.TrackRotatingLabel),
                modPanel,
                (val) -> recalculateStreaks()
        );
        addElement(trackRotatingButton);

        elementPosition.y -= ConfigModPanel.TitleLineHeight;
        FixedModLabel allowTitle = new FixedModLabel(
                uiStrings.TEXT_DICT.get(LocalizationConstants.Config.AllowTitle),
                elementPosition.x,
                elementPosition.y,
                FontHelper.tipBodyFont,
                modPanel,
                (label) -> {}
        );
        addElement(allowTitle);

        final float NumCheckboxenPerLine = 3.0f;

        elementPosition.y -= ConfigModPanel.LineHeight;
        IUIElement allowCustomSeedsButton = createBooleanPropElement(
                elementPosition.x,
                elementPosition.y,
                criteriaModel.allowCustomSeeds,
                uiStrings.TEXT_DICT.get(LocalizationConstants.Config.AllowCustomSeedsLabel),
                modPanel,
                (val) -> recalculateStreaks()
        );
        addElement(allowCustomSeedsButton);

        elementPosition.x += (ConfigModPanel.PageDimensions.x / NumCheckboxenPerLine);
        IUIElement allowDailiesButton = createBooleanPropElement(
                elementPosition.x,
                elementPosition.y,
                criteriaModel.allowDailies,
                uiStrings.TEXT_DICT.get(LocalizationConstants.Config.AllowDailiesLabel),
                modPanel,
                (val) -> recalculateStreaks()
        );
        addElement(allowDailiesButton);

        elementPosition.x += (ConfigModPanel.PageDimensions.x / NumCheckboxenPerLine);
        IUIElement allowEndlessButton = createBooleanPropElement(
                elementPosition.x,
                elementPosition.y,
                criteriaModel.allowDailies,
                uiStrings.TEXT_DICT.get(LocalizationConstants.Config.AllowEndlessLabel),
                modPanel,
                (val) -> recalculateStreaks()
        );
        addElement(allowEndlessButton);

        elementPosition.x = ConfigModPanel.PageTopLeft.x;
        elementPosition.y -= ConfigModPanel.LineHeight;
        IUIElement allowBetaButton = createBooleanPropElement(
                elementPosition.x,
                elementPosition.y,
                criteriaModel.allowBeta,
                uiStrings.TEXT_DICT.get(LocalizationConstants.Config.AllowBetaLabel),
                modPanel,
                (val) -> recalculateStreaks()
        );
        addElement(allowBetaButton);

        elementPosition.x += (ConfigModPanel.PageDimensions.x / NumCheckboxenPerLine);
        IUIElement allowDemoButton = createBooleanPropElement(
                elementPosition.x,
                elementPosition.y,
                criteriaModel.allowDemo,
                uiStrings.TEXT_DICT.get(LocalizationConstants.Config.AllowDemoLabel),
                modPanel,
                (val) -> recalculateStreaks()
        );
        addElement(allowDemoButton);

        // TODO: Move this to DisplayPreferencesModPanelPage if we run out of space, also implementing paging properly
        elementPosition.x = ConfigModPanel.PageTopLeft.x;
        elementPosition.y -= ConfigModPanel.TitleLineHeight;
        FixedModLabel displayPreferencesTitle = new FixedModLabel(
                uiStrings.TEXT_DICT.get(LocalizationConstants.Config.DisplayPreferencesTitle),
                elementPosition.x,
                elementPosition.y,
                modPanel,
                (label) -> {}
        );
        addElement(displayPreferencesTitle);

        elementPosition.y -= ConfigModPanel.TitleLineHeight;
        FixedModLabel renderLayerTitle = new FixedModLabel(
                uiStrings.TEXT_DICT.get(LocalizationConstants.Config.RenderLayerTitle),
                elementPosition.x,
                elementPosition.y,
                FontHelper.tipBodyFont,
                modPanel,
                (label) -> {}
        );
        addElement(renderLayerTitle);

        DisplayPreferencesModel displayPreferencesModel = StreakTheSpire.get().getDisplayPreferencesModel();

        elementPosition.y -= ConfigModPanel.LineHeight;
        FixedModLabeledToggleButton button = new FixedModLabeledToggleButton(
                uiStrings.TEXT_DICT.get(LocalizationConstants.Config.RenderLayerPreRoomLabel),
                elementPosition.x,
                elementPosition.y,
                Settings.CREAM_COLOR,
                FontHelper.charDescFont,
                displayPreferencesModel.renderLayer.get() == DisplayPreferencesModel.RenderLayer.PreRoom,
                modPanel,
                (label) -> {},
                (toggleButton) -> {
                    if(toggleButton.enabled)
                        changeRenderLayer(DisplayPreferencesModel.RenderLayer.PreRoom);
                }
        );
        renderLayerToToggleButton.put(DisplayPreferencesModel.RenderLayer.PreRoom, button);
        addElement(button);

        elementPosition.x += (ConfigModPanel.PageDimensions.x / NumCheckboxenPerLine);
        button = new FixedModLabeledToggleButton(
                uiStrings.TEXT_DICT.get(LocalizationConstants.Config.RenderLayerTopPanelLabel),
                elementPosition.x,
                elementPosition.y,
                Settings.CREAM_COLOR,
                FontHelper.charDescFont,
                displayPreferencesModel.renderLayer.get() == DisplayPreferencesModel.RenderLayer.TopPanel,
                modPanel,
                (label) -> {},
                (toggleButton) -> {
                    if(toggleButton.enabled)
                        changeRenderLayer(DisplayPreferencesModel.RenderLayer.TopPanel);
                }
        );
        renderLayerToToggleButton.put(DisplayPreferencesModel.RenderLayer.TopPanel, button);
        addElement(button);

        elementPosition.x += (ConfigModPanel.PageDimensions.x / NumCheckboxenPerLine);
        button = new FixedModLabeledToggleButton(
                uiStrings.TEXT_DICT.get(LocalizationConstants.Config.RenderLayerAboveMostLabel),
                elementPosition.x,
                elementPosition.y,
                Settings.CREAM_COLOR,
                FontHelper.charDescFont,
                displayPreferencesModel.renderLayer.get() == DisplayPreferencesModel.RenderLayer.AboveMost,
                modPanel,
                (label) -> {},
                (toggleButton) -> {
                    if(toggleButton.enabled)
                        changeRenderLayer(DisplayPreferencesModel.RenderLayer.AboveMost);
                }
        );
        renderLayerToToggleButton.put(DisplayPreferencesModel.RenderLayer.AboveMost, button);
        addElement(button);

        elementPosition.x += (ConfigModPanel.PageDimensions.x / NumCheckboxenPerLine);
        button = new FixedModLabeledToggleButton(
                uiStrings.TEXT_DICT.get(LocalizationConstants.Config.RenderLayerAboveAllLabel),
                elementPosition.x,
                elementPosition.y,
                Settings.CREAM_COLOR,
                FontHelper.charDescFont,
                displayPreferencesModel.renderLayer.get() == DisplayPreferencesModel.RenderLayer.AboveAll,
                modPanel,
                (label) -> {},
                (toggleButton) -> {
                    if(toggleButton.enabled)
                        changeRenderLayer(DisplayPreferencesModel.RenderLayer.AboveAll);
                }
        );
        renderLayerToToggleButton.put(DisplayPreferencesModel.RenderLayer.AboveAll, button);
        addElement(button);

        elementPosition.x = ConfigModPanel.PageTopLeft.x;
        elementPosition.y = ConfigModPanel.PageTopLeft.y - ConfigModPanel.PageDimensions.y + 20f;
        FixedModLabel defaultNoteLabel = new FixedModLabel(uiStrings.TEXT_DICT.get(LocalizationConstants.Config.DefaultNoteLabel),
                elementPosition.x,
                elementPosition.y,
                FontHelper.tipBodyFont,
                modPanel,
                (label) -> {}
        );
        addElement(defaultNoteLabel);

        isInitialised = true;
    }

    private void recalculateStreaks() {
        // TODO: Replace with dirty/apply flow to reduce the cost of changing any setting and reprocessing, watch out
        //       for lack of knowing if this page has been closed.
        StreakCriteriaModel criteria = StreakTheSpire.get().getStreakCriteriaModel();
        PlayerStreakStoreModel model = StreakTheSpire.get().getStreakStoreDataModel();
        PlayerStreakStoreController controller = new PlayerStreakStoreController(model);
        controller.calculateStreakData(criteria, true);

        StreakTheSpire.get().saveConfig();
    }

    private void changeRenderLayer(DisplayPreferencesModel.RenderLayer renderLayer) {
        DisplayPreferencesModel displayPreferencesModel = StreakTheSpire.get().getDisplayPreferencesModel();
        displayPreferencesModel.renderLayer.set(renderLayer);

        renderLayerToToggleButton.forEach((layer, button) -> {
            button.toggle.enabled = layer == renderLayer;
        });

        StreakTheSpire.get().saveConfig();
    }
}
