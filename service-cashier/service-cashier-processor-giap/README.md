###### Note: As of December 1, 2019, the Google Play Developer API is available only for version 3 and higher. If you're using a lower version of the API, you must migrate to version 3 by this date. For more information on migrating to version 3, see Changes to the Google Play Developer API.

### Initial Setup:
Taken from : https://stackoverflow.com/questions/57697597/samples-for-the-androidpublisher-v3-google-api-client-library-for-java

1. Enable Google Developer Play Api library from Google Cloud Console
    * https://console.cloud.google.com/apis/library/androidpublisher.googleapis.com
2. In credentials create service account and save json file.
    * *This file contains all the details required on the processor properties page in ui-network-admin*
3. In Google Developers Console. 
    * In Settings >> Developer Account >> API Access link your app.
    * https://play.google.com/apps/publish
    
    
#### Other important links:
  * https://developers.google.com/android-publisher
  * https://github.com/googleapis/google-api-java-client-services/tree/master/clients/google-api-services-androidpublisher/v3
  * https://console.developers.google.com/iam-admin/serviceaccounts/details/112271768066507285337?orgonly=true&project=gg-dev-social&supportedpurview=project
  * https://console.developers.google.com/apis/credentials?project=api-7207333690159999854-969802
  
##### Product Setup:
  * https://play.google.com/apps/publish/?account=7207333690159999854#ManagedProductsSetupPlace:p=com.playsafesa.megavegas.app.social&appid=4973739278966743219
  * Google Play Console > Select App ( MegaVegas Social Demo ) > Store Presence > In-app Products > Managed products
    * this list must be reflected in ui-network-admin on the products page. 