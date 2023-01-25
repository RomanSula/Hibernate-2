package com.javarush.dao;

import jakarta.persistence.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import java.util.List;

public abstract class GenericDAO<T> {
    private final Class<T> clazz;
    private SessionFactory sessionFactory;

    protected GenericDAO(final Class<T> clazzToSet, SessionFactory sessionFactory) {
        this.clazz = clazzToSet;
        this.sessionFactory = sessionFactory;
    }

    public T getById(final int id){
        return (T)getCurrentSession().get(clazz, id);
    }

    public List<T> getItems(int offset, int count){
        Query query = getCurrentSession().createQuery("from " + clazz.getName(), clazz);
        query.setFirstResult(offset);
        query.setMaxResults(count);
        return query.getResultList();
    }

    public List<T> findAll(){
        return getCurrentSession().createQuery("from " + clazz.getName(), clazz).list();
    }

    public T save(final T entity){
        getCurrentSession().saveOrUpdate(entity);
        return entity;
    }

    public T update(final T entity){
        return (T) getCurrentSession().merge(entity);
    }

    public void delete(final T entity){
        getCurrentSession().delete(entity);
    }

    public void deleteById(final int entityId){
        delete(getById(entityId));
    }

    protected Session getCurrentSession(){
        return sessionFactory.getCurrentSession();
    }
}
