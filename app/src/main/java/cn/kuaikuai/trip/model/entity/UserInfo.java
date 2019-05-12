package cn.kuaikuai.trip.model.entity;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Index;
import org.greenrobot.greendao.annotation.Property;

@Entity
public class UserInfo {

    @Id(autoincrement = true)
    private Long id;
    @Index(unique = true)
    @Property
    private String key;
    @Property
    private String value;

    @Generated(hash = 393019810)
    public UserInfo(Long id, String key, String value) {
        this.id = id;
        this.key = key;
        this.value = value;
    }

    @Generated(hash = 1279772520)
    public UserInfo() {
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getKey() {
        return this.key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getValue() {
        return this.value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
