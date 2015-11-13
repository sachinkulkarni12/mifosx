package org.mifosplatform.batch.command.internal;

import javax.ws.rs.core.UriInfo;

import org.mifosplatform.batch.command.CommandStrategy;
import org.mifosplatform.batch.domain.BatchRequest;
import org.mifosplatform.batch.domain.BatchResponse;
import org.mifosplatform.batch.exception.ErrorHandler;
import org.mifosplatform.batch.exception.ErrorInfo;
import org.mifosplatform.infrastructure.dataqueries.api.DatatablesApiResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class MakeDataTableCommandStrategy  implements CommandStrategy {

	private final DatatablesApiResource datatablesApiResource;

    @Autowired
    public MakeDataTableCommandStrategy(final DatatablesApiResource datatablesApiResource) {
        this.datatablesApiResource = datatablesApiResource;
    }

    
	@Override
	public BatchResponse execute(BatchRequest request, @SuppressWarnings("unused") UriInfo uriInfo) {
		 final BatchResponse response = new BatchResponse();
	        final String responseBody;

	        response.setRequestId(request.getRequestId());
	        response.setHeaders(request.getHeaders());
	        
	        final String[] pathParameters = request.getRelativeUrl().split("/");
	        String datatable  = pathParameters[1];
	        Long apptableId = Long.parseLong(pathParameters[2].substring(0, pathParameters[2].indexOf("?")));
	        // Try-catch blocks to map exceptions to appropriate status codes
	        try {

	            // Calls 'create' function from 'DatatablesApiResource' to create a new
	            // client
	            responseBody = datatablesApiResource.createDatatableEntry(datatable, apptableId, request.getBody());

	            response.setStatusCode(200);
	            // Sets the body of the response after the successful creation of
	            // the datatable
	            response.setBody(responseBody);

	        } catch (RuntimeException e) {

	            // Gets an object of type ErrorInfo, containing information about
	            // raised exception
	            ErrorInfo ex = ErrorHandler.handler(e);

	            response.setStatusCode(ex.getStatusCode());
	            response.setBody(ex.getMessage());
	        }

	        return response;
	}

}
