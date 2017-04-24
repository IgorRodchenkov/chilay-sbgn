package org.ivis.util;

import java.util.List;
import java.util.Map;

import org.ivis.util.QuickSort;

/**
 * This class is used for sorting an Object list or array according to a mapped
 * index for each Object.
 * 
 * @author Esat Belviranli
 *
 * Copyright: i-Vis Research Group, Bilkent University, 2007 - present
 */
public class IndexedObjectSort extends QuickSort
{
	/**
	 * Holds a mapping between an Object and index on which the sorting will be 
	 * based.
	 */
	private Map<Object, Double> indexMapping;
	
	/**
	 * Constructor.
	 *
	 * @param objectList list of objects
	 * @param indexMapping object-to-value index map
	 */
	public IndexedObjectSort(List<Object> objectList,
		Map<Object, Double> indexMapping)
	{
		super(objectList);
		
		this.indexMapping = indexMapping;
	}
	
	/**
	 * Constructor.
	 *
	 * @param objectArray array of objects
	 * @param indexMapping object-to-value index map
	 */
	public IndexedObjectSort(Object[] objectArray, Map<Object, Double> indexMapping)
	{
		super(objectArray);
		
		this.indexMapping = indexMapping;
	}

	/**
	 * This method is required by QuickSort. In this case, comparison is based
	 * on indexes given by the mapping above.
	 *
	 * @param a one indexed object
	 * @param b another indexed object
	 *
	 * @return true when index of b &gt; index of a; otherwise - false
	 */
	public boolean compare(Object a, Object b)
	{
		assert indexMapping.get(b) != null && indexMapping.get(a) != null;
		return indexMapping.get(b) > indexMapping.get(a);
	}
}