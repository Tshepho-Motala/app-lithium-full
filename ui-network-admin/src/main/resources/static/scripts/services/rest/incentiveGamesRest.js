'use strict';

angular.module('lithium').factory('IncentiveGamesRest', ['$log', 'Restangular', '$http',
	function($log, Restangular, $http) {
		try {
			var service = {};
			
			service.baseUrl = 'services/service-casino-provider-incentive/admin';
			
			var config = Restangular.withConfig(function(RestangularConfigurer) {
				RestangularConfigurer.setBaseUrl(service.baseUrl);
			});

			service.getSettlementResultCodes = function() {
				return config.all('bets').one("resultcodes").getList();
			}

			service.getBetsTable = function(draw, start, length, userGuid, isSettled, settlementResult, betTimestampRangeStart,
				betTimestampRangeEnd, settlementTimestampRangeStart, settlementTimestampRangeEnd, betId, settlementId,
				competition, sport, eventName, market) {
					return $http({
						url: service.baseUrl + '/bets/table',
						method: "GET",
						params: {
							draw: draw,
							start: start,
							length: length,
							userGuid: userGuid,
							isSettled: isSettled,
							settlementResult: settlementResult,
							betTimestampRangeStart: betTimestampRangeStart,
							betTimestampRangeEnd: betTimestampRangeEnd,
							settlementTimestampRangeStart: settlementTimestampRangeStart,
							settlementTimestampRangeEnd: settlementTimestampRangeEnd,
							betId: betId,
							settlementId: settlementId,
							competition: competition,
							sport: sport,
							eventName: eventName,
							market: market,
							"order[0][column]": 0,
							"columns[0][data]": "id",
							"order[0][dir]": "desc"
						}
					});
			}

			return service;
		} catch (err) {
			$log.error(err);
			throw err;
		}
	}
]);