package com.example.tongue.core.genericdata;

import com.example.tongue.domain.merchant.enumerations.GroupModifierType;
import com.example.tongue.domain.merchant.GroupModifier;
import com.example.tongue.domain.merchant.Modifier;
import com.example.tongue.domain.merchant.Product;
import com.example.tongue.domain.merchant.StoreVariant;
import com.example.tongue.repositories.merchant.GroupModifierRepository;
import com.example.tongue.repositories.merchant.ModifierRepository;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class ModifiersGenerator {

    public static void generateModifiers(GroupModifier groupModifier,
                                         ModifierRepository modifierRepository,int quantities){

        for (int i=0;i<quantities;i++){
            Modifier modifier = new Modifier();
            modifier.setGroupModifier(groupModifier);
            Double extra = i%10*0.1;
            modifier.setPrice(BigDecimal.ONE.add(BigDecimal.valueOf(extra)));
            modifier.setName("modifier "+i);
            modifierRepository.save(modifier);
        }

    }

    public static List<GroupModifier> generateGroupModifiers(Product product,
                                              List<GroupModifierType> groupModifierTypeList,
                                              GroupModifierRepository groupModifierRepository,
                                              ModifierRepository modifierRepository,
                                              int quantity_per_group,
                                              StoreVariant storeVariant){

        List<GroupModifier> groupModifiers = new ArrayList<>();
        for (int i=0;i<groupModifierTypeList.size();i++){
            GroupModifier groupModifier = new GroupModifier();
            groupModifier.setContext("context "+i);
            groupModifier.setType(groupModifierTypeList.get(i));
            groupModifier.setProduct(product);
            int modulo = i%quantity_per_group;
            groupModifier.setMaximumActiveModifiers(modulo+1);
            groupModifier.setMinimumActiveModifiers(1);
            groupModifier.setStoreVariant(storeVariant);
            groupModifier = groupModifierRepository.save(groupModifier);
            generateModifiers(groupModifier,modifierRepository,quantity_per_group);
            groupModifiers.add(groupModifier);
        }
        return groupModifiers;
    }


}
