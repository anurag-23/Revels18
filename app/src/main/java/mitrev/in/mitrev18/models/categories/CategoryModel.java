package mitrev.in.mitrev18.models.categories;


import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import io.realm.RealmObject;

public class CategoryModel extends RealmObject {

    @SerializedName("cid")
    @Expose
    private String categoryID;

    @SerializedName("cname")
    @Expose
    private String categoryName;

    @SerializedName("cdesc")
    @Expose
    private String categoryDescription;

    public CategoryModel(){

    }
    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public String getCategoryDescription() {
        return categoryDescription;
    }

    public void setCategoryDescription(String categoryDescription) {
        this.categoryDescription = categoryDescription;
    }

    public String getCategoryID() {
        return categoryID;
    }

    public void setCategoryID(String categoryID) {
        this.categoryID = categoryID;
    }
}
