'use strict';

angular.module('lithium').controller('CashierTransactionOnHoldModal', ["transactionId", "domainName", "errors", "$scope", "notify", "$uibModalInstance", "bsLoadingOverlayService", "rest-cashier", "$translate",
    function (transactionId, domainName, errors, $scope, notify, $uibModalInstance, bsLoadingOverlayService, rest, $translate) {
        var controller = this;

        controller.model = {};
        $translate('UI_NETWORK_ADMIN.CASHIER.TRANSACTION.ON_HOLD.REMARK').then(function success (data) {
            controller.remark = data
        })

        controller.fields = [
            {
                className: 'col-xs-12',
                key: "reason",
                type: "input",
                templateOptions: {
                    label: "Reason for the hold",
                    required: true
                },
                expressionProperties: {
                    'templateOptions.label': '"UI_NETWORK_ADMIN.CASHIER.TRANSACTION.ON_HOLD.REASON.LABEL" | translate',
                }
            }
        ];

        controller.onSubmit = function() {
            if (controller.form.$invalid) {
                angular.element("[name='" + controller.form.reason + "']").find('.ng-invalid:visible:first').focus();
                notify.warning("GLOBAL.RESPONSE.FORM_ERRORS");
                return false;
            }

            bsLoadingOverlayService.start({referenceId: "loading"});
            rest.transactionOnHold(domainName, transactionId, controller.remark + " " + controller.model.reason)
                .then(function (response) {
                    if (response.state !== "ON_HOLD") {
                        notify.error(response.errorMessage);
                    } else {
                        notify.success("UI_NETWORK_ADMIN.CASHIER.TRANSACTION.ON_HOLD.SUCCESS");
                        $uibModalInstance.close(response);
                    }
                }).catch(
                errors.catch("UI_NETWORK_ADMIN.CASHIER.TRANSACTION.ON_HOLD.ERROR", false)
            ).finally(function () {
                bsLoadingOverlayService.stop({referenceId: "loading"});
            });
        }

        controller.cancel = function() {
            $uibModalInstance.dismiss('cancel');
        }
    }
]);
