'use strict';

angular.module('lithium')
	.directive('litHeader', ['$translate', '$log',
	function($translate, $log) {
		return {
//			template: 'litHeader - {{title}} - litHeader',
			templateUrl:'scripts/directives/litHeader/litHeader.html',
			scope: {},
			transclude: true,
			link: function(scope, element, attributes) {
				scope.title = attributes.title;
				scope.description = attributes.description;
				scope.hideBreadcrumb = attributes.hidebreadcrumb;
//				console.log(attributes);
//				console.log(scope);
			}
		}
//			require: '^^ncy-breadcrumb',
//			restrict: 'E',
//			
//			scope: {
//				title: '@'
//			}
//		}
	}]);