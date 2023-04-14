package lithium.service.casino.provider.iforium.constant;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Base64;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class TestConstants {

    public static final String GBP_CURRENCY = "GBP";
    public static final String USD_CURRENCY = "USD";

    public static final String DOMAIN_NAME = "domain";

    public static final String OPERATOR_ACCOUNT_ID = DOMAIN_NAME + "/accountId";
    public static final String PLATFORM_KEY = "L100";
    public static final String SEQUENCE = "f82f441f-a20f-4244-b760-35d2d05705d7";
    public static final String TIMESTAMP = "2020-09-14T13:21:49.9546136Z";
    public static final String GAME_ID = "gameId";
    public static final String CONTENT_GAME_PROVIDER_ID = "contentGameProviderId";
    public static final String FREE_GAME_OFFER_CODE = "OfferCode";
    public static final String GAME_VERSION = "V1.2.3.4";
    public static final String TABLE_ID = "T1234";
    public static final BigDecimal JACKPOT_CONTRIBUTION = BigDecimal.ZERO;
    public static final String SOURCE = "OperatorWallet";
    public static final String ALERT_ACTION_ID = "xa-82b3-11eb-8dcd-0242ac130003";
    public static final String OPERATOR_ALERT_ACTION_REFERENCE = "AA1234C";
    public static final String OPERATOR_ALERT_REFERENCE = "AA1234";
    public static final String GAMING_REGULATOR_CODE = "GamingCode";
    public static final String TYPE = "AlertType";
    public static final String METHOD = "AlertActionMethod";
    public static final String DATA = "AlertActionData";

    public static final String GAME_GUID = TestConstants.MODULE_NAME + "_" + TestConstants.GAME_ID;

    public static final String COUNTRY_CODE = "GB";

    public static final String GATEWAY_SESSION_TOKEN = "72542ee1-eca8-4df9-93cb-76a5555e3da2";
    public static final String GAME_ROUND_ID = "13245Z";
    public static final String GAME_ROUND_TRANSACTION_ID = "123456Y";
    public static final String ORIGINAL_BET_GAME_ROUND_TRANSACTION_ID = "123456A";
    public static final String ACCOUNT_TRANSACTION_ID = "d6637386-c3c4-494f-830d-92223d975f37";
    public static final String SHADOW_WITHDRAWAL_TRANSACTION_TYPE_ID = "812";
    public static final String SHADOW_DEPOSIT_TRANSACTION_TYPE_ID = "811";

    public static final Long AMOUNT_ONE_HUNDRED_CENTS = 100L;

    public static final String INVALID_LONG_PARAMETER = "invalidLongParameterinvalidLongParameterinvalidLongParameterinvalidLongParameterLongParameterParameterLongParameter";

    public static final String BALANCE_REQUEST_FIXTURE_PATH = "fixtures/request/balance_request.json";
    public static final String BALANCE_REQUEST_WITH_OPTIONAL_PARAMETERS_FIXTURE_PATH = "fixtures/request/balance_request_with_optional_parameters.json";

    public static final String REDEEM_SESSION_TOKEN_REQUEST_FIXTURE_PATH = "fixtures/request/redeem_session_token_request.json";

    public static final String SUCCESS_BALANCE_RESPONSE_FIXTURE_PATH = "fixtures/response/success_balance_response.json";

    public static final String SECURE_USERNAME = "username";
    public static final String SECURE_PASSWORD = "password";
    public static final String AUTHORIZATION = "Basic " + Base64.getEncoder()
                                                                .encodeToString((SECURE_USERNAME + ":" + SECURE_PASSWORD).getBytes());
    public static final String WHITELISTED_IP = "0.0.0.0";
    public static final String NOT_WHITELISTED_IP = "8.8.8.8";

    public static final String JASYPT_ENCRYPTOR_PASSWORD = "service-casino-provider-iforium";
    public static final String MODULE_NAME = "service-casino-provider-iforium";
    public static final String SUCCESS_REDEEM_SESSION_TOKEN_RESPONSE_FIXTURE_PATH = "fixtures/response/success_redeem_session_token_response.json";
    public static final String FAILURE_RESPONSE_FIXTURE_PATH = "fixtures/response/failure_response.json";
    public static final String FAILURE_RESPONSE_WITH_BALANCE_FIXTURE_PATH = "fixtures/response/failure_response_with_balance.json";
    public static final String CREATE_SESSION_TOKEN_REQUEST_FIXTURE_PATH = "fixtures/request/create_session_token_request.json";
    public static final String PLACE_BET_REQUEST_FIXTURE_PATH = "fixtures/request/place_bet_request.json";
    public static final String END_REQUEST_FIXTURE_PATH = "fixtures/request/end_request.json";
    public static final String END_REQUEST_WITH_OPTIONAL_PARAMETERS_FIXTURE_PATH = "fixtures/request/end_request_with_optional_parameters.json";
    public static final String PLACE_BET_REQUEST_WITH_OPTIONAL_PARAMETERS_FIXTURE_PATH = "fixtures/request/place_bet_request_with_optional_parameters.json";
    public static final String SUCCESS_PLACE_BET_RESPONSE_FIXTURE_PATH = "fixtures/response/success_place_bet_response.json";
    public static final String SUCCESS_AWARD_WINNINGS_RESPONSE_FIXTURE_PATH = "fixtures/response/success_award_winnings_response.json";
    public static final String SUCCESS_ROLL_BACK_BET_RESPONSE_FIXTURE_PATH = "fixtures/response/success_roll_back_bet_response.json";
    public static final String SUCCESS_CREDIT_RESPONSE_FIXTURE_PATH = "fixtures/response/success_credit_response.json";

    public static final String SESSION_KEY = "72542ee1-eca8-4df9-93cb-76a5555e3da2";
    public static final int SESSION_TOKEN_LENGTH = 48;
    public static final long SESSION_TOKEN_TTL = 60000;

    public static final String SESSION_WRAP_TOKEN_PROPERTY_NAME = "api.iforium.session.wrap-token";
    public static final String SESSION_REDEEM_TOKEN_PROPERTY_NAME = "api.iforium.session.redeem-token.path";
    public static final String SESSION_CREATE_TOKEN_PROPERTY_NAME = "api.iforium.session.create-token.path";
    public static final String BALANCE_PROPERTY_NAME = "api.iforium.balance";
    public static final String PLACE_BET_API_PATH_PROPERTY_NAME = "api.iforium.game-round.place-bet.path";
    public static final String END_API_PATH_PROPERTY_NAME = "api.iforium.game-round.end.path";
    public static final String AWARD_WINNINGS_API_PATH_PROPERTY_NAME = "api.iforium.game-round.award-winnings.path";
    public static final String ROLL_BACK_BET_API_PATH_PROPERTY_NAME = "api.iforium.game-round.roll-back-bet.path";
    public static final String VOID_BET_API_PATH_PROPERTY_NAME = "api.iforium.game-round.void-bet.path";
    public static final String ALERT_WALLET_CALLBACK_NOTIFICATION_API_PATH_PROPERTY_NAME = "api.iforium.alerts.alert-wallet-callback-notification";
    public static final String CREDIT_API_PATH_PROPERTY_NAME = "api.iforium.account-transaction.credit.path";

    public static final String SESSION_WRAP_TOKEN_PATH = "/v1.0/session/wraptoken";
    public static final String SESSION_REDEEM_TOKEN_PATH = "/v1.0/session/redeemtoken";
    public static final String SESSION_CREATE_TOKEN_PATH = "/v1.0/session/createtoken";
    public static final String BALANCE_PATH = "/v1.0/balance";
    public static final String PLACE_BET_API_PATH = "/v1.0/gameround/placebet";
    public static final String END_API_PATH = "/v1.0/gameround/end";
    public static final String AWARD_WINNINGS_API_PATH = "/v1.0/gameround/awardwinnings";
    public static final String VOID_BET_API_PATH = "/v1.0/gameround/voidbet";
    public static final String ROLL_BACK_BET_API_PATH = "/v1.0/gameround/rollbackbet";
    public static final String ALERT_WALLET_CALLBACK_NOTIFICATION_API_PATH = "/v1.0/alertwalletcallbacknotification";
    public static final String CREDIT_API_PATH = "/v1.0/accounttransaction/credit ";

    public static final String START_GAME_PATH = "/games/{domainName}/startGame";
    public static final String LIST_GAME_PATH = "/games/{domainName}/listGames";
    public static final String DEMO_GAME_PATH = "/games/{domainName}/demoGame";
    public static final String LIST_FRB_GAMES_REPORT_PATH = "/games/{domainName}/listFrbGames";
    public static final String ADD_GAME_PATH = "/games/add";
    public static final String FIND_BY_ID_PATH = "/games/{gameId}/findById";
    public static final String EDIT_GRAPHIC_PATH = "/games/{gameId}/editGraphic/{graphicFunction}";
    public static final String TOGGLE_LOCKED_PATH = "/games/{gameId}/unlock/toggle";

    public static final String EDIT_PATH = "/games/edit";
    public static final String LIST_DOMAIN_GAMES_PATH = "/games/{domainName}/listDomainGames";
    public static final String FIND_BY_GUID_AND_DOMAIN_NAME_PATH = "/games/{domainName}/find/guid/{gameGuid}";
    public static final String FIND_BY_GUID_AND_DOMAIN_NAME_NO_LABELS_PATH = "/games/{domainName}/find/guid/{gameGuid}/no-labels";
    public static final String LIST_DOMAIN_GAMES_DT_PATH = "/games/{domainName}/listDomainGamesDT";
    public static final String LIST_DOMAIN_GAMES_REPORT_PATH = "/games/{domainName}/listDomainGamesReport";
    public static final String IS_GAME_LOCKED_FOR_PLAYER_PATH = "/games/{domainName}/isGameLockedForPlayer";

    public static final String SUCCESS_START_GAME_RESPONSE_WITHOUT_REGULATIONS_FIXTURE_PATH = "fixtures/response/success_start_game_response_without_regulations.json";
    public static final String SUCCESS_START_GAME_RESPONSE_FIXTURE_PATH = "fixtures/response/success_start_game_response.json";
    public static final String SUCCESS_LIST_GAME_RESPONSE_FIXTURE_PATH = "fixtures/response/success_list_game_response.json";

    public static final String SUCCESS_DEMO_GAME_RESPONSE_FIXTURE_PATH = "fixtures/response/success_demo_game_response.json";
    public static final String SUCCESS_DEMO_GAME_RESPONSE_WITHOUT_REGULATIONS_FIXTURE_PATH = "fixtures/response/success_demo_game_response_without_regulations.json";
    public static final String SUCCESS_EMPTY_DATATABLE_RESPONSE_FIXTURE_PATH = "fixtures/response/success_empty_datatable_response.json";
    public static final String FAILURE_LITHIUM_RESPONSE_NOT_IMPLEMENTED_STATUS_501_RESPONSE_FIXTURE_PATH = "fixtures/response/failure_lithium_response_not_implemented_status_501.json";

    public static final String LANG = "en";
    public static final String LOCAL_GATEWAY_URL = "http://localhost:9000/";

    public static final String QUERY_PARAM_TOKEN = "token";
    public static final String QUERY_PARAM_GAME_ID = "gameId";
    public static final String QUERY_PARAM_LANG = "lang";
    public static final String QUERY_PARAM_CURRENCY = "currency";
    public static final String QUERY_PARAM_OS = "os";
    public static final String QUERY_PARAM_MACHINE_GUID = "machineGUID";
    public static final String QUERY_PARAM_TUTORIAL = "tutorial";
    public static final String QUERY_PARAM_PLATFORM = "platform";
}
