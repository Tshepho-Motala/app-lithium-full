'use strict';

angular.module('lithium').factory('validatorService', [
        function () {
            let service = this;

            service.compareMinMax = function (min, max) {
                if (min === max) {
                    return {
                        expression: false,
                        message: '"UI_NETWORK_ADMIN.LIMITSAGE.DOMAIN.ADD.SUCCESS"'
                    };
                }

                return {
                    expression: min < max,
                    message: '"GLOBAL.VALIDATION.PASSWORD"'
                };
            }

            service.compareMaxMin = function (max, min) {
                if (min === max) {
                    return false;
                }
                return min < max;
            }

            return service;
        }
    ]
);