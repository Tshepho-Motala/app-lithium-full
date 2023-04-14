'use strict';

angular.module('lithium')
    .controller('LiftUserRestrictionModal', ['user', '$uibModalInstance', '$scope', 'notify', 'errors', 'bsLoadingOverlayService', 'UserRestrictionsRest', 'restriction',
            function (user, $uibModalInstance, $scope, notify, errors, bsLoadingOverlayService, rest, restriction) {
                var controller = this;

                controller.options = {};
                controller.model = {};

                controller.fields = [
                    {
                        className: "col-xs-12",
                        key: "comment",
                        type: "textarea",
                        templateOptions: {
                            label: "", description: "", placeholder: "",
                            required: true, minlength: 5, maxlength: 65535
                        },
                        expressionProperties: {
                            'templateOptions.label': '"UI_NETWORK_ADMIN.USER.FIELDS.STATUS.COMMENT.NAME" | translate',
                            'templateOptions.placeholder': '"UI_NETWORK_ADMIN.USER.FIELDS.STATUS.COMMENT.PLACEHOLDER" | translate',
                            'templateOptions.description': '"UI_NETWORK_ADMIN.USER.FIELDS.STATUS.COMMENT.DESCRIPTION" | translate'
                        }
                    }
                ];

                controller.referenceId = 'setuserrestriction-overlay';
                controller.submit = function () {
                    bsLoadingOverlayService.start({referenceId: controller.referenceId});
                    if (controller.form.$invalid) {
                        angular.element("[name='" + controller.form.$name + "']").find('.ng-invalid:visible:first').focus();
                        notify.warning("GLOBAL.RESPONSE.FORM_ERRORS");
                        bsLoadingOverlayService.stop({referenceId: controller.referenceId});
                        return false;
                    }

                    rest.lift(user.domain.name, user.guid, restriction.id, user.id, controller.model.comment).then(function (response) {
                        if (response._status !== 0) {
                            notify.error(response._message);
                        } else {
                            notify.success("UI_NETWORK_ADMIN.PLAYER.RESTRICTIONS.LIFT.SUCCESS");
                            $uibModalInstance.close(response.plain());
                        }
                    }).catch(function () {
                        errors.catch('', false);
                    }).finally(function () {
                        bsLoadingOverlayService.stop({referenceId: controller.referenceId});
                        $uibModalInstance.dismiss();
                    });
                };

                controller.cancel = function () {
                    $uibModalInstance.dismiss('cancel');
                };
            }
        ]
    );
