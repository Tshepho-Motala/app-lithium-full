'use strict';

angular.module('lithium')
    .directive('promotionUserCategory', function() {
        return {
            templateUrl:'scripts/directives/missions/category/index.html',
            scope: {
                missionId: '<',
                missionRevision: '=',
                allowDelete: '<',
                allowAdd: '<' ,
                persist: '<'
            },
            restrict: 'E',
            replace: true,
            controller: ['UserRest', 'PromotionRest', '$uibModal', '$scope', 'notify', '$translate', 'errors', 'bsLoadingOverlayService',
                function(userRest, PromotionRest, $uibModal, $scope, notify, $translate, errors, bsLoadingOverlayService) {
                   

                    $scope.categories = []

                    this.$onInit = () => {
                        const userCategories = $scope.missionRevision.userCategories || []
                        const ids = userCategories.map(uc => uc.userCategoryId);
                        if(ids.length > 0) {
                            userRest.findAllTagsWithIds($scope.missionRevision.domain.name, ids).then(res => {
                               const data = res.plain()
                               $scope.categories = userCategories.map(uc => {

                                   const category = data.find(c => c.id === uc.userCategoryId);

                                   if(category) {
                                       return { name: category.name, type: uc.type.type, userCategoryId: uc.userCategoryId, id: uc.id}
                                   }
                               })
                            })
                        }
                    }


                    $scope.delete = (userCategory) => {

                        if($scope.persist) {
                            angular.confirmDialog({
                                title: "UI_NETWORK_ADMIN.MISSIONS.REMOVE_CATEGORY.TITLE",
                                message: "UI_NETWORK_ADMIN.MISSIONS.REMOVE_CATEGORY.MESSAGE",
                                confirmType: 'danger',
                                confirmButtonIcon: 'trash',
                                confirmButtonText: "GLOBAL.ACTION.DELETE",
                                onConfirm:() => {
                                    PromotionRest.deleteUserCategory($scope.missionId, userCategory.id).then(() => {
                                        $scope.missionRevision.userCategories = $scope.missionRevision.userCategories
                                                        .filter(uc => uc.userCategoryId !== userCategory.userCategoryId);
                                                        
                                        $scope.categories = $scope.categories
                                        .filter(uc => uc.userCategoryId !== userCategory.userCategoryId)
                                    });
                                }
                            })
                        }
                        else {
                            $scope.missionRevision.userCategories = $scope.missionRevision.userCategories
                                                        .filter(uc => uc.userCategoryId !== userCategory.userCategoryId);
                                                        
                            $scope.categories = $scope.categories
                            .filter(uc => uc.userCategoryId !== userCategory.userCategoryId)
                        }
                        
                    }

                    $scope.add = () => {
                        var modalInstance = $uibModal.open({
                            animation: true,
                            ariaLabelledBy: 'modal-title',
                            ariaDescribedBy: 'modal-body',
                            templateUrl: 'scripts/controllers/dashboard/missions/addcategories/index.html',
                            controller: 'MissionsAddCategoryModal',
                            controllerAs: 'controller',
                            size: 'md',
                            resolve: {
                                domainName: function() { return $scope.missionRevision.domain.name; },
                                selectedCategories: () => $scope.categories,
                                loadMyFiles: function($ocLazyLoad) {
                                    return $ocLazyLoad.load({
                                        name:'lithium',
                                        files: ['scripts/controllers/dashboard/missions/addcategories/index.js']
                                    })
                                }
                            }
                        });
                        
                        modalInstance.result.then(function(categories) {

                            if(!angular.isUndefinedOrNull(categories) && categories.length > 0) {

                                if($scope.persist) {
                                    PromotionRest.addUserCategories($scope.missionId, categories).then(res => {
                                        $scope.missionRevision.userCategories = res.edit.userCategories
                                        
                                        $scope.categories = categories.map(c => {

                                            const category = (res.edit.userCategories || []).find(uc => c.userCategoryId === uc.userCategoryId);

                                            c['id'] = category.id;

                                            return c;
                                        })
                                    })
                                }
                                else {
                                    $scope.missionRevision.userCategories = categories
                                    $scope.categories = categories
                                }
                                
                            }
                        });
                    }                  
                }
            ]
        }
    });



