package StreakTheSpire.UI;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.SerializationException;
import com.google.gson.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

/**
 * This class allows you to take an existing StS skeleton json file and using the bonesToRemove and bonesToKeep lists,
 * filter out elements you don't want to display, such as isolating just the head and not including the rest of the body
 */
public class SkeletonModifier {
    public ArrayList<String> bonesToRemove = new ArrayList<>();
    public ArrayList<String> bonesToKeep = new ArrayList<>();

    public SkeletonModifier() {}

    public SkeletonModifier(ArrayList<String> bonesToRemove, ArrayList<String> bonesToKeep) {
        this.bonesToRemove = new ArrayList<>(bonesToRemove);
        this.bonesToKeep = new ArrayList<>(bonesToKeep);
    }

    public void modifySkeletonData(String path, FileHandle tempFile) {
        // Ideally we'd do this by modifying SkeletonData objects, but Spine, in all of its ridiculous
        // terribleness, decided to make core variables both final and based off index like it's 1993
        FileHandle file = Gdx.files.internal(path);

        if (file == null) {
            throw new IllegalArgumentException("file cannot be null.");
        }

        JsonParser parser = new JsonParser();
        JsonElement rootElement = parser.parse(file.readString());

        if(!rootElement.isJsonObject()) {
            throw new IllegalArgumentException("file is not a JSON object.");
        }

        if(!rootElement.getAsJsonObject().has("bones")) {
            throw new IllegalArgumentException("file lacks any bone definitions!");
        }

        JsonArray bonesArray = rootElement.getAsJsonObject().get("bones").getAsJsonArray();

        HashMap<String, JsonElement> boneNameToJson = new HashMap<>();
        HashMap<String, String> boneToParent = new HashMap<>();
        HashMap<String, Integer> boneToOldBoneIndex = new HashMap<>();
        HashMap<Integer, Integer> oldBoneIndexToNewBoneIndex = new HashMap<>();

        int index = 0;
        for (JsonElement bone : bonesArray) {
            JsonObject boneObj = bone.getAsJsonObject();
            String boneName = boneObj.get("name").getAsString();
            JsonElement parentElement = boneObj.get("parent");
            String parentName = parentElement != null ? parentElement.getAsString() : null;

            if (parentName != null) {
                if (!boneNameToJson.containsKey(parentName)) {
                    throw new SerializationException("Parent bone not found: " + parentName);
                }
            }

            boneNameToJson.put(boneName, boneObj);
            boneToParent.put(boneName, parentName);
            boneToOldBoneIndex.put(boneName, index);
            index++;
        }

        HashSet<String> boneRemoveList = new HashSet();

        HashSet<String> currentList = new HashSet();
        for (String boneName : boneNameToJson.keySet()) {
            currentList.clear();
            String current = boneName;
            while (current != null) {
                currentList.add(current);
                if(bonesToRemove.contains(current)) {
                    boneRemoveList.addAll(currentList);
                }
                current = boneToParent.get(current);
            }
        }

        for (String boneName : boneNameToJson.keySet()) {
            currentList.clear();
            String current = boneName;
            while (current != null) {
                currentList.add(current);
                if(bonesToKeep.contains(current)) {
                    boneRemoveList.removeAll(currentList);
                }
                current = boneToParent.get(current);
            }
        }

        for(String boneName : boneRemoveList) {
            bonesArray.remove(boneNameToJson.get(boneName));
        }

        index = 0;
        for (JsonElement bone : bonesArray) {
            JsonObject boneObj = bone.getAsJsonObject();
            String boneName = boneObj.get("name").getAsString();

            oldBoneIndexToNewBoneIndex.put(boneToOldBoneIndex.get(boneName), index);

            JsonElement parentElem = boneObj.get("parent");
            String parentName = parentElem != null ? parentElem.getAsString() : null;

            if(bonesToRemove.contains(parentName))
                boneObj.remove("parent");

            index++;
        }

        Array<String> slotRemoveList = new Array();
        if(rootElement.getAsJsonObject().has("slots")) {
            Array<JsonElement> slotElementsToRemove = new Array();
            JsonArray slotsArray = rootElement.getAsJsonObject().get("slots").getAsJsonArray();

            for (JsonElement slot : slotsArray) {
                String slotName = slot.getAsJsonObject().get("name").getAsString();
                String boneName = slot.getAsJsonObject().get("bone").getAsString();

                if (boneRemoveList.contains(boneName)) {
                    slotRemoveList.add(slotName);
                    slotElementsToRemove.add(slot);
                }
            }

            for (JsonElement slot : slotElementsToRemove)
                slotsArray.remove(slot);
        }

        if(rootElement.getAsJsonObject().has("ik")) {
            Array<JsonElement> ikElementsToRemove = new Array();
            JsonArray ikArray = rootElement.getAsJsonObject().get("ik").getAsJsonArray();

            for (JsonElement ik : ikArray) {
                JsonArray ikBonesArray = ik.getAsJsonObject().get("bones").getAsJsonArray();
                for (JsonElement ikBone : ikBonesArray) {
                    if (boneRemoveList.contains(ikBone.getAsString())) {
                        ikElementsToRemove.add(ik);
                        break;
                    }
                }
            }


            for (JsonElement ik : ikElementsToRemove)
                ikArray.remove(ik);
        }

        Array<String> skinElementsToRemove = new Array();
        if(rootElement.getAsJsonObject().has("skins")) {
            JsonObject skinsRoot = rootElement.getAsJsonObject().get("skins").getAsJsonObject();
            for (Map.Entry<String, JsonElement> skinDefinitionElementPair : skinsRoot.entrySet()) {

                skinElementsToRemove.clear();

                JsonObject skinDefinition = skinDefinitionElementPair.getValue().getAsJsonObject();
                for (Map.Entry<String, JsonElement> skinElement : skinDefinition.entrySet()) {
                    String elementName = skinElement.getKey();
                    if (slotRemoveList.contains(elementName, false)) {
                        skinElementsToRemove.add(elementName);
                        continue;
                    }

                    JsonObject skinElementObj = skinElement.getValue().getAsJsonObject();
                    for (Map.Entry<String, JsonElement> skinSubElementPair : skinElementObj.entrySet()) {
                        JsonObject skinSubElementObj = skinSubElementPair.getValue().getAsJsonObject();
                        if (skinSubElementObj.has("type") && skinSubElementObj.get("type").getAsString().equals("mesh")) {
                            JsonArray uvsArray = skinSubElementObj.get("uvs").getAsJsonArray();
                            JsonArray verticesArray = skinSubElementObj.getAsJsonArray("vertices");
                            int verticesLength = verticesArray.size();

                            // So we have to skip ones which have the same vertex count as uv count, as they're encoded in a different way,
                            // this is again, another innovation by the team behind Spine and definitely good, maintainable practice that
                            // we should all emulate.
                            if (uvsArray.size() == verticesLength)
                                continue;

                            float[] vertices = new float[verticesLength];
                            for (int i = 0; i < verticesArray.size(); i++) {
                                vertices[i] = verticesArray.get(i).getAsFloat();
                            }

                            int vertFloatArrayIndex = 0;

                            while (vertFloatArrayIndex < verticesLength) {
                                int boneCount = (int) vertices[vertFloatArrayIndex++];

                                for (int nn = vertFloatArrayIndex + boneCount * 4; vertFloatArrayIndex < nn; vertFloatArrayIndex += 4) {
                                    int oldBoneIndex = (int) vertices[vertFloatArrayIndex];
                                    //JsonElement vertexJsonElement = verticesArray.get(vertFloatArrayIndex);
                                    //vertices[vertFloatArrayIndex] = oldBoneIndexToNewBoneIndex.get(oldBoneIndex);

                                    int newBoneIndex = oldBoneIndexToNewBoneIndex.get(oldBoneIndex);
                                    JsonPrimitive newPrimitive = new JsonPrimitive((float) newBoneIndex);
                                    verticesArray.set(vertFloatArrayIndex, new JsonPrimitive(oldBoneIndexToNewBoneIndex.get(oldBoneIndex).floatValue()));
                                }
                            }

                        }
                    }
                }

                for (String skinElementName : skinElementsToRemove)
                    skinDefinition.remove(skinElementName);
            }
        }

        // Now we need to remap the skin vertex indices because ofcourse, once again, despite having a
        // json, name based format, they decide to encode information by index directly. Why even support json?!
        // Please do not give your money to the Spine people, it's a ridiculously bad product at a ridiculous price

        if(rootElement.getAsJsonObject().has("animations")) {
            JsonObject animationsRoot = rootElement.getAsJsonObject().get("animations").getAsJsonObject();

            for (Map.Entry<String, JsonElement> animElement : animationsRoot.entrySet()) {
                JsonObject animation = animElement.getValue().getAsJsonObject();

                if (animation.has("slots")) {
                    JsonObject slotsRoot = animation.get("slots").getAsJsonObject();
                    Array<String> animSlotsToRemove = new Array();
                    for (Map.Entry<String, JsonElement> animSlot : slotsRoot.entrySet()) {
                        String slotName = animSlot.getKey();
                        if (slotRemoveList.contains(slotName, false)) {
                            animSlotsToRemove.add(slotName);
                        }
                    }

                    for (String animSlotName : animSlotsToRemove)
                        slotsRoot.remove(animSlotName);
                }

                if (animation.has("bones")) {
                    JsonObject bonesRoot = animation.get("bones").getAsJsonObject();

                    Array<String> animBonesToRemove = new Array();
                    for (Map.Entry<String, JsonElement> animBone : bonesRoot.entrySet()) {
                        String boneName = animBone.getKey();
                        if (boneRemoveList.contains(boneName)) {
                            animBonesToRemove.add(boneName);
                        }
                    }

                    for (String animBonesName : animBonesToRemove)
                        bonesRoot.remove(animBonesName);
                }

                if (animation.has("deform")) {
                    JsonObject deformationsRoot = animation.get("deform").getAsJsonObject();
                    Array<String> animDeformationsToRemove = new Array();
                    for (Map.Entry<String, JsonElement> deformSectionElement : deformationsRoot.entrySet()) {
                        animDeformationsToRemove.clear();
                        JsonObject deformationSection = deformSectionElement.getValue().getAsJsonObject();

                        for (Map.Entry<String, JsonElement> deformationElement : deformationSection.entrySet()) {
                            String deformationName = deformationElement.getKey();
                            if (skinElementsToRemove.contains(deformationName, false)) {
                                animDeformationsToRemove.add(deformationName);
                            }
                        }

                        for (String deformationName : animDeformationsToRemove)
                            deformationSection.remove(deformationName);
                    }
                }
            }
        }

        tempFile.writeString(rootElement.toString(), false);
    }
}
