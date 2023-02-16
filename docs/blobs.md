# Working with Blobs in Microsoft Azure Storage

[[  Overview ]](#overview)  [[ Operation details ]](#operation-details)  [[  Sample configuration  ]](#sample-configuration)

### Overview 

The following operations allow you to work with Blobs. Click an operation name to learn how to use it.
For a sample proxy service that illustrates how to work with Blobs, see [Sample configuration](#sample-configuration).

| Operation        | Description |
| ------------- |-------------|
| [uploadBlob](#uploads-a-blob-file-into-the-storage)    | Uploads a Blob file into the storage. |
| [deleteBlob](#deletes-a-blob-file-from-the-storage)      | Deletes a Blob file from the storage.     |
| [listBlobs](#retrieves-information-about-all-blobs-in-a-container)    | Retrieves information about all Blobs in a container. |

### Operation details

This section provides more information on each of the operations.

#### Uploads a blob file into the storage

The uploadBlob operation uploads a Blob file into the storage. 

##### To upload a file as a blob

**uploadBlob**
```xml
<msazurestorage.uploadBlob>
    <containerName>{$ctx:containerName}</containerName>
    <fileName>{$ctx:fileName}</fileName>
    <filePath>{$ctx:filePath}</filePath>
    <blobContentType>{$ctx:fileContentType}</blobContentType> 
    <metadata>{$ctx:metadata}</metadata>
</msazurestorage.uploadBlob>
```

**Properties**
* containerName: The name of the container.
* fileName: The name of the file.
* filePath: The path to a local file to be uploaded.
* blobContentType: The Content-type of the file to be uploaded
* metadata: A JSON formatted metadata string (optional) (ex: `{"meta1": "val1", "meta2": 2, "meta3": "val3"}`)

**Sample request**

Given below is a sample request for the uploadBlob operation.

```json
{
  "accountName": "test",
  "accountKey": "=gCetnaQlvsXQG4PnlXxxxxXXXXsW37DsDKw5rnCg==",
  "containerName": "sales",
  "fileName": "sample.txt",
  "filePath": "/home/user/Pictures/a.txt",
  "metadata": "{'meta1': 'val1', 'meta2': 'val2'}"
}
```

##### To upload a text content as a blob

**uploadBlob**
```xml
<msazurestorage.uploadBlob>
    <containerName>{$ctx:containerName}</containerName>
    <fileName>{$ctx:fileName}</fileName>
    <textContent>{$ctx:payload}</textContent>
    <blobContentType>{$ctx:fileContentType}</blobContentType> 
    <metadata>{$ctx:metadata}</metadata>
</msazurestorage.uploadBlob>
```

**Properties**
* containerName: The name of the container.
* fileName: The name of the file.
* textContent: The text content to be upload as a blob.
* blobContentType: The Content-type of the file to be uploaded
* metadata: A JSON formatted metadata string (optional) (ex: `{"meta1": "val1", "meta2": 2, "meta3": "val3"}`)

**Sample request**

Given below is a sample request for the uploadBlob operation.

```json
{
  "accountName": "test",
  "accountKey": "=gCetnaQlvsXQG4PnlXxxxxXXXXsW37DsDKw5rnCg==",
  "containerName": "sales",
  "fileName": "sample.txt",
  "textContent": "Hello, world",
  "blobContentType": "text/plain",
  "metadata": "{'meta1': 'val1', 'meta2': 'val2'}"
}
```

**Related Microsoft Azure Storage documentation**

[https://docs.microsoft.com/en-us/azure/storage/blobs/storage-java-how-to-use-blob-storage](https://docs.microsoft.com/en-us/azure/storage/blobs/storage-java-how-to-use-blob-storage)

#### Deletes a blob file from the storage

The deleteBlob operation deletes a Blob file from the storage.

**deleteBlob**
```xml
<msazurestorage.deleteBlob>
    <containerName>{$ctx:containerName}</containerName>
    <fileName>{$ctx:fileName}</fileName>
</msazurestorage.deleteBlob>
```

**Properties**
* containerName: The name of the container.
* fileName: The name of the file.

**Sample request**

Given below is a sample request for the deleteBlob operation.

```json
{
  "accountName": "test",
  "accountKey": "=gCetnaQlvsXQG4PnlXxxxxXXXXsW37DsDKw5rnCg==",
  "containerName": "sales",
  "fileName": "sample.txt"
}
```
**Related Microsoft Azure Storage documentation**

[https://docs.microsoft.com/en-us/azure/storage/blobs/storage-java-how-to-use-blob-storage](https://docs.microsoft.com/en-us/azure/storage/blobs/storage-java-how-to-use-blob-storage)

#### Retrieves information about all Blobs in a container

The listBlobs operation retrieves information about all Blobs in a container.

**listBlobs**
```xml
<msazurestorage.listBlobs>
    <containerName>{$ctx:containerName}</containerName>
</msazurestorage.listBlobs>
```

**Properties**
* containerName: The name of the container.

**Sample request**

Given below is a sample request that can be handled by the listBlobs operation.

```json
{
  "accountName": "test",
  "accountKey": "=gCetnaQlvsXQG4PnlXxxxxXXXXsW37DsDKw5rnCg==",
  "containerName": "sales"
}
```
**Related Microsoft Azure Storage documentation**

[https://docs.microsoft.com/en-us/azure/storage/blobs/storage-java-how-to-use-blob-storage](https://docs.microsoft.com/en-us/azure/storage/blobs/storage-java-how-to-use-blob-storage)


#### Sample configuration

Given below is a sample proxy service that illustrates how to connect to Microsoft Azure Storage with the init operation and use the listBlobs operation. You can find the sample request for this proxy in the listBlobs sample request.

**Sample Proxy**
```xml
<?xml version="1.0" encoding="UTF-8"?>
    <proxy xmlns="http://ws.apache.org/ns/synapse" name="listBlobs" transports="https,http" statistics="disable" trace="disable" startOnLoad="true">
     <target>
      <inSequence onError="faultHandlerSeq">
      <property name="accountName" expression="json-eval($.accountName)"/>
      <property name="accountKey" expression="json-eval($.accountKey)"/>
      <property name="containerName" expression="json-eval($.containerName)"/>
      <msazurestorage.init>
        <accountName>{$ctx:accountName}</accountName>
        <accountKey>{$ctx:accountKey}</accountKey>
      </msazurestorage.init>
      <msazurestorage.listBlobs>
        <containerName>{$ctx:containerName}</containerName>
      </msazurestorage.listBlobs>
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
