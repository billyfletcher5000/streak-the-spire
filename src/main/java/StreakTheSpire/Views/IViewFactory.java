package StreakTheSpire.Views;

import StreakTheSpire.Models.IModel;

public interface IViewFactory {
    <TView extends IView, TModel extends IModel>
    TView createView(TModel model);
}
