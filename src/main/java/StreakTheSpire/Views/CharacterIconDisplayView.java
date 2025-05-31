package StreakTheSpire.Views;

import StreakTheSpire.Models.CharacterIconDisplayModel;
import StreakTheSpire.Models.IModel;
import StreakTheSpire.StreakTheSpire;
import StreakTheSpire.UI.UIImageElement;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;

public class CharacterIconDisplayView extends UIImageElement implements IView {
    private CharacterIconDisplayModel model;

    public CharacterIconDisplayModel getModel() { return model; }

    public CharacterIconDisplayView(CharacterIconDisplayModel model) {
        super(Vector2.Zero, model.iconTexture.get());
        this.model = model;
        this.model.iconTexture.addOnChangedSubscriber(this::onTextureChanged);
    }

    @Override
    protected void elementDestroy() {
        super.elementDestroy();

        if(this.model != null)
            this.model.iconTexture.removeOnChangedSubscriber(this::onTextureChanged);
    }

    private void onTextureChanged() {
        setTextureRegion(new TextureRegion(model.iconTexture.get()));
    }

    public static final IViewFactory FACTORY = new IViewFactory() {
        @Override
        public <TView extends IView, TModel extends IModel> TView createView(TModel model) {
            CharacterIconDisplayModel characterIconDisplayModel = (CharacterIconDisplayModel) model;
            if(characterIconDisplayModel != null) {
                StreakTheSpire.logDebug("CharacterIconDisplayView created!");
                return (TView) new CharacterIconDisplayView(characterIconDisplayModel);
            }

            StreakTheSpire.logWarning("CharacterIconDisplayViewFactory failed to create view!");
            return null;
        }
    };
}
