/**
 * Ardiansyah | http://ard.web.id
 *
 */

package id.web.ard.pc.rest;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import id.web.ard.pc.auth.Secured;
import id.web.ard.pc.entity.Admin;
import id.web.ard.pc.entity.Role;
import id.web.ard.pc.service.AdminService;
import id.web.ard.pc.service.PasswordService;
import java.util.List;
import javax.ejb.EJB;
import javax.validation.Valid;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 *
 * @author Ardiansyah <ard333.ardiansyah@gmail.com>
 */
@Path("/admin")
public class AdminREST {
	
	@EJB
	private AdminService as;
	
	private String responseOK = "{\"response\":\"ok\"}";
	
	@POST @Consumes(MediaType.APPLICATION_JSON)
	@Secured({Role.ADMIN})
	public Response create(@Valid Admin entity) {
		entity.setPassword(PasswordService.hashPassword(entity.getPassword().toCharArray()));
		as.create(entity);
		return Response.ok(responseOK, MediaType.APPLICATION_JSON).build();
	}

	@PUT @Path("/{id}") @Consumes(MediaType.APPLICATION_JSON)
	@Secured({Role.ADMIN})
	public Response edit(@PathParam("id") Integer id, @Valid Admin entity) {
		as.edit(entity);
		return Response.ok(responseOK, MediaType.APPLICATION_JSON).build();
	}

	@DELETE @Path("/{id}")
	@Secured({Role.ADMIN})
	public Response remove(@PathParam("id") Integer id) {
		as.remove(as.find(id));
		return Response.ok(responseOK, MediaType.APPLICATION_JSON).build();
	}

	@GET @Path("/{id}") @Produces(MediaType.APPLICATION_JSON)
	@Secured({Role.ADMIN})
	public Admin find(@PathParam("id") Integer id) {
		return as.find(id);
	}
	
	@GET @Path("/{from}/{limit}") @Produces(MediaType.APPLICATION_JSON)
	@Secured({Role.ADMIN})
	public List<Admin> findRange(@PathParam("from") Integer from, @PathParam("limit") Integer limit) {
		return as.findRange(new int[]{from, limit}, null);
	}

	@GET @Path("/count") @Produces(MediaType.TEXT_PLAIN)
	@Secured({Role.ADMIN})
	public String countREST() {
		return String.valueOf(as.count());
	}
	
	@PUT @Path("/change-password") @Consumes(MediaType.TEXT_PLAIN)
	@Secured({Role.ADMIN, Role.SELF})
	public Response changePassword(@Valid Password password, @Context HttpHeaders headers) {
		Integer idAdmin = Integer.parseInt(headers.getHeaderString("id"));
		Admin m = as.find(idAdmin);
		if (m.getPassword().equals(PasswordService.hashPassword(password.getOldp().toCharArray()))) {
			m.setPassword(PasswordService.hashPassword(password.getNewp().toCharArray()));
			as.edit(m);
			return Response.ok(responseOK, MediaType.APPLICATION_JSON).build();
		} else {
			return Response.status(Response.Status.UNAUTHORIZED).build();
		}
	}
	
	@PUT @Path("/edit-profile") @Consumes(MediaType.APPLICATION_JSON)
	@Secured({Role.ADMIN, Role.SELF})
	public Response edit(@Valid Admin entity, @Context HttpHeaders headers) {
		Integer idAdmin = Integer.parseInt(headers.getHeaderString("id"));
		Admin m = as.find(idAdmin);
		m.setNama(entity.getNama());
		as.edit(m);
		return Response.ok(responseOK, MediaType.APPLICATION_JSON).build();
	}
	
	public static class Password {
		private String oldp;
		private String newp;
		public Password(){
		}
		@JsonCreator
		public Password(@JsonProperty("oldp") String oldp, @JsonProperty("newp") String newp) {
			this.oldp = oldp;
			this.newp = newp;
		}
		public String getOldp() {
			return oldp;
		}
		public void setOldp(String oldp) {
			this.oldp = oldp;
		}
		public String getNewp() {
			return newp;
		}
		public void setNewp(String newp) {
			this.newp = newp;
		}
	}
}
