# Local RGP Development environment

## Steps to debug rgp-services-mono-repo can be found here
- https://gitlab.com/playsafe/roxor/rgp-services-local-mono-repo/-/blob/hein-work-in-progress/README.md

## Steps to configure end to end environment

- Start Local RGP Complete
  - Prerequisites
    - Have docker installed

```bash

git clone https://gitlab.com/playsafe/roxor/rgp-local.git
cd rgp-local
git checkout feature/roxorgaminglocallithium-e2e
docker load < backup/rgp-services-container.tar
docker tag 888176da7d12 gamesys-roxor-gp-docker.jfrog.io/rgp/rgp-services-container:2.1-SNAPSHOT
./scripts/start_slot.sh
## OR ./scripts/start_slot_complete.sh

```

- Start Local Mega Vegas
  - Prerequisites
    - install NVM
    - install node 12 with NVM

```bash
npm install -g @ionic/cli

git clone --recursive https://gitlab.com/playsafe/gogame/ionic-megavegas-app.git
cd ionic-megavegas-app
git checkout LIVESCORE-943-Friezas-Test-Wrapper
npm install

# NB -> create /ionic-megavegas-app/src/environments/environment.ts with the contents below

# start megavegas using
ng run app:serve --host=localhost --port=59011
```

```typescript

// ionic-megavegas-app/src/environments/environment.ts file configure domain created above if neccessary

export const environment = {
  id: 100,
  version_product: 'cash',
  environment: 'dev.real',
  production: false,
  name: 'MegaVegas Dev Real',
  wordpresscasino: 'https://lobby-cash.dev.playsafesa.com',
  wordpressmarketing: 'https://www.megavegas.com',
  // gateway: 'https://gateway.dev.playsafesa.com',
  gateway: 'http://localhost:9000',
  domain: 'livescore_nigeria',
  // domain: 'ggmvrlcad',
  iap: true,
  firebase_sender_id: '862870297288',
  kochava_app_guid_android: 'kogg-mv-dev-casino-android-cte',
  kochava_app_guid_ios: '',
  kochava_app_guid_web: 'kogg-mv-dev-casino-web-1e7u',
  one_signal_app_id: '3727bd82-9cf5-4c60-be2c-cc9554ff7d72',
  privacy_url: 'https://lobby-cash.dev.playsafesa.com/privacy-policy/',
  //   iovation_url: 'https://gateway.lithium-ci-develop.cloud.playsafesa.com/service-access-provider-iovation/blackbox.js',
  //   tslint:disable-next-line: max-line-length
  //   iovation_url: 'https://mpsnare.iesnare.com/general5/wdp.js?loaderVer=5.1.0&compat=false&tp=true&tp_split=false&fp_static=false&fp_dyn=true&flash=false',
  //  The below should be relative URL but I'm leaving it like this for now since local deploys will fail
  // iovation_url: '/assets/iojs/IoJsLoader.js',
  // tslint:disable-next-line:max-line-length
  iovation_url: 'https://ionic-dev-cash.dev.playsafesa.com/iojs-test/general5/wdp.js?loaderVer=5.1.0&compat=false&tp=true&tp_split=false&fp_static=false&fp_dyn=true&flash=false',
  use_biometrics: false,
  flow: 'normal',
  social_fb: false,
  social_guest: true,
  lobby : '/wp',
  bonus_game_campaign_id: 4 ,
  matomo_traking: false ,
  matomo_url: 'https://analytics.dev.playsafesa.com/',
  matomo_app_id: 1 ,
  expiry_time_out: 2000,
  privacy_policy_template_name : 'privacy_policy',
  terms_and_conditions_template_name : 'terms_and_conditions',
  bonusGames: [ 'gogame_land_a_leprechaun', 'gogame_neverending_postcards', 'gogame_kristal_kittens'],
  logging: { level: 'debug', logger: { onesignal: 'debug' } },
  tutorialGameName: 'Pearl of the Dancing Dragon' ,
  show_cataboom_game: false,
  show_delorean_screen: false,
  bonusCode: 'NOTIFICATIONS',
  pixel_tracking_url: 'https://tracking.megavegas.com/Processing/Pixels/Registration.ashx',
  reverse_ftuj_date_ordering: false
};
```

----

## Configure Local Lithium Game

- create player domain (livescore_nigeria)
  - player domain settings (**NB configure default country (United Kingdom), define currency (GBP)**)
  - register a player on the player domain (**NB MegaVegas doesn't like numbers in the first name and last name**)

### POSTMAN 

```http request
POST {{GATEWAY_ADDR}}/service-user/players/livescore_nigeria_local/register 
Content-Type: application/json
Authorization: Basic acme/acmesecret

{
    "username": "player01",
    "firstName": "playerOne",
    "lastName": "SmytheOne",
    "email": "user01@tmp.playsafesa.com",
    "password": "Password123",
    "dobYear": "1990",
    "dobMonth": "12",
    "dobDay": "07",
    "referrerGuid": "",
    "bonusCode":""
}
```

**NB: Inside UNA on the created player adjust the player balance**
 

### Enable casino provider + configure settings (Domain -> Provider -> Casino tab)
- Domain -> Provider -> Casino tab -> Modify

```properties
Name=roxorgaming
Status=Enabled

launchURL=http://localhost/static-assets/gs-wrapper/index.html
sound=true
homeButton=true
homePos=right
balancePos=left
chatHost=
chatContext=
sessionReminderInterval=
sessionElapsed=
homePageURL=www.google.com
depositPageURL=www.google.com
lobbyPageURL=www.google.com
transactionURL=www.google.com
logoutURL=www.google.com
loginPageURL=www.google.com
gameApiUrl=www.google.com
website=roxorgaminglocal
ipWhiteList=
```

## Postman -> Create Game

### Do System Authentication
- (bodytype form-data)

```http request
POST {{gateway}}/server-oauth2/oauth/token
Authorization: Basic system/VERYBLOODYgoodpasswordFORuseInSystemAUTHENTICATION99

grant_type:client_credentials
```


### Add Game

```http request
POST {{gateway}}/service-games/games/add?providerGuid=service-casino-provider-roxor&providerGameId=play-banghai&gameName=Banghai&description=Slot Machine Game
Authorization: Bearer {{SYSTEMTOKEN}}
```

## UNA
 
- Enable Banghai for created domain (livescore_nigeria) "visible" and "enabled"
- UNA -> Games -> Banghai -> Edit -> Summary
- Under Labels -> Add Label
- Add "os" "desktop" Label
- Tick "Enabled" and "Visible"
- Click Save


## Start Game from Mega Vegas 

- Go to http://localhost:59011 in the browser
- Log into domain with registered player
- Choose the configured game to play

