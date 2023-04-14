'use strict';

angular.module('lithium')
.factory('periodService', [
    function() {
        var Granularity = {
            YEARLY: '1',
            MONTHLY: '2',
            DAILY: '3',
            WEEKLY: '4'
        };

        var Type = {
            START: 'start',
            END: 'end'
        };

        var service = {};

        var getDateByOffset = function(type, offsetType, offset) {
            var date = luxon.DateTime.local();
            switch (type) {
                case Type.START:
                    date = date.startOf(offsetType).minus(offset);
                    return date;
                case Type.END:
                    date = date.endOf(offsetType).minus(offset);
                    return date;
                default:
                    console.error('Invalid type', type);
                    return null;
            }
        }

        service.getDateByGranularityAndOffsetAndType = function(granularity, offset, type) {
            switch (granularity) {
                case Granularity.YEARLY: return getDateByOffset(type, 'year', {year:offset});
                case Granularity.MONTHLY: return getDateByOffset(type, 'month', {month:offset});
                case Granularity.DAILY: return getDateByOffset(type, 'day', {day:offset});
                case Granularity.WEEKLY: return getDateByOffset(type, 'week', {week:offset});
                default:
                    console.error('Invalid granularity', granularity);
                    return null;
            }
        }

        return service;
    }
]);
