/**
 * Ardiansyah | http://ard.web.id
 *
 */

package id.web.ard.pc.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.persistence.EntityManager;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Root;

/**
 *
 * @author Ardiansyah <ard333.ardiansyah@gmail.com>
 * @param <T>
 */
public abstract class AbstractService<T> {

	private Class<T> entityClass;

	public AbstractService(Class<T> entityClass) {
		this.entityClass = entityClass;
	}

	protected abstract EntityManager getEntityManager();

	public void create(T entity) {
		getEntityManager().persist(entity);
	}

	public void edit(T entity) {
		getEntityManager().merge(entity);
	}

	public void remove(T entity) {
		getEntityManager().remove(getEntityManager().merge(entity));
	}

	public T find(Object id) {
		return getEntityManager().find(entityClass, id);
	}

	public List<T> findAll(HashMap<String, JoinType> join) {
		javax.persistence.criteria.CriteriaQuery cq = getEntityManager().getCriteriaBuilder().createQuery(entityClass);
		Root<T> entity = cq.from(entityClass);
		if (join != null) {
			for (Map.Entry<String, JoinType> entry : join.entrySet()) {
				entity.fetch(entry.getKey(), entry.getValue());
			}
		}
		cq.select(entity);
		return getEntityManager().createQuery(cq).getResultList();
	}

	public List<T> findRange(int[] range, HashMap<String, JoinType> join) {
		javax.persistence.criteria.CriteriaQuery cq = getEntityManager().getCriteriaBuilder().createQuery(entityClass);
		Root<T> entity = cq.from(entityClass);
		
		if (join != null) {
			for (Map.Entry<String, JoinType> entry : join.entrySet()) {
				entity.fetch(entry.getKey(), entry.getValue());
			}
		}
		cq.select(entity);
		cq.orderBy(getEntityManager().getCriteriaBuilder().desc(entity.get("id")));
		
		javax.persistence.Query q = getEntityManager().createQuery(cq);
		q.setFirstResult(range[0]);
		q.setMaxResults(range[1]);
		return q.getResultList();
	}

	public int count() {
		javax.persistence.criteria.CriteriaQuery cq = getEntityManager().getCriteriaBuilder().createQuery();
		javax.persistence.criteria.Root<T> rt = cq.from(entityClass);
		cq.select(getEntityManager().getCriteriaBuilder().count(rt));
		javax.persistence.Query q = getEntityManager().createQuery(cq);
		return ((Long) q.getSingleResult()).intValue();
	}

}
