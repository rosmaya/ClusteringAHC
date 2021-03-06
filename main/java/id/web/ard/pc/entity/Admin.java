/**
 * Ardiansyah | http://ard.web.id
 *
 */

package id.web.ard.pc.entity;

import java.io.Serializable;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 *
 * @author Ardiansyah <ard333.ardiansyah@gmail.com>
 */
@Entity
@Table(name = "admin")
@NamedQueries({
	@NamedQuery(name = "Admin.login", query = "SELECT a.id FROM Admin a WHERE a.username = :username AND a.password = :password")
})
public class Admin implements Serializable {

	private static final long serialVersionUID = 1L;
	
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
	private Integer id;
	
	@Basic(optional = false)
    @NotNull
    @Size(min = 2, max = 64)
    @Column(name = "nama")
	private String nama;
	
	@Basic(optional = false)
    @NotNull
    @Size(min = 3, max = 16)
    @Column(name = "username")
	private String username;
	
	@Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 255)
    @Column(name = "password")
	private String password;

	public Admin() {
	}

	public Admin(Integer id) {
		this.id = id;
	}

	public Admin(Integer id, String nama, String username, String password) {
		this.id = id;
		this.nama = nama;
		this.username = username;
		this.password = password;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getNama() {
		return nama;
	}

	public void setNama(String nama) {
		this.nama = nama;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	@Override
	public int hashCode() {
		int hash = 0;
		hash += (id != null ? id.hashCode() : 0);
		return hash;
	}

	@Override
	public boolean equals(Object object) {
		// TODO: Warning - this method won't work in the case the id fields are not set
		if (!(object instanceof Admin)) {
			return false;
		}
		Admin other = (Admin) object;
		if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return "id.web.ard.pc.entity.Admin[ id=" + id + " ]";
	}

}
