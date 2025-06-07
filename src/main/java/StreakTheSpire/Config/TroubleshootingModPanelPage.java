package StreakTheSpire.Config;

import StreakTheSpire.StreakTheSpire;
import StreakTheSpire.UI.UIElement;
import StreakTheSpire.Utils.FixedModLabel;
import StreakTheSpire.Utils.FixedModLabeledButton;
import StreakTheSpire.Utils.LocalizationConstants;
import StreakTheSpire.Utils.LocalizationHelper;
import basemod.ModLabeledButton;
import basemod.ModPanel;
import com.badlogic.gdx.math.Vector2;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.helpers.FontHelper;
import com.megacrit.cardcrawl.localization.UIStrings;

import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;

public class TroubleshootingModPanelPage extends ConfigModPanelPage {
    private boolean isInitialised = false;

    @Override
    public String getTitleLocalizationID() {
        return LocalizationConstants.Config.TroubleshootingTitle;
    }

    @Override
    public void initialise(ModPanel modPanel, Vector2 contentTopLeft, Vector2 contentDimensions) {
        if(isInitialised)
            return;

        UIStrings uiStrings = StreakTheSpire.get().getConfigUIStrings();

        Vector2 elementPosition = contentTopLeft.cpy();
        elementPosition.y -= 50.0f;

        FixedModLabeledButton copyErrorLogBtn = new FixedModLabeledButton(
                LocalizationHelper.locDict(uiStrings, LocalizationConstants.Config.CopyErrorLogButtonLabel),
                elementPosition.x,
                elementPosition.y,
                modPanel,
                btn -> saveErrorLogToClipboard()
        );
        addElement(copyErrorLogBtn);

        elementPosition.y -= 120.0f;
        FixedModLabel copyErrorDescLabel = new FixedModLabel(
                LocalizationHelper.locDict(uiStrings, LocalizationConstants.Config.CopyErrorLogButtonTooltip),
                elementPosition.x,
                elementPosition.y,
                FontHelper.charDescFont,
                modPanel,
                a -> {}
        );
        addElement(copyErrorDescLabel);

        isInitialised = true;
    }

    private void saveErrorLogToClipboard() {
        StringSelection stringSelection = new StringSelection(StreakTheSpire.getErrorLog());
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        clipboard.setContents(stringSelection, null);
    }
}
