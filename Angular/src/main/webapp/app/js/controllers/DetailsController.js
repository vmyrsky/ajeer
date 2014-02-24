'use strict';

var DetailsControllers = angular.module('DetailsControllers', []);

var angularPOC = angular.module('angularPOC');
angularPOC.controller('DetailsController',
		[
				'$scope',
				'$routeParams',
				'RestServices',
				'SharePersonDataService',
				function($scope, $routeParams, RestServices,
						SharePersonDataService) {

					$scope.personId = $routeParams.personId;
					$scope.person = {};
					$scope.person.phonenumbers = [];
					$scope.numberTypes = [];
					$scope.newNumber = {};

					var loadAllPersonData = function(id) {
						var successCallback = function(data) {
							console.log("Got person info: "
									+ JSON.stringify(data));
							// The phoneNumbers could/would actually be part of
							// the person data, but we wanted to prevent
							// unnecessary loading in JPA, so we needed to do a
							// small manual maneuver to include the numbers as
							// additional payload
							$scope.person = data.payload.person[0];
							$scope.person.phonenumbers = data.payload.phonenumber;
						};
						var failCallback = function() {
							console.log("Could not get person details");
						};
						RestServices.getSinglePerson(successCallback,
								failCallback, id);
					};

					var loadPersonPhonenumbers = function(id) {
						var successCallback = function(data) {
							console.log("Got person phonenumbers: "
									+ JSON.stringify(data));
							$scope.person.phonenumbers = data.payload.phonenumber;
						};
						var failCallback = function() {
							console.log("Could not get person phone numbers");
						};
						RestServices.getPersonPhoneNumbers(successCallback,
								failCallback, id);
					};

					$scope.getDetails = function(id) {
						// Use existing person data if available
						if (SharePersonDataService.getPerson() != '') {
							var setId = SharePersonDataService.getPerson().id;
							if (setId == $scope.personId) {
								// This should have the person data set when
								// coming here from the main page via id link
								$scope.person = SharePersonDataService
										.getPerson();
								// Load the phone numbers data
								// The phone numbers data could be cached for
								// later use, but let's not implement that
								console.log("loadPersonPhonenumbers");
								loadPersonPhonenumbers(id);
							} else {
								// Load the person details, including the phone
								// numbers
								// Again all this could had been cached by the
								// main page, but well take advantage on using
								// the payload with the response
								console.log("loadAllPersonData for: " + $scope.personId);
								loadAllPersonData($scope.personId);
							}
						} else {
							// Load the person details, including the phone
							// numbers
							// Again all this could had been cached by the
							// main page, but well take advantage on using
							// the payload with the response
							console.log("loadAllPersonData for: " + $scope.personId);
							loadAllPersonData($scope.personId);
						}
					};

					$scope.addNumber = function() {
						console.log("add number clicked: "
								+ JSON.stringify($scope.newNumber));
						var addNumberSuccess = function() {
							$scope.getDetails($scope.personId);
							$scope.newNumber = {};
							$scope.newNumber.numberType = $scope.numberTypes[0];
						};
						var addNumberFail = function() {
							console.log("Failed to add new phone number");
						};

						RestServices.addPhoneNumber(addNumberSuccess,
								addNumberFail, $scope.newNumber,
								$scope.personId);
					};

					$scope.removeNumber = function(id) {
						console.log("Remove phone number by id: " + id);
						var removeSuccess = function(data) {
							if (data.responseStatus == 'OK') {
								$scope.message = data.description;
								$scope.messageStyle = data.responseStatus;
							} else {
								$scope.message = data.description;
								$scope.messageStyle = data.responseStatus;
							}
							$scope.getDetails($scope.personId);
						};
						var removeFail = function() {
							// show message
							$scope.message = 'Remove failed';
							$scope.messageStyle = 'ERROR';
						};
						RestServices.removePhoneNumber(removeSuccess, removeFail, id);
					};
					
					$scope.getPhonenumberTypes = function() {
						console.log("Get Number types");
						var getTypesSuccess = function(data) {
							if (data.responseStatus == 'OK') {
								$scope.message = data.description;
								$scope.messageStyle = data.responseStatus;
								$scope.numberTypes = data.payload.keyValuePair;
								// Set the 1st value selected by default
								$scope.newNumber.numberType = $scope.numberTypes[0];
							} else {
								$scope.message = data.description;
								$scope.messageStyle = data.responseStatus;
							}
						};
						var getTypesFail = function() {
							// show message
							$scope.message = 'Get phone number types failed';
							$scope.messageStyle = 'ERROR';
						};
						RestServices.getPhonenumberTypes(getTypesSuccess, getTypesFail);
					}

					$scope.getDetails($scope.personId);
					$scope.getPhonenumberTypes();
				} ]);