package lithium.service.games;

import lithium.modules.ModuleInfoAdapter;
import lithium.service.role.client.objects.Role;
import lithium.service.role.client.objects.Role.Category;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class ServiceGamesModuleInfo extends ModuleInfoAdapter {
	public ServiceGamesModuleInfo() {
		Category gameCategory = Category.builder().name("Game Operations").description("These are all the roles relevant to managing games.").build();
		addRole(Role.builder().category(gameCategory).name("Game List").role("GAME_LIST").description("View list of all games.").build());
		addRole(Role.builder().category(gameCategory).name("Game Edit").role("GAME_EDIT").description("Edit a game.").build());
		addRole(Role.builder().category(gameCategory).name("Game Add").role("GAME_ADD").description("Add a game.").build());
		addRole(Role.builder().category(gameCategory).name("Game List Populate").role("GAME_LIST_POPULATE").description("Refresh the game list from the providers.").build());
		addRole(Role.builder().category(gameCategory).name("Game Summary").role("GAME_SUMMARY").description("View game summary.").build());
		addRole(Role.builder().category(gameCategory).name("Game Images").role("GAME_IMAGES").description("View game images.").build());
		addRole(Role.builder().category(gameCategory).name("Game Players").role("GAME_PLAYERS").description("View game players tab.").build());

		Category gameSupplierCategory = Category.builder().name("Game Supplier Operations").description("These are all the roles relevant to managing game suppliers.").build();
		addRole(Role.builder().category(gameSupplierCategory).name("Game Supplier View").role("GAME_SUPPLIER_VIEW").description("View all game suppliers.").build());
		addRole(Role.builder().category(gameSupplierCategory).name("Game Supplier Add").role("GAME_SUPPLIER_ADD").description("Add game suppliers.").build());
		addRole(Role.builder().category(gameSupplierCategory).name("Game Supplier Edit").role("GAME_SUPPLIER_EDIT").description("Edit game suppliers.").build());

		Category gameTypeCategory = Category.builder().name("Game Type Operations").description("These are all the roles relevant to managing game types.").build();
		addRole(Role.builder().category(gameTypeCategory).name("Game Type View").role("GAME_TYPE_VIEW").description("View all game types.").build());
		addRole(Role.builder().category(gameTypeCategory).name("Game Type Add").role("GAME_TYPE_ADD").description("Add game types.").build());
		addRole(Role.builder().category(gameTypeCategory).name("Game Type Edit").role("GAME_TYPE_EDIT").description("Edit game types.").build());

		Category gameStudioCategory = Category.builder().name("Game Studio Operations").description("These are all the roles relevant to managing game studios.").build();
		addRole(Role.builder().category(gameStudioCategory).name("Game Studio View").role("GAME_STUDIO_VIEW").description("View all game studios.").build());
		addRole(Role.builder().category(gameStudioCategory).name("Game Studio Add").role("GAME_STUDIO_ADD").description("Add game studios.").build());
		addRole(Role.builder().category(gameStudioCategory).name("Game Studio Edit").role("GAME_STUDIO_EDIT").description("Edit game studios.").build());

		Category progressiveBalanceCategory = Category.builder().name("Progressive Jackpot Balance Operations").description("These are all the roles relevant to managing progressive jackpot balances.").build();
		addRole(Role.builder().category(progressiveBalanceCategory).name("Progressive Jackpot Balances View").role("JACKPOT_BALANCE_VIEW").description("View all progressive jackpot balances.").build());

		Category gameUserStatusCategory = Category.builder().name("Game User Status Operations").description("These are all the roles relevant to managing game user statues.").build();
		addRole(Role.builder().category(gameUserStatusCategory).name("Game User Status Edit").role("GAME_USER_STATUS_EDIT").description("Edit all game user statuses.").build());

	}

	@Override
	public void configureHttpSecurity(HttpSecurity http) throws Exception {
		//lithium.service.games.controllers.LoadingController
		http.authorizeRequests().antMatchers("/frontend/closegame").permitAll();
		//lithium.service.games.controllers.GamesController
		http.authorizeRequests().antMatchers(HttpMethod.GET, "/games/gameuserstatus").authenticated();
		http.authorizeRequests().antMatchers(HttpMethod.POST, "/games/saveGameGraphic").access("@lithiumSecurity.hasRole(authentication, 'GAME_EDIT')");
		http.authorizeRequests().antMatchers("/games/updateProviderGames").access("@lithiumSecurity.hasRole(authentication, 'GAME_EDIT')");
		http.authorizeRequests().antMatchers("/games/add").access("@lithiumSecurity.hasRole(authentication, 'GAME_ADD')");
		http.authorizeRequests().antMatchers(HttpMethod.POST, "/games/edit").access("@lithiumSecurity.hasRole(authentication, 'GAME_EDIT')");
		http.authorizeRequests().antMatchers(HttpMethod.GET, "/games/*/findById").access("@lithiumSecurity.hasRole(authentication, 'GAME_LIST')");
		http.authorizeRequests().antMatchers(HttpMethod.GET, "/games/list/*/userstatus").access("@lithiumSecurity.hasRole(authentication, 'GAME_LIST')");
		http.authorizeRequests().antMatchers(HttpMethod.POST,"/games/*/unlock/toggle").access("@lithiumSecurity.hasRole(authentication, 'GAME_EDIT')");
		http.authorizeRequests().antMatchers(HttpMethod.POST,"/games/*/unlock").access("@lithiumSecurity.hasRole(authentication, 'GAME_EDIT')");
		http.authorizeRequests().antMatchers(HttpMethod.DELETE,"/games/*/unlock/d").access("@lithiumSecurity.hasRole(authentication, 'GAME_EDIT')");
		http.authorizeRequests().antMatchers(HttpMethod.POST,"/games/editGraphic").access("@lithiumSecurity.hasRole(authentication, 'GAME_EDIT')");
		http.authorizeRequests().antMatchers(HttpMethod.POST,"/games/*/removeGraphic/**").access("@lithiumSecurity.hasRole(authentication, 'GAME_EDIT')");
		http.authorizeRequests().antMatchers(HttpMethod.GET,"/games/*/changelogs").access("@lithiumSecurity.hasRole(authentication, 'GAME_EDIT')");
		//lithium.service.games.controllers.DomainGamesController
		http.authorizeRequests().antMatchers("/games/{domain}/listProviderGames").access("@lithiumSecurity.hasDomainRole(authentication, #domain, 'GAME_LIST')");
		http.authorizeRequests().antMatchers("/games/{domain}/listDomainGamesDT").access("@lithiumSecurity.hasDomainRole(authentication, #domain, 'GAME_LIST')");
		http.authorizeRequests().antMatchers("/games/{domain}/listDomainGamesReport").access("@lithiumSecurity.hasDomainRole(authentication, #domain, 'GAME_LIST')");
		http.authorizeRequests().antMatchers("/games/{domain}/listDomainGames").access("@lithiumSecurity.hasDomainRole(authentication, #domain, 'GAME_LIST', 'PLAYER_CASINO_HISTORY_VIEW')");
		http.authorizeRequests().antMatchers("/games/{domain}/listDomainGamesPerChannel").access("@lithiumSecurity.hasDomainRole(authentication, #domain, 'GAME_LIST', 'PLAYER_CASINO_HISTORY_VIEW')");
		http.authorizeRequests().antMatchers("/frontend/search-games/{domainName}").permitAll();
		http.authorizeRequests().antMatchers("/games/{domain}/domainGameData").permitAll(); // TODO: This is used from frontend. gamelist.php / config.php
		http.authorizeRequests().antMatchers("/games/{domain}/getGameUrl").permitAll();
		http.authorizeRequests().antMatchers("/games/{domain}/startGame").permitAll();
		http.authorizeRequests().antMatchers("/games/{domain}/demoGame").permitAll();
		http.authorizeRequests().antMatchers("/games/{domain}/find/guid/**").access("@lithiumSecurity.hasDomainRole(authentication, #domain, 'GAME_LIST')");
		http.authorizeRequests().antMatchers(HttpMethod.GET, "/games/{domain}/getLockMessage").permitAll();
		http.authorizeRequests().antMatchers("/games/{domain}/getImage").permitAll();
		http.authorizeRequests().antMatchers(HttpMethod.GET, "/games/{domain}/isGameLockedForPlayer").authenticated();
		http.authorizeRequests().antMatchers("/system/**").access("@lithiumSecurity.authenticatedSystem(authentication)");
		http.authorizeRequests().antMatchers("/games/{domain}/updateProviderGames").access("@lithiumSecurity.hasDomainRole(authentication, #domain, 'GAME_EDIT', 'GAME_LIST_POPULATE')");
		http.authorizeRequests().antMatchers(HttpMethod.GET, "/games/{domain}/get-domain-games-by-game-type").access("@lithiumSecurity.hasDomainRole(authentication, #domain, 'GAME_TYPE_VIEW', 'GAME_TYPE_EDIT', 'GAME_VIEW', 'GAME_EDIT')");
		http.authorizeRequests().antMatchers("/backoffice/games/{domain}/listDomainGames").access("@lithiumSecurity.hasDomainRole(authentication, #domain, 'GAME_LIST')");
		http.authorizeRequests().antMatchers("/backoffice/games/{domain}/cdn-external-graphic/**").access("@lithiumSecurity.hasDomainRole(authentication, #domain, 'GAME_EDIT')");
		//lithium.service.games.controllers.backoffice.GameSuppliersController
		http.authorizeRequests().antMatchers(HttpMethod.GET, "/backoffice/{domainName}/game-suppliers/**").access("@lithiumSecurity.hasDomainRole(authentication, #domainName, 'GAME_SUPPLIER_VIEW', 'GAME_ADD', 'GAME_EDIT')");
		http.authorizeRequests().antMatchers(HttpMethod.POST, "/backoffice/{domainName}/game-suppliers/add").access("@lithiumSecurity.hasDomainRole(authentication, #domainName, 'GAME_SUPPLIER_ADD')");
		//lithium.service.games.controllers.backoffice.GameSupplierController
		http.authorizeRequests().antMatchers(HttpMethod.GET, "/backoffice/{domainName}/game-supplier/**").access("@lithiumSecurity.hasDomainRole(authentication, #domainName, 'GAME_SUPPLIER_VIEW')");
		http.authorizeRequests().antMatchers(HttpMethod.POST, "/backoffice/{domainName}/game-supplier/{id}/update").access("@lithiumSecurity.hasDomainRole(authentication, #domainName, 'GAME_SUPPLIER_EDIT')");
		//lithium.service.games.controllers.backoffice.ProgressiveBalanceController
		http.authorizeRequests().antMatchers(HttpMethod.GET, "/backoffice/progressive/balances/{domainName}/**").access("@lithiumSecurity.hasDomainRole(authentication, #domainName, 'JACKPOT_BALANCE_VIEW')");
		//lithium.service.games.controllers.backoffice.GameTypeController
		http.authorizeRequests().antMatchers(HttpMethod.GET, "/backoffice/{domainName}/game-types/**").access("@lithiumSecurity.hasDomainRole(authentication, #domainName, 'GAME_TYPE_VIEW', 'GAME_ADD', 'GAME_EDIT')");
		http.authorizeRequests().antMatchers(HttpMethod.POST, "/backoffice/{domainName}/game-types/add").access("@lithiumSecurity.hasDomainRole(authentication, #domainName, 'GAME_TYPE_ADD')");
		http.authorizeRequests().antMatchers(HttpMethod.GET, "/backoffice/{domainName}/game-types/**").access("@lithiumSecurity.hasDomainRole(authentication, #domainName, 'GAME_TYPE_VIEW')");
		http.authorizeRequests().antMatchers(HttpMethod.PUT, "/backoffice/{domainName}/game-types/{id}").access("@lithiumSecurity.hasDomainRole(authentication, #domainName, 'GAME_TYPE_EDIT')");
		http.authorizeRequests().antMatchers(HttpMethod.DELETE, "/backoffice/{domainName}/game-types/{id}").access("@lithiumSecurity.hasDomainRole(authentication, #domainName, 'GAME_TYPE_EDIT')");
		//lithium.service.games.controllers.backoffice.GameStudioController
		http.authorizeRequests().antMatchers(HttpMethod.POST, "/backoffice/{domainName}/game-studio/**").access("@lithiumSecurity.hasDomainRole(authentication, #domainName, 'GAME_STUDIO_VIEW', 'GAME_ADD', 'GAME_EDIT')");
		http.authorizeRequests().antMatchers(HttpMethod.POST, "/backoffice/{domainName}/game-studio/add").access("@lithiumSecurity.hasDomainRole(authentication, #domainName, 'GAME_STUDIO_ADD')");
		http.authorizeRequests().antMatchers(HttpMethod.POST, "/backoffice/{domainName}/game-studio/**").access("@lithiumSecurity.hasDomainRole(authentication, #domainName, 'GAME_STUDIO_VIEW')");
		http.authorizeRequests().antMatchers(HttpMethod.PUT, "/backoffice/{domainName}/game-studio/{id}").access("@lithiumSecurity.hasDomainRole(authentication, #domainName, 'GAME_STUDIO_EDIT')");
		http.authorizeRequests().antMatchers(HttpMethod.DELETE, "/backoffice/{domainName}/game-studio/{id}").access("@lithiumSecurity.hasDomainRole(authentication, #domainName, 'GAME_STUDIO_EDIT')");
		http.authorizeRequests().antMatchers(HttpMethod.POST, "/backoffice/{domainName}/game-studio/{id}").access("@lithiumSecurity.hasDomainRole(authentication, #domainName, 'GAME_STUDIO_EDIT')");
		http.authorizeRequests().antMatchers(HttpMethod.POST,"/backoffice/{domainName}/game-studio/changelogs").access("@lithiumSecurity.hasRole(authentication, 'GAME_EDIT')");
		//lithium.service.games.controllers.backoffice.BackOfficeGameChannelsController
		http.authorizeRequests().antMatchers(HttpMethod.POST,"/backoffice/get-channels/find-all").access("@lithiumSecurity.hasRole(authentication, 'GAME_ADD', 'GAME_LIST','GAME_EDIT')");
		//lithium.service.games.controllers.GameUserStatusController
		http.authorizeRequests().antMatchers(HttpMethod.POST,"/backoffice/game-user-status/free-games/unlock").access("@lithiumSecurity.hasRole(authentication, 'GAME_USER_STATUS_EDIT')");
		http.authorizeRequests().antMatchers(HttpMethod.POST,"/backoffice/game-user-status/free-games/lock").access("@lithiumSecurity.hasRole(authentication, 'GAME_USER_STATUS_EDIT')");
		http.authorizeRequests().antMatchers(HttpMethod.GET, "/backoffice/game-user-status/free-games/find/unlocked").access("@lithiumSecurity.hasRole(authentication, 'PLAYER_VIEW')");
		http.authorizeRequests().antMatchers(HttpMethod.GET, "/frontend/games/meta").permitAll();
		http.authorizeRequests().antMatchers(HttpMethod.POST, "/backoffice/games/{domainName}/table").access("@lithiumSecurity.hasRole(authentication, 'GAME_LIST', 'GAME_EDIT', 'GAME_ADD')");
		//lithium.service.games.controllers.frontend.FrontEndProgressiveJackpotFeedsController
		http.authorizeRequests().antMatchers(HttpMethod.POST,"/frontend/jackpot-feeds/progressive/{domainName}/get/v1").permitAll();
		//lithium.service.games.controllers.frontend.JackpotFeedsController
		http.authorizeRequests().antMatchers(HttpMethod.POST, "/backoffice/jackpot-feeds/progressive/{domainName}/registered-feeds/table").access("@lithiumSecurity.hasDomainRole(authentication, #domainName, 'JACKPOT_BALANCE_VIEW')");
		http.authorizeRequests().antMatchers(HttpMethod.POST, "/backoffice/jackpot-feeds/progressive/{domainName}/table").access("@lithiumSecurity.hasDomainRole(authentication, #domainName, 'JACKPOT_BALANCE_VIEW')");
		http.authorizeRequests().antMatchers(HttpMethod.POST, "/backoffice/jackpot-feeds/progressive/{domainName}/progressive-jackpot-game-balance/get").access("@lithiumSecurity.hasDomainRole(authentication, #domainName, 'JACKPOT_BALANCE_VIEW')");
		http.authorizeRequests().antMatchers(HttpMethod.POST, "/backoffice/jackpot-feeds/progressive/{domainName}/progressive-jackpot-balance/get").access("@lithiumSecurity.hasDomainRole(authentication, #domainName, 'JACKPOT_BALANCE_VIEW')");
		//lithium.service.games.controllers.frontend.JackpotFeedController
		http.authorizeRequests().antMatchers(HttpMethod.POST, "/backoffice/jackpot-feed/progressive/registered-feed/{id}/get").access("@lithiumSecurity.hasRole(authentication, 'JACKPOT_BALANCE_VIEW')");
		http.authorizeRequests().antMatchers(HttpMethod.POST, "/backoffice/jackpot-feed/progressive/registered-feed/{id}/toggle-enabled").access("@lithiumSecurity.hasRole(authentication, 'JACKPOT_BALANCE_VIEW')");
	}
}
