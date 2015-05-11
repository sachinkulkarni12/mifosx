package org.mifosplatform.portfolio.village.exception;

import org.mifosplatform.infrastructure.core.exception.AbstractPlatformResourceNotFoundException;


public class VillageNotFoundException extends AbstractPlatformResourceNotFoundException{

    public VillageNotFoundException(final Long id) {
        super("error.msg.village.id.invalid", "Village with identifier" + id + "does not exist", id);
    }
}
