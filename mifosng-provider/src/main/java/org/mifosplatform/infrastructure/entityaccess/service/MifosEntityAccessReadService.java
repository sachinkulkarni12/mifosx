/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mifosplatform.infrastructure.entityaccess.service;

import java.util.Collection;

import org.mifosplatform.infrastructure.entityaccess.data.MifosEntityAccessData;
import org.mifosplatform.infrastructure.entityaccess.data.MifosEntityRelationData;
import org.mifosplatform.infrastructure.entityaccess.data.MifosEntityToEntityMappingData;
import org.mifosplatform.infrastructure.entityaccess.domain.MifosEntityAccessType;
import org.mifosplatform.infrastructure.entityaccess.domain.MifosEntityType;

public interface MifosEntityAccessReadService {

    Collection<MifosEntityAccessData> retrieveEntityAccessFor(String entityIds, MifosEntityAccessType relationshipType,
            boolean includeAllOffices);

    String getSQLQueryInClause_WithListOfIDsForEntityAccess(String entityIds, MifosEntityAccessType relationship, boolean includeAllOffices);

    String getSQLQueryInClauseIDList_ForLoanProductsForOffice(Long loanProductId, boolean includeAllOffices);

    String getSQLQueryInClauseIDList_ForSavingsProductsForOffice(Long savingsProductId, boolean includeAllOffices);

    String getSQLQueryInClauseIDList_ForChargesForOffice(Long officeId, boolean includeAllOffices);
    
    String getSQLQueryInClauseIDList_ForLoanProductsForRoles (String commaSeparatedRoleIds);
    
    String getSQLQueryInClauseIDList_ForSavingsProductsForRoles (String commaSeparatedRoleIds);

    Collection<MifosEntityRelationData> retrieveAllSupportedMappingTypes();

    Collection<MifosEntityToEntityMappingData> retrieveOneMapping(Long mapId);

    Collection<MifosEntityToEntityMappingData> retrieveEntityToEntityMappings(Long mapId, Long fromoId, Long toId);

}
