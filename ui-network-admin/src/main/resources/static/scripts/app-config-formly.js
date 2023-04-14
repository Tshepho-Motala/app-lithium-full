function formlyConfig(formlyConfigProvider) {

	// formlyConfigProvider.extras.errorExistsAndShouldBeVisibleExpression =
	// 'fc.$touched || form.$submitted';

	formlyConfigProvider.extras.errorExistsAndShouldBeVisibleExpression = 'form.$submitted';

	function camelize(string) {
		string = string.replace(/[-_s]+(.)?/g, function(match, chr) {
			return chr ? chr.toUpperCase() : '';
		});
		// Ensure 1st char is always lowercase
		return string.replace(/^([A-Z])/, function(match, chr) {
			return chr ? chr.toLowerCase() : '';
		});
	}

	// Generated on http://mackentoch.github.io/easyFormGenerator/
	// Will need to revisit this.
	formlyConfigProvider.extras.removeChromeAutoComplete = true;
	formlyConfigProvider
			.setType({
				name : 'richEditor',
				template : '<text-angular class="richTextAngular" ng-model="model[options.key || index]"></text-angular>'
			});
	formlyConfigProvider.setType({
		name : 'blank',
		template : '<div></div>'
	});
	formlyConfigProvider.setType({
		name : 'blank2',
		extends : 'input',
		templateUrl : 'templates/formly/blank.html'
	});
	formlyConfigProvider.setType({
		name : 'hr',
		extends : 'input',
		templateUrl : 'templates/formly/hr.html'
	});
	formlyConfigProvider.setType({
		name: 'h4',
		template: '<div><h4><b>{{options.templateOptions.label}}</b></h4></div>'
	});
	var subTitleTemplate = '<div class="row v-reset-row "><div class=""><h4 class="text-center">{{options.templateOptions.placeholder}}<h4><hr/></div></div>';
	formlyConfigProvider.setType({
		name : 'subTitle',
		template : subTitleTemplate
	});
	formlyConfigProvider.setType({
		name : 'ui-select-single',
		extends : 'select',
		templateUrl : 'templates/formly/ui-select-single.html',
		defaultOptions : function(options) {
			return {
				templateOptions : {
					dataAllowClear : true
				}
			}
		}
	});
	formlyConfigProvider.setType({
		name : 'scheduler',
		templateUrl : 'templates/formly/scheduler.html',
		defaultOptions : function(options) {
			return {
				templateOptions : {
					setup : {
						box: "default",
						title: "GLOBAL.SCHEDULER.TITLE"
					}
				}
			}
		}
	});
	formlyConfigProvider.setType({
		name : 'image-upload',
		templateUrl : 'templates/formly/image-upload.html',
		controller: ['$scope', 'Lightbox', function($scope, Lightbox) {
			$scope.errorHandler = function(event, reader, file, fileList, fileObjs, object) {
				console.log("An error occurred while reading file: "+file.name);
				console.log(event, reader, file, fileList, fileObjs, object);
				reader.abort();
			};
			$scope.openLightBox = function(src) {
				var image = [{
					'url': src
				}]
				Lightbox.openModal(image, 0);
			}
		}]
	});

	formlyConfigProvider.setType({
		name : 'accessRule',
		templateUrl : 'templates/formly/accessRule.html',
		controller: ['$scope', '$stateParams', 'accessRulesRest', function($scope, $stateParams, accessRulesRest) {
			accessRulesRest.findByDomainName($stateParams.domainName).then(function(response) {
				$scope.to.options = response.plain();
			});
		}]
	});
	formlyConfigProvider.setType({
		name : 'ui-number-mask',
		extends : 'input',
		templateUrl : 'templates/formly/ui-number-mask.html'
	});
	formlyConfigProvider.setType({
		name : 'ui-time-mask',
		extends : 'input',
		templateUrl : 'templates/formly/ui-time-mask.html'
	});
	formlyConfigProvider.setType({
		name : 'timerange',
		extends : 'input',
		templateUrl : 'templates/formly/timerange.html',
		defaultOptions : function(options) {
			if (options.data.noValidation) {
				return;
			}
			return {
				templateOptions : {
					placeholder : '13:00',
					placeholder2 : '16:00'
//				},
//				validators : {
//					pattern: {
//						expression: function($viewValue, $modelValue, scope) {
//							console.log($viewValue, $modelValue, scope);
//							if (angular.isDefined(scope.model[scope.options.key])) {
//								var range = scope.model[scope.options.key];
////								console.log(range.start);
////								console.log(range.end);
//								if (range.end < range.start) {
//									console.log("Wrong");
//									return false;
//								}
//								return true;
//							}
////							return true;
//						},
//						message: '"UI_NETWORK_ADMIN.GAME.FIELDS.NAME.PATTERN" | translate'
//					}
				}
			};
		}
	});
	formlyConfigProvider.setType({
		name : 'ui-percentage-mask',
		extends : 'input',
		templateUrl : 'templates/formly/ui-percentage-mask.html'
	});
	formlyConfigProvider.setType({
		name : 'uib-typeahead',
		extends : 'input',
		templateUrl : 'templates/formly/uib-typeahead.html',
		defaultOptions : function(options) {
			return {
				templateOptions : {
					editable : false
				}
			}
		}
	});
	formlyConfigProvider.setType({
		name : 'uib-btn-radio',
		// extends : 'select',
		templateUrl : 'templates/formly/uib-btn-radio.html',
		controller: ['$scope', function($scope) {
			$scope.setValue = function() {
				var key = $scope.options.key;
				var nestedKey = null;
				if (key.indexOf('.') !== -1) {
					nestedKey = key.split('.');
				}
				if (nestedKey != null) {
					var value = null;
					for (var i = 0; i < nestedKey.length; i++) {
						if (value == null) {
							value = $scope.model[nestedKey[i]];
						} else {
							value = value[nestedKey[i]];
						}
					}
					$scope.value = value;
				} else {
					$scope.value = $scope.model[key];
				}
			}

			$scope.setValue();

			$scope.$watch(function() { return $scope.model }, function(newValue, oldValue) {
				if (newValue != oldValue) {
					$scope.setValue();
				}
			}, true);
		}]
	});
	formlyConfigProvider.setType({
		name: 'btn-radio',
		templateUrl: 'templates/formly/btn-radio.html',
		controller: ['$scope', function($scope) {
			$scope.setValue = function() {
				var key = $scope.options.key;
				if ($scope.model[key] !== undefined) {
					$scope.value = $scope.model[key];
				}
			}

			$scope.init = function () {
				$scope.value = 0;
			};

			$scope.init();

			$scope.$watch(function() { return $scope.model }, function(newValue, oldValue) {
				console.log($scope);
				if (newValue !== oldValue) {
					$scope.setValue();
				}
			}, true);
		}]});
	formlyConfigProvider.setType({
		name: 'ptl-btn-radio',
		templateUrl: 'templates/formly/ptl-btn-radio.html',
		controller: ['$scope', function($scope) {
			$scope.setValue = function() {
				var key = $scope.options.key;
				$scope.value = $scope.model[key];
			}

			$scope.init = function () {
				if ($scope.model.currentPlayTimeLimit !== undefined) {
					if ($scope.model.currentPlayTimeLimit.granularity === 3) {
						$scope.value = 0;
					} else if ($scope.model.currentPlayTimeLimit.granularity === 4) {
						$scope.value = 1;
					} else if ($scope.model.currentPlayTimeLimit.granularity === 2) {
						$scope.value = 2;
					}
				} else {
					$scope.value = 0;
				}
			};

			$scope.init();

			$scope.$watch(function() { return $scope.model }, function(newValue, oldValue) {
				if (newValue !== oldValue) {
					$scope.setValue();
				}
			}, true);
	}]});
	formlyConfigProvider.setType({
		name: 'ptl-btn-radio-2',
		templateUrl: 'templates/formly/ptl-btn-radio.html',
		controller: ['$scope', function($scope) {
			$scope.init = function () {
				$scope.value = 0;
			};

			$scope.init();
	}]});

	formlyConfigProvider.setType({
		name : 'currency',
		templateUrl : 'templates/formly/currency.html',
		defaultOptions : {
			templateOptions : {
				btnType : 'default',
				type : 'button',
				idxLabel : ''
			},
			extras : {
				skipNgModelAttrsManipulator : true
			// <-- perf optimazation because this type has no ng-model
			}
		},
		controller : [ '$scope', function($scope) {
			if (!$scope.model[$scope.options.key]) $scope.model[$scope.options.key] = 5;
		}]
	});

	formlyConfigProvider.setType({
		name : 'uib-btn-checkbox',
		// extends : 'select',
		templateUrl : 'templates/formly/uib-btn-checkbox.html'
	});
	formlyConfigProvider.setType({
		name : 'granularity',
		templateUrl : 'templates/formly/granularity.html',
		controller : [ '$scope', function($scope) {
			if (!$scope.model[$scope.options.key]) $scope.model[$scope.options.key] = 5;
		}]
	});
	formlyConfigProvider.setType({
		name : 'granularityfor',
		templateUrl : 'templates/formly/granularityfor.html',
		controller : [ '$scope', function($scope) {
			if (!$scope.model[$scope.options.key]) $scope.model[$scope.options.key] = 5;
		}]
	});
	formlyConfigProvider.setType({
		name : 'granularities',
		templateUrl : 'templates/formly/granularities.html',
		controller : [ '$scope', function($scope) {
			if (!$scope.model[$scope.options.key]) $scope.model[$scope.options.key] = 3;
		}]
	});
	formlyConfigProvider.setType({
		name : 'weekday',
		extends: "checkbox",
		templateUrl : 'templates/formly/weekday.html'
	});
	formlyConfigProvider.setType({
		name : 'masked-input',
		extends : 'input',
		templateUrl : 'templates/formly/masked-input.html'
	});
	formlyConfigProvider.setType({
		name : 'ui-money-mask',
		extends : 'input',
		templateUrl : 'templates/formly/ui-money-mask.html'
	});
	formlyConfigProvider.setType({
		name : 'checkbox2',
		templateUrl : 'templates/formly/checkbox2.html'
	});
	formlyConfigProvider.setType({
		name : 'roles-display',
		templateUrl : 'templates/formly/roles-display.html'
	}); // <span>{{'UI_NETWORK_ADMIN.PLAYER.REALITYCHECK.TITLE'|translate}}</span>
	var uiDirectWithdrawalWarningBlockTemplate = '<div style="margin-bottom: 5px; border: 1px solid rgba(0, 0, 0, 0.2);">'
		+ '<span style="line-height:inherit; margin: 10px;" class="label label-danger">WARNING</span><br/>'
		+ '<div style="line-height:inherit; text-align: justify; margin: 5px 10px 10px;">'
		+ '<span style="color: rgba(0, 0, 0, 0.6);">' + '{{\'UI_NETWORK_ADMIN.DIRECT_WITHDRAWAL.WARNINGS.DW_WARNING_DESC_1\'|translate}}' + '</span><br/>'
		+ '<span style="color: rgba(0, 0, 0, 0.6);">' + '{{\'UI_NETWORK_ADMIN.DIRECT_WITHDRAWAL.WARNINGS.DW_WARNING_DESC_2\'|translate}}' + '</span><br/>'
		+ '</div>' + '</div>';
	formlyConfigProvider.setType({
		name : 'ui-direct-withdrawal-warning-block-template',
		template : uiDirectWithdrawalWarningBlockTemplate
	})
	var uiAccountVerificationStatusBlockTemplate = '<div lit-if-permission="PLAYER_VALIDATE_AGE,PLAYER_VALIDATE_ADDRESS" style="margin-bottom: 5px; border: 2px solid;">'
		+ '<h4 lit-if-permission="PLAYER_VALIDATE_AGE,Pll origiLAYER_VALIDATE_ADDRESS" style="line-height:inherit; margin: 10px; font-weight: bold;">Account Verification Status</h4>'
		+ '</div>';
	formlyConfigProvider.setType({
		name : 'ui-account-verification-status-block-template',
		template : uiAccountVerificationStatusBlockTemplate
	})
	var uiSelectSingleSelect2 = '<ui-select data-ng-model="model[options.key]" data-required="{{to.required}}" data-disabled="{{to.disabled}}" theme="select2">'
			+ '<ui-select-match placeholder="{{to.placeholder}}">{{$select.selected[to.labelProp]}}</ui-select-match>'
			+ '<ui-select-choices data-repeat="option[to.valueProp] as option in to.options | filter: $select.search">'
			+ '<div ng-bind-html="option[to.labelProp] | highlight: $select.search"></div>'
			+ '</ui-select-choices>' + '</ui-select>';
	formlyConfigProvider.setType({
		name : 'ui-select-single-select2',
		extends : 'select',
		template : uiSelectSingleSelect2
	});
	var uiSelectSingleSearch = '<ui-select data-ng-model="model[options.key]" data-required="{{to.required}}" data-disabled="{{to.disabled}}" theme="bootstrap">'
			+ '        <ui-select-match placeholder="{{to.placeholder}}">{{$select.selected[to.labelProp]}}</ui-select-match>'
			+ '        <ui-select-choices data-repeat="option[to.valueProp] as option in to.options | filter: $select.search" data-refresh="to.refresh($select.search, options)" data-refresh-delay="{{to.refreshDelay}}">'
			+ '          <div ng-bind-html="option[to.labelProp] | highlight: $select.search"></div>'
			+ '          <div><small>{{option.geometry.location.lat}}, {{option.geometry.location.lng}}</small></div>'
			+ '        </ui-select-choices>' + '      </ui-select>';
	formlyConfigProvider.setType({
		name : 'ui-select-single-search',
		extends : 'select',
		template : uiSelectSingleSearch
	});
	var uiSelectMultiple = '<ui-select allow-clear multiple ng-model="model[options.key]" ng-required="{{to.required}}" ng-disabled="{{to.disabled}}" theme="bootstrap" close-on-select="true">'
			+ '        <ui-select-match placeholder="{{to.placeholder}}">{{$item[to.labelProp]}}</ui-select-match>'
			+ '        <ui-select-choices group-by="\'{{to.groupBy}}\'" data-repeat="option in to.options | filter: $select.search track by option[to.valueProp]">'
			+ '          <div ng-bind-html="option[to.labelProp] | highlight: $select.search"></div>'
			+ '          <small><span ng-bind-html="option[to.itemExtra] | highlight: $select.search"></span></small>'
			+ '        </ui-select-choices>' + '      </ui-select>';
	formlyConfigProvider.setType({
		name : 'ui-select-multiple',
		extends : 'select',
		template : uiSelectMultiple
	});
	var conditionalInput =
		'<div class="form-group" ng-class="{\'has-error\': showError}">' +
		'<label class="control-label" ng-if="to.label">{{to.label}}</label>' +
		'<div class="d-flex">' +
		'<ui-select search-enabled="false" ng-model="model[options.key].operator" ng-required="{{to.required}}" theme="bootstrap" close-on-select="true" style="width: 100px;">' +
		'<ui-select-match placeholder="{{to.placeholder}}">{{$select.selected}}</ui-select-match>' +
		'<ui-select-choices data-repeat="option in to.options">' +
		'<div ng-bind-html="option"></div>' +
		'</ui-select-choices></ui-select>' +
		'<input  id="current-account-balance" ' +
		'name="current-account-balance" ng-model="model[options.key].value" type="number"' +
		'class="form-control ng-pristine ng-valid ng-isolate-scope ng-empty ng-touched ">' +
		'<span  ' +
		'tooltip title="{{to.tooltip}}">' +
		'<button type="button" class="btn btn-default mr-0" ng-disabled="to.disabled">' +
		'<i class="glyphicon glyphicon-question-sign"></i>' +
		'</button></span></div></div>';
	formlyConfigProvider.setType({
		name: 'conditional-input',
		template: conditionalInput,
	});

	// angular UI date picker
	// thx Kent C. Dodds
	var attributes = [ 'date-disabled', 'custom-class', 'show-weeks',
			'starting-day', 'init-date', 'min-mode', 'max-mode', 'format-day',
			'format-month', 'format-year', 'format-day-header',
			'format-day-title', 'format-month-title', 'year-range',
			'shortcut-propagation', 'datepicker-popup', 'show-button-bar',
			'current-text', 'clear-text', 'close-text',
			'close-on-date-selection', 'datepicker-append-to-body' ];

	var bindings = [ 'datepicker-mode', 'min-date', 'max-date' ];
	var ngModelAttrs = {};

	angular.forEach(attributes, function(attr) {
		ngModelAttrs[camelize(attr)] = {
			attribute : attr
		};
	});
	angular.forEach(bindings, function(binding) {
		ngModelAttrs[camelize(binding)] = {
			bound : binding
		};
	});

	formlyConfigProvider
			.setType({
				name : "ckeditor",
				template : '<div disabled="{{options.disabled}}" ckeditor="to.ckoptions" ng-model="model[options.key || index]"></div>',
				wrapper : [ 'bootstrapLabel', 'bootstrapHasError' ]
			});

	formlyConfigProvider.setType({
		name : 'timepicker',
		template : '<uib-timepicker ng-model="model[options.key]" show-meridian="((to.datepickerOptions.showMeridian != null)? to.datepickerOptions.showMeridian: true)"></uib-timepicker>',
		wrapper : [ 'bootstrapLabel', 'bootstrapHasError' ],
		defaultOptions : {
			ngModelAttrs : ngModelAttrs,
			templateOptions : {
				datepickerOptions : {}
			}
		}
	});

	formlyConfigProvider.setType({
		name: 'readonly-input',
		template: '<input type="text" ng-model="model[to.key]" class="form-control" readonly="readonly">',
		wrapper: [ 'bootstrapLabel', 'bootstrapHasError' ],
		controller: [ '$scope', function($scope) {
			console.log($scope);
		}]
	});

	formlyConfigProvider
			.setType({
				name : 'disabled-input',
				template : '<div class="input-group">'
						+ '	<input type="text" ng-model="model[to.key]" class="form-control" ng-disabled="model[to.keyDisabledInputCheck] === false">'
						+ '	<span class="input-group-addon">'
						+ '		<input type="checkbox" ng-model="model[to.keyDisabledInputCheck]" ng-change="override(model[to.keyDisabledInputCheck])" class="btn btn-default">'
						+ '	</span>' + '</div>',
				wrapper : [ 'bootstrapLabel', 'bootstrapHasError' ],
				controller : [ '$scope', function($scope) {
					$scope.model['wagerReqCheck'] = false;
					$scope.model['wagerReqCheck2'] = false; // HACKED FOR DEMO
					$scope.override = function($event) {
					}
				} ]
			});

	formlyConfigProvider.setType({
		name : 'datepicker',
		templateUrl : 'templates/formly/ui-bootstrap-datepicker.html',
		wrapper : [ 'bootstrapLabel', 'bootstrapHasError' ],
		controller : [ '$scope', function($scope) {
			$scope.datepicker = {};
			$scope.datepicker.opened = false;
			$scope.datepicker.open = function($event) {
				$scope.datepicker.opened = !$scope.datepicker.opened;
			};
		} ],
		defaultOptions : {
			ngModelAttrs : ngModelAttrs,
			templateOptions : {
				onFocus : function($viewValue, $modelValue, scope) {
					scope.to.isOpen = !scope.to.isOpen;
				},
				datepickerOptions : {
					format : 'MM.dd.yyyy HH:mm Z',
					initDate : new Date(),
					ngModelOptions : 'timezone'
				}
			}
		}
	});

	formlyConfigProvider
			.setWrapper([
					{
						template : [
//								'{{options.formControl.$error}}',
								'<span class="fa fa-spinner fa-spin form-input-loader pull-right" ng-show="to.loading || options.formControl.$pending"></span>',
								'<formly-transclude></formly-transclude>',
								'<div class="validation alert alert-error" ng-if="options.validation.errorExistsAndShouldBeVisible"',
								'ng-messages="options.formControl.$error" ng-messages-multiple>',
								'<div ng-messages-include="templates/validation-messages.html"></div>',
								'<div ng-message="{{::name}}" ng-repeat="(name, message) in ::options.validation.messages">',
								'{{message(options.formControl.$viewValue, options.formControl.$modelValue, this) | translate}}',
								'</div>', '</div>', '</div>' ].join(' ')
					},
					{
						template : [
								'<div class="checkbox formly-template-wrapper-for-checkboxes form-group">',
								'<label for="{{::id}}">',
								'<formly-transclude></formly-transclude>',
								'</label>', '</div>' ].join(' '),
						types : 'checkbox'
					} ]);

	// See http://angular-formly.com/#/example/other/matching-two-fields
	formlyConfigProvider
			.setType({
				name : 'matchField',
				apiCheck : function() {
					return {
						data : {
							fieldToMatch : formlyExampleApiCheck.string
						}
					}
				},
				apiCheckOptions : {
					prefix : 'matchField type'
				},
				defaultOptions : function matchFieldDefaultOptions(options) {
					return {
						extras : {
							validateOnModelChange : true
						},
						expressionProperties : {
						// 'templateOptions.disabled': function(viewValue,
						// modelValue, scope) {
						// var matchField = find(scope.fields, 'key',
						// options.data.fieldToMatch);
						// if (!matchField) {
						// throw new Error('Could not find a field for the key '
						// + options.data.fieldToMatch);
						// }
						// var model = options.data.modelToMatch || scope.model;
						// var originalValue = model[options.data.fieldToMatch];
						// var invalidOriginal = matchField.formControl &&
						// matchField.formControl.$invalid;
						// return !originalValue || invalidOriginal;
						// }
						},
						validators : {
							fieldMatch : {
								expression : function(viewValue, modelValue,
										fieldScope) {
									var value = modelValue || viewValue;
									var model = options.data.modelToMatch
											|| fieldScope.model;
									var modelValue = model[options.data.fieldToMatch];
									if ((!value) && (!modelValue))
										return true;
									return value === modelValue;
								},
								message : options.data.matchFieldMessage
										|| '"Must match"'
							}
						}
					};

					function find(array, prop, value) {
						var foundItem;
						array.some(function(item) {
							if (item[prop] === value) {
								foundItem = item;
							}
							return !!foundItem;
						});
						return foundItem;
					}
				}
			});

	formlyConfigProvider.setType({
		name : 'multiselect',
		extends : 'select',
		defaultOptions : {
			ngModelAttrs : {
				'true' : {
					value : 'multiple'
				}
			}
		}
	})

	formlyConfigProvider.setType({
		name : 'editable',
		defaultOptions : function(options) {
			return {
				extras : {
					validateOnModelChange : true
				},
				/* The scope in this case, is the field's scope. */
				expressionProperties : {
					'templateOptions.disabled' : function(viewValue, modelValue, scope) {
						return scope.formState.readOnly;
					}
				}
			};
		}
	});

	formlyConfigProvider
			.setType({
				name : 'timezone-selector',
				template : '<div class="form-group">'
						+ '<label class="control-label">{{to.label}}</label><br/>'
						+ '<timezone-selector ng-model="model[options.key]" class="form-control" display-utc="true" sort-by="offset" show-local="{{to.showLocal}}" set-local="{{to.setLocal}}" width="{{to.width}}">'
						+ '</div>',
				wrapper : [ 'bootstrapHasError' ]
			});

	formlyConfigProvider
			.setType({
				name : 'toggle-switch',
				template : '<toggle-switch ng-model="model[options.key]" on-label="{{to.onLabel}}" off-label="{{to.offLabel}}"></toggle-switch>',
				wrapper : [ 'bootstrapHasError' ]
			});

	formlyConfigProvider.setWrapper([ {
		name : 'fullDateWrapper',
		templateUrl : 'templates/formly/fullDateWrapper.html'
	} ]);

	formlyConfigProvider.setType({
		name : "fullDate",
		templateUrl : 'templates/formly/fullDate.html',
		wrapper : 'fullDateWrapper',
		controller : [ '$scope', function($scope) {
			$scope.enable = enable;
			function enable(selected) {
				$scope.options.templateOptions.disabled = !selected;
				// $scope.options.templateOptions.readOnly = !selected;
			}

			$scope.datepicker = {};
			$scope.datepicker.opened = false;
			$scope.datepicker.open = function($event) {
				$scope.datepicker.opened = !$scope.datepicker.opened;
			};
		} ],
		defaultOptions : {
			templateOptions : {
				onFocus : function($viewValue, $modelValue, scope) {
					scope.to.isOpen = !scope.to.isOpen;
				},
				datepickerOptions : {
					format : 'MM/dd/yyyy',
					initDate : new Date()
				}
			}
		}
	});

	formlyConfigProvider.setType({
		name : "userSearch",
		templateUrl : 'templates/formly/userSearch.html',
		controller : ['$scope', "UserRest", function($scope, userRest) {
			$scope.searchPlayer = function(domainName, searchValue) {
				return userRest.search(domainName, searchValue).then(function(response) {
					return response.plain();
				});
			}
		}]
	});

	formlyConfigProvider.setType({
		name : 'examplewell',
		templateUrl : 'templates/formly/bonus/examplewell.html',
	});
	formlyConfigProvider.setType({
		name : 'table',
		templateUrl : 'templates/formly/table.html',
		controller : function($scope) {
			console.log('table')
		}
	});

	formlyConfigProvider.setWrapper([{
		name : 'requirementsWrapper',
		templateUrl : 'templates/formly/bonus/requirementsWrapper.html'
	}]);


	formlyConfigProvider.setType({
		name : "instantRewardGames",
		templateUrl : 'templates/formly/bonus/repeatSection.html',
		controller : function($scope) {
			$scope.formOptions = {
				formState : $scope.formState
			};
			$scope.remove = remove;
			$scope.addNew = addNew;
			$scope.copyFields = copyFields;

			function remove(element) {
				console.log(element);
				for (var i = 0; i < $scope.model['bonusRulesInstantReward'].length; ++i) {
					if ($scope.model['bonusRulesInstantReward'][i] === element) {
						$scope.model['bonusRulesInstantReward'].splice(i, 1);
					}
				}
			}
			function copyFields(fields) {
				fields = angular.copy(fields);
				addRandomIds(fields);
				return fields;
			}
			function addNew() {
				$scope.model[$scope.options.key] = $scope.model[$scope.options.key] || [];
				var repeatsection = $scope.model[$scope.options.key];
				var lastSection = repeatsection[repeatsection.length - 1];
				var newsection = {};
				if (lastSection) {
					newsection = angular.copy(lastSection);
					newsection.casinoFreeBetGames = [];
				}
				repeatsection.push(newsection);
			}
			function addRandomIds(fields) {
				var unique = 0;
				unique = getRandomInt(0, 9999);
				angular.forEach(fields, function(field, index) {
					if (field.fieldGroup) {
						addRandomIds(field.fieldGroup);
						return; // fieldGroups don't need an ID
					}
					if (field.templateOptions && field.templateOptions.fields) {
						addRandomIds(field.templateOptions.fields);
					}
					field.id = field.id || (field.key + '_' + index + '_' + unique + getRandomInt(0, 9999));
				});
			}
			function getRandomInt(min, max) {
				return Math.floor(Math.random() * (max - min)) + min;
			}
		}
	});

	formlyConfigProvider.setType({
		name : "instantRewardFreespinGames",
		templateUrl : 'templates/formly/bonus/repeatSection.html',
		controller : function($scope) {
			$scope.formOptions = {
				formState : $scope.formState
			};
			$scope.remove = remove;
			$scope.addNew = addNew;
			$scope.copyFields = copyFields;

			function remove(element) {
				console.log(element);
				for (var i = 0; i < $scope.model['bonusRulesInstantRewardFreespin'].length; ++i) {
					if ($scope.model['bonusRulesInstantRewardFreespin'][i] === element) {
						$scope.model['bonusRulesInstantRewardFreespin'].splice(i, 1);
					}
				}
			}
			function copyFields(fields) {
				fields = angular.copy(fields);
				addRandomIds(fields);
				return fields;
			}
			function addNew() {
				$scope.model[$scope.options.key] = $scope.model[$scope.options.key] || [];
				var repeatsection = $scope.model[$scope.options.key];
				var lastSection = repeatsection[repeatsection.length - 1];
				var newsection = {};
				if (lastSection) {
					newsection = angular.copy(lastSection);
					newsection.casinoFreeBetGames = [];
				}
				repeatsection.push(newsection);
			}
			function addRandomIds(fields) {
				var unique = 0;
				unique = getRandomInt(0, 9999);
				angular.forEach(fields, function(field, index) {
					if (field.fieldGroup) {
						addRandomIds(field.fieldGroup);
						return; // fieldGroups don't need an ID
					}
					if (field.templateOptions && field.templateOptions.fields) {
						addRandomIds(field.templateOptions.fields);
					}
					field.id = field.id || (field.key + '_' + index + '_' + unique + getRandomInt(0, 9999));
				});
			}
			function getRandomInt(min, max) {
				return Math.floor(Math.random() * (max - min)) + min;
			}
		}
	});


	formlyConfigProvider.setType({
		name : "freespinGames",
		templateUrl : 'templates/formly/bonus/repeatSection.html',
		controller : function($scope) {
			$scope.formOptions = {
				formState : $scope.formState
			};
			$scope.remove = remove;
			$scope.addNew = addNew;
			$scope.copyFields = copyFields;

			function remove(element) {
				console.log(element);
				for (var i = 0; i < $scope.model['bonusRulesFreespins'].length; ++i) {
					if ($scope.model['bonusRulesFreespins'][i] === element) {
						$scope.model['bonusRulesFreespins'].splice(i, 1);
					}
				}
			}
			function copyFields(fields) {
				fields = angular.copy(fields);
				addRandomIds(fields);
				return fields;
			}
			function addNew() {
				$scope.model[$scope.options.key] = $scope.model[$scope.options.key] || [];
				var repeatsection = $scope.model[$scope.options.key];
				var lastSection = repeatsection[repeatsection.length - 1];
				var newsection = {};
				if (lastSection) {
					newsection = angular.copy(lastSection);
					newsection.freespins = 0;
					newsection.wagerRequirements = 0;
					newsection.freespinsGames = [];
				}
				repeatsection.push(newsection);
			}
			function addRandomIds(fields) {
				var unique = 0;
				unique = getRandomInt(0, 9999);
				angular.forEach(fields, function(field, index) {
					if (field.fieldGroup) {
						addRandomIds(field.fieldGroup);
						return; // fieldGroups don't need an ID
					}
					if (field.templateOptions && field.templateOptions.fields) {
						addRandomIds(field.templateOptions.fields);
					}
					field.id = field.id || (field.key + '_' + index + '_' + unique + getRandomInt(0, 9999));
				});
			}
			function getRandomInt(min, max) {
				return Math.floor(Math.random() * (max - min)) + min;
			}
		}
	});

	formlyConfigProvider.setType({
		name : "casinoChip",
		templateUrl : 'templates/formly/bonus/repeatSection.html',
		controller : function($scope) {
			$scope.formOptions = {
				formState : $scope.formState
			};
			$scope.remove = remove;
			$scope.addNew = addNew;
			$scope.copyFields = copyFields;

			function remove(element) {
				console.log(element);
				for (var i = 0; i < $scope.model['bonusRulesCasinoChip'].length; ++i) {
					if ($scope.model['bonusRulesCasinoChip'][i] === element) {
						$scope.model['bonusRulesCasinoChip'].splice(i, 1);
					}
				}
			}
			function copyFields(fields) {
				fields = angular.copy(fields);
				addRandomIds(fields);
				return fields;
			}
			function addNew() {
				$scope.model[$scope.options.key] = $scope.model[$scope.options.key] || [];
				var repeatsection = $scope.model[$scope.options.key];
				var lastSection = repeatsection[repeatsection.length - 1];
				var newsection = {};
				if (lastSection) {
					newsection = angular.copy(lastSection);
					newsection.casinoFreeBetGames = [];
				}
				repeatsection.push(newsection);
			}
			function addRandomIds(fields) {
				var unique = 0;
				unique = getRandomInt(0, 9999);
				angular.forEach(fields, function(field, index) {
					if (field.fieldGroup) {
						addRandomIds(field.fieldGroup);
						return; // fieldGroups don't need an ID
					}
					if (field.templateOptions && field.templateOptions.fields) {
						addRandomIds(field.templateOptions.fields);
					}
					field.id = field.id || (field.key + '_' + index + '_' + unique + getRandomInt(0, 9999));
				});
			}
			function getRandomInt(min, max) {
				return Math.floor(Math.random() * (max - min)) + min;
			}
		}
	});

	formlyConfigProvider.setType({
		name : "depositRequirements",
		templateUrl : 'templates/formly/bonus/repeatSection.html',
		// wrapper: 'requirementsWrapper',
		controller : function($scope) {
			$scope.formOptions = {
				formState : $scope.formState
			};
			$scope.remove = remove;
			$scope.addNew = addNew;
			$scope.copyFields = copyFields;

			function remove(element) {
				for (var i = 0; i < $scope.model['depositRequirements'].length; ++i) {
					if ($scope.model['depositRequirements'][i] === element) {
						$scope.model['depositRequirements'].splice(i, 1);
					}
				}
			}
			function copyFields(fields) {
				fields = angular.copy(fields);
				addRandomIds(fields);
				return fields;
			}
			function addNew() {
				$scope.model[$scope.options.key] = $scope.model[$scope.options.key] || [];
				var repeatsection = $scope.model[$scope.options.key];
				var lastSection = repeatsection[repeatsection.length - 1];
				var newsection = {};
				if (lastSection) {
					newsection = angular.copy(lastSection);
					newsection.id = -1;
					newsection.bonusPercentage = newsection.bonusPercentage;
					newsection.minDeposit = Math.round(newsection.maxDeposit * 100);
					newsection.maxDeposit = Math.round(newsection.maxDeposit * 100 * 2);
				}
				repeatsection.push(newsection);
			}
			function addRandomIds(fields) {
				var unique = 0;
				unique = getRandomInt(0, 9999);
				angular.forEach(fields, function(field, index) {
					if (field.fieldGroup) {
						addRandomIds(field.fieldGroup);
						return; // fieldGroups don't need an ID
					}
					if (field.templateOptions && field.templateOptions.fields) {
						addRandomIds(field.templateOptions.fields);
					}
					field.id = field.id || (field.key + '_' + index + '_' + unique + getRandomInt(0, 9999));
				});
			}
			function getRandomInt(min, max) {
				return Math.floor(Math.random() * (max - min)) + min;
			}
		}
	});

	formlyConfigProvider.setType({
		name : 'checkboxWrapped',
		wrapper : 'requirementsWrapper',
		extends : 'input',
		link : function(scope, el, attrs, ctrl) {
			scope.formState.readOnly = true;
		},
		controller : [ '$scope', function($scope) {
			$scope.enable = enable;
			function enable(selected) {
				$scope.options.templateOptions.disabled = !selected;
			}
		} ]
	});

	formlyConfigProvider
			.setType({
				name : "signupRequirements",
				templateUrl : 'templates/formly/bonus/repeatSection.html',
				wrapper : 'requirementsWrapper',
				controller : function($scope) {
					$scope.formOptions = {
						formState : $scope.formState
					};
					$scope.enable = enable;
					$scope.addNew = addNew;
					$scope.copyFields = copyFields;

					function enable(selected) {
						if (selected) {
							addNew();
						} else {
							$scope.model['signupRequirements'] = [];
						}
					}

					function copyFields(fields) {
						fields = angular.copy(fields);
						addRandomIds(fields);
						return fields;
					}
					function addNew() {
						$scope.model[$scope.options.key] = $scope.model[$scope.options.key]
								|| [];
						var repeatsection = $scope.model[$scope.options.key];
						console.log(repeatsection, $scope.options.key,
								$scope.model);
						var lastSection = repeatsection[repeatsection.length - 1];
						var newsection = {};
						if (lastSection) {
							newsection = angular.copy(lastSection);
						}
						repeatsection.push(newsection);
					}
					function addRandomIds(fields) {
						var unique = 0;
						unique = getRandomInt(0, 9999);
						console.log("random", unique);
						angular
								.forEach(
										fields,
										function(field, index) {
											if (field.fieldGroup) {
												addRandomIds(field.fieldGroup);
												return; // fieldGroups don't
														// need an ID
											}
											if (field.templateOptions
													&& field.templateOptions.fields) {
												addRandomIds(field.templateOptions.fields);
											}

											field.id = field.id
													|| (field.key + '_' + index
															+ '_' + unique + getRandomInt(
															0, 9999));
										});
					}
					function getRandomInt(min, max) {
						return Math.floor(Math.random() * (max - min)) + min;
					}
				}
			});

	formlyConfigProvider
			.setType({
				name : "applicableGames",
				templateUrl : 'templates/formly/bonus/repeatSection.html',
				wrapper : 'requirementsWrapper',
				controller : function($scope) {
					$scope.formOptions = {
						formState : $scope.formState
					};
					$scope.addNew = addNew;
					$scope.enable = enable;
					$scope.copyFields = copyFields;

					function enable(selected) {
						if (selected) {
							addNew();
							$scope.depositRequirementsAdd = true;
						} else {
							$scope.depositRequirementsAdd = false;
							$scope.model['applicableGames'] = [];
						}
					}

					function copyFields(fields) {
						fields = angular.copy(fields);
						addRandomIds(fields);
						return fields;
					}
					function addNew() {
						$scope.model[$scope.options.key] = $scope.model[$scope.options.key]
								|| [];
						var repeatsection = $scope.model[$scope.options.key];
						console.log(repeatsection, $scope.options.key,
								$scope.model);
						var lastSection = repeatsection[repeatsection.length - 1];
						var newsection = {};
						if (lastSection) {
							newsection = angular.copy(lastSection);
						}
						repeatsection.push(newsection);
					}
					function addRandomIds(fields) {
						var unique = 0;
						unique = getRandomInt(0, 9999);
						console.log("random", unique);
						angular
								.forEach(
										fields,
										function(field, index) {
											if (field.fieldGroup) {
												addRandomIds(field.fieldGroup);
												return; // fieldGroups don't
														// need an ID
											}
											if (field.templateOptions
													&& field.templateOptions.fields) {
												addRandomIds(field.templateOptions.fields);
											}

											field.id = field.id
													|| (field.key + '_' + index
															+ '_' + unique + getRandomInt(
															0, 9999));
										});
					}
					function getRandomInt(min, max) {
						return Math.floor(Math.random() * (max - min)) + min;
					}
				}
			});

	formlyConfigProvider
			.setType({
				name : "freeSpins",
				templateUrl : 'templates/formly/bonus/repeatSection.html',
				wrapper : 'requirementsWrapper',
				controller : function($scope) {
					$scope.formOptions = {
						formState : $scope.formState
					};
					$scope.addNew = addNew;
					$scope.enable = enable;
					$scope.copyFields = copyFields;

					function enable(selected) {
						if (selected) {
							addNew();
							$scope.depositRequirementsAdd = true;
						} else {
							$scope.depositRequirementsAdd = false;
							$scope.model['freeSpins'] = [];
						}
					}

					function copyFields(fields) {
						fields = angular.copy(fields);
						addRandomIds(fields);
						return fields;
					}
					function addNew() {
						$scope.model[$scope.options.key] = $scope.model[$scope.options.key]
								|| [];
						var repeatsection = $scope.model[$scope.options.key];
						console.log(repeatsection, $scope.options.key,
								$scope.model);
						var lastSection = repeatsection[repeatsection.length - 1];
						var newsection = {};
						if (lastSection) {
							newsection = angular.copy(lastSection);
						}
						repeatsection.push(newsection);
					}
					function addRandomIds(fields) {
						var unique = 0;
						unique = getRandomInt(0, 9999);
						console.log("random", unique);
						angular
								.forEach(
										fields,
										function(field, index) {
											if (field.fieldGroup) {
												addRandomIds(field.fieldGroup);
												return; // fieldGroups don't
														// need an ID
											}
											if (field.templateOptions
													&& field.templateOptions.fields) {
												addRandomIds(field.templateOptions.fields);
											}

											field.id = field.id
													|| (field.key + '_' + index
															+ '_' + unique + getRandomInt(
															0, 9999));
										});
					}
					function getRandomInt(min, max) {
						return Math.floor(Math.random() * (max - min)) + min;
					}
				}
			});

	formlyConfigProvider
			.setType({
				name : 'repeatSection',
				template : '<div class="{{hideRepeat}}">'
						+ '<div class="col-md-8">'
						+ '<div class="repeatsection" ng-repeat="element in model[options.key]" ng-init="fields = copyFields(to.fields)">'
						+ '<div class="row v-reset-row ">'
						+ '<formly-form fields="fields" model="element" form="form" options="controller.options">'
						+ '</formly-form>'
						+ '</div>'
						+ '</div>'
						+ '<div class="row v-reset-row ">'
						+ '<button type="button" class="btn btn-primary" ng-click="addNew()" >{{to.btnText}}</button>'
						+ '</div>' + '</div>' + '</div>',
				controller : function($scope) {
					$scope.formOptions = {
						formState : $scope.formState
					};
					$scope.addNew = addNew;
					$scope.copyFields = copyFields;

					function copyFields(fields) {
						fields = angular.copy(fields);
						addRandomIds(fields);
						return fields;
					}
					function addNew() {
						$scope.model[$scope.options.key] = $scope.model[$scope.options.key]
								|| [];
						console.log($scope.options.key);
						console.log($scope.model[$scope.options.key]);
						var repeatsection = $scope.model[$scope.options.key];
						var lastSection = repeatsection[repeatsection.length - 1];
						var newsection = {};
						// if (lastSection) {
						// newsection = angular.copy(lastSection);
						// }
						repeatsection.push(newsection);
					}
					function addRandomIds(fields) {
						var unique = getRandomInt(0, 9999); // TODO: This should
															// be a unique value
						angular
								.forEach(
										fields,
										function(field, index) {
											if (field.fieldGroup) {
												addRandomIds(field.fieldGroup);
												return; // fieldGroups don't
														// need an ID
											}
											if (field.templateOptions
													&& field.templateOptions.fields) {
												addRandomIds(field.templateOptions.fields);
											}
											field.id = field.id
													|| (field.key + '_' + index
															+ '_' + unique + getRandomInt(
															0, 9999));
										});
					}
					function getRandomInt(min, max) {
						return Math.floor(Math.random() * (max - min)) + min;
					}
				}
			});

	formlyConfigProvider.setType({
		name : 'buttoncustom',
		templateUrl : 'templates/formly/buttoncustom.html'
	})

	formlyConfigProvider.setType({
		name : 'age-verification-button',
		templateUrl : 'templates/formly/ageVerificationButton.html',
		controller: function($scope) {
			$scope.onClick = onClick;
			function onClick($event, model) {
				if (angular.isString($scope.to.onClick)) {
					return $scope.$eval($scope.to.onClick, {
						$event: $event,
						model: model
					});
				} else {
					return $scope.to.onClick($event, model);
				}
			}
		}
	})

	formlyConfigProvider.setType({
		name : 'address-verification-button',
		templateUrl : 'templates/formly/addressVerificationButton.html',
		wrapper : [ 'bootstrapLabel' ],
		controller: function($scope) {
			$scope.onClick = onClick;
			function onClick($event, model) {
				if (angular.isString($scope.to.onClick)) {
					return $scope.$eval($scope.to.onClick, {
						$event: $event,
						model: model
					});
				} else {
					return $scope.to.onClick($event, model);
				}
			}
		}
	});

	formlyConfigProvider.setType({
		name : 'button',
		// extends : 'select',
		templateUrl : 'templates/formly/button.html',
		// template: '<button type="{{::to.type}}" class="btn
		// btn-{{::to.btnType}}" ng-click="onClick($event,
		// model)">{{to.text}}</button>',
		wrapper : [ 'bootstrapLabel' ],
		defaultOptions : {
			templateOptions : {
				btnType : 'default',
				type : 'button',
				idxLabel : ''
			},
			extras : {
				skipNgModelAttrsManipulator : true
			// <-- perf optimazation because this type has no ng-model
			}
		},
		controller : function($scope) {
			$scope.onClick = onClick;
			function onClick($event, model) {
				// console.log(model);
				if (angular.isString($scope.to.onClick)) {
					return $scope.$eval($scope.to.onClick, {
						$event : $event,
						model : model
					});
				} else {
					return $scope.to.onClick($event, model);
				}
			}
		}
	});

	formlyConfigProvider.setType({
		name: 'json-editor',
		templateUrl: 'templates/formly/json-editor.html',
		defaultOptions: {
			templateOptions: {
				aceOptions: {
					workerPath: '/js',
					showGutter: true,
					theme: 'terminal',
					mode: 'json'
				}
			}
		}
	});

	formlyConfigProvider
		.setType({
			name : 'warning-text-with-label',
			template : '<div style="color: red; margin-bottom: 20px;" ng-if="model[options.key]">'
			+ '<span >{{to.label}} </span>'
			+ '<span style="display: block">{{model[options.key]}}</span>' + '</div>',
			controller : function($scope) {}
		});

}
