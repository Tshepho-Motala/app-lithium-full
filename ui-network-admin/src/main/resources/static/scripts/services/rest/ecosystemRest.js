'use strict';

angular.module('lithium')
.factory('EcosysRest', ['$log', 'Restangular',
	function($log, Restangular) {
		try {
			var service = {};
			var rest = function() {
				return Restangular.withConfig(function(RestangularConfigurer) {
					RestangularConfigurer.setBaseUrl("services/service-domain/backoffice/ecosystem/");
				});
			}
            
			service.ecosystems = function() {
				return rest().all("list").getList();
			}
			
			service.relationshiptypes = function() {
				return rest().all("relationship-type").all("list").getList();
			}

			service.domainRelationship = function(ecosystemName) {
				return rest().all("domain-relationship").all("list").getList({ ecosystemName: ecosystemName});
			}
			service.addModifyEcosystems = function(ecosystem) {
				return rest().all("create-or-modify").post(ecosystem);
			}
			
			service.remove = function(id) {
				return rest().all("domain-relationship").all("remove").getList({ ecosystemDomainRelationshipId: id});
			}

			service.editDisableRootWelcomeEmail = function(id, disableRootWelcomeEmail) {
				return rest().all("domain-relationship").all("edit").all('disable-root-welcome-email').getList({ ecosystemDomainRelationshipId: id, status: disableRootWelcomeEmail});
			}

			service.domainRelationshipAdd = function(ecosystemId, domainId, relationshipTypeId, deleted, enabled) {
				var params = {ecosystemId: ecosystemId, domainId: domainId, relationshipTypeId: relationshipTypeId, deleted: deleted, enabled: enabled};
				return rest().all("domain-relationship").get("add", params);
			}

			service.ecosystemRelationshiplistByDomainName = function(domainName) {
				return rest().all("domain-relationship").all("list-by-domain-name").post('', {domainName: domainName});
			}

			return service;
		} catch (err) {
			$log.error(err);
			throw err;
		}
	}
]);
