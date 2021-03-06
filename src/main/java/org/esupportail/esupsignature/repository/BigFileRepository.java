package org.esupportail.esupsignature.repository;

import org.esupportail.esupsignature.entity.BigFile;
import org.esupportail.esupsignature.repository.custom.BigFileRepositoryCustom;
import org.springframework.data.repository.CrudRepository;

public interface BigFileRepository extends CrudRepository<BigFile, Long>, BigFileRepositoryCustom {

}
