'use strict';

angular.module('lithium')
	.controller('PlayerSummaryController', ["domain", "user", "userData", "domainCurrencies", "domainSettings", "playerLinks", "$translate", "$state", "$rootScope", "notify", "$http", "errors", "$q", "UserRest", "$security",
	function(domain, user, userData, domainCurrencies, domainSettings, playerLinks, $translate, $state, $rootScope, notify, $http, errors, $q, UserRest, $security) {
		var controller = this;

		//Note: do not change the reference of the controller.user object, to keep two ways banding with player page
		controller.user = user;
		controller.domain = domain;
		controller.userData = userData;

		console.debug('controller.userData', controller.userData);
		
		controller.externalBonusNucleus = {
			box: "default",
			domainName: controller.domain,
			ownerGuid: user.guid,
			provider: "service-casino-provider-nucleus",
			title: "External Bonus (Nucleus)"
		}
		
		controller.externalBonusBetsoft = {
			box: "default",
			domainName: controller.domain,
			ownerGuid: user.guid,
			provider: "service-casino-provider-betsoft",
			title: "External Bonus (Betsoft)"
		}
		
		controller.balances = [];
		
		controller.activeBonus = {
			box: "default",
			domainName: controller.domain,
			ownerGuid: user.guid,
			user: user,
			authorGuid: $rootScope.principal.guid,
			accountCode: "PLAYER_BALANCE_CASINO_BONUS",
			accountType: "PLAYER_BALANCE",
			currency: controller.userData.domain.currency,
			symbol: controller.userData.domain.currencySymbol,
			title: "Active Bonus"
		}

		controller.changelogs = controller.userData.changelogs;

		angular.forEach(controller.userData.playerBalance, function(pb) {
			controller.balances.push({
				id: pb.id,
				box: "info",
				currency: pb.currency.code,
				symbol: pb.currency.symbol,
				domainName: user.domain.name,
				accountCode: pb.accountType,
				accountType: pb.accountType,
				ownerGuid: user.guid,
				authorGuid: $rootScope.principal.guid,
				title: "Player Balance ("+ pb.currency.code +")",
				isDefault: pb.currency.isDefault,
				changelogs: controller.changelogs
			})
		});

		controller.residentialAddress = {
			box: "info",
			title: "Residential Address",
			type: 'residentialAddress',
			userId: user.id,
			domainName: controller.domain
		}
		controller.postalAddress = {
			box: "info",
			title: "Postal Address",
			type: 'postalAddress',
			userId: user.id,
			domainName: controller.domain
		}
		controller.personalinfo = {
			box: "info",
			title: "Personal Info",
			userId: user.id,
			profile: false,
			domainName: controller.domain,
			domainSettings: domainSettings
		}

		controller.playerAttributes = {
			box: "info",
			title: "Player Attributes",
			userId: user.id,
			profile: false,
			domainName: controller.domain,
			domainSettings: domainSettings,
			lossLimitVisibility: userData.limits.lossLimitVisibility
		}

		controller.password = {
			box: "success",
			title: "Change Password",
			userId: user.id,
			domainName: controller.domain
		}
		controller.status = {
			box: "default",
			title: "Status",
			userId: user.id,
			domainName: controller.domain,
			domainSettings: domainSettings
		}
		controller.verificationstatus = {
			box: "default",
			title: "Verification Status",
			userId: user.id,
			domainName: controller.domain,
			domainSettings: domainSettings
		}
		controller.biometricsStatus = {
			box: "default",
			title: "Biometrics Status",
			userId: user.id,
			domainName: controller.domain,
			domainSettings: domainSettings
		}
		controller.usertag = {
			box: "default",
			title: "Tags",
			userId: user.id,
			domainName: controller.domain
		}
		
		controller.userData.comment = {
			box: "info",
			title: "Comments",
			domainName: controller.domain,
			entityId: user.id,
			restService: UserRest,
			changelogs: controller.changelogs,
			showLastCommentRole:$security.hasRole("PLAYER_LAST_COMMENT_LIST")||$security.hasAdminRole()
		}
		
		controller.promooptout = {
			box: "info",
			title: "UI_NETWORK_ADMIN.PLAYER.PROMOOPTOUT.MARKETING_PREFERENCES",
			playerLinkData: playerLinks,
			domainSettings: domainSettings
		}
		
		controller.referral = {
			box: "success",
			title: "Referral"
		}

		controller.affiliate = {
			box: "success",
			title: "Affiliate References"
		}

		controller.playerLinks = {
			box: "info",
			title: "Player Links"
		}
	}
]);
