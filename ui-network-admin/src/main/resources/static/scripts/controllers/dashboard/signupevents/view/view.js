'use strict';

angular.module('lithium').controller('SignupEventsView', ['signupEvent',
	function(signupEvent) {
		var controller = this;
		controller.model = signupEvent;
	}]
);