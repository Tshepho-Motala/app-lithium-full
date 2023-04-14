# Document service
## Synopsis
A microservice that stores binary documents per user.

## Installation
This module is dependent on the **service-domain**  module and their dependencies.

## Configuration
The security configuration is located in the *GamesModuleInfo.java* class.

## API Reference
TODO

## Document upload notification 
There is a possibility to notify DWH when new document uploaded via _`service-document/frontend/document/upload`_ or via hello soda.
To enable notification add **`uploaded_document_mail_dwh`** property with email to domain settings.
For notification used **`uploaded.document.dwh`** template.      

## Domain setting
To store uploaded documents via kyc providers (ex. hellosoda) for both locations there is a domain setting called **`UPLOAD_DOCUMENT_VERSION`**.
Possible active values there are: **`v1`**, **`v2`** or **`v1, v2`**. Default is **`v1, v2`**.
Documents for **`v1`** can be viewed at **`Documents(deprecated)`** LBO user tab 
and **`v2`** at **`Documents`** respectively. 
All other values will be considered as directive not to store any document.

## Document type resolving
It was added possibility to setup mapping for resolving document types when documents uploading from kyc-providers (i.e. onfido, hello-soda)
To setup these mappings go to "**Brand Configuration**" > "**Document Types**" > "**Edit**". At the "**Edit Document Type**" dialog there is a field "Mapping name" where you should to add/remove the type names which provided by kyc-providers.
In case when there is no related mapping documents will use the type **Other**