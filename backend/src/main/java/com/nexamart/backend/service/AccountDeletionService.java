package com.nexamart.backend.service;

import com.nexamart.backend.domain.Role;
import com.nexamart.backend.domain.UserAccount;
import com.nexamart.backend.exception.ApiException;
import com.nexamart.backend.repository.DeliveryPartnerProfileRepository;
import com.nexamart.backend.repository.EarningRepository;
import com.nexamart.backend.repository.NotificationRepository;
import com.nexamart.backend.repository.OrderRepository;
import com.nexamart.backend.repository.UserAccountRepository;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AccountDeletionService {
  private final UserAccountRepository users;
  private final PasswordEncoder encoder;
  private final DeliveryPartnerProfileRepository profiles;
  private final NotificationRepository notifications;
  private final EarningRepository earnings;
  private final OrderRepository orders;

  public AccountDeletionService(UserAccountRepository users, PasswordEncoder encoder,
      DeliveryPartnerProfileRepository profiles, NotificationRepository notifications,
      EarningRepository earnings, OrderRepository orders) {
    this.users=users; this.encoder=encoder; this.profiles=profiles;
    this.notifications=notifications; this.earnings=earnings; this.orders=orders;
  }

  @Transactional
  public void deletePartner(String identifier, String password) {
    if(identifier==null || identifier.isBlank() || password==null || password.isBlank())
      throw new ApiException(HttpStatus.BAD_REQUEST,"Email/phone and password are required.");
    String value=identifier.trim();
    UserAccount user=users.findByEmailIgnoreCase(value)
        .or(()->users.findByPhone(normalizePhone(value)))
        .or(()->users.findByUsernameIgnoreCase(value))
        .orElseThrow(()->new ApiException(HttpStatus.NOT_FOUND,"Partner account was not found."));
    if(user.getRole()!=Role.DELIVERY_PARTNER)
      throw new ApiException(HttpStatus.BAD_REQUEST,"This page is only for VJoyKart Partner accounts.");
    if(!encoder.matches(password,user.getPasswordHash()))
      throw new ApiException(HttpStatus.UNAUTHORIZED,"The password is incorrect.");

    Long id=user.getId();
    // Keep customer/order history intact, but remove the deleted partner relationship.
    orders.clearDeliveryPartner(id);
    earnings.deleteByPartnerId(id);
    notifications.deleteByUserId(id);
    profiles.deleteById(id);
    users.deleteById(id);
  }

  private String normalizePhone(String value){
    String phone=value.trim().replace(" ","").replace("-","");
    if(phone.startsWith("+91")) phone=phone.substring(3);
    else if(phone.startsWith("0091")) phone=phone.substring(4);
    return phone;
  }
}
