'use strict';

angular.module('lithium')
    .factory('SettlementsRest', ['$log', 'Restangular',
        function($log, Restangular) {
            try {
                var service = {};

                service.baseUrl = 'services/service-settlement/settlement';

                var config = Restangular.withConfig(function(RestangularConfigurer) {
                    RestangularConfigurer.setBaseUrl(service.baseUrl);
                });

                service.findSettlementById = function(id) {
                    return config.one(""+id).get();
                }

                service.finalize = function(id) {
                    return config.all(""+id).all("finalize").post();
                }

                service.resendPdfStatement = function(id) {
                    return config.all(""+id).all("pdf").all("resend").post();
                }

                return service;
            } catch (err) {
                $log.error(err);
                throw err;
            }
        }
    ]);