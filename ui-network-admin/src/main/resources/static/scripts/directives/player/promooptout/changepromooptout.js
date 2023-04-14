'use strict';

angular.module('lithium')
.controller('ChangePromoOptoutModal', ["$uibModalInstance", "$scope", "user", "data", "EcosysRest",
function ($uibModalInstance, $scope, user, data, EcosysRest) {
	var controller = this;
	$scope.user = user;
	$scope.data = data;
	$scope.data.isRootAccountLinked = false;
	EcosysRest.ecosystemRelationshiplistByDomainName($scope.user.domain.name).then(function (response) {
		if (angular.isDefined(response)) {
			$scope.ecosystemRelationshipList = response.plain();
			for (var i = 0; i < $scope.ecosystemRelationshipList.length; i++) {
				if (($scope.user.domain.name === $scope.ecosystemRelationshipList[i].domain.name) &&
					($scope.ecosystemRelationshipList[i].relationship.code === "ECOSYSTEM_ROOT")) {
					$scope.data.isRootAccountLinked = true;
				}
			}
		}
	});
	controller.cancel = function() {
		$uibModalInstance.dismiss('cancel');
	};
}]);
