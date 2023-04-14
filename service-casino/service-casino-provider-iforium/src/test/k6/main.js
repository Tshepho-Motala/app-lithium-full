import {group} from 'k6';
import {
	performAlertWalletCallbackNotificationRequest,
	performAwardWinnings,
	performBalanceRequest,
	performCreateTokenRequest,
	performCreditRequest,
	performDemoGamesRequest,
	performEndRequest,
	performPlaceBetRequest,
	performRedeemTokenRequest,
	performRollbackBetRequest,
	performStartGameRequest,
	performVoidBetRequest
} from "./iForiumGameBackendUtils.js";

const VUS_COUNT = __ENV.VUS;
const PERF_TEST_DURATION = __ENV.DURATION;

export let options = {
	scenarios: {
		perf_test: {
			executor: 'constant-vus',
			duration: PERF_TEST_DURATION,
			vus: VUS_COUNT,

			exec: 'perfTest'
		}
	},

	thresholds: {
		'http_req_duration{requestType:balance}': [],
		'http_req_duration{requestType:redeemToken}': [],
		'http_req_duration{requestType:createToken}': [],
		'http_req_duration{requestType:placeBet}': [],
		'http_req_duration{requestType:end}': [],
		'http_req_duration{requestType:awardWinnings}': [],
		'http_req_duration{requestType:startGame}': [],
		'http_req_duration{requestType:demoGame}': [],
		'http_req_duration{requestType:rollBackBet}': [],
		'http_req_duration{requestType:voidBet}': [],
		'http_req_duration{requestType:alertWalletCallbackNotification}': [],
		'http_req_duration{requestType:credit}': [],

		'http_reqs{requestType:balance}': [],
		'http_reqs{requestType:redeemToken}': [],
		'http_reqs{requestType:createToken}': [],
		'http_reqs{requestType:placeBet}': [],
		'http_reqs{requestType:end}': [],
		'http_reqs{requestType:awardWinnings}': [],
		'http_reqs{requestType:startGame}': [],
		'http_reqs{requestType:demoGame}': [],
		'http_reqs{requestType:rollBackBet}': [],
		'http_reqs{requestType:voidBet}': [],
		'http_reqs{requestType:alertWalletCallbackNotification}': [],
		'http_reqs{requestType:credit}': []
	}
}

export function perfTest() {
	group('balance', function () {
		group('balance', () => performBalanceRequest());
	});

	group('redeemToken', function () {
		group('redeemToken', () => performRedeemTokenRequest());
	});

	group('createToken', function () {
		group('createToken', () => performCreateTokenRequest())
	});

	group('balance->placeBet', function () {
		group('balance', () => performBalanceRequest())
		group('placeBet', () => performPlaceBetRequest())
	});

	group('placeBet->end', function () {
		group('placeBet', () => performPlaceBetRequest())
		group('end', () => performEndRequest())
	});

	group('balance->placeBet->end', function () {
		group('balance', () => performBalanceRequest())
		group('placeBet', () => performPlaceBetRequest())
		group('end', () => performEndRequest())
	});

	group('placeBet->awardWinnings', function () {
		group('placeBet', () => performPlaceBetRequest())
		group('awardWinnings', () => performAwardWinnings())
	});

	group('balance->placeBet->awardWinnings->end', function () {
		group('balance', () => performBalanceRequest())
		group('placeBet', () => performPlaceBetRequest())
		group('awardWinnings', () => performAwardWinnings())
		group('end', () => performEndRequest())
	});

	group('startGame', function () {
		group('startGame', () => performStartGameRequest())
	});

	group('demoGame', function () {
		group('demoGame', () => performDemoGamesRequest())
	});

	group('placeBet->rollbackBet', function () {
		group('placeBet', () => performPlaceBetRequest())
		group('rollbackBet', () => performRollbackBetRequest())
	});

	group('balance->placeBet->rollbackBet', function () {
		group('balance', () => performBalanceRequest())
		group('placeBet', () => performPlaceBetRequest())
		group('rollbackBet', () => performRollbackBetRequest())
	});

	group('balance->placeBet->rollbackBet->end', function () {
		group('balance', () => performBalanceRequest())
		group('placeBet', () => performPlaceBetRequest())
		group('rollbackBet', () => performRollbackBetRequest())
		group('end', () => performEndRequest())
	});

	group('balance->placeBet->end->rollbackBet', function () {
		group('balance', () => performBalanceRequest())
		group('placeBet', () => performPlaceBetRequest())
		group('end', () => performEndRequest())
		group('rollbackBet', () => performRollbackBetRequest())
	});

	group('voidBet', function () {
		group('voidBet', () => performVoidBetRequest())
	});

	group('alertWalletCallbackNotification', function () {
		group('alertWalletCallbackNotification', () => performAlertWalletCallbackNotificationRequest())
	})

	group('credit', function () {
		group('credit', () => performCreditRequest())
	})
}