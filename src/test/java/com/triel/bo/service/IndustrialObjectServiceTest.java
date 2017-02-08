package com.triel.bo.service;

import com.triel.bo.ObjectStoreApplication;
import com.triel.bo.domain.IndustrialObject;
import com.triel.bo.persistence.IndustrialObjectRepository;
import org.assertj.core.groups.Tuple;
import org.junit.*;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;

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


    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private IndustrialObjectRepository repository;

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
        repository.deleteAll();

    }

    @Test
    public void listAllWithFilledContentShallReturnAllContent() throws Exception {
        repository.save( object1 );
        repository.save( object2 );


        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));

        HttpEntity<?> entity = new HttpEntity<>(headers);
        ParameterizedTypeReference ref= new ParameterizedTypeReference<List<IndustrialObjectCreateRead>>(){};
        Map<String,?> urlVariables = new HashMap<>();
        ResponseEntity<List<IndustrialObjectCreateRead>> responseEntity = restTemplate.exchange(
                "/objects",
                HttpMethod.GET,
                entity,
                ref,
                urlVariables);


        List<IndustrialObjectCreateRead> objects = responseEntity.getBody();
        MediaType contentType = responseEntity.getHeaders().getContentType();
        HttpStatus statusCode = responseEntity.getStatusCode();


        assertThat(statusCode).isEqualTo(HttpStatus.OK);
        assertThat(contentType).isEqualTo(MediaType.APPLICATION_JSON_UTF8);
        assertThat(objects).hasSize(2);
        assertThat(objects)
                .extracting("name", "description", "state")
                .contains(
                        object1Tuple,
                        object2Tuple
                        );
    }

    @Test
    public void listAllWithEmptyRepositoryShallReturnEmptyList() throws Exception {
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));

        HttpEntity<?> entity = new HttpEntity<>(headers);
        ParameterizedTypeReference ref= new ParameterizedTypeReference<List<IndustrialObjectCreateRead>>(){};
        Map<String,?> urlVariables = new HashMap<>();
        ResponseEntity<List<IndustrialObjectCreateRead>> responseEntity = restTemplate.exchange(
                "/objects", HttpMethod.GET, entity, ref, urlVariables);


        List<IndustrialObjectCreateRead> objects = responseEntity.getBody();
        MediaType contentType = responseEntity.getHeaders().getContentType();
        HttpStatus statusCode = responseEntity.getStatusCode();


        assertThat(statusCode).isEqualTo(HttpStatus.OK);
        assertThat(contentType).isEqualTo(MediaType.APPLICATION_JSON_UTF8);
        assertThat(objects).isEmpty();
    }


    @Test
    public void getByIdShallReturnNotFoundWhenIdNotExists() throws Exception {
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));

        HttpEntity<?> entity = new HttpEntity<>(headers);
        ParameterizedTypeReference ref= new ParameterizedTypeReference<IndustrialObjectCreateRead>(){};
        Map<String,?> urlVariables = new HashMap<>();
        ResponseEntity<IndustrialObject> responseEntity = restTemplate.exchange(
                "/objects/notExists",
                HttpMethod.GET,
                entity,
                ref,
                urlVariables);


        HttpStatus statusCode = responseEntity.getStatusCode();
        assertThat(statusCode).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    public void getByIdShallReturnElementWhenIdExists() throws Exception {
        repository.save(object1);

        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));

        HttpEntity<?> entity = new HttpEntity<>(headers);
        ParameterizedTypeReference ref= new ParameterizedTypeReference<IndustrialObjectCreateRead>(){};
        Map<String,?> urlVariables = new HashMap<>();
        ResponseEntity<IndustrialObjectCreateRead> responseEntity = restTemplate.exchange(
                "/objects/"+object1.getName(),
                HttpMethod.GET,
                entity,
                ref,
                urlVariables);


        IndustrialObjectCreateRead returnedObject = responseEntity.getBody();
        MediaType contentType = responseEntity.getHeaders().getContentType();
        HttpStatus statusCode = responseEntity.getStatusCode();


        assertThat(statusCode).isEqualTo(HttpStatus.FOUND);
        assertThat(contentType).isEqualTo(MediaType.APPLICATION_JSON_UTF8);
        assertThat(returnedObject)
                .extracting("name", "description", "state")
                .contains(NAME_1,DESCRIPTION_1,STATE_1);
    }

    @Test
    public void createIndustrialObject() throws Exception {
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
        headers.setContentType(MediaType.APPLICATION_JSON_UTF8);

        HttpEntity<?> entity = new HttpEntity<>(new IndustrialObjectCreateRead(NAME_1,DESCRIPTION_1,STATE_1), headers);
        Map<String,?> urlVariables = new HashMap<>();
        ResponseEntity<IndustrialObjectCreateRead> responseEntity = restTemplate.exchange(
                "/objects",
                HttpMethod.POST,
                entity,
                IndustrialObjectCreateRead.class,
                urlVariables);


        IndustrialObjectCreateRead object = responseEntity.getBody();
        MediaType contentType = responseEntity.getHeaders().getContentType();
        HttpStatus statusCode = responseEntity.getStatusCode();


        assertThat(statusCode).isEqualTo(HttpStatus.CREATED);
        assertThat(contentType).isEqualTo(MediaType.APPLICATION_JSON_UTF8);
        assertThat(object)
                .extracting("name", "description", "state")
                .containsExactly(NAME_1, DESCRIPTION_1, STATE_1);
    }

    @Test
    public void replaceIndustrialObjectShallReturnNotFoundWhenBadNameSpecified() throws Exception {
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));

        HttpEntity<?> entity = new HttpEntity<>(
                new IndustrialObjectUpdate(DESCRIPTION_1_BIS,STATE_1_BIS),
                headers);
        ParameterizedTypeReference ref= new ParameterizedTypeReference<IndustrialObjectCreateRead>(){};
        Map<String,?> urlVariables = new HashMap<>();
        ResponseEntity<IndustrialObjectCreateRead> responseEntity = restTemplate.exchange(
                "/objects/"+NAME_2,
                HttpMethod.PUT,
                entity,
                ref,
                urlVariables);


        HttpStatus statusCode = responseEntity.getStatusCode();
        assertThat(statusCode).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    public void replaceIndustrialObjectShallReturnNotFoundWhenNoBody() throws Exception {
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));

        HttpEntity<?> entity = new HttpEntity<>(
                new IndustrialObjectUpdate(DESCRIPTION_1_BIS,STATE_1_BIS),
                headers);
        ParameterizedTypeReference ref= new ParameterizedTypeReference<IndustrialObjectCreateRead>(){};
        Map<String,?> urlVariables = new HashMap<>();
        ResponseEntity<IndustrialObjectCreateRead> responseEntity = restTemplate.exchange(
                "/objects/"+NAME_1,
                HttpMethod.PUT,
                entity,
                ref,
                urlVariables);


        HttpStatus statusCode = responseEntity.getStatusCode();
        assertThat(statusCode).isEqualTo(HttpStatus.NOT_FOUND);
    }


    @Test
    public void replaceIndustrialObjectShallReturnUpdatedObject() throws Exception {
        repository.save(object1);

        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
        headers.setContentType(MediaType.APPLICATION_JSON_UTF8);

        HttpEntity<?> entity = new HttpEntity<>(
                new IndustrialObjectUpdate(DESCRIPTION_1_BIS,STATE_1_BIS),
                headers);
        Map<String,?> urlVariables = new HashMap<>();
        ResponseEntity<IndustrialObjectCreateRead> responseEntity = restTemplate.exchange(
                "/objects/"+NAME_1,
                HttpMethod.PUT,
                entity,
                IndustrialObjectCreateRead.class,
                urlVariables);


        IndustrialObjectCreateRead object = responseEntity.getBody();
        MediaType contentType = responseEntity.getHeaders().getContentType();
        HttpStatus statusCode = responseEntity.getStatusCode();


        assertThat(statusCode).isEqualTo(HttpStatus.OK);
        assertThat(contentType).isEqualTo(MediaType.APPLICATION_JSON_UTF8);
        assertThat(object)
                .extracting("name", "description", "state")
                .containsExactly(NAME_1, DESCRIPTION_1_BIS, STATE_1_BIS);
    }

    @Test
    public void deleteIndustrialObjectByNameShallReturnNotFoundWhenBadId() throws Exception {
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));

        HttpEntity<?> entity = new HttpEntity<>(headers);
        ParameterizedTypeReference ref= new ParameterizedTypeReference<IndustrialObjectCreateRead>(){};
        Map<String,?> urlVariables = new HashMap<>();
        ResponseEntity<IndustrialObjectCreateRead> responseEntity = restTemplate.exchange(
                "/objects/"+NAME_1,
                HttpMethod.DELETE,
                entity,
                ref,
                urlVariables);


        HttpStatus statusCode = responseEntity.getStatusCode();
        assertThat(statusCode).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    public void deleteIndustrialObjectByName() throws Exception {
        repository.save(object1);

        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
        headers.setContentType(MediaType.APPLICATION_JSON_UTF8);

        HttpEntity<?> entity = new HttpEntity<>(headers);
        Map<String,?> urlVariables = new HashMap<>();
        ResponseEntity<IndustrialObjectCreateRead> responseEntity = restTemplate.exchange(
                "/objects/"+NAME_1,
                HttpMethod.DELETE,
                entity,
                IndustrialObjectCreateRead.class,
                urlVariables);


        IndustrialObjectCreateRead object = responseEntity.getBody();
        MediaType contentType = responseEntity.getHeaders().getContentType();
        HttpStatus statusCode = responseEntity.getStatusCode();


        assertThat(statusCode).isEqualTo(HttpStatus.OK);
        assertThat(contentType).isEqualTo(MediaType.APPLICATION_JSON_UTF8);
        assertThat(object)
                .extracting("name", "description", "state")
                .containsExactly(NAME_1, DESCRIPTION_1, STATE_1);

        assertThat(repository.findAll())
            .isEmpty();
    }

}