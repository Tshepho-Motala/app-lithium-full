import http from 'k6/http';
import { sleep } from "k6";

export let options = {
    stages: [
        { duration: '1m28s', target: 15 },
        { duration: '2m00s', target: 15 }
    ]
}

export default function () {
    //const url = 'https://rabbitmq.lithium-develop.roxor.games/api/exchanges/%2f/service-accounting-roxor/publish';
    const url = 'http://localhost:15672/api/exchanges/%2f/service-accounting-roxor/publish';

    const params = {
        headers: {
            'Content-Type': 'application/json',
            'Authorization': 'Basic Z3Vlc3Q6Z3Vlc3Q='
        },
    };

    const requestId = uuidv4() + ":" + Math.random().toString(36).substring(28); + ":" + uuidv4()
    const eventTime = new Date().getTime();
    const website = "gamesysgames";
    const gameId = "banghai";
    const sessionId = __VU + 20000000;
    const playerId = __VU + 190788888;
    const gamekey = "play-banghai";

    doSpinScenario(url, params, "CASH_JACKPOT_0XWIN", requestId, eventTime, website, gameId, sessionId, playerId, gamekey);
    doSpinScenario(url, params, "CASH_JACKPOT_1XWIN", requestId, eventTime, website, gameId, sessionId, playerId, gamekey);
    doSpinScenario(url, params, "CASH_JACKPOT_0XWIN", requestId, eventTime, website, gameId, sessionId, playerId, gamekey);
    doSpinScenario(url, params, "CASH_JACKPOT_2XWIN", requestId, eventTime, website, gameId, sessionId, playerId, gamekey);
    doSpinScenario(url, params, "CASH_JACKPOT_0XWIN", requestId, eventTime, website, gameId, sessionId, playerId, gamekey);
    doSpinScenario(url, params, "CASH_JACKPOT_0XWIN", requestId, eventTime, website, gameId, sessionId, playerId, gamekey);
    doSpinScenario(url, params, "CASH_JACKPOT_0XWIN", requestId, eventTime, website, gameId, sessionId, playerId, gamekey);
    doSpinScenario(url, params, "CASH_JACKPOT_0XWIN", requestId, eventTime, website, gameId, sessionId, playerId, gamekey);
    doSpinScenario(url, params, "CASH_JACKPOT_1XWIN", requestId, eventTime, website, gameId, sessionId, playerId, gamekey);
    doSpinScenario(url, params, "CASH_JACKPOT_1XWIN", requestId, eventTime, website, gameId, sessionId, playerId, gamekey);
    doSpinScenario(url, params, "CASH_JACKPOT_0XWIN", requestId, eventTime, website, gameId, sessionId, playerId, gamekey);
    doSpinScenario(url, params, "CASH_JACKPOT_2XWIN", requestId, eventTime, website, gameId, sessionId, playerId, gamekey);
    doSpinScenario(url, params, "CASH_JACKPOT_0XWIN", requestId, eventTime, website, gameId, sessionId, playerId, gamekey);
    doSpinScenario(url, params, "CASH_JACKPOT_0XWIN", requestId, eventTime, website, gameId, sessionId, playerId, gamekey);
    doSpinScenario(url, params, "CASH_0XWIN", requestId, eventTime, website, gameId, sessionId, playerId, gamekey);
    doSpinScenario(url, params, "CASH_0XWIN", requestId, eventTime, website, gameId, sessionId, playerId, gamekey);
    doSpinScenario(url, params, "CASH_0XWIN", requestId, eventTime, website, gameId, sessionId, playerId, gamekey);
    doSpinScenario(url, params, "CASH_1XWIN", requestId, eventTime, website, gameId, sessionId, playerId, gamekey);
    doSpinScenario(url, params, "CASH_0XWIN", requestId, eventTime, website, gameId, sessionId, playerId, gamekey);
    doSpinScenario(url, params, "CASH_0XWIN", requestId, eventTime, website, gameId, sessionId, playerId, gamekey);
    doSpinScenario(url, params, "CASH_0XWIN", requestId, eventTime, website, gameId, sessionId, playerId, gamekey);
    doSpinScenario(url, params, "CASH_0XWIN", requestId, eventTime, website, gameId, sessionId, playerId, gamekey);
    doSpinScenario(url, params, "CASH_0XWIN", requestId, eventTime, website, gameId, sessionId, playerId, gamekey);
    doSpinScenario(url, params, "CASH_0XWIN", requestId, eventTime, website, gameId, sessionId, playerId, gamekey);
    doSpinScenario(url, params, "REWARD_1XWIN", requestId, eventTime, website, gameId, sessionId, playerId, gamekey);
    doSpinScenario(url, params, "CASH_0XWIN", requestId, eventTime, website, gameId, sessionId, playerId, gamekey);
}

function doSpinScenario(url, params, scenarioName, requestId, eventTime, website, gameId, sessionId, playerId, gamekey) {
    const gameplayId = uuidv4();

    if (scenarioName === "CASH_0XWIN") {
        http.post(url, getRabbitMQMessage("START_GAME_PLAY", uuidv4(), requestId, eventTime, gameplayId, website, gameId, sessionId, playerId, gamekey), params);
        http.post(url, getRabbitMQMessage("TRANSFER_DEBIT", uuidv4(), requestId, eventTime, gameplayId, website, gameId, sessionId, playerId, gamekey), params);
        http.post(url, getRabbitMQMessage("FINISH_GAME_PLAY", uuidv4(), requestId, eventTime, gameplayId, website, gameId, sessionId, playerId, gamekey), params);

        sleep(2)
    } else if (scenarioName === "CASH_1XWIN") {
        http.post(url, getRabbitMQMessage("START_GAME_PLAY", uuidv4(), requestId, eventTime, gameplayId, website, gameId, sessionId, playerId, gamekey), params);
        http.post(url, getRabbitMQMessage("TRANSFER_DEBIT", uuidv4(), requestId, eventTime, gameplayId, website, gameId, sessionId, playerId, gamekey), params);
        http.post(url, getRabbitMQMessage("TRANSFER_CREDIT", uuidv4(), requestId, eventTime, gameplayId, website, gameId, sessionId, playerId, gamekey), params);
        http.post(url, getRabbitMQMessage("FINISH_GAME_PLAY", uuidv4(), requestId, eventTime, gameplayId, website, gameId, sessionId, playerId, gamekey), params);

        sleep(6)
    } else if (scenarioName === "CASH_JACKPOT_0XWIN") {
        http.post(url, getRabbitMQMessage("START_GAME_PLAY", uuidv4(), requestId, eventTime, gameplayId, website, gameId, sessionId, playerId, gamekey), params);
        http.post(url, getRabbitMQMessage("TRANSFER_DEBIT", uuidv4(), requestId, eventTime, gameplayId, website, gameId, sessionId, playerId, gamekey), params);
        http.post(url, getRabbitMQMessage("ACCRUAL", uuidv4(), requestId, eventTime, gameplayId, website, gameId, sessionId, playerId, gamekey), params);
        http.post(url, getRabbitMQMessage("FINISH_GAME_PLAY", uuidv4(), requestId, eventTime, gameplayId, website, gameId, sessionId, playerId, gamekey), params);

        sleep(2)
    } else if (scenarioName === "CASH_JACKPOT_1XWIN") {
        http.post(url, getRabbitMQMessage("START_GAME_PLAY", uuidv4(), requestId, eventTime, gameplayId, website, gameId, sessionId, playerId, gamekey), params);
        http.post(url, getRabbitMQMessage("TRANSFER_DEBIT", uuidv4(), requestId, eventTime, gameplayId, website, gameId, sessionId, playerId, gamekey), params);
        http.post(url, getRabbitMQMessage("ACCRUAL", uuidv4(), requestId, eventTime, gameplayId, website, gameId, sessionId, playerId, gamekey), params);
        http.post(url, getRabbitMQMessage("TRANSFER_CREDIT", uuidv4(), requestId, eventTime, gameplayId, website, gameId, sessionId, playerId, gamekey), params);
        http.post(url, getRabbitMQMessage("FINISH_GAME_PLAY", uuidv4(), requestId, eventTime, gameplayId, website, gameId, sessionId, playerId, gamekey), params);

        sleep(6)
    } else if (scenarioName === "CASH_JACKPOT_2XWIN") {
        http.post(url, getRabbitMQMessage("START_GAME_PLAY", uuidv4(), requestId, eventTime, gameplayId, website, gameId, sessionId, playerId, gamekey), params);
        http.post(url, getRabbitMQMessage("TRANSFER_DEBIT", uuidv4(), requestId, eventTime, gameplayId, website, gameId, sessionId, playerId, gamekey), params);
        http.post(url, getRabbitMQMessage("ACCRUAL", uuidv4(), requestId, eventTime, gameplayId, website, gameId, sessionId, playerId, gamekey), params);
        http.post(url, getRabbitMQMessage("TRANSFER_CREDIT", uuidv4(), requestId, eventTime, gameplayId, website, gameId, sessionId, playerId, gamekey), params);
        http.post(url, getRabbitMQMessage("TRANSFER_CREDIT", uuidv4(), requestId, eventTime, gameplayId, website, gameId, sessionId, playerId, gamekey), params);
        http.post(url, getRabbitMQMessage("FINISH_GAME_PLAY", uuidv4(), requestId, eventTime, gameplayId, website, gameId, sessionId, playerId, gamekey), params);

        sleep(9)
    } else if (scenarioName === "REWARD_1XWIN") {
        http.post(url, getRabbitMQMessage("START_GAME_PLAY", uuidv4(), requestId, eventTime, gameplayId, website, gameId, sessionId, playerId, gamekey), params);
        http.post(url, getRabbitMQMessage("REWARD_REDEEM", uuidv4(), requestId, eventTime, gameplayId, website, gameId, sessionId, playerId, gamekey), params);
        http.post(url, getRabbitMQMessage("TRANSFER_CREDIT", uuidv4(), requestId, eventTime, gameplayId, website, gameId, sessionId, playerId, gamekey), params);
        http.post(url, getRabbitMQMessage("FINISH_GAME_PLAY", uuidv4(), requestId, eventTime, gameplayId, website, gameId, sessionId, playerId, gamekey), params);

        sleep(6)
    }

}

function uuidv4() {
    return 'xxxxxxxx-xxxx-4xxx-yxxx-xxxxxxxxxxxx'.replace(/[xy]/g, function (c) {
        const r = Math.random() * 16 | 0, v = c == 'x' ? r : (r & 0x3 | 0x8);
        return v.toString(16);
    });
}

function getMetadata(eventId, requestId, eventTime) {
    const metadata = {
        "nodeName": "localhost",
        "podName": "local",
        "eventType": "gamesys.rgp.core.lifecycle.gameplay.kafka.model.Gameplay",
        "componentName": "core",
        "componentVersion": "unknown"
    };

    metadata.eventId = eventId;
    metadata.requestId = requestId;
    metadata.eventTime = eventTime;

    return metadata;
}

function getRabbitMQProperties() {
    return {
        "properties": {
            "delivery_mode": 2,
            "content_type": "application/json"
        },
        "routing_key": "service-accounting-roxor",
        "payload": "",
        "payload_encoding": "string"
    };
}

function getRabbitMQMessage(gamePlayOperationType, eventId, requestId, eventTime, gameplayId, website, gameId, sessionId, playerId, gamekey) {
    const rabbitMQMessage = getRabbitMQProperties();

    rabbitMQMessage.payload = JSON.stringify(getOperation(gamePlayOperationType, eventId, requestId, eventTime, gameplayId, website, gameId, sessionId, playerId, gamekey));

    return JSON.stringify(rabbitMQMessage);
}

function getOperation(gamePlayOperationType, eventId, requestId, eventTime, gameplayId, website, gameId, sessionId, playerId, gamekey) {
    const operation = {};

    operation.metadata = getMetadata(eventId, requestId, eventTime);
    operation.payload = getPayload(gamePlayOperationType, gameplayId, website, gameId, sessionId, playerId, gamekey);

    return operation;
}

function getPayload(gamePlayOperationType, gameplayId, website, gameId, sessionId, playerId, gamekey) {
    const payload = getPayloadForGamePlayOperationType(gamePlayOperationType);

    payload.gameplayId = gameplayId;
    payload.website = website;
    payload.gameId = gameId;
    payload.sessionId = sessionId;
    payload.playerId = playerId;
    payload.gamekey = gamekey;

    return payload;
}

function getPayloadForGamePlayOperationType(gamePlayOperationType) {

    if (gamePlayOperationType === "START_GAME_PLAY") {
        return {
            "provider": "Gamesys",
            "country": "GB",
            "type": "START_GAME_PLAY",
            "currency": null,
            "amount": null,
            "status": "OK",
            "poolId": null,
            "gameVersion": "1",
            "gameConfigVersion": "1",
            "lines": null,
            "coinSize": null,
            "gameType": "SLOT",
        }
    } else if (gamePlayOperationType === "TRANSFER_DEBIT") {
        return {
            "country": "GB",
            "type": "TRANSFER_DEBIT",
            "currency": "GBP",
            "amount": 0.5,
            "status": "OK",
            "poolId": null,
            "gameVersion": "1",
            "gameConfigVersion": "1",
            "lines": null,
            "coinSize": null,
            "gameType": "SLOT",
            "provider": "Gamesys",
        }
    } else if (gamePlayOperationType === "REWARD_REDEEM") {
        return {
            "provider": "Gamesys",
            "country": "GB",
            "type": "REWARD_REDEEM",
            "currency": "GBP",
            "amount": 20.0,
            "status": "OK",
            "poolId": null,
            "gameVersion": "1",
            "gameConfigVersion": "1",
            "lines": null,
            "coinSize": null,
            "gameType": "SLOT"
        }
    } else if (gamePlayOperationType === "TRANSFER_CREDIT") {
        return {
            "country": "GB",
            "type": "TRANSFER_CREDIT",
            "currency": "GBP",
            "amount": 0.24,
            "status": "OK",
            "poolId": null,
            "gameVersion": "1",
            "gameConfigVersion": "1",
            "lines": null,
            "coinSize": null,
            "gameType": "SLOT",
            "provider": "Gamesys",
        }
    } else if (gamePlayOperationType === "ACCRUAL") {
        return {
            "provide": "Gamesys",
            "country": "GB",
            "type": "ACCRUAL",
            "currency": null,
            "amount": 0.000001000,
            "status": "OK",
            "poolId": "3d1ba7fc-d7b3-41f6-b391-93d4f57521b2",
            "gameVersion": "1",
            "gameConfigVersion": "1",
            "lines": null,
            "coinSize": null,
            "gameType": "SLOT"
        }
    } else if (gamePlayOperationType === "FINISH_GAME_PLAY") {
        return {
            "country": "GB",
            "type": "FINISH_GAME_PLAY",
            "currency": null,
            "amount": null,
            "status": "OK",
            "poolId": null,
            "gameVersion": "1",
            "gameConfigVersion": "1",
            "lines": null,
            "coinSize": null,
            "gameType": "SLOT",
            "provider": "Gamesys"
        }
    }

}
