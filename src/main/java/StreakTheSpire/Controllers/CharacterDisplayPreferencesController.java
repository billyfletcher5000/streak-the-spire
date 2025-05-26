package StreakTheSpire.Controllers;

import StreakTheSpire.Models.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CharacterDisplayPreferencesController {

    public static List<Class<? extends CharacterDisplayModel>> getPreferredDisplayModelClassesInOrder(DisplayPreferencesModel.CharacterStyle style) {
        switch (style) {
            case AnimatedIcon:
                return Arrays.asList(CharacterSkeletonDisplayModel.class, CharacterIconDisplayModel.class, CharacterTextDisplayModel.class);
            case StaticIcon:
                return Arrays.asList(CharacterIconDisplayModel.class, CharacterSkeletonDisplayModel.class, CharacterTextDisplayModel.class);
            case Text:
                return Arrays.asList(CharacterTextDisplayModel.class, CharacterIconDisplayModel.class, CharacterSkeletonDisplayModel.class);
        }

        return new ArrayList<>();
    }
}
