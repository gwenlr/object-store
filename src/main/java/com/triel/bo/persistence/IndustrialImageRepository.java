package com.triel.bo.persistence;

import com.triel.bo.domain.IndustrialImage;
import com.triel.bo.domain.IndustrialObject;
import org.springframework.data.repository.CrudRepository;

import java.util.UUID;

public interface IndustrialImageRepository extends CrudRepository<IndustrialImage,UUID> {

    Iterable<IndustrialImage> findByIndustrialObject(IndustrialObject object);


}
