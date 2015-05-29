package org.mifosplatform.portfolio.village.exception;

import org.mifosplatform.infrastructure.core.exception.AbstractPlatformDomainRuleException;


public class VillageMustBePendingToBeDeletedException extends AbstractPlatformDomainRuleException {

    public VillageMustBePendingToBeDeletedException(final Long id) {
        super("error.msg.villages.cannot.be.deleted",
                "village with identifier " + id + " cannot be deleted as it is not in `Pending` state.", id);
    }
}
