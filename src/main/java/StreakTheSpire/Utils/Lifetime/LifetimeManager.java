package StreakTheSpire.Utils.Lifetime;

import StreakTheSpire.UI.UIElement;

import java.util.ArrayList;

public class LifetimeManager {

    private static ArrayList<IDestroyable> elementsToDestroy = new ArrayList<>();
    private static ArrayList<IDestroyable> pendingElementsToDestroy = new ArrayList<>();
    private static boolean isProcessing = false;

    public static void EnqueueDestroy(IDestroyable element) {
        if(isProcessing)
            pendingElementsToDestroy.add(element);
        else
            elementsToDestroy.add(element);
    }

    public static void ProcessDestroyed() {
        isProcessing = true;
        for (IDestroyable element : elementsToDestroy) {
            element.onDestroy();
        }

        elementsToDestroy.clear();
        elementsToDestroy.addAll(pendingElementsToDestroy);
        pendingElementsToDestroy.clear();
        isProcessing = false;
    }
}
