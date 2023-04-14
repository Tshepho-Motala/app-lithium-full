'use strict';

angular.module('lithium-rest-domain-age-limits', ['restangular']).factory('domainAgeLimitsRest', ['$log', 'Restangular',
    function ($log, Restangular) {
        try {
            const service = {};

            service.GRANULARITY_DAY = 3;
            service.GRANULARITY_WEEK = 4;
            service.GRANULARITY_MONTH = 2;

            const rest = function () {
                return Restangular.withConfig(function (RestangularConfigurer) {
                    RestangularConfigurer.setBaseUrl('services/service-limit/backoffice/domain-age-limit/v1/');
                });
            }

            service.findAllByDomain = function (domainName) {
                return rest().one('find-age-limits/' + domainName).get();
            }

            service.removeOneAgeRange = function (id) {
                return rest().one('remove-domain-age-limit/' + id).remove();
            }

            service.saveAgeRange = function (data) {
                return rest().one('set-age-limit').post('', data, {}, {});
            }

            service.editAgeRange = function (data) {
                return rest().one('edit-age-limit').post('', data, {}, {});
            }

            service.saveAgeRanges = function (data) {
                return rest().one('set-age-limit-group').post('', data, {}, {});
            }

            service.removeAgeRange = function (data) {
                return rest().one('remove-domain-age-limit-group').post('', data, {}, {});
            }

            service.editAgeRangeMinMax = function (data) {
                return rest().one('edit-age-limit-min-max').post('', data, {}, {});
            }

            return service;
        } catch (error) {
            $log.error(error);
            throw error;
        }
    }
]);
