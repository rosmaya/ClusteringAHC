/**
 * Ardiansyah | http://ard.web.id
 *
 */

package id.web.ard.pc.filter;

import id.web.ard.pc.auth.Secured;
import id.web.ard.pc.auth.TokenServices;
import id.web.ard.pc.entity.Role;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureException;
import io.jsonwebtoken.UnsupportedJwtException;
import java.io.IOException;
import java.lang.reflect.AnnotatedElement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import javax.annotation.Priority;
import javax.ws.rs.NotAuthorizedException;
import javax.ws.rs.Priorities;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.ResourceInfo;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Provider;

/**
 *
 * @author Ardiansyah <ard333.ardiansyah@gmail.com>
 */
@Secured
@Provider
@Priority(Priorities.AUTHORIZATION)
public class AuthorizationFilter implements ContainerRequestFilter{
	
	@Context
	private ResourceInfo resourceInfo;

	@Override
	public void filter(ContainerRequestContext requestContext) throws IOException {
		String authorizationHeader = requestContext.getHeaderString(HttpHeaders.AUTHORIZATION);
		
		if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
			throw new NotAuthorizedException("Authorization header must be provided");
		}
		String token = authorizationHeader.substring("Bearer".length()).trim();
		
		try {
			HashMap.Entry<Integer, String> userInfo = TokenServices.validateToken(token);
			Role userRole = Role.valueOf(userInfo.getValue());
			
			List<Role> classRoles = extractRoles(resourceInfo.getResourceClass());
			List<Role> methodRoles = extractRoles(resourceInfo.getResourceMethod());
			
			if (methodRoles.contains(Role.SELF)) {
				requestContext.getHeaders().add("id", userInfo.getKey().toString());
				methodRoles.remove(Role.SELF);
			}
			if (methodRoles.size() > 0) {
				if (!methodRoles.contains(userRole)) {
					requestContext.abortWith(Response.status(Response.Status.FORBIDDEN).build());
				}
			}
			if (classRoles.size() > 0) {
				if (!classRoles.contains(userRole)) {
					requestContext.abortWith(Response.status(Response.Status.FORBIDDEN).build());
				}
			}
		} catch (ExpiredJwtException | UnsupportedJwtException | MalformedJwtException | SignatureException | IllegalArgumentException e) {
			requestContext.abortWith(Response.status(Response.Status.UNAUTHORIZED).build());
		}
		
	}

	private ArrayList<Role> extractRoles(AnnotatedElement annotatedElement) {
		ArrayList<Role> roles = new ArrayList<>();
		if (annotatedElement == null) {
			return roles;
		} else {
			Secured secured = annotatedElement.getAnnotation(Secured.class);
			if (secured == null) {
				return roles;
			} else {
				Role[] allowedRoles = secured.value();
				return new ArrayList<Role>(Arrays.asList(allowedRoles));
			}
		}
	}
	
}
