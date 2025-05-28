package StreakTheSpire.Utils.Lifetime;

import StreakTheSpire.UI.UIElement;

import java.util.ArrayList;

public class LifetimeManager {

    private static final ArrayList<IDestroyable> elementsToDestroy = new ArrayList<>();
    private static final ArrayList<IDestroyable> pendingElementsToDestroy = new ArrayList<>();
    private static boolean isProcessing = false;

    public static void EnqueueDestroy(IDestroyable element) {
        // The ol' double list approach: we want to prevent modifying the list while iterating
        // and destroying, since it's common for one thing to destroy something else (like children)
        // when it destroys itself. We process these lists separately, one list per frame. It is
        // possible to loop and process them all on the same frame but this way prevents infinite loops
        // if someone ever creates and destroys an object in its onDestroy, as well as providing a small
        // method of mitigating large destruction chain costs. It's not very smart.
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
