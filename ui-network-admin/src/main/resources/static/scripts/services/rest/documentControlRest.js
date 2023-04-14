'use strict';

angular.module('lithium-rest-document', ['restangular'])
.factory('rest-document', ['$log', 'Restangular', '$http',
	function($log, Restangular, $http) {
		try {
			var service = {};

			var rest = Restangular.withConfig(function(RestangularConfigurer) {
				RestangularConfigurer.setBaseUrl('services/service-document');
			});

			service.saveDocumentType = function(domainName, documentType) {
				return rest.all("backoffice").all("document-type").one(domainName).all('save').customPUT(documentType);
			}

			service.listDocumentTypes = function(domainName) {
				return rest.all("backoffice").all("document-type").one(domainName).one('list').get();
			}

			service.changelogs = function (domainName, entityId, page) {
				return rest.all("backoffice").all("document-type").one(domainName).one('changelogs').get({ p: page });
			}

			service.listUserDocuments = function(domainName, ownerGuid, sensitive) {
				return rest.all("backoffice").all("document").one(domainName).one(sensitive ? 'per-user-sensitive' : 'per-user').get({ownerGuid: ownerGuid});
			}

			service.getDocumentFile = function(domainName, documentFileId) {
				return rest.all("backoffice").all("document").one(domainName).one('get-document-file').get({documentFileId: documentFileId});
			}

			service.availableTypes = function(domainName, internalOnly) {
				return rest.all("backoffice").all("document").one(domainName).one("available-types").get({internalOnly: internalOnly});
			}

			service.availableReviewReasons = function(domainName) {
				return rest.all("backoffice").all("document").one(domainName).one('available-review-reasons').get();
			}

			service.updateDocument = function(domainName, documentInfo, sensitivity) {
				return rest.all("backoffice").all("document").one(domainName).one(sensitivity).one('update').post('', documentInfo);
			}

			service.uploadDocument = function(domainName, ownerGuid, file, document, sensitivity){
				var uploadUri= "services/service-document/backoffice/document/" + domainName + "/" + sensitivity + "/upload";
				var fd = new FormData();
				fd.append('ownerGuid', ownerGuid)
				fd.append('documentTypeId', document.typeId);
				fd.append('reviewStatusName', document.reviewStatus);
				if (document.reviewReason !== undefined) {
					fd.append('reviewReasonId', document.reviewReason);
				}
				if (document.sensitive !== undefined) {
					fd.append('sensitive', document.sensitive);
				}
				fd.append('file', file);
				return $http.post(uploadUri, fd, {
					transformRequest: angular.identity,
					headers: {'Content-Type': undefined, 'Transfer-Encoding': 'chunked'}
				});
			}

			service.deleteDocument = function(domainName, documentId) {
				return rest.all("backoffice").all("document").one(domainName).one('delete').remove({id:documentId});
			}

			return service;
		} catch (err) {
			$log.error(err);
			throw err;
		}
	}
]);
