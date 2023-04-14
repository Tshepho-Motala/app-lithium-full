'use strict'

angular.module('lithium').controller('AutoRestrictionRulesetViewController', ['ruleset', 'fields', 'operators', 'outcomes','events', 'AutoRestrictionRulesetRest', 'notify', 'errors',
    function(ruleset, fields, operators, outcomes, events, rest, notify, errors) {
        var controller = this;
        controller.ruleset = ruleset.plain();
        controller.fields = fields.plain();
        controller.operators = operators.plain();
        controller.outcomes = outcomes.plain();
        controller.events = events.plain();

        console.debug("ruleset", controller.ruleset);
        console.debug("fields", controller.fields);
        console.debug("operators", controller.operators);
        console.debug("outcomes", controller.outcomes);

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

        controller.getEventName = function(id) {
            if (!id && id !== 0) return null;
            for (var i = 0; i < controller.events.length; i++) {
                if (controller.events[i].id === id) return events[i].displayName;
            }
            return null;
        }

        controller.getOperatorName = function(id) {
            for (var i = 0; i < controller.operators.length; i++) {
                if (controller.operators[i].id === id) return operators[i].displayName;
            }
            return null;
        }

        controller.getOutcomeName = function(id) {
            for (var i = 0; i < controller.outcomes.length; i++) {
                if (controller.outcomes[i].id === id) return outcomes[i].displayName;
            }
            return null;
        }
    }
]);
