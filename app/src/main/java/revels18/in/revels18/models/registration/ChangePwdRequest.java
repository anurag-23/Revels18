package revels18.in.revels18.models.registration;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by anurag on 15/2/18.
 */
public class ChangePwdRequest {
    @Expose
    @SerializedName("newpass")
    private String newPwd;

    @Expose
    @SerializedName("repeatnewpass")
    private String confirmPwd;

    public String getNewPwd() {
        return newPwd;
    }

    public void setNewPwd(String newPwd) {
        this.newPwd = newPwd;
    }

    public String getConfirmPwd() {
        return confirmPwd;
    }

    public void setConfirmPwd(String confirmPwd) {
        this.confirmPwd = confirmPwd;
    }
}
