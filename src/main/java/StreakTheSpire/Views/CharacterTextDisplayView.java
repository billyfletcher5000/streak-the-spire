package StreakTheSpire.Views;

import StreakTheSpire.Models.CharacterIconDisplayModel;
import StreakTheSpire.Models.CharacterTextDisplayModel;
import StreakTheSpire.Models.IModel;
import StreakTheSpire.StreakTheSpire;
import StreakTheSpire.UI.UITextElement;
import StreakTheSpire.Utils.Properties.Property;
import com.badlogic.gdx.math.Vector2;
import com.megacrit.cardcrawl.helpers.FontHelper;

public class CharacterTextDisplayView extends UITextElement implements IView {

    private CharacterTextDisplayModel model;

    private Property.ValueChangedSubscriber textChangedSubscriber;

    public CharacterTextDisplayView(CharacterTextDisplayModel model) {
        super(Vector2.Zero, FontHelper.tipBodyFont, model.displayText.get());
        this.model = model;

        textChangedSubscriber = this.model.displayText.addOnChangedSubscriber(() -> setText(model.displayText.get()));
    }

    @Override
    public void close() {
        super.close();

        if(model != null && textChangedSubscriber != null)
            model.displayText.removeOnChangedSubscriber(textChangedSubscriber);
    }

    public static final IViewFactory FACTORY = new IViewFactory() {
        @Override
        public <TView extends IView, TModel extends IModel> TView createView(TModel model) {
            CharacterTextDisplayModel characterTextDisplayModel = (CharacterTextDisplayModel) model;
            if(characterTextDisplayModel != null) {
                StreakTheSpire.logDebug("CharacterTextDisplayView created!");
                return (TView) new CharacterTextDisplayView(characterTextDisplayModel);
            }

            StreakTheSpire.logWarning("CharacterTextDisplayViewFactory failed to create view!");
            return null;
        }
    };
}
