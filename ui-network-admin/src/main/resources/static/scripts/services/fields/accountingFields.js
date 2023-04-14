'use strict';

angular.module('lithium')
	.factory('accountingFields', ['$translate', 'rest-tranta',
		function($translate, restTranta) {
			var service = {};

			service.adjust = function(currencySymbol) {
				return {
					className : "col-xs-12",
					key: 'adjustment',
					type: "ui-money-mask",
					templateOptions: {
						label: "",
						description: "",
						required: false,
						symbol: currencySymbol
					},
					expressionProperties: {
						'templateOptions.label': '"UI_NETWORK_ADMIN.PLAYER.ACCOUNTING.BALANCEADJUST.FIELDS.ADJUST.LABEL" | translate',
						'templateOptions.placeholder': '"" | translate',
						'templateOptions.description': '"UI_NETWORK_ADMIN.PLAYER.ACCOUNTING.BALANCEADJUST.FIELDS.ADJUST.DESC" | translate'
					}
				}
			}

			service.adjustControls = function(functions) {

				return {
					className : "row v-reset-row ",
					fieldGroup : [{
						className : "col-xs-2",
						type : "button",
						key : "radio-1477465160762",
						templateOptions : {
							text : '- 100',
							btnType: "default btn-block btn-sm",
							onClick : function($event) {
								functions.decrease(100);
							}
						}
					},{
						className : "col-xs-2",
						type : "button",
						key : "radio-1477465160762",
						templateOptions : {
							text : '- 10',
							btnType: "default btn-block btn-sm",
							onClick : function($event) {
								functions.decrease(10);
							}
						}
					},{
						className : "col-xs-2",
						type : "button",
						key : "radio-1477465160762",
						templateOptions : {
							text : '+ 10',
							btnType: "default btn-block btn-sm",
							onClick : function($event) {
								functions.increase(10);
							}
						}
					},{
						className : "col-xs-2",
						type : "button",
						key : "radio-1477465160762",
						templateOptions : {
							text : '+ 100',
							btnType: "default btn-block btn-sm",
							onClick : function($event) {
								functions.increase(100);
							}
						}
					}]
				}
			}

			service.transactionType = function(domainName) {
				return {
					key: "trantypeacct",
					className: "col-xs-12 form-group",
					type: "ui-select-single",
					templateOptions : {
						label: "",
						description: "",
						placeholder: "",
						required : true,
						valueProp: 'accountTypeCode',
						labelProp: 'accountTypeCode',
						optionsAttr: 'ui-options', // 'bs-options' || 'ui-options'
						ngOptions: 'ui-options',
						options: []
					},
					expressionProperties: {
						'templateOptions.label': '"UI_NETWORK_ADMIN.PLAYER.ACCOUNTING.BALANCEADJUST.FIELDS.TRANTYPE.LABEL" | translate',
						'templateOptions.placeholder': '"UI_NETWORK_ADMIN.PLAYER.ACCOUNTING.BALANCEADJUST.FIELDS.TRANTYPE.PLACEHOLDER" | translate',
						'templateOptions.description': '"UI_NETWORK_ADMIN.PLAYER.ACCOUNTING.BALANCEADJUST.FIELDS.TRANTYPE.DESC" | translate'
					},
					controller: ['$scope', function($scope) {
						restTranta.all(domainName).then(function(response) {
							$scope.to.options = response;
						});
					}]
				}
			}

			service.comment = function(key) {
				return {
					className : "col-xs-12",
					type : "textarea",
					key : key,
					templateOptions : {
						label : "",
						required : true,
						placeholder : "",
						description : "",
						maxlength: 65535
					},
					expressionProperties: {
						'templateOptions.label': '"UI_NETWORK_ADMIN.PLAYER.ACCOUNTING.BALANCEADJUST.FIELDS.COMMENT.LABEL" | translate',
						'templateOptions.placeholder': '"" | translate',
						'templateOptions.description': '"UI_NETWORK_ADMIN.PLAYER.ACCOUNTING.BALANCEADJUST.FIELDS.COMMENT.DESC" | translate'
					}
				}
			}

			service.balanceAdjust = function(domainName, keys) {
				return [
					service.transactionType(domainName),
					service.comment(keys.comment)
				]
			}

			return service;
		}

	]);