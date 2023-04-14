'use strict';

angular.module('lithium').factory('SportsbookRest', ['$log', 'Restangular', '$http',
	function($log, Restangular) {
		try {
			var service = {};
			service.baseUrl = 'services/service-casino-provider-sportsbook/backoffice/bet';
			
			var config = Restangular.withConfig(function(RestangularConfigurer) {
				RestangularConfigurer.setBaseUrl(service.baseUrl);
			});

			service.bets = function(domainName, betId) {
				var request = {
					"betId": betId,
				};
				var response = config.all(domainName).all("search").all("bets").post(request);

				return response;
			}

			service.events = function(domainName, leagues) {
				var request = {
					"leagues": leagues,
				};
				var response = config.all(domainName).all("search").all("events").post(request);

				return response;
			}

			service.leagues = function(domainName, sports) {
				const request = {
					"sports": sports,
				};
				return config.all(domainName).all("search").all("leagues").post(request);
			}

			service.sportsByList = function(domainName) {
				return config.all(domainName).all('search').all('sports').all('list').getList();
			}

			service.markets = function(domainName, sports) {
				var request = {
					"sports": sports,
				};
				var response = config.all(domainName).all("search").all("markets").post(request);

				return response;
			}

			service.sports = function(domainName, sports) {
				var request = {
					"sportsIn": sports,
				};
				var response = config.all(domainName).all("search").all("sports").post(request);

				return response;
			}

			service.table = function(domainName, betId, userId, status, betAmountType, betType, matchType, betTimestampRangeStart,
					betTimestampRangeEnd, settlementTimestampRangeStart, settlementTimestampRangeEnd, paginationLength) {

				var dateType = "BetPlacement"
				var fromDate = moment().subtract(1, 'd');
				var toDate = moment();

				if(betTimestampRangeStart !== null && betTimestampRangeEnd !== null) {
					dateType = "BetPlacement"
					fromDate = betTimestampRangeStart;
					toDate = betTimestampRangeEnd;
				}

				if(settlementTimestampRangeStart !== null && settlementTimestampRangeEnd !== null) {
					dateType = "BetSettledDate"
					fromDate = settlementTimestampRangeStart;
					toDate = settlementTimestampRangeEnd;
				}

				var request = {
					"betId": betId,
					"customerId": userId,
					"betAmountTypeIn": betAmountType,
					"betTypeIn": betType,
					"dateType": dateType,
					"from": fromDate,
					"matchType": matchType,
					"page": 0,
					"size": paginationLength,
					"statusIn": status,
					"to": toDate
				};
				var response = config.all(domainName).all("search").post(request);

				return response;
			}

			service.tableById = function (domainName, betId) {

				const request = {
					'betId': betId,
					'dateType': 'BetPlacement',
					'page': 0
				};
				return config.all(domainName).all("/search").post(request);
			}

			service.tablePost = function (domainName) {

				return config.all(domainName).all("table").post(request);
			}

			return service;
		} catch (err) {
			$log.error(err);
			throw err;
		}
	}
]);
