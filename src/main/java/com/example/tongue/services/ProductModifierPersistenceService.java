package com.example.tongue.services;

import com.example.tongue.domain.merchant.GroupModifier;
import com.example.tongue.domain.merchant.Modifier;
import com.example.tongue.domain.merchant.enumerations.GroupModifierType;
import com.example.tongue.repositories.merchant.GroupModifierRepository;
import com.example.tongue.repositories.merchant.ModifierRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class ProductModifierPersistenceService {

    private GroupModifierRepository groupModifierRepository;
    private ModifierRepository modifierRepository;

    public ProductModifierPersistenceService(@Autowired GroupModifierRepository groupModifierRepository,
                                             @Autowired ModifierRepository modifierRepository){
        this.groupModifierRepository=groupModifierRepository;
        this.modifierRepository=modifierRepository;
    }

    public GroupModifier createGroupModifier(GroupModifier groupModifier) throws Exception {
        if (groupModifier.getId()!=null)
            throw new Exception("To create a new Entity, your entity must not have an Id");
        if (groupModifier.getType()== GroupModifierType.MANDATORY){
            if (groupModifier.getMaximumActiveModifiers()<=0 || groupModifier.getMinimumActiveModifiers()<=0
            || groupModifier.getMaximumActiveModifiers()<groupModifier.getMinimumActiveModifiers())
                throw new Exception("Inconsistent number of modifiers when MANDATORY");
        }else {
            groupModifier.setMinimumActiveModifiers(0);
        }
        if (groupModifier.getStoreVariant().getId()!=groupModifier.getProduct().getStoreVariant().getId())
            throw new Exception("The Product doesn't belong to the Store Variant on Group modifier");
        groupModifier = groupModifierRepository.save(groupModifier);
        return groupModifier;
    }

    public GroupModifier updateGroupModifier(GroupModifier groupModifier) throws Exception{
        log.info("Updating GroupModifier");
        GroupModifier groupModifier1 = groupModifierRepository.findById(groupModifier.getId()).get();
        log.info("Id->"+groupModifier1.getId());
        groupModifier.setProduct(groupModifier1.getProduct());
        groupModifier.setStoreVariant(groupModifier1.getStoreVariant());
        if (groupModifier.getType()==GroupModifierType.MANDATORY){
            if (groupModifier.getMaximumActiveModifiers()<=0 || groupModifier.getMinimumActiveModifiers()<=0
                    || groupModifier.getMaximumActiveModifiers()<groupModifier.getMinimumActiveModifiers())
                throw new Exception("Inconsistent number of modifiers when MANDATORY");
        }else {
            groupModifier.setMaximumActiveModifiers(0);
        }
        groupModifier = groupModifierRepository.save(groupModifier);
        return groupModifier;
    }

    public Modifier createModifier(Modifier modifier) throws Exception {
        if (modifier.getId()!=null)
            throw new Exception("To create a new Entity, your entity must not have an Id");
        Modifier modifier1 = modifierRepository.save(modifier);
        return modifier1;
    }

}
