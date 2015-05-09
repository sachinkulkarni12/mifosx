package org.mifosplatform.portfolio.village.service;

import java.util.Collection;

import org.mifosplatform.portfolio.village.data.VillageData;


public interface VillageReadPlatformService {

    VillageData retrieveTemplate(Long officeId);
    Collection<VillageData> retrieveVillagesForLookup(Long officeId);
    VillageData getCountValue(final Long villageId);
}
