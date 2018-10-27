package com.simcoder.whatsappclone.User;

import java.io.Serializable;

public class UserObject implements Serializable {

    private String  uid,
                    name,
                    phone,
                    notificationKey;

    public UserObject(String uid){
        this.uid = uid;
    }
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
    public String getNotificationKey() {
        return notificationKey;
    }

    public void setName(String name) {
        this.name = name;
    }
    public void setNotificationKey(String notificationKey) {
        this.notificationKey = notificationKey;
    }
}
