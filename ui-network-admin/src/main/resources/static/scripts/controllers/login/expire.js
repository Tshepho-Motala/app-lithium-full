'use strict';

angular.module('lithium')
	.controller('TokenExpireModal', ['$uibModalInstance', 'timeout', 'notify', '$filter', '$interval', '$rootScope', 'canExtend','reset',
		function ($uibModalInstance, timeout, notify, $filter, $interval, $rootScope, canExtend, reset) {
			var controller = this;
			controller.timeout = timeout;
			controller.canExtend = canExtend;
			
			var stopped;

			controller.countdown = function() {
				setTimeout(() => {
					controller.startingTime = localStorage.getItem('expires_in')
				}, 1000);


				stopped = $interval(function() {

					if(!controller.canExpire()) {
						controller.stop();
						$uibModalInstance.dismiss('cancel');
						reset();
					}

					controller.timeout--;
					if (controller.timeout === 0) {
						controller.stop();
						$uibModalInstance.dismiss('cancel');
						controller.logout();
						notify.error("You were logged out because your session expired. Please login again to proceed.");
					}
				}, 1000, timeout);
			};

			controller.stop = function() {
				$interval.cancel(stopped);
			}

			controller.canExpire = () => {
				var expiresIn = localStorage.getItem('expires_in')
				var lastRefresh = localStorage.getItem('last_refresh');
				var interval = Math.floor(((new Date()).getTime() - lastRefresh)/ 1000);
				
				return interval < 2 || expiresIn === controller.startingTime;
			}

			controller.logout = function() {
				controller.stop();
				$uibModalInstance.dismiss('cancel');
				reset();
				$rootScope.logout();
			};

			controller.cancel = function() {
				$uibModalInstance.dismiss('cancel');
			};

			controller.extendSession = function() {
				reset();
				$rootScope.extendSession();
				controller.stop();
				$uibModalInstance.dismiss('cancel');
			};
		}]);
