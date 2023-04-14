package lithium.service.reward.provider.casino.blueprint.utils;

public class TestConstants {

    public static final String PROCESS_REWARD_URI="/system/reward/process/v1";
    public static final String CANCEL_REWARD_URI="/system/reward/cancel/v1";

    public static final String FIXTURE_PROCESS_REWARD_REQUEST = "requests/process_reward_request.json";
    public static final String FIXTURE_PROCESS_REWARD_REQUEST_UNSUPPORTED = "requests/unsupported_process_reward_request.json";
    public static final String FIXTURE_PROCESS_REWARD_CANCEL_REQUEST = "requests/process_reward_cancel_request.json";

    public static final String FIXTURE_BLUEPRINT_FAILED_GRANT_RESPONSE = "responses/error_blueprint_response.xml";
    public static final String FIXTURE_BLUEPRINT_SUCCESSFUL_GRANT_RESPONSE = "responses/successful_blueprint_response.xml";

    public static final String FIXTURE_ENABLED_AND_DISABLED_BLUEPRINT_GAMES = "games/enabled_and_disabled_games.json";
    public static final String FIXTURE_SINGLE_BLUEPRINT_GAME = "games/single_game.json";

    public static final String FIXTURE_DOMAIN_PROVIDER_PROPERTIES= "domain/provider_properties.json";
}