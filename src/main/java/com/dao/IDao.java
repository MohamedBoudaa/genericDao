package com.dao;

import java.io.Serializable;
import java.util.List;

public interface IDao<PK extends Serializable, T>{

	PK save (T o);
	void update(T o);
	void delete(PK id);
	T findById(PK id);
	List<T> getAll();
	List<T> getByColName(String pColName, String pColVal, String pClassName);
	
	
	
}
