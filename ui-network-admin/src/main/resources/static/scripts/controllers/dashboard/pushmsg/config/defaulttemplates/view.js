'use strict';

angular.module('lithium')
	.controller('DefaultSMSTemplate', ["template", "domainName", "notify", "$translate", "$log", "$scope", "$state", "$q", "errors",
	function(template, domainName, notify, $translate, $log, $scope, $state, $q, errors) {
		var controller = this;
		controller.model = template;
	}
]);