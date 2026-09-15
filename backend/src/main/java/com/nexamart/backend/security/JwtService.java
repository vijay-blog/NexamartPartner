package com.nexamart.backend.security;

import com.nexamart.backend.config.AppProperties;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Service;
import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;
import java.util.Map;

@Service
public class JwtService {
  private final AppProperties props;
  public JwtService(AppProperties props){this.props=props;}

  @PostConstruct
  void validateConfiguration(){
    String secret=props.getJwtSecret();
    if(secret==null || secret.isBlank()) throw new IllegalStateException("JWT_SECRET is missing. Set it in Railway environment variables.");
    if(secret.getBytes(StandardCharsets.UTF_8).length < 32) throw new IllegalStateException("JWT_SECRET is too short. Use a random secret of at least 32 UTF-8 bytes in Railway environment variables.");
    if(props.getAccessTokenMinutes() <= 0 || props.getRefreshTokenDays() <= 0) throw new IllegalStateException("JWT token TTL configuration must be positive.");
  }
  private SecretKey key(){return Keys.hmacShaKeyFor(props.getJwtSecret().getBytes(StandardCharsets.UTF_8));}
  public String accessToken(Long id,String identifier,String role){return build(id,identifier,role,props.getAccessTokenMinutes()*60_000L,"access");}
  public String refreshToken(Long id,String identifier,String role){return build(id,identifier,role,props.getRefreshTokenDays()*86_400_000L,"refresh");}
  private String build(Long id,String identifier,String role,long ttl,String type){Instant now=Instant.now();return Jwts.builder().claims(Map.of("uid",id,"role",role,"type",type)).subject(identifier).issuedAt(Date.from(now)).expiration(new Date(now.toEpochMilli()+ttl)).signWith(key()).compact();}
  public Claims parse(String token){return Jwts.parser().verifyWith(key()).build().parseSignedClaims(token).getPayload();}
  public boolean validAccess(String token){try{Claims c=parse(token);return "access".equals(c.get("type",String.class)) && c.getExpiration().after(new Date());}catch(Exception e){return false;}}
  public boolean validRefresh(String token){try{Claims c=parse(token);return "refresh".equals(c.get("type",String.class)) && c.getExpiration().after(new Date());}catch(Exception e){return false;}}
}
