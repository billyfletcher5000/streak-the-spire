package StreakTheSpire.Utils;

import com.megacrit.cardcrawl.localization.UIStrings;

public class LocalizationHelper {
    public static String locDict(UIStrings strings, String id) {
        if(strings == null || strings.TEXT_DICT == null || !strings.TEXT_DICT.containsKey(id))
            return "LOCERROR";

        return strings.TEXT_DICT.get(id);
    }
}
