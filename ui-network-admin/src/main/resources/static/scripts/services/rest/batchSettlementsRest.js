'use strict';

angular.module('lithium')
    .factory('BatchSettlementsRest', ['$log', 'Restangular',
        function($log, Restangular) {
            try {
                var service = {};

                service.baseUrl = 'services/service-settlement/batch/settlements';

                var config = Restangular.withConfig(function(RestangularConfigurer) {
                    RestangularConfigurer.setBaseUrl(service.baseUrl);
                });

                service.batchNameIsUnique = function(batchName) {
                    return config.all(batchName).one("isunique").get();
                }

                service.get = function(batchSettlementId) {
                    return config.one(""+batchSettlementId).get();
                }

                service.finalizeBatchSettlements = function(batchSettlementId) {
                    return config.all(""+batchSettlementId).one("finalize").post();
                }

                return service;
            } catch (err) {
                $log.error(err);
                throw err;
            }
        }
    ]);