package com.triel.bo.service;

import com.triel.bo.ObjectStoreApplication;
import com.triel.bo.domain.IndustrialImage;
import com.triel.bo.domain.IndustrialObject;
import com.triel.bo.persistence.IndustrialImageRepository;
import com.triel.bo.persistence.IndustrialObjectRepository;
import org.assertj.core.groups.Tuple;
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
import org.springframework.util.MimeType;
import org.springframework.util.MimeTypeUtils;

import java.util.*;

import static java.util.Collections.*;
import static org.assertj.core.api.Assertions.*;
import static org.springframework.http.MediaType.*;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = ObjectStoreApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class IndustrialObjectServiceTest {

    private static final String NAME_1 = "Name 1";
    private static final String DESCRIPTION_1 = "Description 1";
    private static final String STATE_1 = "State 1";
    private static final String DESCRIPTION_1_BIS = "Description 1 bis";
    private static final String STATE_1_BIS = "State 1 bis";

    private static final String NAME_2 = "Name 2";
    private static final String DESCRIPTION_2 = "Description 2";
    private static final String STATE_2 = "State 2";

    private IndustrialObject object1;
    private IndustrialObject object1bis;
    private IndustrialObject object2;
    private Tuple object1Tuple;
    private Tuple object1bisTuple;
    private Tuple object2Tuple;

    private static byte[] IMAGE_1 = {0,1,2,3,4,5,6,7,8,9};
    private static byte[] IMAGE_2 = {10,11,12,13,14,15,16,17,18,19};

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private IndustrialObjectRepository objectRepository;

    @Autowired
    private IndustrialImageRepository imageRepository;

    @Before
    public void setUp() throws Exception {
        object1 = new IndustrialObject(NAME_1,DESCRIPTION_1,STATE_1);
        object1bis = new IndustrialObject(NAME_1,DESCRIPTION_1_BIS,STATE_1_BIS);
        object2 = new IndustrialObject(NAME_2,DESCRIPTION_2,STATE_2);

        object1Tuple = new Tuple(NAME_1,DESCRIPTION_1,STATE_1);
        object1bisTuple = new Tuple(NAME_1,DESCRIPTION_1_BIS,STATE_1_BIS);
        object2Tuple = new Tuple(NAME_2,DESCRIPTION_2,STATE_2);
    }

    @After
    public void tearDown() throws Exception {
        imageRepository.deleteAll();
        objectRepository.deleteAll();
    }

    @Test
    public void listAllWithFilledContentShallReturnAllContent() throws Exception {
        objectRepository.save( object1 );
        objectRepository.save( object2 );


        ResponseEntity<List<IndustrialObjectCreateRead>> responseEntity = getAllObjects();

        List<IndustrialObjectCreateRead> objects = responseEntity.getBody();
        MediaType contentType = responseEntity.getHeaders().getContentType();
        HttpStatus statusCode = responseEntity.getStatusCode();


        assertThat(statusCode).isEqualTo(HttpStatus.OK);
        assertThat(contentType).isEqualTo(APPLICATION_JSON_UTF8);
        assertThat(objects).hasSize(2);
        assertThat(objects)
                .extracting("name", "description", "state")
                .contains(
                        object1Tuple,
                        object2Tuple
                        );
    }

    private ResponseEntity<List<IndustrialObjectCreateRead>> getAllObjects() {
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(singletonList(APPLICATION_JSON));

        HttpEntity<?> entity = new HttpEntity<>(headers);
        ParameterizedTypeReference<List<IndustrialObjectCreateRead>> ref= new ParameterizedTypeReference<List<IndustrialObjectCreateRead>>(){};
        Map<String,?> urlVariables = new HashMap<>();
        return restTemplate.exchange(
                "/objects",
                HttpMethod.GET,
                entity,
                ref,
                urlVariables);
    }

    private ResponseEntity<IndustrialObjectCreateRead> getObject(String objectId) {
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(singletonList(APPLICATION_JSON));

        HttpEntity<?> entity = new HttpEntity<>(headers);
        ParameterizedTypeReference<IndustrialObjectCreateRead> ref= new ParameterizedTypeReference<IndustrialObjectCreateRead>(){};
        Map<String,?> urlVariables = new HashMap<>();
        return restTemplate.exchange(
                "/objects/"+objectId,
                HttpMethod.GET,
                entity,
                ref,
                urlVariables);
    }

    @Test
    public void listAllWithEmptyRepositoryShallReturnEmptyList() throws Exception {
        ResponseEntity<List<IndustrialObjectCreateRead>> responseEntity = getAllObjects();


        List<IndustrialObjectCreateRead> objects = responseEntity.getBody();
        MediaType contentType = responseEntity.getHeaders().getContentType();
        HttpStatus statusCode = responseEntity.getStatusCode();


        assertThat(statusCode).isEqualTo(HttpStatus.OK);
        assertThat(contentType).isEqualTo(APPLICATION_JSON_UTF8);
        assertThat(objects).isEmpty();
    }


    @Test
    public void getByIdShallReturnNotFoundWhenIdNotExists() throws Exception {
        ResponseEntity<IndustrialObjectCreateRead> responseEntity = getObject("notExists");


        HttpStatus statusCode = responseEntity.getStatusCode();
        assertThat(statusCode).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    public void getByIdShallReturnElementWhenIdExists() throws Exception {
        objectRepository.save(object1);

        ResponseEntity<IndustrialObjectCreateRead> responseEntity = getObject(object1.getName());


        IndustrialObjectCreateRead returnedObject = responseEntity.getBody();
        MediaType contentType = responseEntity.getHeaders().getContentType();
        HttpStatus statusCode = responseEntity.getStatusCode();


        assertThat(statusCode).isEqualTo(HttpStatus.FOUND);
        assertThat(contentType).isEqualTo(APPLICATION_JSON_UTF8);
        assertThat(returnedObject)
                .extracting("name", "description", "state")
                .contains(NAME_1,DESCRIPTION_1,STATE_1);
    }

    @Test
    public void createIndustrialObject() throws Exception {
        ResponseEntity<IndustrialObjectCreateRead> responseEntity = postObject(
                new IndustrialObjectCreateRead(NAME_1,DESCRIPTION_1,STATE_1)
        );

        IndustrialObjectCreateRead object = responseEntity.getBody();

        MediaType contentType = responseEntity.getHeaders().getContentType();
        HttpStatus statusCode = responseEntity.getStatusCode();


        assertThat(statusCode).isEqualTo(HttpStatus.CREATED);
        assertThat(contentType).isEqualTo(APPLICATION_JSON_UTF8);
        assertThat(object)
                .extracting("name", "description", "state")
                .containsExactly(NAME_1, DESCRIPTION_1, STATE_1);
    }

    private ResponseEntity<IndustrialObjectCreateRead> postObject(IndustrialObjectCreateRead object) {
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Arrays.asList(APPLICATION_JSON));
        headers.setContentType(APPLICATION_JSON_UTF8);

        HttpEntity<?> entity = new HttpEntity<>(object, headers);
        Map<String,?> urlVariables = new HashMap<>();
        return restTemplate.exchange(
                "/objects",
                HttpMethod.POST,
                entity,
                IndustrialObjectCreateRead.class,
                urlVariables);
    }

    @Test
    public void replaceIndustrialObjectShallReturnNotFoundWhenBadNameSpecified() throws Exception {
        ResponseEntity<IndustrialObjectCreateRead> response = putObject(
                NAME_2,
                new IndustrialObjectUpdate(DESCRIPTION_1_BIS,STATE_1_BIS));

        assertThat(response)
                .extracting("statusCode")
                .contains(HttpStatus.NOT_FOUND);
    }

    private ResponseEntity<IndustrialObjectCreateRead> putObject(String objectName, IndustrialObjectUpdate update) {
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(singletonList(APPLICATION_JSON));
        headers.setContentType(APPLICATION_JSON_UTF8);

        HttpEntity<?> entity = new HttpEntity<>(
                update,
                headers);
        Map<String,?> urlVariables = new HashMap<>();
        return restTemplate.exchange(
                "/objects/"+objectName,
                HttpMethod.PUT,
                entity,
                IndustrialObjectCreateRead.class,
                urlVariables);
    }

    @Test
    public void replaceIndustrialObjectShallReturnNotFoundWhenNoBody() throws Exception {
        ResponseEntity<IndustrialObjectCreateRead> response = putObject(
                NAME_1,
                new IndustrialObjectUpdate(DESCRIPTION_1_BIS,STATE_1_BIS));

        assertThat(response)
                .extracting("statusCode")
                .contains(HttpStatus.NOT_FOUND);
    }


    @Test
    public void replaceIndustrialObjectShallReturnUpdatedObject() throws Exception {
        objectRepository.save(object1);


        ResponseEntity<IndustrialObjectCreateRead> response = putObject(
                NAME_1,
                new IndustrialObjectUpdate(DESCRIPTION_1_BIS,STATE_1_BIS));

        IndustrialObjectCreateRead object = response.getBody();
        MediaType contentType = response.getHeaders().getContentType();
        HttpStatus statusCode = response.getStatusCode();


        assertThat(statusCode).isEqualTo(HttpStatus.OK);
        assertThat(contentType).isEqualTo(APPLICATION_JSON_UTF8);
        assertThat(object)
                .extracting("name", "description", "state")
                .containsExactly(NAME_1, DESCRIPTION_1_BIS, STATE_1_BIS);
    }

    @Test
    public void deleteIndustrialObjectByNameShallReturnNotFoundWhenBadId() throws Exception {
        ResponseEntity<IndustrialObjectCreateRead> responseEntity = deleteObject(NAME_1);

        HttpStatus statusCode = responseEntity.getStatusCode();
        assertThat(statusCode).isEqualTo(HttpStatus.NOT_FOUND);
    }

    private ResponseEntity<IndustrialObjectCreateRead> deleteObject(String id) {
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(singletonList(APPLICATION_JSON));

        HttpEntity<IndustrialObjectCreateRead> entity = new HttpEntity<>(headers);
        ParameterizedTypeReference<IndustrialObjectCreateRead> ref= new ParameterizedTypeReference<IndustrialObjectCreateRead>(){};
        Map<String,?> urlVariables = new HashMap<>();
        return restTemplate.exchange(
                "/objects/"+id,
                HttpMethod.DELETE,
                entity,
                ref,
                urlVariables);
    }

    @Test
    public void deleteIndustrialObjectByName() throws Exception {
        objectRepository.save(object1);

        ResponseEntity<IndustrialObjectCreateRead> responseEntity = deleteObject(NAME_1);

        IndustrialObjectCreateRead object = responseEntity.getBody();
        MediaType contentType = responseEntity.getHeaders().getContentType();
        HttpStatus statusCode = responseEntity.getStatusCode();


        assertThat(statusCode).isEqualTo(HttpStatus.OK);
        assertThat(contentType).isEqualTo(APPLICATION_JSON_UTF8);
        assertThat(object)
                .extracting("name", "description", "state")
                .containsExactly(NAME_1, DESCRIPTION_1, STATE_1);

        assertThat(objectRepository.findAll())
            .isEmpty();
    }

    @Test
    public void createImageShallFailedWhenObjectNoExists() throws Exception {
        ResponseEntity<IndustrialImageCreated> responseEntity = postImage("notExists", IMAGE_1, MediaType.IMAGE_PNG);

        HttpStatus statusCode = responseEntity.getStatusCode();
        assertThat(statusCode).isEqualTo(HttpStatus.NOT_FOUND);
    }

    private ResponseEntity<IndustrialImageCreated> postImage(String objectId, byte[] image, MediaType imageType) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(imageType);


        HttpEntity<byte[]> entity = new HttpEntity<>(image, headers);
        ParameterizedTypeReference<IndustrialImageCreated> ref= new ParameterizedTypeReference<IndustrialImageCreated>(){};
        Map<String,?> urlVariables = new HashMap<>();
        return restTemplate.exchange(
                "/objects/"+objectId+"/images",
                HttpMethod.POST,
                entity,
                ref,
                urlVariables);
    }


    @Test
    public void createImageShallFailedWhenUnsupportedContentType() throws Exception {
        objectRepository.save(object1);
        ResponseEntity<IndustrialImageCreated> responseEntity = postImage(object1.getName(), IMAGE_1, MediaType.IMAGE_GIF);

        HttpStatus statusCode = responseEntity.getStatusCode();
        assertThat(statusCode).isEqualTo(HttpStatus.UNSUPPORTED_MEDIA_TYPE);
    }

    @Test
    public void createImageShallFailedWhenBodyIsEmpty() throws Exception {
        objectRepository.save(object1);
        ResponseEntity<IndustrialImageCreated> responseEntity = postImage(object1.getName(), new byte[0], MediaType.IMAGE_PNG);

        HttpStatus statusCode = responseEntity.getStatusCode();
        assertThat(statusCode).isEqualTo(HttpStatus.BAD_REQUEST);
    }


    @Test
    public void createImageShallSuccess() throws Exception {
        objectRepository.save(object1);

        ResponseEntity<IndustrialImageCreated> responseEntity = postImage(object1.getName(), IMAGE_1, MediaType.IMAGE_PNG);

        assertThatIsImageCreatedResponse(responseEntity, object1.getName());

        IndustrialImageCreated image = responseEntity.getBody();
        assertThatImageInDatababase( image.getImageUuid(), IMAGE_1, MimeTypeUtils.IMAGE_PNG,  object1 );
    }

    private void assertThatIsImageCreatedResponse( ResponseEntity<IndustrialImageCreated> responseEntity , String objectName) {
        HttpStatus statusCode = responseEntity.getStatusCode();
        assertThat(statusCode).isEqualTo(HttpStatus.CREATED);

        MediaType contentType = responseEntity.getHeaders().getContentType();
        assertThat(contentType).isEqualTo(APPLICATION_JSON_UTF8);

        IndustrialImageCreated content = responseEntity.getBody();
        assertThat(content.getImageUuid()).isNotEmpty();
        assertThat(content.getObjectName()).isEqualTo(objectName);
    }

    private void assertThatImageInDatababase(String imageUuid, byte[] imageContent, MimeType type, IndustrialObject object) {
        IndustrialImage image = imageRepository.findOne(UUID.fromString(imageUuid));
        assertThat( image )
                .extracting("contentType", "content")
                .contains(type.getType(), imageContent);

        assertThat(image.getIndustrialObject())
                .extracting("name")
                .contains(object.getName());

    }

    @Test
    public void getAllImageMetadataShallReturnEmptyCollectionWhenNoImages() {
        objectRepository.save(object1);

        ResponseEntity<Collection<IndustrialImageMetadata>> response = getImageMetadata(object1.getName());

        assertThat(response.getStatusCode())
                .isEqualTo(HttpStatus.OK);
        assertThat(response.getHeaders().getContentType())
                .isEqualTo(MediaType.APPLICATION_JSON_UTF8);
        assertThat( response.getBody()).isEmpty();
    }

    private ResponseEntity<Collection<IndustrialImageMetadata>> getImageMetadata(String objectId) {
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept( Collections.singletonList(MediaType.APPLICATION_JSON_UTF8) );

        //Collection<IndustrialImageMetadata>

        HttpEntity<?> entity = new HttpEntity<>(headers);
        ParameterizedTypeReference<Collection<IndustrialImageMetadata>> ref= new ParameterizedTypeReference<Collection<IndustrialImageMetadata>>(){};
        Map<String,?> urlVariables = new HashMap<>();
        return restTemplate.exchange(
                "/objects/"+objectId+"/images",
                HttpMethod.GET,
                entity,
                ref,
                urlVariables);

    }

    @Test
    public void getAllImageMetadataShallReturnAllImageMetadata() {
        objectRepository.save(object1);
        UUID img1UUID = createImage(object1, MediaType.IMAGE_JPEG, IMAGE_1);
        UUID img2UUID = createImage(object1, MediaType.IMAGE_PNG, IMAGE_2);


        ResponseEntity<Collection<IndustrialImageMetadata>> response = getImageMetadata(object1.getName());

        assertThat(response.getStatusCode())
                .isEqualTo(HttpStatus.OK);
        assertThat(response.getHeaders().getContentType())
                .isEqualTo(MediaType.APPLICATION_JSON_UTF8);
        assertThat( response.getBody())
                .extracting("uuid", "contentType")
                .contains(
                        new Tuple(img1UUID.toString(), MediaType.IMAGE_JPEG_VALUE),
                        new Tuple(img2UUID.toString(), MediaType.IMAGE_PNG_VALUE)
                );
    }

    private UUID createImage(IndustrialObject object, MediaType mediaType, byte[] content) {
        IndustrialImage image = new IndustrialImage();
        image.setIndustrialObject(object);
        image.setContentType(mediaType.toString());
        image.setContent(content);
        IndustrialImage createdImage = imageRepository.save(image);
        return createdImage.getUuid();
    }
}