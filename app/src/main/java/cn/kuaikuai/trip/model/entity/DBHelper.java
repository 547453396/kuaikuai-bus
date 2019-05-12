package cn.kuaikuai.trip.model.entity;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import org.greenrobot.greendao.database.Database;

import cn.kuaikuai.trip.MainApplication;
import cn.kuaikuai.trip.dao.CacheDataDao;
import cn.kuaikuai.trip.dao.DaoMaster;
import cn.kuaikuai.trip.dao.UserInfoDao;

public class DBHelper extends DaoMaster.OpenHelper {
    public static final String DB_NAME = "kuaikuai.db";

    public DBHelper(Context context) {
        super(context, DB_NAME, null);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        MigrationHelper.migrate(db, new MigrationHelper.ReCreateAllTableListener() {
            @Override
            public void onCreateAllTables(Database db, boolean ifNotExists) {
                DaoMaster.createAllTables(db, ifNotExists);
            }
            @Override
            public void onDropAllTables(Database db, boolean ifExists) {
                DaoMaster.dropAllTables(db, ifExists);
            }
        },UserInfoDao.class, CacheDataDao.class);
    }

    /**
     * 根据key查询缓存数据
     * @param key
     * @return
     */
    public static String getUserInfoByKey(String key){
        UserInfoDao userInfoDao = MainApplication.getApplication().getDaoSession().getUserInfoDao();
        UserInfo userInfo = userInfoDao.queryBuilder().where(UserInfoDao.Properties.Key.eq(key)).unique();
        if (userInfo != null){
            return userInfo.getValue();
        }else {
            return "";
        }
    }

    /**
     * 插入或替换一条数据到缓存
     * @param key
     * @param value
     * @return
     */
    public static long insertUserInfo(String key, String value) {
        UserInfoDao userInfoDao = MainApplication.getApplication().getDaoSession().getUserInfoDao();
        return userInfoDao.insertOrReplace(new UserInfo(null, key, value));
    }

    /**
     * 退出登录清空用户信息
     */
    public static void cleanUserInfo(){
        UserInfoDao userInfoDao = MainApplication.getApplication().getDaoSession().getUserInfoDao();
        userInfoDao.deleteAll();
    }

    /**
     * 根据key查询缓存数据
     * @param key
     * @return
     */
    public static String getCacheDataByKey(String key){
        CacheDataDao cacheDataDao = MainApplication.getApplication().getDaoSession().getCacheDataDao();
        CacheData cacheData = cacheDataDao.queryBuilder().where(CacheDataDao.Properties.Key.eq(key)).unique();
        if (cacheData != null){
            return cacheData.getValue();
        }else {
            return "";
        }
    }

    /**
     * 插入或替换一条数据到缓存
     * @param key
     * @param value
     * @return
     */
    public static long insertCacheData(String key, String value) {
        CacheDataDao cacheDataDao = MainApplication.getApplication().getDaoSession().getCacheDataDao();
        return cacheDataDao.insertOrReplace(new CacheData(null, key, value));
    }
}

