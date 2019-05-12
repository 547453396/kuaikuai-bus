package cn.kuaikuai.trip.model.entity;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Index;
import org.greenrobot.greendao.annotation.Property;

/**
 * 缓存实体表，用于存储一些缓存数据，通过key/value形式
 */

@Entity
public class CacheData {

    @Id(autoincrement = true)
    private Long id;
    @Property
    @Index(unique = true)
    private String key;
    @Property
    private String value;

    @Generated(hash = 96704304)
    public CacheData(Long id, String key, String value) {
        this.id = id;
        this.key = key;
        this.value = value;
    }

    @Generated(hash = 1582791643)
    public CacheData() {
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
