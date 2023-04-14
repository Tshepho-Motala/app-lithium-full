'use strict'

angular.module('lithium').controller('PlayerMailHistoryViewController', ['$log', '$state', 'mail',
	function($log, $state, mail) {
		var controller = this;
		controller.mail = mail;
		controller.state = $state.current.name;
	}
]);