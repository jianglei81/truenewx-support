package org.truenewx.support.batch.core.listener;

import org.hibernate.FlushMode;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.dao.DataAccessResourceFailureException;

/**
 * 使Hibernate Session线程安全的作业执行侦听器
 *
 * @author jianglei
 * @since JDK 1.8.0
 */
public class ConcurrentHibernateSessionJobExecutionListener implements JobExecutionListener {

    private static final ThreadLocal<Session> SESSION_CACHE = new ThreadLocal<>();

    private SessionFactory sessionFactory;

    public void setSessionFactory(final SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @Override
    public void beforeJob(final JobExecution jobExecution) {
        Session session = SESSION_CACHE.get();
        if (session == null) {
            session = openSession();
            SESSION_CACHE.set(session);
        }
    }

    protected Session openSession() throws DataAccessResourceFailureException {
        if (this.sessionFactory != null) {
            try {
                final Session session = this.sessionFactory.openSession();
                session.setHibernateFlushMode(FlushMode.MANUAL);
                return session;
            } catch (final HibernateException ex) {
                throw new DataAccessResourceFailureException("Could not open Hibernate Session",
                        ex);
            }
        }
        return null;
    }

    @Override
    public void afterJob(final JobExecution jobExecution) {
        final Session session = SESSION_CACHE.get();
        if (session != null) {
            SESSION_CACHE.remove();
            session.close();
        }
    }

}
