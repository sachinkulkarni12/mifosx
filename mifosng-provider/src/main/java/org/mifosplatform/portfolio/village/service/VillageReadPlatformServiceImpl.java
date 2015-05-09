package org.mifosplatform.portfolio.village.service;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.List;

import org.mifosplatform.infrastructure.core.domain.JdbcSupport;
import org.mifosplatform.infrastructure.core.service.RoutingDataSource;
import org.mifosplatform.infrastructure.security.service.PlatformSecurityContext;
import org.mifosplatform.organisation.office.data.OfficeData;
import org.mifosplatform.organisation.office.service.OfficeReadPlatformService;
import org.mifosplatform.portfolio.village.data.VillageData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;

@Service
public class VillageReadPlatformServiceImpl implements VillageReadPlatformService {

    private final PlatformSecurityContext context;
    private final JdbcTemplate jdbcTemplate;
    
    private final OfficeReadPlatformService officeReadPlatformService;
    
    @Autowired
    public VillageReadPlatformServiceImpl(final PlatformSecurityContext context, final RoutingDataSource dataSource, final OfficeReadPlatformService officeReadPlatformService) {

        this.context = context;
        this.jdbcTemplate = new JdbcTemplate(dataSource);
        this.officeReadPlatformService = officeReadPlatformService;
    }
    
    @Override
    public VillageData retrieveTemplate(final Long officeId) {

        final Long defaultOfficeId = defaultToUsersOfficeIfNull(officeId);
        final Collection<OfficeData> officeOptions = this.officeReadPlatformService.retrieveAllOfficesForDropdown();
        
        return VillageData.template(defaultOfficeId, officeOptions);
    }

    private Long defaultToUsersOfficeIfNull(final Long officeId) {

        Long defaultOfficeId = officeId;
        if (defaultOfficeId == null) {
            defaultOfficeId = this.context.authenticatedUser().getOffice().getId();
        }
        return defaultOfficeId;
    }

    @Override
    public Collection<VillageData> retrieveVillagesForLookup(final Long officeId) {

        this.context.authenticatedUser();
        final VillageLookupDataMapper rm = new VillageLookupDataMapper();
        final String sql = "Select "+ rm.schema() + " where v.office_id = ? ";
        return this.jdbcTemplate.query(sql, rm, new Object[] { officeId });
    }
    
    private static final class VillageLookupDataMapper implements RowMapper<VillageData> {

        public final String schema() {
            return "v.id as id, v.village_name as villageName from chai_villages v ";
        }
        @Override
        public VillageData mapRow(final ResultSet rs, @SuppressWarnings("unused") final int rowNum) throws SQLException {

            final Long id = JdbcSupport.getLong(rs, "id");
            final String villageName = rs.getString("villageName");
            
            return VillageData.lookup(id, villageName);
        }
        
    }
    
    @Override
    public VillageData getCountValue(final Long villageId){

        this.context.authenticatedUser();
        final VillageCountValueMapper vc = new VillageCountValueMapper();
        final String sql = "Select "+ vc.schemaForCount() + " where v.id = ? ";
        return this.jdbcTemplate.queryForObject(sql, vc, new Object[] { villageId });
    }
    
    private final class VillageCountValueMapper implements RowMapper<VillageData> {

        public final String schemaForCount(){
            return " v.counter as counter, v.village_name as villageName from chai_villages v ";
        }
        
        @Override
        public VillageData mapRow(final ResultSet rs,@SuppressWarnings("unused") final int rowNum) throws SQLException {

            final Long counter = rs.getLong("counter");
            final String villageName = rs.getString("villageName");
            return VillageData.countValue(counter, villageName);
        }
        
    }

}
