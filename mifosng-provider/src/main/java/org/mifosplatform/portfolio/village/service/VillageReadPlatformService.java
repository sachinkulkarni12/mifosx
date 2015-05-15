package org.mifosplatform.portfolio.village.service;

import java.util.Collection;

import org.mifosplatform.infrastructure.core.data.PaginationParameters;
import org.mifosplatform.infrastructure.core.service.Page;
import org.mifosplatform.infrastructure.core.service.SearchParameters;
import org.mifosplatform.portfolio.village.data.VillageData;


public interface VillageReadPlatformService {

    VillageData retrieveTemplate(Long officeId);
    Collection<VillageData> retrieveAll(SearchParameters searchParameters, PaginationParameters paginationParameters);
    Page<VillageData> retrievePagedAll(SearchParameters searchParameters, PaginationParameters paginationParameters);
    Collection<VillageData> retrieveVillagesForLookup(Long officeId);
    VillageData getCountValue(final Long villageId);
}
