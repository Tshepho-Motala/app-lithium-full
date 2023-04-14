'use strict';

angular.module('lithium')
    .controller('ChangeDoBModal',
        ['$uibModalInstance', 'user', "UserRest", "ProfileRest", 'userFields', 'notify', 'profile', 'domainSettings', 'errors', 'bsLoadingOverlayService', '$uibModal',
            function ($uibModalInstance, user, UserRest, ProfileRest, userFields, notify, profile, domainSettings, errors, bsLoadingOverlayService, $uibModal) {
                let controller = this;
                controller.submitCalled = false;

                controller.options = {removeChromeAutoComplete:true};
                controller.model = user;

                if (user.dobDay != null && user.dobMonth != null && user.dobYear != null) {
                    user.dateOfBirth = new Date(user.dobYear, user.dobMonth - 1, user.dobDay);
                }

                controller.fields = [
                    userFields.dateOfBirth,
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

                controller.fields[0].templateOptions.datepickerOptions.maxDate = new Date();
                controller.referenceId = 'changepersonal-overlay';
                controller.submit = function() {

                    bsLoadingOverlayService.start({referenceId:controller.referenceId});
                    if (controller.form.$invalid) {
                        angular.element("[name='" + controller.form.$name + "']").find('.ng-invalid:visible:first').focus();
                        notify.warning("GLOBAL.RESPONSE.FORM_ERRORS");
                        bsLoadingOverlayService.stop({referenceId: controller.referenceId});
                        return false;
                    }

                    if (user.dateOfBirth != null) {
                        const utc = Date.UTC(user.dateOfBirth.getFullYear(), user.dateOfBirth.getMonth(), user.dateOfBirth.getDate());
                        user.dateOfBirth = new Date(utc);
                        console.debug("user.dateOfBirth", user.dateOfBirth);
                    }

                    const userChanges = {
                        userId : user.id,
                        dateOfBirth: user.dateOfBirth,
                        comment: controller.model.comment
                    }
                    function processChangeDateOfBirth() {
                        UserRest.changedateofbirth(user.domain.name, userChanges).then(function (response) {
                            $uibModalInstance.close(response);
                        }).catch(
                            errors.catch("UI_NETWORK_ADMIN.USER.ERRORS.PERSONALSAVE", false)
                        ).finally(function () {
                            bsLoadingOverlayService.stop({referenceId: controller.referenceId});
                        });
                    }
                    //if the profile is not a player account we can initiate the update
                    if (profile) {
                        processChangeDateOfBirth();
                        return true;
                    }

                    let age = moment().diff(user.dateOfBirth, 'years');
                    let allowUnderAge = 'false';
                    let minUserAge = 18;

                    if (domainSettings !== undefined) {
                        allowUnderAge = domainSettings['enable_underage_registration'] !== undefined ? domainSettings['enable_underage_registration'] : 'false';
                        minUserAge = domainSettings['minUserAge'] !== undefined  ? domainSettings['minUserAge'] : minUserAge;
                    }

                    if(allowUnderAge === 'false' && age >= minUserAge) {
                        processChangeDateOfBirth();
                    } else if(allowUnderAge === 'false' && age < minUserAge) {
                        controller.confirmChangeDateOfBirth(userChanges);
                    } else if((allowUnderAge === undefined || allowUnderAge === 'true') && age >= 0) {
                        processChangeDateOfBirth();
                    } else {
                        notify.error("UI_NETWORK_ADMIN.USER.ERRORS.PERSONALSAVE.DOB")
                    }
                };

                controller.confirmChangeDateOfBirth = function(userChanges) {
                    let modalInstance  = $uibModal.open({
                        animation: true,
                        ariaLabelledBy: 'modal-title',
                        ariaDescribedBy: 'modal-body',
                        templateUrl: 'scripts/directives/player/personal/confirmchangedateofbirth.html',
                        controller: 'ConfirmChangeDateOfBirth',
                        controllerAs: 'controller',
                        backdrop: 'static',
                        size: 'md',
                        resolve: {
                            controllerModel : function() {
                                return controller.model;
                            },
                            UserRest : function () {
                                return UserRest;
                            },
                            domainName: function () {
                                return user.domain.name;
                            },
                            userUpdates : function () {
                                return userChanges;
                            },
                            loadMyFiles: function($ocLazyLoad) {
                                return $ocLazyLoad.load({
                                    name: 'lithium',
                                    files: ['scripts/directives/player/personal/confirmchangedateofbirth.js']
                                });
                            }
                        }
                    });
                    modalInstance.result.then(function(response) {
                        $uibModalInstance.close(response);
                    });
                }
                controller.cancel = function() {
                    $uibModalInstance.dismiss('cancel');
                };
            }]);