package com.triel.bo.service;

import com.triel.bo.domain.IndustrialImage;
import com.triel.bo.persistence.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

import static com.triel.bo.service.UUIDHelper.*;

@RestController
@RequestMapping("/images")
public class IndustrialImageService {

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


    @RequestMapping(method=RequestMethod.GET, path="/{id}")
    public ResponseEntity<?> getImage(@PathVariable("id") String imageUuidAsString) {
        try {

            UUID imageUUID = toUUID(imageUuidAsString);
            IndustrialImage image = imageRepository.findOne(imageUUID);

            if (image == null)
                return imageNotFoundResponse();

            return imageFoundResponse(image);

        } catch (InvalidUUIDException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch( Throwable th) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private ResponseEntity<?> imageNotFoundResponse() {
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    private ResponseEntity<byte[]> imageFoundResponse(IndustrialImage image ) {
        HttpHeaders headers = new HttpHeaders();
        MediaType contentType = MediaType.parseMediaType(image.getContentType());
        headers.setContentType(contentType);

        return new ResponseEntity(
                image.getContent(),
                headers,
                HttpStatus.OK
        );
    }


    @RequestMapping(method=RequestMethod.DELETE, path="/{id}")
    public ResponseEntity<?> deleteImage(@PathVariable("id") String imageUuidAsString) {
        try {

            UUID imageUUID = toUUID(imageUuidAsString);
            IndustrialImage image = imageRepository.findOne(imageUUID);
            if (image == null)
                return imageNotFoundResponse();

            imageRepository.delete(image);

            return imageDeletedResponse();

        } catch (InvalidUUIDException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch( Throwable th) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private ResponseEntity<?> imageDeletedResponse() {
        return new ResponseEntity<>(HttpStatus.OK);
    }
}