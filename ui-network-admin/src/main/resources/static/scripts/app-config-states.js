function statesConfig($stateProvider) {

	$stateProvider.state('login',{
		templateUrl:'scripts/controllers/login/login.html',
		url:'/login',
		data: {
			pageTitle: "UI_NETWORK_ADMIN.PAGE.TITLE.LOGIN",
		},
		controller:'login as controller',
		resolve: {
			loadMyFiles:function($ocLazyLoad) {
				return $ocLazyLoad.load({
					name:'lithium',
					files:['scripts/controllers/login/login.js']
				})
			}
		}
	});

	$stateProvider.state('dashboard', {
		url:'/dashboard',
		templateUrl: 'scripts/controllers/dashboard/main.html',
		resolve: {
			loadMyDirectives:function($ocLazyLoad){
				return $ocLazyLoad.load({
					name:'lithium',
					files:[
						'scripts/directives/security/security.js',
						'scripts/directives/header/header.js',
						'scripts/directives/sidebar/sidebar.js',
						'scripts/directives/lithiumbar/lithiumbar.js',
						'scripts/directives/litHeader/litHeader.js',
						'scripts/directives/errors/showErrors.js',
						'scripts/directives/sockjs/sockjs.js',
						'scripts/directives/dt/dt.js',
						'scripts/directives/dt2/dt2.js',
						'scripts/directives/accounting/graph/graph.js',
						'scripts/directives/accounting/history/history.js',
						'scripts/directives/kyc/kyc.js',
						'scripts/directives/casino/history/history.js',
						'scripts/directives/dashboard/summary/summary.js',
						'scripts/directives/dashboard/netsummary/netsummary.js',
						'scripts/directives/tooltip/tooltip.js',
						'scripts/directives/player/bonus/bonus.js',
						'scripts/directives/player/bonus/external-bonus.js',
						'scripts/directives/player/bonus/grantbonus.js',
						'scripts/directives/player/bonus/registerbonususersearch.js',
						'scripts/directives/player/unlockgame/unlockgameusersearch.js',
						'scripts/directives/player/accounting/balance.js',
						'scripts/directives/player/address/address.js',
						'scripts/directives/player/personal/personal.js',
						'scripts/directives/player/attributes/attributes.js',
						'scripts/directives/player/password/password.js',
						'scripts/directives/player/status/status.js',
						'scripts/directives/player/verificationstatus/verificationstatus.js',
						'scripts/directives/player/biometrics-status/biometrics-status.js',
						'scripts/directives/player/tag/tag.js',
						'scripts/directives/player/cooloff/cooloff.js',
						'scripts/directives/player/reality-check/reality-check.js',
						'scripts/directives/player/promooptout/promooptout.js',
						'scripts/directives/player/limits/limits.js',
						'scripts/directives/player/depositlimits/limits.js',
						'scripts/directives/player/balance-limit/balance-limit.js',
						'scripts/directives/player/play-time-limits/play-time-limits.js',
						'scripts/directives/player/self-exclusion-v2/self-exclusion.js',
						'scripts/directives/player/referral/referral.js',
						'scripts/directives/player/affiliate/affiliate.js',
						'scripts/directives/player/user-restrictions/user-restrictions.js',
						'scripts/directives/player/quick-actions/quick-actions.js',
						'scripts/directives/player/upload-shortcuts/upload-shortcuts.js',
						'scripts/directives/player/player-links/player-links.js',
						'scripts/directives/player/timeframe/timeframelimits.js',
						'scripts/directives/comment/comment.js',
						'scripts/directives/changelog/changelog.js',
						'scripts/directives/changelog/globalchangelog.js',
						'scripts/directives/changelog/changelogentry.js',
						'scripts/directives/changelog/setpriority.js',
						'scripts/directives/mail/mail.js',
						'scripts/directives/format.js',
						'scripts/directives/sms/sms.js',
						'scripts/directives/scheduler/scheduler.js',
						'scripts/directives/dashboard/balancesummary/balancesummary.js',
						'scripts/directives/dashboard/trantypesummary/trantypesummary.js',
						'scripts/directives/dashboard/summary/summaryforbalance.js',
						'scripts/directives/notifications/inbox/inbox.js',
						'scripts/directives/usermissions/usermission/usermission.js',
						'scripts/directives/incentivegames/bet/bet.js',
						'scripts/directives/incentivegames/bets/bets.js',
						'scripts/directives/sportsbook/bets/bets.js',
						'scripts/directives/dashboard/stats/stats.js',
						'scripts/directives/dashboard/stats/stat.js',
						'scripts/directives/cashier/transactions/list/list.js',
						'scripts/directives/cashier/transactions/overview/overview.js',
						'scripts/directives/cashier/transactions/bank-account-lookup/bank-account-lookup.js',
						'scripts/directives/bonuses/cashbonus/masscashbonuses.js',
						'scripts/directives/bonuses/generic/massgenericbonuses.js',
						'scripts/directives/bonuses/casinoChip/casinochipbonuslist.js',
						'scripts/directives/bonuses/instantReward/instantrewardbonuslist.js',
						'scripts/directives/bonuses/instantRewardFreespin/instantrewardfreespinbonuslist.js',
						'scripts/directives/bonuses/cashbonus/cashbonuslist.js',
						'scripts/directives/bonuses/freespin/freespinbonuslist.js',
						'scripts/directives/player/mass-update/mass-player-update.js',
						'scripts/directives/errormessages/errormessages.js',
						'scripts/directives/player/cruks/cruks.js',
						'scripts/directives/balancemovement/list.js',
						'scripts/directives/changelog/cms.js',
						'scripts/directives/bonuses/sportsfreebets/sportsfreebetslist.js',
						'scripts/directives/missions/category/index.js',
						'scripts/directives/player/player-protection/player-protection.js',
						'scripts/directives/player/player-protection/player-notification.js'

					]
				})
			}
		},
		ncyBreadcrumb: {
			skip: true // Never display this state in breadcrumb.
		},
		controller: ['$state', '$timeout', '$log', function($state, $timeout, $log) {
			if ($state.is("dashboard")) $state.go("dashboard.home-beta");
		}]
	});

	// $stateProvider.state('dashboard.home', {
	// 	url:'/home',
	// 	controller: 'home as controller',
	// 	templateUrl:'scripts/controllers/dashboard/home/home.html',
	// 	data: {
	// 		pageTitle: "UI_NETWORK_ADMIN.PAGE.TITLE.DASHBOARD",
	// 	},
	// 	resolve: {
	// 		loadMyFiles:function($ocLazyLoad) {
	// 			return $ocLazyLoad.load({
	// 				name:'lithium',
	// 				files: ['scripts/controllers/dashboard/home/home.js']
	// 			})
	// 		}
	// 	},
	// 	ncyBreadcrumb: {
	// 		label: "dashboard"
	// 	}
	// });

	$stateProvider.state('dashboard.home-beta', {
		url:'/home',
		controller: 'home-beta as controller',
		templateUrl:'scripts/controllers/dashboard/home-beta/home.html',
		data: {
			pageTitle: "UI_NETWORK_ADMIN.PAGE.TITLE.DASHBOARD_BETA",
		},
		resolve: {
			loadMyFiles:function($ocLazyLoad) {
				return $ocLazyLoad.load({
					name:'lithium',
					files: ['scripts/controllers/dashboard/home-beta/home.js']
				})
			}
		},
		ncyBreadcrumb: {
			label: "dashboard (BETA)"
		}
	});

	$stateProvider.state('dashboard.home-beta-v2', {
		url:'/home-beta-v2',
		controller: 'home-beta-v2 as controller',
		templateUrl:'scripts/controllers/dashboard/home-beta-v2/home.html',
		data: {
			pageTitle: "UI_NETWORK_ADMIN.PAGE.TITLE.DASHBOARD-BETA-V2",
		},
		resolve: {
			loadMyFiles:function($ocLazyLoad) {
				return $ocLazyLoad.load({
					name:'lithium',
					files: ['scripts/controllers/dashboard/home-beta-v2/home.js']
				})
			}
		},
		ncyBreadcrumb: {
			label: "dashboard (BETA) V2"
		}
	});


	$stateProvider.state('dashboard.403',{
		templateUrl:'templates/403.html',
		data: {
			pageTitle: "UI_NETWORK_ADMIN.PAGE.TITLE.ERROR_403",
		},
// url:'/403',
		ncyBreadcrumb: {
			skip: true // Never display this state in breadcrumb.
		},
	});

	$stateProvider.state('dashboard.404',{
		templateUrl:'templates/404.html',
		data: {
			pageTitle: "UI_NETWORK_ADMIN.PAGE.TITLE.ERROR_404",
		},
// url:'/404',
		ncyBreadcrumb: {
			skip: true // Never display this state in breadcrumb.
		},
	});

	$stateProvider.state('dashboard.414',{
		templateUrl:'templates/414.html',
		data: {
			pageTitle: "UI_NETWORK_ADMIN.PAGE.TITLE.ERROR_404",
		},
// url:'/414',
		ncyBreadcrumb: {
			skip: true // Never display this state in breadcrumb.
		},
	});

	$stateProvider.state('dashboard.500',{
		templateUrl:'templates/500.html',
		data: {
			pageTitle: "UI_NETWORK_ADMIN.PAGE.TITLE.ERROR_500",
		},
// url:'/500',
		ncyBreadcrumb: {
			skip: true // Never display this state in breadcrumb.
		},
	});

	$stateProvider.state('dashboard.games.demo', {
		url:'/games/demo',
		controller: 'gameDemoController as controller',
		templateUrl:'scripts/controllers/dashboard/games/demo/demo.html',
		resolve: {
			loadMyFiles:function($ocLazyLoad) {
				return $ocLazyLoad.load({
					name:'lithium',
					files: ['scripts/controllers/dashboard/games/demo/demo.js']
				})
			}
		}
	});

	$stateProvider.state('dashboard.profile', {
		url:'/profile',
		controller: 'ProfileController as controller',
		templateUrl:'scripts/controllers/dashboard/profile/profile.html',
		data: {
			pageTitle: "UI_NETWORK_ADMIN.PAGE.TITLE.PROFILE",
		},
		resolve: {
			user: ["ProfileRest", "errors", "$rootScope", "bsLoadingOverlayService", function(rest, errors, $rootScope, bsLoadingOverlayService) {
				$rootScope.referenceId = "global-loading-overlay";
				bsLoadingOverlayService.start({referenceId:$rootScope.referenceId});
				return rest.get().then(function(profile) {
					return profile;
				}).catch(function(error) {
					errors.catch("Could not retrieve profile information.", false)(error)
				}).finally(function () {
					bsLoadingOverlayService.stop({referenceId:$rootScope.referenceId});
				});
			}],
			loadMyFiles:function($ocLazyLoad) {
				return $ocLazyLoad.load({
					name:'lithium',
					files: ['scripts/controllers/dashboard/profile/profile.js']
				})
			}
		},
		ncyBreadcrumb: {
			label: 'profile'
		},
	});

	$stateProvider.state('dashboard.domains',{
		url:'/domains',
		templateUrl:'scripts/controllers/dashboard/domains/domains.html',
		controller: 'domainsController as controller',
		resolve: {
			loadMyFiles:function($ocLazyLoad) {
				return $ocLazyLoad.load({
					name:'lithium',
					files:['scripts/controllers/dashboard/domains/domains.js']
				})
			}
		},
		permission: ['DOMAIN_LIST','DOMAIN_VIEW','DOMAIN_EDIT'],
		redirectTo: "dashboard.domains.list"
	});
	$stateProvider.state('dashboard.ecosystems',{
		url:'/ecosystems',
		templateUrl:'scripts/controllers/dashboard/ecosystems/ecosystems.html',
		controller: 'ecosystemsController as controller',
		resolve: {
			loadMyFiles:function($ocLazyLoad) {
				return $ocLazyLoad.load({
					name:'lithium',
					files:['scripts/controllers/dashboard/ecosystems/ecosystems.js']
				})
			}
		},
		permission: 'SOME_DOMAIN_ROLE',
		redirectTo: "dashboard.ecosystems.list"
	});
	$stateProvider.state('dashboard.domainrelationship',{
		url:'/domainrelationship',
		templateUrl:'scripts/controllers/dashboard/domainrelationship/domainrelationship.html',
		controller: 'domainRelationshipController as controller',
		resolve: {
			loadMyFiles:function($ocLazyLoad) {
				return $ocLazyLoad.load({
					name:'lithium',
					files:['scripts/controllers/dashboard/domainrelationship/domainrelationship.js']
				})
			}
		},
		permission: 'SOME_DOMAIN_ROLE',
		redirectTo: "dashboard.domainrelationship.list"
	});
	$stateProvider.state('dashboard.domainrelationship.list',{
		url:'/list/:id',
		params: { selectedEcosystem: null },
		templateUrl:'scripts/controllers/dashboard/domainrelationship/list/list.html',
		controller: 'domainRelationshipListController as controller',
		resolve: {
			loadMyFiles:function($ocLazyLoad) {
				return $ocLazyLoad.load({
					name:'lithium',
					files:['scripts/controllers/dashboard/domainrelationship/list/list.js']
				})
			}
		},
		ncyBreadcrumb: {
			label: 'domains'
		},
		permission: ['DOMAIN_LIST','DOMAIN_VIEW','DOMAIN_EDIT']
	});
	$stateProvider.state('dashboard.domainrelationship.add',{
		url:'/add/:id',
		templateUrl:'scripts/controllers/dashboard/domainrelationship/add/add.html',
		controller:'domainRelationshipAddController as controller',
		resolve: {
			// relationship: function() { return},
			loadMyFiles:function($ocLazyLoad) {
				return $ocLazyLoad.load({
					name:'lithium',
					files:['scripts/controllers/dashboard/domainrelationship/add/add.js']
				})
			}
		},
		ncyBreadcrumb: {
			parent: 'dashboard.domainrelationship.list',
			label: 'add'
		},
		permission: ['DOMAIN_LIST','DOMAIN_VIEW','DOMAIN_EDIT']
	});
	$stateProvider.state('dashboard.ecosystems.list',{
		url:'/list',
		templateUrl:'scripts/controllers/dashboard/ecosystems/list/list.html',
		data: {
			pageTitle: "UI_NETWORK_ADMIN.PAGE.TITLE.ECOSYSTEMS",
		},
		controller:'ecosystemListController as controller',
		resolve: {
			ecosystems: ["EcosysRest", function(rest) {
				return rest.ecosystems().then(function (response){
					return response;
				});
			}],
			loadMyFiles:function($ocLazyLoad) {
				return $ocLazyLoad.load({
					name:'lithium',
					files:['scripts/controllers/dashboard/ecosystems/list/list.js']
				})
			}
		},
		ncyBreadcrumb: {
			label: 'domains'
		},
		permission: ['DOMAIN_LIST','DOMAIN_VIEW','DOMAIN_EDIT']
	});
	$stateProvider.state('dashboard.domains.list',{
		url:'/list',
		templateUrl:'scripts/controllers/dashboard/domains/list/list.html',
		data: {
			pageTitle: "UI_NETWORK_ADMIN.PAGE.TITLE.DOMAINS",
		},
		controller:'domainListController as controller',
		resolve: {
			domains: ["rest-domain", "$rootScope", function(rest, $rootScope) {
				return rest.children($rootScope.principal.domainName).then(function(domains) { return domains; });
			}],
			loadMyFiles:function($ocLazyLoad) {
				return $ocLazyLoad.load({
					name:'lithium',
					files:['scripts/controllers/dashboard/domains/list/list.js']
				})
			}
		},
		ncyBreadcrumb: {
			label: 'domains'
		},
		permission: ['DOMAIN_LIST','DOMAIN_VIEW','DOMAIN_EDIT']
	});
	$stateProvider.state('dashboard.domains.add',{
		url:'/add',
		templateUrl:'scripts/controllers/dashboard/domains/add/add.html',
		controller:'domainAdd as controller',
		resolve: {
			domains: ["rest-domain", "$rootScope", function(rest, $rootScope) {
				return rest.children($rootScope.principal.domainName).then(function(domains) { return domains; });
			}],
			loadMyFiles:function($ocLazyLoad) {
				return $ocLazyLoad.load({
					name:'lithium',
					files:['scripts/controllers/dashboard/domains/add/add.js']
				})
			}
		},
		ncyBreadcrumb: {
			parent: 'dashboard.domains.list',
			label: 'add'
		}
	});
	$stateProvider.state('dashboard.ecosystems.add',{
		url:'/add',
		templateUrl:'scripts/controllers/dashboard/ecosystems/add/add.html',
		controller:'ecosystemAdd as controller',
		resolve: {
			ecosystems: function() { return},
			loadMyFiles:function($ocLazyLoad) {
				return $ocLazyLoad.load({
					name:'lithium',
					files:['scripts/controllers/dashboard/ecosystems/add/add.js']
				})
			}
		},
		ncyBreadcrumb: {
			parent: 'dashboard.ecosystems.list',
			label: 'add'
		}
	});
	$stateProvider.state('dashboard.ecosystems.view',{
		url:'/view/:id',
		params: { selectedEcosystem: null },
		templateUrl:'scripts/controllers/dashboard/ecosystems/view/view.html',
		controller:'ecosystemViewController as controller',
		resolve: {
			ecosystems: function() { return},
			loadMyFiles:function($ocLazyLoad) {
				return $ocLazyLoad.load({
					name:'lithium',
					files:['scripts/controllers/dashboard/ecosystems/view/view.js']
				})
			}
		},
		ncyBreadcrumb: {
			parent: 'dashboard.ecosystems.list',
			label: 'view'
		}
	});
	$stateProvider.state('dashboard.domains.domain',{
		templateUrl:'scripts/controllers/dashboard/domains/domain/domain.html',
		url:'/domain/:domainName',
		controller:'Domain as controller',
		resolve: {
			domain: ["rest-domain", "$stateParams", function(rest, $stateParams) {
				return rest.view($stateParams.domainName).then(function(domain) {
					return domain;
				});
			}],
			loadMyFiles:function($ocLazyLoad) {
				return $ocLazyLoad.load({
					name:'lithium',
					files:['scripts/controllers/dashboard/domains/domain/domain.js']
				})
			}
		},
		ncyBreadcrumb: {
			parent: 'dashboard.domains.list',
			label: '{{ $stateParams.domainName }}'
		}
	});

	// $stateProvider.state('dashboard.domains.domain.roles',{
	// 	templateUrl:'scripts/controllers/dashboard/domains/domain/roles/view.html',
	// 	url:'/roles',
	// 	controller:'DomainRoles as controller',
	// 	resolve: {
	// 		loadMyFiles:function($ocLazyLoad) {
	// 			return $ocLazyLoad.load({
	// 				name:'lithium',
	// 				files:['scripts/controllers/dashboard/domains/domain/roles/view.js']
	// 			})
	// 		}
	// 	},
	// 	ncyBreadcrumb: { label: "default roles" }
	// });

	$stateProvider.state('dashboard.domains.domain.limits',{
		templateUrl:'scripts/controllers/dashboard/domains/domain/limits/limits.html',
		url:'/limits',
		controller:'domainLimits as controller',
		resolve: {
			loadMyFiles:function($ocLazyLoad) {
				return $ocLazyLoad.load({
					name:'lithium',
					files:['scripts/controllers/dashboard/domains/domain/limits/limits.js']
				})
			}
		},
		ncyBreadcrumb: { label: "domain limits" }
	});

	$stateProvider.state('dashboard.domains.domain.currencies',{
		template: "<div style=\"margin: 10px;\" ui-view></div>",
		url:'/currencies',
		resolve: {
			loadMyFiles:function($ocLazyLoad) {
				return $ocLazyLoad.load({
					name:'lithium',
					files:
						[
							'scripts/controllers/dashboard/domains/domain/currencies/list/list.js',
							'scripts/controllers/dashboard/domains/domain/currencies/add/add.js'
						]
				})
			}
		},
		ncyBreadcrumb: { skip: true },
		redirectTo: "dashboard.domains.domain.currencies.list"
	});

	$stateProvider.state('dashboard.domains.domain.cataboomcampaigns',{
		template: "<div style=\"margin: 10px;\" ui-view></div>",
		url:'/cataboomcampaigns',
		resolve: {
			loadMyFiles:function($ocLazyLoad) {
				return $ocLazyLoad.load({
					name:'lithium',
					files:
						[
							'scripts/controllers/dashboard/domains/domain/cataboomcampaigns/list/list.js',
							'scripts/controllers/dashboard/domains/domain/cataboomcampaigns/add/add.js'
						]
				})
			}
		},
		ncyBreadcrumb: { skip: true },
		redirectTo: "dashboard.domains.domain.cataboomcampaigns.list"
	});

	$stateProvider.state('dashboard.domains.domain.currencies.list',{
		templateUrl: 'scripts/controllers/dashboard/domains/domain/currencies/list/list.html',
		url: '',
		controller: 'DomainCurrenciesListController as controller',
		ncyBreadcrumb: {
			parent: 'dashboard.domains.domain',
			label: 'currencies'
		}
	});

	$stateProvider.state('dashboard.domains.domain.cataboomcampaigns.list',{
		templateUrl: 'scripts/controllers/dashboard/domains/domain/cataboomcampaigns/list/list.html',
		url: '',
		controller: 'DomainCataboomListController as controller',
		ncyBreadcrumb: {
			parent: 'dashboard.domains.domain',
			label: 'cataboomcampaigns'
		}
	});


	$stateProvider.state('dashboard.domains.domain.currencies.currency',{
		templateUrl: 'scripts/controllers/dashboard/domains/domain/currencies/view/view.html',
		url: '/:id/view',
		controller: 'DomainCurrencyViewController as controller',
		resolve: {
			domainCurrency: ["rest-accounting-internal", "$stateParams", function(rest, $stateParams) {
				return rest.viewCurrency($stateParams.domainName, $stateParams.id);
			}],
			loadMyFiles:function($ocLazyLoad) {
				return $ocLazyLoad.load({
					name:'lithium',
					files:['scripts/controllers/dashboard/domains/domain/currencies/view/view.js']
				})
			}
		}
	});


	$stateProvider.state('dashboard.domains.domain.cataboomcampaigns.cataboomcampaign',{
		templateUrl: 'scripts/controllers/dashboard/domains/domain/cataboomcampaigns/view/view.html',
		url: '/:id/view',
		controller: 'DomainCataboomViewController as controller',
		resolve: {
			campaign: ["rest-cataboom-campaign", "$stateParams", function(rest, $stateParams) {
				return rest.viewCampaign($stateParams.domainName, $stateParams.id);
			}],
			loadMyFiles:function($ocLazyLoad) {
				return $ocLazyLoad.load({
					name:'lithium',
					files:['scripts/controllers/dashboard/domains/domain/cataboomcampaigns/view/view.js']
				})
			}
		}
	});

	$stateProvider.state('dashboard.domains.domain.clients',{
		template: "<div style=\"margin: 10px;\" ui-view></div>",
		url:'/clients',
		resolve: {
			loadMyFiles:function($ocLazyLoad) {
				return $ocLazyLoad.load({
					name:'lithium',
					files:
						[
							'scripts/controllers/dashboard/domains/domain/clients/list/list.js',
							'scripts/controllers/dashboard/domains/domain/clients/add/add.js'
						]
				})
			}
		},
		ncyBreadcrumb: { skip: true },
		redirectTo: "dashboard.domains.domain.clients.list"
	});

	$stateProvider.state('dashboard.domains.domain.clients.list',{
		templateUrl: 'scripts/controllers/dashboard/domains/domain/clients/list/list.html',
		url: '',
		controller: 'DomainClientsListController as controller',
		ncyBreadcrumb: {
			parent: 'dashboard.domains.domain',
			label: 'clients'
		}
	});

	$stateProvider.state('dashboard.domains.domain.clients.client',{
		templateUrl: 'scripts/controllers/dashboard/domains/domain/clients/view/view.html',
		url: '/:id/view',
		controller: 'DomainClientsViewController as controller',
		resolve: {
			client: ["rest-provider-auth-client", "$stateParams", function(rest, $stateParams) {
				return rest.find($stateParams.domainName, $stateParams.id);
			}],
			loadMyFiles:function($ocLazyLoad) {
				return $ocLazyLoad.load({
					name:'lithium',
					files:['scripts/controllers/dashboard/domains/domain/clients/view/view.js']
				})
			}
		}
	});

	$stateProvider.state('dashboard.domains.domain.avatars',{
		template: "<div style=\"margin: 10px;\" ui-view></div>",
		url:'/avatars',
		resolve: {
			loadMyFiles:function($ocLazyLoad) {
				return $ocLazyLoad.load({
					name:'lithium',
					files:
						[
							'scripts/controllers/dashboard/domains/domain/avatars/list/list.js',
							'scripts/controllers/dashboard/domains/domain/avatars/add/add.js'
						]
				})
			}
		},
		ncyBreadcrumb: { skip: true },
		redirectTo: "dashboard.domains.domain.avatars.list"
	});

	$stateProvider.state('dashboard.domains.domain.avatars.list',{
		templateUrl: 'scripts/controllers/dashboard/domains/domain/avatars/list/list.html',
		url: '',
		controller: 'DomainAvatarsListController as controller',
		ncyBreadcrumb: {
			parent: 'dashboard.domains.domain',
			label: 'avatars'
		}
	});

	$stateProvider.state('dashboard.domains.domain.avatars.avatar',{
		templateUrl: 'scripts/controllers/dashboard/domains/domain/avatars/view/view.html',
		url: '/:id/view',
		controller: 'DomainAvatarViewController as controller',
		resolve: {
			avatar: ["AvatarRest", "$stateParams", function(rest, $stateParams) {
				return rest.view($stateParams.domainName, $stateParams.id);
			}],
			loadMyFiles:function($ocLazyLoad) {
				return $ocLazyLoad.load({
					name:'lithium',
					files:['scripts/controllers/dashboard/domains/domain/avatars/view/view.js']
				})
			}
		}
	});

	$stateProvider.state('dashboard.domains.domain.settings', {
		templateUrl: "scripts/controllers/dashboard/domains/domain/settings/settings.html",
		url:'/settings',
		controller: 'DomainSettingsController as controller',
		resolve: {
			loadMyFiles:function($ocLazyLoad) {
				return $ocLazyLoad.load({
					name:'lithium',
					files: [
						'scripts/controllers/dashboard/domains/domain/settings/settings.js',
						'scripts/controllers/dashboard/domains/domain/settings/view/view.js',
						'scripts/controllers/dashboard/domains/domain/settings/add/add.js'
					]
				})
			}
		},
		ncyBreadcrumb: { skip: true }
	});

	$stateProvider.state('login.password-reset.password-reset', {
		templateUrl: "scripts/controllers/login/password-reset/password-reset.html",
		url:'/passwordReset',
		controller: 'PasswordController as controller',
		resolve: {
			loadMyFiles:function($ocLazyLoad) {
				return $ocLazyLoad.load({
					name:'lithium',
					files: [
						'scripts/controllers/login/password-reset/password-reset.js',
					]
				})
			}
		},
		ncyBreadcrumb: { skip: true }
	});

	$stateProvider.state('login.password-reset.password-resetPt2', {
		templateUrl: "scripts/controllers/login/password-reset/password-resetPt2.html",
		url:'/passwordResetPt2',
		controller: 'PasswordPt2Controller as controller',
		resolve: {
			loadMyFiles:function($ocLazyLoad) {
				return $ocLazyLoad.load({
					name:'lithium',
					files: [
						'scripts/controllers/login/password-reset/password-resetPt2.js',
					]
				})
			}
		},
		ncyBreadcrumb: { skip: true }
	});

	$stateProvider.state('dashboard.domains.domain.settings.view', {
		templateUrl: "scripts/controllers/dashboard/domains/domain/settings/view/view.html",
		url:'/view/{domainRevisionId}',
		controller: 'DomainSettingsViewController as controller',
		resolve: {
			domainRevision: ["rest-domain", "$stateParams", function(rest, $stateParams) {
				if ($stateParams.domainRevisionId === '-1') return null;
				return rest.findDomainRevision($stateParams.domainName, $stateParams.domainRevisionId);
			}]
		},
		ncyBreadcrumb: { skip: true }
	});

	$stateProvider.state('dashboard.domains.domain.settings.history', {
		template:'<div ui-view></div>',
		url:'/history',
		redirectTo: "dashboard.domains.domain.settings.history.list"
	});

	$stateProvider.state('dashboard.domains.domain.settings.history.list', {
		templateUrl:'scripts/controllers/dashboard/domains/domain/settings/history/list/list.html',
		url:'/list',
		controller:'DomainSettingsHistoryController as controller',
		resolve: {
			loadMyFiles:function($ocLazyLoad) {
				return $ocLazyLoad.load({
					name:'lithium',
					files: [
						'scripts/controllers/dashboard/domains/domain/settings/history/list/list.js'
					]
				})
			}
		}
	});

	$stateProvider.state('dashboard.domains.domain.trantypeaccounts',{
		templateUrl:'scripts/controllers/dashboard/domains/domain/transactiontypeaccounts/view.html',
		url:'/trantypeaccounts',
		controller:'TransactionTypeAccounts as controller',
		resolve: {
			loadMyFiles:function($ocLazyLoad) {
				return $ocLazyLoad.load({
					name:'lithium',
					files:['scripts/controllers/dashboard/domains/domain/transactiontypeaccounts/view.js']
				})
			}
		},
		ncyBreadcrumb: { label: "transaction type accounts" }
	});

	$stateProvider.state('dashboard.domains.domain.limitsystemaccess',{
		templateUrl:'scripts/controllers/dashboard/domains/domain/limitsystemaccess/view.html',
		url:'/limitsystemaccess',
		controller:'LimitSystemAccess as controller',
		resolve: {
			loadMyFiles:function($ocLazyLoad) {
				return $ocLazyLoad.load({
					name:'lithium',
					files:['scripts/controllers/dashboard/domains/domain/limitsystemaccess/view.js']
				})
			}
		},
		ncyBreadcrumb: { label: "limit system access" }
	});

	$stateProvider.state('dashboard.domains.domain.closurereasons',{
		template: "<div style=\"margin: 10px;\" ui-view></div>",
		url:'/closurereasons',
		resolve: {
			loadMyFiles:function($ocLazyLoad) {
				return $ocLazyLoad.load({
					name:'lithium',
					files:
						[
							'scripts/controllers/dashboard/domains/domain/closurereasons/list/list.js',
							'scripts/controllers/dashboard/domains/domain/closurereasons/add/add.js'
						]
				})
			}
		},
		ncyBreadcrumb: { skip: true },
		redirectTo: "dashboard.domains.domain.closurereasons.list"
	});

	$stateProvider.state('dashboard.domains.domain.closurereasons.list',{
		templateUrl: 'scripts/controllers/dashboard/domains/domain/closurereasons/list/list.html',
		url: '',
		controller: 'DomainClosureReasonsListController as controller',
		ncyBreadcrumb: {
			parent: 'dashboard.domains.domain',
			label: 'closurereasons'
		}
	});

	$stateProvider.state('dashboard.domains.domain.closurereasons.view',{
		templateUrl: 'scripts/controllers/dashboard/domains/domain/closurereasons/view/view.html',
		url: '/:id/view',
		controller: 'DomainClosureReasonsViewController as controller',
		resolve: {
			closurereason: ["ClosureReasonsRest", "$stateParams", function(rest, $stateParams) {
				return rest.find($stateParams.domainName, $stateParams.id);
			}],
			loadMyFiles:function($ocLazyLoad) {
				return $ocLazyLoad.load({
					name:'lithium',
					files:['scripts/controllers/dashboard/domains/domain/closurereasons/view/view.js']
				})
			}
		}
	});

	$stateProvider.state('dashboard.domains.domain.view',{
		templateUrl:'scripts/controllers/dashboard/domains/domain/view/view.html',
		url:'/view',
		controller:'DomainView as controller',
		resolve: {
			loadMyFiles:function($ocLazyLoad) {
				return $ocLazyLoad.load({
					name:'lithium',
					files:['scripts/controllers/dashboard/domains/domain/view/view.js']
				})
			}
		},
		ncyBreadcrumb: { label: "view" }
	});

	$stateProvider.state('dashboard.domains.domain.users',{
		template: "<div style=\"margin: 10px;\" ui-view></div>",
		url:'/users',
		resolve: {
			groups:["rest-group", "domain", function(rest, domain) {
				return rest.list(domain.name).then(function(groups) { return groups; });
			}],
			loadMyFiles:function($ocLazyLoad) {
				return $ocLazyLoad.load({
					name:'lithium',
					files:
						[
							'scripts/controllers/dashboard/users/list/list.js',
							'scripts/controllers/dashboard/users/add/add.js'
						]
				})
			}
		},
		ncyBreadcrumb: { skip: true },
		redirectTo: "dashboard.domains.domain.users.list"
	});

	$stateProvider.state('dashboard.domains.domain.users.list',{
		templateUrl: 'scripts/controllers/dashboard/users/list/list.html',
		url: '',
		controller: 'UsersList as controller',
		ncyBreadcrumb: {
			parent: 'dashboard.domains.domain',
			label: 'users'
		}
	});

	$stateProvider.state('dashboard.domains.domain.users.user',{
		templateUrl: 'scripts/controllers/dashboard/users/user/user.html',
		url:'/:id',
		controller: 'UserController as controller',
		resolve: {
			user: ["domain", "UserRest", "$stateParams", function(domain, rest, $stateParams) {
				return rest.findById(domain.name, $stateParams.id);
			}],
			groups:["rest-group", "domain", function(rest, domain) {
				return rest.list(domain.name).then(function(groups) { return groups; });
			}],
			loadMyFiles:function($ocLazyLoad) {
				return $ocLazyLoad.load({
					name:'lithium',
					files:
						[
							'scripts/controllers/dashboard/users/user/user.js'
						]
				})
			}
		},
		ncyBreadcrumb: { skip: true },
		redirectTo: 'dashboard.domains.domain.users.user.view'
	});

	$stateProvider.state('dashboard.domains.domain.users.user.view',{
		templateUrl: 'scripts/controllers/dashboard/users/user/view/view.html',
		url: '/view',
		controller: 'UserView as controller',
		resolve: {
			domainSettings: ['rest-domain', '$stateParams', '$security', function (domainRest, $stateParams, $security) {
				return domainRest.findCurrentDomainSettings($stateParams.domainName).then(function(response) {
					var settings = response.plain();
					var objSettings = {};
					var viewCountry = $security.domainsWithRole("IBAN_VIEW").length > 0;
					for (var i = 0; i < settings.length; i++) {
						var dslv = settings[i];
						if (dslv.labelValue.label.name == 'iban') {
							if (viewCountry) {
								objSettings[dslv.labelValue.label.name] = dslv.labelValue.value;
							}
						} else {
							objSettings[dslv.labelValue.label.name] = dslv.labelValue.value;
						}
					}
					return objSettings;
				});
			}],
			loadMyFiles:function($ocLazyLoad) {
				return $ocLazyLoad.load({

					name:'lithium',
					files:
						[
							'scripts/controllers/dashboard/users/user/view/view.js'
						]
				})
			}
		},
		ncyBreadcrumb: {
			parent: 'dashboard.domains.domain',
			label: 'users'
		}
	});

	$stateProvider.state('dashboard.domains.domain.users.user.loginevents',{
		templateUrl: 'scripts/controllers/dashboard/users/user/loginevents/loginevents.html',
		url:'/loginevents',
		controller:'UserLoginEventsController as controller',
		resolve: {
			loadMyFiles:function($ocLazyLoad) {
				return $ocLazyLoad.load({
					name:'lithium',
					files:[ 'scripts/controllers/dashboard/users/user/loginevents/loginevents.js' ]
				})
			}
		},
		ncyBreadcrumb: { label: 'login history' }
	});





	$stateProvider.state('dashboard.domains.domain.emailtemplates',{
		template: "<div ui-view></div>",
		url:'/emailtemplates',
		resolve: {
			loadMyFiles:function($ocLazyLoad) {
				return $ocLazyLoad.load({
					name:'lithium',
					files:
						[
							'scripts/controllers/dashboard/emailtemplates/list/list.js',
							'scripts/controllers/dashboard/emailtemplates/crud/crud.js'
						]
				})
			}
		},
		ncyBreadcrumb: { skip: true },
		redirectTo: "dashboard.domains.domain.emailtemplates.list"
	});

	$stateProvider.state('dashboard.domains.domain.emailtemplates.list',{
		templateUrl: 'scripts/controllers/dashboard/emailtemplates/list/list.html',
		url: '',
		controller: 'EmailTemplates as controller',
		ncyBreadcrumb: {
			parent: 'dashboard.domains.domain',
			label: 'email templates'
		}
	});

	$stateProvider.state('dashboard.domains.domain.emailtemplates.add',{
		templateUrl: 'scripts/controllers/dashboard/emailtemplates/crud/crud.html',
		url: '/add',
		controller: 'EmailTemplate as controller',
		resolve: {
			template: function() { return {} }
		},
		ncyBreadcrumb: {
			parent: 'dashboard.domains.domain',
			label: 'email templates'
		}
	});

	$stateProvider.state('dashboard.domains.domain.emailtemplates.view',{
		templateUrl: 'scripts/controllers/dashboard/emailtemplates/crud/crud.html',
		url: '/:id/view',
		controller: 'EmailTemplate as controller',
		resolve: {
			template: ["EmailTemplateRest", "$stateParams", function(rest, $stateParams) {
				return rest.view($stateParams.id);
			}],
		},
		ncyBreadcrumb: {
			parent: 'dashboard.domains.domain',
			label: 'email templates'
		}
	});

	$stateProvider.state('dashboard.domainEdit',{
		templateUrl:'scripts/controllers/dashboard/domains/edit/edit.html',
		url:'/domain/:domainName/edit',
		controller:'domainEdit as controller',
		resolve: {
			loadMyFiles:function($ocLazyLoad) {
				return $ocLazyLoad.load({
					name:'lithium',
					files:['scripts/controllers/dashboard/domains/edit/edit.js']
				})
			}
		},
		ncyBreadcrumb: {
			parent: 'dashboard.domains',
			label: 'edit {{domain.name}}'
		}
	});

	$stateProvider.state('dashboard.languages',{
		templateUrl:'scripts/controllers/dashboard/languages/list/list.html',
		data: {
			pageTitle: "UI_NETWORK_ADMIN.PAGE.TITLE.LANGUAGES",
		},
		url:'/languages',
		controller:'languages as controller',
		resolve: {
			loadMyFiles:function($ocLazyLoad) {
				return $ocLazyLoad.load({
					name:'lithium',
					files:['scripts/controllers/dashboard/languages/list/list.js']
				})
			}
		},
		ncyBreadcrumb: {
			label: 'languages'
		}
	});

	$stateProvider.state('dashboard.translations',{
		templateUrl:'scripts/controllers/dashboard/translations/list/list.html',
		data: {
			pageTitle: "UI_NETWORK_ADMIN.PAGE.TITLE.TRANSLATIONS",
		},
		url:'/translations',
		controller:'translations as controller',
		resolve: {
			loadMyFiles:function($ocLazyLoad) {
				return $ocLazyLoad.load({
					name:'lithium',
					files:['scripts/controllers/dashboard/translations/list/list.js']
				})
			}
		},
		ncyBreadcrumb: {
			label: 'translations'
		}
	});

	$stateProvider.state('dashboard.domains.domain.providers',{
		template: "<div ui-view></div>",
		url:'/dproviders',
		resolve: {
			loadMyFiles:function($ocLazyLoad) {
				return $ocLazyLoad.load({
					name:'lithium',
					files: [
						'scripts/controllers/dashboard/providers/list/list.js',
						'scripts/controllers/dashboard/providers/add/add.js'
					]
				})
			}
		},
		ncyBreadcrumb: {
			label: 'providers'
		},
		params: {
			domainName: ["$rootScope", function($rootScope) {
				return $rootScope.principal.domainName;
			}],
		},
		redirectTo: "dashboard.domains.domain.providers.list"
	});
	$stateProvider.state('dashboard.domains.domain.providers.list',{
		templateUrl: 'scripts/controllers/dashboard/providers/list/list.html',
		url: '/list',
		controller: 'providersList as controller',
		ncyBreadcrumb: {
			skip: true
		}
	});

	$stateProvider.state('dashboard.domains.domain.providers.add',{
		templateUrl: 'scripts/controllers/dashboard/providers/add/add.html',
		url: '/add',
		controller:'providerAdd as controller',
		ncyBreadcrumb: {
			parent: 'dashboard.domains.domain.providers.list',
			label: 'add'
		}
	});

	$stateProvider.state('dashboard.domains.domain.groups',{
		template: "<div style=\"margin: 10px;\" ui-view></div>",
		url:'/dgroups',
		resolve: {
			groups:["rest-group", "domain", function(rest, domain) {
				rest.list(domain.name).then(function(groups) { return groups; });
			}],
			loadMyFiles:function($ocLazyLoad) {
				return $ocLazyLoad.load({
					name:'lithium',
					files: [
						'scripts/controllers/dashboard/groups/list/list.js',
						'scripts/controllers/dashboard/groups/add/add.js'
					]
				})
			}
		},
		ncyBreadcrumb: {
			label: 'groups'
		},
		redirectTo: "dashboard.domains.domain.groups.list"
	});

	$stateProvider.state('dashboard.domains.domain.groups.list',{
		templateUrl: 'scripts/controllers/dashboard/groups/list/list.html',
		url: '/list',
		controller: 'groupListController as controller',
		ncyBreadcrumb: {
			skip: true
		}
	});

	$stateProvider.state('dashboard.domains.domain.groups.add',{
		templateUrl: 'scripts/controllers/dashboard/groups/add/add.html',
		url: '/add',
		controller:'groupAdd as controller',
		ncyBreadcrumb: {
			parent: 'dashboard.domains.domain.groups',
			label: 'add'
		}
	});

	$stateProvider.state('dashboard.domains.domain.groups.group',{
		templateUrl: 'scripts/controllers/dashboard/groups/group/group.html',
		url: '/group/{groupId:int}',
		controller:'Group as controller',
		resolve: {
			group: ["rest-group", "$stateParams", function(rest, $stateParams) {
				var g = rest.view($stateParams.domainName, $stateParams.groupId);
				return g;
			}],
			tabs: function() {
				return [
					{ name: "dashboard.domains.domain.groups.group.view", title: "Info", roles: "GROUP_VIEW,GROUP_EDIT" },
					{ name: "dashboard.domains.domain.groups.group.roles", title: "Roles", roles: "GROUP_ROLES_VIEW,GROUP_ROLES_EDIT" },
					{ name: "dashboard.domains.domain.groups.group.users", title: "Users", roles: "GROUP_USERS_VIEW,GROUP_USERS_EDIT" }
				];
			},
			loadMyFiles:function($ocLazyLoad) {
				return $ocLazyLoad.load({
					name:'lithium',
					files: [
						'scripts/controllers/dashboard/groups/group/group.js',
						'scripts/controllers/dashboard/groups/group/view/view.js',
						'scripts/controllers/dashboard/groups/group/roles/roles.js',
						'scripts/controllers/dashboard/groups/group/users/users.js'
					]
				})
			}
		},
		ncyBreadcrumb: {
			label: '{{ $state.params.groupName }}'
		},
		redirectTo: "dashboard.domains.domain.groups.group.view"
	});

	$stateProvider.state('dashboard.domains.domain.groups.group.view',{
		templateUrl: 'scripts/controllers/dashboard/groups/group/view/view.html',
		url: '/view',
		controller:'GroupView as controller',
		ncyBreadcrumb: {
			parent: 'dashboard.domains.domain.groups.group',
			label: 'view'
		}
	});

	$stateProvider.state('dashboard.domains.domain.groups.group.roles',{
		templateUrl: 'scripts/controllers/dashboard/groups/group/roles/roles.html',
		url: '/roles',
		controller:'GroupViewRoles as controller',
		ncyBreadcrumb: {
			parent: 'dashboard.domains.domain.groups.group',
			label: 'roles'
		}
	});

	$stateProvider.state('dashboard.domains.domain.groups.group.users',{
		templateUrl: 'scripts/controllers/dashboard/groups/group/users/users.html',
		url: '/users',
		controller:'GroupViewUsers as controller',
		ncyBreadcrumb: {
			parent: 'dashboard.domains.domain.groups.group',
			label: 'users'
		}
	});

	$stateProvider.state('dashboard.groups',{
		abstract: true,
		url:'/domain/{domainName}/groups',
		resolve: {
			domain: ["$security", "$stateParams", function(security, $stateParams) {
				return security.domain($stateParams.domainName);
			}]
		},
		params: {
			domainName: ["$rootScope", function($rootScope) {
				return $rootScope.principal.domainName;
			}],
		},
		templateUrl:'scripts/controllers/dashboard/groups/groups.html'
	});

	$stateProvider.state('dashboard.groups.list',{
		url:'/list',
		templateUrl:'scripts/controllers/dashboard/groups/list/list.html',
		controller: 'groupListController as controller',
		resolve: {
			loadMyFiles:function($ocLazyLoad) {
				return $ocLazyLoad.load({
					name:'lithium',
					files:['scripts/controllers/dashboard/groups/list/list.js']
				})
			}
		},
		ncyBreadcrumb: {
			label: 'groups ({{ $stateParams.domainName }})'
		}
	});

	$stateProvider.state('dashboard.groups.add',{
		templateUrl:'scripts/controllers/dashboard/groups/add/add.html',
		url: '/add',
		controller:'groupAdd as controller',
		resolve: {
			loadMyFiles:function($ocLazyLoad) {
				return $ocLazyLoad.load({
					name:'lithium',
					files:['scripts/controllers/dashboard/groups/add/add.js']
				})
			}
		},
		ncyBreadcrumb: {
			parent: 'dashboard.groups.list',
			label: 'add'
		}
	});

	$stateProvider.state('dashboard.groups.group',{
		templateUrl:'scripts/controllers/dashboard/groups/group/group.html',
		url: '/group/{groupId:int}',
		controller:'Group as controller',
		resolve: {
			group: ["rest-group", "$state", "$stateParams", function(rest, $state, $stateParams) {
				var g = rest.view($stateParams.domainName, $stateParams.groupId);
				g.then(function(group) {
					$state.params.groupName = group.name;
				});
				return g;
			}],
			tabs: function() {
				return [
					{ name: "dashboard.groups.group.view", title: "Info", roles: "GROUP_VIEW,GROUP_EDIT" },
					{ name: "dashboard.groups.group.roles", title: "Roles", roles: "GROUP_ROLES_VIEW,GROUP_ROLES_EDIT" },
					{ name: "dashboard.groups.group.users", title: "Users", roles: "GROUP_USERS_VIEW,GROUP_USERS_EDIT" }
				];
			},
			loadMyFiles:function($ocLazyLoad) {
				return $ocLazyLoad.load({
					name:'lithium',
					files:['scripts/controllers/dashboard/groups/group/group.js']
				})
			}
		},
		params: {
			domainName: ["$rootScope", function($rootScope) {
				return $rootScope.principal.domainName;
			}]
		},
		ncyBreadcrumb: {
			parent: 'dashboard.groups.list',
			label: '{{ $state.params.groupName }}'
		}
	});

	$stateProvider.state('dashboard.groups.group.view',{
		templateUrl:'scripts/controllers/dashboard/groups/group/view/view.html',
		url:'/view',
		controller: 'GroupView as controller',
		resolve: {
			loadMyFiles:function($ocLazyLoad) {
				return $ocLazyLoad.load({
					name:'lithium',
					files:['scripts/controllers/dashboard/groups/group/view/view.js']
				})
			}
		},
		ncyBreadcrumb: { label: "view" }
	});

	$stateProvider.state('dashboard.groups.group.roles',{
		templateUrl:'scripts/controllers/dashboard/groups/group/roles/roles.html',
		url:'/roles',
		controller:'GroupViewRoles as controller',
		resolve: {
			loadMyFiles:function($ocLazyLoad) {
				return $ocLazyLoad.load({
					name:'lithium',
					files:['scripts/controllers/dashboard/groups/group/roles/roles.js']
				})
			}
		},
		ncyBreadcrumb: { label: "roles" }
	});

	$stateProvider.state('dashboard.groups.group.users',{
		templateUrl:'scripts/controllers/dashboard/groups/group/users/users.html',
		url:'/users',
		controller:'GroupViewUsers as controller',
		resolve: {
			loadMyFiles:function($ocLazyLoad) {
				return $ocLazyLoad.load({
					name:'lithium',
					files:['scripts/controllers/dashboard/groups/group/users/users.js']
				})
			}
		},
		ncyBreadcrumb: { label: "users" }
	});

	/*
	 * $stateProvider.state('dashboard.users',{
	 * templateUrl:'scripts/controllers/dashboard/users/list/list.html',
	 * url:'/users', controller:'UsersList as controller', resolve: { domain:
	 * ["rest-domain", "$stateParams", "$rootScope", function(rest,
	 * $stateParams, $rootScope) { if
	 * (angular.isDefined($stateParams.domainName)) {
	 * console.log($stateParams.domainName); return
	 * rest.view($stateParams.domainName); } else { return
	 * rest.view($rootScope.principal.domainName); } }],
	 * loadMyFiles:function($ocLazyLoad) { return $ocLazyLoad.load({
	 * name:'lithium', files:[
	 * 'scripts/controllers/dashboard/users/list/list.js',
	 * 'scripts/controllers/dashboard/users/add/add.js' ] }) } }, ncyBreadcrumb: {
	 * label: 'users' } });
	 *
	 * $stateProvider.state('dashboard.user',{
	 * templateUrl:'scripts/controllers/dashboard/users/view/view.html',
	 * url:'/users/user/:id', controller:'UserView as controller', resolve: {
	 * loadMyFiles:function($ocLazyLoad) { return $ocLazyLoad.load({
	 * name:'lithium',
	 * files:['scripts/controllers/dashboard/users/view/view.js'] }) } },
	 * ncyBreadcrumb: { parent: 'dashboard.users', label: 'view' } });
	 */

	$stateProvider.state('dashboard.providers',{
		abstract: true,
		url:'/domain/{domainName}/providers',
		resolve: {
			// list of currently started up providers
			// TODO: remove hardcode and get domain from $userService once
			// implemented
			providers: ["rest-provider", "$stateParams", function(rest, $stateParams) {
				return rest.listForDomain($stateParams.domainName).then(function(providers) { return providers; })
			}]
		},
		params: {
			domainName: ["$rootScope", function($rootScope) {
				return $rootScope.principal.domainName;
			}],
		},
		templateUrl:'scripts/controllers/dashboard/providers/providers.html'
	});

	$stateProvider.state('dashboard.providers.list',{
		url:'/list',
		templateUrl:'scripts/controllers/dashboard/providers/list/list.html',
		controller:'providersList as controller',
		resolve: {
			loadMyFiles:function($ocLazyLoad) {
				return $ocLazyLoad.load({
					name:'lithium',
					files:['scripts/controllers/dashboard/providers/list/list.js']
				})
			}
		},
		ncyBreadcrumb: {
			label: 'providers ({{ $stateParams.domainName }})'
		}
	});

	$stateProvider.state('dashboard.providers.add',{
		url:'/add',
		templateUrl:'scripts/controllers/dashboard/providers/add/add.html',
		controller:'providerAdd as controller',
		resolve: {
			loadMyFiles:function($ocLazyLoad) {
				return $ocLazyLoad.load({
					name:'lithium',
					files:['scripts/controllers/dashboard/providers/add/add.js']
				})
			}
		},
		ncyBreadcrumb: {
			parent: 'dashboard.providers.list',
			label: 'add'
		}
	});

	$stateProvider.state('dashboard.product',{
		template: "<div ui-view></div>",
		url:'/products',
		resolve: {
			loadMyFiles:function($ocLazyLoad) {
				return $ocLazyLoad.load({
					name:'lithium',
					files: [
						'scripts/controllers/dashboard/products/add/add.js',
						'scripts/controllers/dashboard/products/view/view.js',
						'scripts/controllers/dashboard/products/list/list.js',
						'scripts/controllers/dashboard/products/transactions/list/list.js'
					]
				})
			}
		},
		ncyBreadcrumb: { skip: true },
		redirectTo: "dashboard.product.list"
	});

	$stateProvider.state('dashboard.product.list',{
		templateUrl:'scripts/controllers/dashboard/products/list/list.html',
		data: {
			pageTitle: "UI_NETWORK_ADMIN.PAGE.TITLE.PRODUCTS_LIST",
		},
		url:'/list',
		controller:'ProductList as controller'
	});
	$stateProvider.state('dashboard.product.transactions',{
		templateUrl:'scripts/controllers/dashboard/products/transactions/list/list.html',
		data: {
			pageTitle: "UI_NETWORK_ADMIN.PAGE.TITLE.PRODUCTS_TRANSACTIONS",
		},
		url:'/transactions',
		controller:'ProductTransactionList as controller'
	});
	$stateProvider.state('dashboard.product.add',{
		templateUrl:'scripts/controllers/dashboard/products/add/add.html',
		url:'/add',
		controller:'ProductAdd as controller'
	});
	$stateProvider.state('dashboard.product.view',{
		templateUrl:'scripts/controllers/dashboard/products/view/view.html',
		url:'/view/:id',
		controller:'ProductView as controller',
		resolve: {
			product: ["ProductRest", "$stateParams", "errors", function(rest, $stateParams, errors) {
				return rest.findProductById($stateParams.id).then(function(lb) {
					return lb.plain();
				}).catch(function(error) {
					errors.catch("", false)(error)
				});
			}]
		}
	});

	$stateProvider.state('dashboard.leaderboard',{
		template: "<div ui-view></div>",
		url:'/leaderboard',
		resolve: {
			loadMyFiles:function($ocLazyLoad) {
				return $ocLazyLoad.load({
					name:'lithium',
					files: [
						'scripts/controllers/dashboard/leaderboard/add/add.js',
						'scripts/controllers/dashboard/leaderboard/view/view.js',
						'scripts/controllers/dashboard/leaderboard/view/viewhistory.js',
						'scripts/controllers/dashboard/leaderboard/list/list.js'
					]
				})
			}
		},
		ncyBreadcrumb: { skip: true },
		redirectTo: "dashboard.leaderboard.list"
	});

	$stateProvider.state('dashboard.leaderboard.add',{
		templateUrl:'scripts/controllers/dashboard/leaderboard/add/add.html',
		url:'/add',
		controller:'LeaderboardAdd as controller'
	});
	$stateProvider.state('dashboard.leaderboard.view',{
		templateUrl:'scripts/controllers/dashboard/leaderboard/view/view.html',
		url:'/view/:id',
		controller:'LeaderboardView as controller',
		resolve: {
			leaderboard: ["LeaderboardRest", "$stateParams", "errors", function(rest, $stateParams, errors) {
				return rest.findLeaderboardById($stateParams.id).then(function(lb) {
					return lb.plain();
				}).catch(function(error) {
					errors.catch("", false)(error)
				});
			}]
		}
	});
	$stateProvider.state('dashboard.leaderboard.history',{
		templateUrl:'scripts/controllers/dashboard/leaderboard/view/viewhistory.html',
		url:'/history/view/:id',
		controller:'LeaderboardHistoryView as controller',
		resolve: {
			leaderboardHistory: ["LeaderboardRest", "$stateParams", "errors", function(rest, $stateParams, errors) {
				return rest.findLeaderboardHistoryById($stateParams.id).then(function(lb) {
					return lb.plain();
				}).catch(function(error) {
					errors.catch("", false)(error)
				});
			}]
		}
	});
	$stateProvider.state('dashboard.leaderboard.list',{
		templateUrl:'scripts/controllers/dashboard/leaderboard/list/list.html',
		data: {
			pageTitle: "UI_NETWORK_ADMIN.PAGE.TITLE.LEADERBOARDS",
		},
		url:'/list',
		controller:'LeaderboardList as controller'
	});

	$stateProvider.state('dashboard.players',{
		template: "<div ui-view></div>",
		url:'/players',
		resolve: {
			tabs: ["$security", "$filter", "$state", "$rootScope", "$stateParams", function($security, $filter, $state, $rootScope, $stateParams) {
				var tabs = [
					{ name: "dashboard.players.player.summary", title: "UI_NETWORK_ADMIN.PLAYER.TAB.SUMMARY", roles: "PLAYER_VIEW" },
					{ name: "dashboard.players.player.dashboard", title: "UI_NETWORK_ADMIN.PLAYER.TAB.DASHBOARD", roles: "PLAYER_DASHBOARD_VIEW" },
					{ name: "dashboard.players.player.financialtransactions", title: "UI_NETWORK_ADMIN.PLAYER.TAB.FINANCIAL_TRANSACTIONS", roles: "PLAYER_CASHIER_TRANSACTIONS_VIEW" },
					{ name: "dashboard.players.player.comments", title: "UI_NETWORK_ADMIN.PLAYER.TAB.COMMENTS", roles: "PLAYER_COMMENTS_VIEW" },
					{ name: "dashboard.players.player.bonushistory", title: "UI_NETWORK_ADMIN.PLAYER.TAB.BONUSHISTORY", roles: "PLAYER_BONUS_HISTORY_VIEW" },
					{ name: "dashboard.players.player.loginevents", title: "UI_NETWORK_ADMIN.PLAYER.TAB.LOGINEVENTS", roles: "PLAYER_LOGIN_EVENTS_VIEW" },
					{ name: "dashboard.players.player.mailhistory", title: "UI_NETWORK_ADMIN.PLAYER.TAB.MAILHISTORY", roles: "PLAYER_MAIL_HISTORY_VIEW" },
					{ name: "dashboard.players.player.smshistory", title: "UI_NETWORK_ADMIN.PLAYER.TAB.SMSHISTORY", roles: "PLAYER_SMS_HISTORY_VIEW"},
					{ name: "dashboard.players.player.document", title: "UI_NETWORK_ADMIN.PLAYER.TAB.DOCUMENT", roles: "PLAYER_DOCUMENTS_VIEW" },
					{ name: "dashboard.players.player.documentold", title: "Documents (deprecated)", roles: "PLAYER_OLD_DOCUMENTS_VIEW" },
					{ name: "dashboard.players.player.events", title: "UI_NETWORK_ADMIN.PLAYER.TAB.EVENTS", roles: "PLAYER_EVENTS_VIEW", tclass: "disabled" },
					{ name: "dashboard.players.player.notifications", title: "UI_NETWORK_ADMIN.PLAYER.TAB.NOTIFICATIONSINBOX", roles: "PLAYER_NOTIFICATIONS_VIEW" },
					{ name: "dashboard.players.player.missions", title: "UI_NETWORK_ADMIN.PLAYER.TAB.MISSIONS", roles: "PLAYER_MISSIONS_VIEW" },
					{ name: "dashboard.players.player.raf", title: "UI_NETWORK_ADMIN.PLAYER.TAB.REFERRALS", roles: "PLAYER_REFERRALS_VIEW" },
					{ name: "dashboard.players.player.incentivegames", title: "UI_NETWORK_ADMIN.PLAYER.TAB.INCENTIVEGAMES", roles: "PLAYER_INCENTIVE_GAME_VIEW" },
					{ name: "dashboard.players.player.sportsbook", title: "UI_NETWORK_ADMIN.PLAYER.TAB.SPORTSBOOK", roles: "ROLE_PLAYER_SPORTS_BET_HISTORY" },
					{ name: "dashboard.players.player.casinohistory", title: "UI_NETWORK_ADMIN.PLAYER.TAB.CASINO", roles: "PLAYER_CASINO_HISTORY_VIEW" },
					{ name: "dashboard.players.player.bonuses", title: "UI_NETWORK_ADMIN.PLAYER.TAB.BONUSES", roles: "PLAYER_BONUSES_VIEW" },
					{ name: "dashboard.players.player.accountinghistory", title: "UI_NETWORK_ADMIN.PLAYER.TAB.ACCOUNTINGHISTORY", roles: "PLAYER_ACCOUNTING_HISTORY_VIEW" },
					{ name: "dashboard.players.player.sportsbookhistory", title: "UI_NETWORK_ADMIN.PLAYER.TAB.ACCOUNTINGSPORTSBOOKHISTORY", roles: "PLAYER_ACCOUNTING_SPORTSBOOK_HISTORY_VIEW" },
					{ name: "dashboard.players.player.changelogs", title: "UI_NETWORK_ADMIN.PLAYER.TAB.NOTES", roles: "PLAYER_NOTES_VIEW" },
					{ name: "dashboard.players.player.responsiblegambling", title: "UI_NETWORK_ADMIN.PLAYER.RESPONSIBLE_GAMBLING.TITLE", roles: "PLAYER_RESPONSIBLE_GAMING_VIEW" },
					{ name: "dashboard.players.player.kyc", title: "UI_NETWORK_ADMIN.PLAYER.KYC.TAB.TAB_TITLE", roles: "PLAYER_KYC_RESULTS_VIEW" },
					{ name: "dashboard.players.player.playerprotection", title: "UI_NETWORK_ADMIN.PLAYER.PLAYER_PROTECTION.TAB.TITLE", roles: "PLAYER_PROTECTION_VIEW" }
				];
				return tabs;
			}],
			loadMyFiles:function($ocLazyLoad) {
				return $ocLazyLoad.load({
					name:'lithium',
					files: [
						'scripts/controllers/dashboard/players/list/list.js',
						'scripts/controllers/dashboard/players/tags/list.js',
					]
				})
			}
		},
		ncyBreadcrumb: { skip: true },
		redirectTo: "dashboard.players.list"
	});

	$stateProvider.state('dashboard.players.list',{
		templateUrl:'scripts/controllers/dashboard/players/list/list.html',
		data: {
			pageTitle: "UI_NETWORK_ADMIN.PAGE.TITLE.PLAYER_SEARCH",
		},
		url:'/list',
		controller:'PlayersList as controller',
		ncyBreadcrumb: {
			parent: 'dashboard.domain',
			label: 'players'
		}
	});

	$stateProvider.state('dashboard.players.massplayerupdate',{
		templateUrl: 'scripts/controllers/dashboard/players/mass-player-update/domain-select.html',
		data: {
			pageTitle: "UI_NETWORK_ADMIN.PAGE.TITLE.MASS_PLAYER_UPDATE",
		},
		url:'/massupdate',
		controller:'MassPlayerUpdateController as controller',
		resolve: {
			loadMyFiles:function($ocLazyLoad) {
				return $ocLazyLoad.load({
					name:'lithium',
					files: [
						'scripts/controllers/dashboard/players/mass-player-update/domain-select.js'
					]
				})
			}
		}
	});

	$stateProvider.state('dashboard.players.massplayerupdate.tool',{
		templateUrl:'scripts/controllers/dashboard/players/mass-player-update/mass-player-update-tool.html',
		url:'/{domainName}',
		controller:'MassPlayerUpdateToolController as controller',
		params: {
			domainName: ['$stateParams', function($stateParams) {
				return $stateParams.domainName;
			}]
		},
		resolve: {
			statuses: ['StatusRest', function(statusRest) {
				return statusRest.findAll().then(function(statuses) {
					return statuses.plain();
				});
			}],
			statusReasons: ['StatusRest', function(statusRest) {
				return statusRest.findAllStatusReasons().then(function(statusReasons) {
					return statusReasons.plain();
				});
			}],
			tags: ['UserRest', '$stateParams', function(userRest, $stateParams) {
				return userRest.findAllTags($stateParams.domainName).then(function(tags) {
					return tags.plain();
				});
			}],
			currencySymbol: ['rest-domain', "$stateParams", function(domainRest, $stateParams) {
				return domainRest.findByName($stateParams.domainName).then(function(domain) {
					return domain.currencySymbol;
				})
			}],
			domainName: ["$stateParams", function($stateParams) {
				return $stateParams.domainName;
			}],
			excludeStatusReasons: ['rest-domain', '$stateParams', function(domainRest, $stateParams) {
				return domainRest.findCurrentDomainSettings($stateParams.domainName).then(function(response) {
					var settings = response.plain();
					var domainSettings = {};
					for (var i = 0; i < settings.length; i++) {
						var dslv = settings[i];
						domainSettings[dslv.labelValue.label.name] = dslv.labelValue.value;
					}

					let excludeStatusReasons = [];

					var cruksSelfExclEnabled = (domainSettings['cruksId'] == "show" ? true : false);
					if (!cruksSelfExclEnabled) {
						excludeStatusReasons.push("CRUKS_SELF_EXCLUSION")
					}
					excludeStatusReasons.push("GAMSTOP_SELF_EXCLUSION")
					return excludeStatusReasons;
				});
			}],
			loadMyFiles:function($ocLazyLoad) {
				return $ocLazyLoad.load({
					name:'lithium',
					files: [
						'scripts/controllers/dashboard/players/mass-player-update/mass-player-update-tool.js'
					]
				})
			}
		}
	});

	$stateProvider.state('dashboard.players.incomplete',{
		//templateUrl:'scripts/controllers/dashboard/players/abandoned/list.html',
		template: "<div ui-view></div> "  ,
		url:'',
		controller:'PlayerAbandonedList as controller',
		resolve: {
			loadMyFiles:function($ocLazyLoad) {
				return $ocLazyLoad.load({
					name:'lithium',
					files: [
						//		'scripts/controllers/dashboard/players/abandoned/view.js',
						'scripts/controllers/dashboard/players/abandoned/list.js',

					]
				})
			}
		},
		ncyBreadcrumb: {
			parent: 'dashboard.players',
			label: 'incomplete signups'
		},
		redirectTo: "dashboard.players.incomplete.list",

	});

	$stateProvider.state('dashboard.players.incomplete.list',{
		templateUrl:'scripts/controllers/dashboard/players/abandoned/list.html',
		data: {
			pageTitle: "UI_NETWORK_ADMIN.PAGE.TITLE.ABANDONED_SIGNUPS",
		},
		url:'/abandoned',
		controller:'PlayerAbandonedList as controller',
		resolve: {
			loadMyFiles:function($ocLazyLoad) {
				return $ocLazyLoad.load({
					name:'lithium',
					files: [
						'scripts/controllers/dashboard/players/abandoned/view.js'
					]
				})
			}
		},
		ncyBreadcrumb: {
			parent: 'dashboard.domain',
			label: 'incomplete signups'
		}
	});

	$stateProvider.state('dashboard.players.incomplete.view',{
		templateUrl: 'scripts/controllers/dashboard/players/abandoned/view.html',
		url:'/{domainName}/:id/view',
		controller: 'AbandonedPlayersViewController as controller',
		resolve: {
			domain: function($stateParams) { return $stateParams.domainName; },
			user: ["IncompleteUserRest", "$stateParams", "errors", function(rest, $stateParams, errors) {
				return rest.findById($stateParams.domainName, $stateParams.id).then(function(user) {
					return user;
				}).catch(function(error) {
					console.log(error);
					errors.catch("", false)(error)
				});
			}],
			loadMyFiles:function($ocLazyLoad) {
				return $ocLazyLoad.load({
					name:'lithium',
					files:[ 'scripts/controllers/dashboard/players/abandoned/view.js' ]
				})
			}

		},

		ncyBreadcrumb: { label: 'Abandoned User Profile' }
	});

	$stateProvider.state('dashboard.players.playerlinks',{
		templateUrl: 'scripts/controllers/dashboard/players/playerlinks/list.html',
		data: {
			pageTitle: "UI_NETWORK_ADMIN.PAGE.TITLE.PLAYER_LINKS",
		},
		url:'/playerlinks',
		controller:'PlayerLinksList as controller',
		resolve: {
			loadMyFiles:function($ocLazyLoad) {
				return $ocLazyLoad.load({
					name:'lithium',
					files: [
						'scripts/controllers/dashboard/players/playerlinks/list.js',

					]
				})
			}
		},
		ncyBreadcrumb: {
			label: 'global player links'
		},
	});

	$stateProvider.state('dashboard.players.tags', {
		templateUrl:'scripts/controllers/dashboard/players/tags/list.html',
		data: {
			pageTitle: "UI_NETWORK_ADMIN.PAGE.TITLE.PLAYER_TAGS",
		},
		url:'/tags',
		controller:'PlayersTagsList as controller',
		ncyBreadcrumb: {
			parent: 'dashboard.domain',
			label: 'players'
		}
	});

	$stateProvider.state('dashboard.players.tag',{
		template: "<div ui-view></div>",
		url:'/tag',
// resolve: {
// loadMyFiles:function($ocLazyLoad) {
// return $ocLazyLoad.load({
// name:'lithium',
// files: [
// 'scripts/controllers/dashboard/players/list/list.js',
// 'scripts/controllers/dashboard/players/tags/list.js'
// ]
// })
// }
// },
		ncyBreadcrumb: { skip: true },
		redirectTo: "dashboard.players.tag.view"
	});
	$stateProvider.state('dashboard.players.tag.view',{
		templateUrl: 'scripts/controllers/dashboard/players/tags/view.html',
		url:'/view/:id',
		controller:'PlayersTagView as controller',
		resolve: {
			tag: ["$stateParams", function(params) {
				return {
					id: params.id,
					domainName: params.domainName,
					dwhVisible: params.dwhVisible,
					name: params.name,
					description: params.description
				};
			}],
			loadMyFiles:function($ocLazyLoad) {
				return $ocLazyLoad.load({
					name:'lithium',
					files:[ 'scripts/controllers/dashboard/players/tags/view.js' ]
				})
			}
		},
		ncyBreadcrumb: { label: 'dashboard' }
	});
	$stateProvider.state('dashboard.players.tag.view.details',{
		templateUrl: 'scripts/controllers/dashboard/players/tags/viewdetails.html',
		url:'/d',
		controller:'PlayersTagViewDetails as controller',
		resolve: {
			tag: ["tag", function(tag) {
				return tag;
			}],
			loadMyFiles:function($ocLazyLoad) {
				return $ocLazyLoad.load({
					name:'lithium',
					files:[ 'scripts/controllers/dashboard/players/tags/viewdetails.js' ]
				})
			}
		}
	});
	$stateProvider.state('dashboard.players.tag.view.players',{
		templateUrl: 'scripts/controllers/dashboard/players/tags/viewplayers.html',
		url:'/p',
		controller:'PlayersTagViewPlayers as controller',
		resolve: {
			tag: ["tag", function(tag) {
				return tag;
			}],
			loadMyFiles:function($ocLazyLoad) {
				return $ocLazyLoad.load({
					name:'lithium',
					files:[ 'scripts/controllers/dashboard/players/tags/viewplayers.js' ]
				})
			}
		}
	});

	$stateProvider.state('dashboard.players.guidredirect',{
		templateUrl: 'scripts/controllers/dashboard/players/player/guidredirect/guidredirect.html',
		url:'/{domainName}/{usernameOrId}',
		controller: 'PlayerGuidRedirectController as controller',
		resolve: {
			user: ["UserRest", "$stateParams", "errors", function(rest, $stateParams, errors) {
				return rest.findFromGuid($stateParams.domainName, $stateParams.domainName + "/" + $stateParams.usernameOrId).then(function(user) {
					return user;
				}).catch(function(error) {
					errors.catch("", false)(error)
				});
			}],
			loadMyFiles:function($ocLazyLoad) {
				return $ocLazyLoad.load({
					name:'lithium',
					files:['scripts/controllers/dashboard/players/player/guidredirect/guidredirect.js']
				})
			}
		}
	});

	$stateProvider.state('dashboard.players.player',{
		templateUrl: 'scripts/controllers/dashboard/players/player/player.html',
		url:'/{domainName}/:id',
// url:'/:id',
		controller: 'PlayerController as controller',
		resolve: {
			domain: function($stateParams) { return $stateParams.domainName; },
			domainInfo: ["user", "errors", "rest-domain", function(user, errors, restDomain) {
				return restDomain.findByName(encodeURIComponent(user.domain.name)).then(function(domain) {
					return domain.plain();
				}).catch(function(error) {
					errors.catch("", false)(error)
				});
			}],
			domainSettings: ['rest-domain', '$stateParams', function (domainRest, $stateParams) {
				return domainRest.findCurrentDomainSettings($stateParams.domainName).then(function(response) {
					var settings = response.plain();
					var objSettings = {};
					for (var i = 0; i < settings.length; i++) {
						var dslv = settings[i];
						objSettings[dslv.labelValue.label.name] = dslv.labelValue.value;
					}
					return objSettings;
				});
			}],
			limits: ["user", "domainInfo", "userLimitsRest", "errors" ,'$q', function(user, domain, userLimitsRest, errors, $q) {
				// daily loss limit
				var dllPromise = userLimitsRest.findPlayerLimit(user.guid, user.domain.name, userLimitsRest.GRANULARITY_DAY, userLimitsRest.LIMIT_TYPE_LOSS).then(function(response) {
					return (response.amount != undefined)? response.amount / 100: null;
				}).catch(function() {
					errors.catch('', false);
				});

				// weekly loss limit
				var wllPromise = userLimitsRest.findPlayerLimit(user.guid, user.domain.name, userLimitsRest.GRANULARITY_WEEK, userLimitsRest.LIMIT_TYPE_LOSS).then(function(response) {
					return (response.amount != undefined)? response.amount / 100: null;
				}).catch(function() {
					errors.catch('', false);
				});

				// monthly loss limit
				var mllPromise = userLimitsRest.findPlayerLimit(user.guid, user.domain.name, userLimitsRest.GRANULARITY_MONTH, userLimitsRest.LIMIT_TYPE_LOSS).then(function(response) {
					return (response.amount != undefined)? response.amount / 100: null;
				}).catch(function() {
					errors.catch('', false)
				});

				//daily Net Loss To House
				var dnlPromise = userLimitsRest.findNetLossToHouse(user.domain.name, user.guid, domain.currency, userLimitsRest.GRANULARITY_DAY).then(function(response) {
					return (response != undefined)? response / 100: null;
				}).catch(function() {
					errors.catch('', false);
				});

				//weekly Net Loss To House
				var wnlPromise = userLimitsRest.findNetLossToHouse(user.domain.name, user.guid, domain.currency, userLimitsRest.GRANULARITY_WEEK).then(function(response) {
					return (response != undefined)? response / 100: null;
				}).catch(function() {
					errors.catch('', false);
				});

				//monthly Net Loss To House
				var mnlPromise = userLimitsRest.findNetLossToHouse(user.domain.name, user.guid, domain.currency, userLimitsRest.GRANULARITY_MONTH).then(function(response) {
					return (response != undefined)? response / 100: null;
				}).catch(function() {
					errors.catch('', false);
				});

				return $q.all([dllPromise, wllPromise, mllPromise, dnlPromise, wnlPromise, mnlPromise]).then(function(results){
					return {
						dailyLossLimit: results[0],
						weeklyLossLimit: results[1],
						monthlyLossLimit: results[2],
						dailyNetLossToHouse: results[3],
						weeklyNetLossToHouse: results[4],
						monthlyNetLossToHouse: results[5],
						domain: domain
					};
				});
			}],
			depositLimits: ["user", "userLimitsRest", "errors", function(user, userLimitsRest, errors) {
				return userLimitsRest.depositLimits(user.guid).then(function(response) {
					var data = {};
					angular.forEach(response.plain(), function(v, k) {
						if (v.granularity === userLimitsRest.GRANULARITY_DAY) {
							data.dailyLimit = v.amount / 100;
							data.dailyLimitUsed = v.amountUsed / 100;
						} else if (v.granularity === userLimitsRest.GRANULARITY_WEEK) {
							data.weeklyLimit = v.amount / 100;
							data.weeklyLimitUsed = v.amountUsed / 100;
						} else if (v.granularity === userLimitsRest.GRANULARITY_MONTH) {
							data.monthlyLimit = v.amount / 100;
							data.monthlyLimitUsed = v.amountUsed / 100;
						}
					});
					return data;
				}).catch(
					errors.catch('', false)
				);
			}],
			playTimeLimits: ["user", "userLimitsRest", "errors", function(user, userLimitsRest, errors) {
				return userLimitsRest.depositLimits(user.guid).then(function(response) {
					var data = {};
					angular.forEach(response.plain(), function(v, k) {
						if (v.granularity === userLimitsRest.GRANULARITY_DAY) {
							data.dailyLimit = v.amount / 100;
							data.dailyLimitUsed = v.amountUsed / 100;
						} else if (v.granularity === userLimitsRest.GRANULARITY_WEEK) {
							data.weeklyLimit = v.amount / 100;
							data.weeklyLimitUsed = v.amountUsed / 100;
						} else if (v.granularity === userLimitsRest.GRANULARITY_MONTH) {
							data.monthlyLimit = v.amount / 100;
							data.monthlyLimitUsed = v.amountUsed / 100;
						}
					});
					return data;
				}).catch(
					errors.catch('', false)
				);
			}],
			balanceLimits: ["user", "userLimitsRest", "errors", function(user, userLimitsRest, errors) {
				return userLimitsRest.balanceLimitsList(user.domain.name, user.guid).then(function(response) {
					return response;
				}).catch(
					errors.catch('', false)
				);
			}],
			selfExclusion: ["user", "ExclusionRest", "errors", function(user, exclusionRest, errors) {
				return exclusionRest.lookup(user.guid, user.domain.name).then(function(response) {
					if (response.length !== undefined && response.length === 0) {
						return undefined;
					} else if(response.length !== 0 && response.data === null){
						return undefined;
					} else{
						return response.plain();
					}
				}).catch(function() {
					errors.catch('', false);
				});
			}],
			coolOff: ["user", 'CoolOffRest', "errors", function(user, restCoolOff, errors) {
				return restCoolOff.lookup(user.guid, user.domain.name).then(function(response) {
					if (response.length !== undefined && response.length === 0) {
						return undefined;
					} else if(response.length !== 0 && response.data === null){
						return undefined;
					} else{
						return response.plain();
					}
				}).catch(function() {
					errors.catch('', false);
				});
			}],
			realityCheck: ["user", 'RealityCheckRest', "errors", function(user, realityCheckRest, errors) {
				return realityCheckRest.get(user.guid, user.domain.name).then(function(response) {
					return (response.length !== undefined && response.length === 0) ? undefined : response.plain();
				}).catch(function() {
					errors.catch('', false);
				});
			}],
			user: ["UserRest", "$stateParams", "$state", "errors", function(rest, $stateParams, $state, errors) {
				return rest.findById($stateParams.domainName, $stateParams.id).then(function(user) {
					if (user._status === 404) {
						$state.go("dashboard.414");
						return;
					}
					return rest.findAdditionalDataByUserGuid($stateParams.domainName, $stateParams.id, user.guid).then(function(additionalData) {
						user.additionalData = additionalData.plain();
						return user.plain();
					}).catch(function(error) {
						errors.catch("", false)(error)
					});
				}).catch(function(error) {
					errors.catch("", false)(error)
				});
			}],
			playerBalance: ["user", 'rest-accounting', "errors", function(user, restAcc, errors) {
				return restAcc.getAllByOwnerGuid(user.domain.name, user.guid).then(function (balances) {
					return balances;
				}).catch(function (error) {
					errors.catch("UI_NETWORK_ADMIN.USER.ERRORS.BALANCE", false)
				})
			}],
			userRestrictions: ['user', 'UserRestrictionsRest', 'errors', function(user, rest, errors) {
				return rest.get(user.domain.name, user.guid).then(function(response) {
					return response.plain();
				}).catch(function(error) {
					errors.catch('', false)(error);
				});
			}],
			ltDeposits: ['user', 'domainInfo', 'rest-accounting', 'errors', function(user, domainInfo, rest, errors) {
				return rest.tranTypeSummaryByOwnerGuid(user.domain.name, user.guid, 5,
					'PLAYER_BALANCE', 'CASHIER_DEPOSIT', domainInfo.currency).then(function(response) {
					var response = response.plain();
					var total = 0;
					if (response[0]) {
						total += (response[0].debitCents - response[0].creditCents) * -1;
					}
					return total;
				}).catch(function(error) {
					errors.catch('', false)(error);
				});
			}],
			ltWithdrawals: ['user', 'domainInfo', 'rest-accounting', 'errors', function(user, domainInfo, rest, errors) {
				return rest.tranTypeSummaryByOwnerGuid(user.domain.name, user.guid, 5,
					'PLAYER_BALANCE_PENDING_WITHDRAWAL', 'CASHIER_PAYOUT', domainInfo.currency).then(function(response) {
					var response = response.plain();
					var total = 0;
					if (response[0]) {
						total += (response[0].debitCents - response[0].creditCents) * 1;
					}
					return total;
				}).catch(function(error) {
					errors.catch('', false)(error);
				});
			}],
			pendingWithdrawals: ['user', 'domainInfo', 'rest-accounting', 'errors', function(user, domainInfo, rest, errors) {
				return rest.summaryAccountByOwnerGuid(user.domain.name, user.guid, 5,
					'PLAYER_BALANCE_PENDING_WITHDRAWAL', domainInfo.currency).then(function(response) {
					var response = response.plain();
					var total = 0;
					if (response[0]) {
						total += (response[0].debitCents - response[0].creditCents) * -1;
					}
					return total;
				}).catch(function(error) {
					errors.catch('', false)(error);
				});
			}],
			playerLinks: ['user', 'UserRest', 'errors', function (user, rest, errors) {
				return rest.playerLinks(user.guid).then(function(response){
					if (response !== undefined ) {
						return response.plain();
					}
				}).catch(function(error) {
					errors.catch('', false)(error);
				});
			}],
			unlockedGameUserStatuses: ['user', 'rest-games', 'errors', function (user, rest, errors) {
				return rest.findUnlockedFreeGamesForUser(user.guid).then(function(response){
					if (response !== undefined ) {
						return response.plain();
					}
				}).catch(function(error) {
					errors.catch('', false)(error);
				});
			}],
			userData: function() { return {}; },
			loadMyFiles:function($ocLazyLoad) {
				return $ocLazyLoad.load({
					name:'lithium',
					files:['scripts/controllers/dashboard/players/player/player.js']
				})
			}
		},
		redirectTo: "dashboard.players.player.summary",
		ncyBreadcrumb: { label: 'player' }
	});

	$stateProvider.state('dashboard.players.player.dashboard',{
		templateUrl: 'scripts/controllers/dashboard/players/player/dashboard/dashboard.html',
		url:'/dashboard',
		controller:'PlayerDashboardController as controller',
		resolve: {
			loadMyFiles:function($ocLazyLoad) {
				return $ocLazyLoad.load({
					name:'lithium',
					files:[ 'scripts/controllers/dashboard/players/player/dashboard/dashboard.js' ]
				})
			}
		},
		ncyBreadcrumb: { label: 'dashboard' }
	});

	$stateProvider.state('dashboard.players.player.comments',{
		templateUrl: 'scripts/controllers/dashboard/players/player/comments/comments.html',
		url:'/comments',
		controller:'PlayerCommentsController as controller',
		resolve: {
			loadMyFiles:function($ocLazyLoad) {
				return $ocLazyLoad.load({
					name:'lithium',
					files:[ 'scripts/controllers/dashboard/players/player/comments/comments.js' ]
				})
			}
		},
		ncyBreadcrumb: { label: 'comments' }
	});

	$stateProvider.state('dashboard.players.player.bonushistory',{
		templateUrl: 'scripts/controllers/dashboard/players/player/bonushistory/bonushistory.html',
		url:'/bonushistory',
		controller:'PlayerBonusHistoryController as controller',
		resolve: {
			currencySymbol: ['rest-domain', "$stateParams", function(domainRest, $stateParams) {
				return domainRest.findByName($stateParams.domainName).then(function(domain) {
					return domain.currencySymbol;
				})
			}],
			loadMyFiles:function($ocLazyLoad) {
				return $ocLazyLoad.load({
					name:'lithium',
					files:[ 'scripts/controllers/dashboard/players/player/bonushistory/bonushistory.js' ]
				})
			}
		},
		ncyBreadcrumb: { label: 'bonus history' }
	});

	$stateProvider.state('dashboard.players.player.loginevents',{
		templateUrl: 'scripts/controllers/dashboard/players/player/loginevents/loginevents.html',
		url:'/loginevents',
		controller:'PlayerLoginEventsController as controller',
		resolve: {
			loadMyFiles:function($ocLazyLoad) {
				return $ocLazyLoad.load({
					name:'lithium',
					files:[ 'scripts/controllers/dashboard/players/player/loginevents/loginevents.js' ]
				})
			}
		},
		ncyBreadcrumb: { label: 'login history' }
	});

	$stateProvider.state('dashboard.players.player.responsiblegambling',{
		templateUrl: 'scripts/controllers/dashboard/players/player/responsiblegambling/responsiblegambling.html',
		url:'/responsiblegambling',
		controller:'ResponsibleGamblingController as controller',
		resolve: {
			domainSettings: ['rest-domain', '$stateParams', function (domainRest, $stateParams) {
				return domainRest.findCurrentDomainSettings($stateParams.domainName).then(function(response) {
					var settings = response.plain();
					var objSettings = {};
					for (var i = 0; i < settings.length; i++) {
						var dslv = settings[i];
						objSettings[dslv.labelValue.label.name] = dslv.labelValue.value;
					}
					return objSettings;
				});
			}],
			loadMyFiles:function($ocLazyLoad) {
				return $ocLazyLoad.load({
					name:'lithium',
					files:[ 'scripts/controllers/dashboard/players/player/responsiblegambling/responsiblegambling.js' ]
				})
			}
		},
		ncyBreadcrumb: { label: 'responsible gambling' }
	});

	$stateProvider.state('dashboard.players.player.raf',{
		templateUrl: 'scripts/controllers/dashboard/players/player/raf/raf.html',
		url:'/raf',
		controller:'PlayerRAFController as controller',
		resolve: {
			loadMyFiles:function($ocLazyLoad) {
				return $ocLazyLoad.load({
					name:'lithium',
					files:[ 'scripts/controllers/dashboard/players/player/raf/raf.js' ]
				})
			}
		},
		ncyBreadcrumb: { label: 'refer a friend' }
	});

	$stateProvider.state('dashboard.players.player.mailhistory',{
		template: "<div ui-view></div>",
		url:'/mailhistory',
		resolve: {
			loadMyFiles:function($ocLazyLoad) {
				return $ocLazyLoad.load({
					name:'lithium',
					files:
						[
							'scripts/controllers/dashboard/players/player/mailhistory/list/list.js',
							'scripts/controllers/dashboard/players/player/mailhistory/view/view.js'
						]
				})
			}
		},
		ncyBreadcrumb: { skip: true },
		redirectTo: "dashboard.players.player.mailhistory.list"
	});

	$stateProvider.state('dashboard.players.player.mailhistory.list',{
		templateUrl: 'scripts/controllers/dashboard/players/player/mailhistory/list/list.html',
		url: '',
		controller: 'PlayerMailHistoryController as controller',
		ncyBreadcrumb: {
			parent: 'dashboard.players.player',
			label: 'mail history'
		}
	});

	$stateProvider.state('dashboard.players.player.mailhistory.view',{
		templateUrl: 'scripts/controllers/dashboard/players/player/mailhistory/view/view.html',
		url:'/:mailId',
		controller: 'PlayerMailHistoryViewController as controller',
		resolve: {
			mail: ["mailRest", "$stateParams", "errors", function(rest, $stateParams, errors) {
				return rest.findById($stateParams.mailId).then(function(mail) {
					return mail;
				}).catch(function(error) {
					errors.catch("", false)(error)
				});
			}]
		},
		ncyBreadcrumb: {
			parent: 'dashboard.players.player.mailhistory',
			label: 'mail'
		}
	});

	$stateProvider.state('dashboard.players.player.changelogs',{
		template: "<div ui-view></div>",
		url:'/notes',
		ncyBreadcrumb: { skip: true },
		redirectTo: "dashboard.players.player.changelogs.list"
	});

	$stateProvider.state('dashboard.players.player.changelogs.list',{
		template:'<globalchangelog entryhref="dashboard.players.player.changelogs.entry"></globalchangelog>',
		url:'',
		ncyBreadcrumb: {
			parent: 'dashboard.players.player',
			label: 'player changelog'
		}
	});

	$stateProvider.state('dashboard.players.player.changelogs.entry', {
		template: '<changelogentry backhref="dashboard.players.player.changelogs" entry="$resolve.entry"/>',
		url:'/:entryid',
		resolve: {
			entry: ["ChangelogsRest", "$stateParams", "$rootScope", function(rest, $stateParams, $rootScope) {
				return rest.changeLogWithFieldChanges($stateParams.entryid);
			}]
		},
		ncyBreadcrumb: {
			parent: 'dashboard.players.player.changelogs',
			label: 'player changelog entry'
		}
	});

	$stateProvider.state('dashboard.players.player.smshistory',{
		template: "<div ui-view></div>",
		url:'/smshistory',
		resolve: {
			loadMyFiles:function($ocLazyLoad) {
				return $ocLazyLoad.load({
					name:'lithium',
					files:
						[
							'scripts/controllers/dashboard/players/player/smshistory/list/list.js',
							'scripts/controllers/dashboard/players/player/smshistory/view/view.js'
						]
				})
			}
		},
		ncyBreadcrumb: { skip: true },
		redirectTo: "dashboard.players.player.smshistory.list"
	});

	$stateProvider.state('dashboard.players.player.smshistory.list',{
		templateUrl: 'scripts/controllers/dashboard/players/player/smshistory/list/list.html',
		url: '',
		controller: 'PlayerSMSHistoryController as controller',
		ncyBreadcrumb: {
			parent: 'dashboard.players.player',
			label: 'sms history'
		}
	});

	$stateProvider.state('dashboard.players.player.smshistory.view',{
		templateUrl: 'scripts/controllers/dashboard/players/player/smshistory/view/view.html',
		url:'/:smsId',
		controller: 'PlayerSMSHistoryViewController as controller',
		resolve: {
			sms: ["rest-sms", "$stateParams", "errors", function(rest, $stateParams, errors) {
				return rest.findById($stateParams.smsId).then(function(sms) {
					return sms;
				}).catch(function(error) {
					errors.catch("", false)(error)
				});
			}]
		},
		ncyBreadcrumb: {
			parent: 'dashboard.players.player.smshistory',
			label: 'mail'
		}
	});

	$stateProvider.state('dashboard.players.player.summary',{
		templateUrl: 'scripts/controllers/dashboard/players/player/summary/summary.html',
		url:'/summary',
		controller:'PlayerSummaryController as controller',
		resolve: {
			domainCurrencies: ["rest-accounting-internal", "$stateParams", "$filter", "errors", function(rest, $stateParams, $filter, errors) {
				return rest.findDomainCurrencies($stateParams.domainName).then(function(currencies) {
					return currencies.plain();
				}).catch(function(error) {
					errors.catch("", false)(error)
				});
			}],
			domainSettings: ['rest-domain', '$stateParams', '$security', function (domainRest, $stateParams, $security) {
				return domainRest.findCurrentDomainSettings($stateParams.domainName).then(function(response) {
					var settings = response.plain();
					var objSettings = {};
					var viewCountry = $security.domainsWithRole("IBAN_VIEW").length > 0;
					for (var i = 0; i < settings.length; i++) {
						var dslv = settings[i];
						if (dslv.labelValue.label.name == 'iban') {
							if (viewCountry) {
								objSettings[dslv.labelValue.label.name] = dslv.labelValue.value;
							}
						} else {
							objSettings[dslv.labelValue.label.name] = dslv.labelValue.value;
						}
					}
					return objSettings;
				});
			}],
			loadMyFiles:function($ocLazyLoad) {
				return $ocLazyLoad.load({
					name:'lithium',
					files:[ 'scripts/controllers/dashboard/players/player/summary/summary.js' ]
				})
			}
		},
		ncyBreadcrumb: { label: 'summary' }
	});

	$stateProvider.state('dashboard.players.player.accounting',{
		templateUrl: 'scripts/controllers/dashboard/players/player/accounting/accounting.html',
		url:'/accounting',
		controller:'PlayerAccountingController as controller',
		resolve: {
			loadMyFiles:function($ocLazyLoad) {
				return $ocLazyLoad.load({
					name:'lithium',
					files:['scripts/controllers/dashboard/players/player/accounting/accounting.js']
				})
			}
		},
		redirectTo: "dashboard.players.player.accounting.history",
		ncyBreadcrumb: { label: 'accounting' }
	});

	$stateProvider.state('dashboard.players.player.kyc',{
		templateUrl: 'scripts/controllers/dashboard/players/player/kyc/kyc.html',
		url:'/kyc',
		controller:'PlayerKycRecordsController as controller',
		resolve: {
			loadMyFiles:function($ocLazyLoad) {
				return $ocLazyLoad.load({
					name:'lithium',
					files:['scripts/controllers/dashboard/players/player/kyc/kyc.js']
				})
			}
		},
		ncyBreadcrumb: { label: 'kyc' }
	});

	$stateProvider.state('dashboard.players.player.accountinghistory',{
		templateUrl: 'scripts/controllers/dashboard/players/player/accountinghistory/accountinghistory.html',
		url:'/accountinghistory',
		controller:'PlayerAccountingHistoryController as controller',
		resolve: {
			loadMyFiles:function($ocLazyLoad) {
				return $ocLazyLoad.load({
					name:'lithium',
					files:['scripts/controllers/dashboard/players/player/accountinghistory/accountinghistory.js']
				})
			}
		},
		ncyBreadcrumb: { label: 'accountinghistory' }
	});

	$stateProvider.state('dashboard.players.player.sportsbookhistory',{
		templateUrl: 'scripts/controllers/dashboard/players/player/sportsbookhistory/sportsbookhistory.html',
		url:'/sportsbookhistory',
		controller:'SportsbookHistoryController as controller',
		resolve: {
			loadMyFiles:function($ocLazyLoad) {
				return $ocLazyLoad.load({
					name:'lithium',
					files:['scripts/controllers/dashboard/players/player/sportsbookhistory/sportsbookhistory.js']
				})
			}
		},
		ncyBreadcrumb: { label: 'sportsbookhistory' }
	});

	$stateProvider.state('dashboard.players.player.casinohistory',{
		templateUrl: 'scripts/controllers/dashboard/players/player/casinohistory/casinohistory.html',
		url:'/casinohistory',
		controller:'CasinoHistoryController as controller',
		resolve: {
			loadMyFiles:function($ocLazyLoad) {
				return $ocLazyLoad.load({
					name:'lithium',
					files:['scripts/controllers/dashboard/players/player/casinohistory/casinohistory.js']
				})
			}
		},
		ncyBreadcrumb: { label: 'casinohistory' }
	});

	$stateProvider.state('dashboard.players.player.playerprotection',{
		templateUrl: 'scripts/controllers/dashboard/players/player/playerprotection/playerprotection.html',
		url:'/playerprotection',
		controller:'PlayerProtectionController as controller',
		resolve: {
			loadMyFiles:function($ocLazyLoad) {
				return $ocLazyLoad.load({
					name:'lithium',
					files:['scripts/controllers/dashboard/players/player/playerprotection/playerprotection.js']
				})
			}
		},
		ncyBreadcrumb: { label: 'playerprotection' }
	});

	$stateProvider.state('dashboard.players.player.bonuses',{
		templateUrl: 'scripts/controllers/dashboard/players/player/bonuses/bonuses.html',
		url:'/bonuses',
		controller:'PlayerBonusesController as controller',
		resolve: {
			loadMyFiles:function($ocLazyLoad) {
				return $ocLazyLoad.load({
					name:'lithium',
					files:[ 'scripts/controllers/dashboard/players/player/bonuses/bonuses.js' ]
				})
			},
			providerSportsBook: ['rest-provider', '$filter', '$stateParams', function(restProvider, $filter, $stateParams) {
				return restProvider.listForDomain($stateParams.domainName).then(function(providers) {
					let sportsBookProviderSetting = $filter('filter')(providers, {url: 'service-casino-provider-sportsbook'});
					return sportsBookProviderSetting;
				})
			}]
		},
		ncyBreadcrumb: { label: 'player bonuses' },
		redirectTo: "dashboard.players.player.bonuses.cashbonuses"
	});

	$stateProvider.state('dashboard.players.player.bonuses.cashbonuses',{
		templateUrl: 'scripts/controllers/dashboard/players/player/bonuses/cashbonuses/cashbonuses.html',
		url:'/cashbonuses',
		controller:'PlayerBonusesCashBonusesController as controller',
		resolve: {
			loadMyFiles: function($ocLazyLoad) {
				return $ocLazyLoad.load({
					name:'lithium',
					files:[ 'scripts/controllers/dashboard/players/player/bonuses/cashbonuses/cashbonuses.js' ]
				})
			},
			currencySymbol: ['rest-domain', 'user', function(domainRest, user) {
				return domainRest.findByName(user.domain.name).then(function(domain) {
					return domain.currencySymbol;
				})
			}],
			bonusCodes: ['rest-casino', 'user', 'errors', function(casinoRest, user, errors) {
				return casinoRest.getActiveCashBonusTypes(user.domain.name).then(function(response) {
					var bonusCodes = (response != undefined && response != null) ? response.plain() : [];
					// Build the bonus codes into key value pairs so it is usable by formly
					let obj = new Array();
					for (let i = 0; i < bonusCodes.length; i++) {
						obj[i] = { bonusCode: bonusCodes[i].bonusCode }
					}
					return obj;
				}).catch(function(error) {
					errors.catch("", false)(error)
				});
			}]
		},
		ncyBreadcrumb: { label: 'player cash bonuses' }
	});
	$stateProvider.state('dashboard.players.player.bonuses.freespins',{
		templateUrl: 'scripts/controllers/dashboard/players/player/bonuses/cashbonuses/cashbonuses.html',
		url:'/freespins',
		controller:'PlayerBonusesFreeSpinsController as controller',
		resolve: {
			loadMyFiles: function($ocLazyLoad) {
				return $ocLazyLoad.load({
					name:'lithium',
					files:[ 'scripts/controllers/dashboard/players/player/bonuses/freespins/freespins.js' ]
				})
			},
			currencySymbol: ['rest-domain', 'user', function(domainRest, user) {
				return domainRest.findByName(user.domain.name).then(function(domain) {
					return domain.currencySymbol;
				})
			}],
			bonusCodes: ['rest-casino', 'user', 'errors', function(casinoRest, user, errors) {
				return casinoRest.getActiveCashBonusTypes(user.domain.name).then(function(response) {
					var bonusCodes = (response != undefined && response != null) ? response.plain() : [];
					// Build the bonus codes into key value pairs so it is usable by formly
					let obj = new Array();
					for (let i = 0; i < bonusCodes.length; i++) {
						obj[i] = { bonusCode: bonusCodes[i].bonusCode }
					}
					return obj;
				}).catch(function(error) {
					errors.catch("", false)(error)
				});
			}]
		},
		ncyBreadcrumb: { label: 'free spin bonuses' }
	});

	$stateProvider.state('dashboard.players.player.bonuses.freebets',{
		templateUrl: 'scripts/controllers/dashboard/players/player/bonuses/freebets/freebets.html',
		url:'/freebets',
		controller:'PlayerBonusesFreeBetsController as controller',
		resolve: {
			loadMyFiles: function($ocLazyLoad) {
				return $ocLazyLoad.load({
					name:'lithium',
					files:[ 'scripts/controllers/dashboard/players/player/bonuses/freebets/freebets.js' ]
				})
			}
		},
		ncyBreadcrumb: { label: 'player free bets' }
	});

	$stateProvider.state('dashboard.players.player.financialtransactions',{
		templateUrl: 'scripts/controllers/dashboard/players/player/financialtransactions/financialtransactions.html',
		url:'/financialtransactions',
		controller:'PlayerFinancialTransactionsController as controller',
		resolve: {
			loadMyFiles:function($ocLazyLoad) {
				return $ocLazyLoad.load({
					name:'lithium',
					files:[ 'scripts/controllers/dashboard/players/player/financialtransactions/financialtransactions.js' ]
				})
			}
		},
		ncyBreadcrumb: { label: 'player financial transactions' },
		redirectTo: "dashboard.players.player.financialtransactions.overview"
	});

	$stateProvider.state('dashboard.players.player.financialtransactions.cashiertransactions',{
		templateUrl: 'scripts/controllers/dashboard/players/player/financialtransactions/cashiertransactions/transactions.html',
		url:'/cashiertransactions',
		controller:'PlayerCashierTransController as controller',
		resolve: {
			loadMyFiles:function($ocLazyLoad) {
				return $ocLazyLoad.load({
					name:'lithium',
					files:[ 'scripts/controllers/dashboard/players/player/financialtransactions/cashiertransactions/transactions.js' ]
				})
			}
		},
		ncyBreadcrumb: { label: 'player financial cashier transactions' }
	});

	$stateProvider.state('dashboard.players.player.financialtransactions.overview',{
		templateUrl: 'scripts/controllers/dashboard/players/player/financialtransactions/overview/overview.html',
		url:'/overview',
		controller:'PlayerFinancialTransOverviewController as controller',
		resolve: {
			loadMyFiles:function($ocLazyLoad) {
				return $ocLazyLoad.load({
					name:'lithium',
					files:[ 'scripts/controllers/dashboard/players/player/financialtransactions/overview/overview.js' ]
				})
			}
		},
		ncyBreadcrumb: { label: 'player financial transactions overview' }
	});

	$stateProvider.state('dashboard.players.player.financialtransactions.balancemovement',{
		templateUrl: 'scripts/controllers/dashboard/players/player/financialtransactions/balancemovement/transactions.html',
		url:'/balancemovement',
		controller:'PlayerBalanceMovementController as controller',
		resolve: {
			loadMyFiles:function($ocLazyLoad) {
				return $ocLazyLoad.load({
					name:'lithium',
					files:[ 'scripts/controllers/dashboard/players/player/financialtransactions/balancemovement/transactions.js' ]
				})
			}
		},
		ncyBreadcrumb: { label: 'player balance movement transactions overview' }
	});

	$stateProvider.state('dashboard.players.player.accounting.summary',{
		templateUrl: 'scripts/controllers/dashboard/players/player/accounting/summary.html',
		url:'/summary',
		controller:'PlayerAccountingSummaryController as controller',
		resolve: {
			loadMyFiles:function($ocLazyLoad) {
				return $ocLazyLoad.load({
					name:'lithium',
					files:['scripts/controllers/dashboard/players/player/accounting/summary.js']
				})
			}
		},
		ncyBreadcrumb: { label: 'summary' }
	});

// $stateProvider.state('dashboard.players.player.accounting.adjustments',{
// templateUrl:
// 'scripts/controllers/dashboard/players/player/accounting/adjustments.html',
// url:'/adjustments',
// controller:'PlayerAccountingAdjustmentsController as controller',
// resolve: {
// loadMyFiles:function($ocLazyLoad) {
// return $ocLazyLoad.load({
// name:'lithium',
// files:['scripts/controllers/dashboard/players/player/accounting/adjustments.js']
// })
// }
// },
// ncyBreadcrumb: { label: 'adjustments' }
// });

	$stateProvider.state('dashboard.players.player.accounting.history',{
		templateUrl: 'scripts/controllers/dashboard/players/player/accounting/history.html',
		url:'/adjustments',
		controller:'PlayerAccountingHistoryController as controller',
		resolve: {
			loadMyFiles:function($ocLazyLoad) {
				return $ocLazyLoad.load({
					name:'lithium',
					files:['scripts/controllers/dashboard/players/player/accounting/history.js']
				})
			}
		},
		ncyBreadcrumb: { label: 'history' }
	});

	$stateProvider.state('dashboard.players.player.events',{
		templateUrl: 'scripts/controllers/dashboard/players/player/events/events.html',
		url:'/events',
		controller:'PlayerEventsController as controller',
		resolve: {
			loadMyFiles:function($ocLazyLoad) {
				return $ocLazyLoad.load({
					name:'lithium',
					files:['scripts/controllers/dashboard/players/player/events/events.js']
				})
			}
		},
		ncyBreadcrumb: { label: 'view' }
	});

	$stateProvider.state('dashboard.players.player.document',{
		templateUrl: 'scripts/controllers/dashboard/players/player/document/document.html',
		url:'/document?documentFileId',
		controller:'PlayerDocumentController as controller',
		resolve: {
			loadMyFiles:function($ocLazyLoad) {
				return $ocLazyLoad.load({
					name:'lithium',
					files:['scripts/controllers/dashboard/players/player/document/document.js']
				})
			}
		},
		ncyBreadcrumb: { label: 'view' }
	});

	$stateProvider.state('dashboard.players.player.documentold',{
		templateUrl: 'scripts/controllers/dashboard/players/player/document-old/document.html',
		url:'/document-old',
		controller:'PlayerDocumentOldController as controller',
		resolve: {
			loadMyFiles:function($ocLazyLoad) {
				return $ocLazyLoad.load({
					name:'lithium',
					files:['scripts/controllers/dashboard/players/player/document-old/document.js']
				})
			}
		},
		ncyBreadcrumb: { label: 'view' }
	});

	$stateProvider.state('dashboard.players.player.missions',{
		template: "<div ui-view></div>",
		url:'/promotions',
		resolve: {
			loadMyFiles:function($ocLazyLoad) {
				return $ocLazyLoad.load({
					name:'lithium',
					files:
						[
							'scripts/controllers/dashboard/players/player/missions/list/list.js'
						]
				})
			}
		},
		ncyBreadcrumb: { skip: true },
		redirectTo: "dashboard.players.player.missions.list"
	});

	$stateProvider.state('dashboard.players.player.missions.list',{
		templateUrl: 'scripts/controllers/dashboard/players/player/missions/list/list.html',
		url: '',
		controller: 'PlayerMissionsController as controller',
		ncyBreadcrumb: {
			parent: 'dashboard.players.player',
			label: 'missions'
		}
	});

	$stateProvider.state('dashboard.players.player.incentivegames',{
		templateUrl: 'scripts/controllers/dashboard/players/player/incentivegames/incentivegames.html',
		url:'/incentivegames',
		controller:'PlayerIncentiveGamesController as controller',
		resolve: {
			loadMyFiles:function($ocLazyLoad) {
				return $ocLazyLoad.load({
					name:'lithium',
					files:[ 'scripts/controllers/dashboard/players/player/incentivegames/incentivegames.js' ]
				})
			}
		},
		ncyBreadcrumb: { label: 'incentive games' },
		redirectTo: "dashboard.players.player.incentivegames.bethistory"
	});

	$stateProvider.state('dashboard.players.player.incentivegames.bethistory',{
		templateUrl: 'scripts/controllers/dashboard/players/player/incentivegames/bethistory/bethistory.html',
		url:'/bethistory',
		controller:'PlayerIncentiveGamesBetHistoryController as controller',
		resolve: {
			loadMyFiles:function($ocLazyLoad) {
				return $ocLazyLoad.load({
					name:'lithium',
					files:[ 'scripts/controllers/dashboard/players/player/incentivegames/bethistory/bethistory.js' ]
				})
			}
		},
		ncyBreadcrumb: { label: 'incentive games bet history' }
	});

	$stateProvider.state('dashboard.pushmsg', {
		template: "<div ui-view></div>",
		url:'/pushmsg',
		controller: function($scope) {
			$scope.setDescription = function(description) {
				$scope.description = description;
			}
		},
		ncyBreadcrumb: { skip: true },
		redirectTo: "dashboard.pushmsg.config"
	});
	$stateProvider.state('dashboard.pushmsg.config', {
		templateUrl:'scripts/controllers/dashboard/pushmsg/config/config.html',
		controller:'PushmsgConfigController as controller',
		url:'/config',
		resolve: {
			loadMyFiles:function($ocLazyLoad) {
				return $ocLazyLoad.load({
					name:'lithium',
					files: [
						'scripts/controllers/dashboard/pushmsg/config/config.js'
					]
				})
			}
		},
		ncyBreadcrumb: { skip: true }
	});

	$stateProvider.state('dashboard.pushmsg.config.providers', {
		templateUrl:'scripts/controllers/dashboard/pushmsg/config/providers/list.html',
		data: {
			pageTitle: "UI_NETWORK_ADMIN.PAGE.TITLE.PUSH_MESSAGE",
		},
		url:'/{domainName}/providers',
		controller:'PushmsgProvidersListController as controller',
		params: {
			domainName: ["$stateParams", function($stateParams) {
				return $stateParams.domainName;
			}]
		},
		resolve: {
			domainProviders: ["rest-pushmsg", "$stateParams", "$filter", "errors", function(rest, $stateParams, $filter, errors) {
				return rest.domainProviders($stateParams.domainName).then(function(domainProviders) {
					return $filter('orderBy')(domainProviders.plain(), 'provider.code');
				}).catch(function(error) {
					errors.catch("", false)(error)
				});
			}],
			loadMyFiles:function($ocLazyLoad) {
				return $ocLazyLoad.load({
					name:'lithium',
					files: [
						'scripts/controllers/dashboard/pushmsg/config/providers/list.js',
						'scripts/controllers/dashboard/pushmsg/config/providers/addprovider.js'
					]
				})
			}
		}
	});

	$stateProvider.state('dashboard.pushmsg.config.users', {
		template: "<div ui-view></div>",
		url: '/{domainName}/users',
		resolve: {
			domainName: ["$stateParams", function($stateParams) {
				return $stateParams.domainName;
			}],
			loadMyFiles:function($ocLazyLoad) {
				return $ocLazyLoad.load({
					name:'lithium',
					files: [
						'scripts/controllers/dashboard/pushmsg/config/users/list.js'
					]
				})
			}
		},
		redirectTo: "dashboard.pushmsg.config.users.list"
	});

	$stateProvider.state('dashboard.pushmsg.config.users.list', {
		templateUrl:'scripts/controllers/dashboard/pushmsg/config/users/list.html',
		url:'',
		controller:'PushMsgUsers as controller'
	});

	$stateProvider.state('dashboard.pushmsg.config.history', {
		template: "<div ui-view></div>",
		url: '/{domainName}/history',
		resolve: {
			domainName: ["$stateParams", function($stateParams) {
				return $stateParams.domainName;
			}],
			loadMyFiles:function($ocLazyLoad) {
				return $ocLazyLoad.load({
					name:'lithium',
					files: [
						'scripts/controllers/dashboard/pushmsg/config/history/list.js'
					]
				})
			}
		},
		redirectTo: "dashboard.pushmsg.config.history.list"
	});

	$stateProvider.state('dashboard.pushmsg.config.history.list', {
		templateUrl:'scripts/controllers/dashboard/pushmsg/config/history/list.html',
		url:'',
		controller:'PushMsgHistory as controller'
	});

	$stateProvider.state('dashboard.pushmsg.config.templates', {
		template: "<div ui-view></div>",
		url: '/{domainName}/templates',
		resolve: {
			domainName: ["$stateParams", function($stateParams) {
				return $stateParams.domainName;
			}],
			loadMyFiles:function($ocLazyLoad) {
				return $ocLazyLoad.load({
					name:'lithium',
					files: [
						'scripts/controllers/dashboard/pushmsg/config/templates/list.js'
					]
				})
			}
		},
		redirectTo: "dashboard.pushmsg.config.templates.list"
	});

	$stateProvider.state('dashboard.pushmsg.config.templates.list', {
		templateUrl:'scripts/controllers/dashboard/pushmsg/config/templates/list.html',
		url:'',
		controller:'PushMsgTemplates as controller'
	});

	$stateProvider.state('dashboard.pushmsg.config.templates.view',{
		templateUrl: 'scripts/controllers/dashboard/pushmsg/config/templates/view.html',
		url: '/{domainName}/:id/view',
		controller: 'PushMsgTemplate as controller',
		resolve: {
			template: ["PushMsgTemplateRest", "$stateParams", function(rest, $stateParams) {
				return rest.view($stateParams.id);
			}],
			loadMyFiles:function($ocLazyLoad) {
				return $ocLazyLoad.load({
					name:'lithium',
					files:['scripts/controllers/dashboard/pushmsg/config/templates/view.js']
				})
			}
		}
	});

	$stateProvider.state('dashboard.pushmsg.config.templates.edit', {
		templateUrl:'scripts/controllers/dashboard/pushmsg/config/templates/edit.html',
		url:'/:id/edit',
		controller:'PushMsgTemplateEdit as controller',
		resolve: {
			template:["PushMsgTemplateRest","$stateParams", "errors" ,function(rest, $stateParams, errors) {
				return rest.edit($stateParams.id).then(function(template) {
					return template;
				}).catch(function(error) {
					errors.catch("", false)(error)
				});
			}],
			loadMyFiles:function($ocLazyLoad) {
				return $ocLazyLoad.load({
					name:'lithium',
					files:['scripts/controllers/dashboard/pushmsg/config/templates/edit.js']
				})
			}
		}
	});

	// / SMS

	$stateProvider.state('dashboard.players.player.notifications',{
		template: "<div ui-view></div>",
		url:'/notifications',
		resolve: {
			loadMyFiles:function($ocLazyLoad) {
				return $ocLazyLoad.load({
					name:'lithium',
					files: [
						'scripts/controllers/dashboard/players/player/notifications/list/list.js',
						'scripts/controllers/dashboard/players/player/notifications/view/view.js'
					]
				})
			}
		},
		ncyBreadcrumb: { skip: true },
		redirectTo: "dashboard.players.player.notifications.list"
	});

	$stateProvider.state('dashboard.players.player.notifications.list',{
		templateUrl: 'scripts/controllers/dashboard/players/player/notifications/list/list.html',
		url: '',
		controller: 'PlayerNotificationsController as controller',
		ncyBreadcrumb: {
			parent: 'dashboard.players.player',
			label: 'notifications'
		}
	});

	$stateProvider.state('dashboard.players.player.notifications.view',{
		templateUrl: 'scripts/controllers/dashboard/players/player/notifications/view/view.html',
		url:'/:inboxId',
		controller: 'PlayerNotificationsViewController as controller',
		resolve: {
			inbox: ["NotificationRest", "$stateParams", "errors", function(rest, $stateParams, errors) {
				return rest.viewInbox($stateParams.inboxId).then(function(response) {
					return response;
				}).catch(function(error) {
					errors.catch("", false)(error)
				});
			}]
		},
		ncyBreadcrumb: {
			parent: 'dashboard.players.player.notifications',
			label: 'notifications'
		}
	});

	$stateProvider.state('dashboard.sms', {
		template: "<div ui-view></div>",
		url:'/sms',
		controller: function($scope) {
			$scope.setDescription = function(description) {
				$scope.description = description;
			}
		},
		ncyBreadcrumb: { skip: true }
	});

	$stateProvider.state('dashboard.sms.config', {
		templateUrl:'scripts/controllers/dashboard/sms/config/config.html',
		controller:'SMSConfigController as controller',
		url:'/config',
		resolve: {
			loadMyFiles:function($ocLazyLoad) {
				return $ocLazyLoad.load({
					name:'lithium',
					files: [
						'scripts/controllers/dashboard/sms/config/config.js'
					]
				})
			}
		},
		ncyBreadcrumb: { skip: true }
	});

	$stateProvider.state('dashboard.sms.config.providers', {
		templateUrl:'scripts/controllers/dashboard/sms/config/providers/list.html',
		data: {
			pageTitle: "UI_NETWORK_ADMIN.PAGE.TITLE.SMS",
		},
		url:'/{domainName}/providers',
		controller:'SMSProvidersListController as controller',
		params: {
			domainName: ["$stateParams", function($stateParams) {
				return $stateParams.domainName;
			}]
		},
		resolve: {
			domainProviders: ["rest-sms", "$stateParams", "$filter", "errors", function(rest, $stateParams, $filter, errors) {
				return rest.domainProviders($stateParams.domainName).then(function(domainProviders) {
					return $filter('orderBy')(domainProviders.plain(), 'provider.code');
				}).catch(function(error) {
					errors.catch("", false)(error)
				});
			}],
			loadMyFiles:function($ocLazyLoad) {
				return $ocLazyLoad.load({
					name:'lithium',
					files: [
						'scripts/controllers/dashboard/sms/config/providers/list.js',
						'scripts/controllers/dashboard/sms/config/providers/addprovider.js'
					]
				})
			}
		}
	});

	$stateProvider.state('dashboard.sms.config.templates', {
		template: "<div ui-view></div>",
		url: '/{domainName}/templates',
		resolve: {
			domainName: ["$stateParams", function($stateParams) {
				return $stateParams.domainName;
			}],
			loadMyFiles:function($ocLazyLoad) {
				return $ocLazyLoad.load({
					name:'lithium',
					files: [
						'scripts/controllers/dashboard/sms/config/templates/list.js'
					]
				})
			}
		},
		redirectTo: "dashboard.sms.config.templates.list"
	});

	$stateProvider.state('dashboard.sms.config.templates.list', {
		templateUrl:'scripts/controllers/dashboard/sms/config/templates/list.html',
		url:'',
		controller:'SMSTemplates as controller'
	});

	$stateProvider.state('dashboard.sms.config.templates.view',{
		templateUrl: 'scripts/controllers/dashboard/sms/config/templates/view.html',
		url: '/{domainName}/:id/view',
		controller: 'SMSTemplate as controller',
		resolve: {
			template: ["SMSTemplateRest", "$stateParams", function(rest, $stateParams) {
				return rest.view($stateParams.id);
			}],
			loadMyFiles:function($ocLazyLoad) {
				return $ocLazyLoad.load({
					name:'lithium',
					files:['scripts/controllers/dashboard/sms/config/templates/view.js']
				})
			}
		}
	});

	$stateProvider.state('dashboard.sms.config.templates.edit', {
		templateUrl:'scripts/controllers/dashboard/sms/config/templates/edit.html',
		url:'/:id/edit',
		controller:'SMSTemplateEdit as controller',
		resolve: {
			template:["SMSTemplateRest","$stateParams",function(rest,$stateParams) {
				return rest.edit($stateParams.id).then(function(template) {
					return template;
				}).catch(function(error) {
					errors.catch("", false)(error)
				});
			}],
			loadMyFiles:function($ocLazyLoad) {
				return $ocLazyLoad.load({
					name:'lithium',
					files:['scripts/controllers/dashboard/sms/config/templates/edit.js']
				})
			}
		}
	});

	$stateProvider.state('dashboard.sms.config.defaulttemplates', {
		template: "<div ui-view></div>",
		url: '/{domainName}/defaulttemplates',
		resolve: {
			domainName: ["$stateParams", function($stateParams) {
				return $stateParams.domainName;
			}],
			loadMyFiles:function($ocLazyLoad) {
				return $ocLazyLoad.load({
					name:'lithium',
					files: [
						'scripts/controllers/dashboard/sms/config/defaulttemplates/list.js'
					]
				})
			}
		},
		redirectTo: "dashboard.sms.config.defaulttemplates.list"
	});

	$stateProvider.state('dashboard.sms.config.defaulttemplates.list', {
		templateUrl:'scripts/controllers/dashboard/sms/config/defaulttemplates/list.html',
		url:'',
		controller:'DefaultSMSTemplates as controller'
	});

	$stateProvider.state('dashboard.sms.config.defaulttemplates.view',{
		templateUrl: 'scripts/controllers/dashboard/sms/config/defaulttemplates/view.html',
		url: '/:id/view',
		controller: 'DefaultSMSTemplate as controller',
		resolve: {
			template: ["SMSTemplateRest", "$stateParams", function(rest, $stateParams) {
				return rest.viewDefaultSMSTemplate($stateParams.id);
			}],
			loadMyFiles:function($ocLazyLoad) {
				return $ocLazyLoad.load({
					name:'lithium',
					files:['scripts/controllers/dashboard/sms/config/defaulttemplates/view.js']
				})
			}
		}
	});

	$stateProvider.state('dashboard.sms.queue',{
		template: "<div ui-view></div>",
		url:'/smsqueue',
		resolve: {
			domain: function() { return },
			loadMyFiles:function($ocLazyLoad) {
				return $ocLazyLoad.load({
					name:'lithium',
					files: [
						'scripts/controllers/dashboard/sms/queue/list/list.js'
					]
				})
			}
		},
		ncyBreadcrumb: { skip: true },
		redirectTo: "dashboard.sms.queue.list"
	});

	$stateProvider.state('dashboard.sms.queue.list',{
		templateUrl:'scripts/controllers/dashboard/sms/queue/list/list.html',
		data: {
			pageTitle: "UI_NETWORK_ADMIN.PAGE.TITLE.SMS_QUEUE",
		},
		url:'',
		controller:'SMSQueueController as controller',
		ncyBreadcrumb: {
			parent: 'dashboard.smsqueue',
			label: 'sms queue'
		}
	});

	$stateProvider.state('dashboard.sms.queue.view',{
		templateUrl: 'scripts/controllers/dashboard/sms/queue/view/view.html',
		url:'/:id',
		controller: 'SMSQueueViewController as controller',
		resolve: {
			sms: ["rest-sms", "$stateParams", "errors", function(rest, $stateParams, errors) {
				return rest.findById($stateParams.id).then(function(sms) {
					return sms;
				}).catch(function(error) {
					errors.catch("", false)(error)
				});
			}],
			loadMyFiles:function($ocLazyLoad) {
				return $ocLazyLoad.load({
					name:'lithium',
					files:['scripts/controllers/dashboard/sms/queue/view/view.js']
				})
			}
		},
		ncyBreadcrumb: { label: 'sms' }
	});


	$stateProvider.state('dashboard.mail', {
		template: "<div ui-view></div>",
		url:'/mail',
		controller: function($scope) {
			$scope.setDescription = function(description) {
				$scope.description = description;
			}
		},
		ncyBreadcrumb: { skip: true }
	});

	$stateProvider.state('dashboard.mail.config', {
		templateUrl:'scripts/controllers/dashboard/mail/config/config.html',
		data: {
			pageTitle: "UI_NETWORK_ADMIN.PAGE.TITLE.MAIL",
		},
		controller:'MailConfigController as controller',
		url:'/config',
		resolve: {
			loadMyFiles:function($ocLazyLoad) {
				return $ocLazyLoad.load({
					name:'lithium',
					files: [
						'scripts/controllers/dashboard/mail/config/config.js'
					]
				})
			}
		},
		ncyBreadcrumb: { skip: true }
	});

	$stateProvider.state('dashboard.mail.config.providers', {
		templateUrl:'scripts/controllers/dashboard/mail/config/providers/list.html',
		data: {
			pageTitle: "UI_NETWORK_ADMIN.PAGE.TITLE.MAIL",
		},
		url:'/{domainName}/providers',
		controller:'MailProvidersListController as controller',
		params: {
			domainName: ["$stateParams", function($stateParams) {
				return $stateParams.domainName;
			}]
		},
		resolve: {
			domainProviders: ["mailRest", "$stateParams", "$filter", "errors", function(rest, $stateParams, $filter, errors) {
				return rest.domainProviders($stateParams.domainName).then(function(domainProviders) {
					return $filter('orderBy')(domainProviders.plain(), 'provider.code');
				}).catch(function(error) {
					errors.catch("", false)(error)
				});
			}],
			loadMyFiles:function($ocLazyLoad) {
				return $ocLazyLoad.load({
					name:'lithium',
					files: [
						'scripts/controllers/dashboard/mail/config/providers/list.js',
						'scripts/controllers/dashboard/mail/config/providers/addprovider.js'
					]
				})
			}
		}
	});

	$stateProvider.state('dashboard.mail.config.templates', {
		template: "<div ui-view></div>",
		url: '/{domainName}/templates',
		resolve: {
			domainName: ["$stateParams", function($stateParams) {
				return $stateParams.domainName;
			}],
			loadMyFiles:function($ocLazyLoad) {
				return $ocLazyLoad.load({
					name:'lithium',
					files: [
						'scripts/controllers/dashboard/mail/config/templates/list.js'
					]
				})
			}
		},
		redirectTo: "dashboard.mail.config.templates.list"
	});

	$stateProvider.state('dashboard.mail.config.templates.list', {
		templateUrl:'scripts/controllers/dashboard/mail/config/templates/list.html',
		url:'',
		controller:'EmailTemplates as controller'
	});

	$stateProvider.state('dashboard.mail.config.templates.view',{
		templateUrl: 'scripts/controllers/dashboard/mail/config/templates/view.html',
		url: '/{domainName}/:id/view',
		controller: 'EmailTemplate as controller',
		resolve: {
			template: ["EmailTemplateRest", "$stateParams", function(rest, $stateParams) {
				return rest.view($stateParams.id);
			}],
			loadMyFiles:function($ocLazyLoad) {
				return $ocLazyLoad.load({
					name:'lithium',
					files:['scripts/controllers/dashboard/mail/config/templates/view.js']
				})
			}
		}
	});

	$stateProvider.state('dashboard.mail.config.templates.edit', {
		templateUrl:'scripts/controllers/dashboard/mail/config/templates/edit.html',
		url:'/:id/edit',
		controller:'EmailTemplateEdit as controller',
		resolve: {
			template:["EmailTemplateRest","$stateParams",function(rest,$stateParams) {
				return rest.edit($stateParams.id).then(function(template) {
					return template;
				}).catch(function(error) {
					errors.catch("", false)(error)
				});
			}],
			loadMyFiles:function($ocLazyLoad) {
				return $ocLazyLoad.load({
					name:'lithium',
					files:['scripts/controllers/dashboard/mail/config/templates/edit.js']
				})
			}
		}
	});

	$stateProvider.state('dashboard.mail.config.defaulttemplates', {
		template: "<div ui-view></div>",
		url: '/{domainName}/defaulttemplates',
		resolve: {
			domainName: ["$stateParams", function($stateParams) {
				return $stateParams.domainName;
			}],
			loadMyFiles:function($ocLazyLoad) {
				return $ocLazyLoad.load({
					name:'lithium',
					files: [
						'scripts/controllers/dashboard/mail/config/defaulttemplates/list.js'
					]
				})
			}
		},
		redirectTo: "dashboard.mail.config.defaulttemplates.list"
	});

	$stateProvider.state('dashboard.mail.config.defaulttemplates.list', {
		templateUrl:'scripts/controllers/dashboard/mail/config/defaulttemplates/list.html',
		url:'',
		controller:'DefaultEmailTemplates as controller'
	});

	$stateProvider.state('dashboard.mail.config.defaulttemplates.view',{
		templateUrl: 'scripts/controllers/dashboard/mail/config/defaulttemplates/view.html',
		url: '/:id/view',
		controller: 'DefaultEmailTemplate as controller',
		resolve: {
			template: ["EmailTemplateRest", "$stateParams", function(rest, $stateParams) {
				return rest.viewDefaultEmailTemplate($stateParams.id);
			}],
			loadMyFiles:function($ocLazyLoad) {
				return $ocLazyLoad.load({
					name:'lithium',
					files:['scripts/controllers/dashboard/mail/config/defaulttemplates/view.js']
				})
			}
		}
	});

	$stateProvider.state('dashboard.mail.queue',{
		template: "<div ui-view></div>",
		url:'/mailqueue',
		resolve: {
			domain: function() { return },
			loadMyFiles:function($ocLazyLoad) {
				return $ocLazyLoad.load({
					name:'lithium',
					files: [
						'scripts/controllers/dashboard/mail/queue/list/list.js'
					]
				})
			}
		},
		ncyBreadcrumb: { skip: true },
		redirectTo: "dashboard.mail.queue.list"
	});

	$stateProvider.state('dashboard.mail.queue.list',{
		templateUrl:'scripts/controllers/dashboard/mail/queue/list/list.html',
		data: {
			pageTitle: "UI_NETWORK_ADMIN.PAGE.TITLE.MAIL_QUEUE",
		},
		url:'',
		controller:'MailQueueController as controller',
		ncyBreadcrumb: {
			parent: 'dashboard.mail.queue',
			label: 'mail queue'
		}
	});

	$stateProvider.state('dashboard.mail.queue.view',{
		templateUrl: 'scripts/controllers/dashboard/mail/queue/view/view.html',
		url:'/:id',
		controller: 'MailQueueViewController as controller',
		resolve: {
			mail: ["mailRest", "$stateParams", "errors", function(rest, $stateParams, errors) {
				return rest.findById($stateParams.id).then(function(mail) {
					return mail;
				}).catch(function(error) {
					errors.catch("", false)(error)
				});
			}],
			loadMyFiles:function($ocLazyLoad) {
				return $ocLazyLoad.load({
					name:'lithium',
					files:['scripts/controllers/dashboard/mail/queue/view/view.js']
				})
			}
		},
		ncyBreadcrumb: { label: 'mail' }
	});


	$stateProvider.state('dashboard.raf', {
		template: "<div ui-view></div>",
		url:'/raf',
		controller: function($scope) {
			$scope.setDescription = function(description) {
				$scope.description = description;
			}
		},
		ncyBreadcrumb: { skip: true }
	});

	$stateProvider.state('dashboard.raf.config', {
		templateUrl:'scripts/controllers/dashboard/raf/config/config.html',
		controller:'RAFConfigController as controller',
		url:'/config',
		resolve: {
			loadMyFiles:function($ocLazyLoad) {
				return $ocLazyLoad.load({
					name:'lithium',
					files: [
						'scripts/controllers/dashboard/raf/config/config.js'
					]
				})
			}
		},
		ncyBreadcrumb: { skip: true }
	});

	$stateProvider.state('dashboard.raf.config.settings', {
		templateUrl:'scripts/controllers/dashboard/raf/config/settings/settings.html',
		data: {
			pageTitle: "UI_NETWORK_ADMIN.PAGE.TITLE.RAF_CONFIGURATION",
		},
		url:'/{domainName}/settings',
		controller:'RAFSettingsController as controller',
		params: {
			domainName: ["$stateParams", function($stateParams) {
				return $stateParams.domainName;
			}]
		},
		resolve: {
			config: ["RAFRest", "$stateParams", "errors", function(rest, $stateParams, errors) {
				return rest.getConfiguration($stateParams.domainName).then(function(config) {
					return config.plain();
				}).catch(function(error) {
					errors.catch("", false)(error)
				});
			}],
			loadMyFiles:function($ocLazyLoad) {
				return $ocLazyLoad.load({
					name:'lithium',
					files: [
						'scripts/controllers/dashboard/raf/config/settings/settings.js'
					]
				})
			}
		}
	});

	$stateProvider.state('dashboard.raf.clicks',{
		template: "<div ui-view></div>",
		url:'/clicks',
		resolve: {
			domain: function() { return },
			loadMyFiles:function($ocLazyLoad) {
				return $ocLazyLoad.load({
					name:'lithium',
					files: [
						'scripts/controllers/dashboard/raf/clicks/list/list.js'
					]
				})
			}
		},
		ncyBreadcrumb: { skip: true },
		redirectTo: "dashboard.raf.clicks.list"
	});

	$stateProvider.state('dashboard.raf.clicks.list',{
		templateUrl:'scripts/controllers/dashboard/raf/clicks/list/list.html',
		data: {
			pageTitle: "UI_NETWORK_ADMIN.PAGE.TITLE.RAF_CLICKS",
		},
		url:'',
		controller:'RAFClicksController as controller',
		ncyBreadcrumb: {
			parent: 'dashboard.raf.clicks',
			label: 'raf clicks'
		}
	});

	$stateProvider.state('dashboard.raf.signups',{
		template: "<div ui-view></div>",
		url:'/signups',
		resolve: {
			domain: function() { return },
			loadMyFiles:function($ocLazyLoad) {
				return $ocLazyLoad.load({
					name:'lithium',
					files: [
						'scripts/controllers/dashboard/raf/signups/list/list.js'
					]
				})
			}
		},
		ncyBreadcrumb: { skip: true },
		redirectTo: "dashboard.raf.signups.list"
	});

	$stateProvider.state('dashboard.raf.signups.list',{
		templateUrl:'scripts/controllers/dashboard/raf/signups/list/list.html',
		data: {
			pageTitle: "UI_NETWORK_ADMIN.PAGE.TITLE.RAF_SIGNUPS",
		},
		url:'',
		controller:'RAFSignupsController as controller',
		ncyBreadcrumb: {
			parent: 'dashboard.raf.signups',
			label: 'raf signups'
		}
	});

	$stateProvider.state('dashboard.raf.conversions',{
		template: "<div ui-view></div>",
		url:'/conversions',
		resolve: {
			domain: function() { return },
			loadMyFiles:function($ocLazyLoad) {
				return $ocLazyLoad.load({
					name:'lithium',
					files: [
						'scripts/controllers/dashboard/raf/conversions/list/list.js'
					]
				})
			}
		},
		ncyBreadcrumb: { skip: true },
		redirectTo: "dashboard.raf.conversions.list"
	});

	$stateProvider.state('dashboard.raf.conversions.list',{
		templateUrl:'scripts/controllers/dashboard/raf/conversions/list/list.html',
		data: {
			pageTitle: "UI_NETWORK_ADMIN.PAGE.TITLE.RAF_CONVERSIONS",
		},
		url:'',
		controller:'RAFConversionsController as controller',
		ncyBreadcrumb: {
			parent: 'dashboard.raf.conversions',
			label: 'raf conversions'
		}
	});

	$stateProvider.state('dashboard.cashier', {
		template: "<div ui-view></div>",
		url:'/cashier',
		controller: function($scope) {
			$scope.setDescription = function(description) {
				$scope.description = description;
			}
		},
		resolve: {
			methods: ["rest-cashier", function(rest) {
				return rest.methods().then(function(methods) { return methods.plain(); });
			}]
		},
		ncyBreadcrumb: { skip: true }
	});

	$stateProvider.state('dashboard.cashier.config', {
		templateUrl:'scripts/controllers/dashboard/cashier/config/config.html',
		data: {
			pageTitle: "UI_NETWORK_ADMIN.PAGE.TITLE.CASHIER_CONFIGURATION",
		},
		controller:'CashierConfigController as controller',
		url:'/config',
		resolve: {
			loadMyFiles:function($ocLazyLoad) {
				return $ocLazyLoad.load({
					name:'lithium',
					files: [
						'scripts/controllers/dashboard/cashier/config/config.js'
					]
				})
			}
		},
		ncyBreadcrumb: { skip: true }
	});

	$stateProvider.state('dashboard.cashier.config.methods', {
		templateUrl:'scripts/controllers/dashboard/cashier/config/methods/methods.html',
		controller:'CashierMethodsController as controller',
		url:'/{domainName}/methods',
		params: {
			domainName: ["$stateParams", function($stateParams) {
				return $stateParams.domainName;
			}]
		},
		resolve: {
			loadMyFiles:function($ocLazyLoad) {
				return $ocLazyLoad.load({
					name:'lithium',
					files: [
						'scripts/controllers/dashboard/cashier/config/methods/methods.js'
					]
				})
			}
		},
		redirectTo: "dashboard.cashier.config.methods.list"
	});

	$stateProvider.state('dashboard.cashier.config.methods.list', {
		templateUrl:'scripts/controllers/dashboard/cashier/config/methods/list.html',
		controller:'CashierMethodsListController as controller',
		url:'/list',
		params: {
			type: 'deposit',
		},
		resolve: {
			domainMethods: ["rest-cashier", "$stateParams", "errors", function(rest, $stateParams, errors) {
				return rest.domainMethods($stateParams.domainName, $stateParams.type).then(function(methods) {
					return methods.plain();
				}).catch(function(error) {
					errors.catch("", false)(error)
				});
			}],
			loadMyFiles:function($ocLazyLoad) {
				return $ocLazyLoad.load({
					name:'lithium',
					files: [
						'scripts/controllers/dashboard/cashier/config/methods/list.js'
					]
				})
			}
		}
	});

	$stateProvider.state('dashboard.cashier.config.profiles', {
		templateUrl:'scripts/controllers/dashboard/cashier/config/profiles/list.html',
		url:'/{domainName}/profiles',
		controller:'CashierProfilesListController as controller',
		params: {
			domainName: ["$stateParams", function($stateParams) {
				return $stateParams.domainName;
			}]
		},
		resolve: {
			profiles: ["rest-cashier", "$stateParams", "errors", "$filter", function(rest, $stateParams, errors, $filter) {
				return rest.profiles($stateParams.domainName).then(function(profiles) {
					return $filter('orderBy')(profiles.plain(), 'code');
				}).catch(function(error) {
					errors.catch("", false)(error)
				});
			}],
			loadMyFiles:function($ocLazyLoad) {
				return $ocLazyLoad.load({
					name:'lithium',
					files: [
						'scripts/controllers/dashboard/cashier/config/profiles/list.js'
					]
				})
			}
		}
	});
	$stateProvider.state('dashboard.cashier.config.profile', {
		templateUrl:'scripts/controllers/dashboard/cashier/config/profiles/profile/profile.html',
		controller:'CashierDomainProfileController as controller',
		url:'/{domainName}/profile/{profileId}',
		resolve: {
			domainName: ["rest-cashier", "$stateParams", "errors", function(rest, $stateParams, errors) {
				return $stateParams.domainName;
			}],
			profile: ["rest-cashier", "$stateParams", "errors", function(rest, $stateParams, errors) {
				return rest.profileById($stateParams.domainName, $stateParams.profileId).then(function(p) {
					return p;
				}).catch(function(error) {
					errors.catch("", false)(error)
				});
			}],
			loadMyFiles:function($ocLazyLoad) {
				return $ocLazyLoad.load({
					name:'lithium',
					files: [
						'scripts/controllers/dashboard/cashier/config/profiles/profile/profile.js'
					]
				})
			}
		},
		redirectTo: "dashboard.cashier.config.profile.edit"
	});
	$stateProvider.state('dashboard.cashier.config.profile.edit', {
		templateUrl:'scripts/controllers/dashboard/cashier/config/profiles/profile/edit.html',
		url:'/edit',
		controller:'CashierDomainProfileEditController as controller',
		resolve: {
			loadMyFiles:function($ocLazyLoad) {
				return $ocLazyLoad.load({
					name:'lithium',
					files: [
						'scripts/controllers/dashboard/cashier/config/profiles/profile/edit.js'
					]
				})
			}
		}
	});
	$stateProvider.state('dashboard.cashier.config.profile.users', {
		templateUrl:'scripts/controllers/dashboard/cashier/config/profiles/profile/users.html',
		url:'/users',
		controller:'CashierDomainProfileUsersController as controller',
		resolve: {
			loadMyFiles:function($ocLazyLoad) {
				return $ocLazyLoad.load({
					name:'lithium',
					files: [
						'scripts/controllers/dashboard/cashier/config/profiles/profile/users.js'
					]
				})
			}
		}
	});
	$stateProvider.state('dashboard.cashier.config.profile.processors', {
		templateUrl:'scripts/controllers/dashboard/cashier/config/profiles/profile/processors.html',
		url:'/processors',
		controller:'CashierDomainProfileProcessorsController as controller',
		resolve: {
			loadMyFiles:function($ocLazyLoad) {
				return $ocLazyLoad.load({
					name:'lithium',
					files: [
						'scripts/controllers/dashboard/cashier/config/profiles/profile/processors.js'
					]
				})
			}
		}
	});

	$stateProvider.state('dashboard.cashier.beta-transactions',{
		templateUrl:'scripts/controllers/dashboard/cashier/transactions/beta/transactions.html',
		url:'/beta-transactions',
		controller:'CashierBetaTransController as controller',
			resolve: {
				loadMyFiles:function($ocLazyLoad) {
					return $ocLazyLoad.load({
						name:'lithium',
						files: [
							'scripts/controllers/dashboard/cashier/transactions/beta/transactions.js'
						]
					})
				}
			},
		ncyBreadcrumb: { skip: true },
		redirectTo: "dashboard.cashier.beta-transactions.list"
	});


	$stateProvider.state('dashboard.cashier.beta-transactions.list', {
		templateUrl:'scripts/controllers/dashboard/cashier/transactions/beta/list.html',
		url:'/{domainName}',
		controller:'CashierTransListBetaController as controller',
		params: {
			domainName: ["$stateParams", function($stateParams) {
				return $stateParams.domainName;
			}],
		},
		ncyBreadcrumb: { skip: true },
		resolve: {
			loadMyFiles:function($ocLazyLoad) {
				return $ocLazyLoad.load({
					name:'lithium',
					files: [
						'scripts/controllers/dashboard/cashier/transactions/beta/list.js'
					]
				})
			}
		}
	});


	$stateProvider.state('dashboard.cashier.transactions', {
		templateUrl:'scripts/controllers/dashboard/cashier/transactions/transactions.html',
		data: {
			pageTitle: "UI_NETWORK_ADMIN.PAGE.TITLE.CASHIER_TRANSACTIONS",
		},
		url:'/transactions',
		controller:'CashierTransController as controller',
		resolve: {
			loadMyFiles:function($ocLazyLoad) {
				return $ocLazyLoad.load({
					name:'lithium',
					files: [
						'scripts/controllers/dashboard/cashier/transactions/transactions.js'
					]
				})
			}
		},
		redirectTo: "dashboard.cashier.transactions.list"
	});
	$stateProvider.state('dashboard.cashier.transactions.list', {
		templateUrl:'scripts/controllers/dashboard/cashier/transactions/list.html',
		url:'/{domainName}',
		controller:'CashierTransListController as controller',
		params: {
			domainName: ["$stateParams", function($stateParams) {
				return $stateParams.domainName;
			}],
			tranType: ["$stateParams", function($stateParams) {
				return $stateParams.tranType;
			}],
			granularity: ["$stateParams", function($stateParams) {
				return $stateParams.granularity;
			}],
			offset: ["$stateParams", function($stateParams) {
				return $stateParams.offset;
			}],
			status: ["$stateParams", function($stateParams) {
				return $stateParams.status;
			}],
			paymentType: ["$stateParams", function($stateParams) {
				return $stateParams.paymentType;
			}]
		},
		resolve: {
			loadMyFiles:function($ocLazyLoad) {
				return $ocLazyLoad.load({
					name:'lithium',
					files: [
						'scripts/controllers/dashboard/cashier/transactions/list.js'
					]
				})
			}
		}
	});

	$stateProvider.state('dashboard.cashier.transaction', {
		templateUrl:'scripts/controllers/dashboard/cashier/transactions/view.html',
		url:'/{domainName}/view/{tranId}',
		controller:'CashierTranViewController as controller',
		resolve: {
			loadMyFiles:function($ocLazyLoad) {
				return $ocLazyLoad.load({
					name:'lithium',
					files: [
						'scripts/controllers/dashboard/cashier/transactions/view.js'
					]
				})
			}
		}
	});

	$stateProvider.state('dashboard.cashier.transactions.add', {
		templateUrl:'scripts/controllers/dashboard/cashier/transactions/add.html',
		url:'/{domainName}/add',
		controller:'CashierTranAddController as controller',
		resolve: {
			loadMyFiles:function($ocLazyLoad) {
				return $ocLazyLoad.load({
					name:'lithium',
					files: [
						'scripts/controllers/dashboard/cashier/transactions/add.js'
					]
				})
			}
		}
	});

	$stateProvider.state('dashboard.cashier.bank_account_lookup', {
		templateUrl: 'scripts/controllers/dashboard/cashier/bank-account-lookup/bank-account-lookup.html',
		url: '/bank-account-lookup',
		controller: 'CashierBankAccountLookupController as controller',
		resolve: {
			loadMyFiles: function($ocLazyLoad) {
				return $ocLazyLoad.load({
					name:'lithium',
					files: [
						'scripts/controllers/dashboard/cashier/bank-account-lookup/bank-account-lookup.js'
					]
				})
			}
		}
	});

	$stateProvider.state('dashboard.cashier.bank_account_lookup.page', {
		templateUrl: 'scripts/controllers/dashboard/cashier/bank-account-lookup/page/bank-account-lookup-page.html',
		url: '/{domainName}/page',
		controller: 'CashierBankAccountLookupPageController as controller',
		resolve: {
			domain: ["rest-domain", "$stateParams", "errors", function(rest, $stateParams, errors) {
				return rest.findByName($stateParams.domainName).then(function (domain) {
					return domain;
				}).catch(function (error) {
					errors.catch("", false)(error)
				});
			}],
			loadMyFiles: function($ocLazyLoad) {
				return $ocLazyLoad.load({
					name: 'lithium',
					files: [
						'scripts/controllers/dashboard/cashier/bank-account-lookup/page/bank-account-lookup-page.js'
					]
				})
			}
		}
	});

	$stateProvider.state('dashboard.bonuses', {
		template: "<div ui-view></div>",
		url:'/bonuses',
		resolve: {
			loadMyFiles:function($ocLazyLoad) {
				return $ocLazyLoad.load({
					name:'lithium',
					files: [
						'scripts/controllers/dashboard/bonuses/list/list.js',
						'scripts/controllers/dashboard/bonuses/add/add.js'
					]
				})
			}
		},
		ncyBreadcrumb: { skip: true },
		redirectTo: "dashboard.bonuses.list"
	});

	$stateProvider.state('dashboard.bonuses.list', {
		templateUrl:'scripts/controllers/dashboard/bonuses/list/list.html',
		data: {
			pageTitle: "UI_NETWORK_ADMIN.PAGE.TITLE.BONUSES",
		},
		url:'',
		controller:'BonusListController as controller',
		ncyBreadcrumb: {
			parent: 'dashboard.bonuses',
			label: 'bonuses'
		}
	});

	$stateProvider.state('dashboard.bonuses.add', {
		templateUrl:'scripts/controllers/dashboard/bonuses/add/add.html',
		url:'/add',
		controller:'BonusAddController as controller',
		resolve: {
			domains: ["rest-domain", "$rootScope", function(rest, $rootScope) {
				return rest.children($rootScope.principal.domainName).then(function(domains) { return domains.plain(); });
			}],
			types: ["$translate", function($translate) {
				var types = [];
				$translate('GLOBAL.BONUS.TYPE.0').then(function (translations) {
					types.push({id:0, name:translations});
				});
				$translate('GLOBAL.BONUS.TYPE.1').then(function (translations) {
					types.push({id:1, name:translations});
				});
				$translate('GLOBAL.BONUS.TYPE.2').then(function (translations) {
					types.push({id:2, name:translations});
				});
				$translate('GLOBAL.BONUS.TYPE.3').then(function (translations) {
					types.push({id:3, name:translations});
				}, function () { types.push({id:3,name:"untranslated new bonus type"}); }) ;
				return types;
			}]
		},
		ncyBreadcrumb: {
			parent: 'dashboard.bonuses',
			label: 'add'
		}
	});

	$stateProvider.state('dashboard.bonuses.bonus', {
		templateUrl: "scripts/controllers/dashboard/bonuses/bonus/bonus.html",
		url:'/bonus/{bonusId}',
		controller: 'BonusController as controller',
		resolve: {
			bonus: ["rest-casino", "$stateParams", "errors", function(rest, $stateParams, errors) {
				return rest.findBonusById($stateParams.bonusId).then(function(bonus) {
					return bonus.plain();
				}).catch(function(error) {
					errors.catch("", false)(error)
				});
			}],
			loadMyFiles:function($ocLazyLoad) {
				return $ocLazyLoad.load({
					name:'lithium',
					files: [
						'scripts/controllers/dashboard/bonuses/bonus/bonus.js'
					]
				})
			}
		},
		ncyBreadcrumb: { skip: true }
	});

	$stateProvider.state('dashboard.bonuses.edit', {
		templateUrl:'scripts/controllers/dashboard/bonuses/bonus/edit/edit.html',
		url:'/bonus/{bonusId}/edit/{bonusRevisionId}',
		controller:'BonusEditController as controller',
		resolve: {
			bonus: ["rest-casino", "$stateParams", function(casinoRest, $stateParams) {
				return casinoRest.findBonusById($stateParams.bonusId).then(function(response) {
					var freshBonus = response.plain();
					return freshBonus;
				}).catch(function(error) {
					errors.catch("", false)(error)
				});
			}],
			bonusRevision: ["rest-casino", "$stateParams", "errors", function(rest, $stateParams, errors) {
				return rest.findByBonusRevisionId($stateParams.bonusRevisionId).then(function(bonusRevision) {
					return bonusRevision.plain();
				}).catch(function(error) {
					errors.catch("", false)(error)
				});
			}],
			types: ["$translate", function($translate) {
				var types = [];
				$translate('GLOBAL.BONUS.TYPE.0').then(function (translations) {
					types.push({id:0, name:translations});
				});
				$translate('GLOBAL.BONUS.TYPE.1').then(function (translations) {
					types.push({id:1, name:translations});
				});
				$translate('GLOBAL.BONUS.TYPE.2').then(function (translations) {
					types.push({id:2, name:translations});
				});
				$translate('GLOBAL.BONUS.TYPE.3').then(function (translations) {
					types.push({id:3, name:translations});
				}, function () { types.push({id:3,name:"untranslated new bonus type"}); }) ;
				return types;
			}],
			loadMyFiles:function($ocLazyLoad) {
				return $ocLazyLoad.load({
					name:'lithium',
					files:[ 'scripts/controllers/dashboard/bonuses/bonus/edit/edit.js' ]
				})
			}
		},
		ncyBreadcrumb: {
			label: 'edit'
		},
		redirectTo: "dashboard.bonuses.edit.summary"
	});

	$stateProvider.state('dashboard.bonuses.edit.summary', {
		templateUrl:'scripts/controllers/dashboard/bonuses/bonus/edit/summary.html'
	});
	$stateProvider.state('dashboard.bonuses.edit.freespins', {
		templateUrl:'scripts/controllers/dashboard/bonuses/bonus/edit/freespins.html'
	});
	$stateProvider.state('dashboard.bonuses.edit.casinoChip', {
		templateUrl:'scripts/controllers/dashboard/bonuses/bonus/edit/casinoChip.html'
	});
	$stateProvider.state('dashboard.bonuses.edit.instantReward', {
		templateUrl:'scripts/controllers/dashboard/bonuses/bonus/edit/instantReward.html'
	});
	$stateProvider.state('dashboard.bonuses.edit.instantRewardFreespin', {
		templateUrl:'scripts/controllers/dashboard/bonuses/bonus/edit/instantRewardFreespin.html'
	});
	$stateProvider.state('dashboard.bonuses.edit.unlockgame', {
		templateUrl:'scripts/controllers/dashboard/bonuses/bonus/edit/unlockgame.html'
	});
	$stateProvider.state('dashboard.bonuses.edit.percentages', {
		templateUrl:'scripts/controllers/dashboard/bonuses/bonus/edit/gamepercentages.html'
	});
	$stateProvider.state('dashboard.bonuses.edit.categories', {
		templateUrl:'scripts/controllers/dashboard/bonuses/bonus/edit/gamecategories.html'
	});

	$stateProvider.state('dashboard.bonuses.bonus.dashboard', {
		templateUrl:'scripts/controllers/dashboard/bonuses/bonus/dashboard.html',
		url:'/dashboard/{bonusRevisionId}',
		controller:'BonusDashboardController as controller',
		resolve: {
			bonusRevision: ["rest-casino", "$stateParams", "errors", function(rest, $stateParams, errors) {
				return rest.findByBonusRevisionId($stateParams.bonusRevisionId).then(function(bonusRevision) {
					return bonusRevision.plain();
				}).catch(function(error) {
					errors.catch("", false)(error)
				});
			}],
			loadMyFiles:function($ocLazyLoad) {
				return $ocLazyLoad.load({
					name:'lithium',
					files:[ 'scripts/controllers/dashboard/bonuses/bonus/dashboard.js' ]
				})
			}
		}
	});

	$stateProvider.state('dashboard.bonuses.bonus.activation', {
		templateUrl:'scripts/controllers/dashboard/bonuses/bonus/activation.html',
		url:'/activation/{bonusRevisionId}',
		controller:'BonusActivationController as controller',
		resolve: {
			bonusRevision: ["rest-casino", "$stateParams", "errors", function(rest, $stateParams, errors) {
				return rest.findByBonusRevisionId($stateParams.bonusRevisionId).then(function(bonusRevision) {
					return bonusRevision.plain();
				}).catch(function(error) {
					errors.catch("", false)(error)
				});
			}],
			loadMyFiles:function($ocLazyLoad) {
				return $ocLazyLoad.load({
					name:'lithium',
					files:[ 'scripts/controllers/dashboard/bonuses/bonus/activation.js' ]
				})
			}
		}
	});

	$stateProvider.state('dashboard.bonuses.bonus.pending', {
		templateUrl:'scripts/controllers/dashboard/bonuses/bonus/pending.html',
		url:'/pending/{bonusRevisionId}',
		controller:'BonusPendingController as controller',
		resolve: {
			bonusRevision: ["rest-casino", "$stateParams", "errors", function(rest, $stateParams, errors) {
				return rest.findByBonusRevisionId($stateParams.bonusRevisionId).then(function(bonusRevision) {
					return bonusRevision.plain();
				}).catch(function(error) {
					errors.catch("", false)(error)
				});
			}],
			loadMyFiles:function($ocLazyLoad) {
				return $ocLazyLoad.load({
					name:'lithium',
					files:[ 'scripts/controllers/dashboard/bonuses/bonus/pending.js' ]
				})
			}
		}
	});

	$stateProvider.state('dashboard.bonuses.bonus.csvbonusallocation', {
		templateUrl:'scripts/controllers/dashboard/bonuses/bonus/csvbonusallocation.html',
		url:'/csvbonusallocation/{bonusRevisionId}',
		controller:'CsvBonusAllocationController as controller',
		resolve: {
			bonusRevision: ["rest-casino", "$stateParams", "errors", function(rest, $stateParams, errors) {
				return rest.findByBonusRevisionId($stateParams.bonusRevisionId).then(function(bonusRevision) {
					return bonusRevision.plain();
				}).catch(function(error) {
					errors.catch("", false)(error)
				});
			}],
			loadMyFiles:function($ocLazyLoad) {
				return $ocLazyLoad.load({
					name:'lithium',
					files:[ 'scripts/controllers/dashboard/bonuses/bonus/csvbonusallocation.js' ]
				})
			}
		}
	});

	$stateProvider.state('dashboard.bonuses.bonus.view', {
		templateUrl:'scripts/controllers/dashboard/bonuses/bonus/view.html',
		url:'/view/{bonusRevisionId}',
		resolve: {
			bonusRevision: ["rest-casino", "$stateParams", "errors", function(rest, $stateParams, errors) {
				return rest.findByBonusRevisionId($stateParams.bonusRevisionId).then(function(bonusRevision) {
					return bonusRevision.plain();
				}).catch(function(error) {
					errors.catch("", false)(error)
				});
			}]
		},
		redirectTo: "dashboard.bonuses.bonus.view.summary"
	});

	$stateProvider.state('dashboard.bonuses.bonus.nocurrent', {
		templateUrl:'scripts/controllers/dashboard/bonuses/bonus/nocurrent.html',
		url:'/nocurrent'
	});

	$stateProvider.state('dashboard.bonuses.bonus.view.summary', {
		templateUrl:'scripts/controllers/dashboard/bonuses/bonus/view/view.html',
		url:'/summary',
		controller:'BonusViewController as controller',
		resolve: {
			loadMyFiles:function($ocLazyLoad) {
				return $ocLazyLoad.load({
					name:'lithium',
					files:[ 'scripts/controllers/dashboard/bonuses/bonus/view/view.js' ]
				})
			}
		},
		ncyBreadcrumb: {
			label: 'bonus'
		}
	});

	$stateProvider.state('dashboard.bonuses.bonus.view.unlockgames', {
		templateUrl:'scripts/controllers/dashboard/bonuses/bonus/view/viewunlockgames.html',
		url:'/percentages',
		controller:'BonusViewUnlockGamesController as controller',
		resolve: {
			loadMyFiles:function($ocLazyLoad) {
				return $ocLazyLoad.load({
					name:'lithium',
					files:[ 'scripts/controllers/dashboard/bonuses/bonus/view/viewunlockgames.js' ]
				})
			}
		},
		ncyBreadcrumb: {
// parent: 'dashboard.bonuses',
			label: 'percentages'
		}
	});

	$stateProvider.state('dashboard.bonuses.bonus.view.gamepercentages', {
		templateUrl:'scripts/controllers/dashboard/bonuses/bonus/view/viewgamepercentages.html',
		url:'/percentages',
		controller:'BonusViewGamePercentagesController as controller',
		resolve: {
			loadMyFiles:function($ocLazyLoad) {
				return $ocLazyLoad.load({
					name:'lithium',
					files:[ 'scripts/controllers/dashboard/bonuses/bonus/view/viewgamepercentages.js' ]
				})
			}
		},
		ncyBreadcrumb: {
// parent: 'dashboard.bonuses',
			label: 'percentages'
		}
	});

	$stateProvider.state('dashboard.bonuses.bonus.view.gamecategories', {
		templateUrl:'scripts/controllers/dashboard/bonuses/bonus/view/viewgamecategories.html',
		url:'/categories',
		controller:'BonusViewGameCategoriesController as controller',
		resolve: {
			loadMyFiles:function($ocLazyLoad) {
				return $ocLazyLoad.load({
					name:'lithium',
					files:[ 'scripts/controllers/dashboard/bonuses/bonus/view/viewgamecategories.js' ]
				})
			}
		},
		ncyBreadcrumb: {
// parent: 'dashboard.bonuses',
			label: 'categories'
		}
	});

	$stateProvider.state('dashboard.bonuses.bonus.revisions', {
		templateUrl:'scripts/controllers/dashboard/bonuses/bonus/revisions.html',
		url:'/revisions',
		redirectTo: "dashboard.bonuses.bonus.revisions.list"
	});

	$stateProvider.state('dashboard.bonuses.bonus.revisions.list', {
		templateUrl:'scripts/controllers/dashboard/bonuses/bonus/edit/revisionslist.html',
		url:'/list',
		controller:'BonusRevisionsListController as controller',
		resolve: {
			bonus: ["rest-casino", "$stateParams", "errors", function(rest, $stateParams, errors) {
				return rest.findBonusById($stateParams.bonusId).then(function(bonus) {
					return bonus.plain();
				}).catch(function(error) {
					errors.catch("", false)(error)
				});
			}],
			loadMyFiles:function($ocLazyLoad) {
				return $ocLazyLoad.load({
					name:'lithium',
					files:[ 'scripts/controllers/dashboard/bonuses/bonus/edit/revisionslist.js' ]
				})
			}
		},
		ncyBreadcrumb: {
			label: 'revisions'
		}
	});

	$stateProvider.state('dashboard.bonuses.activebonus', {
		templateUrl: 'scripts/controllers/dashboard/bonuses/bonus/active/activebonuses.html',
		data: {
			pageTitle: 'Active Bonuses'
		},
		url: '/activebonus',
		controller: 'ActiveBonusesController as controller',
		resolve: {
			loadMyFiles: function ($ocLazyLoad) {
				return $ocLazyLoad.load({
					name: 'lithium',
					files: [
						'scripts/controllers/dashboard/bonuses/bonus/active/activebonuses.js'
					]
				});
			}
		}
	});

	$stateProvider.state('dashboard.bonuses.grantmass',{
		templateUrl: 'scripts/controllers/dashboard/bonuses/grantmass/grantmassbonuses.html',
		data: {
			pageTitle: "UI_NETWORK_ADMIN.PAGE.TITLE.GRANT_MASS_BONUSES",
		},
		url:'/grantmass',
		controller:'GrantMassBonusesController as controller',
		resolve: {
			loadMyFiles:function($ocLazyLoad) {
				return $ocLazyLoad.load({
					name:'lithium',
					files: [
						'scripts/controllers/dashboard/bonuses/grantmass/grantmassbonuses.js'
					]
				})
			}
		}
	});

	$stateProvider.state('dashboard.bonuses.grantmass.tool',{
		templateUrl:'scripts/controllers/dashboard/bonuses/grantmass/grantmassbonusestool.html',
		url:'/{domainName}',
		controller:'GrantMassBonusesToolController as controller',
		params: {
			domainName: ["$stateParams", function($stateParams) {
				return $stateParams.domainName;
			}]
		},
		resolve: {
			currencySymbol: ['rest-domain', "$stateParams", function(domainRest, $stateParams) {
				return domainRest.findByName($stateParams.domainName).then(function(domain) {
					return domain.currencySymbol;
				})
			}],
			loadMyFiles:function($ocLazyLoad) {
				return $ocLazyLoad.load({
					name:'lithium',
					files: [
						'scripts/controllers/dashboard/bonuses/grantmass/grantmassbonusestool.js'
					]
				})
			},
			domainName: ["$stateParams", function($stateParams) {
				return $stateParams.domainName;
			}]
		}
	});

	$stateProvider.state('dashboard.games.list',{
		templateUrl:'scripts/controllers/dashboard/games/list/list.html',
		data: {
			pageTitle: "UI_NETWORK_ADMIN.PAGE.TITLE.GAMES",
		},
		url:'/domain/{domainName}/games/list',
		controller:'GameListController as controller',
		resolve: {
			loadMyFiles:function($ocLazyLoad) {
				return $ocLazyLoad.load({
					name:'lithium',
					files:['scripts/controllers/dashboard/games/list/list.js']
				})
			}
		},
		params: {
			domainName: ["$stateParams", function ($stateParams) {
				return $stateParams.domainName;
			}]
		},
		ncyBreadcrumb: {
			label: 'games'
		}
	});

	$stateProvider.state('dashboard.games', {
		templateUrl:'scripts/controllers/dashboard/games/games.html',
		url:'/games',
		controller:'GamesController as controller',
		resolve: {
			loadMyFiles:function($ocLazyLoad) {
				return $ocLazyLoad.load({
					name:'lithium',
					files: [
						'scripts/controllers/dashboard/games/games.js'
					]
				})
			}
		},
		ncyBreadcrumb: { skip: true },
		redirectTo: "dashboard.games.list"
	});

	$stateProvider.state('dashboard.domains.domain.providers.view',{
		templateUrl:'scripts/controllers/dashboard/providers/view/view.html',
		url:'/provider/:domainName/:providerId/view?linkId',
		controller:'providerView as controller',
		resolve: {
			loadMyFiles:function($ocLazyLoad) {
				return $ocLazyLoad.load({
					name:'lithium',
					files:['scripts/controllers/dashboard/providers/view/view.js']
				})
			}
		},
		ncyBreadcrumb: {
			label: 'providers'
		}
	});

	$stateProvider.state('dashboard.domains.domain.providers.edit',{
		templateUrl:'scripts/controllers/dashboard/providers/edit/edit.html',
		url:'/provider/:domainName/:providerId/edit?linkId',
		controller:'providerEdit as controller',
		resolve: {
			loadMyFiles:function($ocLazyLoad) {
				return $ocLazyLoad.load({
					name:'lithium',
					files:['scripts/controllers/dashboard/providers/edit/edit.js']
				})
			}
		},
		ncyBreadcrumb: {
			parent: 'dashboard.providers',
			label: 'edit {{provider.name}}'
		}
	});

	$stateProvider.state('dashboard.gameAdd',{
		templateUrl:'scripts/controllers/dashboard/games/add/add.html',
		url:'/games/:domainName/add',
		controller:'gameAdd as controller',
		resolve: {
			loadMyFiles:function($ocLazyLoad) {
				return $ocLazyLoad.load({
					name:'lithium',
					files:['scripts/controllers/dashboard/games/add/add.js']
				})
			}
		},
		params: {
			domainName: ["$stateParams", function ($stateParams) {
				return $stateParams.domainName;
			}]
		},
		ncyBreadcrumb: {
			label: 'games'
		}
	});

	$stateProvider.state('dashboard.gamesDemo', {
		url:'/games/demo',
		controller: 'gameDemoController as controller',
		templateUrl:'scripts/controllers/dashboard/games/demo/demo.html',
		resolve: {
			loadMyFiles:function($ocLazyLoad) {
				return $ocLazyLoad.load({
					name:'lithium',
					files: ['scripts/controllers/dashboard/games/demo/demo.js']
				})
			}
		}
	});

	$stateProvider.state('dashboard.gameEdit',{
		templateUrl:'scripts/controllers/dashboard/games/edit/game.html',
		url:'/games/:domainName/:gameId/edit',
		controller:'gameEdit as controller',
		resolve: {
			cdnExternalGameGraphic: ["$stateParams", "rest-games", function ($stateParams, gamesRest) {
				return gamesRest.findCdnExternalGameGraphic($stateParams.domainName, $stateParams.gameId, false);
			}],
			liveCasinoImage: ["$stateParams", "rest-games", function ($stateParams, gamesRest) {
				return gamesRest.findCdnExternalGameGraphic($stateParams.domainName, $stateParams.gameId, true);
			}],
			loadMyFiles:function($ocLazyLoad) {
				return $ocLazyLoad.load({
					name:'lithium',
					files:['scripts/controllers/dashboard/games/edit/game.js']
				})
			}
		},
		ncyBreadcrumb: {
			label: 'games'
		},
		redirectTo: "dashboard.gameEdit.summary"
	});
	$stateProvider.state('dashboard.gameEdit.summary', {
		templateUrl:'scripts/controllers/dashboard/games/edit/edit.html'
	});
	$stateProvider.state('dashboard.gameEdit.images', {
		templateUrl:'scripts/controllers/dashboard/games/edit/images.html'
	});
	$stateProvider.state('dashboard.gameEdit.players', {
		templateUrl:'scripts/controllers/dashboard/games/edit/players.html'
	});

	$stateProvider.state('dashboard.responsiblegaming',{
		template: "<div ui-view></div>",
		url:'/responsiblegaming',
		ncyBreadcrumb: { skip: true }
	});

	$stateProvider.state('dashboard.responsiblegaming.players',{
		template: "<div ui-view></div>",
		url:'/players',
		ncyBreadcrumb: { skip: true }
	});

	$stateProvider.state('dashboard.responsiblegaming.players.list',{
		templateUrl:'scripts/controllers/dashboard/responsiblegaming/players/list.html',
		url:'/list',
		controller:'ResponsibleGamingPlayersList as controller',
		resolve: {
			loadMyFiles:function($ocLazyLoad) {
				return $ocLazyLoad.load({
					name:'lithium',
					files:['scripts/controllers/dashboard/responsiblegaming/players/list.js']
				})
			}
		},
		ncyBreadcrumb: {
			parent: 'dashboard.responsiblegaming',
			label: 'list'
		}
	});

	$stateProvider.state('dashboard.responsiblegaming.players.report',{
		templateUrl:'scripts/controllers/dashboard/responsiblegaming/players/report/report.html',
		url:'/responsiblegaming/:reportId',
		controller:'ReportGamstop as controller',
		resolve: {
			loadMyFiles:function($ocLazyLoad) {
				return $ocLazyLoad.load({
					name:'lithium',
					files:['scripts/controllers/dashboard/responsiblegaming/players/report/report.js']
				})
			}
		},
		ncyBreadcrumb: {
			parent: 'dashboard.responsiblegaming',
			label: 'report'
		}
	});
	$stateProvider.state('dashboard.responsiblegaming.players.run',{
		templateUrl:'scripts/controllers/dashboard/responsiblegaming/players/report/reportrun.html',
		url:'/responsiblegaming/:reportId/run/:reportRunId',
		controller:'ReportRunPlayers as controller',
		resolve: {
			loadMyFiles:function($ocLazyLoad) {
				return $ocLazyLoad.load({
					name:'lithium',
					files:['scripts/controllers/dashboard/responsiblegaming/players/report/reportrun.js']
				})
			}
		},
		ncyBreadcrumb: {
			parent: 'dashboard.responsiblegaming',
			label: 'run'
		}
	});
	$stateProvider.state('dashboard.responsiblegaming.players.edit',{
		templateUrl:'scripts/controllers/dashboard/responsiblegaming/players/report/report-edit.html',
		url:'/report/:reportId/edit',
		controller:'ReportPlayersEdit as controller',
		resolve: {
			report:["ReportPlayersRest", "$stateParams", "errors",
				function(ReportPlayersRest, $stateParams, errors) {
					return ReportPlayersRest.edit($stateParams.reportId).then(function(reportEdit) {
						return reportEdit;
					}).catch(function(error) {
						errors.catch("", false)(error)
					});
				}
			],
			loadMyFiles:function($ocLazyLoad) {
				return $ocLazyLoad.load({
					name:'lithium',
					files:['scripts/controllers/dashboard/responsiblegaming/players/report/report-edit.js']
				})
			}
		},
		ncyBreadcrumb: {
			parent: 'dashboard.responsiblegaming',
			label: 'report'
		}
	});

	$stateProvider.state('dashboard.reports',{
		template: "<div ui-view></div>",
		url:'/reports',
		ncyBreadcrumb: { skip: true }
	});

	$stateProvider.state('dashboard.reports.players',{
		template: "<div ui-view></div>",
		url:'/players',
		ncyBreadcrumb: { skip: true }
	});

	$stateProvider.state('dashboard.reports.players.list',{
		templateUrl:'scripts/controllers/dashboard/reports/players/list.html',
		data: {
			pageTitle: "UI_NETWORK_ADMIN.PAGE.TITLE.PLAYER_REPORTS",
		},
		url:'/list',
		controller:'ReportPlayersList as controller',
		resolve: {
			loadMyFiles:function($ocLazyLoad) {
				return $ocLazyLoad.load({
					name:'lithium',
					files:['scripts/controllers/dashboard/reports/players/list.js']
				})
			}
		},
		ncyBreadcrumb: {
			parent: 'dashboard.reports',
			label: 'list'
		}
	});

	$stateProvider.state('dashboard.reports.players.create', {
		templateUrl:'scripts/controllers/dashboard/reports/players/report/report-create.html',
		url:'/report/create',
		controller:'ReportPlayersCreate as controller',
		resolve: {
			loadMyFiles:function($ocLazyLoad) {
				return $ocLazyLoad.load({
					name:'lithium',
					files:['scripts/controllers/dashboard/reports/players/report/report-create.js']
				})
			}
		},
		nycBreadcrumb: {
			parent: 'dashboard.reports',
			label: 'report'
		}
	});

	$stateProvider.state('dashboard.reports.players.report',{
		templateUrl:'scripts/controllers/dashboard/reports/players/report/report.html',
		url:'/report/:reportId',
		controller:'ReportPlayers as controller',
		resolve: {
			loadMyFiles:function($ocLazyLoad) {
				return $ocLazyLoad.load({
					name:'lithium',
					files:['scripts/controllers/dashboard/reports/players/report/report.js']
				})
			}
		},
		ncyBreadcrumb: {
			parent: 'dashboard.reports',
			label: 'report'
		}
	});

	$stateProvider.state('dashboard.reports.players.edit',{
		templateUrl:'scripts/controllers/dashboard/reports/players/report/report-edit.html',
		url:'/report/:reportId/edit',
		controller:'ReportPlayersEdit as controller',
		resolve: {
			report:["ReportPlayersRest", "$stateParams", "errors",
				function(ReportPlayersRest, $stateParams, errors) {
					return ReportPlayersRest.edit($stateParams.reportId).then(function(reportEdit) {
						return reportEdit;
					}).catch(function(error) {
						errors.catch("", false)(error)
					});
				}
			],
			loadMyFiles:function($ocLazyLoad) {
				return $ocLazyLoad.load({
					name:'lithium',
					files:['scripts/controllers/dashboard/reports/players/report/report-edit.js']
				})
			}
		},
		ncyBreadcrumb: {
			parent: 'dashboard.reports',
			label: 'report'
		}
	});

	$stateProvider.state('dashboard.reports.players.run',{
		templateUrl:'scripts/controllers/dashboard/reports/players/report/reportrun.html',
		url:'/report/:reportId/run/:reportRunId',
		controller:'ReportRunPlayers as controller',
		resolve: {
			loadMyFiles:function($ocLazyLoad) {
				return $ocLazyLoad.load({
					name:'lithium',
					files:['scripts/controllers/dashboard/reports/players/report/reportrun.js']
				})
			}
		},
		ncyBreadcrumb: {
			parent: 'dashboard.reports',
			label: 'run'
		}
	});

	$stateProvider.state('dashboard.reports.incompleteplayers',{
		template: "<div ui-view></div>",
		url:'/incompleteplayers',
		ncyBreadcrumb: { skip: true }
	});

	$stateProvider.state('dashboard.reports.incompleteplayers.list',{
		templateUrl:'scripts/controllers/dashboard/reports/incompleteplayers/list.html',
		data: {
			pageTitle: "UI_NETWORK_ADMIN.PAGE.TITLE.INCOMPLETE_PLAYER_REPORT",
		},
		url:'/list',
		controller:'ReportIncompletePlayersList as controller',
		resolve: {
			loadMyFiles:function($ocLazyLoad) {
				return $ocLazyLoad.load({
					name:'lithium',
					files:['scripts/controllers/dashboard/reports/incompleteplayers/list.js']
				})
			}
		},
		ncyBreadcrumb: {
			parent: 'dashboard.reports',
			label: 'list'
		}
	});

	$stateProvider.state('dashboard.reports.incompleteplayers.create', {
		templateUrl:'scripts/controllers/dashboard/reports/incompleteplayers/report/report-create.html',
		url:'/report/create',
		controller:'ReportIncompletePlayersCreate as controller',
		resolve: {
			loadMyFiles:function($ocLazyLoad) {
				return $ocLazyLoad.load({
					name:'lithium',
					files:['scripts/controllers/dashboard/reports/incompleteplayers/report/report-create.js']
				})
			}
		},
		nycBreadcrumb: {
			parent: 'dashboard.reports',
			label: 'report'
		}
	});

	$stateProvider.state('dashboard.reports.incompleteplayers.report',{
		templateUrl:'scripts/controllers/dashboard/reports/incompleteplayers/report/report.html',
		url:'/report/:reportId',
		controller:'ReportIncompletePlayers as controller',
		resolve: {
			loadMyFiles:function($ocLazyLoad) {
				return $ocLazyLoad.load({
					name:'lithium',
					files:['scripts/controllers/dashboard/reports/incompleteplayers/report/report.js']
				})
			}
		},
		ncyBreadcrumb: {
			parent: 'dashboard.reports',
			label: 'report'
		}
	});

	$stateProvider.state('dashboard.reports.incompleteplayers.edit',{
		templateUrl:'scripts/controllers/dashboard/reports/incompleteplayers/report/report-edit.html',
		url:'/report/:reportId/edit',
		controller:'ReportIncompletePlayersEdit as controller',
		resolve: {
			report:["ReportIncompletePlayersRest", "$stateParams", "errors",
				function(ReportIncompletePlayersRest, $stateParams, errors) {
					return ReportIncompletePlayersRest.edit($stateParams.reportId).then(function(reportEdit) {
						return reportEdit;
					}).catch(function(error) {
						errors.catch("", false)(error)
					});
				}
			],
			loadMyFiles:function($ocLazyLoad) {
				return $ocLazyLoad.load({
					name:'lithium',
					files:['scripts/controllers/dashboard/reports/incompleteplayers/report/report-edit.js']
				})
			}
		},
		ncyBreadcrumb: {
			parent: 'dashboard.reports',
			label: 'report'
		}
	});

	$stateProvider.state('dashboard.reports.incompleteplayers.run',{
		templateUrl:'scripts/controllers/dashboard/reports/incompleteplayers/report/reportrun.html',
		url:'/report/:reportId/run/:reportRunId',
		controller:'ReportRunIncompletePlayers as controller',
		resolve: {
			loadMyFiles:function($ocLazyLoad) {
				return $ocLazyLoad.load({
					name:'lithium',
					files:['scripts/controllers/dashboard/reports/incompleteplayers/report/reportrun.js']
				})
			}
		},
		ncyBreadcrumb: {
			parent: 'dashboard.reports',
			label: 'run'
		}
	});

	$stateProvider.state('dashboard.reports.games',{
		template: "<div ui-view></div>",
		url:'/games',
		ncyBreadcrumb: { skip: true }
	});

	$stateProvider.state('dashboard.reports.games.list',{
		templateUrl:'scripts/controllers/dashboard/reports/games/list.html',
		data: {
			pageTitle: "UI_NETWORK_ADMIN.PAGE.TITLE.GAME_REPORTS",
		},
		url:'/list',
		controller:'ReportGamesList as controller',
		resolve: {
			loadMyFiles:function($ocLazyLoad) {
				return $ocLazyLoad.load({
					name:'lithium',
					files:['scripts/controllers/dashboard/reports/games/list.js']
				})
			}
		},
		ncyBreadcrumb: {
			parent: 'dashboard.reports',
			label: 'list'
		}
	});

	$stateProvider.state('dashboard.reports.games.create', {
		templateUrl:'scripts/controllers/dashboard/reports/games/report/report-create.html',
		url:'/report/create',
		controller:'ReportGamesCreate as controller',
		resolve: {
			loadMyFiles:function($ocLazyLoad) {
				return $ocLazyLoad.load({
					name:'lithium',
					files:['scripts/controllers/dashboard/reports/games/report/report-create.js']
				})
			}
		},
		nycBreadcrumb: {
			parent: 'dashboard.reports',
			label: 'report'
		}
	});

	$stateProvider.state('dashboard.reports.games.report',{
		templateUrl:'scripts/controllers/dashboard/reports/games/report/report.html',
		url:'/report/:reportId',
		controller:'ReportGames as controller',
		resolve: {
			loadMyFiles:function($ocLazyLoad) {
				return $ocLazyLoad.load({
					name:'lithium',
					files:['scripts/controllers/dashboard/reports/games/report/report.js']
				})
			}
		},
		ncyBreadcrumb: {
			parent: 'dashboard.reports',
			label: 'report'
		}
	});

	$stateProvider.state('dashboard.reports.games.edit',{
		templateUrl:'scripts/controllers/dashboard/reports/games/report/report-edit.html',
		url:'/report/:reportId/edit',
		controller:'ReportGamesEdit as controller',
		resolve: {
			report:["ReportGamesRest", "$stateParams", "errors",
				function(ReportGamesRest, $stateParams, errors) {
					return ReportGamesRest.edit($stateParams.reportId).then(function(reportEdit) {
						return reportEdit;
					}).catch(function(error) {
						errors.catch("", false)(error)
					});
				}
			],
			loadMyFiles:function($ocLazyLoad) {
				return $ocLazyLoad.load({
					name:'lithium',
					files:['scripts/controllers/dashboard/reports/games/report/report-edit.js']
				})
			}
		},
		ncyBreadcrumb: {
			parent: 'dashboard.reports',
			label: 'report'
		}
	});

	$stateProvider.state('dashboard.reports.games.run',{
		templateUrl:'scripts/controllers/dashboard/reports/games/report/reportrun.html',
		url:'/report/:reportId/run/:reportRunId',
		controller:'ReportRunGames as controller',
		resolve: {
			loadMyFiles:function($ocLazyLoad) {
				return $ocLazyLoad.load({
					name:'lithium',
					files:['scripts/controllers/dashboard/reports/games/report/reportrun.js']
				})
			}
		},
		ncyBreadcrumb: {
			parent: 'dashboard.reports',
			label: 'run'
		}
	});

	// *********************************
	$stateProvider.state('dashboard.reports.ia',{
		template: "<div ui-view></div>",
		url:'/ia',
		ncyBreadcrumb: { skip: true }
	});

	$stateProvider.state('dashboard.reports.ia.list',{
		templateUrl:'scripts/controllers/dashboard/reports/ia/list.html',
		data: {
			pageTitle: "UI_NETWORK_ADMIN.PAGE.TITLE.INCOME_ACCESS_REPORTS",
		},
		url:'/list',
		controller:'ReportIaList as controller',
		resolve: {
			loadMyFiles:function($ocLazyLoad) {
				return $ocLazyLoad.load({
					name:'lithium',
					files:['scripts/controllers/dashboard/reports/ia/list.js']
				})
			}
		},
		ncyBreadcrumb: {
			parent: 'dashboard.reports',
			label: 'list'
		}
	});

	$stateProvider.state('dashboard.reports.ia.create', {
		templateUrl:'scripts/controllers/dashboard/reports/ia/report/report-create.html',
		url:'/report/create',
		controller:'ReportIaCreate as controller',
		resolve: {
			loadMyFiles:function($ocLazyLoad) {
				return $ocLazyLoad.load({
					name:'lithium',
					files:['scripts/controllers/dashboard/reports/ia/report/report-create.js']
				})
			}
		},
		nycBreadcrumb: {
			parent: 'dashboard.reports',
			label: 'report'
		}
	});

	$stateProvider.state('dashboard.reports.ia.report',{
		templateUrl:'scripts/controllers/dashboard/reports/ia/report/report.html',
		url:'/report/:reportId',
		controller:'ReportIa as controller',
		resolve: {
			loadMyFiles:function($ocLazyLoad) {
				return $ocLazyLoad.load({
					name:'lithium',
					files:['scripts/controllers/dashboard/reports/ia/report/report.js']
				})
			}
		},
		ncyBreadcrumb: {
			parent: 'dashboard.reports',
			label: 'report'
		}
	});

	$stateProvider.state('dashboard.reports.ia.edit',{
		templateUrl:'scripts/controllers/dashboard/reports/ia/report/report-edit.html',
		url:'/report/:reportId/edit',
		controller:'ReportIaEdit as controller',
		resolve: {
			report:["ReportIaRest", "$stateParams", "errors",
				function(ReportIaRest, $stateParams, errors) {
					return ReportIaRest.edit($stateParams.reportId).then(function(reportEdit) {
						return reportEdit;
					}).catch(function(error) {
						errors.catch("", false)(error)
					});
				}
			],
			loadMyFiles:function($ocLazyLoad) {
				return $ocLazyLoad.load({
					name:'lithium',
					files:['scripts/controllers/dashboard/reports/ia/report/report-edit.js']
				})
			}
		},
		ncyBreadcrumb: {
			parent: 'dashboard.reports',
			label: 'report'
		}
	});

	$stateProvider.state('dashboard.reports.ia.run',{
		templateUrl:'scripts/controllers/dashboard/reports/ia/report/reportrun.html',
		url:'/report/:reportId/run/:reportRunId',
		controller:'ReportRunIa as controller',
		resolve: {
			loadMyFiles:function($ocLazyLoad) {
				return $ocLazyLoad.load({
					name:'lithium',
					files:['scripts/controllers/dashboard/reports/ia/report/reportrun.js']
				})
			}
		},
		ncyBreadcrumb: {
			parent: 'dashboard.reports',
			label: 'run'
		}
	});
	// *********************************

	$stateProvider.state('dashboard.loginevents', {
		url:'/loginevents',
		controller: 'LoginEventsController as controller',
		templateUrl:'scripts/controllers/dashboard/loginevents/loginevents.html',
		data: {
			pageTitle: "UI_NETWORK_ADMIN.PAGE.TITLE.LOGIN_HISTORY",
		},
		resolve: {
			loadMyFiles:function($ocLazyLoad) {
				return $ocLazyLoad.load({
					name:'lithium',
					files: ['scripts/controllers/dashboard/loginevents/loginevents.js']
				})
			}
		},
		ncyBreadcrumb: {
			label: "login history"
		}
	});

	$stateProvider.state('dashboard.signupevents',{
		template: "<div ui-view></div>",
		url:'/signupevents',
		resolve: {
			domain: function() { return },
			loadMyFiles:function($ocLazyLoad) {
				return $ocLazyLoad.load({
					name:'lithium',
					files: [
						'scripts/controllers/dashboard/signupevents/list/list.js'
					]
				})
			}
		},
		ncyBreadcrumb: { skip: true },
		redirectTo: "dashboard.signupevents.list"
	});

	$stateProvider.state('dashboard.signupevents.list', {
		url:'',
		controller: 'SignupEventsList as controller',
		templateUrl: 'scripts/controllers/dashboard/signupevents/list/list.html',
		data: {
			pageTitle: "UI_NETWORK_ADMIN.PAGE.TITLE.SIGNUP_EVENTS",
		},
		ncyBreadcrumb: { label: "signup events" }
	});
	$stateProvider.state('dashboard.threshold',{
		templateUrl: 'scripts/controllers/dashboard/reports/playerprotection/threshold.html',
		url:'/threshold',
		controller:'ThresholdReport as controller',
		data: {
			pageTitle: "UI_NETWORK_ADMIN.PAGE.TITLE.LOGIN_HISTORY",
		},
		resolve: {
			loadMyFiles:function($ocLazyLoad) {
				return $ocLazyLoad.load({
					name:'lithium',
					files:['scripts/controllers/dashboard/reports/playerprotection/threshold.js']
				})
			}
		},
		redirectTo: "dashboard.threshold.list"
	});

	$stateProvider.state('dashboard.threshold.list', {
		templateUrl:'scripts/controllers/dashboard/reports/playerprotection/threshold-list.html',
		url:'/{domainName}',
		controller:'ThresholdListReport as controller',
		params: {
			domainName: ["$stateParams", function($stateParams) {
				return $stateParams.domainName;
			}]
		},
		resolve: {
			loadMyFiles:function($ocLazyLoad) {
				return $ocLazyLoad.load({
					name:'lithium',
					files: [
						'scripts/controllers/dashboard/reports/playerprotection/threshold-list.js'
					]
				})
			}
		}
	});

	$stateProvider.state('dashboard.depositthreshold',{
		templateUrl: 'scripts/controllers/dashboard/reports/playerprotection/depositthreshold.html',
		url:'/depositthreshold',
		controller:'DepositThresholdReport as controller',
		data: {
			pageTitle: "",
		},
		resolve: {
			loadMyFiles:function($ocLazyLoad) {
				return $ocLazyLoad.load({
					name:'lithium',
					files:['scripts/controllers/dashboard/reports/playerprotection/depositthreshold.js']
				})
			}
		}
	});
	$stateProvider.state('dashboard.depositthreshold.list', {
		templateUrl:'scripts/controllers/dashboard/reports/playerprotection/depositthreshold-list.html',
		url:'/{domainName}',
		controller:'ThresholdDepositListReport as controller',
		params: {
			domainName: ["$stateParams", function($stateParams) {
				return $stateParams.domainName;
			}]
		},
		resolve: {
			loadMyFiles:function($ocLazyLoad) {
				return $ocLazyLoad.load({
					name:'lithium',
					files: [
						'scripts/controllers/dashboard/reports/playerprotection/depositthreshold-list.js'
					]
				})
			}
		}
	});

	$stateProvider.state('dashboard.signupevents.view', {
		url: '/:id/view',
		controller: 'SignupEventsView as controller',
		templateUrl: 'scripts/controllers/dashboard/signupevents/view/view.html',
		resolve: {
			signupEvent: ["signupEventsRest", "$stateParams", function(signupEventsRest, $stateParams) {
				return signupEventsRest.findById($stateParams.id);
			}],
			loadMyFiles:function($ocLazyLoad) {
				return $ocLazyLoad.load({
					name:'lithium',
					files:
						[
							'scripts/controllers/dashboard/signupevents/view/view.js'
						]
				})
			}
		},
		ncyBreadcrumb: {
			parent: 'dashboard.signupevents',
			label: 'signup event'
		}
	});

	$stateProvider.state('dashboard.accesscontrol',{
		template: "<div ui-view></div>",
		url:'/accesscontrol',
		ncyBreadcrumb: { skip: true }
	});

	$stateProvider.state('dashboard.accesscontrol.lists',{
		template: "<div ui-view></div>",
		url:'/lists',
		ncyBreadcrumb: { skip: true },
		redirectTo: "dashboard.accesscontrol.lists.list"
	});

	$stateProvider.state('dashboard.accesscontrol.lists.list',{
		templateUrl:'scripts/controllers/dashboard/accesscontrol/lists/list/list.html',
		data: {
			pageTitle: "UI_NETWORK_ADMIN.PAGE.TITLE.ACCESS_CONTROL_LISTS",
		},
		url:'/list',
		controller:'AccessControlListController as controller',
		resolve: {
			loadMyFiles:function($ocLazyLoad) {
				return $ocLazyLoad.load({
					name:'lithium',
					files:[
						'scripts/controllers/dashboard/accesscontrol/lists/list/list.js',
						'scripts/controllers/dashboard/accesscontrol/lists/add/add.js'
					]
				})
			}
		},
		ncyBreadcrumb: {
			parent: 'dashboard.accesscontrol',
			label: 'access control'
		}
	});

	$stateProvider.state('dashboard.accesscontrol.lists.view',{
		templateUrl:'scripts/controllers/dashboard/accesscontrol/lists/view/view.html',
		url:'/view/:id',
		controller: 'AccessControlViewController as controller',
		resolve: {
			list: ['accessControlRest', '$stateParams', function(accessControlRest, $stateParams) {
				return accessControlRest.findListById($stateParams.id);
			}],
			loadMyFiles:function($ocLazyLoad) {
				return $ocLazyLoad.load({
					name:'lithium',
					files:
						[
							'scripts/controllers/dashboard/accesscontrol/lists/view/view.js'
						]
				})
			}
		},
		ncyBreadcrumb: {
			parent: 'dashboard.accesscontrol',
			label: 'access control'
		}
	});

	$stateProvider.state('dashboard.accesscontrol.rules',{
		template: "<div ui-view></div>",
		url:'/rules',
		ncyBreadcrumb: { skip: true },
		redirectTo: 'dashboard.accesscontrol.rules.list'
	});

	$stateProvider.state('dashboard.accesscontrol.rules.list',{
		templateUrl:'scripts/controllers/dashboard/accesscontrol/rules/list/list.html',
		data: {
			pageTitle: "UI_NETWORK_ADMIN.PAGE.TITLE.ACCESS_CONTROL_RULESETS",
		},
		url:'/list',
		controller:'AccessRulesList as controller',
		resolve: {
			loadMyFiles:function($ocLazyLoad) {
				return $ocLazyLoad.load({
					name:'lithium',
					files:[
						'scripts/controllers/dashboard/accesscontrol/rules/list/list.js',
						'scripts/controllers/dashboard/accesscontrol/rules/add/add.js'
					]
				})
			}
		},
		ncyBreadcrumb: {
			parent: 'dashboard.accesscontrol',
			label: 'access control'
		}
	});

	$stateProvider.state('dashboard.accesscontrol.rules.view',{
		templateUrl:'scripts/controllers/dashboard/accesscontrol/rules/view/view.html',
		url:'/view/:id',
		controller: 'AccessRulesView as controller',
		resolve: {
			accessRule: ["accessRulesRest", "$stateParams", "$q", function(accessRulesRest, $stateParams, $q) {
				return accessRulesRest.ruleset($stateParams.id);
			}],
			loadMyFiles:function($ocLazyLoad) {
				return $ocLazyLoad.load({
					name:'lithium',
					files:
						[
							'scripts/controllers/dashboard/accesscontrol/rules/view/view.js'
						]
				})
			}
		},
		ncyBreadcrumb: {
			parent: 'dashboard.accesscontrol',
			label: 'access control'
		}
	});

	

	$stateProvider.state('dashboard.cmsimages', {
		template: '<div ui-view></div>',
		url:'/cmsimages',
		controller: function($scope) {
			$scope.setDescription = function(description) {
				$scope.description = description;
			}

			
		},
		ncyBreadcrumb: { skip: true }
	});

	$stateProvider.state('dashboard.cmsimages.domain', {
		templateUrl: 'scripts/controllers/dashboard/cmsimages/domain.html',
		controller:'CmsAssetDomainController as controller',
		url:'/domain',
		resolve: {
			loadMyFiles:function($ocLazyLoad) {
				return $ocLazyLoad.load({
					name:'lithium',
					files: [
						'scripts/controllers/dashboard/cmsimages/domain.js'
					]
				})
			}
		},
		ncyBreadcrumb: { skip: true }
	});


	$stateProvider.state('dashboard.cmsimages.domain.list', {
		templateUrl:'scripts/controllers/dashboard/cmsimages/list/list.html',
		data: {
			pageTitle: "UI_NETWORK_ADMIN.CMS.PAGE.TITLE.IMAGES",
		},
		url:'/{domainName}/list',
		controller:'ImagesListController as controller',
		params: {
			domainName: ["$stateParams", function($stateParams) {
				return $stateParams.domainName;
			}]
		},
		resolve: {
			domainName: ["$stateParams", function($stateParams) {
				return $stateParams.domainName;
			}],
			loadMyFiles:function($ocLazyLoad) {
				return $ocLazyLoad.load({
					name:'lithium',
					files: [
						'scripts/controllers/dashboard/cmsimages/list/list.js'
					]
				})
			}
		}
	});

	$stateProvider.state('dashboard.cmsassets', {
		templateUrl:'scripts/controllers/dashboard/cmsassets/index.html',
		controller:'CmsAssetController as controller',
		url:'/cms-assets',
		resolve: {
			loadMyFiles:function($ocLazyLoad) {
				return $ocLazyLoad.load({
					name:'lithium',
					files: [
						'scripts/controllers/dashboard/cmsassets/index.js'
					]
				})
			}
		}
	});


	$stateProvider.state('dashboard.templates', {
		template: "<div ui-view></div>",
		url:'/templates',
		controller: function($scope) {
			$scope.setDescription = function(description) {
				$scope.description = description;
			}
		},
		ncyBreadcrumb: { skip: true }
	});

	$stateProvider.state('dashboard.cmsimages.domain.view',{
		templateUrl: 'scripts/controllers/dashboard/cmsimages/view/view.html',
		url: '/{domainName}/:id/view',
		controller: 'ImagesViewController as controller',
		resolve: {
			image: ["AssetTemplatesRest", "$stateParams", function(rest, $stateParams) {
				return rest.view($stateParams.id,$stateParams.domainName);
			}],
			domainName: ["$stateParams", function($stateParams) {
				return $stateParams.domainName;
			}],
			loadMyFiles:function($ocLazyLoad) {
				return $ocLazyLoad.load({
					name:'lithium',
					files:['scripts/controllers/dashboard/cmsimages/view/view.js']
				})
			}
		}
	});

	$stateProvider.state('dashboard.cmsbuilder',{
		templateUrl: 'scripts/controllers/dashboard/cmsbuilder/demo.html',
		url: '/cmsbuilderdemo',
		controller: 'CmsBuilderDemoController as controller',
		resolve: {
			loadMyFiles:function($ocLazyLoad) {
				return $ocLazyLoad.load({
					name:'lithium',
					files: [
						'scripts/controllers/dashboard/cmsbuilder/demo.js'
					]
				})
			}
		},
	});

	$stateProvider.state('dashboard.banner-management',{
		templateUrl: 'scripts/controllers/dashboard/casino/banners/index.html',
		url: '/banner-management',
		controller: 'CmsBannerManagementController as controller',
		resolve: {
			loadMyFiles:function($ocLazyLoad) {
				return $ocLazyLoad.load({
					name:'lithium',
					files: [
						'scripts/controllers/dashboard/casino/banners/index.js',
					]
				})
			}
		},
	});

	$stateProvider.state('dashboard.templates.domain', {
		templateUrl:'scripts/controllers/dashboard/templates/domain.html',
		controller:'TemplatesDomainController as controller',
		url:'/domain',
		resolve: {
			loadMyFiles:function($ocLazyLoad) {
				return $ocLazyLoad.load({
					name:'lithium',
					files: [
						'scripts/controllers/dashboard/templates/domain.js'
					]
				})
			}
		},
		ncyBreadcrumb: { skip: true }
	});

	$stateProvider.state('dashboard.templates.domain.list', {
		templateUrl:'scripts/controllers/dashboard/templates/list/list.html',
		data: {
			pageTitle: "UI_NETWORK_ADMIN.PAGE.TITLE.TEMPLATES",
		},
		url:'/{domainName}/list',
		controller:'TemplatesListController as controller',
		params: {
			domainName: ["$stateParams", function($stateParams) {
				return $stateParams.domainName;
			}]
		},
		resolve: {
			domainName: ["$stateParams", function($stateParams) {
				return $stateParams.domainName;
			}],
			loadMyFiles:function($ocLazyLoad) {
				return $ocLazyLoad.load({
					name:'lithium',
					files: [
						'scripts/controllers/dashboard/templates/list/list.js'
					]
				})
			}
		}
	});

	$stateProvider.state('dashboard.templates.domain.view',{
		templateUrl: 'scripts/controllers/dashboard/templates/view/view.html',
		url: '/{domainName}/:id/view',
		controller: 'TemplatesViewController as controller',
		resolve: {
			template: ["TemplatesRest", "$stateParams", function(rest, $stateParams) {
				return rest.view($stateParams.id,$stateParams.domainName);
			}],
			domainName: ["$stateParams", function($stateParams) {
				return $stateParams.domainName;
			}],
			loadMyFiles:function($ocLazyLoad) {
				return $ocLazyLoad.load({
					name:'lithium',
					files:['scripts/controllers/dashboard/templates/view/view.js']
				})
			}
		}
	});

	$stateProvider.state('dashboard.templates.domain.edit', {
		templateUrl:'scripts/controllers/dashboard/templates/edit/edit.html',
		url:'/{domainName}/:id/edit',
		controller:'TemplatesEditController as controller',
		resolve: {
			template:["TemplatesRest","$stateParams",function(rest,$stateParams) {
				return rest.edit($stateParams.domainName,$stateParams.id).then(function(template) {
					return template;
				}).catch(function(error) {
					errors.catch("", false)(error)
				});
			}],
			domainName: ["$stateParams", function($stateParams) {
				return $stateParams.domainName;
			}],
			loadMyFiles:function($ocLazyLoad) {
				return $ocLazyLoad.load({
					name:'lithium',
					files:['scripts/controllers/dashboard/templates/edit/edit.js']
				})
			}
		}
	});

	$stateProvider.state('dashboard.xp', {
		template: "<div ui-view></div>",
		url:'/xp',
		controller: function($scope) {
			$scope.setDescription = function(description) {
				$scope.description = description;
			}
		},
		ncyBreadcrumb: { skip: true }
	});

	$stateProvider.state('dashboard.xp.schemes',{
		template: "<div ui-view></div>",
		url:'/schemes',
		resolve: {
			domain: function() { return },
			loadMyFiles:function($ocLazyLoad) {
				return $ocLazyLoad.load({
					name:'lithium',
					files: [
						'scripts/controllers/dashboard/xp/list/list.js',
						'scripts/controllers/dashboard/xp/add/add.js'
					]
				})
			}
		},
		ncyBreadcrumb: { skip: true },
		redirectTo: "dashboard.xp.schemes.list"
	});

	$stateProvider.state('dashboard.xp.schemes.list',{
		templateUrl:'scripts/controllers/dashboard/xp/list/list.html',
		data: {
			pageTitle: "UI_NETWORK_ADMIN.PAGE.TITLE.XP",
		},
		url:'',
		controller:'XPSchemesListController as controller',
		ncyBreadcrumb: {
			parent: 'dashboard.xp.schemes',
			label: 'xp schemes'
		}
	});

	$stateProvider.state('dashboard.xp.schemes.add', {
		templateUrl:'scripts/controllers/dashboard/xp/add/add.html',
		url:'/add',
		controller:'XPSchemesAddController as controller',
		ncyBreadcrumb: {
			parent: 'dashboard.xp.schemes',
			label: 'add xp scheme'
		}
	});

	$stateProvider.state('dashboard.xp.schemes.view',{
		templateUrl: 'scripts/controllers/dashboard/xp/view/view.html',
		url: '/:id/view',
		controller: 'XPSchemesViewController as controller',
		resolve: {
			scheme: ["XPRest", "$stateParams", function(rest, $stateParams) {
				return rest.viewScheme($stateParams.id);
			}],
			loadMyFiles:function($ocLazyLoad) {
				return $ocLazyLoad.load({
					name:'lithium',
					files:['scripts/controllers/dashboard/xp/view/view.js']
				})
			}
		}
	});

	$stateProvider.state('dashboard.xp.schemes.edit',{
		templateUrl: 'scripts/controllers/dashboard/xp/edit/edit.html',
		url: '/:id/edit',
		controller: 'XPSchemesEditController as controller',
		resolve: {
			scheme: ["XPRest", "$stateParams", function(rest, $stateParams) {
				return rest.viewScheme($stateParams.id);
			}],
			loadMyFiles:function($ocLazyLoad) {
				return $ocLazyLoad.load({
					name:'lithium',
					files:['scripts/controllers/dashboard/xp/edit/edit.js']
				})
			}
		}
	});

	$stateProvider.state('dashboard.notifications', {
		template: "<div ui-view></div>",
		url:'/notifications',
		controller: function($scope) {
			$scope.setDescription = function(description) {
				$scope.description = description;
			}
		},
		ncyBreadcrumb: { skip: true }
	});

	$stateProvider.state('dashboard.notifications.notifications',{
		template: "<div ui-view></div>",
		url:'/list',
		resolve: {
			domain: function() { return },
			loadMyFiles:function($ocLazyLoad) {
				return $ocLazyLoad.load({
					name:'lithium',
					files: [
						'scripts/controllers/dashboard/notifications/notifications/list/list.js',
						'scripts/controllers/dashboard/notifications/notifications/add/add.js'
					]
				})
			}
		},
		ncyBreadcrumb: { skip: true },
		redirectTo: "dashboard.notifications.notifications.list"
	});

	$stateProvider.state('dashboard.notifications.notifications.list',{
		templateUrl:'scripts/controllers/dashboard/notifications/notifications/list/list.html',
		data: {
			pageTitle: "UI_NETWORK_ADMIN.PAGE.TITLE.NOTIFICATIONS",
		},
		url:'',
		controller:'NotificationsListController as controller',
		ncyBreadcrumb: {
			parent: 'dashboard.notifications.notifications',
			label: 'notifications list'
		}
	});

	$stateProvider.state('dashboard.notifications.notifications.add', {
		templateUrl:'scripts/controllers/dashboard/notifications/notifications/add/add.html',
		url:'/add',
		controller:'NotificationsAddController as controller',
		resolve: {
			domain: function() { return },
			availableChannels: ["NotificationRest", function(rest) {
				return rest.allChannels();
			}],
			notificationTypes: ["NotificationRest", function(rest) {
				return rest.getNotificationTypes();
			}]
		},
		ncyBreadcrumb: {
			label: 'add a notification'
		}
	});

	$stateProvider.state('dashboard.notifications.notifications.view', {
		templateUrl:'scripts/controllers/dashboard/notifications/notifications/view/view.html',
		url:'/{id}/view',
		controller:'NotificationsViewController as controller',
		resolve: {
			domain: function() { return },
			notification: ["NotificationRest", "$stateParams", function(rest, $stateParams) {
				return rest.view($stateParams.id);
			}],
			loadMyFiles:function($ocLazyLoad) {
				return $ocLazyLoad.load({
					name:'lithium',
					files: [
						'scripts/controllers/dashboard/notifications/notifications/view/view.js'
					]
				})
			}
		}
	});

	$stateProvider.state('dashboard.notifications.notifications.edit', {
		templateUrl:'scripts/controllers/dashboard/notifications/notifications/edit/edit.html',
		url:'/{id}/edit',
		controller:'NotificationsEditController as controller',
		resolve: {
			domain: function() { return },
			notification: ["NotificationRest", "$stateParams", function(rest, $stateParams) {
				return rest.view($stateParams.id);
			}],
			availableChannels: ["NotificationRest", function(rest) {
				return rest.allChannels();
			}],
			notificationTypes: ["NotificationRest", function(rest) {
				return rest.getNotificationTypes();
			}],
			loadMyFiles:function($ocLazyLoad) {
				return $ocLazyLoad.load({
					name:'lithium',
					files: [
						'scripts/controllers/dashboard/notifications/notifications/edit/edit.js'
					]
				})
			}
		}
	});

	$stateProvider.state('dashboard.notifications.inbox',{
		template: "<div ui-view></div>",
		url:'/inbox',
		resolve: {
			domain: function() { return },
			loadMyFiles:function($ocLazyLoad) {
				return $ocLazyLoad.load({
					name:'lithium',
					files: [
						'scripts/controllers/dashboard/notifications/inbox/list/list.js'
					]
				})
			}
		},
		ncyBreadcrumb: { skip: true },
		redirectTo: "dashboard.notifications.inbox.list"
	});

	$stateProvider.state('dashboard.notifications.inbox.list',{
		templateUrl:'scripts/controllers/dashboard/notifications/inbox/list/list.html',
		data: {
			pageTitle: "UI_NETWORK_ADMIN.PAGE.TITLE.NOTIFICATIONS_INBOX",
		},
		url:'',
		controller:'InboxListController as controller',
		ncyBreadcrumb: {
			parent: 'dashboard.notifications.inbox',
			label: 'inbox list'
		}
	});

	$stateProvider.state('dashboard.notifications.inbox.view', {
		templateUrl:'scripts/controllers/dashboard/notifications/inbox/view/view.html',
		url:'/{id}/view',
		controller:'InboxViewController as controller',
		resolve: {
			domain: function() { return },
			inbox: ["NotificationRest", "$stateParams", function(rest, $stateParams) {
				return rest.viewInbox($stateParams.id);
			}],
			loadMyFiles:function($ocLazyLoad) {
				return $ocLazyLoad.load({
					name:'lithium',
					files: [
						'scripts/controllers/dashboard/notifications/inbox/view/view.js'
					]
				})
			}
		}
	});

	$stateProvider.state('dashboard.missions',{
		template: "<div ui-view></div>",
		url:'/promotions',
		resolve: {
			loadMyFiles:function($ocLazyLoad) {
				return $ocLazyLoad.load({
					name:'lithium',
					files: [
						'scripts/controllers/dashboard/missions/list/list.js',
						'scripts/controllers/dashboard/missions/add/add.js'
					]
				})
			}
		},
		ncyBreadcrumb: { skip: true },
		redirectTo: "dashboard.missions.list"
	});

	$stateProvider.state('dashboard.missions.list',{
		templateUrl:'scripts/controllers/dashboard/missions/list/list.html',
		data: {
			pageTitle: "UI_NETWORK_ADMIN.PAGE.TITLE.MISSIONS",
		},
		url:'',
		controller:'MissionsListController as controller',
		ncyBreadcrumb: {
			parent: 'dashboard.missions',
			label: 'missions'
		}
	});

	$stateProvider.state('dashboard.missions.beta', {
		templateUrl:`scripts/controllers/dashboard/promotions/list.html`,
		url:'/beta',
		controller: 'PromotionBetaController as controller',
		ncyBreadcrumb: {
			label: 'add a mission'
		},
		resolve: {
			loadMyFiles:function($ocLazyLoad) {
				return $ocLazyLoad.load({
					name:'lithium',
					files: [
						'scripts/controllers/dashboard/promotions/list.js'
					]
				})
			}
		}
	});

	$stateProvider.state('dashboard.rewards', {
		templateUrl:`scripts/controllers/dashboard/rewards/list.html`,
		url:'/rewards',
		controller: 'RewardsController as controller',
		ncyBreadcrumb: {
			label: 'Rewards'
		},
		resolve: {
			loadMyFiles:function($ocLazyLoad) {
				return $ocLazyLoad.load({
					name:'lithium',
					files: [
						'scripts/controllers/dashboard/rewards/list.js'
					]
				})
			}
		}
	});

	$stateProvider.state('dashboard.missions.add', {
		templateUrl:'scripts/controllers/dashboard/missions/add/add.html',
		url:'/add',
		controller:'MissionsAddController as controller',
		resolve: {
			domain: function() { return }
		},
		ncyBreadcrumb: {
			label: 'add a mission'
		}
	});

	$stateProvider.state('dashboard.missions.mission', {
		templateUrl: "scripts/controllers/dashboard/missions/mission/mission.html",
		url:'/promotion/{id}/{missionRevisionId}',
		controller: 'MissionController as controller',
		resolve: {
			mission: ["PromotionRest", "$stateParams", function(rest, $stateParams) {
				return rest.view($stateParams.id);
			}],
			missionRevision: ["PromotionRest", "$stateParams", function(rest, $stateParams) {
				return rest.findRevision($stateParams.id, $stateParams.missionRevisionId);
			}],
			loadMyFiles:function($ocLazyLoad) {
				return $ocLazyLoad.load({
					name:'lithium',
					files: [
						'scripts/controllers/dashboard/missions/mission/mission.js'
					]
				})
			}
		},
		ncyBreadcrumb: { skip: true }
	});

	$stateProvider.state('dashboard.missions.mission.revisions', {
		template:'<div ui-view></div>',
		url:'/revisions',
		resolve: {
			mission: ["PromotionRest", "$stateParams", function(rest, $stateParams) {
				return rest.view($stateParams.id);
			}]
		},
		redirectTo: "dashboard.missions.mission.revisions.list"
	});

	$stateProvider.state('dashboard.missions.mission.revisions.list', {
		templateUrl:'scripts/controllers/dashboard/missions/mission/view/revisions/revisions.html',
		url:'/list',
		controller:'MissionRevisionsListController as controller',
		resolve: {
			domain: function() { return },
			mission: ["PromotionRest", "$stateParams", function(rest, $stateParams) {
				return rest.view($stateParams.id);
			}],
			loadMyFiles:function($ocLazyLoad) {
				return $ocLazyLoad.load({
					name:'lithium',
					files: [
						'scripts/controllers/dashboard/missions/mission/view/revisions/revisions.js'
					]
				})
			}
		}
	});

	$stateProvider.state('dashboard.missions.mission.view', {
		templateUrl:'scripts/controllers/dashboard/missions/mission/view.html',
		url:'/view',
		resolve: {
			mission: ["PromotionRest", "$stateParams", function(rest, $stateParams) {
				return rest.view($stateParams.id);
			}],
		},
		redirectTo: "dashboard.missions.mission.view.details"
	});

	$stateProvider.state('dashboard.missions.mission.view.details', {
		templateUrl:'scripts/controllers/dashboard/missions/mission/view/view.html',
		url:'/details',
		controller:'MissionViewController as controller',
		resolve: {
			domain: function() { return },
			mission: ["PromotionRest", "$stateParams", function(rest, $stateParams) {
				return rest.view($stateParams.id);
			}],
			gamesList: ["PromotionRest", "rest-games", "$stateParams", function(PromotionRest, gamesRest, $stateParams) {
				return PromotionRest.view($stateParams.id).then(function(response) {
					var mission = response.plain();
					return gamesRest.list(mission.current.domain.name);
				});
			}],
			loadMyFiles:function($ocLazyLoad) {
				return $ocLazyLoad.load({
					name:'lithium',
					files: [
						'scripts/controllers/dashboard/missions/mission/view/view.js'
					]
				})
			}
		}
	});

	$stateProvider.state('dashboard.missions.mission.view.usermissions', {
		templateUrl:'scripts/controllers/dashboard/missions/mission/view/usermissions/list/list.html',
		url:'/userpromotions',
		controller:'MissionRevisionUserMissionsListController as controller',
		resolve: {
			domain: function() { return },
			missionRevision: ["PromotionRest", "$stateParams", function(rest, $stateParams) {
				return rest.findRevision($stateParams.id, $stateParams.missionRevisionId);
			}],
			loadMyFiles:function($ocLazyLoad) {
				return $ocLazyLoad.load({
					name:'lithium',
					files: [
						'scripts/controllers/dashboard/missions/mission/view/usermissions/list/list.js'
					]
				})
			}
		}
	});

	$stateProvider.state('dashboard.missions.edit', {
		templateUrl:'scripts/controllers/dashboard/missions/mission/edit/edit.html',
		url:'/{id}/edit',
		controller:'MissionEditController as controller',
		resolve: {
			domain: function() { return },
			mission: ["PromotionRest", "$stateParams", function(rest, $stateParams) {
				return rest.edit($stateParams.id);
			}],
			gamesList: ["PromotionRest", "rest-games", "$stateParams", function(PromotionRest, gamesRest, $stateParams) {
				return PromotionRest.view($stateParams.id).then(function(response) {
					var mission = response.plain();
					return gamesRest.list(mission.current.domain.name);
				});
			}],
			bonusList: ["PromotionRest", "rest-casino", "$stateParams", function(PromotionRest, casinoRest, $stateParams) {
				return PromotionRest.view($stateParams.id).then(function(response) {
					var mission = response.plain();
					return casinoRest.findPublicBonusListV2(mission.current.domain.name, 2, 5)
				});
			}],
			loadMyFiles:function($ocLazyLoad) {
				return $ocLazyLoad.load({
					name:'lithium',
					files: [
						'scripts/controllers/dashboard/missions/mission/edit/edit.js'
					]
				})
			}
		}
	});

	$stateProvider.state('dashboard.usermissions',{
		template: "<div ui-view></div>",
		url:'/userpromotions',
		resolve: {
			loadMyFiles:function($ocLazyLoad) {
				return $ocLazyLoad.load({
					name:'lithium',
					files: [
						'scripts/controllers/dashboard/usermissions/list/list.js'
					]
				})
			}
		},
		ncyBreadcrumb: { skip: true },
		redirectTo: "dashboard.usermissions.list"
	});

	$stateProvider.state('dashboard.usermissions.list',{
		templateUrl:'scripts/controllers/dashboard/usermissions/list/list.html',
		data: {
			pageTitle: "UI_NETWORK_ADMIN.PAGE.TITLE.USER_MISSIONS",
		},
		url:'',
		controller:'UserMissionsListController as controller',
		ncyBreadcrumb: {
			parent: 'dashboard.usermissions',
			label: 'user missions'
		}
	});

	$stateProvider.state('dashboard.usermissions.view', {
		templateUrl:'scripts/controllers/dashboard/usermissions/view/view.html',
		url:'/{id}/view',
		controller:'UserMissionViewController as controller',
		resolve: {
			domain: function() { return },
			usermission: ["PromotionRest", "$stateParams", function(rest, $stateParams) {
				return rest.findUserPromotionById($stateParams.id);
			}],
			gamesList: ["PromotionRest", "rest-games", "$stateParams", function(PromotionRest, gamesRest, $stateParams) {
				return PromotionRest.findUserPromotionById($stateParams.id).then(function(response) {
					var usermission = response.plain();
					return gamesRest.list(usermission.missionRevision.domain.name);
				});
			}],
			loadMyFiles:function($ocLazyLoad) {
				return $ocLazyLoad.load({
					name:'lithium',
					files: [
						'scripts/controllers/dashboard/usermissions/view/view.js'
					]
				})
			}
		}
	});

	$stateProvider.state('dashboard.gogamegames',{
		template: '<div ui-view></div>',
		url:'/gogamegames',
		ncyBreadcrumb: { skip: true }
	});

	$stateProvider.state('dashboard.gogamegames.hotometers',{
		template: "<div ui-view></div>",
		url:'/hotometers',
		resolve: {
			loadMyFiles:function($ocLazyLoad) {
				return $ocLazyLoad.load({
					name:'lithium',
					files: [
						'scripts/controllers/dashboard/gogamegames/hotometers/list/list.js'
					]
				})
			}
		},
		ncyBreadcrumb: { skip: true },
		redirectTo: "dashboard.gogamegames.hotometers.list"
	});

	$stateProvider.state('dashboard.gogamegames.hotometers.list',{
		templateUrl:'scripts/controllers/dashboard/gogamegames/hotometers/list/list.html',
		data: {
			pageTitle: "UI_NETWORK_ADMIN.PAGE.TITLE.GOGAMES_METERS",
		},
		url:'',
		controller:'GoGameHotOMetersListController as controller',
		ncyBreadcrumb: {
			parent: 'dashboard.gogamegames',
			label: 'gogame hotometers list'
		}
	});

	$stateProvider.state('dashboard.gogamegames.engines',{
		template: "<div ui-view></div>",
		url:'/engines',
		resolve: {
			loadMyFiles:function($ocLazyLoad) {
				return $ocLazyLoad.load({
					name:'lithium',
					files: [
						'scripts/controllers/dashboard/gogamegames/engines/list/list.js'
					]
				})
			}
		},
		ncyBreadcrumb: { skip: true },
		redirectTo: "dashboard.gogamegames.engines.list"
	});

	$stateProvider.state('dashboard.gogamegames.engines.list',{
		templateUrl:'scripts/controllers/dashboard/gogamegames/engines/list/list.html',
		data: {
			pageTitle: "UI_NETWORK_ADMIN.PAGE.TITLE.GOGAMES_ENGINES",
		},
		url:'',
		controller:'GoGameEnginesListController as controller',
		ncyBreadcrumb: {
			parent: 'dashboard.gogamegames',
			label: 'gogame engines list'
		}
	});

	$stateProvider.state('dashboard.gogamegames.engines.engine', {
		templateUrl: "scripts/controllers/dashboard/gogamegames/engines/engine/engine.html",
		url:'/{id}',
		controller: 'GoGameEnginesEngineController as controller',
		resolve: {
			engine: ["GoGameGamesRest", "$stateParams", function(rest, $stateParams) {
				return rest.findenginebyid($stateParams.id);
			}],
			features: ["GoGameGamesRest", "$stateParams", function(rest, $stateParams) {
				return rest.findEngineFeatures($stateParams.id);
			}],
			symbols: ["GoGameGamesRest", "$stateParams", function(rest, $stateParams) {
				return rest.findEngineSymbols($stateParams.id);
			}],
			loadMyFiles:function($ocLazyLoad) {
				return $ocLazyLoad.load({
					name:'lithium',
					files: [
						'scripts/controllers/dashboard/gogamegames/engines/engine/engine.js'
					]
				})
			}
		},
		ncyBreadcrumb: { skip: true }
	});

	$stateProvider.state('dashboard.gogamegames.mathmodels',{
		template: "<div ui-view></div>",
		url:'/mathmodels',
		resolve: {
			loadMyFiles:function($ocLazyLoad) {
				return $ocLazyLoad.load({
					name:'lithium',
					files: [
						'scripts/controllers/dashboard/gogamegames/mathmodels/list/list.js',
						'scripts/controllers/dashboard/gogamegames/mathmodels/add/add.js'
					]
				})
			}
		},
		ncyBreadcrumb: { skip: true },
		redirectTo: "dashboard.gogamegames.mathmodels.list"
	});

	$stateProvider.state('dashboard.gogamegames.mathmodels.list',{
		templateUrl:'scripts/controllers/dashboard/gogamegames/mathmodels/list/list.html',
		data: {
			pageTitle: "UI_NETWORK_ADMIN.PAGE.TITLE.GOGAMES_MATH",
		},
		url:'',
		controller:'GoGameMathModelsListController as controller',
		ncyBreadcrumb: {
			parent: 'dashboard.gogamegames',
			label: 'gogame math models list'
		}
	});

	$stateProvider.state('dashboard.gogamegames.mathmodels.add', {
		templateUrl:'scripts/controllers/dashboard/gogamegames/mathmodels/add/add.html',
		url:'/add',
		controller:'GoGameMathModelsAddController as controller',
		resolve: {
		},
		ncyBreadcrumb: {
			label: 'add a gogame math model'
		}
	});

	$stateProvider.state('dashboard.gogamegames.mathmodels.mathmodel', {
		templateUrl: "scripts/controllers/dashboard/gogamegames/mathmodels/mathmodel/mathmodel.html",
		url:'/mathmodel/{id}/{mathModelRevisionId}',
		controller: 'GoGameMathModelController as controller',
		resolve: {
			mathModel: ["GoGameGamesRest", "$stateParams", function(rest, $stateParams) {
				return rest.findmathmodelbyid($stateParams.id);
			}],
			mathModelRevision: ["GoGameGamesRest", "$stateParams", function(rest, $stateParams) {
				return rest.findmathmodelrevisionbyid($stateParams.id, $stateParams.mathModelRevisionId);
			}],
			loadMyFiles:function($ocLazyLoad) {
				return $ocLazyLoad.load({
					name:'lithium',
					files: [
						'scripts/controllers/dashboard/gogamegames/mathmodels/mathmodel/mathmodel.js'
					]
				})
			}
		},
		ncyBreadcrumb: { skip: true }
	});

	$stateProvider.state('dashboard.gogamegames.mathmodels.mathmodel.revisions', {
		template:'<div ui-view></div>',
		url:'/revisions',
		resolve: {
			mathModel: ["GoGameGamesRest", "$stateParams", function(rest, $stateParams) {
				return rest.findmathmodelbyid($stateParams.id);
			}]
		},
		redirectTo: "dashboard.gogamegames.mathmodels.mathmodel.revisions.list"
	});

	$stateProvider.state('dashboard.gogamegames.mathmodels.mathmodel.revisions.list', {
		templateUrl:'scripts/controllers/dashboard/gogamegames/mathmodels/mathmodel/view/revisions/revisions.html',
		url:'/list',
		controller:'GoGameMathModelRevisionsListController as controller',
		resolve: {
			mathModel: ["GoGameGamesRest", "$stateParams", function(rest, $stateParams) {
				return rest.findmathmodelbyid($stateParams.id);
			}],
			loadMyFiles:function($ocLazyLoad) {
				return $ocLazyLoad.load({
					name:'lithium',
					files: [
						'scripts/controllers/dashboard/gogamegames/mathmodels/mathmodel/view/revisions/revisions.js'
					]
				})
			}
		}
	});

	$stateProvider.state('dashboard.gogamegames.mathmodels.mathmodel.view', {
		templateUrl:'scripts/controllers/dashboard/gogamegames/mathmodels/mathmodel/view.html',
		url:'/view',
		resolve: {
			mathModel: ["GoGameGamesRest", "$stateParams", function(rest, $stateParams) {
				return rest.findmathmodelbyid($stateParams.id);
			}],
		},
		redirectTo: "dashboard.gogamegames.mathmodels.mathmodel.view.details"
	});

	$stateProvider.state('dashboard.gogamegames.mathmodels.mathmodel.view.details', {
		templateUrl:'scripts/controllers/dashboard/gogamegames/mathmodels/mathmodel/view/view.html',
		url:'/details',
		controller:'GoGameMathModelViewController as controller',
		resolve: {
			domain: function() { return },
			mathModel: ["GoGameGamesRest", "$stateParams", function(rest, $stateParams) {
				return rest.findmathmodelbyid($stateParams.id);
			}],
			loadMyFiles:function($ocLazyLoad) {
				return $ocLazyLoad.load({
					name:'lithium',
					files: [
						'scripts/controllers/dashboard/gogamegames/mathmodels/mathmodel/view/view.js'
					]
				})
			}
		}
	});

	$stateProvider.state('dashboard.gogamegames.mathmodels.edit', {
		templateUrl:'scripts/controllers/dashboard/gogamegames/mathmodels/mathmodel/edit/edit.html',
		url:'/{id}/edit',
		controller:'GoGameMathModelEditController as controller',
		resolve: {
			mathModel: ["GoGameGamesRest", "$stateParams", function(rest, $stateParams) {
				return rest.modifyMathModel($stateParams.id);
			}],
			loadMyFiles:function($ocLazyLoad) {
				return $ocLazyLoad.load({
					name:'lithium',
					files: [
						'scripts/controllers/dashboard/gogamegames/mathmodels/mathmodel/edit/edit.js'
					]
				})
			}
		}
	});

	$stateProvider.state('dashboard.gogamegames.ledgers',{
		template: "<div ui-view></div>",
		url:'/ledgers',
		resolve: {
			loadMyFiles:function($ocLazyLoad) {
				return $ocLazyLoad.load({
					name:'lithium',
					files: [
						'scripts/controllers/dashboard/gogamegames/ledgers/list/list.js',
						'scripts/controllers/dashboard/gogamegames/ledgers/add/add.js'
					]
				})
			}
		},
		ncyBreadcrumb: { skip: true },
		redirectTo: "dashboard.gogamegames.ledgers.list"
	});

	$stateProvider.state('dashboard.gogamegames.ledgers.list',{
		templateUrl:'scripts/controllers/dashboard/gogamegames/ledgers/list/list.html',
		data: {
			pageTitle: "UI_NETWORK_ADMIN.PAGE.TITLE.GOGAMES_LEDGERS",
		},
		url:'',
		controller:'GoGameLedgersListController as controller',
		ncyBreadcrumb: {
			parent: 'dashboard.gogamegames',
			label: 'gogame ledgers list'
		}
	});

	$stateProvider.state('dashboard.gogamegames.ledgers.ledger',{
		template: "<div ui-view></div>",
		url:'/{id}',
		resolve: {
			ledger: ["GoGameGamesRest", "$stateParams", function(rest, $stateParams) {
				return rest.findledgerbyid($stateParams.id);
			}],
			ledgerAnalysis: ["GoGameGamesRest", "$stateParams", function(rest, $stateParams) {
				return rest.ledgerAnalysis($stateParams.id);
			}],
			countNew: ["GoGameGamesRest", "$stateParams", function(rest, $stateParams) {
				return rest.countEntriesByStatus($stateParams.id, null, 'new');
			}],
			countSeen: ["GoGameGamesRest", "$stateParams", function(rest, $stateParams) {
				return rest.countEntriesByStatus($stateParams.id, null, 'seen');
			}],
			countTaken: ["GoGameGamesRest", "$stateParams", function(rest, $stateParams) {
				return rest.countEntriesByStatus($stateParams.id, null, 'taken');
			}],
			loadMyFiles:function($ocLazyLoad) {
				return $ocLazyLoad.load({
					name:'lithium',
					files: [
						'scripts/controllers/dashboard/gogamegames/ledgers/ledger/ledger.js',
						'scripts/controllers/dashboard/gogamegames/ledgers/ledger/block/block.js'
					]
				})
			}
		},
		ncyBreadcrumb: { skip: true },
		redirectTo: "dashboard.gogamegames.ledgers.ledger.view"
	});

	$stateProvider.state('dashboard.gogamegames.ledgers.ledger.view', {
		templateUrl: "scripts/controllers/dashboard/gogamegames/ledgers/ledger/ledger.html",
		url:'',
		controller: 'GoGameLedgersLedgerController as controller',
		ncyBreadcrumb: { skip: true }
	});

	$stateProvider.state('dashboard.gogamegames.ledgers.ledger.block', {
		templateUrl: "scripts/controllers/dashboard/gogamegames/ledgers/ledger/block/block.html",
		url:'/blocks/{ledgerBlockId}',
		controller: 'GoGameLedgersLedgerBlockController as controller',
		resolve: {
			ledger: ["GoGameGamesRest", "$stateParams", function(rest, $stateParams) {
				return rest.findledgerbyid($stateParams.id);
			}],
			ledgerBlock: ["GoGameGamesRest", "$stateParams", function(rest, $stateParams) {
				return rest.findledgerblockbyid($stateParams.id, $stateParams.ledgerBlockId);
			}],
			countNew: ["GoGameGamesRest", "$stateParams", function(rest, $stateParams) {
				return rest.countEntriesByStatus($stateParams.id, $stateParams.ledgerBlockId, 'new');
			}],
			countSeen: ["GoGameGamesRest", "$stateParams", function(rest, $stateParams) {
				return rest.countEntriesByStatus($stateParams.id, $stateParams.ledgerBlockId, 'seen');
			}],
			countTaken: ["GoGameGamesRest", "$stateParams", function(rest, $stateParams) {
				return rest.countEntriesByStatus($stateParams.id, $stateParams.ledgerBlockId, 'taken');
			}],
			loadMyFiles:function($ocLazyLoad) {
				return $ocLazyLoad.load({
					name:'lithium',
					files: [
						'scripts/controllers/dashboard/gogamegames/ledgers/ledger/block/block.js'
					]
				})
			}
		}
	});

	$stateProvider.state('dashboard.gogamegames.resultbatches',{
		template: "<div ui-view></div>",
		url:'/resultbatches',
		resolve: {
			loadMyFiles:function($ocLazyLoad) {
				return $ocLazyLoad.load({
					name:'lithium',
					files: [
						'scripts/controllers/dashboard/gogamegames/resultbatches/list/list.js'
					]
				})
			}
		},
		ncyBreadcrumb: { skip: true },
		redirectTo: "dashboard.gogamegames.resultbatches.list"
	});

	$stateProvider.state('dashboard.gogamegames.resultbatches.list',{
		templateUrl:'scripts/controllers/dashboard/gogamegames/resultbatches/list/list.html',
		data: {
			pageTitle: "UI_NETWORK_ADMIN.PAGE.TITLE.GOGAMES_RESULTS",
		},
		url:'',
		controller:'GoGameResultBatchesListController as controller',
		ncyBreadcrumb: {
			parent: 'dashboard.gogamegames',
			label: 'gogame result batches list'
		}
	});

	$stateProvider.state('dashboard.gogamegames.resultbatches.resultbatch', {
		templateUrl: "scripts/controllers/dashboard/gogamegames/resultbatches/resultbatch/resultbatch.html",
		url:'/{id}',
		controller: 'GoGameResultBatchesResultBatchController as controller',
		resolve: {
			resultBatch: ["GoGameGamesRest", "$stateParams", function(rest, $stateParams) {
				return rest.findResultBatchById($stateParams.id);
			}],
			resultBatchAnalysis: ["GoGameGamesRest", "$stateParams", function(rest, $stateParams) {
				return rest.findResultBatchAnalysisByResultBatchId($stateParams.id);
			}],
			loadMyFiles:function($ocLazyLoad) {
				return $ocLazyLoad.load({
					name:'lithium',
					files: [
						'scripts/controllers/dashboard/gogamegames/resultbatches/resultbatch/resultbatch.js'
					]
				})
			}
		},
		ncyBreadcrumb: { skip: true }
	});

	$stateProvider.state('dashboard.gogamegames.games',{
		template: "<div ui-view></div>",
		url:'/games',
		resolve: {
			loadMyFiles:function($ocLazyLoad) {
				return $ocLazyLoad.load({
					name:'lithium',
					files: [
						'scripts/controllers/dashboard/gogamegames/games/list/list.js',
						'scripts/controllers/dashboard/gogamegames/games/add/add.js'
					]
				})
			}
		},
		ncyBreadcrumb: { skip: true },
		redirectTo: "dashboard.gogamegames.games.list"
	});

	$stateProvider.state('dashboard.gogamegames.games.list',{
		templateUrl:'scripts/controllers/dashboard/gogamegames/games/list/list.html',
		data: {
			pageTitle: "UI_NETWORK_ADMIN.PAGE.TITLE.GOGAMES_GAMES",
		},
		url:'',
		controller:'GoGameGamesListController as controller',
		ncyBreadcrumb: {
			parent: 'dashboard.gogamegames',
			label: 'gogame games list'
		}
	});

	$stateProvider.state('dashboard.gogamegames.games.game', {
		templateUrl: "scripts/controllers/dashboard/gogamegames/games/game/game.html",
		url:'/{id}/{domainName}',
		controller: 'GoGameGamesGameController as controller',
		resolve: {
			game: ["GoGameGamesRest", "$stateParams", function(rest, $stateParams) {
				return rest.findgamebyid($stateParams.id, $stateParams.domainName);
			}],
			gameLedgers: ["GoGameGamesRest", "$stateParams", function(rest, $stateParams) {
				return rest.findGameLedgers($stateParams.id, $stateParams.domainName);
			}],
			loadMyFiles:function($ocLazyLoad) {
				return $ocLazyLoad.load({
					name:'lithium',
					files: [
						'scripts/controllers/dashboard/gogamegames/games/game/game.js'
					]
				})
			}
		},
		ncyBreadcrumb: { skip: true }
	});

	$stateProvider.state('dashboard.gogamegames.spins',{
		template: "<div ui-view></div>",
		url:'/spins',
		resolve: {
			loadMyFiles:function($ocLazyLoad) {
				return $ocLazyLoad.load({
					name:'lithium',
					files: [
						'scripts/controllers/dashboard/gogamegames/spins/list/list.js'
					]
				})
			}
		},
		ncyBreadcrumb: { skip: true },
		redirectTo: "dashboard.gogamegames.spins.list"
	});

	$stateProvider.state('dashboard.gogamegames.spins.list',{
		templateUrl:'scripts/controllers/dashboard/gogamegames/spins/list/list.html',
		data: {
			pageTitle: "UI_NETWORK_ADMIN.PAGE.TITLE.GOGAMES_SPINS",
		},
		url:'',
		controller:'GoGameSpinsListController as controller',
		ncyBreadcrumb: {
			parent: 'dashboard.gogamegames',
			label: 'gogame game spins list'
		}
	});

	$stateProvider.state('dashboard.gogamegames.uploadederrors',{
		template: "<div ui-view></div>",
		url:'/uploadederrors',
		resolve: {
			loadMyFiles:function($ocLazyLoad) {
				return $ocLazyLoad.load({
					name:'lithium',
					files: [
						'scripts/controllers/dashboard/gogamegames/uploadederrors/list/list.js'
					]
				})
			}
		},
		ncyBreadcrumb: { skip: true },
		redirectTo: "dashboard.gogamegames.uploadederrors.list"
	});

	$stateProvider.state('dashboard.gogamegames.uploadederrors.list',{
		templateUrl:'scripts/controllers/dashboard/gogamegames/uploadederrors/list/list.html',
		data: {
			pageTitle: "UI_NETWORK_ADMIN.PAGE.TITLE.GOGAMES_ERRORS",
		},
		url:'',
		controller:'GoGameUploadedErrorsListController as controller',
		ncyBreadcrumb: {
			parent: 'dashboard.gogamegames',
			label: 'gogame game uploaded errors list'
		}
	});

	$stateProvider.state('dashboard.gogamegames.reelconfigs',{
		template: "<div ui-view></div>",
		url:'/reels/configs',
		resolve: {
			loadMyFiles:function($ocLazyLoad) {
				return $ocLazyLoad.load({
					name:'lithium',
					files: [
						'scripts/controllers/dashboard/gogamegames/reels/configs/list/list.js',
						'scripts/controllers/dashboard/gogamegames/reels/configs/add/add.js'
					]
				})
			}
		},
		ncyBreadcrumb: { skip: true },
		redirectTo: "dashboard.gogamegames.reelconfigs.list"
	});

	$stateProvider.state('dashboard.gogamegames.reelconfigs.list',{
		templateUrl:'scripts/controllers/dashboard/gogamegames/reels/configs/list/list.html',
		data: {
			pageTitle: "UI_NETWORK_ADMIN.PAGE.TITLE.GOGAMES_REELS",
		},
		url:'',
		controller:'GoGameReelConfigsListController as controller',
		ncyBreadcrumb: {
			parent: 'dashboard.gogamegames',
			label: 'gogame reel configurations list'
		}
	});

	$stateProvider.state('dashboard.gogamegames.reelconfigs.view', {
		templateUrl: "scripts/controllers/dashboard/gogamegames/reels/configs/view/view.html",
		url:'/view/{id}',
		controller: 'GoGameReelConfigurationViewController as controller',
		resolve: {
			config: ["GoGameGamesRest", "$stateParams", function(rest, $stateParams) {
				return rest.findreelconfigbyid($stateParams.id);
			}],
			loadMyFiles:function($ocLazyLoad) {
				return $ocLazyLoad.load({
					name:'lithium',
					files: [
						'scripts/controllers/dashboard/gogamegames/reels/configs/view/view.js'
					]
				})
			}
		},
		ncyBreadcrumb: { skip: true }
	});

	$stateProvider.state('dashboard.gogamegames.reelconfigs.add',{
		templateUrl:'scripts/controllers/dashboard/gogamegames/reels/configs/add/add.html',
		url:'/add?copiedFromId',
		controller:'GoGameReelConfigsAddController as controller',
		resolve: {
			copy: ["GoGameGamesRest", "$stateParams", function(rest, $stateParams) {
				if ($stateParams.copiedFromId !== undefined && $stateParams.copiedFromId !== null) {
					return rest.findreelconfigbyid($stateParams.copiedFromId);
				}
				return null;
			}]
		},
		ncyBreadcrumb: {
			label: 'add a gogame reel configuration'
		}
	});

	$stateProvider.state('dashboard.gogamegames.reelsets',{
		template: "<div ui-view></div>",
		url:'/reels/sets',
		resolve: {
			loadMyFiles:function($ocLazyLoad) {
				return $ocLazyLoad.load({
					name:'lithium',
					files: [
						'scripts/controllers/dashboard/gogamegames/reels/sets/list/list.js',
						'scripts/controllers/dashboard/gogamegames/reels/sets/add/add.js'
					]
				})
			}
		},
		ncyBreadcrumb: { skip: true },
		redirectTo: "dashboard.gogamegames.reelsets.list"
	});

	$stateProvider.state('dashboard.gogamegames.reelsets.list',{
		templateUrl:'scripts/controllers/dashboard/gogamegames/reels/sets/list/list.html',
		data: {
			pageTitle: "UI_NETWORK_ADMIN.PAGE.TITLE.GOGAMES_SETS",
		},
		url:'',
		controller:'GoGameReelSetsListController as controller',
		ncyBreadcrumb: {
			parent: 'dashboard.gogamegames',
			label: 'gogame reel sets list'
		}
	});

	$stateProvider.state('dashboard.gogamegames.reelsets.add',{
		templateUrl:'scripts/controllers/dashboard/gogamegames/reels/sets/add/add.html',
		url:'/add',
		controller:'GoGameReelSetAddController as controller',
		resolve: {
		},
		ncyBreadcrumb: {
			label: 'add a gogame reel set'
		}
	});

	$stateProvider.state('dashboard.gogamegames.reelsets.view', {
		templateUrl: "scripts/controllers/dashboard/gogamegames/reels/sets/view/view.html",
		url:'/view/{id}',
		controller: 'GoGameReelSetViewController as controller',
		resolve: {
			reelSet: ["GoGameGamesRest", "$stateParams", function(rest, $stateParams) {
				return rest.findReelSetById($stateParams.id);
			}],
			loadMyFiles:function($ocLazyLoad) {
				return $ocLazyLoad.load({
					name:'lithium',
					files: [
						'scripts/controllers/dashboard/gogamegames/reels/sets/view/view.js'
					]
				})
			}
		},
		ncyBreadcrumb: { skip: true }
	});

	$stateProvider.state('dashboard.gogamegames.dailygame',{
		template: "<div ui-view></div>",
		url:'/dailygame',
		resolve: {
			loadMyFiles:function($ocLazyLoad) {
				return $ocLazyLoad.load({
					name:'lithium',
					files: [
						'scripts/controllers/dashboard/gogamegames/dailygame/list/list.js'
					]
				})
			}
		},
		ncyBreadcrumb: { skip: true },
		redirectTo: "dashboard.gogamegames.dailygame.list"
	});

	$stateProvider.state('dashboard.gogamegames.dailygame.list',{
		templateUrl:'scripts/controllers/dashboard/gogamegames/dailygame/list/list.html',
		data: {
			pageTitle: "UI_NETWORK_ADMIN.PAGE.TITLE.GOGAMES_PLAYS",
		},
		url:'',
		controller:'GoGameDailyGameListController as controller',
		ncyBreadcrumb: {
			parent: 'dashboard.gogamegames',
			label: 'gogame daily game list'
		}
	});

	$stateProvider.state('dashboard.gogamegames.dailygame.view', {
		templateUrl: "scripts/controllers/dashboard/gogamegames/dailygame/view/view.html",
		url:'/view/{id}',
		controller: 'GoGameDailyGameViewController as controller',
		resolve: {
			dailyGame: ["GoGameGamesRest", "$stateParams", function(rest, $stateParams) {
				return rest.findDailyGamePlay($stateParams.id);
			}],
			loadMyFiles:function($ocLazyLoad) {
				return $ocLazyLoad.load({
					name:'lithium',
					files: [
						'scripts/controllers/dashboard/gogamegames/dailygame/view/view.js'
					]
				})
			}
		},
		ncyBreadcrumb: { skip: true }
	});

	$stateProvider.state('dashboard.gogamegames.tutorials',{
		template: "<div ui-view></div>",
		url:'/tutorials',
		resolve: {
			loadMyFiles:function($ocLazyLoad) {
				return $ocLazyLoad.load({
					name:'lithium',
					files: [
						'scripts/controllers/dashboard/gogamegames/tutorials/list/list.js',
						'scripts/controllers/dashboard/gogamegames/tutorials/add/add.js'
					]
				})
			}
		},
		ncyBreadcrumb: { skip: true },
		redirectTo: "dashboard.gogamegames.tutorials.list"
	});

	$stateProvider.state('dashboard.gogamegames.tutorials.list',{
		templateUrl:'scripts/controllers/dashboard/gogamegames/tutorials/list/list.html',
		data: {
			pageTitle: "UI_NETWORK_ADMIN.PAGE.TITLE.GOGAMES_TUTORIALS",
		},
		url:'',
		controller:'GoGameTutorialsListController as controller',
		ncyBreadcrumb: {
			parent: 'dashboard.gogamegames',
			label: 'gogame game tutorials list'
		}
	});

	$stateProvider.state('dashboard.gogamegames.tutorials.add',{
		templateUrl:'scripts/controllers/dashboard/gogamegames/tutorials/add/add.html',
		url:'/add',
		controller:'GoGameTutorialsAddController as controller',
		resolve: {
		},
		ncyBreadcrumb: {
			label: 'add a gogame game tutorial'
		}
	});

	$stateProvider.state('dashboard.gogamegames.tutorials.tutorial',{
		template: "<div ui-view></div>",
		url:'/{id}',
		resolve: {
			tutorial: ["GoGameGamesRest", "$stateParams", function(rest, $stateParams) {
				return rest.findtutorialbyid($stateParams.id);
			}],
			loadMyFiles:function($ocLazyLoad) {
				return $ocLazyLoad.load({
					name:'lithium',
					files: [
						'scripts/controllers/dashboard/gogamegames/tutorials/tutorial/tutorial.js',
						'scripts/controllers/dashboard/gogamegames/tutorials/edit/edit.js',
					]
				})
			}
		},
		ncyBreadcrumb: { skip: true },
		redirectTo: "dashboard.gogamegames.tutorials.tutorial.view"
	});

	$stateProvider.state('dashboard.gogamegames.tutorials.tutorial.view', {
		templateUrl: "scripts/controllers/dashboard/gogamegames/tutorials/tutorial/tutorial.html",
		url:'',
		controller: 'GoGameTutorialsTutorialController as controller',
		ncyBreadcrumb: { skip: true }
	});

	$stateProvider.state('dashboard.gogamegames.tutorials.tutorial.edit', {
		templateUrl: "scripts/controllers/dashboard/gogamegames/tutorials/edit/edit.html",
		url:'/edit',
		controller: 'GoGameTutorialsEditController as controller',
		ncyBreadcrumb: { skip: true }
	});

	$stateProvider.state('dashboard.gogamegames.debugresults',{
		template: "<div ui-view></div>",
		url:'/debugresults',
		resolve: {
			loadMyFiles:function($ocLazyLoad) {
				return $ocLazyLoad.load({
					name:'lithium',
					files: [
						'scripts/controllers/dashboard/gogamegames/debugresults/list/list.js',
						'scripts/controllers/dashboard/gogamegames/debugresults/add/add.js',
						'scripts/controllers/dashboard/gogamegames/debugresults/delete/delete.js'
					]
				})
			}
		},
		ncyBreadcrumb: { skip: true },
		redirectTo: "dashboard.gogamegames.debugresults.list"
	});

	$stateProvider.state('dashboard.gogamegames.debugresults.list',{
		templateUrl:'scripts/controllers/dashboard/gogamegames/debugresults/list/list.html',
		data: {
			pageTitle: "UI_NETWORK_ADMIN.PAGE.TITLE.GOGAMES_DEBUG",
		},
		url:'',
		controller:'GoGameDebugResultsListController as controller',
		ncyBreadcrumb: {
			parent: 'dashboard.gogamegames',
			label: 'gogame game debug results list'
		}
	});

	$stateProvider.state('dashboard.gogamegames.debugresults.add',{
		templateUrl:'scripts/controllers/dashboard/gogamegames/debugresults/add/add.html',
		url:'/add',
		controller:'GoGameDebugResultsAddController as controller',
		resolve: {
		},
		ncyBreadcrumb: {
			label: 'add a gogame game debug result'
		}
	});

	$stateProvider.state('dashboard.gogamegames.debugresults.delete',{
		templateUrl:'scripts/controllers/dashboard/gogamegames/debugresults/delete/delete.html',
		url:'/delete',
		controller:'GoGameDebugResultsDeleteController as controller',
		resolve: {
		},
		ncyBreadcrumb: {
			label: 'delete engine gogame game debug results'
		}
	});

	$stateProvider.state('dashboard.gogamegames.debugresults.debugresult',{
		template: "<div ui-view></div>",
		url:'/{id}/{engineId}/{mathModelRevisionId}',
		resolve: {
			debugResult: ["GoGameGamesRest", "$stateParams", function(rest, $stateParams) {
				return rest.finddebugresultbyid($stateParams.id, $stateParams.engineId,$stateParams.mathModelRevisionId);
			}],
			loadMyFiles:function($ocLazyLoad) {
				return $ocLazyLoad.load({
					name:'lithium',
					files: [
						'scripts/controllers/dashboard/gogamegames/debugresults/debugresult/debugresult.js',
						'scripts/controllers/dashboard/gogamegames/debugresults/edit/edit.js',
					]
				})
			}
		},
		ncyBreadcrumb: { skip: true },
		redirectTo: "dashboard.gogamegames.debugresults.debugresult.view"
	});

	$stateProvider.state('dashboard.gogamegames.debugresults.debugresult.view', {
		templateUrl: "scripts/controllers/dashboard/gogamegames/debugresults/debugresult/debugresult.html",
		url:'',
		controller: 'GoGameDebugResultController as controller',
		ncyBreadcrumb: { skip: true }
	});

	$stateProvider.state('dashboard.gogamegames.debugresults.debugresult.edit', {
		templateUrl: "scripts/controllers/dashboard/gogamegames/debugresults/edit/edit.html",
		url:'/edit',
		controller: 'GoGameDebugResultsEditController as controller',
		ncyBreadcrumb: { skip: true }
	});

	$stateProvider.state('dashboard.gogamegames.resultsimulations',{
		template: "<div ui-view></div>",
		url:'/resultsimulations',
		resolve: {
			loadMyFiles:function($ocLazyLoad) {
				return $ocLazyLoad.load({
					name:'lithium',
					files: [
						'scripts/controllers/dashboard/gogamegames/resultsimulations/list/list.js'
					]
				})
			}
		},
		ncyBreadcrumb: { skip: true },
		redirectTo: "dashboard.gogamegames.resultsimulations.list"
	});

	$stateProvider.state('dashboard.gogamegames.resultsimulations.list',{
		templateUrl:'scripts/controllers/dashboard/gogamegames/resultsimulations/list/list.html',
		data: {
			pageTitle: "UI_NETWORK_ADMIN.PAGE.TITLE.GOGAMES_SIMULATIONS",
		},
		url:'',
		controller:'GoGameResultSimulationsListController as controller',
		ncyBreadcrumb: {
			parent: 'dashboard.gogamegames',
			label: 'gogame game result simulations list'
		}
	});

	$stateProvider.state('dashboard.gogamegames.resultsimulations.resultsimulation', {
		templateUrl: "scripts/controllers/dashboard/gogamegames/resultsimulations/resultsimulation/resultsimulation.html",
		url:'/{id}',
		controller: 'GoGameResultSimulationsResultSimulationController as controller',
		resolve: {
			resultSimulation: ["GoGameGamesRest", "$stateParams", function(rest, $stateParams) {
				return rest.findResultSimulationById($stateParams.id);
			}],
			loadMyFiles:function($ocLazyLoad) {
				return $ocLazyLoad.load({
					name:'lithium',
					files: [
						'scripts/controllers/dashboard/gogamegames/resultsimulations/resultsimulation/resultsimulation.js'
					]
				})
			}
		},
		ncyBreadcrumb: { skip: true }
	});

	$stateProvider.state('dashboard.gogamegames.exhaustionrates',{
		template: "<div ui-view></div>",
		url:'/exhaustionrates',
		resolve: {
			loadMyFiles:function($ocLazyLoad) {
				return $ocLazyLoad.load({
					name:'lithium',
					files: [
						'scripts/controllers/dashboard/gogamegames/exhaustionrates/list/list.js'
					]
				})
			}
		},
		ncyBreadcrumb: { skip: true },
		redirectTo: "dashboard.gogamegames.exhaustionrates.list"
	});

	$stateProvider.state('dashboard.gogamegames.exhaustionrates.list',{
		templateUrl:'scripts/controllers/dashboard/gogamegames/exhaustionrates/list/list.html',
		data: {
			pageTitle: "UI_NETWORK_ADMIN.PAGE.TITLE.GOGAMES_EXHAUSTION",
		},
		url:'',
		controller:'GoGameExhaustionRatesListController as controller',
		ncyBreadcrumb: {
			parent: 'dashboard.gogamegames',
			label: 'gogame game exhaustion rates list'
		}
	});

	$stateProvider.state('dashboard.gogamegames.exhaustionrates.exhaustionrate', {
		templateUrl: "scripts/controllers/dashboard/gogamegames/exhaustionrates/exhaustionrate/exhaustionrate.html",
		url:'/{id}',
		controller: 'GoGameExhaustionRatesExhaustionRateController as controller',
		resolve: {
			exhaustionRate: ["GoGameGamesRest", "$stateParams", function(rest, $stateParams) {
				return rest.findExhaustionRateTestById($stateParams.id);
			}],
			exhaustionRateData: ["GoGameGamesRest", "$stateParams", function(rest, $stateParams) {
				return rest.findExhaustionRateDataById($stateParams.id);
			}],
			loadMyFiles:function($ocLazyLoad) {
				return $ocLazyLoad.load({
					name:'lithium',
					files: [
						'scripts/controllers/dashboard/gogamegames/exhaustionrates/exhaustionrate/exhaustionrate.js'
					]
				})
			}
		},
		ncyBreadcrumb: { skip: true }
	});

	$stateProvider.state('dashboard.gogamegames.windistributions',{
		template: "<div ui-view></div>",
		url:'/windistributions',
		resolve: {
			loadMyFiles:function($ocLazyLoad) {
				return $ocLazyLoad.load({
					name:'lithium',
					files: [
						'scripts/controllers/dashboard/gogamegames/windistributions/list/list.js',
						'scripts/controllers/dashboard/gogamegames/windistributions/add/add.js'
					]
				})
			}
		},
		ncyBreadcrumb: { skip: true },
		redirectTo: "dashboard.gogamegames.windistributions.list"
	});

	$stateProvider.state('dashboard.gogamegames.windistributions.list',{
		templateUrl:'scripts/controllers/dashboard/gogamegames/windistributions/list/list.html',
		data: {
			pageTitle: "UI_NETWORK_ADMIN.PAGE.TITLE.GOGAMES_WINS",
		},
		url:'',
		controller:'GoGameWinDistributionsListController as controller',
		ncyBreadcrumb: {
			parent: 'dashboard.gogamegames',
			label: 'gogame game win distributions list'
		}
	});

	$stateProvider.state('dashboard.gogamegames.windistributions.add', {
		templateUrl:'scripts/controllers/dashboard/gogamegames/windistributions/add/add.html',
		url:'/add',
		controller:'GoGameWinDistributionsAddController as controller',
		resolve: {
			comparators: ["GoGameGamesRest", function(rest) {
				return rest.getComparators();
			}],
		},
		ncyBreadcrumb: {
			label: 'add a gogame win distribution test'
		}
	});

	$stateProvider.state('dashboard.gogamegames.windistributions.windistribution', {
		templateUrl: "scripts/controllers/dashboard/gogamegames/windistributions/windistribution/windistribution.html",
		url:'/{id}',
		controller: 'GoGameWinDistributionsWinDistributionController as controller',
		resolve: {
			winDistribution: ["GoGameGamesRest", "$stateParams", function(rest, $stateParams) {
				return rest.findWinDistributionTestById($stateParams.id);
			}],
			winDistributionBuckets: ["GoGameGamesRest", "$stateParams", function(rest, $stateParams) {
				return rest.findWinDistributionDataById($stateParams.id);
			}],
			loadMyFiles:function($ocLazyLoad) {
				return $ocLazyLoad.load({
					name:'lithium',
					files: [
						'scripts/controllers/dashboard/gogamegames/windistributions/windistribution/windistribution.js'
					]
				})
			}
		},
		ncyBreadcrumb: { skip: true }
	});

	$stateProvider.state('dashboard.incentivegames',{
		template: '<div ui-view></div>',
		url:'/incentivegames',
		ncyBreadcrumb: { skip: true }
	});

	$stateProvider.state('dashboard.incentivegames.bets',{
		template: "<div ui-view></div>",
		url:'/bets',
		resolve: {
			loadMyFiles:function($ocLazyLoad) {
				return $ocLazyLoad.load({
					name:'lithium',
					files: [
						'scripts/controllers/dashboard/incentivegames/bets/list/list.js'
					]
				})
			}
		},
		ncyBreadcrumb: { skip: true },
		redirectTo: "dashboard.incentivegames.bets.list"
	});

	$stateProvider.state('dashboard.incentivegames.bets.list',{
		templateUrl:'scripts/controllers/dashboard/incentivegames/bets/list/list.html',
		data: {
			pageTitle: "UI_NETWORK_ADMIN.PAGE.TITLE.INCENTIVE_BETS",
		},
		url:'',
		controller:'IncentiveGamesBetsListController as controller',
		params: {
			domainName: ["$stateParams", function($stateParams) {
				return $stateParams.domainName;
			}],
			tranType: ["$stateParams", function($stateParams) {
				return $stateParams.tranType;
			}],
			granularity: ["$stateParams", function($stateParams) {
				return $stateParams.granularity;
			}],
			offset: ["$stateParams", function($stateParams) {
				return $stateParams.offset;
			}],
			userGuid: ["$stateParams", function($stateParams) {
				return $stateParams.userGuid;
			}]
		},
		resolve: {
			user: ["UserRest", "$stateParams", function (rest, $stateParams) {
				if ($stateParams.userGuid === undefined ||
					$stateParams.userGuid === null ||
					$stateParams.userGuid === '') return null;
				return rest.findFromGuid($stateParams.domainName, $stateParams.userGuid);
			}],
		},
		ncyBreadcrumb: {
			parent: 'dashboard.incentivegames',
			label: 'incentivegames bets list'
		}
	});

	//Sportsbook
	$stateProvider.state('dashboard.sportsbook',{
		template: '<div ui-view></div>',
		url:'/sportsbook',
		ncyBreadcrumb: { skip: true }
	});

	$stateProvider.state('dashboard.sportsbook.bets.domain', {
		templateUrl:'scripts/controllers/dashboard/sportsbook/bets/domain.html',
		controller:'SportsbookDomainController as controller',
		url:'/domain',
		resolve: {
			loadMyFiles:function($ocLazyLoad) {
				return $ocLazyLoad.load({
					name:'lithium',
					files: [
						'scripts/controllers/dashboard/sportsbook/bets/domain.js'
					]
				})
			}
		},
		ncyBreadcrumb: { skip: true }
	});

	$stateProvider.state('dashboard.sportsbook.bets',{
		template: "<div ui-view></div>",
		url:'/bets',
		params: {
			domainName: ["$stateParams", function($stateParams) {
				return $stateParams.domainName;
			}],
			playerOffset: ["$stateParams", function($stateParams) {
				return $stateParams.playerOffset;
			}]
		},
		resolve: {
			domainName: ["$stateParams", function($stateParams) {
				return $stateParams.domainName;
			}],
			playerOffset: ["$stateParams", function($stateParams) {
				return $stateParams.playerOffset;
			}]
		},
		ncyBreadcrumb: { skip: true },
	});

	$stateProvider.state('dashboard.players.player.sportsbook',{
		templateUrl: 'scripts/controllers/dashboard/players/player/sportsbook/sportsbook.html',
		url:'/sportsbook',
		controller:'PlayerSportsbookController as controller',
		resolve: {
			loadMyFiles:function($ocLazyLoad) {
				return $ocLazyLoad.load({
					name:'lithium',
					files:[ 'scripts/controllers/dashboard/players/player/sportsbook/sportsbook.js' ]
				})
			}
		},
		ncyBreadcrumb: { label: 'sportsbook' },
		redirectTo: "dashboard.players.player.sportsbook.bethistory"
	});

	$stateProvider.state('dashboard.players.player.sportsbook.bethistory',{
		templateUrl: 'scripts/controllers/dashboard/players/player/sportsbook/bethistory/bethistory.html',
		url:'/bethistory',
		controller:'PlayerSportsbookBetHistoryController as controller',
		params: {
			playerOffset: ["$stateParams", function($stateParams) {
				return $stateParams.playerOffset;
			}]
		},
		resolve: {
			playerOffset: ["$stateParams", function($stateParams) {
				return $stateParams.playerOffset;
			}],
			loadMyFiles:function($ocLazyLoad) {
				return $ocLazyLoad.load({
					name:'lithium',
					files:[ 'scripts/controllers/dashboard/players/player/sportsbook/bethistory/bethistory.js' ]
				})
			}
		},
		ncyBreadcrumb: { label: 'sportsbook bet history' }
	});

	$stateProvider.state('dashboard.settlements',{
		template: "<div ui-view></div>",
		url:'/settlements',
		ncyBreadcrumb: { skip: true }
	});

	$stateProvider.state('dashboard.settlements.batches',{
		template: "<div ui-view></div>",
		url:'/batches',
		ncyBreadcrumb: { skip: true }
	});

	$stateProvider.state('dashboard.settlements.batches.list',{
		templateUrl:'scripts/controllers/dashboard/settlements/settlements-batch-list.html',
		data: {
			pageTitle: "UI_NETWORK_ADMIN.PAGE.TITLE.SETTLEMENT",
		},
		url:'/list',
		controller:'SettlementsBatchListController as controller',
		resolve: {
			loadMyFiles:function($ocLazyLoad) {
				return $ocLazyLoad.load({
					name:'lithium',
					files:['scripts/controllers/dashboard/settlements/settlements-batch-list.js']
				})
			}
		},
		ncyBreadcrumb: {
			parent: 'dashboard.settlements',
			label: 'list'
		}
	});

	$stateProvider.state('dashboard.settlements.batches.batch',{
		templateUrl:'scripts/controllers/dashboard/settlements/settlements-list.html',
		url:'/batch/:batchSettlementId',
		controller:'SettlementsListController as controller',
		resolve: {
			batchSettlement: ["BatchSettlementsRest", "$stateParams", function(rest, $stateParams) {
				return rest.get($stateParams.batchSettlementId);
			}],
			loadMyFiles:function($ocLazyLoad) {
				return $ocLazyLoad.load({
					name:'lithium',
					files:['scripts/controllers/dashboard/settlements/settlements-list.js']
				})
			}
		},
		ncyBreadcrumb: {
			parent: 'dashboard.settlements',
			label: 'batch'
		}
	});

	$stateProvider.state('dashboard.settlements.batches.settlement',{
		templateUrl:'scripts/controllers/dashboard/settlements/settlement-view.html',
		url:'/batch/:batchSettlementId/settlement/:settlementId',
		controller:'SettlementViewController as controller',
		resolve: {
			settlement: ["SettlementsRest", "$stateParams", function(rest, $stateParams) {
				return rest.findSettlementById($stateParams.settlementId);
			}],
			loadMyFiles:function($ocLazyLoad) {
				return $ocLazyLoad.load({
					name:'lithium',
					files:['scripts/controllers/dashboard/settlements/settlement-view.js']
				})
			}
		},
		ncyBreadcrumb: {
			parent: 'dashboard.settlements',
			label: 'settlement'
		}
	});

	$stateProvider.state('dashboard.accounting',{
		template: '<div ui-view></div>',
		url:'/accounting',
		ncyBreadcrumb: { skip: true }
	});

	$stateProvider.state('dashboard.accounting.transactions',{
		templateUrl: 'scripts/controllers/dashboard/accounting/transactions/transactions.html',
		data: {
			pageTitle: "UI_NETWORK_ADMIN.PAGE.TITLE.ACCOUNTING",
		},
		url:'/transactions',
		controller:'AccountingTransController as controller',
		resolve: {
			loadMyFiles:function($ocLazyLoad) {
				return $ocLazyLoad.load({
					name:'lithium',
					files: [
						'scripts/controllers/dashboard/accounting/transactions/transactions.js'
					]
				})
			}
		},
		redirectTo: "dashboard.accounting.transactions.list"
	});

	$stateProvider.state('dashboard.accounting.transactions.list',{
		templateUrl:'scripts/controllers/dashboard/accounting/transactions/list.html',
		url:'/{domainName}',
		controller:'AccountingTransListController as controller',
		params: {
			domainName: ["$stateParams", function($stateParams) {
				return $stateParams.domainName;
			}]
		},
		resolve: {
			loadMyFiles:function($ocLazyLoad) {
				return $ocLazyLoad.load({
					name:'lithium',
					files: [
						'scripts/controllers/dashboard/accounting/transactions/list.js'
					]
				})
			}
		}
	});

	$stateProvider.state('dashboard.changelogs',{
		template: '<div ui-view></div>',
		url:'/changelogs',
		ncyBreadcrumb: { skip: true }
	});

	$stateProvider.state('dashboard.changelogs.entries',{
		template: "<div ui-view></div>",
		url:'/entries',
		ncyBreadcrumb: { skip: true },
		redirectTo: "dashboard.changelogs.entries.list"
	});

	$stateProvider.state('dashboard.changelogs.entries.list',{
		templateUrl:'scripts/controllers/dashboard/changelogs/list/list.html',
		url:'',
		ncyBreadcrumb: {
			parent: 'dashboard.changelogs',
			label: 'global changelog entries'
		}
	});

	$stateProvider.state('dashboard.changelogs.entries.entry', {
		templateUrl: "scripts/controllers/dashboard/changelogs/entry/entry.html",
		url:'/:entryid',
		resolve: {
			entry: ["ChangelogsRest", "$stateParams", "$rootScope", function(rest, $stateParams, $rootScope) {
				return rest.changeLogWithFieldChanges($stateParams.entryid);
			}]
		},
		ncyBreadcrumb: { skip: true }
	});


	$stateProvider.state('dashboard.cashier.autowithdrawals',{
		template: "<div ui-view></div>",
		url:'/autowithdrawals',
		ncyBreadcrumb: { skip: true },
		redirectTo: "dashboard.cashier.autowithdrawals.rulesets"
	});

	$stateProvider.state('dashboard.cashier.autowithdrawals.rulesets',{
		template: "<div ui-view></div>",
		url:'/rulesets',
		resolve: {
			loadMyFiles:function($ocLazyLoad) {
				return $ocLazyLoad.load({
					name:'lithium',
					files: [
						'scripts/controllers/dashboard/cashier/auto-withdrawals/beta/list/list.js'
					]
				})
			}
		},
		ncyBreadcrumb: { skip: true },
		redirectTo: "dashboard.cashier.autowithdrawals.rulesets.list"
	});


	$stateProvider.state('dashboard.cashier.autowithdrawals.rulesets.list',{
		templateUrl:'scripts/controllers/dashboard/cashier/auto-withdrawals/beta/list/list.html',
		data: {
			pageTitle: "UI_NETWORK_ADMIN.PAGE.TITLE.AUTO_WITHDRAWAL",
		},
		url:'',
		controller:'AutoWithdrawalRulesetsBetaListController as controller',
		resolve: {
			fields: ["AutoWithdrawalRulesetRest", function(rest) {
				return rest.fields();
			}],
			operators: ["AutoWithdrawalRulesetRest", function(rest) {
				return rest.operators();
			}],
		},
		ncyBreadcrumb: {
			parent: 'dashboard.cashier.autowithdrawals.rulesets',
			label: 'cashier auto-withdrawal rulesets'
		}
	});

	$stateProvider.state('dashboard.cashier.autowithdrawals.rulesets.view', {
		templateUrl: "scripts/controllers/dashboard/cashier/auto-withdrawals/beta/view/view.html",
		url:'/{id}/view',
		controller: 'AutoWithdrawalRulesetViewBetaController as controller',
		resolve: {
			ruleset: ["AutoWithdrawalRulesetRest", "$stateParams", function(rest, $stateParams) {
				return rest.autoWithdrawalRulesetById($stateParams.id);
			}],
			loadMyFiles:function($ocLazyLoad) {
				return $ocLazyLoad.load({
					name:'lithium',
					files: [
						'scripts/controllers/dashboard/cashier/auto-withdrawals/beta/view/view.js'
					]
				})
			}
		},
		ncyBreadcrumb: { skip: true }
	});

	$stateProvider.state('dashboard.cashier.autowithdrawals.rulesets.edit', {
		templateUrl: "scripts/controllers/dashboard/cashier/auto-withdrawals/rulesets/edit/edit.html",
		url:'/{id}/edit',
		controller: 'AutoWithdrawalRulesetEditController as controller',
		resolve: {
			ruleset: ["AutoWithdrawalRulesetRest", "$stateParams", function(rest, $stateParams) {
				return rest.autoWithdrawalRulesetById($stateParams.id);
			}],
			fields: ["AutoWithdrawalRulesetRest", function(rest) {
				return rest.fields();
			}],
			operators: ["AutoWithdrawalRulesetRest", function(rest) {
				return rest.operators();
			}],
			loadMyFiles:function($ocLazyLoad) {
				return $ocLazyLoad.load({
					name:'lithium',
					files: [
						'scripts/controllers/dashboard/cashier/auto-withdrawals/rulesets/edit/edit.js'
					]
				})
			}
		},
		ncyBreadcrumb: { skip: true }
	});

	$stateProvider.state('dashboard.cashier.autowithdrawals.rulesets.add', {
		templateUrl:'scripts/controllers/dashboard/cashier/auto-withdrawals/rulesets/add/add.html',
		url:'/add',
		controller:'AutoWithdrawalRulesetAddController as controller',
		resolve: {
			fields: ["AutoWithdrawalRulesetRest", function(rest) {
				return rest.fields();
			}],
			operators: ["AutoWithdrawalRulesetRest", function(rest) {
				return rest.operators();
			}],
			loadMyFiles:function($ocLazyLoad) {
				return $ocLazyLoad.load({
					name:'lithium',
					files: [
						'scripts/controllers/dashboard/cashier/auto-withdrawals/rulesets/add/add.js'
					]
				})
			}
		},
		ncyBreadcrumb: {
			label: 'add an auto-withdrawal ruleset'
		}
	});

	$stateProvider.state('dashboard.restrictions',{
		template: "<div ui-view></div>",
		url:'/restrictions',
		ncyBreadcrumb: { skip: true },
		redirectTo: "dashboard.restrictions"
	});

	$stateProvider.state('dashboard.restrictions.dictionary',{
		template: "<div ui-view></div>",
		url:'/dictionary',
		resolve: {
			loadMyFiles:function($ocLazyLoad) {
				return $ocLazyLoad.load({
					name:'lithium',
					files: [
						'scripts/controllers/dashboard/restrictions/dictionary/list/list.js'
					]
				})
			}
		},
		ncyBreadcrumb: { skip: true },
		redirectTo: "dashboard.restrictions.dictionary.list"
	});

	$stateProvider.state('dashboard.restrictions.dictionary.list',{
		templateUrl:'scripts/controllers/dashboard/restrictions/dictionary/list/list.html',
		data: {
			pageTitle: "UI_NETWORK_ADMIN.PAGE.TITLE.RESTRICTIONS",
		},
		url:'',
		controller:'RestrictionsListController as controller',
		ncyBreadcrumb: {
			parent: 'dashboard.restrictions.dictionary',
			label: 'restrictions dictionary'
		}
	});

	$stateProvider.state('dashboard.restrictions.dictionary.view', {
		templateUrl: "scripts/controllers/dashboard/restrictions/dictionary/view/view.html",
		url:'/{id}/view',
		controller: 'RestrictionsViewController as controller',
		resolve: {
			set: ["RestrictionsRest", "$stateParams", function(rest, $stateParams) {
				return rest.findSetById($stateParams.id);
			}],
			loadMyFiles:function($ocLazyLoad) {
				return $ocLazyLoad.load({
					name:'lithium',
					files: [
						'scripts/controllers/dashboard/restrictions/dictionary/view/view.js'
					]
				})
			}
		},
		ncyBreadcrumb: { skip: true }
	});

	$stateProvider.state('dashboard.restrictions.dictionary.edit', {
		templateUrl: "scripts/controllers/dashboard/restrictions/dictionary/edit/edit.html",
		url:'/{id}/edit',
		controller: 'RestrictionsEditController as controller',
		resolve: {
			set: ["RestrictionsRest", "$stateParams", function(rest, $stateParams) {
				return rest.findSetById($stateParams.id);
			}],
			restrictionTypes: ["RestrictionsRest", function(rest) {
				return rest.restrictionTypes();
			}],
			loadMyFiles:function($ocLazyLoad) {
				return $ocLazyLoad.load({
					name:'lithium',
					files: [
						'scripts/controllers/dashboard/restrictions/dictionary/edit/edit.js'
					]
				})
			}
		},
		ncyBreadcrumb: { skip: true }
	});

	$stateProvider.state('dashboard.restrictions.dictionary.add', {
		templateUrl:'scripts/controllers/dashboard/restrictions/dictionary/add/add.html',
		url:'/add',
		controller:'RestrictionsAddController as controller',
		resolve: {
			restrictionTypes: ["RestrictionsRest", "$stateParams", function(rest, $stateParams) {
				return rest.restrictionTypes();
			}],
			outcomeActions: ["RestrictionsRest", "$stateParams", function(rest, $stateParams) {
				return rest.findAllOutcomeActions();
			}],
			loadMyFiles:function($ocLazyLoad) {
				return $ocLazyLoad.load({
					name:'lithium',
					files: [
						'scripts/controllers/dashboard/restrictions/dictionary/add/add.js'
					]
				})
			}
		},
		ncyBreadcrumb: {
			label: 'add a restriction set'
		}
	});

	$stateProvider.state('dashboard.restrictions.autorestrictions',{
		template: "<div ui-view></div>",
		url:'/autorestrictions',
		ncyBreadcrumb: { skip: true },
		redirectTo: "dashboard.restrictions.autorestrictions.rulesets"
	});

	$stateProvider.state('dashboard.restrictions.autorestrictions.rulesets',{
		template: "<div ui-view></div>",
		url:'/rulesets',
		resolve: {
			loadMyFiles:function($ocLazyLoad) {
				return $ocLazyLoad.load({
					name:'lithium',
					files: [
						'scripts/controllers/dashboard/restrictions/auto-restrictions/rulesets/list/list.js'
					]
				})
			}
		},
		ncyBreadcrumb: { skip: true },
		redirectTo: "dashboard.restrictions.autorestrictions.rulesets.list"
	});

	$stateProvider.state('dashboard.restrictions.autorestrictions.rulesets.list',{
		templateUrl:'scripts/controllers/dashboard/restrictions/auto-restrictions/rulesets/list/list.html',
		data: {
			pageTitle: "UI_NETWORK_ADMIN.PAGE.TITLE.AUTO_RESTRICTIONS",
		},
		url:'',
		controller:'AutoRestrictionRulesetsListController as controller',
		ncyBreadcrumb: {
			parent: 'dashboard.restrictions.autorestrictions.rulesets',
			label: 'auto-restrictions rulesets'
		}
	});

	$stateProvider.state('dashboard.restrictions.autorestrictions.rulesets.view', {
		templateUrl: "scripts/controllers/dashboard/restrictions/auto-restrictions/rulesets/view/view.html",
		url:'/{id}/view',
		controller: 'AutoRestrictionRulesetViewController as controller',
		resolve: {
			ruleset: ["AutoRestrictionRulesetRest", "$stateParams", function(rest, $stateParams) {
				return rest.autoRestrictionRulesetById($stateParams.id);
			}],
			fields: ["AutoRestrictionRulesetRest", function(rest) {
				return rest.fields();
			}],
			operators: ["AutoRestrictionRulesetRest", function(rest) {
				return rest.operators();
			}],
			outcomes: ["AutoRestrictionRulesetRest", function(rest) {
				return rest.outcomes();
			}],
			events: ["AutoRestrictionRulesetRest", function(rest) {
				return rest.restrictionEvents();
			}],
			loadMyFiles:function($ocLazyLoad) {
				return $ocLazyLoad.load({
					name:'lithium',
					files: [
						'scripts/controllers/dashboard/restrictions/auto-restrictions/rulesets/view/view.js'
					]
				})
			}
		},
		ncyBreadcrumb: { skip: true }
	});

	$stateProvider.state('dashboard.restrictions.autorestrictions.rulesets.edit', {
		templateUrl: "scripts/controllers/dashboard/restrictions/auto-restrictions/rulesets/edit/edit.html",
		url:'/{id}/edit',
		controller: 'AutoRestrictionRulesetEditController as controller',
		resolve: {
			ruleset: ["AutoRestrictionRulesetRest", "$stateParams", function(rest, $stateParams) {
				return rest.autoRestrictionRulesetById($stateParams.id);
			}],
			fields: ["AutoRestrictionRulesetRest", function(rest) {
				return rest.fields();
			}],
			operators: ["AutoRestrictionRulesetRest", function(rest) {
				return rest.operators();
			}],
			outcomes: ["AutoRestrictionRulesetRest", function(rest) {
				return rest.outcomes();
			}],
			events: ["AutoRestrictionRulesetRest", function(rest) {
				return rest.restrictionEvents();
			}],
			loadMyFiles:function($ocLazyLoad) {
				return $ocLazyLoad.load({
					name:'lithium',
					files: [
						'scripts/controllers/dashboard/restrictions/auto-restrictions/rulesets/edit/edit.js'
					]
				})
			}
		},
		ncyBreadcrumb: { skip: true }
	});

	$stateProvider.state('dashboard.restrictions.autorestrictions.rulesets.add', {
		templateUrl:'scripts/controllers/dashboard/restrictions/auto-restrictions/rulesets/add/add.html',
		url:'/add',
		controller:'AutoRestrictionRulesetAddController as controller',
		resolve: {
			fields: ["AutoRestrictionRulesetRest", function(rest) {
				return rest.fields();
			}],
			operators: ["AutoRestrictionRulesetRest", function(rest) {
				return rest.operators();
			}],
			outcomes: ["AutoRestrictionRulesetRest", function(rest) {
				return rest.outcomes();
			}],
			events: ["AutoRestrictionRulesetRest", function(rest) {
				return rest.restrictionEvents();
			}],
			loadMyFiles:function($ocLazyLoad) {
				return $ocLazyLoad.load({
					name:'lithium',
					files: [
						'scripts/controllers/dashboard/restrictions/auto-restrictions/rulesets/add/add.js'
					]
				})
			}
		},
		ncyBreadcrumb: {
			label: 'add an auto-restriction ruleset'
		}
	});

	$stateProvider.state('dashboard.brandConfig', {
		templateUrl:'scripts/controllers/dashboard/brandConfig/config/config.html',
		data: {
			pageTitle: "UI_NETWORK_ADMIN.PAGE.TITLE.BRAND",
		},
		controller:'BrandConfigController as controller',
		url:'/brand/config',
		resolve: {
			loadMyFiles:function($ocLazyLoad) {
				return $ocLazyLoad.load({
					name:'lithium',
					files: [
						'scripts/controllers/dashboard/brandConfig/config/config.js'
					]
				})
			}
		},
		ncyBreadcrumb: { skip: true }
	});

	$stateProvider.state('dashboard.brandConfig.limits',{
		templateUrl:'scripts/controllers/dashboard/brandConfig/losslimits/losslimits.html',
		url:'/limits',
		controller:'lossLimitsController as controller',
		params: {
			domainName: ['$stateParams', function($stateParams) {
				return $stateParams.domainName;
			}],
			domain: ["$stateParams", function($stateParams) {
				return $stateParams.domain;
			}]
		},
		resolve: {
			loadMyFiles:function($ocLazyLoad) {
				return $ocLazyLoad.load({
					name:'lithium',
					files:['scripts/controllers/dashboard/brandConfig/losslimits/losslimits.js']
				})
			}
		},
		ncyBreadcrumb: { label: "domain limits" }
	});

	$stateProvider.state('dashboard.brandConfig.errormessages',{
		templateUrl:'scripts/controllers/dashboard/brandConfig/errormessages/errormessages.html',
		url:'/errormessages',
		controller:'ErrorMessagesController as controller',
		params: {
			domain: ["$stateParams", function($stateParams) {
				return $stateParams.domain;
			}]
		},
		resolve: {
			loadMyFiles:function($ocLazyLoad) {
				return $ocLazyLoad.load({
					name:'lithium',
					files:['scripts/controllers/dashboard/brandConfig/errormessages/errormessages.js']
				})
			},
			domain: ['$stateParams', function($stateParams) {
				return $stateParams.domain;
			}]
		},
		ncyBreadcrumb: { label: "domain errors" }
	});

	$stateProvider.state('dashboard.brandConfig.doctypes',{
		templateUrl:'scripts/controllers/dashboard/brandConfig/doctypes/doctypes.html',
		url:'/doc-types',
		controller:'DocumentTypes as controller',
		params: {
			domainName: null,
			domain: null
		},
		resolve: {
			loadMyFiles:function($ocLazyLoad) {
				return $ocLazyLoad.load({
					name:'lithium',
					files:['scripts/controllers/dashboard/brandConfig/doctypes/doctypes.js']
				})
			},
			domainName: ['$stateParams', function($stateParams) {
				return $stateParams.domainName;
			}],
			domain: ['$stateParams', function($stateParams) {
				return $stateParams.domain;
			}]
		},
		ncyBreadcrumb: { label: "document types" }
	});

	$stateProvider.state('dashboard.casino', {
		template: '<div ui-view></div>',
		url:'/casino',
		controller: function($scope) {
			$scope.setDescription = function(description) {
				$scope.description = description;
			}
		},
		ncyBreadcrumb: { skip: true }
	});

	$stateProvider.state('dashboard.casino.lobbies', {
		templateUrl:'scripts/controllers/dashboard/casino/lobbies/domain-select.html',
		controller:'CasinoLobbiesDomainSelectController as controller',
		url:'/lobbies',
		resolve: {
			loadMyFiles:function($ocLazyLoad) {
				return $ocLazyLoad.load({
					name:'lithium',
					files: [
						'scripts/controllers/dashboard/casino/lobbies/domain-select.js'
					]
				})
			}
		},
		redirectTo: "dashboard.casino.lobbies.list"
	});

	$stateProvider.state('dashboard.casino.lobbies.list', {
		templateUrl:'scripts/controllers/dashboard/casino/lobbies/list/list.html',
		data: {
			pageTitle: "UI_NETWORK_ADMIN.PAGE.TITLE.CASINO",
		},
		url:'/{domainName}',
		controller:'CasinoLobbiesListController as controller',
		params: {
			domainName: ['$stateParams', function($stateParams) {
				return $stateParams.domainName;
			}]
		},
		resolve: {
			domainName: ['$stateParams', function($stateParams) {
				return $stateParams.domainName;
			}],
			loadMyFiles:function($ocLazyLoad) {
				return $ocLazyLoad.load({
					name:'lithium',
					files: [
						'scripts/controllers/dashboard/casino/lobbies/list/list.js'
					]
				})
			}
		}
	});

	$stateProvider.state('dashboard.casino.lobbies.add', {
		templateUrl:'scripts/controllers/dashboard/casino/lobbies/add/add.html',
		url:'/{domainName}/add',
		controller:'CasinoLobbiesAddController as controller',
		resolve: {
			domainName: ['$stateParams', function($stateParams) {
				return $stateParams.domainName;
			}],
			loadMyFiles:function($ocLazyLoad) {
				return $ocLazyLoad.load({
					name:'lithium',
					files: [
						'scripts/controllers/dashboard/casino/lobbies/add/add.js'
					]
				})
			}
		},
		ncyBreadcrumb: {
			label: 'add a casino lobby configuration'
		}
	});

	$stateProvider.state('dashboard.casino.lobbies.lobby', {
		templateUrl: 'scripts/controllers/dashboard/casino/lobbies/lobby/lobby.html',
		url:'/{domainName}/lobby/{lobbyId}',
		controller: 'CasinoLobbyController as controller',
		resolve: {
			lobby: ["CasinoCMSRest", "$stateParams", function(rest, $stateParams) {
				return rest.find($stateParams.domainName, $stateParams.lobbyId);
			}],
			loadMyFiles:function($ocLazyLoad) {
				return $ocLazyLoad.load({
					name:'lithium',
					files: [
						'scripts/controllers/dashboard/casino/lobbies/lobby/lobby.js'
					]
				})
			}
		},
		redirectTo: 'dashboard.casino.lobbies.lobby.view'
	});

	$stateProvider.state('dashboard.casino.lobbies.lobby.revisions', {
		templateUrl:'scripts/controllers/dashboard/casino/lobbies/lobby/view/revisions/revisions.html',
		url:'/revisions',
		controller:'CasinoLobbyRevisionsController as controller',
		resolve: {
			loadMyFiles:function($ocLazyLoad) {
				return $ocLazyLoad.load({
					name:'lithium',
					files: [
						'scripts/controllers/dashboard/casino/lobbies/lobby/view/revisions/revisions.js'
					]
				})
			}
		}
	});

	$stateProvider.state('dashboard.casino.lobbies.lobby.view', {
		templateUrl:'scripts/controllers/dashboard/casino/lobbies/lobby/view/view.html',
		url:'/{lobbyRevisionId}/view',
		controller:'CasinoLobbyViewController as controller',
		resolve: {
			lobbyRevision: ["CasinoCMSRest", "$stateParams", function(rest, $stateParams) {
				return rest.findRevision($stateParams.domainName, $stateParams.lobbyId, $stateParams.lobbyRevisionId);
			}],
			loadMyFiles:function($ocLazyLoad) {
				return $ocLazyLoad.load({
					name:'lithium',
					files: [
						'scripts/controllers/dashboard/casino/lobbies/lobby/view/view.js'
					]
				})
			}
		}
	});

	$stateProvider.state('dashboard.casino.lobbies.edit', {
		templateUrl:'scripts/controllers/dashboard/casino/lobbies/lobby/edit/edit.html',
		url:'/{domainName}/{lobbyId}/edit',
		controller:'CasinoLobbyEditController as controller',
		resolve: {
			lobby: ['CasinoCMSRest', '$stateParams', function(rest, $stateParams) {
				return rest.modifyLobby($stateParams.domainName, $stateParams.lobbyId);
			}],
			loadMyFiles:function($ocLazyLoad) {
				return $ocLazyLoad.load({
					name:'lithium',
					files: [
						'scripts/controllers/dashboard/casino/lobbies/lobby/edit/edit.js'
					]
				})
			}
		}
	});

	$stateProvider.state('dashboard.casino.game-suppliers', {
		templateUrl:'scripts/controllers/dashboard/casino/game-suppliers/domain-select.html',
		data: {
			pageTitle: "UI_NETWORK_ADMIN.GAME_SUPPLIERS.HEADER.TITLE",
		},
		controller:'GameSuppliersDomainSelectController as controller',
		url:'/game-suppliers',
		resolve: {
			loadMyFiles:function($ocLazyLoad) {
				return $ocLazyLoad.load({
					name:'lithium',
					files: [
						'scripts/controllers/dashboard/casino/game-suppliers/domain-select.js'
					]
				})
			}
		},
		redirectTo: "dashboard.casino.game-suppliers.list"
	});

	$stateProvider.state('dashboard.casino.game-suppliers.list', {
		templateUrl:'scripts/controllers/dashboard/casino/game-suppliers/list/list.html',
		data: {
			pageTitle: "UI_NETWORK_ADMIN.GAME_SUPPLIERS.HEADER.TITLE",
		},
		url:'/{domainName}',
		controller:'GameSuppliersListController as controller',
		params: {
			domainName: ['$stateParams', function($stateParams) {
				return $stateParams.domainName;
			}]
		},
		resolve: {
			domainName: ['$stateParams', function($stateParams) {
				return $stateParams.domainName;
			}],
			loadMyFiles:function($ocLazyLoad) {
				return $ocLazyLoad.load({
					name:'lithium',
					files: [
						'scripts/controllers/dashboard/casino/game-suppliers/list/list.js'
					]
				})
			}
		}
	});

	$stateProvider.state('dashboard.casino.game-suppliers.add', {
		templateUrl:'scripts/controllers/dashboard/casino/game-suppliers/add/add.html',
		url:'/{domainName}/add',
		controller:'GameSuppliersAddController as controller',
		resolve: {
			domainName: ['$stateParams', function($stateParams) {
				return $stateParams.domainName;
			}],
			loadMyFiles:function($ocLazyLoad) {
				return $ocLazyLoad.load({
					name:'lithium',
					files: [
						'scripts/controllers/dashboard/casino/game-suppliers/add/add.js'
					]
				})
			}
		},
		ncyBreadcrumb: {
			label: 'add a game supplier'
		}
	});

	$stateProvider.state('dashboard.casino.game-suppliers.view', {
		templateUrl:'scripts/controllers/dashboard/casino/game-suppliers/view/view.html',
		url:'/{domainName}/{id}/view',
		controller:'GameSuppliersViewController as controller',
		resolve: {
			gameSupplier: ["GameSuppliersRest", "$stateParams", function(rest, $stateParams) {
				return rest.get($stateParams.domainName, $stateParams.id);
			}],
			loadMyFiles:function($ocLazyLoad) {
				return $ocLazyLoad.load({
					name:'lithium',
					files: [
						'scripts/controllers/dashboard/casino/game-suppliers/view/view.js'
					]
				})
			}
		}
	});

	$stateProvider.state('dashboard.casino.game-suppliers.edit', {
		templateUrl:'scripts/controllers/dashboard/casino/game-suppliers/edit/edit.html',
		url:'/{domainName}/{id}/edit',
		controller:'GameSuppliersEditController as controller',
		resolve: {
			gameSupplier: ['GameSuppliersRest', '$stateParams', function(rest, $stateParams) {
				return rest.get($stateParams.domainName, $stateParams.id);
			}],
			loadMyFiles:function($ocLazyLoad) {
				return $ocLazyLoad.load({
					name:'lithium',
					files: [
						'scripts/controllers/dashboard/casino/game-suppliers/edit/edit.js'
					]
				})
			}
		}
	});

	$stateProvider.state('dashboard.casino.progressive-balances', {
		templateUrl:'scripts/controllers/dashboard/casino/progressive-balances/domain-select.html',
		controller:'ProgressiveBalancesDomainSelectController as controller',
		url:'/progressive-balances',
		resolve: {
			loadMyFiles:function($ocLazyLoad) {
				return $ocLazyLoad.load({
					name:'lithium',
					files: [
						'scripts/controllers/dashboard/casino/progressive-balances/domain-select.js'
					]
				})
			}
		},
		redirectTo: "dashboard.casino.progressive-balances.list"
	});

	$stateProvider.state('dashboard.casino.progressive-balances.list', {
		templateUrl:'scripts/controllers/dashboard/casino/progressive-balances/list/list.html',
		data: {
			pageTitle: "UI_NETWORK_ADMIN.CASINO.PROGRESSIVE_BALANCES.HEADER.TITLE",
		},
		url:'/{domainName}',
		controller:'ProgressiveBalancesListController as controller',
		params: {
			domainName: ['$stateParams', function($stateParams) {
				return $stateParams.domainName;
			}]
		},
		resolve: {
			domainName: ['$stateParams', function($stateParams) {
				return $stateParams.domainName;
			}],
			loadMyFiles:function($ocLazyLoad) {
				return $ocLazyLoad.load({
					name:'lithium',
					files: [
						'scripts/controllers/dashboard/casino/progressive-balances/list/list.js'
					]
				})
			}
		}
	});

	$stateProvider.state('dashboard.casino.cms-images', {
		templateUrl:'scripts/controllers/dashboard/casino/cms-images/index.html',
		controller:'CasinoCmsImagesController as controller',
		url:'/casino-cms-images',
		resolve: {
			loadMyFiles:function($ocLazyLoad) {
				return $ocLazyLoad.load({
					name:'lithium',
					files: [
						'scripts/controllers/dashboard/casino/cms-images/index.js'
					]
				})
			}
		}
	});

	$stateProvider.state('dashboard.casino.game-types', {
		templateUrl:'scripts/controllers/dashboard/casino/game-types/domain-select.html',
		data: {
			pageTitle: "UI_NETWORK_ADMIN.GAME_TYPES.HEADER.TITLE",
		},
		controller:'GameTypesDomainSelectController as controller',
		url:'/game-types',
		resolve: {
			loadMyFiles:function($ocLazyLoad) {
				return $ocLazyLoad.load({
					name:'lithium',
					files: [
						'scripts/controllers/dashboard/casino/game-types/domain-select.js'
					]
				})
			}
		},
		redirectTo: "dashboard.casino.game-types.list"
	});

	$stateProvider.state('dashboard.casino.game-types.list', {
		templateUrl:'scripts/controllers/dashboard/casino/game-types/list/list.html',
		data: {
			pageTitle: "UI_NETWORK_ADMIN.GAME_TYPES.HEADER.TITLE",
		},
		url:'/{domainName}',
		controller:'GameTypesListController as controller',
		params: {
			domainName: ['$stateParams', function($stateParams) {
				return $stateParams.domainName;
			}]
		},
		resolve: {
			domainName: ['$stateParams', function($stateParams) {
				return $stateParams.domainName;
			}],
			loadMyFiles:function($ocLazyLoad) {
				return $ocLazyLoad.load({
					name:'lithium',
					files: [
						'scripts/controllers/dashboard/casino/game-types/list/list.js'
					]
				})
			}
		}
	});

	$stateProvider.state('dashboard.casino.game-types.add', {
		templateUrl:'scripts/controllers/dashboard/casino/game-types/add/add.html',
		url:'/{domainName}/add',
		controller:'GameTypesAddController as controller',
		resolve: {
			domainName: ['$stateParams', function($stateParams) {
				return $stateParams.domainName;
			}],
			loadMyFiles:function($ocLazyLoad) {
				return $ocLazyLoad.load({
					name:'lithium',
					files: [
						'scripts/controllers/dashboard/casino/game-types/add/add.js'
					]
				})
			}
		},
		ncyBreadcrumb: {
			label: 'add a game type'
		}
	});

	$stateProvider.state('dashboard.casino.game-types.view', {
		templateUrl:'scripts/controllers/dashboard/casino/game-types/view/view.html',
		url:'/{domainName}/{id}/view',
		controller:'GameTypesViewController as controller',
		resolve: {
			gameType: ["GameTypesRest", "$stateParams", function(rest, $stateParams) {
				return rest.get($stateParams.domainName, $stateParams.id);
			}],
			loadMyFiles:function($ocLazyLoad) {
				return $ocLazyLoad.load({
					name:'lithium',
					files: [
						'scripts/controllers/dashboard/casino/game-types/view/view.js'
					]
				})
			}
		}
	});

	$stateProvider.state('dashboard.casino.game-types.edit', {
		templateUrl:'scripts/controllers/dashboard/casino/game-types/edit/edit.html',
		url:'/{domainName}/{id}/edit',
		controller:'GameTypesEditController as controller',
		resolve: {
			gameType: ['GameTypesRest', '$stateParams', function(rest, $stateParams) {
				return rest.get($stateParams.domainName, $stateParams.id);
			}],
			loadMyFiles:function($ocLazyLoad) {
				return $ocLazyLoad.load({
					name:'lithium',
					files: [
						'scripts/controllers/dashboard/casino/game-types/edit/edit.js'
					]
				})
			}
		}
	});

	$stateProvider.state('dashboard.casino.game-studio', {
		templateUrl:'scripts/controllers/dashboard/casino/game-studio/domain-select.html',
		data: {
			pageTitle: "UI_NETWORK_ADMIN.GAME_STUDIO.HEADER.TITLE",
		},
		controller:'GameStudioDomainSelectController as controller',
		url:'/game-studio',
		resolve: {
			loadMyFiles:function($ocLazyLoad) {
				return $ocLazyLoad.load({
					name:'lithium',
					files: [
						'scripts/controllers/dashboard/casino/game-studio/domain-select.js'
					]
				})
			}
		},
		redirectTo: "dashboard.casino.game-studio.list"
	});

	$stateProvider.state('dashboard.casino.game-studio.list', {
		templateUrl:'scripts/controllers/dashboard/casino/game-studio/list/list.html',
		data: {
			pageTitle: "UI_NETWORK_ADMIN.GAME_STUDIO.HEADER.TITLE",
		},
		url:'/{domainName}',
		controller:'GameStudioListController as controller',
		params: {
			domainName: ['$stateParams', function($stateParams) {
				return $stateParams.domainName;
			}]
		},
		resolve: {
			domainName: ['$stateParams', function($stateParams) {
				return $stateParams.domainName;
			}],
			loadMyFiles:function($ocLazyLoad) {
				return $ocLazyLoad.load({
					name:'lithium',
					files: [
						'scripts/controllers/dashboard/casino/game-studio/list/list.js'
					]
				})
			}
		}
	});

	$stateProvider.state('dashboard.casino.game-studio.add', {
		templateUrl:'scripts/controllers/dashboard/casino/game-studio/add/add.html',
		url:'/{domainName}/add',
		controller:'GameStudioAddController as controller',
		resolve: {
			domainName: ['$stateParams', function($stateParams) {
				return $stateParams.domainName;
			}],
			loadMyFiles:function($ocLazyLoad) {
				return $ocLazyLoad.load({
					name:'lithium',
					files: [
						'scripts/controllers/dashboard/casino/game-studio/add/add.js'
					]
				})
			}
		},
		ncyBreadcrumb: {
			label: 'add a game studio'
		}
	});

	$stateProvider.state('dashboard.casino.game-studio.view', {
		templateUrl:'scripts/controllers/dashboard/casino/game-studio/view/view.html',
		url:'/{domainName}/{id}/view',
		controller:'GameStudioViewController as controller',
		resolve: {
			gameStudio: ["GameStudioRest", "$stateParams", function(rest, $stateParams) {
				return rest.findGameStudio($stateParams.domainName, $stateParams.id);
			}],
			loadMyFiles:function($ocLazyLoad) {
				return $ocLazyLoad.load({
					name:'lithium',
					files: [
						'scripts/controllers/dashboard/casino/game-studio/view/view.js'
					]
				})
			}
		}
	});

	$stateProvider.state('dashboard.casino.game-studio.edit', {
		templateUrl:'scripts/controllers/dashboard/casino/game-studio/edit/edit.html',
		url:'/{domainName}/{id}/edit',
		controller:'GameStudioEditController as controller',
		resolve: {
			gameStudio: ['GameStudioRest', '$stateParams', function(rest, $stateParams) {
				return rest.findGameStudio($stateParams.domainName, $stateParams.id);
			}],
			loadMyFiles:function($ocLazyLoad) {
				return $ocLazyLoad.load({
					name:'lithium',
					files: [
						'scripts/controllers/dashboard/casino/game-studio/edit/edit.js'
					]
				})
			}
		}
	});

	$stateProvider.state('dashboard.casino.progressive-balances.configuration', {
		templateUrl:'scripts/controllers/dashboard/casino/progressive-balances/configuration/configuration.html',
		data: {
			pageTitle: "UI_NETWORK_ADMIN.CASINO.PROGRESSIVE_BALANCES.HEADER.TITLE",
		},
		url:'/{domainName}',
		controller:'ProgressiveJackpotFeedsController as controller',
		params: {
			domainName: ['$stateParams', function($stateParams) {
				return $stateParams.domainName;
			}]
		},
		resolve: {
			domainName: ['$stateParams', function($stateParams) {
				return $stateParams.domainName;
			}],
			loadMyFiles:function($ocLazyLoad) {
				return $ocLazyLoad.load({
					name:'lithium',
					files: [
						'scripts/controllers/dashboard/casino/progressive-balances/configuration/configuration.js'
					]
				})
			}
		}
	});

}
