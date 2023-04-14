import http from 'k6/http';
import {check} from 'k6';
import encoding from 'k6/encoding';
import {
	ACCOUNT_TRANSACTION_ID, ACCOUNT_TRANSACTION_TYPE_ID,
	CURRENCY_CODE,
	GAME_BACKEND_BASE_URL_LOCAL,
	GAME_ID,
	GAME_ROUND_ID,
	GAME_ROUND_TRANSACTION_ID,
	GATEWAY_SESSION_TOKEN,
	IP_ADDRESS,
	OPERATOR_ACCOUNT_ID,
	PLATFORM_KEY,
	SESSION_KEY
} from "./perftestProperties.js";
import {uuidv4} from "https://jslib.k6.io/k6-utils/1.0.0/index.js";

export function performBalanceRequest() {
	const body = JSON.stringify({
		'PlatformKey': PLATFORM_KEY,
		'Sequence': uuidv4(),
		'Timestamp': `${new Date().toISOString()}`,
		'OperatorAccountID': OPERATOR_ACCOUNT_ID
	});

	const response = http.post(`${GAME_BACKEND_BASE_URL_LOCAL}/v1.0/balance`, body, getCommonParams('balance'));
	verifyResponse(response);
}

export function performRedeemTokenRequest() {
	const body = JSON.stringify({
		'PlatformKey': PLATFORM_KEY,
		'Sequence': uuidv4(),
		'Timestamp': `${new Date().toISOString()}`,
		'SessionToken': SESSION_KEY + "-" + Date.now().toString(16),
		'IPAddress': IP_ADDRESS
	});

	const response = http.post(`${GAME_BACKEND_BASE_URL_LOCAL}/v1.0/session/redeemtoken`, body, getCommonParams('redeemToken'));
	verifyResponse(response);
}

export function performCreateTokenRequest() {
	const body = JSON.stringify({
		'PlatformKey': PLATFORM_KEY,
		'Sequence': uuidv4(),
		'Timestamp': `${new Date().toISOString()}`,
		'OperatorAccountID': OPERATOR_ACCOUNT_ID,
		'GameID': GAME_ID
	});

	const response = http.post(`${GAME_BACKEND_BASE_URL_LOCAL}/v1.0/session/createtoken`, body, getCommonParams('createToken'));
	verifyResponse(response);
}

export function performPlaceBetRequest() {
	const body = JSON.stringify({
		'PlatformKey': PLATFORM_KEY,
		'Sequence': uuidv4(),
		'Timestamp': `${new Date().toISOString()}`,
		'GatewaySessionToken': GATEWAY_SESSION_TOKEN,
		'OperatorAccountID': OPERATOR_ACCOUNT_ID,
		'GameRoundID': GAME_ROUND_ID,
		'GameRoundTransactionID': GAME_ROUND_TRANSACTION_ID,
		'GameID': GAME_ID,
		'CurrencyCode': CURRENCY_CODE,
		'Amount': 1.00,
		'StartRound': true,
		'EndRound': false
	});

	const response = http.post(`${GAME_BACKEND_BASE_URL_LOCAL}/v1.0/gameround/placebet`, body, getCommonParams('placeBet'));
	verifyResponse(response);
}

export function performEndRequest() {
	const body = JSON.stringify({
		'PlatformKey': PLATFORM_KEY,
		'Sequence': uuidv4(),
		'Timestamp': `${new Date().toISOString()}`,
		'GatewaySessionToken': GATEWAY_SESSION_TOKEN,
		'OperatorAccountID': OPERATOR_ACCOUNT_ID,
		'GameRoundID': GAME_ROUND_ID,
		'GameID': GAME_ID,
		'CurrencyCode': CURRENCY_CODE
	});

	const response = http.post(`${GAME_BACKEND_BASE_URL_LOCAL}/v1.0/gameround/end`, body, getCommonParams('end'));
	verifyResponse(response);
}

export function performAwardWinnings() {
	const body = JSON.stringify({
		'PlatformKey': PLATFORM_KEY,
		'Sequence': uuidv4(),
		'Timestamp': `${new Date().toISOString()}`,
		'GatewaySessionToken': GATEWAY_SESSION_TOKEN,
		'OperatorAccountID': OPERATOR_ACCOUNT_ID,
		'GameRoundID': GAME_ROUND_ID,
		'GameRoundTransactionID': GAME_ROUND_TRANSACTION_ID,
		'GameID': GAME_ID,
		'CurrencyCode': CURRENCY_CODE,
		'Amount': 10.00,
		'StartRound': false,
		'EndRound': false
	});

	const response = http.post(`${GAME_BACKEND_BASE_URL_LOCAL}/v1.0/gameround/awardwinnings`, body, getCommonParams('awardWinnings'));
	verifyResponse(response);
}

export function performRollbackBetRequest() {
	const body = JSON.stringify({
		'PlatformKey': PLATFORM_KEY,
		'Sequence': uuidv4(),
		'Timestamp': `${new Date().toISOString()}`,
		'GatewaySessionToken': GATEWAY_SESSION_TOKEN,
		'OperatorAccountID': OPERATOR_ACCOUNT_ID,
		'GameRoundID': GAME_ROUND_ID,
		'OriginalBetGameRoundTransactionID': GAME_ROUND_TRANSACTION_ID,
		'GameRoundTransactionID': GAME_ROUND_TRANSACTION_ID,
		'GameID': GAME_ID,
		'CurrencyCode': CURRENCY_CODE,
		'Amount': 1.00,
		'EndRound': false
	});

	const response = http.post(`${GAME_BACKEND_BASE_URL_LOCAL}/v1.0/gameround/rollbackbet`, body, getCommonParams('rollbackBet'));
	verifyResponse(response);
}

export function performVoidBetRequest() {
	const body = JSON.stringify({
		'PlatformKey': PLATFORM_KEY,
		'Sequence': uuidv4(),
		'Timestamp': `${new Date().toISOString()}`,
		'GatewaySessionToken': GATEWAY_SESSION_TOKEN,
		'OperatorAccountID': OPERATOR_ACCOUNT_ID,
		'GameRoundID': GAME_ROUND_ID,
		'GameRoundTransactionID': GAME_ROUND_TRANSACTION_ID,
		'GameID': GAME_ID,
		'CurrencyCode': CURRENCY_CODE,
		'Amount': 10.00,
		'EndRound': false
	});

	const response = http.post(`${GAME_BACKEND_BASE_URL_LOCAL}/v1.0/gameround/voidbet`, body, getCommonParams('voidBet'));
	verifyResponse(response);
}

export function performAlertWalletCallbackNotificationRequest() {
	const body = JSON.stringify({
		'PlatformKey': PLATFORM_KEY,
		'Sequence': uuidv4(),
		'Timestamp': `${new Date().toISOString()}`,
		'GatewaySessionToken': GATEWAY_SESSION_TOKEN,
		'OperatorAccountID': OPERATOR_ACCOUNT_ID,
		'Source': "OperatorWallet",
		'AlertActionID': "xa-82b3-11eb-8dcd-0242ac130003",
		'OperatorAlertActionReference': "AA1234C",
		'OperatorAlertReference': "AA1234",
		'GamingRegulatorCode': "555",
		'Type': "AlertType",
		'Method': "AlertActionMethod",
		'Data': "AlertActionData"
	});

	const response = http.post(`${GAME_BACKEND_BASE_URL_LOCAL}/v1.0/alertwalletcallbacknotification`, body, getCommonParams('alertWalletCallbackNotification'));
	verifyResponse(response);
}

export function performCreditRequest() {
	const body = JSON.stringify({
		'PlatformKey': PLATFORM_KEY,
		'Sequence': uuidv4(),
		'Timestamp': `${new Date().toISOString()}`,
		'OperatorAccountID': OPERATOR_ACCOUNT_ID,
		'AccountTransactionID': ACCOUNT_TRANSACTION_ID,
		'AccountTransactionTypeID': ACCOUNT_TRANSACTION_TYPE_ID,
		'CurrencyCode': CURRENCY_CODE,
		'Amount': 10.00
	});

	const response = http.post(`${GAME_BACKEND_BASE_URL_LOCAL}/v1.0/accounttransaction/credit`, body, getCommonParams('credit'));
	verifyResponse(response);
}

export function performStartGameRequest() {

	const response = http.get(`${GAME_BACKEND_BASE_URL_LOCAL}/games/default/startGame?token=eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9.eyJsYXN0TmFtZSI6Ik9vQ2tqWVV2UkIiLCJqd3RVc2VyIjoiSDRzSUFBQUFBQUFBQUhXUVRZdkNNQlJGLzRxOGRldlF0QzhtWFExMVowWEZRV0VRR1dwTUpQMktKTFliOGIvUGN6RWJHVmNQenJtWEMrOE9GbkxFbVl4Z2dCeXNjZDRPWGNMVGhFdkdabHdJQVJFWVV2MXBMUDBwYkZSTm9DV3dkdk9tL3Q2TjI0S0FmbFArN0NyYjl0WE4rYWx5SFFYUHRNZm85RlJvN2FpRGNsNy9ETTFUQmNnUGQvalBQTm55ajAxMkpiSHJHZktiSC9UakdFRkZPbU1xUVNaMGpKaGluQ2xUeFlJTEdTY0tNeU5TaHRJWXFvVUxaVmZGdnR3V1g1djVnc2psWmUvanpST0NwVVhrVEQ1K0FmSmU4QUExQVFBQSIsInNlc3Npb25LZXkiOiJmYTgxNjZmNi1mZTBlLTQ5M2QtYTdiYS0wZjdiODVjYmQ1MTciLCJ1c2VyX25hbWUiOiJpZm9yaXVtMTYzMTY5MjI3Njg4OCIsInNlc3Npb25JZCI6NTYyOSwidXNlckd1aWQiOiJkZWZhdWx0L2lmb3JpdW0xNjMxNjkyMjc2ODg4IiwidXNlcklkIjo1NTc5LCJhdXRob3JpdGllcyI6WyJST0xFX1VTRVIiXSwiY2xpZW50X2lkIjoiYWNtZSIsImZpcnN0TmFtZSI6Im5idktyYnNQY2oiLCJhdWQiOlsib2F1dGgyLXJlc291cmNlIl0sInNjb3BlIjpbIm9wZW5pZCJdLCJkb21haW5OYW1lIjoiZGVmYXVsdCIsInJlZ2lzdHJhdGlvbkRhdGUiOjE2MzE2OTIyNzcwMDAsImV4cCI6MTYzNDM3MDY3Nywic2hvcnRHdWlkIjoiTkJWS1JCU1BDSiIsImp0aSI6IjYzZjRmMzMyLTc2YWMtNDZkMi05M2EyLWUyNzA5MjhmNmJlMyIsImVtYWlsIjoiaWZvcml1bTE2MzE2OTIyNzY4ODhAbWFpbG5hdG9yLmNvbSIsInVzZXJuYW1lIjoiaWZvcml1bTE2MzE2OTIyNzY4ODgifQ.pV49AySkBcjWc76e_qAAnddjnmlpok7lwX9dWSRVLt73b-7GzQaXBnSDOWnHs6KoMocWinwDPRWgcQwBjer1aBgcL3qStY4taApfdzbavw_ENfSvoyRGutqJ4BE9XGVVPIV-s0tjOJkwJlSFbTzz2fBxntczXPD_vumQ7AwpitTEY0JZPSkabyAXuXK80-r1v3EwoKofF50lwW6hi7hY7Jr9hFbPSz0saXWddHLuLtmN8RYo6AtZEPb-XIgIORQNiPb6iLk1Gta1fOkSYMtNmfz5ezGA_8e75VJIXryl8bOCC9jPyU3vfu31AB1IAf7IO0PczYx3Kwdzxf8CNj8lOTPWfJFek04LFYJMmfsP1kgwNqxxXN99gPMmW8SYewszGwE4JuFjKS1_nZIGCWAmOoQdLHZsQ6W2bYcnWzNaadw9x8Rvr8V28DEkduEXaaCzJ_vYqkz43dfPcC2ubSRDaC6Ryb_rgbEgxJQ5AOp680K-1z0cPWldDXVvqG6P5UasNWfr_YUk0ihvMRWVoefAtrtCAyjhh2Y-c1QQveBZlerj6Torthp3rqKtOU5Z_YthhFsqlIKZCNd-drMgeCFQgQA-iHdaOdmhCbAIfrhxqDW64bnynL7GDD0B2hZeea2E7_vCh5RNAjcXaB9AeW18MX8obCCOh9QfD4w9zftu94E&gameId=11588&domainName=default&lang=en`,
		getCommonParams('startGame'));
	verifyResponseForLaunchGame(response);
}

export function performDemoGamesRequest() {
	const response = http.get(`${GAME_BACKEND_BASE_URL_LOCAL}/games/default/demoGame?gameId=11588&domainName=default&lang=en`, getCommonParams('demoGame'));
	verifyResponseForLaunchGame(response);
}

function getCommonParams(request) {
	return {
		headers: {
			'Authorization': `Basic ${encoding.b64encode('admin:admin')}`,
			'Content-Type': 'application/json',
			'X-Forwarded-For': '127.0.0.1'
		},
		tags: {
			'requestType': request
		}
	}
}

function verifyResponse(response) {
	check(response, {
		'is status 200': (r) => r.status === 200,
		'is body errorCode 0': (r) => r.body && r.json('ErrorCode') === 0
	})
}

function verifyResponseForLaunchGame(response) {
	check(response, {
		'is status 200': (r) => r.status === 200,
		'is body status 0': (r) => r.body && r.json('status') === 0
	})
}