# object-store

## Configuration
The server is available on localhost:8080.
The servlet server used is Jetty 

## Structures

All structures are exchanged in JSON format.

### IndustrialObjectCreateRead
* name: String representing the name of the object, shall not be null nor empty. It is unique in the store.
* description: String describing the object. Cannot be null;
* state: String representing the state of the object, shall not be null.

### IndustrialObjectUpdate
* description: String describing the object. Cannot be null;
* state: String representing the state of the object, shall not be null.

### IndustrialImageCreated
* objectName: String containing the name of the object;
* imageUuid: String containing the UUID of the created image;

### IndustrialImageMetadata
* uuid: String containing the UUID of the created image;
* contentType: String containing the MediaType of the image



##Operations

### List all objects
Request: GET /objects
Returns: 
* OK / List of IndustrialObjectCreateRead

### Get an object from its name
Request: GET /objects/{name}
Returns: 
* OK / IndustrialObjectCreateRead
* NOT_FOUND

### Create a new object 
Request: POST /objects
Returns: 
* CREATED / IndustrialObjectCreateRead
* NO_CONTENT

### Replace an object 
Request: PUT /objects/{name}
Returns: 
* OK / IndustrialObjectCreateRead
* NO_CONTENT
* NOT_FOUND

### Delete an object
Request: DELETE /objects/{name}
Headers:
    Content-Type: the type of the image
Returns: 
* OK / IndustrialObjectCreateRead
* NOT_FOUND

### Add an image to an object
Request POST /objects/{name}/images
Returns: 
* CREATED / IndustrialImageCreated
* NOT_FOUND
* UNSUPPORTED_MEDIA_TYPE

### Get all image metadata of an object
Request GET /objects/{name}/images
Returns: 
* OK / List of IndustrialImageMetadata

### Get an image
Request GET /images/{uuid}
Returns:
* OK / content-type / byte[] 
* NOT_FOUND


### Delete an image
Request DELETE /images/{uuid}
* OK 
* NOT_FOUND
