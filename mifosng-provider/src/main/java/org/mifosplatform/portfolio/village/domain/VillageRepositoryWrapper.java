package org.mifosplatform.portfolio.village.domain;

import org.mifosplatform.portfolio.village.exception.VillageNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class VillageRepositoryWrapper {

    private final VillageRepository repository;

    @Autowired
    public VillageRepositoryWrapper(VillageRepository repository) {
        this.repository = repository;
    }
    
    public Village findOneWithNotFoundDetection(final Long id) {
         final Village entity = this.repository.findOne(id);
        if (entity == null) {
            throw new VillageNotFoundException(id);
        }
        return entity;
    }
    
    public void saveAndFlush(final Village entity) {
        this.repository.saveAndFlush(entity);
    }
    
    public void save(final Village entity) {
        this.repository.save(entity);
    }
}
