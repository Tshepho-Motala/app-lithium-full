'use strict';

angular.module('lithium')
	.directive('languageSelect', function() {
		return {
			templateUrl:'scripts/directives/language-select/language-select.html',
			scope: { },
			restrict: 'E',
			replace: true,
			controller: ['$rootScope', '$translate', '$http', '$scope', '$log', 
			             function($rootScope, $translate, $http, $scope, $log) {

				$scope.refresh = function() {
					
					$scope.currentLanguageKey = $translate.use() || $translate.proposedLanguage();
					
					$http.get("services/service-translate/apiv1/languages/enabled").then(function(response){
						$scope.currentLanguage = {};
						$scope.languages = response.data;
						for (var i in response.data) {
							if (response.data[i].locale2 == $scope.currentLanguageKey) {
								$scope.currentLanguage = response.data[i];
							}
						}
					});
				};
				
				$scope.changeLanguage = function (langKey) {
					$translate.use(langKey);
				};
				
				$scope.refresh();
				
				$rootScope.$on("$translateChangeEnd", function(event, args) {
					$scope.refresh();
				});
				
			}]
		}
	});