package com.sope.domain;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.inject.Inject;

import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.hibernate.StatelessSession;
import org.hibernate.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;


@Service
public class EntityRepository {
    private static final Logger LOGGER = LoggerFactory.getLogger(EntityRepository.class);
    @Inject
    private SessionFactory sessionFactory;

    public <T> List<T> get(String hql, Map<Object, Object> parameters) {
        Query query = createQuery(hql);
        if (parameters != null) {
            query.setProperties(parameters);
        }
        return query.list();
    }
    public <T> List<T> get(String hql, Map<Object, Object> parameters, int currentPage, int itemCount) {
        Query query = createQuery(hql);
        if (parameters != null) {
            query.setProperties(parameters);
        }
        query.setFirstResult(currentPage);
        query.setMaxResults(itemCount);
        return query.list();
    }
    public <T> Optional<T> getUnique(String hql, Map<Object, Object> parameters) {
        Query query = createQuery(hql);
        if (parameters != null) {
            query.setProperties(parameters);
        }
        return (Optional<T>) Optional.ofNullable(query.uniqueResult());
    }

    private Query createQuery(String hql) {
        return sessionFactory.getCurrentSession().createQuery(hql);
    }

    public <T extends IdEntity> void save(IdEntity idEntity) {
        // remove this. Fast fix
        if (idEntity instanceof CommonTableProperties && ((CommonTableProperties)idEntity).getCreated() == null) {
            ((CommonTableProperties)idEntity).setCreated(new Date());
        }
        sessionFactory.getCurrentSession().save(idEntity);

    }

    public <T extends IdEntity> void delete(IdEntity idEntity) {
        sessionFactory.getCurrentSession().delete(idEntity);
    }

    public <T extends IdEntity> void statelessSave(List<T> idEntity) {
        StatelessSession session = sessionFactory.openStatelessSession();
        Transaction transaction = session.beginTransaction();
        int i = 0;
        for (IdEntity idEntity2 : idEntity) {
            session.insert(idEntity2);
            if (i % 50 == 0) {
                LOGGER.info("Saving shows " + i + " / " + idEntity.size());
            }
            ++i;

        }
        transaction.commit();
        session.close();
    }

    public void delete(String hql, Map<Object, Object> parameters) {
        Query query = createQuery(hql);
        if (parameters != null) {
            query.setProperties(parameters);
        }
        query.executeUpdate();
    }

}
