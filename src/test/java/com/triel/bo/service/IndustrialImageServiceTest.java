package com.triel.bo.service;

import com.triel.bo.ObjectStoreApplication;
import com.triel.bo.domain.IndustrialImage;
import com.triel.bo.domain.IndustrialObject;
import com.triel.bo.persistence.IndustrialImageRepository;
import com.triel.bo.persistence.IndustrialObjectRepository;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.MimeTypeUtils;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = ObjectStoreApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class IndustrialImageServiceTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private IndustrialObjectRepository objectRepository;

    @Autowired
    private IndustrialImageRepository imageRepository;

    private IndustrialObject industrialObject;
    private IndustrialImage image;

    private static final byte[] IMAGE_CONTENT = {1,3,7,9,11,13};

    @Before
    public void setUp() throws Exception {
        IndustrialObject objectToCreate = new IndustrialObject("ObjectName","Blablabla","Active");
        industrialObject = objectRepository.save(objectToCreate);

        IndustrialImage imageToCreate = new IndustrialImage();
        imageToCreate.setIndustrialObject(industrialObject);
        imageToCreate.setContentType(MimeTypeUtils.IMAGE_JPEG_VALUE);
        imageToCreate.setContent(IMAGE_CONTENT);
        image = imageRepository.save(imageToCreate);
    }

    @After
    public void tearDown() throws Exception {
        imageRepository.deleteAll();
        objectRepository.deleteAll();
    }

    @Test
    public void shallNotGetImageWhenImageIdIsInvalid() throws Exception {
        ResponseEntity<byte[]> response = getImage("invalidId");
        assertThat( response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    private ResponseEntity<byte[]> getImage(String imageId) {
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Arrays.asList());

        HttpEntity<?> entity = new HttpEntity<>(headers);
        ParameterizedTypeReference<byte[]> ref= new ParameterizedTypeReference<byte[]>(){};
        Map<String,?> urlVariables = new HashMap<>();
        return restTemplate.exchange(
                "/images/"+imageId,
                HttpMethod.GET,
                entity,
                ref,
                urlVariables);
    }


    @Test
    public void shallNotGetImageWhenImageIdDoesNotExist() throws Exception {
        ResponseEntity<byte[]> response = getImage(UUID.randomUUID().toString());
        assertThat( response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    public void shallGetImageWhenImageExists() throws Exception {
        ResponseEntity<byte[]> response = getImage(image.getUuid().toString());

        assertThat(response.getHeaders().getContentType())
                .isEqualTo(MediaType.IMAGE_JPEG);
        assertThat( response)
            .extracting("statusCode", "body")
            .contains(HttpStatus.OK, IMAGE_CONTENT);
    }

    @Test
    public void shallNotDeleteImageWhenImageIdIsInvalid() throws Exception {
        ResponseEntity<String> response = deleteImage("invalidUUID");
        assertThat( response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }


    private ResponseEntity<String> deleteImage(String imageId) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.TEXT_PLAIN);

        HttpEntity<?> entity = new HttpEntity<>(headers);
        ParameterizedTypeReference<String> ref= new ParameterizedTypeReference<String>(){};
        Map<String,?> urlVariables = new HashMap<>();
        return restTemplate.exchange(
                "/images/"+imageId,
                HttpMethod.DELETE,
                entity,
                ref,
                urlVariables);
    }

    @Test
    public void shallNotDeleteImageWhenImageIdDoesNotExist() throws Exception {
        ResponseEntity<String> response = deleteImage(UUID.randomUUID().toString());
        assertThat( response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    public void deleteImage() throws Exception {
        ResponseEntity<String> response = deleteImage(image.getUuid().toString());

        assertThat( response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNull();

        assertThat(imageRepository.findAll()).isEmpty();
    }
}