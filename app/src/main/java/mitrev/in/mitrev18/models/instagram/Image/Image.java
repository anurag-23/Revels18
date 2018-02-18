package mitrev.in.mitrev18.models.instagram.Image;

/**
 * Created by Naman on 8/31/2016.
 */
public class Image {

    private LowResolution low_resolution;
    private Thumbnail thumbnail;
    private StandardResolution standard_resolution;

    public Image() {
    }

    public LowResolution getLow_resolution() {
        return low_resolution;
    }

    public void setLow_resolution(LowResolution low_resolution) {
        this.low_resolution = low_resolution;
    }

    public StandardResolution getStandard_resolution() {
        return standard_resolution;
    }

    public void setStandard_resolution(StandardResolution standard_resolution) {
        this.standard_resolution = standard_resolution;
    }

    public Thumbnail getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(Thumbnail thumbnail) {
        this.thumbnail = thumbnail;
    }
}
