package org.mifosplatform.portfolio.village.handler;

import org.mifosplatform.commands.handler.NewCommandSourceHandler;
import org.mifosplatform.infrastructure.core.api.JsonCommand;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResult;
import org.mifosplatform.portfolio.village.service.VillageWritePlatformService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DeleteVillageCommandHandler implements NewCommandSourceHandler {

    private VillageWritePlatformService villageWritePlatformService;
    
    @Autowired
    public DeleteVillageCommandHandler(VillageWritePlatformService villageWritePlatformService) {

        this.villageWritePlatformService = villageWritePlatformService;
    }

    @Override
    public CommandProcessingResult processCommand(JsonCommand command) {

        return this.villageWritePlatformService.deleteVillage(command.entityId());
    }
}
