package StreakTheSpire.Views;

import StreakTheSpire.Models.IModel;
import java.util.HashMap;

public class ViewFactoryManager {
    private static ViewFactoryManager instance = new ViewFactoryManager();
    public static ViewFactoryManager get() { return instance; }

    private HashMap<Class<? extends IModel>, IViewFactory> modelClassToFactory = new HashMap<>();

    public void registerViewFactory(Class<? extends IModel> modelClass, IViewFactory factory) {
        modelClassToFactory.put(modelClass, factory);
    }

    public IViewFactory getViewFactory(Class<?> modelClass) {
        return modelClassToFactory.get(modelClass);
    }

    public <T extends IView> T createView(IModel model) {
        IViewFactory factory = getViewFactory(model.getClass());
        if (factory == null)
            return null;

        return factory.createView(model);
    }
}
