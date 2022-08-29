package com.example.tongue.services;

import com.example.tongue.security.domain.MerchantRegistrationForm;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class MerchantValidation {

    public Boolean validateMerchantValidationForm(MerchantRegistrationForm registrationForm){
        /** Simple Nullability validation**/
        return !(isBlackOrNullString(registrationForm.getEmail()) ||
                isBlackOrNullString(registrationForm.getFirstName()) ||
                isBlackOrNullString(registrationForm.getLastName()) ||
                isBlackOrNullString(registrationForm.getPhone()) ||
                isBlackOrNullString(registrationForm.getStoreName()) ||
                isBlackOrNullString(registrationForm.getStoreAddress()) ||
                isBlackOrNullString(registrationForm.getStoreIdentificationNumber()));
    }

    private Boolean isBlackOrNullString(String str){
        if (str==null)
            return false;
        return str.isBlank();
    }

}
