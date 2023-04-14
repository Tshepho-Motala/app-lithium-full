'use strict';

angular.module('lithium')
	.controller('gameEdit', ["errors", "$translate", "unlockgameusersearch", "$state", "$stateParams", "$dt", "rest-games","notify", "file-upload", "bsLoadingOverlayService", "$filter", "$userService", "$scope", "rest-accounting-internal", "cdnExternalGameGraphic", "GameSuppliersRest", "GameTypesRest", "GameStudioRest", "liveCasinoImage",
		function(errors, $translate, unlockgameusersearch, $state, $stateParams, $dt, gamesRest, notify, fileUpload, bsLoadingOverlayService, $filter, $userService, $scope, accountingInternalRest, cdnExternalGameGraphic, gameSuppliersRest, gameTypesRest, gameStudioRest, liveCasinoImage) {
		var controller = this;
		var gameId = $stateParams.gameId;
		controller.game = {};
		controller.game.id = gameId;
		controller.cdnExternalGameGraphic = cdnExternalGameGraphic || {};
		controller.liveCasinoImage = liveCasinoImage || {};
		controller.changelogs = {
				domainName: $stateParams.domainName,
				entityId: controller.game.id,
				restService: gamesRest,
				reload: 0
			}

		var baseUrl = "services/service-games/games/list/"+controller.game.id+"/userstatus?1=1";
		console.log(controller.game, baseUrl);
		
		//.column($dt.labelcolumn($translate("UI_NETWORK_ADMIN.GAME.PLAYERSDT.LOCKED"), [{lclass: function(data) { return (data.locked === true)?"success":"danger"; }, text: function(data) { return '<i class="fa fa-check fa-lg" style="color:green">' } }]))
		controller.playersTable = $dt.builder()
		.column($dt.column('id').withTitle($translate("UI_NETWORK_ADMIN.GAME.PLAYERSDT.ID")))
		.column($dt.column('user.guid').withTitle($translate("UI_NETWORK_ADMIN.GAME.PLAYERSDT.GUID")))
		.column($dt.columntick('locked').withTitle($translate("UI_NETWORK_ADMIN.GAME.PLAYERSDT.LOCKED")))
		.column($dt.linkscolumn("", [{ permission: "game_list", permissionType:"any", title: "UI_NETWORK_ADMIN.GAME.UNLOCK.REMOVE", click: function(data){ controller.removeUser(data) } }]))
		.options(baseUrl)
		.build();
		
		controller.domains = $userService.playerDomainsWithAnyRole(["ADMIN", "GAME_LIST"]);//$userService.playerDomainsWithAnyRole(["ADMIN", "CASHIER_VIEW"]);
		controller.uploadFileUri= "services/service-games/games/editGraphic";
		controller.model = {};
		
		controller.referenceId = "fileupload_"+(Math.random()*1000);
		
		controller.loadGame = function(gameId) {
			gamesRest.findByGameId(gameId).then(function(game) {
				controller.game = game.plain();
				console.log(controller.game);
				if ((controller.game.gameSupplier !== undefined && controller.game.gameSupplier !== null)  && (controller.game.gameSupplier.deleted)) {
					$translate('UI_NETWORK_ADMIN.GAME.FIELDS.GAME-SUPPLIER-CURRENT.DELETED_WARN')
						.then(function(response) {
							controller.game.gameSupplier.name = controller.game.gameSupplier.name + " | " + response;
						});
				}
				controller.gameGuid = game.guid;
				controller.selectedDomain = game.domain.name;
				var found = false;
				angular.forEach(controller.domains, function(domain) {
					if (domain.name === game.domain.name) found = true;
				});
				if (!found) {
					controller.domains.splice(0, 0, {name:game.domain.name, pd:null});
				}
				controller.prepGame(game.plain());
			});
		}
		controller.loadGame(gameId);
		
		controller.loadGameByDomain = function(domain) {
			return gamesRest.findByGameGuid(controller.gameGuid, domain).then(function(game) {
				console.log(game.plain());
				if (game.length === 0) {
					notify.error("Please enable on gamelist first. Displaying original selected domain.");
					controller.loadGame(gameId);
					return false;
				} else {
//					controller.playersTable.instance.DataTable.ajax.reload();
					controller.game = game.plain();
					controller.selectedDomain = game.domain.name;
					controller.prepGame(game.plain());
					return true;
				}
			});
		}
		
		controller.tabs = [
			{ name: "dashboard.gameEdit.summary", title: "UI_NETWORK_ADMIN.GAME.TABS.SUMMARY", roles: "GAME_SUMMARY" },
			{ name: "dashboard.gameEdit.images", title: "UI_NETWORK_ADMIN.GAME.TABS.IMAGES", roles: "GAME_IMAGES" },
			{ name: "dashboard.gameEdit.players", title: "UI_NETWORK_ADMIN.GAME.TABS.PLAYERS", roles: "GAME_PLAYERS" }
		];
		
		controller.setTab = function(tab) {
			console.log("TAB SET");
			if (tab.tclass !== 'disabled') {
				$state.go(tab.name);
			}
		}
		
		function isEmpty(obj) {
			for (var key in obj) {
				if (obj.hasOwnProperty(key))
					return false;
			}
			return true;
		}
		
		controller.refresh = function() {
			console.log('refresh', controller.playersTable);
			if (!isEmpty(controller.playersTable.instance)) {
				baseUrl = "services/service-games/games/list/"+controller.game.id+"/userstatus?1=1";
				controller.playersTable.instance._renderer.options.ajax = baseUrl;
				controller.playersTable.instance.rerender();
			}
		}
		
		controller.domainSelect = function(item) {
			controller.loadGameByDomain(item.name).then(function(result) {
				console.log(result);
				controller.selectedDomain = item.name;
				if (result) controller.refresh();
			});
//				if (angular.isUndefined(controller.tab)) {
//					controller.setTab(controller.tabs[0]);
//				} else {
//					controller.setTab(controller.tab);
//				}
		}
		controller.clearSelectedDomain = function() {
			controller.selectedDomain = null;
		}
		
		controller.removeUser = function(data) {
			gamesRest.toggleDelete(controller.game.id, data.user.guid).then(function(response) {
				console.log(response.plain());
				controller.playersTable.instance.DataTable.ajax.reload();
			}).catch(function() {
				errors.catch('UI_NETWORK_ADMIN.GAME.UNLOCK.USERREGERROR', false);
			}).finally(function() {
				
			});
			console.log(data);
		}
		
		controller.openUnlockGame = function() {
			unlockgameusersearch.unlockGame().then(function(response) {
				if (response != null) console.log(response.plain());
				controller.playersTable.instance.DataTable.ajax.reload();
			});
		}

		controller.prepGame = function(game) {
			accountingInternalRest.findDomainCurrencies(controller.game.domain.name).then(function(response) {
				console.log("Got currencies", response.plain());
				var c = [];
				for (var i = 0; i < response.plain().length; i++) {
					c.push({value: response.plain()[i].currency.code, label: response.plain()[i].currency.code});
				}
				controller.fieldsGameCurrency[0].templateOptions.options = c;
			});
			
			controller.model = game;
			if (controller.model.gameCurrency !== undefined && controller.model.gameCurrency !== null
					&& controller.model.gameCurrency.minimumAmountCents !== undefined && controller.model.gameCurrency.minimumAmountCents !== null) {
				controller.model.gameCurrency.minimumAmountCents = controller.model.gameCurrency.minimumAmountCents / 100;
			}
			controller.imageUrl = "/services/service-games/games/"+game.domain.name+"/getImage?";
			controller.imageDefaultUrl = "/services/service-games/games/default/getImage?";
			
			controller.model.labelArray = [];
			var labels = angular.forEach(controller.model.labels, function(value, key){
				this.push(angular.copy(value));
			}, controller.model.labelArray);

			// Setup channel checkboxes from game.channels List containt channel names if they are active
			controller.model.desktopWeb = game.channels.find(s => s === 'desktop_web') ? true : false;
			controller.model.mobileWeb = game.channels.find(s => s === 'mobile_web') ? true : false;
			controller.model.mobileIos = game.channels.find(s => s === 'mobile_ios') ? true : false;
			controller.model.androidNative = game.channels.find(s => s === 'android_native') ? true : false;

//			controller.model.labelArray = labels;
			controller.model.forceImageReload=new Date().getTime();
//			controller.model.labels = [];
//			for(var j=0; j < labels.length; ++j) {
//				var keyval = labels[j].split("=");
//				var label = {"labelName" : keyval[0], "labelValue" : keyval[1]};
//				controller.model.labels.push(label);
//			}
			controller.originalModel = angular.copy(controller.model);
			controller.originalFields = angular.copy(controller.fields);
		}

		controller.options = {};

		controller.channelFields = [{
			className : "col-xs-12",
			key : "desktopWeb",
			type : "checkbox2",
			optionsTypes: ['editable'],
			templateOptions : {
				disabled : true,
				label : "Desktop Web"
			},
			expressionProperties: {
				'templateOptions.label': '"UI_NETWORK_ADMIN.GAME.FIELDS.CHANNEL_NAME.DESKTOP_WEB.LABEL" | translate'
			}
		},
		{
			className : "col-xs-12",
			key : "mobileWeb",
			type : "checkbox2",
			optionsTypes: ['editable'],
			templateOptions : {
				disabled : true,
				label : "Mobile Web",
			},
			expressionProperties: {
				'templateOptions.label': '"UI_NETWORK_ADMIN.GAME.FIELDS.CHANNEL_NAME.MOBILE_WEB.LABEL" | translate'
			}
		},
		{
			className : "col-xs-12",
			key : "mobileIos",
			type : "checkbox2",
			optionsTypes: ['editable'],
			templateOptions : {
				disabled : true,
				label : "Mobile iOS",
			},
			expressionProperties: {
				'templateOptions.label': '"UI_NETWORK_ADMIN.GAME.FIELDS.CHANNEL_NAME.MOBILE_IOS.LABEL" | translate'
			}
		},
		{
			className: "col-xs-12",
			key: "androidNative",
			type: "checkbox2",
			optionsTypes: ['editable'],
			templateOptions: {
				disabled: true,
				label: "Android Native",
			},
			expressionProperties: {
				'templateOptions.label': '"UI_NETWORK_ADMIN.GAME.FIELDS.CHANNEL_NAME.ANDROID_NATIVE.LABEL" | translate'
			}
		}]

		controller.labelFields = [{
			type : 'repeatSection',
			key : 'labelArray',
			templateOptions : {
				btnText : '',
				fields : [{
					fieldGroup : [{
						className : 'col-xs-4',
						type : 'input',
						key : 'name',
						templateOptions : {
							placeholder : ''
						},
						expressionProperties: {
							'templateOptions.placeholder': '"UI_NETWORK_ADMIN.GAME.FIELDS.LABEL_NAME.PLACEHOLDER" | translate'
						}
					},
					{
						className : 'col-xs-4',
						type : 'input',
						key : 'value',
						templateOptions : {
							placeholder : ''
						},
						expressionProperties: {
							'templateOptions.placeholder': '"UI_NETWORK_ADMIN.GAME.FIELDS.LABEL_VALUE.PLACEHOLDER" | translate',
						}
					},
					{
						className : 'col-xs-4',
						type : 'button',
						templateOptions : {
							btnType: 'danger',
							text : '',
							idxLabel : 'myindex',
							onClick : function($event, model) {
								for (var i=0; i < controller.model['labelArray'].length; ++i) {
									if (controller.model['labelArray'][i] === model) {
										controller.model['labelArray'].splice(i, 1);
									}
								}
							}
						},
						expressionProperties: {
							'templateOptions.text': '"UI_NETWORK_ADMIN.GAME.BUTTON.REMOVE_LABEL.NAME" | translate'
						}
					}]
				}]
			},
			expressionProperties: {
				'templateOptions.btnText': '"UI_NETWORK_ADMIN.GAME.BUTTON.ADD_LABEL.NAME" | translate'
			}
		}];

			controller.fieldsGameCurrency = [{
			key: 'gameCurrency.currencyCode', 
			type: 'ui-select-single',
			templateOptions : {
				label: "",
				description: "",
				optionsAttr: 'bs-options',
				valueProp: 'value',
				labelProp: 'label',
				optionsAttr: 'ui-options', ngOptions: 'ui-options',
				options: []
			},
			expressionProperties: {
				'templateOptions.label': '"UI_NETWORK_ADMIN.GAME.CURRENCY.CODE.NAME" | translate',
				'templateOptions.description': '"UI_NETWORK_ADMIN.GAME.CURRENCY.CODE.DESCRIPTION" | translate'
			}
		}, {
				key: "gameCurrency.minimumAmountCents",
				type: "ui-money-mask",
				optionsTypes: ['editable'],
				templateOptions : {
					label: "",
					description: "",
					addFormControlClass: true,
					required: true
				},
				hideExpression: function($viewValue, $modelValue, scope) {
					if (controller.model.gameCurrency === undefined || controller.model.gameCurrency === null ||
							controller.model.gameCurrency.currencyCode === undefined || controller.model.gameCurrency.currencyCode === null ||
							controller.model.gameCurrency.currencyCode === '') {
						controller.model.gameCurrency = null;
						return true;
					}
					return false;
				},
				expressionProperties: {
					'templateOptions.label': '"UI_NETWORK_ADMIN.GAME.CURRENCY.MINAMOUNT.NAME" | translate',
					'templateOptions.description': '"UI_NETWORK_ADMIN.GAME.CURRENCY.MINAMOUNT.DESCRIPTION" | translate'
				}
		}];
		
		controller.fieldsLockMsg = [
			{
				className: "col-xs-12",
				key: "lockedMessage",
				type: "ckeditor",
				optionsTypes: ['editable'],
				templateOptions: {
					label: "", description: "", placeholder: "",
					required: false, maxlength: 1000000,
					ckoptions: {
//					fullPage: true,
						language: 'en',
						enterMode: CKEDITOR.ENTER_P,
						shiftEnterMode: CKEDITOR.ENTER_BR,
						allowedContent: true,
						entities: false,
						filebrowserBrowseUrl: gamesRest.baseUrl + 'browser/browse.php',  // Not sure wtf this is for
						filebrowserUploadUrl: gamesRest.baseUrl + 'uploader/upload.php'  // Not sure wtf this is for
					}
				},
				expressionProperties: {
					'templateOptions.label': '"UI_NETWORK_ADMIN.GAME.FIELDS.LOCKMSG.NAME" | translate',
					'templateOptions.placeholder': '"UI_NETWORK_ADMIN.GAME.FIELDS.LOCKMSG.PLACEHOLDER" | translate',
					'templateOptions.description': '"UI_NETWORK_ADMIN.GAME.FIELDS.LOCKMSG.DESCRIPTION" | translate'
				}
 			}];

		controller.fields = [{
//			"className" : "col-xs-8",
			"type" : "input",
			"key" : "providerGuid",
			"templateOptions" : {
				"label" : '',
				disabled: true
			},
			expressionProperties: {
				'templateOptions.label': '"UI_NETWORK_ADMIN.GAME.FIELDS.PROVIDER_GUID.NAME" | translate',
				'templateOptions.placeholder': '"UI_NETWORK_ADMIN.GAME.FIELDS.PROVIDER_GUID.PLACEHOLDER" | translate',
				'templateOptions.description': '"UI_NETWORK_ADMIN.GAME.FIELDS.PROVIDER_GUID.DESCRIPTION" | translate'
			}
		},
		{
//			className: "col-xs-8",
			key: "providerGameId",
			type: "input",
			templateOptions: {
				label: "", description: "", placeholder: "",
				disabled: true,
				required: true,
				focus: true
			},
			modelOptions: {
				updateOn: 'default blur', debounce: {'default': 1000, 'blur': 0}
			},
			expressionProperties: {
				'templateOptions.label': '"UI_NETWORK_ADMIN.GAME.FIELDS.PROVIDER_GAME_ID.NAME" | translate',
				'templateOptions.placeholder': '"UI_NETWORK_ADMIN.GAME.FIELDS.PROVIDER_GAME_ID.PLACEHOLDER" | translate',
				'templateOptions.description': '"UI_NETWORK_ADMIN.GAME.FIELDS.PROVIDER_GAME_ID.DESCRIPTION" | translate'
			},
			validators: {
				pattern: {
					expression: function($viewValue, $modelValue, scope) {
						return /^[0-9a-z-_\\.]+$/.test($viewValue);
					},
					message: '"UI_NETWORK_ADMIN.GAME.FIELDS.PROVIDER_GAME_ID.PATTERN" | translate'
				}
			}
		},
		{
			key: "supplierGameGuid",
			type: "input",
			templateOptions: {
				label: "", description: "", placeholder: "",
				required: false,
				focus: true
			},
			modelOptions: {
				updateOn: 'default blur', debounce: {'default': 1000, 'blur': 0}
			},

			expressionProperties: {
				'templateOptions.label': '"UI_NETWORK_ADMIN.GAME.FIELDS.SUPPLIER_GAME_GUID.NAME" | translate',
				'templateOptions.placeholder': '"UI_NETWORK_ADMIN.GAME.FIELDS.SUPPLIER_GAME_GUID.PLACEHOLDER" | translate',
				'templateOptions.description': '"UI_NETWORK_ADMIN.GAME.FIELDS.SUPPLIER_GAME_GUID.DESCRIPTION" | translate'
			}
		},
		{
			key: "supplierGameRewardGuid",
			type: "input",
			templateOptions: {
				label: "", description: "", placeholder: "",
				required: false,
				focus: true
			},
			modelOptions: {
				updateOn: 'default blur', debounce: {'default': 1000, 'blur': 0}
			},

			expressionProperties: {
				'templateOptions.label': '"UI_NETWORK_ADMIN.GAME.FIELDS.SUPPLIER_GAME_REWARD_GUID.NAME" | translate',
				'templateOptions.placeholder': '"UI_NETWORK_ADMIN.GAME.FIELDS.SUPPLIER_GAME_REWARD_GUID.PLACEHOLDER" | translate',
				'templateOptions.description': '"UI_NETWORK_ADMIN.GAME.FIELDS.SUPPLIER_GAME_REWARD_GUID.DESCRIPTION" | translate'
			}
		},
		{
			key: "moduleSupplierId",
			type: "input",
			templateOptions: {
				label: "", description: "", placeholder: "",
				required: false,
				focus: true
			},
			modelOptions: {
				updateOn: 'default blur', debounce: {'default': 1000, 'blur': 0}
			},

			expressionProperties: {
				'templateOptions.label': '"UI_NETWORK_ADMIN.GAME.FIELDS.MODULE_SUPPLIER_ID.NAME" | translate',
				'templateOptions.placeholder': '"UI_NETWORK_ADMIN.GAME.FIELDS.MODULE_SUPPLIER_ID.PLACEHOLDER" | translate',
				'templateOptions.description': '"UI_NETWORK_ADMIN.GAME.FIELDS.MODULE_SUPPLIER_ID.DESCRIPTION" | translate'
			}
		},
		{
			// FIXME FIXME FIXME: Not required as opposed to spec to cater for other game providers with no
			//					  game supplier dependency
			"type": "ui-select-single",
			"key": "gameSupplier.id",
			"templateOptions": {
				"label": '',
				"optionsAttr": 'bs-options',
				"description": "",
				"valueProp": 'id',
				"labelProp": 'name',
				"placeholder": '',
				"ngOptions": 'ui-options',
				"options": []
			},
			controller: ['$scope', function ($scope) {
				gameSuppliersRest.findByDomain($stateParams.domainName)
					.then(function (gameSuppliers) {
						$scope.to.options = gameSuppliers;
					});
			}],
			expressionProperties: {
				'templateOptions.label': '"UI_NETWORK_ADMIN.GAME.FIELDS.GAME-SUPPLIER.NAME" | translate',
				'templateOptions.description': '"UI_NETWORK_ADMIN.GAME.FIELDS.GAME-SUPPLIER.DESCRIPTION" | translate'
			}
		},
		{
			key: "gameSupplier.name",
			type: "readonly-input",
			templateOptions: {
				label: "",
				description: ""
			},
			expressionProperties: {
				'templateOptions.label': '"UI_NETWORK_ADMIN.GAME.FIELDS.GAME-SUPPLIER-CURRENT.NAME" | translate'
			}
		},
			{
				"type": "ui-select-single",
				"key": "gameStudio.id",
				"templateOptions": {
					"label": '',
					"optionsAttr": 'bs-options',
					"description": "",
					"valueProp": 'id',
					"labelProp": 'name',
					"placeholder": '',
					"ngOptions": 'ui-options',
					"options": []
				},
				controller: ['$scope', function ($scope) {
					gameStudioRest.findByDomain($stateParams.domainName)
						.then(function (gameStudio) {
							$scope.to.options = gameStudio;
						});
				}],
				expressionProperties: {
					'templateOptions.label': '"UI_NETWORK_ADMIN.GAME.FIELDS.GAME-STUDIO.NAME" | translate',
					'templateOptions.description': '"UI_NETWORK_ADMIN.GAME.FIELDS.GAME-STUDIO.DESCRIPTION" | translate'
				}
			},
			{
				key: "gameStudio.name",
				type: "readonly-input",
				templateOptions: {
					label: "",
					description: ""
				},
				expressionProperties: {
					'templateOptions.label': '"UI_NETWORK_ADMIN.GAME.FIELDS.GAME-STUDIO-CURRENT.NAME" | translate'
				}
			},
			{
				"type": "ui-select-single",
				"key": "primaryGameType.id",
				"templateOptions": {
					"label": '',
					"optionsAttr": 'bs-options',
					"description": "",
					"valueProp": 'id',
					"labelProp": 'name',
					"placeholder": '',
					"ngOptions": 'ui-options',
					"options": []
				},
				controller: ['$scope', function ($scope) {
					gameTypesRest.findByDomainAndType($stateParams.domainName, 'primary')
					.then(function (gameTypes) {
						$scope.to.options = gameTypes;
					});
				}],
				expressionProperties: {
					'templateOptions.label': '"UI_NETWORK_ADMIN.GAME.FIELDS.PRIMARY-GAME-TYPE.NAME" | translate',
					'templateOptions.description': '"UI_NETWORK_ADMIN.GAME.FIELDS.PRIMARY-GAME-TYPE.DESCRIPTION" | translate'
				}
			},
			{
				key: "primaryGameType.name",
				type: "readonly-input",
				templateOptions: {
					label: "",
					description: ""
				},
				expressionProperties: {
					'templateOptions.label': '"UI_NETWORK_ADMIN.GAME.FIELDS.PRIMARY-GAME-TYPE-CURRENT.NAME" | translate'
				}
			},
			{
				"type": "ui-select-single",
				"key": "secondaryGameType.id",
				"templateOptions": {
					"label": '',
					"optionsAttr": 'bs-options',
					"description": "",
					"valueProp": 'id',
					"labelProp": 'name',
					"placeholder": '',
					"ngOptions": 'ui-options',
					"options": []
				},
				controller: ['$scope', function ($scope) {
					gameTypesRest.findByDomainAndType($stateParams.domainName, 'secondary')
					.then(function (gameTypes) {
						$scope.to.options = gameTypes;
					});
				}],
				expressionProperties: {
					'templateOptions.label': '"UI_NETWORK_ADMIN.GAME.FIELDS.SECONDARY-GAME-TYPE.NAME" | translate',
					'templateOptions.description': '"UI_NETWORK_ADMIN.GAME.FIELDS.SECONDARY-GAME-TYPE.DESCRIPTION" | translate'
				}
			},
			{
				key: "secondaryGameType.name",
				type: "readonly-input",
				templateOptions: {
					label: "",
					description: ""
				},
				expressionProperties: {
					'templateOptions.label': '"UI_NETWORK_ADMIN.GAME.FIELDS.SECONDARY-GAME-TYPE-CURRENT.NAME" | translate'
				}
			},
		{
//			className: "col-xs-8",
			key: "name",
			type: "input",
			templateOptions: {
				label: "", description: "", placeholder: "",
				required: true,
				minlength: 2, maxlength: 255,
				focus: true
			},
			modelOptions: {
				updateOn: 'default blur', debounce: { 'default': 1000, 'blur': 0 }
			},
			
			expressionProperties: {
				'templateOptions.label': '"UI_NETWORK_ADMIN.GAME.FIELDS.NAME.NAME" | translate',
				'templateOptions.placeholder': '"UI_NETWORK_ADMIN.GAME.FIELDS.NAME.PLACEHOLDER" | translate',
				'templateOptions.description': '"UI_NETWORK_ADMIN.GAME.FIELDS.NAME.DESCRIPTION" | translate'
			},
			validators: {
				pattern: {
					expression: function ($viewValue, $modelValue, scope) {
						return /^[a-zA-Z0-9äÜéöÖüßáÁÄÉíÍñÑèùòøØåÅ\s,.'_()!:&-]*$/.test($viewValue);
					},
					message: '"UI_NETWORK_ADMIN.GAME.FIELDS.NAME.PATTERN" | translate'
				}
			}
		},
			{
//			className: "col-xs-8",
				key: "commercialName",
				type: "input",
				templateOptions: {
					label: "", description: "", placeholder: "",
					required: true,
					minlength: 2, maxlength: 255,
					focus: true
				},
				modelOptions: {
					updateOn: 'default blur', debounce: { 'default': 1000, 'blur': 0 }
				},

				expressionProperties: {
					'templateOptions.label': '"UI_NETWORK_ADMIN.GAME.FIELDS.COMMERCIAL_NAME.NAME" | translate',
					'templateOptions.placeholder': '"UI_NETWORK_ADMIN.GAME.FIELDS.COMMERCIAL_NAME.PLACEHOLDER" | translate',
					'templateOptions.description': '"UI_NETWORK_ADMIN.GAME.FIELDS.COMMERCIAL_NAME.DESCRIPTION" | translate'
				},
				validators: {
					pattern: {
						expression: function ($viewValue, $modelValue, scope) {
							return /^[a-zA-Z0-9äÜéöÖüßáÁÄÉíÍñÑèùòøØåÅ\s,.'_()!:&-]*$/.test($viewValue);
						},
						message: '"UI_NETWORK_ADMIN.GAME.FIELDS.COMMERCIAL_NAME.PATTERN" | translate'
					}
				}
			},
		{
//			className: "col-xs-8",
			key: "description",
			type: "textarea",
			templateOptions: {
				label: ''
			},
			modelOptions: {
				updateOn: 'default blur', debounce: { 'default': 1000, 'blur': 0 }
			},
			expressionProperties: {
				'templateOptions.label': '"UI_NETWORK_ADMIN.GAME.FIELDS.DESCRIPTION.NAME" | translate',
				'templateOptions.placeholder': '"UI_NETWORK_ADMIN.GAME.FIELDS.DESCRIPTION.PLACEHOLDER" | translate',
				'templateOptions.description': '"UI_NETWORK_ADMIN.GAME.FIELDS.DESCRIPTION.DESCRIPTION" | translate'
			}
		},
			{
				key: "rtp",
				type: "ui-percentage-mask",
				optionsTypes: ['editable'],
				templateOptions : {
					label: "",
					description: "",
					required: false,
					hidesep: true,
					decimals: 2,
					placeholder: "rtp 100%",
					max: 100
				},
				expressionProperties: {
					'templateOptions.label': '"UI_NETWORK_ADMIN.GAME.FIELDS.GAME.NAME" | translate',
					'templateOptions.placeholder': '"UI_NETWORK_ADMIN.GAME.FIELDS.GAME.PLACEHOLDER" | translate',
					'templateOptions.description': '"UI_NETWORK_ADMIN.GAME.FIELDS.GAME.DESCRIPTION" | translate'
				},
			},
			{
				className: "form-row row",
				fieldGroup: [
					{
						key: "introductionDate",
						className:"form-check form-check-inline game-time-picker-formatter",
						type: "datepicker",
						optionsTypes: ['editable'],
						templateOptions: {
							label: "", description: "", placeholder: "",
							required: false,
							datepickerOptions: {
								format:'yyyy/MM/dd'

							},
						},
						expressionProperties: {
							'templateOptions.label': '"UI_NETWORK_ADMIN.GAME.FIELDS.GAME_DATETIME_INTRODUCTION.NAME" | translate',
							'templateOptions.placeholder': '"UI_NETWORK_ADMIN.GAME.FIELDS.GAME_DATETIME_INTRODUCTION.PLACEHOLDER" | translate',
							'templateOptions.description': '"UI_NETWORK_ADMIN.GAME.FIELDS.GAME_DATETIME_INTRODUCTION.DESCRIPTION" | translate'
						},
					},
					{
						key: "introductionDate",
						className:"form-check form-check-inline",
						type: "timepicker",
						optionsTypes: ['editable'],
						templateOptions: {
							required: false,
						}
					}]
			},
			{
				className: "form-row row",
				fieldGroup: [
				{
					key: "activeDate",
					className:"form-check form-check-inline game-time-picker-formatter",
					type: "datepicker",
					optionsTypes: ['editable'],
					templateOptions : {
						label: "", description: "", placeholder: "",
						required: false,
						datepickerOptions: {
							format: 'yyyy/MM/dd'
						},
					},
					expressionProperties: {
						'templateOptions.label': '"UI_NETWORK_ADMIN.GAME.FIELDS.GAME_DATETIME_ACTIVE.NAME" | translate',
						'templateOptions.placeholder': '"UI_NETWORK_ADMIN.GAME.FIELDS.GAME_DATETIME_ACTIVE.PLACEHOLDER" | translate',
						'templateOptions.description': '"UI_NETWORK_ADMIN.GAME.FIELDS.GAME_DATETIME_ACTIVE.DESCRIPTION" | translate'
					},
					validators: {
						pattern: {
							expression: function ($viewValue, $modelValue, scope) {
								if($viewValue)
									return (new Date()).getTime() >= new Date($viewValue).getTime();
								return true;
							},
							message: '"UI_NETWORK_ADMIN.GAME.FIELDS.GAME_DATETIME_ACTIVE.PATTERN" | translate'
						}
					}
				},

				{
					key: "activeDate",
					className:"form-check form-check-inline",
					type: "timepicker",
					optionsTypes: ['editable'],
					defaultValue: new Date(),
					templateOptions: {
						required: false,
					}
				}]
			},
			{
				className: "form-row row",
				fieldGroup: [
					{
						key: "inactiveDate",
						className: "form-check form-check-inline game-time-picker-formatter",
						type: "datepicker",
						optionsTypes: ['editable'],
						templateOptions: {
							label: "", description: "", placeholder: "",
							required: false,
							datepickerOptions: {
								format: 'yyyy/MM/dd'
							},
						},
						expressionProperties: {
							'templateOptions.label': '"UI_NETWORK_ADMIN.GAME.FIELDS.GAME_DATETIME_INACTIVE.NAME" | translate',
							'templateOptions.placeholder': '"UI_NETWORK_ADMIN.GAME.FIELDS.GAME_DATETIME_INACTIVE.PLACEHOLDER" | translate',
							'templateOptions.description': '"UI_NETWORK_ADMIN.GAME.FIELDS.GAME_DATETIME_INACTIVE.DESCRIPTION" | translate'
						},
						validators: {
							pattern: {
								expression: function ($viewValue, $modelValue, scope) {
									if ($viewValue)
										return new Date().getTime() >= new Date($viewValue).getTime();
									return true;
								},
								message: '"UI_NETWORK_ADMIN.GAME.FIELDS.GAME_DATETIME_INACTIVE.PATTERN" | translate'
							}
						}
					},
					{
						key: "inactiveDate",
						className: "form-check form-check-inline",
						type: "timepicker",
						optionsTypes: ['editable'],
						defaultValue: new Date(),
						templateOptions: {
							required: false,
						}
					}]
			},
			{
				key : "progressiveJackpot",
				type : "checkbox2",
				templateOptions : {
					disabled : false,
					label : "",
					description : "",
					placeholder : ""
				},
				expressionProperties: {
					'templateOptions.label': '"UI_NETWORK_ADMIN.GAME.FIELDS.PROGRESSIVE_JACKPOT.NAME" | translate',
					'templateOptions.placeholder': '"UI_NETWORK_ADMIN.GAME.FIELDS.PROGRESSIVE_JACKPOT.NAME" | translate'
				}
			}
			,{
				key : "networkedJackpotPool",
				type : "checkbox2",
				templateOptions : {
					disabled : false,
					label : "",
					description : "",
					placeholder : ""
				},
				expressionProperties: {
					'templateOptions.label': '"UI_NETWORK_ADMIN.GAME.FIELDS.NETWORKED_JACKPOT_POOL.NAME" | translate',
					'templateOptions.placeholder': '"UI_NETWORK_ADMIN.GAME.FIELDS.NETWORKED_JACKPOT_POOL.NAME" | translate'
				}
			}
			,{
				key : "localJackpotPool",
				type : "checkbox2",
				templateOptions : {
					disabled : false,
					label : "",
					description : "",
					placeholder : ""
				},
				expressionProperties: {
					'templateOptions.label': '"UI_NETWORK_ADMIN.GAME.FIELDS.LOCAL_JACKPOT_POOL.NAME" | translate',
					'templateOptions.placeholder': '"UI_NETWORK_ADMIN.GAME.FIELDS.LOCAL_JACKPOT_POOL.NAME" | translate'
				}
			}
			,{
//			className : "col-xs-8",
			key : "freeSpinEnabled",
			type : "checkbox2",
//						optionsTypes: ['editable'],
			templateOptions : {
				disabled : false,
				label : "",
				description : "",
				placeholder : ""
			},
			expressionProperties: {
				'templateOptions.label': '"UI_NETWORK_ADMIN.GAME.FIELDS.FREE_SPIN_ENABLED.NAME" | translate',
				'templateOptions.placeholder': '"UI_NETWORK_ADMIN.GAME.FIELDS.FREE_SPIN_ENABLED.NAME" | translate'
			}
			},{
//			className : "col-xs-8",
				key : "casinoChipEnabled",
				type : "checkbox2",
//						optionsTypes: ['editable'],
				templateOptions : {
					disabled : false,
					label : "",
					description : "",
					placeholder : ""
				},
				expressionProperties: {
					'templateOptions.label': '"UI_NETWORK_ADMIN.GAME.FIELDS.CASINO_CHIP_ENABLED.NAME" | translate',
					'templateOptions.placeholder': '"UI_NETWORK_ADMIN.GAME.FIELDS.CASINO_CHIP_ENABLED.NAME" | translate'
				}
			},{
//			className : "col-xs-8",
				key : "instantRewardEnabled",
				type : "checkbox2",
//						optionsTypes: ['editable'],
				templateOptions : {
					disabled : false,
					label : "",
					description : "",
					placeholder : ""
				},
				expressionProperties: {
					'templateOptions.label': '"UI_NETWORK_ADMIN.GAME.FIELDS.INSTANT_REWARD_ENABLED.NAME" | translate',
					'templateOptions.placeholder': '"UI_NETWORK_ADMIN.GAME.FIELDS.INSTANT_REWARD_ENABLED.NAME" | translate'
				}
			},{
//			className : "col-xs-8",
				key : "instantRewardFreespinEnabled",
				type : "checkbox2",
//						optionsTypes: ['editable'],
				templateOptions : {
					disabled : false,
					label : "",
					description : "",
					placeholder : ""
				},
				expressionProperties: {
					'templateOptions.label': '"UI_NETWORK_ADMIN.GAME.FIELDS.INSTANT_REWARD_FREESPIN_ENABLED.NAME" | translate',
					'templateOptions.placeholder': '"UI_NETWORK_ADMIN.GAME.FIELDS.INSTANT_REWARD_FREESPIN_ENABLED.NAME" | translate'
				}
			},{
//			className : "col-xs-8",
				key : "freeSpinValueRequired",
				type : "checkbox2",
//						optionsTypes: ['editable'],
				templateOptions : {
					disabled : false,
					label : "",
					description : "",
					placeholder : ""
				},
				expressionProperties: {
					'templateOptions.label': '"UI_NETWORK_ADMIN.GAME.FIELDS.FREE_SPIN_VALUE_REQUIRED.NAME" | translate',
					'templateOptions.placeholder': '"UI_NETWORK_ADMIN.GAME.FIELDS.FREE_SPIN_VALUE_REQUIRED.NAME" | translate'
				}
			},{
//			className : "col-xs-8",
				key : "freeSpinPlayThroughEnabled",
				type : "checkbox2",
//						optionsTypes: ['editable'],
				templateOptions : {
					disabled : false,
					label : "",
					description : "",
					placeholder : ""
				},
				expressionProperties: {
					'templateOptions.label': '"UI_NETWORK_ADMIN.GAME.FIELDS.FREE_SPIN_PLAY_THROUGH_ENABLED.NAME" | translate',
					'templateOptions.placeholder': '"UI_NETWORK_ADMIN.GAME.FIELDS.FREE_SPIN_PLAY_THROUGH_ENABLED.NAME" | translate'
				}
			},{
//			className : "col-xs-8",
			key : "enabled",
			type : "checkbox2",
//							optionsTypes: ['editable'],
			templateOptions : {
				disabled : false,
				label : "",
				description : "",
				placeholder : "",
				onChange: function() {
					if(!controller.model.enabled && controller.model.visible) {
						controller.model.visible = false;
					} else {
						controller.model.visible = controller.game.visible;
					}
				}
			},
			expressionProperties: {
				'templateOptions.label': '"UI_NETWORK_ADMIN.GAME.FIELDS.ENABLED.NAME" | translate',
				'templateOptions.placeholder': '"UI_NETWORK_ADMIN.GAME.FIELDS.ENABLED.NAME" | translate'
			}
		},
			{
//			className : "col-xs-8",
			key : "visible",
			type : "checkbox2",
//							optionsTypes: ['editable'],
			templateOptions : {
				disabled: false,
				label : "",
				description : "",
				placeholder : "",
				onChange:function() {
					if (!controller.model.visible) {
						controller.model.introductionDate = controller.game.introductionDate
					} else {
						var introductionDate = new Date;
						introductionDate.setHours(0, 0, 0);
						controller.model.introductionDate = controller.game.introductionDate ? controller.game.introductionDate : introductionDate;
					}
				},
			},
			expressionProperties: {
				'templateOptions.label': '"UI_NETWORK_ADMIN.GAME.FIELDS.VISIBLE.NAME" | translate',
				'templateOptions.placeholder': '"UI_NETWORK_ADMIN.GAME.FIELDS.VISIBLE.NAME" | translate',
				'templateOptions.disabled': function(viewValue, modelValue, scope) {
					return !controller.model.enabled;
				}
			}
		}
		    ,{
				key : "freeGame",
				type : "checkbox2",
				templateOptions : {
					disabled : false,
					label : "",
					description : "",
					placeholder : ""
				},
				expressionProperties: {
					'templateOptions.label': '"UI_NETWORK_ADMIN.GAME.FIELDS.FREE_GAME.NAME" | translate',
					'templateOptions.placeholder': '"UI_NETWORK_ADMIN.GAME.FIELDS.FREE_GAME.NAME" | translate',
				}
		},{
				key : "liveCasino",
				type : "checkbox2",
				templateOptions : {
					disabled : false,
					label : "",
					description : "",
					placeholder : ""
				},
				expressionProperties: {
					'templateOptions.label': '"UI_NETWORK_ADMIN.GAME.FIELDS.LIVE_CASINO.NAME" | translate',
					'templateOptions.placeholder': '"UI_NETWORK_ADMIN.GAME.FIELDS.LIVE_CASINO.NAME" | translate',
				}
			},{
				key : "excludeRecentlyPlayed",
				type : "checkbox2",
				templateOptions : {
					disabled : false,
					label : "",
					description : "",
					placeholder : ""
				},
				expressionProperties: {
					'templateOptions.label': '"UI_NETWORK_ADMIN.GAME.FIELDS.NOT_RECENTLY_PLAYED.NAME" | translate',
				}
			},{
//			className : "col-xs-8",
			key : "locked",
			type : "checkbox2",
//							optionsTypes: ['editable'],
			templateOptions : {
				disabled : false,
				label : "",
				description : "",
				placeholder : ""
			},
			expressionProperties: {
				'templateOptions.label': '"UI_NETWORK_ADMIN.GAME.FIELDS.LOCKED.NAME" | translate',
				'templateOptions.placeholder': '"UI_NETWORK_ADMIN.GAME.FIELDS.LOCKED.NAME" | translate',
				'templateOptions.description': '"UI_NETWORK_ADMIN.GAME.FIELDS.LOCKED.DESCRIPTION" | translate'
			}
		}];
		
		controller.uploadFile = function(name) {
			bsLoadingOverlayService.start({referenceId:controller.referenceId+"_"+name});
			var file = controller["my"+name+"File"];
			var extraKeyVal = [];
			extraKeyVal.push({"key": "gameId", "value": controller.game.id});
			extraKeyVal.push({"key": "graphicFunctionName", "value": name});
			extraKeyVal.push({"key": "deleted", "value": false});
			extraKeyVal.push({"key": "enabled", "value": true});
			extraKeyVal.push({"key": "domainName", "value": controller.model.domain.name});
			
			console.log(extraKeyVal);
//			private String domainName; //Just for an extra check internally
			//console.log('file is ' );
			//console.dir(file);
			//console.log(controller.uploadFileUri+"/"+name);
			fileUpload.uploadFileToUrl(file, controller.uploadFileUri, extraKeyVal).then(function() {
				notify.success("UI_NETWORK_ADMIN.GAME.EDIT.GRAPHIC_SUCCESS");
				controller.model.forceImageReload=new Date().getTime();
			}).catch(function(error) {
				errors.catch("Could not upload image.", false)(error)
			}).finally(function () {
				bsLoadingOverlayService.stop({referenceId:controller.referenceId+"_"+name});
			});
		}
		
		controller.removeFile = function(name) {
			bsLoadingOverlayService.start({referenceId:controller.referenceId+"_"+name});
			gamesRest.removeGraphic(controller.game.id, controller.model.domain.name, name, false).then(function() {
				notify.success("UI_NETWORK_ADMIN.GAME.EDIT.GRAPHIC_REMOVED_SUCCESS");
				controller.model.forceImageReload=new Date().getTime();
			}).catch(function(error) {
				errors.catch("Could not remove image.", false)(error)
			}).finally(function () {
				bsLoadingOverlayService.stop({referenceId:controller.referenceId+"_"+name});
			});
		}

		controller.fieldsCDNExternalGraphic = [{
			key: 'url',
			type: 'input',
			optionsTypes: ['editable'],
			templateOptions : {
				label: "URL",
				description: "The URL where the casino image is located",
				required: true
			},
			expressionProperties: {
				'templateOptions.label': '"UI_NETWORK_ADMIN.GAME.IMAGES.CDN_EXTERNAL.CASINO.FIELDS.URL.LABEL" | translate',
				'templateOptions.description': '"UI_NETWORK_ADMIN.GAME.IMAGES.CDN_EXTERNAL.CASINO.FIELDS.URL.DESCRIPTION" | translate'
			},
			validators: {
				urlValid: {
					expression: function($viewValue, $modelValue, scope) {
						var value = $modelValue || $viewValue;
						return /(ftp|http|https):\/\/(\w+:{0,1}\w*@)?(\S+)(:[0-9]+)?(\/|\/([\w#!:.?+=&%@!\-\/]))?/.test(value);
					},
					message: '($viewValue != undefined && $viewValue != "")? $viewValue + " is not a valid URL. Make sure to enter the protocol, e.g. http:// or https://" : ""'
				}
			}
		}];

			controller.fieldsLiveCasinoLobbyImage = [{
				key: 'url',
				type: 'input',
				optionsTypes: ['editable'],
				templateOptions : {
					label: "URL",
					description: "The URL where the live casino image is located",
					required: true
				},
				expressionProperties: {
					'templateOptions.label': '"UI_NETWORK_ADMIN.GAME.IMAGES.CDN_EXTERNAL.LIVE_CASINO.FIELDS.URL.LABEL" | translate',
					'templateOptions.description': '"UI_NETWORK_ADMIN.GAME.IMAGES.CDN_EXTERNAL.LIVE_CASINO.FIELDS.URL.DESCRIPTION" | translate'
				},
				validators: {
					urlValid: {
						expression: function($viewValue, $modelValue, scope) {
							var value = $modelValue || $viewValue;
							return /(ftp|http|https):\/\/(\w+:{0,1}\w*@)?(\S+)(:[0-9]+)?(\/|\/([\w#!:.?+=&%@!\-\/]))?/.test(value);
						},
						message: '($viewValue != undefined && $viewValue != "")? $viewValue + " is not a valid URL. Make sure to enter the protocol, e.g. http:// or https://" : ""'
					}
				}
			}];

		controller.saveCdnExternalGameGraphic = function() {
			if (controller.formCDNExternalGraphic.$invalid) {
				angular.element("[name='" + controller.formCDNExternalGraphic.$name + "']").find('.ng-invalid:visible:first').focus();
				notify.warning('GLOBAL.RESPONSE.FORM_ERRORS');
				return false;
			}

			bsLoadingOverlayService.start({referenceId:controller.referenceId+"_CDN_EXTERNAL"});
			gamesRest.saveCdnExternalGameGraphic(controller.game.domain.name, controller.game.id, controller.cdnExternalGameGraphic.url, false).then(function(response) {
				if (response._status === 0) {
					notify.success("UI_NETWORK_ADMIN.GAME.IMAGES.CDN_EXTERNAL.SAVE.SUCCESS");
					controller.cdnExternalGameGraphic = response.plain();
				} else {
					notify.error(response._message);
				}
			}).catch(function(error) {
				errors.catch("UI_NETWORK_ADMIN.GAME.IMAGES.CDN_EXTERNAL.SAVE.ERROR", false)(error)
			}).finally(function () {
				bsLoadingOverlayService.stop({referenceId:controller.referenceId+"_CDN_EXTERNAL"});
			});
		}

		controller.removeCdnExternalGameGraphic = function() {
			bsLoadingOverlayService.start({referenceId:controller.referenceId+"_CDN_EXTERNAL"});
			gamesRest.removeCdnExternalGameGraphic(controller.game.domain.name, controller.game.id, false).then(function(response) {
				if (response) {
					notify.success("UI_NETWORK_ADMIN.GAME.IMAGES.CDN_EXTERNAL.REMOVE.SUCCESS");
					controller.cdnExternalGameGraphic = {};
				} else {
					notify.error(response._message);
				}
			}).catch(function(error) {
				errors.catch("UI_NETWORK_ADMIN.GAME.IMAGES.CDN_EXTERNAL.REMOVE.ERROR", false)(error)
			}).finally(function () {
				bsLoadingOverlayService.stop({referenceId:controller.referenceId+"_CDN_EXTERNAL"});
			});
		}


		controller.saveLiveCasinoImage = function() {
			if (controller.formLiveCasinoImage.$invalid) {
				angular.element("[name='" + controller.formLiveCasinoImage.$name + "']").find('.ng-invalid:visible:first').focus();
				notify.warning('GLOBAL.RESPONSE.FORM_ERRORS');
				return false;
			}

			bsLoadingOverlayService.start({referenceId:controller.referenceId+"_LIVE_CASINO_LOBBY_IMAGE"});
			gamesRest.saveCdnExternalGameGraphic(controller.game.domain.name, controller.game.id, controller.liveCasinoImage.url, true).then(function(response) {
				if (response._status === 0) {
					notify.success("UI_NETWORK_ADMIN.GAME.IMAGES.CDN_EXTERNAL.SAVE.SUCCESS");
					controller.liveCasinoImage = response.plain();
				} else {
					notify.error(response._message);
				}
			}).catch(function(error) {
				errors.catch("UI_NETWORK_ADMIN.GAME.IMAGES.CDN_EXTERNAL.SAVE.ERROR", false)(error)
			}).finally(function () {
				bsLoadingOverlayService.stop({referenceId:controller.referenceId+"_LIVE_CASINO_LOBBY_IMAGE"});
			});
		}

		controller.removeLiveCasinoImage = function() {
			bsLoadingOverlayService.start({referenceId:controller.referenceId+"_LIVE_CASINO_LOBBY_IMAGE"});
			gamesRest.removeCdnExternalGameGraphic(controller.game.domain.name, controller.game.id, true).then(function(response) {
				if (response) {
					notify.success("UI_NETWORK_ADMIN.GAME.IMAGES.CDN_EXTERNAL.REMOVE.SUCCESS");
					controller.liveCasinoImage = {};
				} else {
					notify.error(response._message);
				}
			}).catch(function(error) {
				errors.catch("UI_NETWORK_ADMIN.GAME.IMAGES.CDN_EXTERNAL.REMOVE.ERROR", false)(error)
			}).finally(function () {
				bsLoadingOverlayService.stop({referenceId:controller.referenceId+"_LIVE_CASINO_LOBBY_IMAGE"});
			});
		}
		
		controller.resetModel = function() {
			controller.model = angular.copy(controller.originalModel);
		}

		controller.cancel = function() {
			$state.go('dashboard.games.list', {
				domainName: controller.selectedDomain
			});
		}

		controller.buildChannels = function(model) {
			model.channels = [];
			if (model.desktopWeb) {
				model.channels.push('desktop_web');
			}
			if (model.mobileWeb) {
				model.channels.push('mobile_web')
			}
			if (model.mobileIos) {
				model.channels.push('mobile_ios');
			}
			if (model.androidNative) {
				model.channels.push('android_native');
			}
		}

		controller.onSubmit = function() {
			bsLoadingOverlayService.start({referenceId:controller.referenceId+"_SAVE"});
			if (controller.form.$invalid) {
				angular.element("[name='" + controller.form.$name + "']").find('.ng-invalid:visible:first').focus();
				notify.warning("GLOBAL.RESPONSE.FORM_ERRORS");
				bsLoadingOverlayService.stop({referenceId:controller.referenceId+"_SAVE"});
				return false;
			}
			controller.model.labels = {};
			var p=0;
			for (p=0; p < controller.model.labelArray.length; ++p) {
				controller.model.labels[controller.model.labelArray[p].name] = angular.copy(controller.model.labelArray[p]);
				controller.model.labels[controller.model.labelArray[p].name].enabled = true;
				controller.model.labels[controller.model.labelArray[p].name].deleted = false;
				var origLabel = controller.originalModel.labels[controller.model.labelArray[p].name] || {};
				//console.log(origLabel);
				if (angular.isDefined(origLabel.value) && origLabel.value === controller.model.labels[controller.model.labelArray[p].name].value) {
					controller.model.labels[controller.model.labelArray[p].name].domainName = origLabel.domainName;
						//console.log("matching value, using original domain for label");
				} else {
					controller.model.labels[controller.model.labelArray[p].name].domainName = controller.model.domain.name;
				}
			}

			controller.buildChannels(controller.model);
//			var labels = controller.model.labels;
//			controller.model.labels = [];
//			for(var j=0; j < labels.length; ++j) {
//				var keyval = labels[j].labelName+"="+labels[j].labelValue;
//				controller.model.labels.push(keyval);
//			}

			if (controller.model.gameCurrency !== undefined && controller.model.gameCurrency !== null
					&& controller.model.gameCurrency.minimumAmountCents !== undefined && controller.model.gameCurrency.minimumAmountCents !== null) {
				controller.model.gameCurrency.minimumAmountCents = (Big(controller.model.gameCurrency.minimumAmountCents).times(100)).toString();
			}

			let ckEditorInSrcMode = false;

			for (var i in CKEDITOR.instances) {
				if (CKEDITOR.instances[i].mode === 'source') {
					ckEditorInSrcMode = true;
					break;
				}
			}

			if (ckEditorInSrcMode) {
				notify.warning("The template content editor is still in source mode. Changes will not persist. Please switch the mode of the template content editor.");
				return;
			}

			gamesRest.save(controller.model).then(function(game) {
				notify.success("UI_NETWORK_ADMIN.GAME.EDIT.SUCCESS");
				$state.go('dashboard.games.list', {
					domainName: controller.selectedDomain
				});
			}).catch(function(error) {
				errors.catch("Could not save game information.", false)(error)
			}).finally(function () {
				bsLoadingOverlayService.stop({referenceId:controller.referenceId+"_SAVE"});
			});
		}
	}
]);
