'use strict'

angular.module('lithium').controller('PlayerNotificationsViewController', ['$log', '$state', 'inbox',
	function($log, $state, inbox) {
		var controller = this;
		controller.model = inbox;
	}
]);