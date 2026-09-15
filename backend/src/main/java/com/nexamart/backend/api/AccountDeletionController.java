package com.nexamart.backend.api;

import com.nexamart.backend.service.AccountDeletionService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class AccountDeletionController {
  private final AccountDeletionService service;
  public AccountDeletionController(AccountDeletionService service){this.service=service;}

  @GetMapping(value="/vjoykart-partner/delete-account", produces=MediaType.TEXT_HTML_VALUE)
  public ResponseEntity<String> page(){return ResponseEntity.ok(html(null,false));}

  @PostMapping(value="/vjoykart-partner/delete-account", consumes=MediaType.APPLICATION_FORM_URLENCODED_VALUE, produces=MediaType.TEXT_HTML_VALUE)
  public ResponseEntity<String> delete(@RequestParam String identifier,@RequestParam String password){
    try { service.deletePartner(identifier,password); return ResponseEntity.ok(html("Your VJoyKart Partner account and associated partner profile data have been deleted. Historical orders are retained without the deleted partner assignment where required for transaction records.",true)); }
    catch(Exception e){ return ResponseEntity.ok(html(e.getMessage()==null?"We could not process the request. Please verify your details and try again.":e.getMessage(),false)); }
  }

  private String html(String message, boolean success){
    String safe=message==null?"":message.replace("&","&amp;").replace("<","&lt;").replace(">","&gt;");
    return """
<!doctype html><html><head><meta name="viewport" content="width=device-width,initial-scale=1"><title>VJoyKart Partner - Delete Account</title>
<style>body{font-family:Arial,sans-serif;background:#f6f7fb;margin:0;padding:24px}.card{max-width:560px;margin:30px auto;background:#fff;padding:28px;border-radius:18px;box-shadow:0 8px 30px #0001}h1{margin-top:0}input{width:100%;box-sizing:border-box;padding:13px;margin:7px 0 14px;border:1px solid #ccd1da;border-radius:10px}button{width:100%;padding:14px;border:0;border-radius:10px;background:#111827;color:#fff;font-weight:700}.note{background:#f1f5f9;padding:14px;border-radius:10px;line-height:1.5}.msg{padding:14px;border-radius:10px;background:%s;margin-bottom:16px}</style></head><body><div class="card"><h1>Delete VJoyKart Partner Account</h1>
<p>This page lets a VJoyKart Partner request permanent account deletion.</p>%s
<form method="post"><label>Email or mobile number</label><input name="identifier" required><label>Account password</label><input type="password" name="password" required><button type="submit">Delete My Account</button></form>
<div class="note"><b>Data handling:</b> account credentials, profile, notifications and partner earnings are deleted. Historical order records may be retained for transaction/audit purposes, but the deleted partner is unassigned from those orders. Deletion is permanent.</div></div></body></html>
""".formatted(success?"#ecfdf5":"#fff7ed", safe.isBlank()?"":("<div class=\"msg\">"+safe+"</div>"));
  }
}
