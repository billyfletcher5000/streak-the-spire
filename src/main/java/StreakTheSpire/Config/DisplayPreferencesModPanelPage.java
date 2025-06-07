package StreakTheSpire.Config;

import StreakTheSpire.Models.DisplayPreferencesModel;
import StreakTheSpire.StreakTheSpire;
import StreakTheSpire.Utils.FixedModLabel;
import StreakTheSpire.Utils.FixedModLabeledToggleButton;
import StreakTheSpire.Utils.LocalizationConstants;
import StreakTheSpire.Utils.LocalizationHelper;
import basemod.IUIElement;
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
    public String getTitleLocalizationID() { return LocalizationConstants.Config.DisplayPreferencesTitle; }

    @Override
    public void initialise(ModPanel modPanel, Vector2 contentTopLeft, Vector2 contentDimensions) {
        if(isInitialised)
            return;

        final float NumCheckboxenPerLine = 3.0f;
        UIStrings uiStrings = StreakTheSpire.get().getConfigUIStrings();

        DisplayPreferencesModel displayPreferencesModel = StreakTheSpire.get().getDisplayPreferencesModel();

        Vector2 elementPosition = contentTopLeft.cpy();
        IUIElement textColourButton = createBooleanPropElement(
                elementPosition.x,
                elementPosition.y,
                displayPreferencesModel.colouredStreakNumbers,
                LocalizationHelper.locDict(uiStrings, LocalizationConstants.Config.ColouredTextLabel),
                LocalizationHelper.locDict(uiStrings, LocalizationConstants.Config.ColouredTextTooltip),
                modPanel,
                (val) -> { StreakTheSpire.get().saveConfig(); }
        );
        addElement(textColourButton);

        elementPosition.y -= ConfigModPanel.LineHeight;
        IUIElement suppressSaveNotifButton = createBooleanPropElement(
                elementPosition.x,
                elementPosition.y,
                displayPreferencesModel.suppressSaveNotification,
                LocalizationHelper.locDict(uiStrings, LocalizationConstants.Config.SuppressSaveNotificationLabel),
                LocalizationHelper.locDict(uiStrings, LocalizationConstants.Config.SuppressSaveNotificationTooltip),
                modPanel,
                (val) -> { StreakTheSpire.get().saveConfig(); }
        );
        addElement(suppressSaveNotifButton);

        elementPosition.y -= ConfigModPanel.LineHeight;
        FixedModLabel renderLayerTitle = new FixedModLabel(
                LocalizationHelper.locDict(uiStrings, LocalizationConstants.Config.RenderLayerTitle),
                elementPosition.x,
                elementPosition.y,
                FontHelper.tipBodyFont,
                modPanel,
                (label) -> {}
        );
        addElement(renderLayerTitle);

        elementPosition.y -= ConfigModPanel.LineHeight;
        FixedModLabeledToggleButton button = new FixedModLabeledToggleButton(
                LocalizationHelper.locDict(uiStrings, LocalizationConstants.Config.RenderLayerPreRoomLabel),
                LocalizationHelper.locDict(uiStrings, LocalizationConstants.Config.RenderLayerPreRoomTooltip),
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
                LocalizationHelper.locDict(uiStrings, LocalizationConstants.Config.RenderLayerTopPanelLabel),
                LocalizationHelper.locDict(uiStrings, LocalizationConstants.Config.RenderLayerTopPanelTooltip),
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
                LocalizationHelper.locDict(uiStrings, LocalizationConstants.Config.RenderLayerAboveMostLabel),
                LocalizationHelper.locDict(uiStrings, LocalizationConstants.Config.RenderLayerAboveMostTooltip),
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
                LocalizationHelper.locDict(uiStrings, LocalizationConstants.Config.RenderLayerAboveAllLabel),
                LocalizationHelper.locDict(uiStrings, LocalizationConstants.Config.RenderLayerAboveAllTooltip),
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
        FixedModLabel defaultNoteLabel = new FixedModLabel(
                LocalizationHelper.locDict(uiStrings, LocalizationConstants.Config.DefaultNoteLabel),
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
