'use strict';

angular.module('lithium')
    .factory('AccessProviderRest', ['$log', 'Restangular',
            function ($log, restAngular) {
                try {
                    let service = {};

                    const cruksRest = function() {
                        return restAngular.withConfig(function(restAngularConfigurer) {
                            restAngularConfigurer.setBaseUrl('services/service-access-provider-sphonic-cruks/');
                        }).service('backoffice');
                    }

                    service.verifyCruksId = function (cruksId, domainName) {
                        return cruksRest().one('validation').one(domainName).one('cruks').get({cruksId: cruksId});
                    }

                    return service;
                } catch (error) {
                    $log.error(error);
                    throw error;
                }
            }
        ]
    );
