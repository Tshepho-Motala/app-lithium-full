'use strict';

angular.module('lithium').controller('AccessControlSaveMessage',
    ["$uibModalInstance", "notify", "$scope", "$translate","accessRule", "rule", "accessRulesRest",
    function ($uibModalInstance, notify, $scope, $translate, accessRule, rule, accessRulesRest) {
        var controller = this;
        controller.rule = rule;
        if (angular.isUndefined(rule)) {
            controller.rule = {};
            controller.rule.id = -1;
            controller.rule.type = 'default';
            $translate('UI_NETWORK_ADMIN.ACCESSCONTROL.RULESETS.DEFAULT.TITLE').then(function (translations) {
                controller.rule.description = translations;
            });
            controller.rule.message = accessRule.defaultMessage;
        }
        controller.options = {formState: {}};
        controller.model = {
            message: controller.rule.message,
            reviewMessage: controller.rule.reviewMessage,
            timeoutMessage: controller.rule.timeoutMessage
        };

        controller.fields = [{
            key: "message",
            type: "textarea",
            templateOptions: {
                label: "Message", description: "", placeholder: "", required: false
            },
            modelOptions: {
                updateOn: 'default blur', debounce: { 'default': 1000, 'blur': 0 }
            },
            expressionProperties: {
                'templateOptions.label': '"UI_NETWORK_ADMIN.ACCESSCONTROL.RULESETS.VIEW.TABLE.REJECT_MSG" | translate',
                'templateOptions.placeholder': '"UI_NETWORK_ADMIN.ACCESSCONTROL.RULESETS.SAVEMESSAGE.MESSAGE.PLACEHOLDER" | translate: { messageType: "REJECT"}',
                'templateOptions.description': '"UI_NETWORK_ADMIN.ACCESSCONTROL.SAVEMESSAGE.MESSAGE.DESCRIPTION" | translate'
            }
        }
        ];

        if (controller.rule.type === 'provider') {
            controller.fields.push({
                key: "reviewMessage",
                type: "textarea",
                templateOptions: {
                    label: "Message", description: "", placeholder: "", required: false
                },
                modelOptions: {
                    updateOn: 'default blur', debounce: {'default': 1000, 'blur': 0}
                },
                expressionProperties: {
                    'templateOptions.label': '"UI_NETWORK_ADMIN.ACCESSCONTROL.RULESETS.VIEW.TABLE.REVIEW_MSG" | translate',
                    'templateOptions.placeholder': '"UI_NETWORK_ADMIN.ACCESSCONTROL.RULESETS.SAVEMESSAGE.MESSAGE.PLACEHOLDER" | translate: { messageType: "REVIEW"}',
                    'templateOptions.description': '"UI_NETWORK_ADMIN.ACCESSCONTROL.SAVEMESSAGE.MESSAGE.DESCRIPTION" | translate'
                }
            });
            controller.fields.push({
                key: "timeoutMessage",
                type: "textarea",
                templateOptions: {
                    label: "Message", description: "", placeholder: "", required: false
                },
                modelOptions: {
                    updateOn: 'default blur', debounce: {'default': 1000, 'blur': 0}
                },
                expressionProperties: {
                    'templateOptions.label': '"UI_NETWORK_ADMIN.ACCESSCONTROL.RULESETS.VIEW.TABLE.TIMEOUT_MSG" | translate',
                    'templateOptions.placeholder': '"UI_NETWORK_ADMIN.ACCESSCONTROL.RULESETS.SAVEMESSAGE.MESSAGE.PLACEHOLDER" | translate: { messageType: "TIMEOUT"}',
                    'templateOptions.description': '"UI_NETWORK_ADMIN.ACCESSCONTROL.SAVEMESSAGE.MESSAGE.DESCRIPTION" | translate'
                }
            });
        }

        controller.save = function() {

            let requestMessage = {
                'type': controller.rule.type,
                'message': controller.model.message === '' ? null : controller.model.message,
                'reviewMessage': controller.model.reviewMessage === '' ? null : controller.model.reviewMessage,
                'timeoutMessage': controller.model.timeoutMessage === '' ? null : controller.model.timeoutMessage
            };

            accessRulesRest.saveRuleMessage(accessRule.id, controller.rule.id, requestMessage).then(function(response) {
                notify.success("UI_NETWORK_ADMIN.ACCESSCONTROL.SAVE_MESSAGE.SUCCESS");
                $uibModalInstance.close(response);
            }, function(status) {
                notify.error("UI_NETWORK_ADMIN.ACCESSCONTROL.SAVE_MESSAGE.ERROR");
            });
        }

        controller.cancel = function() {
            $uibModalInstance.dismiss('cancel');
        }
    }]);
