'use strict';

angular.module('lithium').controller('XPSchemesLevelAddNotificationModal', ["domainName", "notification", "$scope", "notify", "errors", "$uibModalInstance", "NotificationRest",
    function (domainName, notification, $scope, notify, errors, $uibModalInstance, notificationRest) {
        var controller = this;

        if (notification !== null) {
            controller.model = notification;
        }

        controller.fields = [
            {
                key: 'triggerPercentage',
                type: 'ui-number-mask',
                optionsTypes: ['editable'],
                templateOptions : {
                    label: "Trigger Percentage",
                    required: true,
                    decimals: 0,
                    hidesep: true,
                    neg: false,
                    min: '',
                    max: ''
                }
            }, {
                key: "notificationName",
                type: "ui-select-single",
                "optionsTypes": ['editable'],
                templateOptions : {
                    label: "Notification",
                    description: "",
                    placeholder: "",
                    valueProp: 'name',
                    labelProp: 'name',
                    optionsAttr: 'ui-options', ngOptions: 'ui-options',
                    options: []
                },
                controller: ['$scope', function($scope) {
                    if (domainName) {
                        notificationRest.findByDomainName(domainName).then(function(notifications) {
                            $scope.to.options = notifications.plain();
                        }).catch(function(error) {
                            notify.error("Could not load notifications.");
                            $scope.to.options = [];
                        }).finally(function() {
                        });
                    }
                }]
            }
        ];

        controller.save = function() {
            if (controller.form.$invalid) {
                angular.element("[name='" + controller.form.$name + "']").find('.ng-invalid:visible:first').focus();
                notify.warning("GLOBAL.RESPONSE.FORM_ERRORS");
                return false;
            }

            $uibModalInstance.close(controller.model);
        }

        controller.cancel = function() {
            $uibModalInstance.dismiss('cancel');
        }
    }]);