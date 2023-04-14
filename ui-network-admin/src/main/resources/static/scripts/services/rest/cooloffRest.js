'use strict';

angular.module('lithium').factory('CoolOffRest', ['Restangular',
    function(Restangular) {
        try {
            var service = {};

            var rest = function() {
                return Restangular.withConfig(function(RestangularConfigurer) {
                    RestangularConfigurer.setBaseUrl('services/service-limit/backoffice/cooloff');
                });
            }

            service.optionsInDays = function(domainName) {
                return rest().all(domainName).all('options').all('days').getList();
            }

            service.lookup = function(playerGuid, domainName) {
                return rest().all(domainName).one('lookup').get({playerGuid:playerGuid});
            }

            service.set = function(playerGuid, periodInDays, domainName) {
                return rest().all(domainName).all('set').post({playerGuid:playerGuid, periodInDays:periodInDays});
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
