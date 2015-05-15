package org.mifosplatform.portfolio.village.data;

import org.joda.time.LocalDate;

@SuppressWarnings("unused")
public class VillageTimelineData {

    private final LocalDate activatedOnDate;
    private final String activatedByUsername;
    private final String activatedByFirstname;
    private final String activatedByLastname;
    
    private final LocalDate submittedOnDate;
    private final String submittedByUsername;
    private final String submittedByFirstname;
    private final String submittedByLastname;
    
    
    public VillageTimelineData(LocalDate activatedOnDate, String activatedByUsername, String activatedByFirstname,
            String activatedByLastname, LocalDate submittedOnDate, String submittedByUsername, String submittedByFirstname,
            String submittedByLastname) {

        this.activatedOnDate = activatedOnDate;
        this.activatedByUsername = activatedByUsername;
        this.activatedByFirstname = activatedByFirstname;
        this.activatedByLastname = activatedByLastname;
        this.submittedOnDate = submittedOnDate;
        this.submittedByUsername = submittedByUsername;
        this.submittedByFirstname = submittedByFirstname;
        this.submittedByLastname = submittedByLastname;
    }
    
}
