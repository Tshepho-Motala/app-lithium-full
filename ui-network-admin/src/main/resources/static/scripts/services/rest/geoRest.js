'use strict';

angular.module('lithium')
.factory('GeoRest', ['$log', 'Restangular',
	function($log, Restangular) {
		try {
			var service = {};
			var rest = function() {
				return Restangular.withConfig(function(RestangularConfigurer) {
					RestangularConfigurer.setBaseUrl("services/service-geo/geo");
				});
			}
			
			service.countries = function() {
				return rest().all("countries").getList();
			}
			
			service.countriesSearch = function(country) {
				return rest().all("countries").all(country).getList();
			}
			
			service.countryLevel1s = function(countryCode) {
				return rest().all("countries").all(countryCode).all("level1").getList();
			}
			
			service.countryLevel1sSearch = function(countryCode, level1) {
				return rest().all("countries").all(countryCode).all("level1").all(level1).getList();
			}
			
			service.level1s = function(level1) {
				return rest().all("level1s").all(level1).getList();
			}
			
			service.level2 = function(countryCode, level1) {
				return rest().all("countries").all(countryCode).all("level1").all(level1).all("level2").getList();
			}
			
			service.level1Cities = function(countryCode, level1) {
				return rest().all("countries").all(countryCode).all("level1").all(level1).all("cities").getList();
			}
			
			service.level1CitiesSearch = function(countryCode, level1, city) {
				return rest().all("countries").all(countryCode).all("level1").all(level1).all("cities").all(city).getList();
			}
			
			service.countryCities = function(countryCode) {
				return rest().all("countries").all(countryCode).all("cities").getList();
			}
			
			service.countryCitiesSearch = function(countryCode, city) {
				return rest().all("countries").all(countryCode).all("cities").all(city).getList();
			}
			
			service.cities = function(city) {
				return rest().all("cities").all(city).getList();
			}
			
			return service;
		} catch (err) {
			$log.error(err);
			throw err;
		}
	}
]);