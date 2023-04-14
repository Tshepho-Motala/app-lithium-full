'use strict';

angular.module('lithium')
    .factory('BvnVerifyRest', ['$log', 'Restangular',
        function($log, Restangular) {
            try {
                var service = {};

                var rest = function() {
                    return Restangular.withConfig(function(RestangularConfigurer) {
                        RestangularConfigurer.setBaseUrl("services/service-kyc-provider-paystack/backoffice");
                    });
                }
                service.verifyBvn = function(verifyBvnRequest) {
                    return rest().all("kyc").all("verify-bvn").post(verifyBvnRequest);
                }
                return service;
            } catch (err) {
                $log.error(err);
                throw err;
            }
        }
    ]);