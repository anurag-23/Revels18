package mitrev.in.mitrev18.models.registration;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by anurag on 14/2/18.
 */
public class ProfileResponse {
    @Expose
    @SerializedName("student_name")
    private String name;

    @Expose
    @SerializedName("student_delno")
    private String delegateID;

    @Expose
    @SerializedName("student_regno")
    private String regNo;

    @Expose
    @SerializedName("student_mail")
    private String email;

    @Expose
    @SerializedName("student_college")
    private String college;

    @Expose
    @SerializedName("student_phone")
    private String phoneNo;

    @Expose
    @SerializedName("qr")
    private String qr;

    @Expose
    @SerializedName("status")
    private Integer status;

    @Expose
    @SerializedName("message")
    private String message;

    @Expose
    @SerializedName("event_data")
    private List<RegEvent> eventData;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDelegateID() {
        return delegateID;
    }

    public void setDelegateID(String delegateID) {
        this.delegateID = delegateID;
    }

    public String getRegNo() {
        return regNo;
    }

    public void setRegNo(String regNo) {
        this.regNo = regNo;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getCollege() {
        return college;
    }

    public void setCollege(String college) {
        this.college = college;
    }

    public String getPhoneNo() {
        return phoneNo;
    }

    public void setPhoneNo(String phoneNo) {
        this.phoneNo = phoneNo;
    }

    public String getQr() {
        return qr;
    }

    public void setQr(String qr) {
        this.qr = qr;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<RegEvent> getEventData() {
        return eventData;
    }

    public void setEventData(List<RegEvent> eventData) {
        this.eventData = eventData;
    }
}
