'use strict';

angular.module('lithium-rest-accounting-internal', ['restangular'])
.factory('rest-accounting-internal', ['$log', 'Restangular',
	function($log, Restangular) {
		try {
			var service = {};

			var rest = Restangular.withConfig(function(RestangularConfigurer) {
				RestangularConfigurer.setBaseUrl('services/service-accounting-provider-internal');
			});

			service.findCurrencyByCode = function(code) {
				return rest.all('currencies').one('findByCode', code).get();
			}
			
			service.allCurrencies = function() {
				return rest.all('currencies').all('all').getList();
			}
			
			service.allCurrenciesSearch = function(search) {
				return rest.all('currencies').all('search').all(search).getList();
			}
			
			service.findDomainCurrencies = function(domainName) {
				return rest.all('currencies').all('domain').all(domainName).all('list').getList();
			}
			
			service.saveDomainCurrency = function(domainName, domainCurrency) {
				return rest.all('currencies').all('domain').all(domainName).all('save').post(domainCurrency);
			}
			
			service.deleteDomainCurrency = function(domainName, id) {
				return rest.all('currencies').all('domain').all(domainName).all('delete').all(id).remove();
			}
			
			service.setAsDefault = function(domainName, id) {
				return rest.all('currencies').all('domain').all(domainName).all(id).all('setAsDefault').post();
			}
			
			service.viewCurrency = function(domainName, id) {
				return rest.all('currencies').all('domain').all(domainName).one(id).get();
			}

			service.labelValuesByLabelName = function(labelName, fetchSize, pageNumber) {
				return rest.all('admin').all('transactions').all('labelvalues').customGET(labelName, {'fetchSize': fetchSize, 'pageNumber': pageNumber});
			}

			service.fetchAccountCodes = function () {
				return rest.all('admin').all('transactions').one('account-codes').getList();
			}

			service.fetchBalanceMovementTypesForUser = function (userGuid) {
				return rest.all('backoffice').all('balance-movement').customGET('types',{'userGuid':userGuid});
			}
			
			return service;
		} catch (err) {
			$log.error(err);
			throw err;
		}
	}
]);
