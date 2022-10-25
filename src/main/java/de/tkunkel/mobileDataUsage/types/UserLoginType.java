package de.tkunkel.mobileDataUsage.types;

public class UserLoginType {
    public String alias;
    public String password;
    public String logindata;
    public String csrf_token;

    @Override
    public String toString() {
        return "UserLoginType{" +
                "alias='" + alias + '\'' +
                ", password='" + password + '\'' +
                ", logindata='" + logindata + '\'' +
                ", csrf_token='" + csrf_token + '\'' +
                '}';
    }
}
