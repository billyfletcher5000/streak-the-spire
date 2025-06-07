package StreakTheSpire.Config;

import StreakTheSpire.Controllers.PlayerStreakStoreController;
import StreakTheSpire.Models.PlayerStreakStoreModel;
import StreakTheSpire.Models.StreakCriteriaModel;
import StreakTheSpire.StreakTheSpire;
import StreakTheSpire.Utils.*;
import basemod.*;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import com.megacrit.cardcrawl.helpers.FontHelper;
import com.megacrit.cardcrawl.localization.UIStrings;

public class CriteriaModPanelPage extends ConfigModPanelPage {

    private static final String minButtonNormalPath = "StreakTheSpire/textures/ui/0_button_normal.png";
    private static final String minButtonHoveredPath = "StreakTheSpire/textures/ui/0_button_hovered.png";
    private static final String minButtonPressedPath = "StreakTheSpire/textures/ui/0_button_pressed.png";
    private static final String maxButtonNormalPath = "StreakTheSpire/textures/ui/20_button_normal.png";
    private static final String maxButtonHoveredPath = "StreakTheSpire/textures/ui/20_button_hovered.png";
    private static final String maxButtonPressedPath = "StreakTheSpire/textures/ui/20_button_pressed.png";

    private boolean isInitialised = false;

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
                LocalizationHelper.locDict(uiStrings, LocalizationConstants.Config.RequireHeartKillLabel),
                LocalizationHelper.locDict(uiStrings, LocalizationConstants.Config.RequireHeartKillTooltip),
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
                LocalizationHelper.locDict(uiStrings, LocalizationConstants.Config.RequiredAscensionLevelLabel),
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
                LocalizationHelper.locDict(uiStrings, LocalizationConstants.Config.EnforceRotatingLabel),
                LocalizationHelper.locDict(uiStrings, LocalizationConstants.Config.EnforceRotatingTooltip),
                modPanel,
                (val) -> recalculateStreaks()
        );
        addElement(enforceRotatingButton);

        elementPosition.x = contentTopLeft.x;
        elementPosition.y -= ConfigModPanel.TitleLineHeight;
        FixedModLabel allowTitle = new FixedModLabel(
                LocalizationHelper.locDict(uiStrings, LocalizationConstants.Config.AllowTitle),
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
                LocalizationHelper.locDict(uiStrings, LocalizationConstants.Config.AllowCustomSeedsLabel),
                LocalizationHelper.locDict(uiStrings, LocalizationConstants.Config.AllowCustomSeedsTooltip),
                modPanel,
                (val) -> recalculateStreaks()
        );
        addElement(allowCustomSeedsButton);

        elementPosition.x += (contentDimensions.x / NumCheckboxenPerLine);
        IUIElement allowDailiesButton = createBooleanPropElement(
                elementPosition.x,
                elementPosition.y,
                criteriaModel.allowDailies,
                LocalizationHelper.locDict(uiStrings, LocalizationConstants.Config.AllowDailiesLabel),
                LocalizationHelper.locDict(uiStrings, LocalizationConstants.Config.AllowDailiesTooltip),
                modPanel,
                (val) -> recalculateStreaks()
        );
        addElement(allowDailiesButton);

        elementPosition.x += (contentDimensions.x / NumCheckboxenPerLine);
        IUIElement allowEndlessButton = createBooleanPropElement(
                elementPosition.x,
                elementPosition.y,
                criteriaModel.allowDailies,
                LocalizationHelper.locDict(uiStrings, LocalizationConstants.Config.AllowEndlessLabel),
                LocalizationHelper.locDict(uiStrings, LocalizationConstants.Config.AllowEndlessTooltip),
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
                LocalizationHelper.locDict(uiStrings, LocalizationConstants.Config.AllowBetaLabel),
                LocalizationHelper.locDict(uiStrings, LocalizationConstants.Config.AllowBetaTooltip),
                modPanel,
                (val) -> recalculateStreaks()
        );
        addElement(allowBetaButton);

        elementPosition.x += (contentDimensions.x / NumCheckboxenPerLine);
        IUIElement allowDemoButton = createBooleanPropElement(
                elementPosition.x,
                elementPosition.y,
                criteriaModel.allowDemo,
                LocalizationHelper.locDict(uiStrings, LocalizationConstants.Config.AllowDemoLabel),
                LocalizationHelper.locDict(uiStrings, LocalizationConstants.Config.AllowDemoTooltip),
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
