'use strict';

var DetailsControllers = angular.module('MessageControllers', []);

var angularPOC = angular.module('angularPOC');
angularPOC
		.controller(
				'MessagesController',
				[
						'$scope',
						'$routeParams',
						'RestServices',
						'ShareDataService',
						function($scope, $routeParams, RestServices,
								ShareDataService) {

							$scope.messageText = ["test"];
							$scope.messageTypeClass = "INFO";
							
							
						} ]);