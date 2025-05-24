package StreakTheSpire.UI;

import java.util.ArrayList;

public class UILifetimeManager {
    private static ArrayList<UIElement> elementsToDestroy = new ArrayList<>();

    public static void EnqueueDestroy(UIElement element) {
        elementsToDestroy.add(element);
    }

    public static void ProcessDestroyed() {
        for (UIElement element : elementsToDestroy) {
            element._internalDestroy();
        }

        elementsToDestroy.clear();
    }
}
