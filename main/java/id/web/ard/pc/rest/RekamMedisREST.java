/**
 * Ardiansyah | http://ard.web.id
 *
 */

package id.web.ard.pc.rest;

import id.web.ard.pc.auth.Secured;
import id.web.ard.pc.entity.ClusteringResult;
import id.web.ard.pc.entity.RekamMedis;
import id.web.ard.pc.entity.Role;
import id.web.ard.pc.service.RekamMedisService;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
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
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.jboss.resteasy.plugins.providers.multipart.InputPart;
import org.jboss.resteasy.plugins.providers.multipart.MultipartFormDataInput;

/**
 *
 * @author Ardiansyah <ard333.ardiansyah@gmail.com>
 */
@Path("/rekam-medis")
public class RekamMedisREST {
	
	@EJB
	private RekamMedisService rms;
	
	private String responseOK = "{\"response\":\"ok\"}";
	
	@POST @Consumes(MediaType.APPLICATION_JSON)
	@Secured({Role.ADMIN})
	public Response create(@Valid RekamMedis entity) {
		rms.create(entity);
		return Response.ok(responseOK, MediaType.APPLICATION_JSON).build();
	}

	@PUT @Path("/{id}") @Consumes(MediaType.APPLICATION_JSON)
	@Secured({Role.ADMIN})
	public Response edit(@PathParam("id") Integer id, @Valid RekamMedis entity) {
		rms.edit(entity);
		return Response.ok(responseOK, MediaType.APPLICATION_JSON).build();
	}

	@DELETE @Path("/{id}")
	@Secured({Role.ADMIN})
	public Response remove(@PathParam("id") Integer id) {
		rms.remove(rms.find(id));
		return Response.ok(responseOK, MediaType.APPLICATION_JSON).build();
	}

	@GET @Path("/{id}") @Produces(MediaType.APPLICATION_JSON)
	@Secured({Role.ADMIN})
	public RekamMedis find(@PathParam("id") Integer id) {
		return rms.find(id);
	}
	
	@GET @Path("/{from}/{limit}") @Produces(MediaType.APPLICATION_JSON)
	@Secured({Role.ADMIN})
	public List<RekamMedis> findRange(@PathParam("from") Integer from, @PathParam("limit") Integer limit) {
		return rms.findRange(new int[]{from, limit}, null);
	}

	@GET @Path("/count") @Produces(MediaType.TEXT_PLAIN)
	@Secured({Role.ADMIN})
	public String countREST() {
		return String.valueOf(rms.count());
	}
	
	@POST @Path("/excel") @Consumes(MediaType.MULTIPART_FORM_DATA)
	@Secured({Role.ADMIN})
	public Response excel(MultipartFormDataInput input) {
		Map<String, List<InputPart>> uploadForm = input.getFormDataMap();
		List<InputPart> inputParts = uploadForm.get("file");
		try {
			InputStream inputStream = inputParts.get(0).getBody(InputStream.class,null);
			rms.importExcel(inputStream);
		} catch (IOException ex) {
			Logger.getLogger(RekamMedisREST.class.getName()).log(Level.SEVERE, null, ex);
		}
		return Response.ok(responseOK, MediaType.APPLICATION_JSON).build();
	}
	
	@GET @Path("/cluster/{month}/{year}/{size}") @Produces(MediaType.APPLICATION_JSON)
	@Secured({Role.ADMIN})
	public ClusteringResult find(@PathParam("month") Integer month, @PathParam("year") Integer year, @PathParam("size") Integer size) {
		
		if (rms.setup(month, year, size)) {
			return rms.cluster();
		} else {
			return new ClusteringResult();
		}
	}
	
	@GET @Path("/get-year") @Produces(MediaType.APPLICATION_JSON)
	@Secured({Role.ADMIN})
	public List<Integer> findAllByMonthAndYear() {
		return rms.getAllYear();
	}
}
