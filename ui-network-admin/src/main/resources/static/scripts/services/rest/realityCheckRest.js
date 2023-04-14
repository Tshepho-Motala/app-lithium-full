'use strict';

angular.module('lithium').factory('RealityCheckRest', ['Restangular',
    function(Restangular) {
        try {
            var service = {};

            var rest = function() {
                return Restangular.withConfig(function(RestangularConfigurer) {
                    RestangularConfigurer.setBaseUrl('services/service-limit/backoffice/reality-check/v1');
                });
            }

            service.optionsInMillis = function(domainName) {
                return rest().all(domainName).all('getlistinmins').getList();
            }

            service.get = function(playerGuid, domainName) {
                return rest().all(domainName).one('get').get({guid:playerGuid});
            }

            service.set = function(playerGuid, newRealityCheckTime, domainName) {
                return rest().all(domainName).all('set').post({playerGuid:playerGuid, newRealityCheckTime:newRealityCheckTime});
            }


            return service;
        } catch (error) {
            console.error(error);
            throw error;
        }
    }
]);
