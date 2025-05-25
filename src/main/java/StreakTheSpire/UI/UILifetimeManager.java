package StreakTheSpire.UI;

import java.util.ArrayList;

public class UILifetimeManager {

    private static ArrayList<UIElement> elementsToDestroy = new ArrayList<>();
    private static ArrayList<UIElement> pendingElementsToDestroy = new ArrayList<>();
    private static boolean isProcessing = false;

    public static void EnqueueDestroy(UIElement element) {
        if(isProcessing)
            pendingElementsToDestroy.add(element);
        else
            elementsToDestroy.add(element);
    }

    public static void ProcessDestroyed() {
        isProcessing = true;
        for (UIElement element : elementsToDestroy) {
            element._internalDestroy();
        }

        elementsToDestroy.clear();
        elementsToDestroy.addAll(pendingElementsToDestroy);
        pendingElementsToDestroy.clear();
        isProcessing = false;
    }
}
