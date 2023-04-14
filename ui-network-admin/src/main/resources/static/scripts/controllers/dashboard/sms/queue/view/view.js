'use strict'

angular.module('lithium').controller('SMSQueueViewController', ['$log', '$state', 'sms',
	function($log, $state, sms) {
		var controller = this;
		controller.sms = sms;
		controller.state = $state.current.name;
	}
]);