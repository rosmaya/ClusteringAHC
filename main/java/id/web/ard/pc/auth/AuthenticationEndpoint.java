/**
 * Ardiansyah | http://ard.web.id
 *
 */

package id.web.ard.pc.auth;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import id.web.ard.pc.entity.Role;
import id.web.ard.pc.service.AdminService;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureException;
import io.jsonwebtoken.UnsupportedJwtException;
import java.util.Calendar;
import java.util.HashMap;
import javax.ejb.EJB;
import javax.json.Json;
import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 *
 * @author Ardiansyah <ard333.ardiansyah@gmail.com>
 */
@Path("/auth")
public class AuthenticationEndpoint {
	
	@EJB
	private AdminService as;
	
	@POST @Path("/admin")
	@Produces("application/json") @Consumes("application/x-www-form-urlencoded")
	public Response authenticateAdmin(@FormParam("username") String username, @FormParam("password") String password) {
		Integer id = as.login(username, password);
		if (id!=null) {
			return Response.ok(new Token(createToken(id, String.valueOf(Role.ADMIN))), MediaType.APPLICATION_JSON).build();
		} else {
			return Response.status(Response.Status.UNAUTHORIZED).build();
		}
	}
	
	@POST @Path("/check")
	@Produces("application/json") @Consumes("application/json")
	public Response checkToken(Token token) {
		try {
			HashMap.Entry<Integer, String> result = TokenServices.validateToken(token.getToken());
			return Response.ok(
				Json.createObjectBuilder().add("id", result.getKey()).add("role", result.getValue()).build(),
				MediaType.APPLICATION_JSON)
			.build();
		} catch (ExpiredJwtException | UnsupportedJwtException | MalformedJwtException | SignatureException | IllegalArgumentException e) {
			return Response.status(Response.Status.UNAUTHORIZED).build();
		}
	}
	
	private String createToken(Integer id, String role) {
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.HOUR_OF_DAY, 3);
		return TokenServices.createToken(id, role, cal.getTimeInMillis());
	}
	
	public static class Token {
		private String token;
		public Token(){
		}
		@JsonCreator
		public Token(@JsonProperty("token") String token) {
			this.token = token;
		}
		public String getToken() {
			return token;
		}
		public void setToken(String token) {
			this.token = token;
		}
	}
}