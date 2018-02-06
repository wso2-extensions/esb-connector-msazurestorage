# Configuring Microsoft Azure Storage Operations

[[Initializing the Connector]](#initializing-the-connector)  [[Obtaining user credentials]](#obtaining-user-credentials)

> NOTE: To work with the Microsoft Azure Storage connector, you need to have a Microsoft Azure account. If you do not have a Microsoft Azure account, go to [https://azure.microsoft.com/en-in/free/](https://azure.microsoft.com/en-in/free/) and create a Microsoft Azure account.

To use the Microsoft Azure Storage connector, add the <msazurestorage.init> element in your configuration before carrying out any other Microsoft Azure Storage operations. 

Microsoft Azure Storage  uses the access key to securely access the data.
## Obtaining the access credentials

* **Follow the steps below to obtain the access credentials from Microsoft Azure Storage account:**

    1. Go to https://azure.microsoft.com/en-in/free/, and sign up to create a Microsoft Azure account. 
    2. Go to the dashboard and click **Storage accounts** then click **add** and fill the required details to create new storage account.(For more details - [https://docs.microsoft.com/en-us/azure/storage/common/storage-create-storage-account](https://docs.microsoft.com/en-us/azure/storage/common/storage-create-storage-account))
    3. Go to your storage account and click **Access keys** under **Settings** and obtain the access key.

In order to use the Microsoft Azure Storage connector, you need to download the azure-storage-6.1.0.jar from the [https://mvnrepository.com/artifact/com.microsoft.azure/azure-storage/6.1.0](https://mvnrepository.com/artifact/com.microsoft.azure/azure-storage/6.1.0) and copy the jar to the <EI_HOME>/lib directory.

## Initializing the Connector
Specify the init method as follows:

**init**
```xml
<msazurestorage.init>
    <accountName>{$ctx:accountName}</accountName>
    <accountKey>{$ctx:accountKey}</accountKey>
</msazurestorage.init>
```
**Properties** 
* accountName:  The name of the azure storage account. 
* accountKey:  The access key for the storage account.

 

Now that you have connected to Microsoft Azure Storage, use the information in the following topics to perform various operations with the connector:

[Working with Blobs](blobs.md)

[Working with Containers](containers.md)