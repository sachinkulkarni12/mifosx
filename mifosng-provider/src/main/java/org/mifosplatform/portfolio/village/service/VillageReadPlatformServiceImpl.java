package org.mifosplatform.portfolio.village.service;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.joda.time.LocalDate;
import org.mifosplatform.infrastructure.core.api.ApiParameterHelper;
import org.mifosplatform.infrastructure.core.data.EnumOptionData;
import org.mifosplatform.infrastructure.core.data.PaginationParameters;
import org.mifosplatform.infrastructure.core.data.PaginationParametersDataValidator;
import org.mifosplatform.infrastructure.core.domain.JdbcSupport;
import org.mifosplatform.infrastructure.core.service.Page;
import org.mifosplatform.infrastructure.core.service.PaginationHelper;
import org.mifosplatform.infrastructure.core.service.RoutingDataSource;
import org.mifosplatform.infrastructure.core.service.SearchParameters;
import org.mifosplatform.infrastructure.security.service.PlatformSecurityContext;
import org.mifosplatform.organisation.office.data.OfficeData;
import org.mifosplatform.organisation.office.service.OfficeReadPlatformService;
import org.mifosplatform.portfolio.village.data.VillageData;
import org.mifosplatform.portfolio.village.data.VillageTimelineData;
import org.mifosplatform.portfolio.village.domain.VillageTypeEnumerations;
import org.mifosplatform.portfolio.village.exception.VillageNotFoundException;
import org.mifosplatform.useradministration.domain.AppUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;


@Service
public class VillageReadPlatformServiceImpl implements VillageReadPlatformService {

    private final PlatformSecurityContext context;
    private final JdbcTemplate jdbcTemplate;
    
    private final OfficeReadPlatformService officeReadPlatformService;
    private final PaginationHelper<VillageData> paginationHelper = new PaginationHelper<>();
    private final PaginationParametersDataValidator paginationParametersDataValidator;
    private final static Set<String> supportedOrderByValues = new HashSet<>(Arrays.asList("id", "name", "officeId", "officeName"));
    
    private final VillageDataMapper villageDataMapper = new VillageDataMapper();
    private final RetrieveOneMapper oneVillageMapper = new RetrieveOneMapper();
    
    @Autowired
    public VillageReadPlatformServiceImpl(final PlatformSecurityContext context, final RoutingDataSource dataSource, final OfficeReadPlatformService
            officeReadPlatformService, final PaginationParametersDataValidator paginationParametersDataValidator) {

        this.context = context;
        this.jdbcTemplate = new JdbcTemplate(dataSource);
        this.officeReadPlatformService = officeReadPlatformService;
        this.paginationParametersDataValidator = paginationParametersDataValidator;
    }
    
    @Override
    public VillageData retrieveTemplate(final Long officeId) {

        final Long defaultOfficeId = defaultToUsersOfficeIfNull(officeId);
        final Collection<OfficeData> officeOptions = this.officeReadPlatformService.retrieveAllOfficesForDropdown();
        
        return VillageData.template(defaultOfficeId, officeOptions);
    }
    
    @Override
    public Page<VillageData> retrievePagedAll(SearchParameters searchParameters, PaginationParameters paginationParameters) {

        this.paginationParametersDataValidator.validateParameterValues(paginationParameters, supportedOrderByValues, "audits");
        final AppUser currentUser = this.context.authenticatedUser();
        final String hierarchy = currentUser.getOffice().getHierarchy();
        final String hierarchySearchString = hierarchy + "%";
        
        StringBuilder sqlBuilder = new StringBuilder(200);
        sqlBuilder.append("select SQL_CALC_FOUND_ROWS ");
        sqlBuilder.append(this.villageDataMapper.schema());
        sqlBuilder.append(" where o.hierarchy like ?");
        
        final String extraCriteria = getVillageExtraCriteria(searchParameters);
        if (StringUtils.isNotBlank(extraCriteria)) {
            sqlBuilder.append(" and (").append(extraCriteria).append(")");
        }
        
        if (searchParameters.isOrderByRequested()) {
            sqlBuilder.append(" order by ").append(searchParameters.getOrderBy());
            if (searchParameters.isSortOrderProvided()) {
                sqlBuilder.append(' ').append(searchParameters.getSortOrder());
            }
        }
        
        if (searchParameters.isLimited()) {
            sqlBuilder.append(" limit ").append(searchParameters.getLimit());
            if (searchParameters.isOffset()) {
                sqlBuilder.append(" offset ").append(searchParameters.getOffset());
            }
        }
        
        final String sqlCountRows = "SELECT FOUND_ROWS()";
        return this.paginationHelper.fetchPage(this.jdbcTemplate, sqlCountRows, sqlBuilder.toString(), new Object[] { hierarchySearchString },
                this.villageDataMapper);
    }
    
    @Override
    public Collection<VillageData> retrieveAll(SearchParameters searchParameters, PaginationParameters paginationParameters) {
       
        this.paginationParametersDataValidator.validateParameterValues(paginationParameters, supportedOrderByValues, "audits");
        final AppUser currentUser = this.context.authenticatedUser();
        final String hierarchy = currentUser.getOffice().getHierarchy();
        final String hierarchySearchString = hierarchy + "%";
        
        final StringBuilder sqlBuilder = new StringBuilder(200);
        sqlBuilder.append("select ");
        sqlBuilder.append(this.villageDataMapper.schema());
        sqlBuilder.append(" where o.hierarchy like ?");
        
        final String extraCriteria = getVillageExtraCriteria(searchParameters);
        if (StringUtils.isNotBlank(extraCriteria)) {
            sqlBuilder.append(" and (").append(extraCriteria).append(")");
        }
        
        if (searchParameters.isOrderByRequested()) {
            sqlBuilder.append(" order by ").append(searchParameters.getOrderBy());
            if (searchParameters.isSortOrderProvided()) {
                sqlBuilder.append(' ').append(searchParameters.getSortOrder());
            }
        }
        
        if (searchParameters.isLimited()) {
            sqlBuilder.append(" limit ").append(searchParameters.getLimit());
            if (searchParameters.isOffset()) {
                sqlBuilder.append(" offset ").append(searchParameters.getOffset());
            }
        }
        
        return this.jdbcTemplate.query(sqlBuilder.toString(), this.villageDataMapper, new Object[] { hierarchySearchString });
    }
    
    @Override
    public VillageData retrieveOne(final Long villageId) {

        try {
            final AppUser currentUser = this.context.authenticatedUser();
            final String hierarchy = currentUser.getOffice().getHierarchy();
            final String hierarchySearchString = hierarchy + "%";
            
            final String sql = "select " + this.oneVillageMapper.schema() + " where v.id = ? and o.hierarchy like ? ";
            return this.jdbcTemplate.queryForObject(sql, this.oneVillageMapper, new Object[] { villageId, hierarchySearchString });
            
        } catch (final EmptyResultDataAccessException e) {
            throw new VillageNotFoundException(villageId);
        }
    }
    
    private static final class RetrieveOneMapper implements RowMapper<VillageData> {

        private final String schema;
        
        public RetrieveOneMapper() {
            
            final StringBuilder builder = new StringBuilder(400);
            
            builder.append("v.id as villageId, v.external_id as externalId, v.office_id as officeId, o.name as officeName, v.village_code as villageCode, ");
            builder.append("v.village_name as villageName, v.counter as counter, v.taluk as taluk, v.district as district, v.pincode as pincode, ");
            builder.append("v.state as state, v.status as status, ");
            
            builder.append("v.activatedon_date as activatedOnDate, ");
            builder.append("acu.username as activatedByUsername, ");
            builder.append("acu.firstname as activatedByFirstname, ");
            builder.append("acu.lastname as activatedByLastname, ");
            
            builder.append("v.submitedon_date as submittedOnDate, ");
            builder.append("sbu.username as submittedByUsername, ");
            builder.append("sbu.firstname as submittedByFirstname, ");
            builder.append("sbu.lastname as submittedByLastname ");
            
            builder.append("from chai_villages v ");
            builder.append("join m_office o on o.id = v.office_id ");
            builder.append("left join m_appuser sbu on sbu.id = v.submitedon_userid ");
            builder.append("left join m_appuser acu on acu.id = v.activatedon_userid ");
            
            this.schema = builder.toString();
        }
        
        public String schema() {
            return this.schema;
        }
        
        @Override
        public VillageData mapRow(final ResultSet rs, @SuppressWarnings("unused") final int rowNum) throws SQLException {

            final Long id = rs.getLong("villageId");
            final String externalId = rs.getString("externalId");
            final Long officeId = rs.getLong("officeId");
            final String officeName = rs.getString("officeName");
            final String villageCode = rs.getString("villageCode");
            final String villageName = rs.getString("villageName");
            final Long counter = rs.getLong("counter");
            final String taluk = rs.getString("taluk");
            final String district = rs.getString("district");
            final Long pincode = rs.getLong("pincode");
            final String state = rs.getString("state");
            final Integer status = JdbcSupport.getInteger(rs, "status");
            final EnumOptionData statusName = VillageTypeEnumerations.status(status);
            final LocalDate activatedOnDate = JdbcSupport.getLocalDate(rs, "activatedOnDate");
            final String activatedByUsername = rs.getString("activatedByUsername");
            final String activatedByFirstName = rs.getString("activatedByFirstName");
            final String activatedByLastName = rs.getString("activatedByLastName");
            final LocalDate submittedOnDate = JdbcSupport.getLocalDate(rs, "submittedOnDate");
            final String submittedByUsername = rs.getString("submittedByUsername");
            final String submittedByFirstName = rs.getString("submittedByFirstName");
            final String submittedByLastName = rs.getString("submittedByLastName");
            
            final VillageTimelineData timeline = new VillageTimelineData(activatedOnDate, activatedByUsername, activatedByFirstName, 
                    activatedByLastName, submittedOnDate, submittedByUsername, submittedByFirstName, submittedByLastName);

            return VillageData.instance(id, externalId, officeId, officeName, villageCode, villageName, counter, taluk, district, pincode, 
                    state, statusName, timeline);
        }
        
    }
    
    private String getVillageExtraCriteria(SearchParameters searchParameters) {

        String extraCriteria = "";
        
        String sqlSearch = searchParameters.getSqlSearch();
        final Long officeId = searchParameters.getOfficeId();
        final String externalId = searchParameters.getExternalId();
        final String name = searchParameters.getName();
        
        if (sqlSearch != null) {
            sqlSearch = sqlSearch.replaceAll(" village_name ", " v.village_name ");
            sqlSearch = sqlSearch.replaceAll("village_name ", "v.village_name ");
            extraCriteria = " and (" + sqlSearch + ")";
        }
        
        if (officeId != null) {
            extraCriteria += " and v.office_id = " + officeId;
        }
        
        if (externalId != null) {
            extraCriteria += " and v.external_id like " + ApiParameterHelper.sqlEncodeString(externalId);
        }
        
        if (externalId != null) {
            extraCriteria += " and v.village_name like " + ApiParameterHelper.sqlEncodeString(name);
        }
        
        if (searchParameters.isScopedByOfficeHierarchy()) {
            extraCriteria += " and o.hierarchy like " + ApiParameterHelper.sqlEncodeString(searchParameters.getHierarchy() + "%");
        }

        if (StringUtils.isNotBlank(extraCriteria)) {
            extraCriteria = extraCriteria.substring(4);
        }
        
        return extraCriteria;
    }

    private static final class VillageDataMapper implements RowMapper<VillageData> {
        
        private final String schema;
        
        public VillageDataMapper() {
            final StringBuilder builder = new StringBuilder(200);
            
            builder.append(" v.id as id, v.external_id as externalId, v.office_id as officeId, o.name as officeName, ");
            builder.append(" v.village_code as villageCode, v.village_name as villageName, v.counter as counter, v.taluk as taluk, ");
            builder.append(" v.district as district, v.pincode as pincode, v.state as state, v.`status` as status, ");
            builder.append(" v.activatedon_date as activatedOnDate, v.submitedon_date as submittedOnDate, ");
            builder.append(" acu.username as activatedByUsername, acu.firstname as activatedByFirstName, acu.lastname as activatedByLastName, ");
            builder.append(" sbu.username as submittedByUsername, sbu.firstname as submittedByFirstName, sbu.lastname as submittedByLastName ");
            builder.append(" from chai_villages v  ");
            builder.append(" join m_office o on o.id = v.office_id ");
            builder.append(" left join m_appuser acu on acu.id = v.activatedon_userid ");
            builder.append(" left join m_appuser sbu on sbu.id = v.submitedon_userid ");
            
            this.schema = builder.toString();
        }
        
        public final String schema() {
            return this.schema;
        }

        @Override
        public VillageData mapRow(final ResultSet rs, @SuppressWarnings("unused") final int rowNum) throws SQLException {

            final Long id = rs.getLong("id");
            final String externalId = rs.getString("externalId");
            final Long officeId = rs.getLong("officeId");
            final String officeName = rs.getString("officeName");
            final String villageCode = rs.getString("villageCode");
            final String villageName = rs.getString("villageName");
            final Long counter = rs.getLong("counter");
            final String taluk = rs.getString("taluk");
            final String district = rs.getString("district");
            final Long pincode = rs.getLong("pincode");
            final String state = rs.getString("state");
            final Integer status = JdbcSupport.getInteger(rs, "status");
            final EnumOptionData statusName = VillageTypeEnumerations.status(status);
            final LocalDate activatedOnDate = JdbcSupport.getLocalDate(rs, "activatedOnDate");
            final String activatedByUsername = rs.getString("activatedByUsername");
            final String activatedByFirstName = rs.getString("activatedByFirstName");
            final String activatedByLastName = rs.getString("activatedByLastName");
            final LocalDate submittedOnDate = JdbcSupport.getLocalDate(rs, "submittedOnDate");
            final String submittedByUsername = rs.getString("submittedByUsername");
            final String submittedByFirstName = rs.getString("submittedByFirstName");
            final String submittedByLastName = rs.getString("submittedByLastName");
            
            final VillageTimelineData timeline = new VillageTimelineData(activatedOnDate, activatedByUsername, activatedByFirstName, 
                    activatedByLastName, submittedOnDate, submittedByUsername, submittedByFirstName, submittedByLastName);

            return VillageData.instance(id, externalId, officeId, officeName, villageCode, villageName, counter, taluk, district, pincode, 
                    state, statusName, timeline);
        }
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

        final AppUser currentUser = this.context.authenticatedUser();
        /*final String hierarchy = currentUser.getOffice().getHierarchy();
        final String hierarchySerachingString = hierarchy + "%";*/
        
        final VillageLookupDataMapper rm = new VillageLookupDataMapper();
        final String sql = "Select "+ rm.schema() + " where v.office_id = ? "; //and o.hierarchy like ? ";
        return this.jdbcTemplate.query(sql, rm, new Object[] { officeId });
    }
    
    private static final class VillageLookupDataMapper implements RowMapper<VillageData> {

        public final String schema() {
            return "v.id as id, v.village_name as villageName from chai_villages v ";  //join m_office o on o.id = v.office_id ";
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
