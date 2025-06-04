package StreakTheSpire.Config;

import StreakTheSpire.Controllers.PlayerStreakStoreController;
import StreakTheSpire.Models.DisplayPreferencesModel;
import StreakTheSpire.Models.PlayerStreakStoreModel;
import StreakTheSpire.Models.StreakCriteriaModel;
import StreakTheSpire.StreakTheSpire;
import StreakTheSpire.Utils.FixedModLabel;
import StreakTheSpire.Utils.FixedModLabeledToggleButton;
import StreakTheSpire.Utils.LocalizationConstants;
import StreakTheSpire.Utils.TextureCache;
import basemod.*;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import com.megacrit.cardcrawl.helpers.FontHelper;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.localization.UIStrings;

import java.util.HashMap;

public class CriteriaModPanelPage extends ConfigModPanelPage {

    private static final String minButtonNormalPath = "StreakTheSpire/textures/ui/0_button_normal.png";
    private static final String minButtonHoveredPath = "StreakTheSpire/textures/ui/0_button_hovered.png";
    private static final String minButtonPressedPath = "StreakTheSpire/textures/ui/0_button_pressed.png";
    private static final String maxButtonNormalPath = "StreakTheSpire/textures/ui/20_button_normal.png";
    private static final String maxButtonHoveredPath = "StreakTheSpire/textures/ui/20_button_hovered.png";
    private static final String maxButtonPressedPath = "StreakTheSpire/textures/ui/20_button_pressed.png";

    private boolean isInitialised = false;
    private HashMap<DisplayPreferencesModel.RenderLayer, FixedModLabeledToggleButton> renderLayerToToggleButton = new HashMap<>();

    @Override
    public String getTitleLocalizationID() { return LocalizationConstants.Config.CriteriaSettingsTitle; }

    @Override
    public void initialise(ModPanel modPanel, Vector2 contentTopLeft, Vector2 contentDimensions) {
        if(isInitialised)
            return;

        final float NumCheckboxenPerLine = 3.0f;

        UIStrings uiStrings = StreakTheSpire.get().getConfigUIStrings();
        StreakCriteriaModel criteriaModel = StreakTheSpire.get().getStreakCriteriaModel();

        Vector2 elementPosition = contentTopLeft.cpy();
        IUIElement requireHeartKillButton = createBooleanPropElement(
                elementPosition.x,
                elementPosition.y,
                criteriaModel.requireHeartKill,
                uiStrings.TEXT_DICT.get(LocalizationConstants.Config.RequireHeartKillLabel),
                uiStrings.TEXT_DICT.get(LocalizationConstants.Config.RequireHeartKillTooltip),
                modPanel,
                (val) -> recalculateStreaks()
        );
        addElement(requireHeartKillButton);

        TextureCache textureCache = StreakTheSpire.get().getTextureCache();
        Texture minNormalTex = textureCache.getTexture(minButtonNormalPath);
        Texture minHoveredTex = textureCache.getTexture(minButtonHoveredPath);
        Texture minPressedTex = textureCache.getTexture(minButtonPressedPath);
        Texture maxNormalTex = textureCache.getTexture(maxButtonNormalPath);
        Texture maxHoveredTex = textureCache.getTexture(maxButtonHoveredPath);
        Texture maxPressedTex = textureCache.getTexture(maxButtonPressedPath);

        elementPosition.y -= ConfigModPanel.LineHeight;
        IUIElement[] requiredAscensionInputElements = createIntPropButtonsElements(
                elementPosition.x,
                elementPosition.y,
                criteriaModel.requiredAscensionLevel,
                0,
                20,
                uiStrings.TEXT_DICT.get(LocalizationConstants.Config.RequiredAscensionLevelLabel),
                minNormalTex,
                minHoveredTex,
                minPressedTex,
                maxNormalTex,
                maxHoveredTex,
                maxPressedTex,
                modPanel,
                (val) -> recalculateStreaks()
        );
        for(IUIElement element : requiredAscensionInputElements)
            addElement(element);

        elementPosition.y -= ConfigModPanel.LineHeight;
        IUIElement enforceRotatingButton = createBooleanPropElement(
                elementPosition.x,
                elementPosition.y,
                criteriaModel.enforceRotating,
                uiStrings.TEXT_DICT.get(LocalizationConstants.Config.EnforceRotatingLabel),
                uiStrings.TEXT_DICT.get(LocalizationConstants.Config.EnforceRotatingTooltip),
                modPanel,
                (val) -> recalculateStreaks()
        );
        addElement(enforceRotatingButton);

        elementPosition.x = contentTopLeft.x;
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

        elementPosition.y -= ConfigModPanel.LineHeight;
        IUIElement allowCustomSeedsButton = createBooleanPropElement(
                elementPosition.x,
                elementPosition.y,
                criteriaModel.allowCustomSeeds,
                uiStrings.TEXT_DICT.get(LocalizationConstants.Config.AllowCustomSeedsLabel),
                uiStrings.TEXT_DICT.get(LocalizationConstants.Config.AllowCustomSeedsTooltip),
                modPanel,
                (val) -> recalculateStreaks()
        );
        addElement(allowCustomSeedsButton);

        elementPosition.x += (contentDimensions.x / NumCheckboxenPerLine);
        IUIElement allowDailiesButton = createBooleanPropElement(
                elementPosition.x,
                elementPosition.y,
                criteriaModel.allowDailies,
                uiStrings.TEXT_DICT.get(LocalizationConstants.Config.AllowDailiesLabel),
                uiStrings.TEXT_DICT.get(LocalizationConstants.Config.AllowDailiesTooltip),
                modPanel,
                (val) -> recalculateStreaks()
        );
        addElement(allowDailiesButton);

        elementPosition.x += (contentDimensions.x / NumCheckboxenPerLine);
        IUIElement allowEndlessButton = createBooleanPropElement(
                elementPosition.x,
                elementPosition.y,
                criteriaModel.allowDailies,
                uiStrings.TEXT_DICT.get(LocalizationConstants.Config.AllowEndlessLabel),
                uiStrings.TEXT_DICT.get(LocalizationConstants.Config.AllowEndlessTooltip),
                modPanel,
                (val) -> recalculateStreaks()
        );
        addElement(allowEndlessButton);

        elementPosition.x = contentTopLeft.x;
        elementPosition.y -= ConfigModPanel.LineHeight;
        IUIElement allowBetaButton = createBooleanPropElement(
                elementPosition.x,
                elementPosition.y,
                criteriaModel.allowBeta,
                uiStrings.TEXT_DICT.get(LocalizationConstants.Config.AllowBetaLabel),
                uiStrings.TEXT_DICT.get(LocalizationConstants.Config.AllowBetaTooltip),
                modPanel,
                (val) -> recalculateStreaks()
        );
        addElement(allowBetaButton);

        elementPosition.x += (contentDimensions.x / NumCheckboxenPerLine);
        IUIElement allowDemoButton = createBooleanPropElement(
                elementPosition.x,
                elementPosition.y,
                criteriaModel.allowDemo,
                uiStrings.TEXT_DICT.get(LocalizationConstants.Config.AllowDemoLabel),
                uiStrings.TEXT_DICT.get(LocalizationConstants.Config.AllowDemoTooltip),
                modPanel,
                (val) -> recalculateStreaks()
        );
        addElement(allowDemoButton);

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
}
