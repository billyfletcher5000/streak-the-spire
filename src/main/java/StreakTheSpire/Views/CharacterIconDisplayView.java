package StreakTheSpire.Views;

import StreakTheSpire.Models.CharacterIconDisplayModel;
import StreakTheSpire.Models.IModel;
import StreakTheSpire.StreakTheSpire;
import StreakTheSpire.UI.UIImageElement;
import StreakTheSpire.Utils.Properties.Property;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;

public class CharacterIconDisplayView extends UIImageElement implements IView {
    private CharacterIconDisplayModel model;

    private Property.ValueChangedSubscriber textureChangedSubscriber;

    public CharacterIconDisplayModel getModel() { return model; }

    public CharacterIconDisplayView(CharacterIconDisplayModel model) {
        super(Vector2.Zero, model.iconTexture.get());
        this.model = model;
        textureChangedSubscriber = this.model.iconTexture.addOnChangedSubscriber(() -> this.setTextureRegion(new TextureRegion(model.iconTexture.get())));
    }

    @Override
    public void close() {
        super.close();

        if(this.model != null && this.model.iconTexture != null && textureChangedSubscriber != null)
            this.model.iconTexture.removeOnChangedSubscriber(textureChangedSubscriber);
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
