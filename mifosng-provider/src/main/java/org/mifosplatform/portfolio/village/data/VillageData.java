package org.mifosplatform.portfolio.village.data;

import java.util.Collection;

import org.mifosplatform.infrastructure.core.data.EnumOptionData;
import org.mifosplatform.organisation.office.data.OfficeData;


public class VillageData {

    private final Long villageId;
    private final String externalId;
    private final Long officeId;
    private final String officeName;
    private final String villageCode;
    private final String villageName;
    private final Long counter;
    private final String taluk;
    private final String district;
    private final Long pincode;
    private final String state;
  //  private final LocalDate activatedOnDate;
  //  private final LocalDate submittedOnDate;
    private final EnumOptionData status;
    
    // template
    private final Collection<OfficeData> officeOptions;
    
    final VillageTimelineData timeline;
    
    public static VillageData template(final Long officeId, final Collection<OfficeData> officeOptions) {
        
        return new VillageData(null, null, officeId, null, null, null, null, officeOptions, null, null, null, null, null, null);
    }
    
    private VillageData(final Long id, final String externalId, final Long officeId, final String officeName, final String villageCode,
            final String villageName, final Long counter, final Collection<OfficeData> officeOptions, final String taluk, final String district, 
            final Long pincode, final String state, final EnumOptionData status, final VillageTimelineData timeline){
        
        this.villageId = id;
        this.externalId = externalId;
        this.officeId = officeId;
        this.officeName = officeName;
        this.villageCode = villageCode;
        this.villageName = villageName;
        this.counter = counter;
        this.officeOptions = officeOptions;
        this.taluk = taluk;
        this.district = district;
        this.pincode = pincode;
        this.state = state;
        this.status = status;
        this.timeline = timeline;
    }
    
    public static VillageData instance(final Long id, final String externalId, final Long officeId, final String officeName, final String villageCode, 
            final String villageName, final Long counter, final String taluk, final String district, final Long pincode, final String state, 
            final EnumOptionData status, final VillageTimelineData timeline) {
        
        return new VillageData(id, externalId, officeId, officeName, villageCode, villageName, counter, null, taluk, district, pincode, state, status, 
                 timeline);
    }
    
    public static VillageData lookup(final Long id, final String villageName) {
        
        return new VillageData(id, null, null, null, null, villageName, null, null, null, null, null, null, null, null);
    }
    
    public static VillageData countValue(final Long counter, final String villageName) {
        
        return new VillageData(null, null, null, null, null, villageName, counter, null, null, null, null, null, null, null);
    }
    
}
