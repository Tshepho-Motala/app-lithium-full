'use strict';

angular.module('lithium')
.directive('promooptout', function() {
	return {
		templateUrl:'scripts/directives/player/promooptout/promooptout.html',
		scope: {
			data: "=",
			user: "=ngModel"
		},
		restrict: 'E',
		replace: true,
		controller: ['$scope', 'UserRest', 'notify', 'errors', 'bsLoadingOverlayService', 'EcosysRest',
		function($scope, UserRest, notify, errors, bsLoadingOverlayService, EcosysRest) {
			$scope.referenceId = 'promooptout-overlay';
			$scope.data.isRootAccountLinked = false
			$scope.crossDomainOptOutDisplayEnabled = $scope.data.domainSettings['leaderboard_push_domain_link_opt_out'] == "show" ? true : false;

			$scope.brandCountryCode = function (domainName) {
				if (domainName.includes("_")) {
					var brandName = domainName.split("_");
					return `${brandName[0]} ${brandName[1].toUpperCase()}`;
				}

				var countryCode = $scope.user.countryCode;
				if(countryCode != null) {
					return `${domainName} ${countryCode.toUpperCase()}`;
				}

				return domainName;
			}
		
			if ($scope.data.playerLinkData !== undefined && $scope.data.playerLinkData.length > 0) {
				$scope.user.ecosystemRootBrand = $scope.data.playerLinkData[0];
				$scope.userLinkType = $scope.user.ecosystemRootBrand.userLinkType.code;
				$scope.brandCountryCode($scope.user.ecosystemRootBrand.secondaryUser.domain.name);
				if ($scope.userLinkType === "CROSS_DOMAIN_LINK") {
					$scope.user.brandName = $scope.brandCountryCode($scope.user.ecosystemRootBrand.secondaryUser.domain.name);
					$scope.user.primaryBrandName = $scope.brandCountryCode($scope.user.domain.name);
					$scope.user.ecosystemRootEmailOptOut = $scope.user.ecosystemRootBrand.secondaryUser.emailOptOut;
					$scope.user.ecosystemRootPostOptOut = $scope.user.ecosystemRootBrand.secondaryUser.postOptOut;
					$scope.user.ecosystemRootSmsOptOut = $scope.user.ecosystemRootBrand.secondaryUser.smsOptOut;
					$scope.user.ecosystemRootCallOptOut = $scope.user.ecosystemRootBrand.secondaryUser.callOptOut;

					EcosysRest.ecosystemRelationshiplistByDomainName($scope.user.domain.name).then(function (response) {
						$scope.ecosystemRelationshipList = response.plain();
						for (var i = 0; i < $scope.ecosystemRelationshipList.length; i++) {
							if (($scope.user.domain.name === $scope.ecosystemRelationshipList[i].domain.name) &&
								($scope.ecosystemRelationshipList[i].relationship.code === "ECOSYSTEM_ROOT")) {
								$scope.data.isRootAccountLinked = true;

							}
						}
					});
				}
			}
			$scope.opt = function(method, optOut) {
				bsLoadingOverlayService.start({referenceId:$scope.referenceId});
				UserRest.opt($scope.user.domain.name, $scope.user.id, method, optOut).then(function(response) {
					$scope.user.emailOptOut = response.emailOptOut;
					$scope.user.postOptOut = response.postOptOut;
					$scope.user.smsOptOut = response.smsOptOut;
					$scope.user.callOptOut = response.callOptOut;
					$scope.user.pushOptOut = response.pushOptOut;
					$scope.user.leaderboardOptOut = response.leaderboardOptOut;
				}).catch(
					errors.catch("UI_NETWORK_ADMIN.USER.ERRORS.PROMOOPTOUT", false)
				).finally(function () {
					bsLoadingOverlayService.stop({referenceId:$scope.referenceId});
				});
			}

			$scope.optEcosystemRoot = function(method, optOut) {
				bsLoadingOverlayService.start({referenceId:$scope.referenceId});
				UserRest.opt($scope.user.ecosystemRootBrand.secondaryUser.domain.name, $scope.user.ecosystemRootBrand.secondaryUser.id, method, optOut).then(function(response) {
					$scope.user.ecosystemRootEmailOptOut = response.emailOptOut;
					$scope.user.ecosystemRootPostOptOut = response.postOptOut;
					$scope.user.ecosystemRootSmsOptOut = response.smsOptOut;
					$scope.user.ecosystemRootCallOptOut = response.callOptOut;
				}).catch(
					errors.catch("UI_NETWORK_ADMIN.USER.ERRORS.PROMOOPTOUT", false)
				).finally(function () {
					bsLoadingOverlayService.stop({referenceId:$scope.referenceId});
				});
			}
		}]
	}
});
