/**
 * Ardiansyah | http://ard.web.id
 *
 */

package id.web.ard.pc.service;

import id.web.ard.pc.entity.Admin;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

/**
 *
 * @author Ardiansyah <ard333.ardiansyah@gmail.com>
 */
@Stateless
public class AdminService extends AbstractService<Admin> {
	
	@PersistenceContext(unitName = "pcPU")
	private EntityManager em;

	@Override
	protected EntityManager getEntityManager() {
		return em;
	}

	public AdminService() {
		super(Admin.class);
	}
	
	public Integer login(String username, String password) {
		TypedQuery<Integer> tq = this.getEntityManager().createNamedQuery("Admin.login", Integer.class);
		tq.setParameter("username", username);
		tq.setParameter("password", PasswordService.hashPassword(password.toCharArray()));
		
		try {
			return tq.getSingleResult();
		} catch (Exception e) {
			return null;
		}
	}
	
}
