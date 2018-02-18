package mitrev.in.mitrev18.models.instagram.Caption;

/**
 * Created by Naman on 8/31/2016.
 */
public class Caption {

    private String created_time;
    private String text;
    private From from;
    private String id;

    public Caption() {
    }

    public String getCreated_time() {
        return created_time;
    }

    public void setCreated_time(String created_time) {
        this.created_time = created_time;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public From getFrom() {
        return from;
    }

    public void setFrom(From from) {
        this.from = from;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
