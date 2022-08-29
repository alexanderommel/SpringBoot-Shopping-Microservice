package com.example.tongue.security;

import com.example.tongue.core.contracts.ApiResponse;
import com.example.tongue.security.domain.MerchantPasswordForm;
import com.example.tongue.security.domain.MerchantRegistrationForm;
import com.example.tongue.security.domain.MerchantRegistrationSession;
import com.example.tongue.security.domain.StringLabeledWrapper;
import com.example.tongue.services.MerchantPersistenceService;
import com.example.tongue.services.MerchantValidation;
import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

@Slf4j
@RestController
public class MerchantAuthController {

    private Gson gson = new Gson();
    private MerchantValidation merchantValidation;
    private MerchantPersistenceService merchantPersistenceService;
    private JwtProvider jwtProvider;

    public MerchantAuthController(@Autowired MerchantValidation merchantValidation,
                                  @Autowired MerchantPersistenceService merchantPersistenceService,
                                  @Autowired JwtProvider jwtProvider){
        this.merchantValidation = merchantValidation;
        this.merchantPersistenceService = merchantPersistenceService;
        this.jwtProvider = jwtProvider;
    }

    /** Register Line
     *  1. The Client sends the required data in a merchant registration form.
     *  2. The Server starts a Registration Session and send the metadata to the client.
     *  3. The Client creates a new password and sends it together with the
     *  RegistrationSessionId back to the server.
     *  4. The Server validates the sessionId and creates a new Merchant, Store
     *  and StoreVariant instance.
     *  5. The Server creates a JWT which will be used for future authentication
     *  and sends it to the Client.
     * **/

    @PostMapping("/auth/merchants/register/session")
    public ResponseEntity<ApiResponse> registerSession(@RequestBody MerchantRegistrationForm form){
        log.info("/auth/merchants/register/session");
        Boolean validForm = merchantValidation.validateMerchantValidationForm(form);
        String expiresAt = "21:30";
        StringLabeledWrapper<MerchantRegistrationForm> payload = new StringLabeledWrapper<>(form,expiresAt);
        String payloadJson = gson.toJson(payload);
        String encodedPayload = Base64.getEncoder().encodeToString(payloadJson.getBytes(StandardCharsets.UTF_8));
        String sessionId = "RANDOM SESSION ID";
        MerchantRegistrationSession session = new MerchantRegistrationSession(sessionId,encodedPayload);
        return ResponseEntity.ok(ApiResponse.success(session));
    }

    @PostMapping("/auth/merchants/register")
    public ResponseEntity<ApiResponse> register(@RequestBody MerchantPasswordForm passwordForm){
        log.info("/auth/merchants/register");
        String payload = passwordForm.getSession().getPayload();
        String decodedPayload = new String(Base64.getDecoder().decode(payload),StandardCharsets.UTF_8);
        StringLabeledWrapper wrapper = gson.fromJson(decodedPayload,StringLabeledWrapper.class);
        String coveredJson = gson.toJson(wrapper.getCovered());
        String expiresAt = wrapper.getLabel();
        MerchantRegistrationForm registrationForm = gson.fromJson(coveredJson,MerchantRegistrationForm.class);
        merchantPersistenceService.registerNewMerchantAccount(registrationForm,passwordForm.getPassword());
        String jwt = jwtProvider.generateMerchantAuthorizationToken(registrationForm.getEmail());
        return ResponseEntity.ok(ApiResponse.success(jwt));
    }

}
