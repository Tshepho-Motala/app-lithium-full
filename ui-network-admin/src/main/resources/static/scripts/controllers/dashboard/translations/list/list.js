'use strict';

angular
		.module('lithium')
		.controller('translations',	["notify", "$timeout", "$translate","$log","$dt","$scope","$state","$http", "$rootScope",
			function(notify, $timeout, $translate, $log, $dt, $scope, $state, $http, $rootScope) {

				$log.info("Translations");
							
				var baseUrl = "services/service-translate/apiv1/";
				var listUrl = baseUrl + "translations/list?1=1";
				var saveUrl = baseUrl + "translation/saveByKeyId?1=1";

				var controller = this;

				controller.model = { languageFrom: "en", showCompleted: false };
				controller.options = {};
				controller.start = 1;
				controller.length = 100;
				controller.total = 0;
				controller.noData = false;
				controller.searchText = "";
				controller.count = 0;
				
				controller.fields = 
				[
					{
						"className" : "row v-reset-row ",
						"fieldGroup" : 
						[
							{
								"className" : "col-xs-12 col-lg-6",
								"type" : "ui-select-single",
								"key" : "languageTo",
								"templateOptions" : {
									"label" : "Translation Language",
									"required" : true,
									"optionsAttr": 'bs-options',
									"description" : "The language that you would like to provide translations for.",
									"valueProp" : 'locale2',
									"labelProp" : 'description',
									"placeholder" : 'Select Language',
									"optionsAttr": 'ui-options', "ngOptions": 'ui-options',
									"options" : []
								},
								controller: ['$scope', function($scope) {
									$http.get("services/service-translate/apiv1/languages/all").then(function(response) {
										$scope.to.options = response.data;
									});
								}]
							},
							{
								"className" : "col-xs-12 col-lg-6",
								"type" : "ui-select-single",
								"key" : "languageFrom",
								"templateOptions" : {
									"label" : "Reference Language",
									"required" : true,
									"optionsAttr": 'bs-options',
									"description" : "The language to show as reference while translating another language.",
									"valueProp" : 'locale2',
									"labelProp" : 'description',
									"placeholder" : 'Select Language',
									"optionsAttr": 'ui-options', "ngOptions": 'ui-options',
									"options" : []
								},
								controller: ['$scope', function($scope) {
									$http.get("services/service-translate/apiv1/languages/all").then(function(response) {
										$scope.to.options = response.data;
									});
								}]
							},
							{
								"className" : "col-xs-12 col-lg-6",
								"type": "checkbox",
								"key": "showCompleted",
								"templateOptions": {
									"label": "Show completed translations."
								}
							}
						]
					}
				];

				controller.originalFields = angular.copy(controller.fields);
				
				controller.translationFields = function(focus) {
					return	[
						{
							"className" : "row v-reset-row ",
							"fieldGroup" : 
							[
								{
									"className" : "col-xs-12 col-lg-6",
									"type" : "input",
									"key" : "reference",
									"templateOptions" : {
										"label" : "Reference Value",
										"disabled" : true,
										"optionsAttr": 'bs-options',
										"options" : []
									}
								},
//								{
//									"className" : "col-xs-12 col-lg-6",
//									"type" : "input",
//									"key" : "current",
//									"templateOptions" : {
//										"label" : "Current Value",
//										"disabled" : true,
//										"optionsAttr": 'bs-options',
//										"options" : []
//									}
//								},
								{
									"className" : "col-xs-12 col-lg-6",
									"type" : "input",
									"key" : "translation",
									"templateOptions" : {
										label : "Translation",
										required : true,
										optionsAttr: 'bs-options',
										options : [],
										focus : focus
									}
								},

							]
						}
					];
				}


				// function definition
				controller.onSubmit = function() {
//					controller.options.updateInitialValue();
					//alert(JSON.stringify(controller.model), null, 2);
					$http.get(listUrl + "&languageFrom=" + controller.model.languageFrom +
							"&search[value]=" + encodeURIComponent(controller.searchText) + 
							"&completed=" + controller.model.showCompleted + 
							"&languageTo=" + controller.model.languageTo +
							"&draw=1&start=" + controller.start +
							"&length=" + controller.length).then(function (response){
								var translations = response.data.data;
								
								for (var i in translations) {
									var translation = translations[i];
									
									translation.translationFields = controller.translationFields(i == 0);
									
									if (translation.referenceValue && translation.referenceValue.defaultValue && translation.referenceValue.defaultValue.value)
										translation.reference = translation.referenceValue.defaultValue.value;
									if (translation.referenceValue && translation.referenceValue.current && translation.referenceValue.current.value)
										translation.reference = translation.referenceValue.current.value;
									
									if (translation.value && translation.value.defaultValue && translation.value.defaultValue.value)
										translation.translation = translation.value.defaultValue.value;
									if (translation.value && translation.value.current && translation.value.current.value)
										translation.translation = translation.value.current.value;
									
									translation.current = translation.translation;
									
									$log.info(translation.translation);
								}
								
								controller.translations = translations;
								controller.total = response.data.recordsTotal;
								controller.noData = (controller.total == 0);
								controller.firstPage = controller.start < controller.length;
								controller.lastPage = controller.start > controller.total - controller.length;
								controller.count = controller.length - 1;
								if (controller.start > controller.total) controller.start = 1;
								if (controller.start + controller.count > controller.total) 
									controller.count = controller.total - controller.start;
							});
				}
				
				controller.onReset = function() {
					controller.options.resetModel();
					controller.translations = [];
					controller.noData = false;
				}
				
				controller.flattenNamespace = function(namespace) {
					var code = namespace.code;
					if (namespace.parent) code = controller.flattenNamespace(namespace.parent) + "." + code;
					return code;
				}
				
				controller.save = function(translation) {
					$log.info(translation);
					$http.get(saveUrl + "&lang=" + controller.model.languageTo + "&keyId=" + translation.key.id + 
							"&value=" + encodeURIComponent(translation.translation)).then(function(response){
						notify.success("UI_NETWORK_ADMIN.TRANSLATIONS.SUCCESS");
						var index = 0;
						for (var i = 0; i < controller.translations.length; i++) {
							if (controller.translations[i] == translation) {
								controller.translations.splice(i, 1);
								index = i;
								break;
							}
						}

						if (controller.translations.length == 0) {
							controller.onSubmit();
							return;
						}

						if (index >= controller.translations.length) index = controller.translations.length - 1;
						var next = controller.translations[index];
						if (next) {
							next.translationFields = controller.translationFields(true);
							return;
						}

						//controller.onSubmit();
					});
				}
				
				controller.navigate = function(step) {
					$log.info(controller.start);
					controller.start = controller.start + (step * controller.length);
					$log.info(controller.start);
					if (controller.start >= controller.total) controller.start -= controller.length;
					if (controller.start < 1) controller.start = 1;
					$log.info(controller.start);
					controller.onSubmit();
				}
				
				controller.search = function() {
					if (controller.searchPromise) $timeout.cancel(controller.searchPromise);
					controller.searchPromise = $timeout(function () {
						controller.onSubmit();
					}, 1000);
				}

			} ]);