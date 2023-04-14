'use strict';

angular.module('lithium')
    .factory('VerificationStatusRest', ['$log', 'Restangular',
        function($log, Restangular) {
            try {
                var service = {};

                var rest = function() {
                    return Restangular.withConfig(function(RestangularConfigurer) {
                        RestangularConfigurer.setBaseUrl("services/service-limit/backoffice/verification/status");
                    });
                }
                service.findAll = function() {
                    return rest().all("all").getList();
                }

                return service;
            } catch (err) {
                $log.error(err);
                throw err;
            }
        }
    ]);