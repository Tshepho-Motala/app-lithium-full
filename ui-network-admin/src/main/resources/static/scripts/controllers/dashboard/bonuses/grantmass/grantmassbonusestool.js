'use strict';

angular.module('lithium').controller('GrantMassBonusesToolController', ['domainName', 'rest-casino', '$state', 'currencySymbol', 'errors','$translate',
function(domainName, casinoRest, $state, currencySymbol, errors, $translate) {

	var controller = this;
	controller.data = {};

	controller.tabs = [
		{ name: "cashbonuses", title: "UI_NETWORK_ADMIN.ACTIVE_BONUSES.GLOBAL.FIELDS.CASH_BONUSES", roles: "MASS_BONUS_ALLOCATION_VIEW", tclass: "enabled", uploadType:"BONUS_CASH"},
		{ name: "freechips", title: "UI_NETWORK_ADMIN.ACTIVE_BONUSES.GLOBAL.FIELDS.CASINO_CHIPS", roles: "MASS_BONUS_ALLOCATION_VIEW" , tclass: "enabled", uploadType:"BONUS_CASINOCHIP"},
		{ name: "freespins", title: "UI_NETWORK_ADMIN.ACTIVE_BONUSES.GLOBAL.FIELDS.FREE_SPINS", roles: "MASS_BONUS_ALLOCATION_VIEW", tclass: "enabled", uploadType:"BONUS_FREESPIN"},
		{ name: "instantrewards", title: "UI_NETWORK_ADMIN.ACTIVE_BONUSES.GLOBAL.FIELDS.INSTANT_REWARDS", roles: "MASS_BONUS_ALLOCATION_VIEW", tclass: "enabled", uploadType:"BONUS_INSTANT"},
		{ name: "instantrewardfreespins", title: "UI_NETWORK_ADMIN.ACTIVE_BONUSES.GLOBAL.FIELDS.INSTANT_REWARD_FREESPINS", roles: "MASS_BONUS_ALLOCATION_VIEW", tclass: "enabled", uploadType:"BONUS_INSTANT"},
		{ name: "wagering", title: "UI_NETWORK_ADMIN.ACTIVE_BONUSES.GLOBAL.FIELDS.WAGERING_BONUSES", roles: "MASS_BONUS_ALLOCATION_VIEW" , tclass: "disabled", uploadType:""}
	];

	controller.setTab = function(tab) {
		if (tab.tclass !== 'disabled') { //Ensures that you are not able to click on the disabled tabs
			controller.tab = tab;
			controller.data.tabName = tab.uploadType;
		}
	}

	casinoRest.getActiveCashBonusTypes(domainName).then(function (response) {
		let selectedTab = (controller.tab !== null && controller.tab !== undefined ) ? controller.tab : controller.tabs[0];
		let bonusCodes = (response !== undefined && response !== null) ? response.plain() : [];
		let obj = [];
		for (let i = 0; i < bonusCodes.length; i++) {
			let name = bonusCodes[i].bonusName + ' (' + bonusCodes[i].bonusCode + ')';
			obj[i] = {name: name, value: bonusCodes[i].bonusCode}
		}
		controller.data = {bonusCodes: obj, domainName: domainName, currencySymbol: currencySymbol, tabName: selectedTab.uploadType};
		controller.setTab(controller.tabs[0]); //On init, show cash bonus tab
	}).catch(function (error) {
		errors.catch("", false)(error)
	});
}]);
