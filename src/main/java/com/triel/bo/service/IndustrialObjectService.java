package com.triel.bo.service;

import com.triel.bo.domain.IndustrialObject;
import com.triel.bo.persistence.IndustrialObjectRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@RestController
@RequestMapping("/objects")
public class IndustrialObjectService {

    private IndustrialObjectRepository repository;

    @Autowired
    public void setRepository(IndustrialObjectRepository repository) {
        this.repository = repository;
    }

    @RequestMapping
    public Collection<IndustrialObjectCreateRead> listAll() {
        Iterable<IndustrialObject> iteration = repository.findAll();
        return StreamSupport.stream(iteration.spliterator(), false)
                .map(o -> toIndustrialObjectCreateRead(o))
                .collect(Collectors.toList());
    }


    @RequestMapping("/{id}")
    public ResponseEntity<IndustrialObjectCreateRead> getById(@PathVariable("id") String id) {
        IndustrialObject object = repository.findOne(id);
        if( object == null)
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);

        return new ResponseEntity<IndustrialObjectCreateRead>(
                toIndustrialObjectCreateRead(object),
                HttpStatus.FOUND);
    }


    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity<IndustrialObjectCreateRead> createIndustrialObject(@RequestBody IndustrialObject object) {
        if (object == null)
            return new ResponseEntity<IndustrialObjectCreateRead>(HttpStatus.NO_CONTENT);

        IndustrialObject createdObject = repository.save(object);
        return new ResponseEntity<IndustrialObjectCreateRead>(
                toIndustrialObjectCreateRead(createdObject),
                HttpStatus.CREATED
                );
    }

    private static IndustrialObjectCreateRead toIndustrialObjectCreateRead(IndustrialObject persistObject) {
        return new IndustrialObjectCreateRead(
                persistObject.getName(),
                persistObject.getDescription(),
                persistObject.getState()
                );
    }

    @RequestMapping(method = RequestMethod.PUT, path = "/{id}")
    public ResponseEntity<IndustrialObjectCreateRead> replaceIndustrialObject(@PathVariable("id") String name, @RequestBody IndustrialObjectUpdate object) {
        if (object == null)
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);

        IndustrialObject previousObject = repository.findByName(name);
        if( previousObject == null)
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);

        previousObject.setDescription(object.getDescription());
        previousObject.setState(object.getState());

        IndustrialObject updatedObject = repository.save(previousObject);
        return new ResponseEntity<IndustrialObjectCreateRead>(
                toIndustrialObjectCreateRead(updatedObject),
                HttpStatus.OK
        );
    }

    @RequestMapping(method = RequestMethod.DELETE, path = "/{id}")
    public ResponseEntity<IndustrialObjectCreateRead> deleteIndustrialObjectByName(@PathVariable("id") String name) {
        IndustrialObject industrialObject = repository.findByName(name);
        if( industrialObject == null)
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);

        repository.delete(name);
        return new ResponseEntity<IndustrialObjectCreateRead>(
                toIndustrialObjectCreateRead(industrialObject),
                HttpStatus.OK
        );
    }
}