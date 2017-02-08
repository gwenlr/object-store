# object-store

## Configuration
The server is available on localhost:8080.
The servlet server used is Jetty 

## Structures

All structures are exchanged in JSON format.

###IndustrialObjectCreateRead
* name: String representing the name of the object, shall not be null nor empty. It is unique in the store.
* description: String describing the object. Cannot be null;
* state: String representing the state of the object, shall not be null.

###IndustrialObjectUpdate
* description: String describing the object. Cannot be null;
* state: String representing the state of the object, shall not be null.


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
* OK / IndustrialObjectCreateRead
* NO_CONTENT

### Replace an object 
Request: PUT /objects/{name}
Returns: 
* OK / IndustrialObjectCreateRead
* NO_CONTENT
* NOT_FOUND

### Delete an object
Request: DELETE /objects/{name}
Returns: 
* OK / IndustrialObjectCreateRead
* NOT_FOUND





