package org.mifosplatform.portfolio.village.api;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriInfo;

import org.mifosplatform.commands.domain.CommandWrapper;
import org.mifosplatform.commands.service.CommandWrapperBuilder;
import org.mifosplatform.commands.service.PortfolioCommandSourceWritePlatformService;
import org.mifosplatform.infrastructure.core.api.ApiRequestParameterHelper;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResult;
import org.mifosplatform.infrastructure.core.serialization.ApiRequestJsonSerializationSettings;
import org.mifosplatform.infrastructure.core.serialization.ToApiJsonSerializer;
import org.mifosplatform.infrastructure.security.service.PlatformSecurityContext;
import org.mifosplatform.portfolio.village.data.VillageData;
import org.mifosplatform.portfolio.village.service.VillageReadPlatformService;
//import org.mifosplatform.portfolio.village.data.VillageData;
//import org.mifosplatform.portfolio.village.service.VillageReadPlatformService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Path("/villages")
@Component
@Scope("singleton")
public class VillageApiResource {

    private final PlatformSecurityContext context;
    private final VillageReadPlatformService villageReadPlatformService;
    private final ApiRequestParameterHelper apiRequestParameterHelper;
    private final ToApiJsonSerializer<VillageData> villageDataApiJsonSerializer;
    private final PortfolioCommandSourceWritePlatformService commandSourceWritePlatformService;
    private final ToApiJsonSerializer<Object> toApiJsonSerializer;
    
    @Autowired
    public VillageApiResource(PlatformSecurityContext context, VillageReadPlatformService villageReadPlatformService,
            ApiRequestParameterHelper apiRequestParameterHelper, ToApiJsonSerializer<VillageData> villageDataApiJsonSerializer, 
            PortfolioCommandSourceWritePlatformService commandSourceWritePlatformService, ToApiJsonSerializer<Object> toApiJsonSerializer) {

        this.context = context;
        this.villageReadPlatformService = villageReadPlatformService;
        this.apiRequestParameterHelper = apiRequestParameterHelper;
        this.villageDataApiJsonSerializer = villageDataApiJsonSerializer;
        this.commandSourceWritePlatformService = commandSourceWritePlatformService;
        this.toApiJsonSerializer = toApiJsonSerializer;
    }
    
    @GET
    @Path("template")
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public String retrieveTemplate(@Context final UriInfo uriInfo, @QueryParam("officeId") final Long officeId) {
       
        this.context.authenticatedUser().validateHasReadPermission(VillageTypeApiConstants.VILLAGE_RESOURCE_NAME);
        
        final VillageData villageTemplate = this.villageReadPlatformService.retrieveTemplate(officeId);
        final ApiRequestJsonSerializationSettings settings = this.apiRequestParameterHelper.process(uriInfo.getQueryParameters());
        return this.villageDataApiJsonSerializer.serialize(settings, villageTemplate, VillageTypeApiConstants.VILLAGE_RESPONSE_DATA_PARAMETERS);
    }
    
    @POST
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public String create(final String apiRequestBodyAsJson) {
        
        final CommandWrapper commandRequest = new CommandWrapperBuilder() //
                    .createVillage() //
                    .withJson(apiRequestBodyAsJson) //
                    .build();
        final CommandProcessingResult result = this.commandSourceWritePlatformService.logCommandSource(commandRequest);
        return this.toApiJsonSerializer.serialize(result);
    }
}
