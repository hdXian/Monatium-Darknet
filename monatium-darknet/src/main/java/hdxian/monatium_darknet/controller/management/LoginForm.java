package hdxian.monatium_darknet.controller.management;

import lombok.Data;

@Data
public class LoginForm {
    private String loginId;
    private String password;

    public LoginForm() {
    }

    public LoginForm(String loginId, String password) {
        this.loginId = loginId;
        this.password = password;
    }

}
