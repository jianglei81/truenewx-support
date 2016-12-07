package org.truenewx.verify.data.dao;

import java.util.Date;

import org.truenewx.data.orm.dao.UnityDao;
import org.truenewx.verify.data.model.VerifyUnity;

/**
 * 验证信息实体DAO
 *
 * @author jianglei
 * @since JDK 1.8
 * @param <U>
 *            验证单体类型
 * @param <T>
 *            验证类型枚举类型
 */
public interface VerifyUnityDao<U extends VerifyUnity<T>, T extends Enum<T>>
        extends UnityDao<U, Long> {

    U findByCode(String code);

    int deleteByLatestExpiredTime(Date latestExpiredTime);

}
