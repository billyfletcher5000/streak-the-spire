package StreakTheSpire.Views;

import StreakTheSpire.Models.*;
import StreakTheSpire.StreakTheSpire;
import StreakTheSpire.UI.Layout.UIHorizontalLayoutGroup;
import StreakTheSpire.UI.Layout.UIVerticalLayoutGroup;
import StreakTheSpire.UI.UIElement;
import StreakTheSpire.UI.UILabeledToggleButtonElement;
import StreakTheSpire.UI.UISDFTextElement;
import StreakTheSpire.Utils.Properties.Property;
import StreakTheSpire.Utils.Properties.PropertyHashSet;
import StreakTheSpire.Utils.Properties.PropertyLinkedHashSet;
import StreakTheSpire.Utils.Properties.PropertyList;
import StreakTheSpire.Utils.TextureCache;
import basemod.IUIElement;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.math.Vector2;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

// TODO: Make this less of an overengineered mess, the whole settings side doesn't need to be automatic and it's clunky
//       and painful to maintain
public class ConfigPanelView extends UIElement implements IView, IUIElement {

    private static final Vector2 ModConfigPanelPosition = new Vector2(355.0f, 140.0f);
    private static final Vector2 ModConfigPanelDimensions = new Vector2(460.0f, 590.0f);
    private static final int MaxGroupElementsPerLine = 3;
    private static final float ItemWidth = 150.0f;

    private ConfigPanelModel configPanelModel;

    private UIVerticalLayoutGroup mainVerticalGroup;
    private LinkedHashMap<String, ArrayList<UIHorizontalLayoutGroup>> groupIDToLayoutGroup = new LinkedHashMap<>();

    public ConfigPanelView(ConfigPanelModel model) {
        setLocalPosition(ModConfigPanelPosition);
        setDimensions(ModConfigPanelDimensions);


    }

    private void createSettings() {

        LinkedHashMap<Property<? extends IConfigDataModel>, String> configDataMap = StreakTheSpire.get().getConfigDataModelToConfigIDMap();

        for(Map.Entry<Property<? extends IConfigDataModel>, String> entry : configDataMap.entrySet()) {
            IConfigDataModel model = entry.getKey().get();
            Field[] fields = model.getClass().getFields();

            for(Field field : fields) {
                ConfigProperty configVariableAnno = field.getAnnotation(ConfigProperty.class);
                if(configVariableAnno != null) {
                    String localisationID = configVariableAnno.localisationID();
                    if(localisationID == null || localisationID.isEmpty())
                        localisationID = field.getName();

                    String groupID = configVariableAnno.groupID();
                    Class<?> propertyType = configVariableAnno.type();

                    Class<?> fieldType = field.getType();
                    if(fieldType == Property.class) {
                        addProperty(model, field, propertyType, localisationID, groupID);
                    }
                    else if(fieldType == PropertyList.class) {
                        addPropertyList(model, field, propertyType, localisationID, groupID);
                    }
                    else if(fieldType == PropertyHashSet.class) {
                        addPropertyHashSet(model, field, propertyType, localisationID, groupID);
                    }
                    else if(fieldType == PropertyLinkedHashSet.class) {
                        addPropertyLinkedHashSet(model, field, propertyType, localisationID, groupID);
                    }
                }
            }
        }
    }

    private void createViewFromSettings() {
        mainVerticalGroup = new UIVerticalLayoutGroup();
        addChild(mainVerticalGroup);

        for(Map.Entry<String, ArrayList<UIHorizontalLayoutGroup>> entry : groupIDToLayoutGroup.entrySet()) {
            String groupID = entry.getKey();
            String localisedGroupID = getLocalisedText(groupID);
            if(localisedGroupID != null && !localisedGroupID.isEmpty()) {
                addChild(createHeading(localisedGroupID));
            }

            ArrayList<UIHorizontalLayoutGroup> groups = entry.getValue();
            for(UIHorizontalLayoutGroup group : groups)
                addChild(group);
        }
    }

    private void addProperty(IConfigDataModel model, Field field, Class<?> propertyType, String localisedName, String groupID) {
        try {
            TextureCache textureCache = StreakTheSpire.get().getTextureCache();

            if (propertyType == Boolean.class) {
                Property<Boolean> prop = (Property<Boolean>) field.get(model);
                UIButtonDataModel buttonData = configPanelModel.toggleButtonData.get();
                UILabeledToggleButtonElement labeledToggle = new UILabeledToggleButtonElement(
                        Vector2.Zero,
                        new Vector2(150f, 40f), // This gets overridden by layouting
                        buttonData.backgroundNormalPath.get() != null ? textureCache.getTexture(buttonData.backgroundNormalPath.get()) : null,
                        buttonData.backgroundNormalPath.get() != null ? textureCache.getTexture(buttonData.backgroundNormalPath.get()) : null,
                        buttonData.backgroundNormalPath.get() != null ? textureCache.getTexture(buttonData.backgroundNormalPath.get()) : null,
                        buttonData.backgroundNormalPath.get() != null ? textureCache.getTexture(buttonData.backgroundNormalPath.get()) : null,
                        buttonData.backgroundNormalPath.get() != null ? textureCache.getTexture(buttonData.backgroundNormalPath.get()) : null,
                        buttonData.pressedOffset.get(),
                        localisedName,
                        configPanelModel.labelFont.get()
                );

                labeledToggle.addOnToggledSubscriber(isSelected -> {
                    prop.set(isSelected);
                });

                UIHorizontalLayoutGroup layoutGroup = getGroupLayoutGroupWithSpace(groupID);
                layoutGroup.addChild(labeledToggle);
            }
            else if(propertyType == Integer.class) {

            }
        }
        catch(Exception e) {
            StreakTheSpire.logError("Error when retrieving field \"" + field.getName() + "\": " + e.getMessage());
        }
    }

    private void addPropertyList(IConfigDataModel model, Field field, Class<?> propertyType, String localisedName, String groupID) {
        try {
            // TODO: Support lists somehow, I guess a popup that has a list view with add/remove
        }
        catch(Exception e) {
            StreakTheSpire.logError("Error when retrieving field \"" + field.getName() + "\": " + e.getMessage());
        }
    }

    private void addPropertyHashSet(IConfigDataModel model, Field field, Class<?> propertyType, String localisedName, String groupID) {
        try {
            // TODO: Support lists somehow, I guess a popup that has a list view with add/remove
        }
        catch(Exception e) {
            StreakTheSpire.logError("Error when retrieving field \"" + field.getName() + "\": " + e.getMessage());
        }
    }

    private void addPropertyLinkedHashSet(IConfigDataModel model, Field field, Class<?> propertyType, String localisedName, String groupID) {
        try {
            // TODO: Support lists somehow, I guess a popup that has a list view with add/remove
        }
        catch(Exception e) {
            StreakTheSpire.logError("Error when retrieving field \"" + field.getName() + "\": " + e.getMessage());
        }
    }

    private UIElement createHeading(String heading) {
        DisplayPreferencesModel preferences = StreakTheSpire.get().getDisplayPreferencesModel();
        BitmapFont font = StreakTheSpire.get().getFontCache().getFont(preferences.fontIdentifier.get());
        return new UISDFTextElement(Vector2.Zero, font, heading);
    }

    private UIHorizontalLayoutGroup getGroupLayoutGroupWithSpace(String groupID) {
        ArrayList<UIHorizontalLayoutGroup> groups;
        if(!groupIDToLayoutGroup.containsKey(groupID)) {
            groups = new ArrayList<>();
            groupIDToLayoutGroup.put(groupID, groups);
        }
        else {
            groups = groupIDToLayoutGroup.get(groupID);
        }

        UIHorizontalLayoutGroup group = groups.get(groups.size() - 1);
        if(group.getChildren().length >= MaxGroupElementsPerLine) {
            group = new UIHorizontalLayoutGroup();
            group.setFixedItemWidth(ItemWidth);
            groups.add(group);
        }

        return group;
    }

    private String getLocalisedText(String localisationID) {
        return configPanelModel.localisationSource.get().TEXT_DICT.get(localisationID);
    }

    //region IUIElement
    @Override
    public void update() {
        update(StreakTheSpire.getDeltaTime());
    }

    @Override
    public int renderLayer() {
        return getLayer();
    }

    @Override
    public int updateOrder() {
        return 0;
    }

    @Override
    public void set(float xPos, float yPos) {
        setLocalPosition(new Vector2(xPos, yPos));
    }

    @Override
    public void setX(float xPos) {
        setLocalPosition(new Vector2(xPos, getX()));
    }

    @Override
    public void setY(float yPos) {
        setLocalPosition(new Vector2(getX(), yPos));
    }

    @Override
    public float getX() {
        return getLocalPosition().x;
    }

    @Override
    public float getY() {
        return getLocalPosition().y;
    }
    //endregion
}
