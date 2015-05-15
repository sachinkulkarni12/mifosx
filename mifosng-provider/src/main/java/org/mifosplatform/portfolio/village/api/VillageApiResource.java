package org.mifosplatform.portfolio.village.api;

import java.util.Collection;

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
import org.mifosplatform.infrastructure.core.data.PaginationParameters;
import org.mifosplatform.infrastructure.core.serialization.ApiRequestJsonSerializationSettings;
import org.mifosplatform.infrastructure.core.serialization.ToApiJsonSerializer;
import org.mifosplatform.infrastructure.core.service.Page;
import org.mifosplatform.infrastructure.core.service.SearchParameters;
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
    
    @GET
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public String retrieveAll(@Context final UriInfo uriInfo, @QueryParam("sqlSearch") final String sqlSearch, @QueryParam("officeId") final Long officeId,
                    @QueryParam("externalId") final String externalId, @QueryParam("name") final String name, @QueryParam("paged") final Boolean paged, 
                    @QueryParam("offset") Integer offset, @QueryParam("limit") final Integer limit, @QueryParam("orderBy") final String orderBy, 
                    @QueryParam("sortOrder") final String sortOrder)  {
       
        this.context.authenticatedUser().validateHasReadPermission(VillageTypeApiConstants.VILLAGE_RESOURCE_NAME);
        final ApiRequestJsonSerializationSettings settings = this.apiRequestParameterHelper.process(uriInfo.getQueryParameters());
        final PaginationParameters parameters = PaginationParameters.instance(paged, offset, limit, orderBy, sortOrder);
        final SearchParameters searchParameters = SearchParameters.forVillages(sqlSearch, officeId, externalId, name, offset, limit, orderBy, sortOrder);
        
        if (parameters.isPaged()) {
            final Page<VillageData> villages = this.villageReadPlatformService.retrievePagedAll(searchParameters, parameters);
            
           return this.toApiJsonSerializer.serialize(settings, villages, VillageTypeApiConstants.VILLAGE_RESPONSE_DATA_PARAMETERS);         
        }
        
        final Collection<VillageData> villages = this.villageReadPlatformService.retrieveAll(searchParameters, parameters);
        return this.toApiJsonSerializer.serialize(settings, villages, VillageTypeApiConstants.VILLAGE_RESPONSE_DATA_PARAMETERS);
    }
}
