import requests as req
import jwt
import datetime
import base64
import json
ts = int(datetime.datetime.now().timestamp())
exp = ts + 1600
json_ = {
  "iss": "casino-recommendation-gw-sa@gamesys-eu-dev-sbtech-data.iam.gserviceaccount.com",
  "scope": "https://www.googleapis.com/auth/cloud-platform",
  "aud": "https://oauth2.googleapis.com/token",
  "exp": exp,
  "iat": ts
}
with open('casino-rec-gw-sa.json', 'r') as fh:
    sa_json = json.load(fh)
    priv_key = sa_json['private_key']
signature = jwt.encode(json_, priv_key, 'RS256')
url = 'https://oauth2.googleapis.com/token'
req_params = {
    'grant_type': 'urn:ietf:params:oauth:grant-type:jwt-bearer', 
    'assertion': signature
}
resp = req.post(url, params=req_params)
acc_token = resp.json()['access_token']
print(acc_token)