'use strict';

angular.module('lithium-rest-group', ['restangular'])
.factory('rest-group', ['$log', 'Restangular',
	function($log, Restangular) {
		try {
			var service = {};
			var groupService = Restangular.withConfig(function(RestangularConfigurer) {
				RestangularConfigurer.setBaseUrl('services/service-user');
			});
			
			service.view = function(domainName, groupId) {
				return groupService.all("domain").one(domainName).all("group").one(groupId+'').get();
			}
			
			service.list = function(domainName) {
				//GroupsController
				return groupService.all("domain").one(domainName).all("groups").getList();
			}
			
			service.roles = function(domainName, groupId, searchDomainName) {
				//GroupController
				return groupService.all("domain").one(domainName).all("group").one(groupId+'').all("roles").all(searchDomainName).getList();
			}
			
			service.removeRole = function(domainName, groupId, grdId) {
				//GroupController
				return groupService.all("domain").all(domainName).all("group").all(groupId+'').all("removeRole").all(grdId).post();
			}
			service.addRole = function(domainName, groupId, grdDomain, roles) {
				//GroupController
				return groupService.all("domain").all(domainName).all("group").all(groupId+'').all("addrole").all(grdDomain).post(roles);
			}
			
			service.grdUpdateDescending = function(domainName, groupId, grdId, change) {
				//GroupController
				return groupService.all("domain").all(domainName).all("group").all(groupId+'').all("grd").all(grdId).all("change").all(change).all("d").post();
			}
			service.grdUpdateSelfApplied = function(domainName, groupId, grdId, change) {
				//GroupController
				return groupService.all("domain").all(domainName).all("group").all(groupId+'').all("grd").all(grdId).all("change").all(change).all("s").post();
			}
			
			service.grds = function(domainName, groupId) {
				//GroupController
				var promise = groupService.all("domain").one(domainName).all("group").one(groupId+'').customGET("grds");
				return promise;
			}
			
			service.enabled = function(domainName, groupId, enabled) {
				//GroupController
				return groupService.all("domain").one(domainName).all("group").one(groupId+'').all("enabled").all(enabled).post();
			}
			service.remove = function(domainName, groupId) {
				//GroupController
				return groupService.all("domain").one(domainName).all("group").one(groupId+'').all("remove").post();
			}
			
			service.save = function(domainName, groupId, model) {
				//GroupController
				return groupService.all("domain").one(domainName).all("group").all(groupId+'').post(model);
			}
			
			service.changelogs = function(domainName, entityId, page) {
				return groupService.all("domain").one(domainName).all("group").all(entityId).one("changelogs").get({ p: page });
			}
			
//			service.findById = function(id) {
//				return groupService.one(id).customGET("view");
//			}
//			
//			service.save = function(domain, name, description) {
//				var fd = new FormData();
//				fd.append('domain', domain);
//				fd.append('name', name);
//				fd.append('description', description);
//				var save = groupService.all('create')
//				.withHttpConfig({transformRequest: angular.identity})
//				.customPOST(fd, undefined, undefined, { 'Content-Type': undefined });
//				$log.info(save);
//				return save;
//			}
//			
//			service.findByDomainName = function(domainname) {
//				var groups = groupService.all("list").all("roles").all(domainname).getList();
//				$log.debug(groups);
//				return groups;
//			}
			
			return service;
		} catch (err) {
			$log.error(err);
			throw err;
		}
	}
]);