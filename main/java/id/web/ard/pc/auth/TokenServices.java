/**
 * Ardiansyah | http://ard.web.id
 *
 */

package id.web.ard.pc.auth;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.SignatureException;
import io.jsonwebtoken.UnsupportedJwtException;
import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import javax.crypto.spec.SecretKeySpec;
import javax.xml.bind.DatatypeConverter;

/**
 *
 * @author Ardiansyah <ard333.ardiansyah@gmail.com>
 */
public class TokenServices {
	
	private static final Key signingKey = new SecretKeySpec(
		DatatypeConverter.parseBase64Binary("ard333-forPC"), SignatureAlgorithm.HS512.getJcaName()
	);
	
	public static String createToken(Integer id, String role, long expired) {
		return Jwts.builder()
					.setSubject(id.toString())
					.claim("role", role)
					.signWith(SignatureAlgorithm.HS512, signingKey)
					.setExpiration(new Date(expired))
					.compact();
	}
	
	public static HashMap.Entry<Integer, String> validateToken(String token)throws ExpiredJwtException, UnsupportedJwtException, MalformedJwtException, SignatureException, IllegalArgumentException {
		Claims claims = Jwts.parser().setSigningKey(signingKey).parseClaimsJws(token).getBody();
		String role = null;
		Integer id = null;
		
		role = claims.get("role").toString();
		id = Integer.parseInt(claims.getSubject());
		Jwts.parser()
			.requireSubject(claims.getSubject())
			.setSigningKey(signingKey)
			.parseClaimsJws(token);
		
		return new HashMap.SimpleEntry<>(id, role);
	}
	
	
}
