'use strict';

angular.module('lithium')
.factory('UserRest', ['$log', 'Restangular', 'DTOptionsBuilder', '$http', '$rootScope', '$timeout', '$userService',
	function($log, Restangular, DTOptionsBuilder, $http, $rootScope, $timeout, $userService) {
		try {
			var service = {};
			var userPasswordReset = {token: "", password:""};
			var callAtTimeoutModal = null;

			var rest = function(domainName) {
				return Restangular.withConfig(function(RestangularConfigurer) {
					RestangularConfigurer.setBaseUrl("services/service-user/" + domainName +"/");
				}).service("users");
			}

			var commentRest = function(domainName) {
				return Restangular.withConfig(function(RestangularConfigurer) {
					RestangularConfigurer.setBaseUrl("services/service-user/" + domainName +"/");
				}).service("comments");
			}
			
			var tagRest = Restangular.withConfig(function(RestangularConfigurer) {
				RestangularConfigurer.setBaseUrl("services/service-user/backoffice/players/tag/");
			});

			var onHoldRest = function() {
				return Restangular.withConfig(function(RestangularConfigurer) {
					RestangularConfigurer.setBaseUrl("services/service-user/backoffice/onhold/");
				});
			}

			var restrictionRest = Restangular.withConfig(function(RestangularConfigurer) {
				RestangularConfigurer.setBaseUrl("services/service-limit/backoffice/restrictions/");
			});

			let backofficePlayersRest = function (domainName) {
				return Restangular.withConfig(function (RestangularConfigurer) {
					RestangularConfigurer.setBaseUrl(
							"services/service-user/backoffice/players/" + domainName + "/");
				});
			}
			var allPlayersRest = Restangular.withConfig(function(RestangularConfigurer) {
				RestangularConfigurer.setBaseUrl("services/service-user/players/");
			});

			var adminPlayersRest = Restangular.withConfig(function(RestangularConfigurer) {
				RestangularConfigurer.setBaseUrl("services/service-user/admin/");
			});

			var passwordRest = Restangular.withConfig(function (RestangularConfigurer) {
				RestangularConfigurer.setBaseUrl('services/service-user/');
			});

			// var userLinkRest = Restangular.withConfig(function (RestangularConfigurer) {
			// 	RestangularConfigurer.setBaseUrl('services/service-user/backoffice/');
			// });
			var userLinkRest = function() {
				return Restangular.withConfig(function(RestangularConfigurer) {
					RestangularConfigurer.setBaseUrl('services/service-user/backoffice/');
				});
			}

			service.addUserLink = function(primaryUserGuid, secondaryUserGuid, userLinkTypeCode, linkNote) {
				return userLinkRest().all("user-link").get("add-user-link", {primaryUserGuid: primaryUserGuid, secondaryUserGuid: secondaryUserGuid, userLinkTypeCode: userLinkTypeCode, linkNote: linkNote});
			}

			service.userLinkTypeList = function() {
				return userLinkRest().all("user-link").all("type-list").getList();
			}

			service.updateUserLink = function(userLinkId, linkNote, deleted) {
				return userLinkRest().all("user-link").get("update-user-link", {userLinkId: userLinkId, linkNote: linkNote, deleted: deleted});
			}

			service.modifyUserLink  = (userLinkId, linkTypeCode, linkNote ) => {
				return userLinkRest().all('user-link').one(`${userLinkId}`).put({linkTypeCode, linkNote})
			}
			
			service.playerLinks = function(userGuid) {
				return userLinkRest().all("user-link").get("list-by-user", {userGuid: userGuid});
			}

			service.passwordReset = function(domain, emailAddress,name) {
				return passwordRest.one("passwordreset").one("1").one("backoffice").customPOST({},'', {domainName: domain,email:emailAddress,username: name,mobile: null,type: 'email',token: 'n',tokenLength: 5,dateOfBirth: null},{});
			}

			service.passwordResetPt2 = function(domain, emailAddress, token, password, username) {
				userPasswordReset.token = token;
				userPasswordReset.password = password;
				return passwordRest.one("passwordreset").one("2").customPOST(userPasswordReset,'', {domainName: domain, email: emailAddress, username: username},{});
			}

			service.clearFailedResetCount = function(playerGuid) {
				return adminPlayersRest.all("passwordreset").all("clearFailedResetCount").post('', {playerGuid:playerGuid});
			}

			service.resetPasswordEmail = function() {
				return adminPlayersRest.all("passwordreset").all("clearFailedResetCount").post('', {playerGuid:playerGuid});
			}

			service.searchAllPlayers = function(search) {
				return allPlayersRest.all("search").getList({ search: search });
			}
			
			service.findTagById = function(id) {
				return tagRest.one("view").one(id).get();
			}
			service.findAllTags = function(domains) {
				return tagRest.all('list').getList({domainNames:domains});
			}

			service.findAllTagsWithIds = function(domain, ids) {
				return tagRest.all('get-categories-from-ids').getList({domainName:domain, categoryIds: ids});
			}

			service.removeTag = function(id) {
				return tagRest.all('removetag').remove({id: id});
			}
			service.tagAddUpdate = function(model) {
				return tagRest.all('').post(model);
			}
			service.tagAddPlayer = function(tagId, username) {
				return tagRest.all("addplayer").post('', {tagId:tagId, username:username});
			}
			service.tagRemovePlayer = function(tagId, username) {
				return tagRest.all("removeplayer").remove({tagId:tagId, username:username});
			}

			service.findRestrictions = function(domains) {
				return restrictionRest.all('list').getList({domains:domains, enabled:'true'});
			}

			service.findById = function(domainName, id) {
				return rest(domainName).one(id).get();
			}

			service.findAdditionalDataByUserGuid = function(domainName, id) {
				return rest(domainName).one(id).one("additionaldata").get();
			}

			service.findFromGuid = function(domainName, guid) {
				return rest(domainName).one("findFromGuid").get({ guid: guid });
			}

			service.findUsersByGuidsOrUsernames = function(domainName, usernamesAndGuids) {
				return rest(domainName).one("find-users-by-usernames-or-guids").post('',usernamesAndGuids);
			}
			
			service.isUnique = function(domainName, username, userId) {
				return rest(domainName).one("isunique").get({ username: username, id: userId });
			}
			
			service.add = function(domainName, user) {
				return rest(domainName).post(user);
			}

			/// /UsersController
			service.search = function(domainName, username) {
				return rest(domainName).one().getList("list", { search: username });
			}

			// service.guidtouserid = function(domainName, username) {
			// 	return rest(domainName).one(username).one("guidtouserid").get();
			// }
			
			service.save = function(domainName, user) {
				/// /UserController
			return rest(domainName).one().all(user.id).post(user);
			}

			service.saveAdditionalDataByUserGuid = function(domainName, id, additionalData) {
				return rest(domainName).one(id).all("additionaldata").post(additionalData);
			}
			
			service.saveAddress = function(domainName, address) {
				/// /UserController
			return rest(domainName).one(address.userId).all("saveaddress").post(address);
			}
			
			service.saveStatus = function(domainName, statusUpdate) {
				/// /UserController
			return rest(domainName).one(statusUpdate.userId).all("savestatus").post(statusUpdate);
			}

			service.updateFailedLoginBlock = function(domainName, userId, blockStatus) {
			return rest(domainName).one(userId).all("update-failed-login-block").one(blockStatus).post();
			}

			service.saveVerificationStatus = function(domainName, statusUpdate) {
				/// /UserController
			return rest(domainName).one(statusUpdate.userId).all("saveverificationstatus").post(statusUpdate);
			}

			service.saveBiometricsStatus = function (domainName, statusUpdate) {
				/// /UserController
				return rest(domainName).one(statusUpdate.userId).all("biometrics-status").customPUT(statusUpdate);
			}

			service.savePassword = function (domainName, userId, passwd) {
				/// /UserController
				return rest(domainName).one(userId).all("changepassword").post(passwd);
			}

            service.changedateofbirth = function (domainName, userChanges) {
                /// /UserController
                return rest(domainName).one(userChanges.userId).all("changedateofbirth").post(userChanges);
            }

			service.updateUserPlaceOfBirth = function (domainName, userChanges) {
				return rest(domainName).one(userChanges.id).all("updateplaceofbirth").post(userChanges);
			}

			service.toggleAutoWithdrawalAllowed = function (domainName, userId) {
				return rest(domainName).one(userId).all("toggleAutoWithdrawalAllowed").post();
			}

			service.toggleEmailValidation = function (domainName, userId) {
				return rest(domainName).one(userId).all("toggleEmailValidation").post();
			}

			service.toggleSowValidation = function (domainName, userId) {
				return rest(domainName).one(userId).all("toggle-sow-validation").post();
			}

			service.toggleMobileValidation = function (domainName, userId) {
				return rest(domainName).one(userId).all("toggleMobileValidation").post();
			}

			service.toggleAgeVerification = function (domainName, userId) {
				return rest(domainName).one(userId).all("toggleAgeVerification").post();
			}

			service.toggleAddressVerification = function (domainName, userId) {
				return rest(domainName).one(userId).all("toggleAddressVerification").post();
			}

			service.changelogs = function (domainName, entityId, page) {
				return rest(domainName).one(entityId).one("changelogs").get({p: page});
			}

			service.comment = function (domainName, entityId, comment) {
				return commentRest(domainName).one(entityId).all("add").post(comment);
			}

			service.lastComment = function (domainName, entityId) {
				return commentRest(domainName).one(entityId).one("last").get();
			}

			service.documentsExternal = function (domainName, entityId) {
				return rest(domainName).one("documents").one("admin").getList("listUserDocumentsExternal", {userId: entityId});
			}

			service.documentsInternal = function (domainName, entityId) {
				return rest(domainName).one("documents").one("admin").getList("listUserDocumentsInternal", {userId: entityId});
			}
			
			service.createDocument = function(domainName, name, statusString, documentFunction, external, userId) {
				return rest(domainName).one("documents").one("admin").post("createDocument", {}, {name: name, statusString: statusString, documentFunction: documentFunction, userId: userId, external: external});
			}
			
			service.updateDocument = function(domainName, document) {
				return rest(domainName).one("documents").one("admin").post("updateDocument", document);
			}
			
			service.opt = function(domainName, userId, method, optOut) {
				return rest(domainName).one(userId).all('opt').all(method).all(optOut).post();
			}

			service.redoEmailValidation = function (domainName, userId, email) {
				return backofficePlayersRest(domainName).all(userId).all('redo-email-validation').post({}, {email: email});
			}

			service.redoEmailValidationV2 = function (domainName, userId, email) {
				return backofficePlayersRest(domainName).all(userId).all('v2').all('redo-email-validation').post({}, {email: email});
			}

			service.redoMobilePhoneValidation = function (domainName, userId, email) {
				return backofficePlayersRest(domainName).all(userId).all('redo-mobile-phone-validation').post({}, {email: email});
			}

			service.setTest = function (domainName, userId, isTest) {
				return rest(domainName).one(userId).all("test-account").all(isTest).post();
			}

			service.getPlayerPlayTimeLimitConfig = function (guid) {
				return userLinkRest().allUrl("/playtime-limit/v2/configuration/get?playerGuid="+guid).post({}, {});
			}
			//LSPLAT-5758 Restangular was not returning the data from both POST and GET methods had to resort to using $http
			service.getPlayerPlayTimeLimitConfigHttp = async function(guid, domain) {
				$userService._refreshToken();
				$userService._retrieveDomainSettings(domain);
				const request = {
					method: 'POST',
					url: "/services/service-user/backoffice/playtime-limit/v2/configuration/get?playerGuid="+guid,
					headers: {
						"Authorization": 'Bearer ' + $rootScope.token,
						"Content-type": "application/json"
					},
					data: $.param({})
				}
				return await $http(request).then(function(data) {
					return data;
				}, function(error) {
					console.error(error);
				});
			}
			//LSPLAT-5758 Restangular was not returning the data from both POST and GET methods had to resort to using $http
			service.setPlayerPlayTimeLimitConfigHttp = async function(userId, granularity, secondsAllocated) {
				$userService._refreshToken();
				const request = {
					method: 'POST',
					url: "/services/service-user/backoffice/playtime-limit/v2/configuration/set",
					headers: {
						"Authorization": 'Bearer ' + $rootScope.token,
						"Content-type": "application/json"
					},
					data: {
						userId: userId,
						granularity: granularity,
						secondsAllocated: secondsAllocated,
					}
				}
				return await $http(request).then(function(data) {
					return data;
				}, function(error) {
					console.error(error);
				});
			}
			//LSPLAT-5758 Restangular was not returning the data from both POST and GET methods had to resort to using $http
			service.updateAndGetPlayerEntryHttp = async function (guid) {
				const request = {
					method: 'POST',
					url: "/services/service-user/backoffice/playtime-limit/v2/active-entry/get?playerGuid="+guid,
					headers: {
						"Authorization": 'Bearer ' + $rootScope.token,
						"Content-type": "application/json"
					},
					data: $.param({})
				}
				return await $http(request).then(function (data) {
					return data;
				}, function (error) {
					console.error(error);
				});
			}


			service.getActivePlayerTimeLimitGranularities = function () {
				return userLinkRest().all("playtime-limit").all('v2').all('active-granularity').all("get").getList();
			}

			service.updateAndGetPlayerEntry = function (guid) {
				return userLinkRest().all("playtime-limit").all('v2').all('active-entry').all("get").post({}, {playerGuid: guid});
			}

			service.setPlayerPlayTimeLimitConfig = function (userId, granularity, secondsAllocated) {
				return userLinkRest().allUrl("playtime-limit/v2/configuration/set").post(
						{
							userId: userId,
							granularity: granularity,
							secondsAllocated: secondsAllocated,
						}
				);
			}

			service.removePendingConfigPlayTimeLimit = function (id) {
				return userLinkRest().allUrl('playtime-limit/v2/configuration/remove-pending').post(
						{}, {configId: id});
			}
			
			return service;
		} catch (err) {
			$log.error(err);
			throw err;
		}
	}
]);
