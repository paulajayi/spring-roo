package org.springframework.roo.project.maven.pom;

/**
 * Filter listener interface that clients can implement in order
 * to be notified of changes to project filters
 * 
 * @author Alan Stewart
 * @since 1.1
 */
public interface FilterListener {

	void filterAdded(Filter filter);

	void filterRemoved(Filter filter);
}