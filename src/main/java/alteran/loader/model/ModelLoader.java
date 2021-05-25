package alteran.loader.model;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import alteran.common.AlteranCommon;
import alteran.loader.FolderLoader;
import net.minecraft.util.ResourceLocation;

public class ModelLoader {

	public static final String MODELS_PATH = "assets/alteran/models/";
	private static final Map<ResourceLocation, OBJModel> LOADED_MODELS = new HashMap<>();

	public static OBJModel getModel(ResourceLocation resourceLocation) {
		return LOADED_MODELS.get(resourceLocation);
	}

	public static void reloadModels() throws IOException {
		LOADED_MODELS.clear();

		List<String> modelPaths = FolderLoader.getAllFiles(MODELS_PATH, ".obj");

		for (String modelPath : modelPaths) {
			String modelResourcePath = modelPath.replaceFirst("assets/alteran/", "");
			LOADED_MODELS.put(new ResourceLocation(AlteranCommon.modId, modelResourcePath), OBJLoader.loadModel(modelPath));
		}
	}

	public static ResourceLocation getModelResource(String model) {
		return new ResourceLocation(AlteranCommon.modId, "models/" + model);
	}
}
