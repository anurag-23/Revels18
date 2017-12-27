package revels18.in.revels18.models.instagram;

import java.util.List;

import revels18.in.revels18.models.instagram.Caption.Caption;
import revels18.in.revels18.models.instagram.Image.Image;

/**
 * Created by anurag on 8/6/16.
 */
public class InstaFeedModel {

    private String attribution;
    private List<String> tags;
    private String type;
    private Location location;
    private Comment comments;
    private String filter;
    private String created_time;
    private String link;
    private Like likes;
    private Image images;
    private List<String> users_in_photos;
    private Caption caption;
    private boolean user_has_liked;
    private String id;
    private User user;

    public InstaFeedModel() {
    }

    public String getAttribution() {
        return attribution;
    }

    public void setAttribution(String attribution) {
        this.attribution = attribution;
    }

    public Comment getComments() {
        return comments;
    }

    public void setComments(Comment comments) {
        this.comments = comments;
    }

    public String getFilter() {
        return filter;
    }

    public void setFilter(String filter) {
        this.filter = filter;
    }

    public String getCreated_time() {
        return created_time;
    }

    public void setCreated_time(String created_time) {
        this.created_time = created_time;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public Like getLikes() {
        return likes;
    }

    public void setLikes(Like likes) {
        this.likes = likes;
    }

    public Image getImages() {
        return images;
    }

    public void setImages(Image images) {
        this.images = images;
    }

    public List<String> getUsers_in_photos() {
        return users_in_photos;
    }

    public void setUsers_in_photos(List<String> users_in_photos) {
        this.users_in_photos = users_in_photos;
    }

    public Caption getCaption() {
        return caption;
    }

    public void setCaption(Caption caption) {
        this.caption = caption;
    }

    public boolean isUser_has_liked() {
        return user_has_liked;
    }

    public void setUser_has_liked(boolean user_has_liked) {
        this.user_has_liked = user_has_liked;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }
}











































