'use strict';

angular.module('lithium')
	.controller('login', ["$scope", "$state", "$rootScope", "$userService", "$sce",'$uibModal',
		function($scope, $state, $rootScope, $userService, $sce,$uibModal) {
		const self = this;
		self.hasProviders = false;

		$rootScope.provide.authentication['resetPassword'] = () => {
			var modalInstance = $uibModal.open({
				animation: true,
				ariaLabelledBy: 'modal-title',
				ariaDescribedBy: 'modal-body',
				templateUrl: 'scripts/controllers/login/password-reset/password-reset.html',
				controller: 'PasswordController',
				controllerAs: 'controller',
				backdrop: 'static',
				size: 'md',
				resolve: {
					credentials: function () {
						return angular.copy(self.credentials);
					}
				}
			});

			modalInstance.result.then(function() {
				$state.go('login.passwordReset.password-reset',{}, {reload: true});
			});
		}

		self.authProviders = function() {
			self.hasProviders = false;
			var domain = 'default'
			if (!angular.isUndefined(this.credentials)) domain = this.credentials.domain;
			if (angular.isUndefined(domain)) domain = 'default';
			if (domain === '') domain = 'default';
			$userService.authProviders(domain).then(function(result) {
				self.providers = result.plain();
				angular.forEach(self.providers, function(p) {
					p.domain = domain;
				});
				if (self.providers.length > 0) self.hasProviders = true;
			}).catch(function(error) {
				console.debug(self.credentials.domain);
				console.error(error);
				self.error = true;
			});
		}
		
		self.loadProvider = function(provider) {
			console.log(provider);
			this.providerUrl = $sce.trustAsResourceUrl('/services/' + provider.url + '/login?domain=' + provider.domain);
		}
}]);

