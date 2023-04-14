'use strict';

angular.module('userService', ['ngCookies', 'litSecurity'])
	.factory('$userService', ['$translate', '$state','$rootScope', '$http', 'jwtHelper', '$q', "$security", "$menu", "$filter", "$timeout", "$uibModal", "rest-domain", "notify", "errors",
		function($translate, $state, $rootScope, $http, jwtHelper, $q, $security, $menu, $filter, $timeout, $uibModal, restDomain, notify, errors) {
			try {
				var service = {};
				var MAX_TIMEOUT_VALUE = 2147483647;
				var callAtTimeoutModal = null;
				var countdownModalOpened = false;

				service.authenticated = false;
				$rootScope.authenticated = false;

				$rootScope.refreshInProgress = false;
				$rootScope.token = null;
				$rootScope.principal = null;

				service._buildMenu = function() {
					//$menu.add("", "DASHBOARD", "home", "dashboard.home", "Dashboard", "dashboard", null);
					$menu.add("", "DASHBOARD, ACCOUNTING_SUMMARY_DOMAIN, BETA", "home-beta", "dashboard.home-beta", "Dashboard", "dashboard", null);
					// $menu.add("", "DASHBOARD, ACCOUNTING_SUMMARY_DOMAIN, BETA", "home-beta-v2", "dashboard.home-beta-v2", "Dashboard (BETA)-V2", "dashboard", null);

					var admin = $menu.add("TREE", "domain_*, ECOSYSTEMS_*, accesscontrol_*, accessrules_*", "admin", null, "Administration", "cogs", null);
					$menu.add("TREE", "domain_*", "domains", "dashboard.domains", "Domains", "cloud", admin);
					$menu.add("TREE", "ECOSYSTEMS_VIEW", "ecosystems", "dashboard.ecosystems", "Ecosystems", "cogs", admin);

					var sub = $menu.add("TREE", "VIEW_TRANSLATE", "i18n", null, "Internationalisation", "globe", admin);
					$menu.add("TREE", "VIEW_TRANSLATE", "languages", "dashboard.languages", "Languages", "globe", sub);
					$menu.add("TREE", "VIEW_TRANSLATE", "translations", "dashboard.translations", "Translations", "globe", sub);

					$menu.add("TREE", "accesscontrol_*", "access_control", "dashboard.accesscontrol.lists", "Access Control Lists", "list", admin);
					$menu.add("TREE", "accessrules_*", "access_rules", "dashboard.accesscontrol.rules", "Access Control Rulesets", "flag-o", admin);
					$menu.add("TREE", "brand_config_*, error_messages_*", "brandConfig", "dashboard.brandConfig", "Brand Configuration", "cog");

					var reporting = $menu.add("TREE", "loginevents_*, signupevents_*, report_*", "reporting", "dashboard.reporting", "Reporting", "list");
					$menu.add("TREE", "loginevents_*", "loginevents", "dashboard.loginevents", "Logins Report", "circle-o-notch", reporting);
					$menu.add("TREE", "signupevents_*", "signupevents", "dashboard.signupevents", "Registration History", "user-plus", reporting);
					$menu.add("TREE", "USER_THRESHOLD_HISTORY_VIEW_*", "depositthreshold", "dashboard.depositthreshold", "Deposit Threshold Trigger", "th-list", reporting);
					$menu.add("TREE", "USER_THRESHOLD_HISTORY_VIEW_*", "threshold", "dashboard.threshold", "Limits Threshold History", "th-list", reporting);
					$menu.add("TREE", "report_players", "dormantaccounts", "disabled", "Dormant Accounts*", "user-plus", reporting);
					$menu.add("TREE", "report_players", "frozenaccounts", "disabled", "Frozen Accounts", "user-plus", reporting);
					var playermanagement = $menu.add("TREE", "player_*", "playermanagement", "dashboard.players", "Player Management", "users", null);
					$menu.add("TREE", "player_*", "players", "dashboard.players.list", "Player Search", "users", playermanagement);
					$menu.add("TREE", "player_*", "massplayerupdate", "dashboard.players.massplayerupdate", "Mass Player Update", "users", playermanagement);
					$menu.add("TREE", "player_*", "onlineplayers", "disabled", "Online Players", "users", playermanagement);
					$menu.add("TREE", "player_*", "segmentations", "disabled", "Segmentations", "users", playermanagement);
					$menu.add("TREE", "player_*", "playerstags", "dashboard.players.tags", "Tag Management", "tags", playermanagement);
					$menu.add("TREE", "player_*", "playerlinks", "dashboard.players.playerlinks", "Duplicated Accounts", "user-plus", playermanagement);
					$menu.add("TREE", "player_*", "abandonedplayerssignups", "dashboard.players.incomplete", "Abandoned Signups", "user-times", playermanagement);

					var finance = $menu.add("TREE", "cashier_*, GLOBAL_ACCOUNTING_VIEW, AUTOWITHDRAWALS_RULESETS_VIEW", "cashier", "dashboard.cashier", "Finance", "usd", null);
					$menu.add("TREE", "cashier_config", "config", "dashboard.cashier.config", "Cashier Configuration", "cog", finance);
					$menu.add("TREE", "CASHIER_TRANSACTIONS", "transactions", "dashboard.cashier.transactions", "Global Cashier Transactions", "exchange", finance);
					$menu.add("TREE", "CASHIER_TRANSACTIONS_BETA", "transactions_beta", "dashboard.cashier.beta-transactions", "BETA  Cashier Transactions", "exchange", finance);
					$menu.add("TREE", "GLOBAL_ACCOUNTING_VIEW", "globalaccounting", "disabled", "Global Accounting", "list-ol", finance);
					$menu.add("TREE", "CASHIER_BANK_ACCOUNT_LOOKUP", "bank_account_lookup", "dashboard.cashier.bank_account_lookup", "Bank Account Lookup", "search", finance);
					$menu.add("TREE", "AUTOWITHDRAWALS_RULESETS_VIEW", "autowithdrawals_rulesets", "dashboard.cashier.autowithdrawals.rulesets", "Auto-Withdrawal Rules", "filter",finance);
					$menu.add("TREE", "GLOBAL_ACCOUNTING_VIEW", "liabilityreport", "disabled", "Liability Report", "list-ol", finance);

					var bonuses = $menu.add("TREE", "BONUS_VIEW, MASS_BONUS_ALLOCATION_VIEW", "bonuses", "dashboard.bonuses", "Bonuses", "btc", null)
					$menu.add("TREE", "BONUS_VIEW", "activebonuses", "dashboard.bonuses.activebonus", "Active Bonuses", "btc", bonuses);
					$menu.add("TREE", "BONUS_VIEW", "bonuses", "dashboard.bonuses", "Bonus Management", "btc", bonuses);
					$menu.add("TREE", "MASS_BONUS_ALLOCATION_VIEW", "bonuses.grantmass", "dashboard.bonuses.grantmass", "Grant Mass Bonuses", "plus-square", bonuses);

					var promos = $menu.add("TREE", "PROMOTIONS_*", "missions", null, "Promotions", "money", null);
					$menu.add("TREE", "missions_*", "missions", "dashboard.missions", "Management", "gamepad", promos);
					$menu.add("TREE", "usermissions_*", "usermissions", "dashboard.usermissions", "Player History", "history", promos);
					$menu.add("TREE", "xp_schemes_*", "schemes", "dashboard.xp.schemes", "XP and Leveling", "trophy", promos);
					$menu.add("TREE", "PROMOTIONS_*", "schemes", "dashboard.missions.beta", "Promo Beta", "trophy", promos);
					// var xpAndLeveling = $menu.add("TREE", "xp_schemes_*", "xp", "dashboard.xp", "XP and Leveling", "trophy", null);
					// $menu.add("TREE", "xp_schemes_*", "schemes", "dashboard.xp.schemes", "XP and Leveling", "trophy", promos);
					//I will comment these submenu items for later use
					$menu.add("TREE", "", "bonus_view", "disabled", "Rewards", "monitor", promos);
					$menu.add("TREE", "leaderboard", "leaderboard", "dashboard.leaderboard", "Leaderboards", "clipboard", promos);



					var sports = $menu.add("TREE", "ROLE_SPORTS_*", "sports", "dashboard.sportsbook", "Sports", "play-circle", null);
					$menu.add("TREE", "ROLE_SPORTS_BET_HISTORY", "bets", "dashboard.sportsbook.bets.domain", "Bets Search", "usd", sports);
					$menu.add("TREE", "", "exposuremonitor", "disabled", "Exposure Monitor", "monitor", sports);
					$menu.add("TREE", "", "betsmonitor", "disabled", "Bets Monitor", "bet", sports);

					var casino = $menu.add("TREE", "casino_*, game_*", "casino", "dashboard.casino", "Casino", "gamepad", null);
					$menu.add("TREE", "casino_lobbies_*", "casinobetsearch", "disabled", "Casino Bet Search", "file-text-o", casino);
					$menu.add("TREE", "casino_lobbies_*", "lobbies", "dashboard.casino.lobbies", "Lobbies", "file-text-o", casino);
					$menu.add("TREE", "game_list", "games", "dashboard.games", "Games", "play-circle", casino);
					$menu.add("TREE", "game_supplier_*", "game-suppliers", "dashboard.casino.game-suppliers", "Game Suppliers", "chain", casino);
					$menu.add("TREE", "game_studio_*", "game-studio", "dashboard.casino.game-studio", "Game Studios", "chain", casino);
					$menu.add("TREE", "game_type_*", "game-types", "dashboard.casino.game-types", "Game Types", "chain", casino);
					$menu.add("TREE", "jackpot_balance_view", "progressive-balances", "dashboard.casino.progressive-balances", "Progressive Jackpot Feeds", "usd", casino);
					$menu.add("TREE", "banner_image_*,game_tile_*", "casino-cms-images", "dashboard.casino.cms-images", "CMS Images", "chain", casino);
					$menu.add("TREE", "templates_*", "cms-builder-link", "dashboard.cmsbuilder", "CMS Builder (BETA)", "list-alt", casino);
					$menu.add("TREE", "casino_banners_*", "banner-management", "dashboard.banner-management", "Banner Management", "list-alt", casino);

					var virtuals = $menu.add("TREE", "INCENTIVEGAMES_BETS_VIEW", "incentivegames", "dashboard.incentivegames", "Virtuals", "play-circle", null);
					$menu.add("TREE", "INCENTIVEGAMES_BETS_VIEW", "bets", "dashboard.incentivegames.bets", "Incentive Games", "usd", virtuals);

					var restrictions = $menu.add("TREE", "restrictions_*, autorestriction_*", "restrictions", "dashboard.restrictions", "Restrictions", "ban", null);
					$menu.add("TREE", "restrictions_*", "dictionary", "dashboard.restrictions.dictionary", "Restriction Dictionary", "list-ol", restrictions);
					$menu.add("TREE", "autorestriction_rulesets_*", "autorestriction_rulesets", "dashboard.restrictions.autorestrictions.rulesets", "Auto Restrictions Rules", "filter", restrictions);

					var communicationcenter = $menu.add("TREE", "mail_config, sms_config, sms_queue_view, mail_queue_view", "communicationcenter", null, "Communication Center", "comment");
					$menu.add("TREE", "mail_config,email_templates_view", "mailconfig", "dashboard.mail.config", "Mail Configuration", "cog", communicationcenter);
					$menu.add("TREE", "mail_queue_view", "mailqueue", "dashboard.mail.queue", "Mail Queue", "exchange", communicationcenter);
					$menu.add("TREE", "sms_config,sms_templates_view", "smsconfig", "dashboard.sms.config", "SMS Configuration", "cog", communicationcenter);
					$menu.add("TREE", "sms_queue_view", "smsqueue", "dashboard.sms.queue", "SMS Queue", "exchange", communicationcenter);

					var cms = $menu.add("TREE", "templates_*", "cmstemplateasset", null, "CMS", "list");
					$menu.add("TREE", "templates_*", "cms-template-link", "dashboard.templates.domain", "CMS Templates", "list-alt", cms);
					$menu.add("TREE", "templates_*", "cms-images-link", "dashboard.cmsimages.domain", "CMS Images", "list-alt", cms);
					$menu.add("TREE", "web_asset_*", "cms-assets-link", "dashboard.cmsassets", "CMS Assets", "list-alt", cms);
					//$menu.add("TREE", "templates_*", "cms-builder-link", "dashboard.cmsbuilder", "CMS Builder Beta", "list-alt", cms);

					// var changelogs = $menu.add("TREE", "changelogs_*", "changelogs", "dashboard.changelogs", "Changelogs", "history", null);
					// $menu.add("TREE", "changelogs_*", "entries", "dashboard.changelogs.entries", "Entries", "list-alt", changelogs);
					var audit = $menu.add("TREE", "changelogs_*", "changelogs", "dashboard.changelogs", "Audit", "history", null);
					$menu.add("TREE", "changelogs_*", "entries", "dashboard.changelogs.entries", "Global Changelog", "list-alt", audit);

					var advancereporting = $menu.add("TREE", "report_*", "advancereporting", null, "Advance Reporting", "print");
					$menu.add("TREE", "report_players", "report_players", "dashboard.reports.players.list", "Players", "users", advancereporting);
					$menu.add("TREE", "report_games", "report_games", "dashboard.reports.games.list", "Games", "play-circle", advancereporting);

					$menu.add("TREE", "settlements_*", "settlements", "dashboard.settlements.batches.list", "Settlements", "usd", null);

					$menu.add("TREE", "products_*", "products", "dashboard.product", "Products", "product-hunt");
					var products = $menu.add("TREE", "products", "products", "dashboard.product", "Products", "product-hunt", null);
					$menu.add("TREE", "products", "list", "dashboard.product.list", "List", "cog", products);
					$menu.add("TREE", "products", "transactions", "dashboard.product.transactions", "Transactions", "exchange", products);

					var gogamegames = $menu.add("TREE", "gogamegames_*", "gogamegames", "dashboard.gogamegames", "GoGame Games", "play-circle", null);
					$menu.add("TREE", "gogamegames_engines_*", "engines", "dashboard.gogamegames.engines", "Engines", "cog", gogamegames);
					var reels = $menu.add("TREE", "gogamegames_reels_*", "reels", null, "Reels", "cogs", gogamegames);
					$menu.add("TREE", "gogamegames_reels_*", "configurations", "dashboard.gogamegames.reelconfigs", "Configurations", "cog", reels);
					$menu.add("TREE", "gogamegames_reels_*", "sets", "dashboard.gogamegames.reelsets", "Sets", "bars", reels);
					$menu.add("TREE", "gogamegames_math_models_*", "mathmodels", "dashboard.gogamegames.mathmodels", "Math Models", "calculator", gogamegames);
					$menu.add("TREE", "gogamegames_ledgers_*", "ledgers", "dashboard.gogamegames.ledgers", "Ledgers", "file-text-o", gogamegames);
					$menu.add("TREE", "gogamegames_result_batches_*", "resultbatches", "dashboard.gogamegames.resultbatches", "Result Batches", "object-group", gogamegames);
					$menu.add("TREE", "gogamegames_games_*", "games", "dashboard.gogamegames.games", "Games", "play-circle", gogamegames);
					$menu.add("TREE", "gogamegames_spins_*", "spins", "dashboard.gogamegames.spins", "Spins", "circle-o-notch", gogamegames);
					$menu.add("TREE", "gogamegames_dailygame_*", "dailygame", "dashboard.gogamegames.dailygame", "Daily Game", "gamepad", gogamegames);
					$menu.add("TREE", "gogamegames_uploadederrors_*", "uploadederrors", "dashboard.gogamegames.uploadederrors", "Uploaded Errors", "exclamation-triangle", gogamegames);
					$menu.add("TREE", "gogamegames_hotometers_*", "hotometers", "dashboard.gogamegames.hotometers", "Hot-O-Meters", "tachometer", gogamegames);
					$menu.add("TREE", "gogamegames_tutorials_*", "tutorials", "dashboard.gogamegames.tutorials", "Tutorials", "gamepad", gogamegames);
					$menu.add("TREE", "gogamegames_debugresults_*", "debugresults", "dashboard.gogamegames.debugresults", "Debug Results", "bug", gogamegames);
					$menu.add("TREE", "gogamegames_resultsimulations_*", "resultsimulations", "dashboard.gogamegames.resultsimulations", "Result Simulations", "bolt", gogamegames);
					$menu.add("TREE", "gogamegames_exhaustionrates_*", "exhaustionrates", "dashboard.gogamegames.exhaustionrates", "Exhaustion Rate", "area-chart", gogamegames);
					$menu.add("TREE", "gogamegames_windistributions_*", "windistributions", "dashboard.gogamegames.windistributions", "Win Distributions", "line-chart", gogamegames);

					var reports = $menu.add("TREE", "report_*", "reports", null, "Reports", "list", null);
					$menu.add("TREE", "report_incomplete_players", "report_incomplete_players", "dashboard.reports.incompleteplayers.list", "Incomplete Players", "user-times", reports);
					$menu.add("TREE", "report_ia", "report_ia", "dashboard.reports.ia.list", "Income Access", "handshake-o", reports);

					var notifications = $menu.add("TREE", "notifications_*", "notifications", "dashboard.notifications", "Notifications", "bell", null);
					$menu.add("TREE", "notifications_*", "notifications", "dashboard.notifications.notifications", "Notifications", "bell-o", notifications);
					$menu.add("TREE", "notifications_*", "inbox", "dashboard.notifications.inbox", "Inbox", "inbox", notifications);

					var pushmsg = $menu.add("TREE", "pushmsg_*", "pushmsg", "dashboard.pushmsg", "Push Messages", "comment", null);
//					$menu.add("TREE", "pushmsg_config", "config", "dashboard.pushmsg.config", "Configuration", "cog", pushmsg);
//					$menu.add("TREE", "pushmsg_queue_view", "queue", "dashboard.pushmsg.queue", "Queue", "exchange", pushmsg);

					const gamstop = $menu.add("TREE", "gamstop_*", "gamstop", null, "Responsible Gaming", "print");
					$menu.add("TREE", "gamstop_*", "gamstop_*", "dashboard.responsiblegaming.players.list", "Gamstop Report", "users", gamstop);

					var reports = $menu.add("TREE", "report_*", "reports", null, "Reports", "print");
					$menu.add("TREE", "report_players", "report_players", "dashboard.reports.players.list", "Players", "users", reports);
					$menu.add("TREE", "report_incomplete_players", "report_incomplete_players", "dashboard.reports.incompleteplayers.list", "Incomplete Players", "user-times", reports);
					$menu.add("TREE", "report_games", "report_games", "dashboard.reports.games.list", "Games", "play-circle", reports);
					$menu.add("TREE", "report_ia", "report_ia", "dashboard.reports.ia.list", "Income Access", "handshake-o", reports);

					var raf = $menu.add("TREE", "raf_*", "raf", "dashboard.raf", "Refer a Friend", "address-book", null);
					$menu.add("TREE", "raf_config", "config", "dashboard.raf.config", "Configuration", "cog", raf);
					$menu.add("TREE", "raf_clicks", "clicks", "dashboard.raf.clicks", "Clicks", "mouse-pointer", raf);
					$menu.add("TREE", "raf_referral", "signups", "dashboard.raf.signups", "Signups", "user-plus", raf);
					$menu.add("TREE", "raf_referral", "conversions", "dashboard.raf.conversions", "Conversions", "plus-square", raf);

					// var xpAndLeveling = $menu.add("TREE", "xp_schemes_*", "xp", "dashboard.xp", "XP and Leveling", "trophy", null);
					// $menu.add("TREE", "xp_schemes_*", "schemes", "dashboard.xp.schemes", "Schemes", "list", xpAndLeveling);
				}

				service._retrieveDomainSettings = function(domainName) {
					restDomain.findCurrentDomainSettings(domainName).then(function(response) {
						var settings = response.plain();
						var objSettings = {};
						for (var i = 0; i < settings.length; i++) {
							var dslv = settings[i];
							objSettings[dslv.labelValue.label.name] = dslv.labelValue.value;
						}
						$rootScope.settings = objSettings;
						// console.debug("$rootScope.settings", $rootScope.settings);
					}).catch(function(error) {
						notify.error($translate.instant('UI_NETWORK_ADMIN.DOMAIN.SETTINGS.ERRORS.COULD_NOT_RETRIEVE'));
						errors.catch('', false)(error)
					});
				}
				service._checkUserPrivileges = function() {
					if(!$security.playersDomain()){
						if ($rootScope.toState) {
							$state.go($rootScope.toState, $rootScope.toParams);
						} else {
							$state.go('dashboard.home-beta');
						}
					} else {
						service._authentication_fail();
						notify.error('User is not authorized to access platform');
					}
				}
				service._callSecurityServices = function() {
					$rootScope.principal = $security.init($rootScope.token);

//				$log.info("roles() :: "+service.roles());
//				$log.info("rolesForDomain(\"default\") :: "+service.rolesForDomain("default"));
//				$log.info("rolesForDomain(\"EPN\") :: "+service.rolesForDomain("EPN"));
//				$log.info("rolesForDomain(\"INT\") :: "+service.rolesForDomain("INT"));
//				$log.info("domainsWithRole(\"ADMIN\") :: "+service.domainsWithRole("ADMIN"));
//				$log.info("domainsWithRole(\"CREATE_USER\") :: "+service.domainsWithRole("CREATE_USER"));
//				$log.info("hasRole(\"ADMIN\") :: "+service.hasRole("ADMIN"));
//				$log.info("hasAdminRole() :: "+service.hasAdminRole());
//				$log.info("hasAdminRoleForDomain(\"INT\") :: "+service.hasAdminRoleForDomain("INT"));
//				$log.info("hasRoleForDomain(\"default\", \"ADMIN\") :: "+service.hasRoleForDomain("default", "ADMIN"));
//				$log.info("hasRoleForDomain(\"EPN\", \"CREATE_USER\") :: "+service.hasRoleForDomain("EPN", "CREATE_USER"));
//				$log.info("hasRoleForDomain(\"EPN\", \"EDIT_TRANSLATE\") :: "+service.hasRoleForDomain("EPN", "EDIT_TRANSLATE"));
//				$log.info("hasRoleForDomain(\"INT\", \"ADMIN\") :: "+service.hasRoleForDomain("INT", "ADMIN"));
//				$log.info("hasRoleForDomain(\"INT\", \"VIEW_USER\") :: "+service.hasRoleForDomain("INT", "VIEW_USER"));
//				$log.info("hasRoleForDomain(\"INT\", \"EDIT_USER\") :: "+service.hasRoleForDomain("INT", "EDIT_USER"));
				}
				service.roles = function() {
					return $security.roles();
				}
				service.rolesForDomain = function(domain) {
					return $security.rolesForDomain(domain);
				}
				service.domainsWithRole = function(role) {
					return $security.domainsWithRole(role);
				}
				service.domainsWithAnyRole = function(roles) {
					var domains = [];
					for (var r = 0; r < roles.length; r++) {
						domains = domains.concat(service.domainsWithRole(roles[r]));
					}
					return ($filter('unique')(domains, 'name')).sort();
				}
				service.playerDomainsWithAnyRole = function(roles) {
					var domains = [];
					for (var r = 0; r < roles.length; r++) {
						var domainsWithRole = service.domainsWithRole(roles[r]);
						angular.forEach(domainsWithRole, function(d) {
							if (d.pd === true) domains = domains.concat(d);
						});
					}

					return ($filter('unique')(domains, 'name')).sort();
				}

				service.hasRole = function(role) {
					return $security.hasRole(role) || $security.hasAdminRole();
				}
				service.hasAdminRole = function() {
					return $security.hasAdminRole();
				}
				service.hasAdminRoleForDomain = function(domain) {
					return $security.hasAdminRoleForDomain(domain);
				}
				service.hasRoleForDomain = function(domain, role) {
					return ($security.hasRoleForDomain(domain, role) || service.hasAdminRoleForDomain(domain));
				}

				service.isExperimentalFeatures = function() {
					return $security.isExperimentalFeatures();
				}

				service._authentication_success = function(token) {
					$rootScope.token = token;
					service.authenticated = true;
					$rootScope.authenticated = true;
					localStorage.setItem('lithium-oauth-token', $rootScope.token)
					localStorage.setItem('lithium-refresh-token', $rootScope.refreshToken)
					$.ajaxSetup({ // A better way is possible: https://github.com/l-lin/angular-datatables/issues/548
						beforeSend: function (xhr) {
							xhr.setRequestHeader('Authorization', 'Bearer ' + $rootScope.token);
						}
					});
				}

				service._authentication_fail = function() {
					$menu.destroy();
					$rootScope.token = null;
					$rootScope.refreshToken = null;
					$rootScope.principal = null;
					service.authenticated = false;
					$rootScope.authenticated = false;
					localStorage.removeItem('lithium-oauth-token')
					localStorage.removeItem('lithium-refresh-token')
					localStorage.setItem('expires_in', 0);
				}



				service._setupExpireWatcher = function(expiresIn, canExtend = true) {

					if(countdownModalOpened) {
						//Already counting down
						return;
					}

					// console.log('setting up expire watcher', expiresIn, canExtend);
					var warningTime = 60;
					var timeout = ((expiresIn-warningTime-1)*1000);

					if (timeout > MAX_TIMEOUT_VALUE) {
						timeout = 2147483647;
					}

					$timeout.cancel(callAtTimeoutModal)
					
					callAtTimeoutModal = $timeout(function() {
						var d = new Date();
						var currentTime = d.getTime();

						var currentSession = $rootScope.queryCurrentSession();

							var storedExpiresInTime = localStorage.getItem('expires_in');
							var storedupdatedBy = localStorage.getItem('updated_by');

						if (currentSession == storedupdatedBy) {
							// currently active tab
							// console.log('display popup - last active tab');
							// $timeout.cancel(callAtTimeoutModal);
							if (localStorage.getItem('lithium-oauth-token')) {
								callAtTimeout(warningTime, canExtend);
							}

						} else {
							if (currentTime > storedExpiresInTime) {
								if (localStorage.getItem('lithium-oauth-token')) {
									callAtTimeout(warningTime, canExtend);

										$timeout.cancel(callAtTimeoutModal);
										service.logout();
										location.reload();
										// notify.warning('You were logged out because your session expired on another tab. Please login again to proceed.');
									}
								} else {

									// console.log('delaying timeout - not yet expired - not active tab');

									$timeout.cancel(callAtTimeoutModal);
									service._setupExpireWatcher(expiresIn, canExtend);
								}
							} 

						}, timeout);
					}


					function callAtTimeout(warningTime, canExtend) {

						var countdownModal = $uibModal.open({
							backdrop : 'static',
							keyboard : false,
							animation: true,
							ariaLabelledBy: 'modal-title',
							ariaDescribedBy: 'modal-body',
							templateUrl: 'scripts/controllers/login/expire.html',
							controller: 'TokenExpireModal',
							controllerAs: 'controller',
							size: 'md',
							resolve: {
								timeout: function() { return warningTime; },
								canExtend: function() { return canExtend; },
								reset: function () {
									return () => {
										$timeout.cancel(callAtTimeoutModal);
									}
								},
								loadMyFiles: function($ocLazyLoad) {
									return $ocLazyLoad.load({
										name:'lithium',
										files: [ 'scripts/controllers/login/expire.js' ]
									})
								}
							}
						});

						countdownModal.opened.then(() => countdownModalOpened = true)
						countdownModal.result.then(() => {
							$rootScope.refreshInProgress = false
						},() => {
							countdownModalOpened = false;
						});
				}
				service._refreshToken = function() {
					var refreshToken = $rootScope.refreshToken;
					var clientauth = btoa("default/una:uNa@h4sANEWp4sswd");
					return $http({
						url: 'auth/token/refresh',
						method: 'POST',
						//skipAuthorization : false,
						headers: {
							"Authorization": "Basic " + clientauth,
							"Content-type": "application/x-www-form-urlencoded; charset=utf-8"
						},
						data: $.param({grant_type: "refresh_token", refresh_token: refreshToken})
					}).success(function (response, status, headers) {
						$rootScope.refreshInProgress = false;
						$rootScope.token = response.access_token;
						$rootScope.refreshToken = response.refresh_token;
						$timeout.cancel(callAtTimeoutModal);

						var d = new Date();
						var n = d.getTime();
						var updatedExpiresInTime = n + (response.expires_in * 1000);

						var currentSession = $rootScope.queryCurrentSession();
						localStorage.setItem('expires_in', updatedExpiresInTime);
						localStorage.setItem('updated_by', currentSession);
						localStorage.setItem('last_refresh', n);

						service._authentication_success(response.access_token)

						service._setupExpireWatcher(response.expires_in, true)

						// console.log(response.expires_in);
						// console.log(updatedExpiresInTime);

						return $rootScope.token;
					}).error(function (data) {
						$rootScope.refreshInProgress = false;
						$timeout.cancel(callAtTimeoutModal);
						service._authentication_fail();
						console.error(data);
					});
				}

				service.authProviders = function(domainName) {
					return restDomain.authProviders(domainName);
				}

				service.authenticate = function(credentials) {
					var deferred = $q.defer();

					setTimeout(function() {
						if (!credentials) {
							var tokenFromLocalStorage = localStorage.getItem('lithium-oauth-token');
							$rootScope.token = tokenFromLocalStorage;
							var refreshTokenFromLocalStorage = localStorage.getItem('lithium-refresh-token');
							$rootScope.refreshToken = refreshTokenFromLocalStorage;
							if (tokenFromLocalStorage) {
								$http.get('user').then(function(response) {
//								console.log("tokenFromCookie");
									service._authentication_success(tokenFromLocalStorage);

									var now = new Date();
									var secondsToExpire = Math.floor(($rootScope.queryCurrentSessionTimeout()-now.getTime())/1000)
									
									try {
										service._callSecurityServices();
										service._buildMenu();
										service._retrieveDomainSettings($security.domainName());
										deferred.resolve();
									} catch (error) {
										$log.error(error);
										service._authentication_fail();
										deferred.reject();
									};
								}, function () {
									service._authentication_fail();
									deferred.reject();
								});
							} else {
								deferred.reject();
							}
							return;
						};

						var clientauth = btoa("default/una:uNa@h4sANEWp4sswd");
						var req = {
							method: 'POST',
							url: "auth/backoffice/oauth/token",
							headers: {
								"Authorization": "Basic " + clientauth,
								"Content-type": "application/x-www-form-urlencoded; charset=utf-8"
							},
							data: $.param({ grant_type:"password", domain: credentials.domain, username: credentials.username, password: credentials.password })
						}
						$http(req).then(function(data) {
							if (data.status === 200) {
								$rootScope.refreshToken = data.data.refresh_token;
								service._authentication_success(data.data.access_token);
								try {
									service._callSecurityServices();
									service._buildMenu();
									service._checkUserPrivileges();
									deferred.resolve();
								} catch (error) {
									console.error(error);
									$log.error(error);
									service._authentication_fail();
									deferred.reject();
								}
							} else {
								service._authentication_fail();
								deferred.reject();
							}
						}, function(error) {
							service._authentication_fail();
							deferred.reject();
						});
					}, 100);
					return deferred.promise;
				}

				service.onSuccessfulAuth = function(accessToken, refreshToken) {
					$rootScope.refreshToken = refreshToken;
					service._authentication_success(accessToken);

					try {
						service._callSecurityServices();
						service._buildMenu();
						service._checkUserPrivileges();
					} catch (error) {
						console.error(error);
						$log.error(error);
						service._authentication_fail();
					}
				}

				service.onFailAuth = function() {
					service._authentication_fail();
				}

				service.logout = function() {
					service._authentication_fail();
				};
				service.extendSession = function() {
					service._refreshToken();
				};

				return service;
			} catch (err) {
				$log.error(err);
				throw err;
			}
		}
	]);
//.factory('$userServiceRequestInterceptor', ['$rootScope', '$log',
//	function($rootScope, $log) {
//		if ($rootScope.principal === null) {
//			$rootScope.logout();
//		} else {
//			var sessionInjector = {
//				request: function(config) {
//					if ($rootScope.token) {
//						config.headers['Authorization'] = "Bearer " + $rootScope.token;
//					}
//					return config;
//				}
//			};
//			return sessionInjector;
//		}
//	}
//])
//.config(["$httpProvider",
//	function ($httpProvider) {
//		try {
//			$httpProvider.interceptors.push('$userServiceRequestInterceptor');
//		} catch (err) {
//			alert('During userService.config: ' + err);
//			throw err;
//		}
//	}
//]);
