package com.dao;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

public class SessionFactoryBuilder {
	private static SessionFactory sessionFactory;

	public static SessionFactory getSessionFactory() {
		if (sessionFactory == null) {
			sessionFactory = new Configuration().configure().buildSessionFactory();

		}

		return sessionFactory;
	}

}
