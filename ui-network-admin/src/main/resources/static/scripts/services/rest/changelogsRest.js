'use strict';

angular.module('lithium').factory('ChangelogsRest', ['Restangular',
    function (Restangular) {
        try {
            let service = {};

            service.baseUrl = 'services/service-changelog/backoffice';

            let config = Restangular.withConfig(function (RestangularConfigurer) {
                RestangularConfigurer.setBaseUrl(service.baseUrl);
            });

            service.entities = function () {
                return config.all('changelogs').all('global').one("entities").getList();
            }

            service.types = function () {
                return config.all('changelogs').all('global').one("types").getList();
            }

            service.setPriority = function (entryId, priority) {
                return config.all('changelogs').all('global').one('entry', entryId).one('priority', priority).post();
            }

            service.setPinned = function (entryId, pinned) {
                return config.all('changelogs').all('global').one('entry', entryId).one('pinned', pinned).post();
            }

            service.setDeleted = function (entryId, deleted) {
                return config.all('changelogs').all('global').one('entry', entryId).one('deleted', deleted).post();
            }

            service.changeLogWithFieldChanges = function (id) {
                return config.all('changelogs').all('global').one('entry', id).get();
            }

            service.categories = function () {
                return config.all('changelogs').all('global').one('categories').getList();
            }

            service.subCategories = function (value) {
                return config.all('changelogs').all('global').one('subcategories?category='+value).getList();
            }

            service.addNote = function (domainName, entityRecordId, model) {
                let changelog = {
                    domainName: domainName,
                    entityRecordId: entityRecordId,
                    categoryName: model.category,
                    subCategoryName: model.subCategory,
                    comments: model.text,
                    priority: model.priority
                };
                return config.all('changelogs').all('global').all("add-note").post(changelog);
            }

            service.editNotes = function (notes) {
                return config.all('changelogs').all('global').all('update-note').post(notes);
            }

            return service;
        } catch (err) {
            $log.error(err);
            throw err;
        }
    }
]);
