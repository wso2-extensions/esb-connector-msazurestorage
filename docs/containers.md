# Working with Containers in Microsoft Azure Storage

[[  Overview ]](#overview)  [[ Operation details ]](#operation-details)  [[  Sample configuration  ]](#sample-configuration)

### Overview 

The following operations allow you to work with containers. Click an operation name to see details on how to use it.
For a sample proxy service that illustrates how to work with containers, see [Sample configuration](#sample-configuration).

| Operation        | Description |
| ------------- |-------------|
| [createContainer](#creates-a-container-in-the-storage)    | Creates a container in the storage. |
| [deletecontainer](#deletes-a-container-from-the-storage)      | Deletes a container from the storage.     |
| [listContainers](#retrieves-information-about-all-containers)    | Retrieves information about all containers. |

### Operation details

This section gives information about each of the operations.

#### Create a container in the storage

The createContainer operation creates a container in the storage.

**createContainer**
```xml
<msazurestorage.createContainer>
    <containerName>{$ctx:containerName}</containerName>
</msazurestorage.createContainer>
```

**Properties**
* containerName: The name of the container.

**Sample request**

Given below is a sample request that can be handled by the createContainer operation.

```json
{
  "accountName": "test",
  "accountKey": "=gCetnaQlvsXQG4PnlXxxxxXXXXsW37DsDKw5rnCg==",
  "containerName": "sales"
}
```

**Related Microsoft Azure Storage documentation**

[https://docs.microsoft.com/en-us/azure/storage/containers/storage-java-how-to-use-container-storage](https://docs.microsoft.com/en-us/azure/storage/containers/storage-java-how-to-use-container-storage)

#### Delete a container from the storage

The deleteContainer operation deletes a container from the storage.

**deleteContainer**
```xml
<msazurestorage.deleteContainer>
    <containerName>{$ctx:containerName}</containerName>
</msazurestorage.deleteContainer>
```

**Properties**
* containerName: The name of the container.

**Sample request**

Given below is a sample request that can be handled by the deleteContainer operation.

```json
{
  "accountName": "test",
  "accountKey": "=gCetnaQlvsXQG4PnlXxxxxXXXXsW37DsDKw5rnCg==",
  "containerName": "sales"
}
```
**Related Microsoft Azure Storage documentation**

[https://docs.microsoft.com/en-us/azure/storage/containers/storage-java-how-to-use-container-storage](https://docs.microsoft.com/en-us/azure/storage/containers/storage-java-how-to-use-container-storage)

#### Retrieve information about all containers

The listContainers operation retrieves information about all containers in the storage.

**listContainers**
```xml
<msazurestorage.listContainers/>
```

**Sample request**

Given below is a sample request that can be handled by the listContainers operation.

```json
{
  "accountName": "test",
  "accountKey": "=gCetnaQlvsXQG4PnlXxxxxXXXXsW37DsDKw5rnCg=="
}
```
**Related Microsoft Azure Storage documentation**

[https://docs.microsoft.com/en-us/azure/storage/containers/storage-java-how-to-use-container-storage](https://docs.microsoft.com/en-us/azure/storage/containers/storage-java-how-to-use-container-storage)


#### Sample configuration

Given below is a sample proxy service that illustrates how to connect to Microsoft Azure Storage with the init operation and see information about all containers with the listContainers operation. The sample request for this proxy can be found in the listContainers sample request.

**Sample Proxy**
```xml
<?xml version="1.0" encoding="UTF-8"?>
    <proxy xmlns="http://ws.apache.org/ns/synapse" name="listcontainers" transports="https,http" statistics="disable" trace="disable" startOnLoad="true">
     <target>
      <inSequence onError="faultHandlerSeq">
      <property name="accountName" expression="json-eval($.accountName)"/>
      <property name="accountKey" expression="json-eval($.accountKey)"/>
      <msazurestorage.init>
        <accountName>{$ctx:accountName}</accountName>
        <accountKey>{$ctx:accountKey}</accountKey>
      </msazurestorage.init>
      <msazurestorage.listcontainers/>
       <respond/>
     </inSequence>
      <outSequence>
       <log/>
       <send/>
      </outSequence>
     </target>
   <description/>
  </proxy>
```
