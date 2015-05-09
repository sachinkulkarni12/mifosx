package org.mifosplatform.portfolio.village.data;

import java.util.Collection;

import org.mifosplatform.organisation.office.data.OfficeData;


public class VillageData {

    private final Long villageId;
    private final Long officeId;
    private final String villageName;
    private final Long counter;
  //  private final LocalDate activatedOnDate;
  //  private final LocalDate submittedOnDate;
  //  private final EnumOptionData status;
    
    // template
    private final Collection<OfficeData> officeOptions;
    
    public static VillageData template(final Long officeId, final Collection<OfficeData> officeOptions) {
        
        return new VillageData(null, null, officeId, null, officeOptions);
    }
    
    private VillageData(final Long id, final String villageName, final Long officeId, final Long counter, final Collection<OfficeData> officeOptions){
        
        this.villageId = id;
        this.villageName = villageName;
        this.officeId = officeId;
        this.counter = counter;
        this.officeOptions = officeOptions;
    }
    
    public static VillageData lookup(final Long id, final String villageName) {
        
        return new VillageData(id, villageName, null, null, null);
    }
    
    public static VillageData countValue(final Long counter, final String villageName) {
        
        return new VillageData(null,  villageName, null, counter, null);
    }
    
}
