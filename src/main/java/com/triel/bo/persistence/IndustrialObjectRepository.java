package com.triel.bo.persistence;

import com.triel.bo.domain.IndustrialObject;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface IndustrialObjectRepository extends CrudRepository<IndustrialObject,String> {

    IndustrialObject findByName(@Param("name") String name);

    List<IndustrialObject> findByState(@Param("state") String state);

}
