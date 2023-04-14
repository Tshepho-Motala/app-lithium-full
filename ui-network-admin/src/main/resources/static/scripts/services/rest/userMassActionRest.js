'use strict';

angular.module('lithium-rest-user-mass-action', ['restangular'])
.factory('rest-user-mass-action', ['$log', 'Restangular',
	function($log, Restangular) {
		try {
			var service = {};
			var userMassActionService = Restangular.withConfig(function(RestangularConfigurer) {
				RestangularConfigurer.setBaseUrl('services/service-user-mass-action/backoffice/');
			});

			service.uploadCsv = function(model, file) {
				var fd = new FormData();
				fd.append('file', file);
				if (model.action.uploadType.startsWith('BONUS')) {
					fd.append('bonusCode', model.bonusCode);
					fd.append('defaultBonusAmount', model.defaultAmount);
					fd.append('allowDuplicates', model.allowDuplicates);
					fd.append('bonusDescription', model.bonusDescription);
				}
				return userMassActionService.all(model.domainName).all(model.action.uploadType).all("upload").one("csv")
					.withHttpConfig({transformRequest: angular.identity})
					.customPOST(fd, undefined, undefined, { 'Content-Type': undefined });
			}

			service.monitorFileUploadProgress = function(model, stageName) {
				return userMassActionService.all(model.domainName).all(model.action.uploadType).all("progress").one(stageName).get({id: model.action.id});
			}

			service.getFileUploadStatus = function(model) {
				return userMassActionService.all(model.domainName).all(model.action.uploadType).one("in-progress").get({id: model.action.id});
			}

			service.processFileUpload = function(model) {
				return userMassActionService.all(model.domainName).all(model.action.uploadType).all("process").post(service.buildActionsRequest(model));
			}

			service.getFileUploadSummary = function(model) {
				return userMassActionService.all(model.domainName).all(model.action.uploadType).one("summary").get({id: model.action.id});
			}

			service.removeFileDataRecord = function(model, rowNumber) {
				return userMassActionService.all(model.domainName).all(model.action.uploadType).all("remove").one("table", rowNumber).remove({id: model.action.id});
			}

			service.buildActionsRequest = function(model) {
				let actionsRequest = {
					fileUploadId: model.action.id,
					actions: []
				};

				if (model.bonusCode) {
					actionsRequest.actions.push("GRANT_BONUS")
					actionsRequest.bonusCode = model.bonusCode;
					actionsRequest.defaultBonusAmount = model.defaultAmount;
					actionsRequest.bonusDescription = model.bonusDescription;
					actionsRequest.allowDuplicates = model.allowDuplicates;
				}

				if (model.status) {
					actionsRequest.actions.push("CHANGE_STATUS")
					actionsRequest.status = model.statuses.find(s => s.id === model.status).name;
					if (model.statusReason) {
						actionsRequest.statusReason = model.statusReason;
					}
					if (model.statusComment) {
						actionsRequest.statusComment = model.statusComment;
					}
				}

				if (model.verificationStatus || model.ageVerified !== undefined || model.addressVerified !== undefined) {
					actionsRequest.actions.push("CHANGE_VERIFICATION_STATUS")
					actionsRequest.verificationStatus = model.verificationStatus;
					actionsRequest.ageVerified = model.ageVerified;
					actionsRequest.addressVerified = model.addressVerified;
					if (model.verificationStatusComment) {
						actionsRequest.verificationStatusComment = model.verificationStatusComment;
					}
				}

				if (model.biometricsStatus) {
					actionsRequest.actions.push("CHANGE_BIOMETRICS_STATUS")
					actionsRequest.biometricsStatus = model.biometricsStatus;
					if (model.biometricsStatusComment) {
						actionsRequest.biometricsStatusComment = model.biometricsStatusComment;
					}
				}

				if (model.accessRule) {
					actionsRequest.actions.push("PROCESS_ACCESS_RULE")
					actionsRequest.accessRule = model.accessRule;
				}

				if (model.testPlayer === false || model.testPlayer === true) {
					actionsRequest.actions.push("MARK_AS_TEST_PLAYER");
					actionsRequest.testPlayer = model.testPlayer;
				}

				if (model.tagOptions === 'add') {
					if (model.add && model.add.length > 0) {
						actionsRequest.actions.push("ADD_PLAYER_TAGS");
						actionsRequest.addTags = [];
						for (let i = 0; i < model.add.length; i++) {
							actionsRequest.addTags.push(model.add[i].id)
						}
					}
				}

				if (model.tagOptions === 'replace') {
					if (model.replaceFrom && model.replaceTo) {
						actionsRequest.actions.push("REPLACE_PLAYER_TAGS");
						actionsRequest.replaceTagFrom = model.replaceFrom;
						actionsRequest.replaceTagTo = model.replaceTo;
					}
				}

				if (model.tagOptions === 'remove-all') {
					actionsRequest.actions.push("REMOVE_ALL_PLAYER_TAGS");
				}

				if (model.tagOptions === 'remove') {
					if (model.remove && model.remove.length > 0) {
						actionsRequest.actions.push("REMOVE_PLAYER_TAGS");
						actionsRequest.removeTags = [];
						for (let i = 0; i < model.remove.length; i++) {
							actionsRequest.removeTags.push(model.remove[i].id)
						}
					}
				}

				if (model.category ||  model.subCategory || model.priority >= 0 || model.text) {
					actionsRequest.actions.push("ADD_NOTE");
					actionsRequest.noteCategory = model.category;
					actionsRequest.noteSubCategory = model.subCategory;
					actionsRequest.notePriority = model.priority;
					actionsRequest.noteComment = model.text;
				}

				if (model.adjustment > 0 || model.adjustment < 0 || model.trantypeacct || model.adjustComment) {
					actionsRequest.actions.push("BALANCE_ADJUSTMENT");
					actionsRequest.adjustmentAmountCents = model.adjustment;
					actionsRequest.adjustmentTransactionTypeCode = model.trantypeacct;
					actionsRequest.adjustmentComment = model.adjustComment;
				}
				if(typeof model.restrictionAction === 'string' && model.restrictionAction.length > 0) {

					const actions = {
						lift: 'LIFT_PLAYER_RESTRICTIONS',
						place: 'PLACE_PLAYER_RESTRICTIONS'
					}

					const action = actions[model.restrictionAction.toLowerCase()] || null
					if(action !== null && model.restrictions) {
						const playerRestrictions = {
							restrictions: Array.isArray(model.restrictions) ? model.restrictions.map(restriction => restriction.id) : [model.restrictions.id],
							reason: actionsRequest.playerRestrictionsReason  = model.restrictionReason || '',
							subType: model.subordinate
						}

						actionsRequest.playerRestrictions = JSON.stringify(playerRestrictions)
						actionsRequest.actions.push(action);
					}

				}
				return actionsRequest;
			}


			return service;
		} catch (err) {
			$log.error(err);
			throw err;
		}
	}
]);
