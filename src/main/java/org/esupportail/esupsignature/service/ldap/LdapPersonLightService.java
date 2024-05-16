package org.esupportail.esupsignature.service.ldap;

import org.esupportail.esupsignature.config.ldap.LdapProperties;
import org.esupportail.esupsignature.service.ldap.entry.PersonLightLdap;
import org.esupportail.esupsignature.service.ldap.mapper.PersonLightLdapAttributesMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.ldap.core.LdapTemplate;
import org.springframework.ldap.query.LdapQuery;
import org.springframework.ldap.query.LdapQueryBuilder;
import org.springframework.stereotype.Service;

import jakarta.annotation.Resource;
import java.text.MessageFormat;
import java.util.List;

@Service
@ConditionalOnProperty({"spring.ldap.base"})
@EnableConfigurationProperties(LdapProperties.class)
public class LdapPersonLightService {

    private static final Logger logger = LoggerFactory.getLogger(LdapPersonLightService.class);

    @Resource
    private LdapProperties ldapProperties;

    @Resource
    private LdapTemplate ldapTemplate;

    public List<PersonLightLdap> searchLight(String searchString) {
        String formattedFilter = MessageFormat.format(ldapProperties.getUsersSearchFilter(), (Object[]) new String[] { searchString });
        logger.debug("search PersonLdapLight with : " + formattedFilter);
        return launchLdapQuery(formattedFilter);
    }

    public List<PersonLightLdap> getPersonLdapLight(String authName) {
        String formattedFilter = MessageFormat.format(ldapProperties.getUserIdSearchFilter(), (Object[]) new String[] { authName });
        logger.debug("search PersonLdapLight with : " + formattedFilter);
        return launchLdapQuery(formattedFilter);
    }

    public List<PersonLightLdap> getPersonLdapLightByEppn(String eppn) {
        String formattedFilter = MessageFormat.format(ldapProperties.getUserEppnSearchFilter(), (Object[]) new String[] { eppn });
        logger.debug("search PersonLdapLight by eppn");
        return launchLdapQuery(formattedFilter);
    }

    private List<PersonLightLdap> launchLdapQuery(String formattedFilter) {
        StringBuilder objectClasses = new StringBuilder();
        for(String objectClass : ldapProperties.getUserObjectClasses()) {
            objectClasses.append("(objectClass=").append(objectClass).append(")");
        }
        formattedFilter = "(&(|" + objectClasses + ")" + formattedFilter + ")";
        LdapQuery ldapQuery = LdapQueryBuilder.query().countLimit(10).base(ldapProperties.getSearchBase()).filter(formattedFilter);
        logQuery(ldapQuery);
        return ldapTemplate.search(ldapQuery, new PersonLightLdapAttributesMapper());
    }

    private void logQuery(LdapQuery ldapQuery) {
        StringBuilder queryStringBuilder = new StringBuilder();
        queryStringBuilder.append("Base: ").append(ldapQuery.base()).append("\n");
        queryStringBuilder.append("Filtre: ").append(ldapQuery.filter().encode()).append("\n");
        queryStringBuilder.append("Attributs: ").append(ldapQuery.attributes()).append("\n");
        logger.debug("personLight : " + queryStringBuilder);
    }

}