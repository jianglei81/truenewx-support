package org.truenewx.support.batch.item;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.truenewx.data.model.Entity;
import org.truenewx.data.orm.dao.EntityDao;

/**
 * 实体项目批处理
 *
 * @author jianglei
 * @version 1.0.0 2015年12月23日
 * @since JDK 1.8
 */
public abstract class EntityItemlet<T extends Entity, D extends EntityDao<T>>
        implements Itemlet<T, T> {

    protected D dao;

    @Autowired
    public void setDao(final D dao) {
        this.dao = dao;
    }

    @Override
    public int getCommitInterval() {
        return 10;
    }

    @Override
    public void write(final List<? extends T> items) throws Exception {
        for (final T item : items) {
            this.dao.save(item);
        }
    }
}
