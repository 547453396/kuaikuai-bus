package cn.kuaikuai.trip.dao;

import java.util.Map;

import org.greenrobot.greendao.AbstractDao;
import org.greenrobot.greendao.AbstractDaoSession;
import org.greenrobot.greendao.database.Database;
import org.greenrobot.greendao.identityscope.IdentityScopeType;
import org.greenrobot.greendao.internal.DaoConfig;

import cn.kuaikuai.trip.model.entity.CacheData;
import cn.kuaikuai.trip.model.entity.UserInfo;

import cn.kuaikuai.trip.dao.CacheDataDao;
import cn.kuaikuai.trip.dao.UserInfoDao;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.

/**
 * {@inheritDoc}
 * 
 * @see org.greenrobot.greendao.AbstractDaoSession
 */
public class DaoSession extends AbstractDaoSession {

    private final DaoConfig cacheDataDaoConfig;
    private final DaoConfig userInfoDaoConfig;

    private final CacheDataDao cacheDataDao;
    private final UserInfoDao userInfoDao;

    public DaoSession(Database db, IdentityScopeType type, Map<Class<? extends AbstractDao<?, ?>>, DaoConfig>
            daoConfigMap) {
        super(db);

        cacheDataDaoConfig = daoConfigMap.get(CacheDataDao.class).clone();
        cacheDataDaoConfig.initIdentityScope(type);

        userInfoDaoConfig = daoConfigMap.get(UserInfoDao.class).clone();
        userInfoDaoConfig.initIdentityScope(type);

        cacheDataDao = new CacheDataDao(cacheDataDaoConfig, this);
        userInfoDao = new UserInfoDao(userInfoDaoConfig, this);

        registerDao(CacheData.class, cacheDataDao);
        registerDao(UserInfo.class, userInfoDao);
    }
    
    public void clear() {
        cacheDataDaoConfig.clearIdentityScope();
        userInfoDaoConfig.clearIdentityScope();
    }

    public CacheDataDao getCacheDataDao() {
        return cacheDataDao;
    }

    public UserInfoDao getUserInfoDao() {
        return userInfoDao;
    }

}
