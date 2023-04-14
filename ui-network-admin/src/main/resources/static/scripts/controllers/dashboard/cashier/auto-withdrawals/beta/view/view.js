'use strict'

angular.module('lithium').controller('AutoWithdrawalRulesetViewBetaController', ['ruleset', "$rootScope",'AutoWithdrawalRulesetRest',
    function(ruleset,  $rootScope,  rest) {
        var controller = this;
        controller.ruleset = ruleset.plain();

        $rootScope.provide.cashierProvider['cloneRule'] = (domain,data) => {
            return  rest.autoWithdrawalRulesetCreate(domain, data)
        }
        $rootScope.provide.cashierProvider['rulesetID'] = ruleset.id
        window.VuePluginRegistry.loadByPage("AutoWithdrawalDetailPage")
    }
]);