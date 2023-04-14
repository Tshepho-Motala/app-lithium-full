'use strict';

angular.module('lithium').factory('ExclusionRest', ['Restangular',
    function(Restangular) {
        try {
            var service = {};

            var rest = function() {
                return Restangular.withConfig(function(RestangularConfigurer) {
                    RestangularConfigurer.setBaseUrl('services/service-limit/backoffice/exclusion');
                });
            }

            service.optionsInMonths = function(domainName) {
                return rest().all(domainName).all('options').all('months').getList();
            }

            service.lookup = function(playerGuid, domainName) {
                return rest().all(domainName).one('lookup').get({playerGuid:playerGuid});
            }

            service.set = function(playerGuid, periodInMonths, domainName) {
                return rest().all(domainName).all('set').post({playerGuid:playerGuid, periodInMonths:periodInMonths});
            }

            service.clear = function(playerGuid, domainName) {
                return rest().all(domainName).all('clear').post('', {playerGuid:playerGuid});
            }

            return service;
        } catch (error) {
            console.error(error);
            throw error;
        }
    }
]);
