package com.dao;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import org.apache.log4j.Logger;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

public class HibernateGenericDao<PK extends Serializable, T> implements IDao<PK, T> {

	private SessionFactory sf;
	
	private Class boClass;

	
	/** Utilisé par tous les DAOs */
	protected final Logger LOGGER;
	
	public HibernateGenericDao(Class boClass) {
		
		LOGGER = Logger.getLogger(HibernateGenericDao.class);

		LOGGER.debug("HibernateGenericDao created");

		sf = SessionFactoryBuilder.getSessionFactory();
		this.boClass = boClass;

	}

	

	
	
	public PK save(T o) {
		LOGGER.debug("start save method");

		PK id = null ;
		Session s = null;
		Transaction tx = null;
		try {
			s = sf.getCurrentSession();
			tx =  s.beginTransaction();
			
			id = (PK) s.save(o); 			
			
			tx.commit();

		} catch (HibernateException e) {
			LOGGER.debug("error due to :" + e);

			
			if(tx !=null) {
				tx.rollback();
			}
			
			throw new DaoException(e);
			
		} finally {
			
			if(s !=null && s.isOpen()) {
				s.close();
			}

		}

		return id;
	}

	public void update(T o) {

		Session s = null;
		Transaction tx = null;
		try {
			s = sf.getCurrentSession();
			tx =  s.beginTransaction();
			
			s.update(o); 			
			
			tx.commit();

		} catch (HibernateException e) {
			LOGGER.debug("error due to :" + e);

			
			if(tx !=null) {
				tx.rollback();
			}
			
			throw new DaoException(e);
			
		} finally {
			
			if(s !=null && s.isOpen()) {
				s.close();
			}

		}


	}

	public void delete(PK id) {
		Session s = null;
		Transaction tx = null;
		try {
			s = sf.getCurrentSession();
			tx =  s.beginTransaction();
			
			s.delete(id);		
			
			tx.commit();

		} catch (HibernateException e) {
			LOGGER.debug("error due to :" + e);

			
			if(tx !=null) {
				tx.rollback();
			}
			throw new DaoException(e);
			
		} finally {
			
			if(s !=null && s.isOpen()) {
				s.close();
			}

		}

	}

	public T findById(PK id) {
		
		Session s = null;
		Transaction tx = null;
		T o = null;
		try {
			s = sf.getCurrentSession();
			tx =  s.beginTransaction();
			
		    o = (T)	s.find(boClass, id);	
			
			tx.commit();

		} catch (HibernateException e) {
			LOGGER.debug("error due to :" + e);

			
			if(tx !=null) {
				tx.rollback();
			}
			throw new DaoException(e);
		} finally {
			
			if(s !=null && s.isOpen()) {
				s.close();
			}

		}
		
		return o;
	}

	public List<T> getAll() {

		Session session = null;
		Transaction tx = null;
		List<T> list = new ArrayList<T>();
		try {

			// on obtient une session
			session = sf.getCurrentSession();

			// On commence une transaction
			tx = session.beginTransaction();

			CriteriaBuilder builder = session.getCriteriaBuilder();
			CriteriaQuery<T> query = builder.createQuery(boClass);
			Root<T> root = query.from(boClass);
			query.select(root);
			Query<T> q = session.createQuery(query);
			list = q.getResultList();

			tx.commit();
		} catch (HibernateException ex) {
			LOGGER.debug("error due to :" + ex);

			// Si il y a des problèmes et une transaction a été déjà crée on l'annule
			if (tx != null) {
				// Annulation d'une transaction
				tx.rollback();

			}

			// On n'oublie pas de remonter l'erreur originale
			throw new DaoException(ex);

		} finally {

			// Si la session n'est pas encore fermée par commit
			if (session != null && session.isOpen()) {
				session.close();
			}

		}

		return list;

	}

	public List<T> getByColName(String pColName, String pColVal, String pClassName) {


		// On obtient la session en cours
		Session s = sf.getCurrentSession();

		List<T> list = new ArrayList<T>();



		Transaction tx = null;

		try {

			// On démarre une transaction localement
			tx = s.beginTransaction();

			Query q = s.createQuery("from " + pClassName + " where " + pColName + "=:cne");
			q.setParameter("cne", pColVal);
			list = q.list();

			tx.commit();
		} catch (HibernateException ex) {
			
			LOGGER.debug("error due to :" + ex);

			// Si il y a des problèmes et une transaction a été déjà crée on l'annule
			if (tx != null) {
				// Annulation d'une transaction
				tx.rollback();

			}

			// On n'oublie pas de remonter l'erreur originale
			throw new DaoException(ex);
		} finally {
			// Si la session n'est pas encore fermée par commit
			if (s != null && s.isOpen()) {
				s.close();
			}
		}

		return list;
	}

}
