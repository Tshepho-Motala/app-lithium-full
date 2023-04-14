'use strict';

angular.module('lithium')
    .factory('DocumentGenerationRest', ['$log', 'Restangular',
        function($log, Restangular) {
            try {
                var service = {};

                var rest = function() {
                    return Restangular.withConfig(function(RestangularConfigurer) {
                        RestangularConfigurer.setBaseUrl("services/service-document-generation/document");
                    });
                }
                service.generateDocument = function(document) {
                    return rest().all("generate").post(document);
                }

                service.documentStatus = function(reference) {
                    return rest().one(reference + '/status').get();
                }

                service.documentCancel = function(reference) {
                    return rest().one(reference + '/cancel').post();
                }

                service.documentDownload = function(reference) {
                    return rest().one(reference + '/download').get();
                }

                return service;
            } catch (err) {
                $log.error(err);
                throw err;
            }
        }
    ]);