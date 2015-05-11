package org.mifosplatform.portfolio.village.domain;

import org.mifosplatform.infrastructure.core.data.EnumOptionData;


public class VillageTypeEnumerations {

public static EnumOptionData status(final Integer statusId){
        
        return status(VillageTypeStatus.fromInt(statusId));
    }
    
    public static EnumOptionData status(final VillageTypeStatus status){
        
        EnumOptionData optionData = new EnumOptionData(VillageTypeStatus.INVALID.getValue().longValue(), VillageTypeStatus.INVALID.getCode(), "Invalid");
        
        switch(status){
            case INVALID:
                optionData = new EnumOptionData(VillageTypeStatus.INVALID.getValue().longValue(), VillageTypeStatus.INVALID.getCode(), "Invalid");
            break;
            case PENDING: 
                optionData = new EnumOptionData(VillageTypeStatus.PENDING.getValue().longValue(), VillageTypeStatus.PENDING.getCode(), "Pending");
            break;  
            case ACTIVE:
                optionData = new EnumOptionData(VillageTypeStatus.ACTIVE.getValue().longValue(), VillageTypeStatus.ACTIVE.getCode(), "Active");
            break;
            case CLOSED: 
                optionData = new EnumOptionData(VillageTypeStatus.CLOSED.getValue().longValue(), VillageTypeStatus.CLOSED.getCode(), "Closed");
            break;
        }
        return optionData;
    }

}
