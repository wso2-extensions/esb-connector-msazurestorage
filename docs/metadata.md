# Working with Metadata in Microsoft Azure Storage

[[  Overview ]](#overview)  [[ Operation details ]](#operation-details)  [[  Sample configuration  ]](#sample-configuration)

### Overview 

The following operations allow you to work with Blob metadata. Click an operation name to learn how to use it.
For a sample proxy service that illustrates how to work with Blob metadata, see [Sample configuration](#sample-configuration).

| Operation        | Description |
| ------------- |-------------|
| [uploadMetadata](#uploads-metadata-into-the-storage)    | Uploads metadata of a specific blob into the storage. |
| [listMetadata](#retrieves-all-metadata-of-a-blob)    | Retrieves all the metadata of a specific blob. |

### Operation details

This section provides more information on each of the operations.

#### Uploads metadata into the storage

**uploadMetadata**
```xml
<msazurestorage.uploadMetadata>
    <containerName>{$ctx:containerName}</containerName>
    <fileName>{$ctx:fileName}</fileName>
    <metadata>{$ctx:payload}</metadata>
</msazurestorage.uploadMetadata>
```

**Properties**
* containerName: The name of the container.
* fileName: The name of the file.
* metadata: A JSON formatted metadata (ex: `{"metadata": {"meta1": "val1", "meta2": 2, "meta3": "val3"}}`)

**Sample request**

Given below is a sample request for the uploadBlob operation.

```json
{
  "accountName": "test",
  "accountKey": "=gCetnaQlvsXQG4PnlXxxxxXXXXsW37DsDKw5rnCg==",
  "containerName": "sales",
  "fileName": "sample.txt",
  "metadata": "{'metadata': {'meta1': 'val1', 'meta2': 'val2'}}"
}
```

#### Retrieves all metadata of a blob

**uploadMetadata**
```xml
<msazurestorage.listMetadata>
    <containerName>{$ctx:containerName}</containerName>
    <fileName>{$ctx:fileName}</fileName>
</msazurestorage.listMetadata>
```

**Properties**
* containerName: The name of the container.
* fileName: The name of the file.

**Sample request**

Given below is a sample request for the uploadBlob operation.

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
      <property name="fileName" expression="json-eval($.fileName)"/>
      <msazurestorage.init>
        <accountName>{$ctx:accountName}</accountName>
        <accountKey>{$ctx:accountKey}</accountKey>
      </msazurestorage.init>
      <msazurestorage.listMetadata>
        <containerName>{$ctx:containerName}</containerName>
        <fileName>{$ctx:fileName}</fileName>
      </msazurestorage.listMetadata>
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