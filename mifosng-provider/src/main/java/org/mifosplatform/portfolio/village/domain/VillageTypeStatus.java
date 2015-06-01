package org.mifosplatform.portfolio.village.domain;


public enum  VillageTypeStatus {

    INVALID(0, "VillageStatusType.invalid"), //
    PENDING(100, "VillageStatusType.pending"), //
    ACTIVE(300, "VillageStatusType.active"), //
    CLOSED(600, "VillageStatusType.closed");
    
    private final Integer value;
    private final String code;
    
    public static VillageTypeStatus fromInt(final Integer statusValue){
        
        VillageTypeStatus enumeration = VillageTypeStatus.INVALID;
        switch(statusValue){
            
            case 100:
                enumeration = VillageTypeStatus.PENDING;
            break;
            case 300:
                enumeration = VillageTypeStatus.ACTIVE;
            break;
            case 600:
                enumeration = VillageTypeStatus.CLOSED;
            break;    
        }
        return enumeration;
    }
    
    private VillageTypeStatus(final Integer value, final String code){
        this.value = value;
        this.code = code;
    }
    
    public boolean hasStateOf(final VillageTypeStatus state){
        return this.value.equals(state.getValue());
    }

    
    public Integer getValue() {
        return this.value;
    }

    
    public String getCode() {
        return this.code;
    }
    
    public boolean isPending(){
        return this.value.equals(VillageTypeStatus.PENDING.getValue());
    }
    
    public boolean isActive(){
        return this.value.equals(VillageTypeStatus.ACTIVE.getValue());
    }
    
    public boolean isClosed(){
        return this.value.equals(VillageTypeStatus.CLOSED.getValue());
    }

}
