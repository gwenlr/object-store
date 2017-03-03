package com.triel.bo.service;

import com.triel.bo.domain.IndustrialImage;
import com.triel.bo.domain.IndustrialObject;
import com.triel.bo.persistence.IndustrialImageRepository;
import com.triel.bo.persistence.IndustrialObjectRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MimeType;
import org.springframework.util.MimeTypeUtils;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@RestController
@RequestMapping("/objects")
public class IndustrialObjectService {

    private IndustrialObjectRepository objectRepository;
    private IndustrialImageRepository imageRepository;

    @Autowired
    public void setObjectRepository(IndustrialObjectRepository objectRepository) {
        this.objectRepository = objectRepository;
    }

    @Autowired
    public void setImageRepository(IndustrialImageRepository imageRepository) {
        this.imageRepository = imageRepository;
    }

    @RequestMapping
    public Collection<IndustrialObjectCreateRead> listAll() {
        Iterable<IndustrialObject> iteration = objectRepository.findAll();
        return StreamSupport.stream(iteration.spliterator(), false)
                .map(IndustrialObjectService::toIndustrialObjectCreateRead)
                .collect(Collectors.toList());
    }


    @RequestMapping("/{id}")
    public ResponseEntity<IndustrialObjectCreateRead> getById(@PathVariable("id") String id) {
        IndustrialObject object = objectRepository.findOne(id);
        if( object == null)
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);

        return new ResponseEntity<>(
                toIndustrialObjectCreateRead(object),
                HttpStatus.FOUND);
    }


    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity<IndustrialObjectCreateRead> createIndustrialObject(@RequestBody IndustrialObject object) {
        if (object == null)
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);

        IndustrialObject createdObject = objectRepository.save(object);
        return new ResponseEntity<>(
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

        IndustrialObject previousObject = objectRepository.findByName(name);
        if( previousObject == null)
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);

        previousObject.setDescription(object.getDescription());
        previousObject.setState(object.getState());

        IndustrialObject updatedObject = objectRepository.save(previousObject);
        return new ResponseEntity<>(
                toIndustrialObjectCreateRead(updatedObject),
                HttpStatus.OK
        );
    }

    @RequestMapping(method = RequestMethod.DELETE, path = "/{id}")
    public ResponseEntity<IndustrialObjectCreateRead> deleteIndustrialObjectByName(@PathVariable("id") String name) {
        try {
            IndustrialObject industrialObject = objectRepository.findByName(name);
            if( industrialObject == null)
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);

            objectRepository.delete(name);
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON_UTF8);
            return new ResponseEntity<>(
                    toIndustrialObjectCreateRead(industrialObject),
                    headers,
                    HttpStatus.OK
            );
        } catch( Throwable th) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @RequestMapping(method = RequestMethod.POST, path="/{id}/images")
    public ResponseEntity<?> addImageToIndustrialObject(@PathVariable("id") String name, @RequestBody byte[] imageContent, @RequestHeader HttpHeaders headers) {
        try {
            IndustrialObject industrialObject = objectRepository.findByName(name);
            if (industrialObject == null)
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);

            MimeType contentType = headers.getContentType();
            if (!isSupportedImageContentType(contentType))
                return new ResponseEntity<>(HttpStatus.UNSUPPORTED_MEDIA_TYPE);

            IndustrialImage savedImage = createIndustrialImage(industrialObject, contentType, imageContent);

            IndustrialImageCreated imageCreated = new IndustrialImageCreated(industrialObject.getName(), savedImage.getUuid().toString());
            HttpHeaders responseHeaders= new HttpHeaders();
            responseHeaders.setContentType(MediaType.APPLICATION_JSON_UTF8);
            return new ResponseEntity<>(imageCreated, responseHeaders, HttpStatus.CREATED);

        } catch( Throwable th) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private IndustrialImage createIndustrialImage(IndustrialObject industrialObject, MimeType contentType, @RequestBody byte[] imageContent) {
        IndustrialImage image = new IndustrialImage();
        image.setContent(imageContent);
        image.setContentType(contentType.getType());
        image.setIndustrialObject(industrialObject);
        return imageRepository.save(image);
    }

    private boolean isSupportedImageContentType(MimeType contentType) {
        return     MimeTypeUtils.IMAGE_JPEG.equals(contentType)
                || MimeTypeUtils.IMAGE_PNG.equals(contentType);
    }

    @RequestMapping(method=RequestMethod.GET, path="/{id}/images")
    public ResponseEntity<Collection<IndustrialImageMetadata>> getAllImageMetadata(@PathVariable("id") String objectName) {
        try {

            IndustrialObject industrialObject = objectRepository.findByName(objectName);
            if (industrialObject == null)
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);

            Iterable<IndustrialImage> imageIterable = imageRepository.findByIndustrialObject(industrialObject);
            Collection<IndustrialImageMetadata> metadataList = StreamSupport.stream(imageIterable.spliterator(),false)
                    .map(IndustrialObjectService::extractMetadata)
                    .collect(Collectors.toList());

            return new ResponseEntity<>(metadataList, HttpStatus.OK);

        } catch( Throwable th ) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private static IndustrialImageMetadata extractMetadata(IndustrialImage image ) {
        IndustrialImageMetadata metadata = new IndustrialImageMetadata();
        metadata.setUuid(image.getUuid().toString());
        metadata.setContentType(image.getContentType());
        return metadata;
    }

}