package org.mifosplatform.portfolio.village.handler;

import org.mifosplatform.commands.handler.NewCommandSourceHandler;
import org.mifosplatform.infrastructure.core.api.JsonCommand;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResult;
import org.mifosplatform.portfolio.village.service.VillageWritePlatformService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CreateVillageCommandHandler implements NewCommandSourceHandler {

    private final VillageWritePlatformService villageWritePlatformService;
    
    @Autowired
    public CreateVillageCommandHandler(VillageWritePlatformService villageWritePlatformService) {
        this.villageWritePlatformService = villageWritePlatformService;
    }

    @Transactional
    @Override
    public CommandProcessingResult processCommand(JsonCommand command) {

        return this.villageWritePlatformService.createVillage(command);
    }

    
}
