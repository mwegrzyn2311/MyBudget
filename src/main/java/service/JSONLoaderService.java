package service;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import model.TopCategory;

import javax.inject.Singleton;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.lang.reflect.Type;
import java.util.List;

@Singleton
public class JSONLoaderService {
    private static JSONLoaderService instance=null;
    private final Gson gson = new Gson();
    private static final Type TOP_CATEGORY_TYPE = new TypeToken<List<TopCategory>>() {}.getType();
    private final List<TopCategory> categories;

    private JSONLoaderService(){
        categories = loadCategories();
    }

    public static JSONLoaderService getInstance(){
        if(instance==null) instance = new JSONLoaderService();
        return instance;
    }

    private List<TopCategory> loadCategories() {
        File file = new File("./src/main/resources/categories/categories.json");
        JsonReader reader = null;
        try {
            reader = new JsonReader(new FileReader(file));
        }catch (FileNotFoundException e){System.out.println("File "+file+" not found");}
        assert reader != null;
        return gson.fromJson(reader, TOP_CATEGORY_TYPE);
    }

    public List<TopCategory> getCategories() {
        return categories;
    }
}
