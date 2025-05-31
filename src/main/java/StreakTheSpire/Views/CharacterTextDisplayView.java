package StreakTheSpire.Views;

import StreakTheSpire.Models.CharacterTextDisplayModel;
import StreakTheSpire.Models.IModel;
import StreakTheSpire.StreakTheSpire;
import StreakTheSpire.UI.UITextElement;
import com.badlogic.gdx.math.Vector2;
import com.megacrit.cardcrawl.helpers.FontHelper;

public class CharacterTextDisplayView extends UITextElement implements IView {

    private CharacterTextDisplayModel model;


    public CharacterTextDisplayView(CharacterTextDisplayModel model) {
        super(Vector2.Zero, FontHelper.tipBodyFont, model.displayText.get());
        this.model = model;

         this.model.displayText.addOnChangedSubscriber(this::onTextChanged);
    }

    @Override
    protected void elementDestroy() {
        super.elementDestroy();

        if(model != null)
            model.displayText.removeOnChangedSubscriber(this::onTextChanged);
    }

    private void onTextChanged() {
        setText(model.displayText.get());
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
