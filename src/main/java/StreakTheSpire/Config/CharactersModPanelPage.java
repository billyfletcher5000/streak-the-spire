package StreakTheSpire.Config;

import StreakTheSpire.Controllers.CharacterLocalisationSetController;
import StreakTheSpire.Controllers.PlayerStreakStoreController;
import StreakTheSpire.Models.*;
import StreakTheSpire.StreakTheSpire;
import StreakTheSpire.Utils.FixedModLabel;
import StreakTheSpire.Utils.FixedModLabeledToggleButton;
import StreakTheSpire.Utils.FixedModToggleButton;
import StreakTheSpire.Utils.LocalizationConstants;
import basemod.IUIElement;
import basemod.ModPanel;
import com.badlogic.gdx.math.Vector2;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.helpers.FontHelper;
import com.megacrit.cardcrawl.localization.CharacterStrings;
import com.megacrit.cardcrawl.localization.UIStrings;

import java.util.ArrayList;
import java.util.HashMap;

public class CharactersModPanelPage extends ConfigModPanelPage {

    private boolean isInitialised = false;
    private HashMap<String, FixedModLabeledToggleButton> trackableCharacterIDToToggleButton = new HashMap<>();

    @Override
    public String getTitleLocalizationID() { return LocalizationConstants.Config.CharactersTitle; }

    @Override
    public void initialise(ModPanel modPanel, Vector2 contentTopLeft, Vector2 contentDimensions) {
        if(isInitialised)
            return;

        final int NumCheckboxenPerLine = 3;

        UIStrings uiStrings = StreakTheSpire.get().getConfigUIStrings();
        StreakCriteriaModel criteriaModel = StreakTheSpire.get().getStreakCriteriaModel();
        CharacterLocalisationSetModel localisationSetModel = StreakTheSpire.get().getCharacterLocalisationSetModel();
        CharacterLocalisationSetController localisationSetController = new CharacterLocalisationSetController(localisationSetModel);

        float checkboxItemWidth = contentDimensions.x / NumCheckboxenPerLine;

        Vector2 elementPosition = contentTopLeft.cpy();

        FixedModLabel allowTitle = new FixedModLabel(
                uiStrings.TEXT_DICT.get(LocalizationConstants.Config.TrackCharactersTitle),
                elementPosition.x,
                elementPosition.y,
                FontHelper.tipBodyFont,
                modPanel,
                (label) -> {}
        );
        addElement(allowTitle);

        elementPosition.y -= ConfigModPanel.LineHeight;

        ArrayList<String> trackedCharacterIDs = new ArrayList<>(criteriaModel.trackableCharacterClasses);
        trackedCharacterIDs.add(PlayerStreakStoreModel.RotatingPlayerIdentifier);

        int lineIndex = 0;
        for (int characterIndex = 0; characterIndex < trackedCharacterIDs.size(); characterIndex++) {
            String characterID = trackedCharacterIDs.get(characterIndex);
            CharacterLocalisationModel localisationModel = localisationSetController.getLocalisationForCharacter(characterID);
            String characterName = characterID;
            if(localisationModel != null && localisationModel.localisationID.get() != null) {
                CharacterStrings characterStrings = StreakTheSpire.get().getCharacterStrings(localisationModel.localisationID.get());
                if(characterStrings.NAMES.length > 0)
                    characterName = characterStrings.NAMES[0];
            }

            boolean isEnabled = false;
            if(criteriaModel.trackContinuous.get() && characterID.equals(PlayerStreakStoreModel.RotatingPlayerIdentifier)) {
                isEnabled = true;
            }
            else {
                isEnabled = criteriaModel.trackedCharacterClasses.contains(characterID);
            }

            FixedModLabeledToggleButton button = new FixedModLabeledToggleButton(
                    characterName,
                    uiStrings.TEXT_DICT.get(LocalizationConstants.Config.TrackCharactersTooltip),
                    elementPosition.x,
                    elementPosition.y,
                    Settings.CREAM_COLOR,
                    FontHelper.charDescFont,
                    isEnabled,
                    modPanel,
                    (label) -> {},
                    (toggleButton) -> {
                        onCharacterButtonToggled(toggleButton.enabled, characterID);
                    }
            );
            addElement(button);

            lineIndex++;
            if(lineIndex >= NumCheckboxenPerLine) {
                lineIndex = 0;
                elementPosition.x = contentTopLeft.x;
                elementPosition.y -= ConfigModPanel.LineHeight;
            }
            else {
                elementPosition.x += checkboxItemWidth;
            }
        }
    }

    private void onCharacterButtonToggled(boolean isEnabled, String characterID) {
        StreakCriteriaModel criteriaModel = StreakTheSpire.get().getStreakCriteriaModel();

        if(isEnabled) {
            if(characterID.equals(PlayerStreakStoreModel.RotatingPlayerIdentifier)) {
                if(!criteriaModel.trackContinuous.get())
                    criteriaModel.trackContinuous.set(true);
            }
            else if(!criteriaModel.trackedCharacterClasses.contains(characterID)) {
                criteriaModel.trackedCharacterClasses.add(characterID);
                recalculateStreaks();
            }
        }
        else {
            if(characterID.equals(PlayerStreakStoreModel.RotatingPlayerIdentifier)) {
                if(criteriaModel.trackContinuous.get())
                    criteriaModel.trackContinuous.set(false);
            }
            else if(criteriaModel.trackedCharacterClasses.contains(characterID)) {
                criteriaModel.trackedCharacterClasses.remove(characterID);
                recalculateStreaks();
            }
        }

        isInitialised = false;
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
