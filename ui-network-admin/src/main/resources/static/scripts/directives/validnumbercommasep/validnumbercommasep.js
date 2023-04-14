'use strict';

angular.module('lithium').directive('validNumberCommaSep', function() {
	return {
		require: '?ngModel',
		link: function(scope, element, attrs, ngModelCtrl) {
			if (!ngModelCtrl) {
				return;
			}
			
			ngModelCtrl.$parsers.push(function(val) {
				 var clean = val.replace(/[^\d,]/gm,'');
				 clean = clean.replace(/^,+/m, '');
				 clean = clean.replace(/,{2,}/g, ',');
				 if (val !== clean) {
					 ngModelCtrl.$setViewValue(clean);
					 ngModelCtrl.$render();
				 }
				 return clean;
			});
			
			element.bind('keypress', function(event) {
				if (event.keyCode === 32) {
					event.preventDefault();
				}
			});
		}
	};
});