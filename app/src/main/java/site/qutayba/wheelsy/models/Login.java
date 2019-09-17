package site.qutayba.wheelsy.models;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;

public class Login  extends BaseObservable {
    private String email;
    private String password;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
