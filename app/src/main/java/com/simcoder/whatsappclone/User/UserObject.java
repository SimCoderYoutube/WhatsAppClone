package com.simcoder.whatsappclone.User;

public class UserObject {

    private String  uid,
                    name,
                    phone;

    public UserObject(String uid, String name, String phone){
        this.uid = uid;
        this.name = name;
        this.phone = phone;
    }

    public String getUid() {
        return uid;
    }
    public String getPhone() {
        return phone;
    }
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
