'use strict'

angular.module('lithium').controller('AutoWithdrawalRulesetViewController', ['$translate', 'ruleset', 'fields', 'operators', 'AutoWithdrawalRulesetRest', 'notify', 'errors', '$userService', "$rootScope",  "VerificationStatusRest", "rest-cashier", '$http', "UserRest",
    function($translate, ruleset, fields, operators, rest, notify, errors, $userService, $rootScope, verificationStatusRest,   cashierRest, $http, UserRest) {
        var controller = this;
        controller.ruleset = ruleset.plain();
        controller.fields = fields.plain();
        controller.operators = operators.plain();
        controller.modalIsVisible = false
        controller.selectedDomain = ''
        controller.defaultSelectedDomain = 'Choose one of domains'


        controller.domains = $userService.domainsWithAnyRole(["ADMIN", "AUTOWITHDRAWALS_*"]);

        console.debug("ruleset", controller.ruleset);
        console.debug("fields", controller.fields);
        console.debug("operators", controller.operators);

        controller.changelogs = {
            domainName: ruleset.domain.name,
            entityId: ruleset.id,
            restService: rest,
            reload: 0
        }

        controller.getFieldName = function(id) {
            for (var i = 0; i < controller.fields.length; i++) {
                if (controller.fields[i].id === id) return fields[i].displayName;
            }
            return null;
        }

        controller.getOperatorName = function(rule) {
            for (var i = 0; i < controller.operators.length; i++) {
                if (rule.field === 11 && controller.operators[i].operator === "IN") {
                    operators[i].displayName = $translate.instant('UI_NETWORK_ADMIN.AUTOWITHDRAWALS.RULESETS.RULE.EXCHANGER.IN');
                }
                if (controller.operators[i].id === rule.operator) return operators[i].displayName;
            }
            return null;
        }

        controller.getValue = function(rule) {
            var value = '';
            for (var i = 0; i < rule.value.length; i++) {
                value = value + rule.value[i].description
                    if (i < rule.value.length - 1) {
                        value = value+ ', '
                    }
            }
            if (rule.value2 !== undefined && rule.value2 !== null) {
                value = value +  " to " + rule.value2;
            }
            return value;
        }

        controller.processRuleset = function() {
            rest.queueAutoWithdrawalRulesetProcess(ruleset.domain.name, ruleset.id)
                .then(function (response) {
                    if (response._status !== 0) {
                        notify.error(response._message);
                    } else {
                        var data = response.plain();
                        notify.success("UI_NETWORK_ADMIN.CASHIER.AUTOWITHDRAWALS.RULESET.PROCESS.SUCCESS");
                        controller.changelogs.reload += 1;
                    }
                }).catch(
                errors.catch("UI_NETWORK_ADMIN.CASHIER.AUTOWITHDRAWALS.RULESET.PROCESS.ERROR", false)
            ).finally(function () {
            });
        }

        controller.cloneRuleset = function() {
            let clonedRuleset = {}
            for ( let key in controller.ruleset) {

                if(key == 'name') {
                    clonedRuleset[key] = controller.ruleset[key] + '_clone'
                } else if(key == 'id') {
                    clonedRuleset[key] = null
                } else if(key == 'domain') {
                    clonedRuleset[key] = {
                        "id": null,
                        "version" : null,
                        "name": controller.selectedDomain
                    }
                } else if(key == 'rules') {
                    let tmpRules = controller.ruleset[key].map(item => { return { ...item, id: null}})
                    clonedRuleset[key] = tmpRules
                } else {
                    clonedRuleset[key] = controller.ruleset[key]
                }
            }
            console.log(controller.selectedDomain, clonedRuleset)
            rest.autoWithdrawalRulesetCreate(controller.selectedDomain, clonedRuleset)
        }

        controller.domainSelect = function(domain) {
            controller.selectedDomain = domain
            controller.defaultSelectedDomain = ''
        }

        $rootScope.provide.cashierProvider['rulesFields'] =  controller.operators
        $rootScope.provide.cashierProvider['rulesOperators'] =  controller.fields
        $rootScope.provide.cashierProvider['domains'] =  controller.domains
        $rootScope.provide.cashierProvider['getRuleset'] = () => {
            return controller.ruleset
        }
        $rootScope.provide.cashierProvider['ruleCashierRest'] = () => {
            return cashierRest.methodsNoImage()
        }
        $rootScope.provide.cashierProvider['ruleFindAllTags'] = (data) => {
            return  UserRest.findAllTags(data)
        }

        $rootScope.provide.cashierProvider['ruleVerificationStatusRest'] = () => {
            return verificationStatusRest.findAll()
        }
        $rootScope.provide.cashierProvider['rulesetID'] = ruleset.id


        $rootScope.provide.cashierProvider['cloneRule'] = (domain,data) => {
            return  rest.autoWithdrawalRulesetCreate(domain, data)
        }

        // EXPERIMENTAL FEATURES ENABLED
        controller.experimentalFeatures =  $userService.isExperimentalFeatures()

        window.VuePluginRegistry.loadByPage("CloneRuleset")
    }
]);