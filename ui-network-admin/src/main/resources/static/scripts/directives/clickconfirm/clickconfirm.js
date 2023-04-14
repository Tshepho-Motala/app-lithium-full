'use strict';

angular.module('lithium').directive('clickConfirm', function() {
	return {
		link: function(scope, element, attributes) {
			var message = attributes.clickConfirm;
			var action = attributes.clickConfirmed;
			element.bind('click', function(event) {
				if (window.confirm(message)) {
					scope.$eval(action)
				}
			});
		}
	};
});