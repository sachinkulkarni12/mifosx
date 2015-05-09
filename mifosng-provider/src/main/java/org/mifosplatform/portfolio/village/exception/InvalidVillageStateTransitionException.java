package org.mifosplatform.portfolio.village.exception;

import org.mifosplatform.infrastructure.core.exception.AbstractPlatformDomainRuleException;


public class InvalidVillageStateTransitionException extends AbstractPlatformDomainRuleException{

    public InvalidVillageStateTransitionException(final String action, final String postFix, String defaultUserMessage,
            Object... defaultUserMessageArgs) {
        super("error.msg." + action + "." + postFix, defaultUserMessage, defaultUserMessageArgs);
    }

    
}
