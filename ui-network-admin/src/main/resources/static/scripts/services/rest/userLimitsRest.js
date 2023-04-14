'use strict';

angular.module('lithium-rest-user-limits', ['restangular']).factory('userLimitsRest', ['$log', 'Restangular',
	function($log, Restangular) {
		try {
			var service = {};
			
			service.GRANULARITY_DAY = 3;
			service.GRANULARITY_WEEK = 4;
			service.GRANULARITY_MONTH = 2;

			service.LIMIT_TYPE_WIN = 1;
			service.LIMIT_TYPE_LOSS = 2;
			service.DEPOSIT_LIMIT = 3;
			service.DEPOSIT_LIMIT_PENDING = 4;

			service.onTimeSlotLimitFetched = () => {}
			service.timeSlotLimitLastGuid = ""
			service.timeSlotLastData = {
				limitFromUtc: 0,
				limitToUtc: 0
			}

			
			var rest = function(domainName) {
				return Restangular.withConfig(function(RestangularConfigurer) {
					RestangularConfigurer.setBaseUrl("services/service-limit/backoffice/player-limit/v1/" + domainName +"/");
				});
			}

			var isNotUndefined = function (object) {
				return object !== null && object !== undefined
			}

			// Restangular.setErrorInterceptor(function(response, deferred, responseHandler) {
			// 	console.log(response, deferred, responseHandler);
			// });

			var restBackoffice = Restangular.withConfig(function(RestangularConfigurer) {
				RestangularConfigurer.setBaseUrl("services/service-limit/backoffice/");
			});

			var convertUnixToLocal = function(unixtimestamp) {
				if(unixtimestamp === null || unixtimestamp === undefined) {
					return null
				}
				const date = new Date(unixtimestamp);
				const minutes = ("0" + date.getMinutes()).substr(-2);
				const hours = ("0" + date.getUTCHours()).substr(-2);
				return `${hours}:${minutes}`;
			};

			service.findNetLossToHouse = function(domainName, playerGuid, currency, granularity) {
				return rest(domainName).one('net-loss-to-house').get({domainName: domainName, playerGuid: playerGuid, currency: currency, granularity: granularity});
			}
			
			service.findPlayerLimit = function(playerGuid, domainName, granularity, type) {
				return rest(domainName).one('find-player-limit').get({playerGuid: playerGuid, granularity: granularity, type: type});
			}
			
			service.setPlayerLimit = function(playerGuid, playerId, domainName, granularity, amount, type) {
				return rest(domainName).one('set-player-limit').get({playerGuid: playerGuid, playerId: playerId, granularity: granularity, amount: amount, type: type});
			}
			
			service.removePlayerLimit = function(playerGuid, playerId, domainName, granularity, type) {
				return rest(domainName).one('remove-player-limit').remove({playerGuid: playerGuid, playerId: playerId, granularity: granularity, type: type});
			}

			service.getLossLimitVisibility = function(domainName, playerGuid) {
				return rest(domainName).one('get-loss-limit-visibility').get({playerGuid: playerGuid});
			}
			service.setLossLimitVisibility = function(domainName, playerGuid, visibility) {
				return rest(domainName).all('set-loss-limit-visibility').post('', {playerGuid: playerGuid, visibility: visibility});
			}

			// Time Frame Limits
			service.setTimeSlotLimit = async function(playerGuid, playerId, domainName, timeFromUtc, timeToUtc) {
				const response = await rest(domainName).one('set-player-time-slot-limit').get({playerGuid: playerGuid, timeFromUtc:timeFromUtc, timeToUtc:timeToUtc, playerId: playerId});
				this.timeSlotLimitLastGuid = playerGuid
				this.timeSlotLastData = {
					exists: true,
					limitFromUtc: response.limitFromUtc,
					limitToUtc: response.limitToUtc,
					limitFromDisplay: convertUnixToLocal(response.limitFromUtc),
					limitToDisplay: convertUnixToLocal(response.limitToUtc),
				}
				return this.timeSlotLastData
			}
			service.findTimeSlotLimit = async function(playerGuid, domainName) {
				const response = await rest(domainName).one('get-player-time-slot-limit').get({playerGuid: playerGuid});
				this.timeSlotLimitLastGuid = playerGuid
				this.timeSlotLastData = {
					exists: isNotUndefined(response.limitFromUtc) && isNotUndefined(response.limitToUtc),
					limitFromUtc: response.limitFromUtc,
					limitToUtc: response.limitToUtc,
					limitFromDisplay: convertUnixToLocal(response.limitFromUtc),
					limitToDisplay: convertUnixToLocal(response.limitToUtc),
				}
				return this.timeSlotLastData
			}
			service.removeTimeSlotLimit = async  function(playerGuid, playerId, domainName) {
				const response = await rest(domainName).one('remove-player-time-slot-limit').remove({playerGuid: playerGuid, playerId: playerId});
				this.timeSlotLimitLastGuid = playerGuid
				this.timeSlotLastData = {
					exists: false,
					limitFromUtc: 0,
					limitToUtc: 0,
					limitFromDisplay: null,
					limitToDisplay: null
				}
				return response
			}

			//Deposit Limits
			service.depositLimits = function(playerGuid) {
				return restBackoffice.one('depositlimit', playerGuid.split("/")[0]).get({playerGuid: playerGuid});
			}
			service.depositLimitsPending = function(playerGuid) {
				return restBackoffice.one('depositlimit', playerGuid.split("/")[0]).one('pending').get({playerGuid: playerGuid});
			}
			service.depositLimitsSupposed = function(playerGuid) {
				return restBackoffice.one('depositlimit', playerGuid.split("/")[0]).one('supposed').get({playerGuid: playerGuid});
			}
			service.depositLimitRemove = function(playerGuid, granularity) {
				return restBackoffice.one('depositlimit', playerGuid.split("/")[0]).remove({playerGuid: playerGuid, granularity: granularity});
			}
			service.depositLimitRemovePending = function(playerGuid, granularity) {
				return restBackoffice.one('depositlimit', playerGuid.split("/")[0]).all('pending').remove({playerGuid: playerGuid, granularity: granularity});
			}
			service.depositLimitRemoveSupposed = function(playerGuid, granularity) {
				return restBackoffice.one('depositlimit', playerGuid.split("/")[0]).all('supposed').remove({playerGuid: playerGuid, granularity: granularity});
			}
			service.depositLimitApplySupposed = function(playerGuid, granularity) {
				return restBackoffice.one('depositlimit', playerGuid.split("/")[0]).all("supposed").post('',{playerGuid: playerGuid, granularity: granularity});
			}
			service.depositLimitSave = function(playerGuid, granularity, amount) {
				return restBackoffice.all('depositlimit').all(playerGuid.split("/")[0]).post('', {playerGuid: playerGuid, granularity: granularity, amount: amount});
			}

			//Balance limits
			service.balanceLimitsList = function(domainName, playerGuid) {
				return restBackoffice.one('balance-limit', domainName).one('player').get({playerGuid: playerGuid});
			}
			service.balanceLimitRemovePending = function(domainName, playerGuid) {
				return restBackoffice.one('balance-limit', domainName).one('pending').remove({playerGuid: playerGuid});
			}
			service.balanceLimitSave = function(domainName, playerGuid, amount) {
				return restBackoffice.one('balance-limit').one(domainName).all('save').post('', {playerGuid: playerGuid, amount: amount});
			}
			
			return service;
		} catch (error) {
			$log.error(error);
			throw error;
		}
	}
]);
