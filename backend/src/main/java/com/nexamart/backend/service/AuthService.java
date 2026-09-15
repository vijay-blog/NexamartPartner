package com.nexamart.backend.service;
import com.nexamart.backend.api.ApiModels.*;import com.nexamart.backend.config.AppProperties;import com.nexamart.backend.domain.*;import com.nexamart.backend.exception.ApiException;import com.nexamart.backend.repository.UserAccountRepository;import com.nexamart.backend.security.JwtService;import org.springframework.http.HttpStatus;import org.springframework.security.crypto.password.PasswordEncoder;import org.springframework.stereotype.Service;import org.springframework.transaction.annotation.Transactional;import java.time.Instant;
@Service public class AuthService{final UserAccountRepository users;final PasswordEncoder encoder;final JwtService jwt;final AppProperties props;final com.nexamart.backend.repository.DeliveryPartnerProfileRepository profiles;public AuthService(UserAccountRepository u,PasswordEncoder e,JwtService j,AppProperties p,com.nexamart.backend.repository.DeliveryPartnerProfileRepository pr){users=u;encoder=e;jwt=j;props=p;profiles=pr;}
 public LoginResponse login(LoginRequest r){String identifier=r.identifier().trim();
  String normalizedPhone=normalizePhone(identifier);
  UserAccount u=users.findByEmailIgnoreCase(identifier)
    .or(()->users.findByUsernameIgnoreCase(identifier))
    .or(()->users.findByPhone(normalizedPhone))
    .orElseThrow(()->new ApiException(HttpStatus.UNAUTHORIZED,"Invalid login details. Please try again."));if(u.getStatus()!=AccountStatus.ACTIVE)throw new ApiException(HttpStatus.FORBIDDEN,"Account is not active.");if(!encoder.matches(r.password(),u.getPasswordHash()))throw new ApiException(HttpStatus.UNAUTHORIZED,"Invalid login details. Please try again.");u.setLastActiveAt(Instant.now());users.save(u);return tokens(u);}
 @Transactional public RegistrationResponse register(RegisterRequest r){ return registerWithRole(r, Role.DELIVERY_PARTNER); }
 @Transactional public RegistrationResponse registerCustomer(RegisterRequest r){ return registerWithRole(r, Role.CUSTOMER); }
 private RegistrationResponse registerWithRole(RegisterRequest r, Role role){
  String email = r.email().trim().toLowerCase();
  String phone = normalizePhone(r.phone());
  if(!r.password().equals(r.confirmPassword())) throw new ApiException(HttpStatus.BAD_REQUEST,"Passwords do not match.");
  if(!phone.matches("[6-9]\\d{9}")) throw new ApiException(HttpStatus.BAD_REQUEST,"Please enter a valid 10-digit Indian mobile number.");
  if(users.existsByEmailIgnoreCase(email)) throw new ApiException(HttpStatus.CONFLICT,"Email is already registered.");
  if(users.existsByPhone(phone)) throw new ApiException(HttpStatus.CONFLICT,"Mobile number is already registered.");

  UserAccount u=new UserAccount();
  u.setName(r.name().trim());
  u.setEmail(email);
  u.setPhone(phone);
  u.setPasswordHash(encoder.encode(r.password()));
  u.setRole(role);
  u.setStatus(AccountStatus.ACTIVE);
  u=users.saveAndFlush(u);

  if(role==Role.DELIVERY_PARTNER){
    var profile=new com.nexamart.backend.domain.DeliveryPartnerProfile();
    profile.setUser(u);
    profiles.saveAndFlush(profile);
  }
  return new RegistrationResponse("Delivery partner account created successfully.");
}
 public LoginResponse refresh(RefreshRequest r){if(!jwt.validRefresh(r.refreshToken()))throw new ApiException(HttpStatus.UNAUTHORIZED,"Refresh token is invalid or expired.");var c=jwt.parse(r.refreshToken());UserAccount u=users.findById(((Number)c.get("uid")).longValue()).orElseThrow(()->new ApiException(HttpStatus.UNAUTHORIZED,"Account not found."));if(u.getStatus()!=AccountStatus.ACTIVE)throw new ApiException(HttpStatus.FORBIDDEN,"Account is not active.");return tokens(u);}
 public UserResponse user(UserAccount u){return new UserResponse(u.getId(),u.getName(),u.getPhone(),u.getEmail(),u.getRole().name());}
 private LoginResponse tokens(UserAccount u){return new LoginResponse(jwt.accessToken(u.getId(),u.getEmail()!=null?u.getEmail():u.getUsername(),u.getRole().name()),jwt.refreshToken(u.getId(),u.getEmail()!=null?u.getEmail():u.getUsername(),u.getRole().name()),user(u));}
 private String normalizePhone(String value){
  String phone=value==null?"":value.trim().replace(" ","").replace("-","");
  if(phone.startsWith("+91")) phone=phone.substring(3);
  else if(phone.startsWith("0091")) phone=phone.substring(4);
  if(phone.matches("[6-9]\\d{9}")) return phone;
  return phone;
 }
 public void ensureAdmin(){
  String username=props.getAdminUsername()==null?"":props.getAdminUsername().trim();
  String password=props.getAdminPassword();
  if(username.isBlank()) throw new IllegalStateException("ADMIN_USERNAME must not be blank.");
  if(password==null || password.isBlank()) throw new IllegalStateException("ADMIN_PASSWORD is missing. Set ADMIN_PASSWORD in Railway environment variables.");

  UserAccount admin=users.findByUsernameIgnoreCase(username).orElse(null);
  if(admin==null){
    UserAccount a=new UserAccount();
    a.setName(props.getAdminName());
    a.setUsername(username);
    a.setEmail(props.getAdminEmail());
    a.setPasswordHash(encoder.encode(password));
    a.setRole(Role.ADMIN);
    a.setStatus(AccountStatus.ACTIVE);
    users.saveAndFlush(a);
    return;
  }

  // The bootstrap admin is environment-managed. Keep its password aligned with
  // ADMIN_PASSWORD so a changed Railway secret actually takes effect.
  boolean changed=false;
  if(admin.getRole()!=Role.ADMIN){admin.setRole(Role.ADMIN);changed=true;}
  if(admin.getStatus()!=AccountStatus.ACTIVE){admin.setStatus(AccountStatus.ACTIVE);changed=true;}
  if(!encoder.matches(password,admin.getPasswordHash())){admin.setPasswordHash(encoder.encode(password));changed=true;}
  if(changed) users.saveAndFlush(admin);
 }
}
