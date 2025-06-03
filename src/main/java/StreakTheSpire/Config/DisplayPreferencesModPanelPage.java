package StreakTheSpire.Config;

import StreakTheSpire.Models.DisplayPreferencesModel;
import StreakTheSpire.Models.StreakCriteriaModel;
import StreakTheSpire.StreakTheSpire;
import StreakTheSpire.Utils.FixedModLabel;
import StreakTheSpire.Utils.FixedModLabeledToggleButton;
import StreakTheSpire.Utils.LocalizationConstants;
import basemod.ModPanel;
import com.badlogic.gdx.math.Vector2;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.helpers.FontHelper;
import com.megacrit.cardcrawl.localization.UIStrings;

import java.util.HashMap;

public class DisplayPreferencesModPanelPage extends ConfigModPanelPage {

    private boolean isInitialised = false;
    private HashMap<DisplayPreferencesModel.RenderLayer, FixedModLabeledToggleButton> renderLayerToToggleButton = new HashMap<>();

    @Override
    public String getTitleLocalizationID() { return "display_preferences_title"; }

    @Override
    public void initialise(ModPanel modPanel, Vector2 contentTopLeft, Vector2 contentDimensions) {
        if(isInitialised)
            return;

        final float NumCheckboxenPerLine = 3.0f;
        UIStrings uiStrings = StreakTheSpire.get().getConfigUIStrings();

        Vector2 elementPosition = contentTopLeft.cpy();

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
                uiStrings.TEXT_DICT.get(LocalizationConstants.Config.RenderLayerPreRoomTooltip),
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

        elementPosition.x += (contentDimensions.x / NumCheckboxenPerLine);
        button = new FixedModLabeledToggleButton(
                uiStrings.TEXT_DICT.get(LocalizationConstants.Config.RenderLayerTopPanelLabel),
                uiStrings.TEXT_DICT.get(LocalizationConstants.Config.RenderLayerTopPanelTooltip),
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

        elementPosition.x = contentTopLeft.x;
        elementPosition.y -= ConfigModPanel.LineHeight;
        button = new FixedModLabeledToggleButton(
                uiStrings.TEXT_DICT.get(LocalizationConstants.Config.RenderLayerAboveMostLabel),
                uiStrings.TEXT_DICT.get(LocalizationConstants.Config.RenderLayerAboveMostTooltip),
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

        elementPosition.x += (contentDimensions.x / NumCheckboxenPerLine);
        button = new FixedModLabeledToggleButton(
                uiStrings.TEXT_DICT.get(LocalizationConstants.Config.RenderLayerAboveAllLabel),
                uiStrings.TEXT_DICT.get(LocalizationConstants.Config.RenderLayerAboveAllTooltip),
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

        elementPosition.x = contentTopLeft.x;
        elementPosition.y = contentTopLeft.y - contentDimensions.y + 0f;
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

    private void changeRenderLayer(DisplayPreferencesModel.RenderLayer renderLayer) {
        DisplayPreferencesModel displayPreferencesModel = StreakTheSpire.get().getDisplayPreferencesModel();
        displayPreferencesModel.renderLayer.set(renderLayer);

        renderLayerToToggleButton.forEach((layer, button) -> {
            button.toggle.enabled = layer == renderLayer;
        });

        StreakTheSpire.get().saveConfig();
    }
}
