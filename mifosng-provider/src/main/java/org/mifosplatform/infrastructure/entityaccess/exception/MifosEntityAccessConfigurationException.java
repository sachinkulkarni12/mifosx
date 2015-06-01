/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mifosplatform.infrastructure.entityaccess.exception;

import org.mifosplatform.infrastructure.core.exception.AbstractPlatformDomainRuleException;
import org.mifosplatform.infrastructure.entityaccess.domain.MifosEntityAccessType;
import org.mifosplatform.infrastructure.entityaccess.domain.MifosEntityType;

public class MifosEntityAccessConfigurationException extends AbstractPlatformDomainRuleException {

    public MifosEntityAccessConfigurationException(final String firstEntityIds,
    		MifosEntityAccessType relationshipType) {
        super("error.msg.entityaccess.config",
                "Error while getting entity access configuration for: "  + firstEntityIds + 
                " with relationship type: " + relationshipType.id() + "/" + relationshipType.toStr());
    }

}
