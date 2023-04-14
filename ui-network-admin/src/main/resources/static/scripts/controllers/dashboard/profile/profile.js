'use strict';

angular.module('lithium')
	.controller('ProfileController', ["notify", "user",
	function(notify, user) {
		var controller = this;
		controller.user = user;
		
		controller.residentialAddress = {
			box: "info",
			title: "Residential Address",
			type: 'residentialAddress',
			profile: true
		}
		controller.postalAddress = {
			box: "info",
			title: "Postal Address",
			type: 'postalAddress',
			profile: true
		}
		controller.password = {
			box: "success",
			title: "Change Password",
			profile: true
		}
		controller.personalinfo = {
			box: "info",
			title: "Personal Info",
			profile: true,
			userId: user.id,
			domainName: user.domain.name,
			domainSettings: []
		}
	}
]);