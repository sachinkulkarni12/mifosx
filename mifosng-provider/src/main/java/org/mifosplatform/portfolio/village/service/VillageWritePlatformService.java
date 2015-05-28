package org.mifosplatform.portfolio.village.service;

import org.mifosplatform.infrastructure.core.api.JsonCommand;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResult;


public interface VillageWritePlatformService {

  //  CommandProcessingResult associateCentersToVillage(Long villageId, JsonCommand command);
    CommandProcessingResult createVillage(final JsonCommand command);
    CommandProcessingResult updateVillage(final Long villageId, final JsonCommand command);
    CommandProcessingResult deleteVillage(final Long villageId);
    CommandProcessingResult activateVillage(Long villageId, JsonCommand command);
}
