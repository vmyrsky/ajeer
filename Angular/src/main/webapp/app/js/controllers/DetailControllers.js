'use strict';

var DetailsControllers = angular.module('DetailControllers', []);

var angularPOC = angular.module('angularPOC');
angularPOC
		.controller(
				'DetailsController',
				[
						'$scope',
						'$routeParams',
						'RestServices',
						'ShareDataService',
						function($scope, $routeParams, RestServices,
								ShareDataService) {

							$scope.personId = $routeParams.personId;
							$scope.useDemoData = false;
							// Will hold the unmodified data
							$scope.originalPhonenumbersModel = [];
							$scope.person = {
								"id" : 1,
								"timestamp" : "2014-02-13T16:54:18.498",
								"names" : "Harry 'Japanese version'",
								"lastName" : "Potteruu"
							};
							$scope.person.phonenumbers = [ {
								"id" : 1,
								"timestamp" : "2014-02-12T08:50:04.916",
								"numberType" : "WORK",
								"phoneNumber" : "111-2222222",
								"description" : "Secretary"
							}, {
								"id" : 2,
								"timestamp" : "2014-02-13T15:36:04.558",
								"numberType" : "HOME",
								"phoneNumber" : "111-1111111",
								"description" : "Never answers"
							}, {
								"id" : 3,
								"timestamp" : "2014-02-25T08:46:45.612",
								"numberType" : "WORK",
								"phoneNumber" : "111-3333333",
								"description" : "Old number"
							} ];
							$scope.numberTypes = [];
							$scope.newNumber = {};

							var loadAllPersonData = function(id) {
								var successCallback = function(data) {
									console.log("Got person info: "
											+ JSON.stringify(data));
									// The phoneNumbers could/would actually be
									// part of
									// the person data, but we wanted to prevent
									// unnecessary loading in JPA, so we needed
									// to do a
									// small manual maneuver to include the
									// numbers as
									// additional payload
									$scope.person = data.payload.person[0];
									$scope.person.phonenumbers = data.payload.phonenumber;
									$scope.originalPhonenumbersModel = angular
											.copy($scope.person.phonenumbers);
									$scope.phonenumberForm.$setPristine();
								};
								var failCallback = function() {
									var msg = "Failed to get person details";
									console.log(msg);
									ShareDataService.addMessage(msg, "ERROR");
								};
								if (!$scope.useDemoData) {
									RestServices.getSinglePerson(
											successCallback, failCallback, id);
								} else {
									console.log("Using demo data");
								}
							};

							var loadPersonPhonenumbers = function(id) {
								var successCallback = function(data) {
									console.log("Got person phonenumbers: "
											+ JSON.stringify(data));
									$scope.person.phonenumbers = data.payload.phonenumber;
								};
								var failCallback = function() {
									var msg = "Failed to get person phone numbers";
									console.log(msg);
									ShareDataService.addMessage(msg, "ERROR");
								};
								if (!$scope.useDemoData) {
									RestServices.getPersonPhoneNumbers(
											successCallback, failCallback, id);
								} else {
									console.log("Using demo data");
								}
							};

							$scope.getDetails = function(id) {
								// Use existing person data if available
								if (ShareDataService.getPerson() != '') {
									var setId = ShareDataService.getPerson().id;
									if (setId == $scope.personId) {
										// This should have the person data set
										// when
										// coming here from the main page via id
										// link
										if (!$scope.useDemoData) {
											$scope.person = ShareDataService
													.getPerson();
										} else {
											console.log("Using demo data");
										}
										// Load the phone numbers data
										// The phone numbers data could be
										// cached for
										// later use, but let's not implement
										// that
										console.log("loadPersonPhonenumbers");
										loadPersonPhonenumbers(id);
									} else {
										// Load the person details, including
										// the phone
										// numbers
										// Again all this could had been cached
										// by the
										// main page, but well take advantage on
										// using
										// the payload with the response
										console.log("loadAllPersonData for: "
												+ $scope.personId);
										loadAllPersonData($scope.personId);
									}
								} else {
									// Load the person details, including the
									// phone
									// numbers
									// Again all this could had been cached by
									// the
									// main page, but well take advantage on
									// using
									// the payload with the response
									console.log("loadAllPersonData for: "
											+ $scope.personId);
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
									var msg = "Failed to add phone number";
									console.log(msg);
								};

								RestServices.addPhoneNumber(addNumberSuccess,
										addNumberFail, $scope.newNumber,
										$scope.personId);
							};

							$scope.removeNumber = function(id) {
								console.log("Remove phone number by id: " + id);
								var removeSuccess = function(data) {
									$scope.getDetails($scope.personId);
								};
								var removeFail = function() {
									// show message
									var msg = "Failed to remove phone number";
									console.log(msg);
								};
								RestServices.removePhoneNumber(removeSuccess,
										removeFail, id);
							};

							$scope.getPhonenumberTypes = function() {
								console.log("Get Number types");
								var getTypesSuccess = function(data) {

									$scope.numberTypes = data.payload.keyValuePair;
									// Set the 1st value selected by default
									$scope.newNumber.numberType = $scope.numberTypes[0];
								};
								var getTypesFail = function() {
									// show message
									var msg = "Failed to get phone number types";
									console.log(msg);
									ShareDataService.addMessage(msg, "ERROR");
								};
								RestServices.getPhonenumberTypes(
										getTypesSuccess, getTypesFail);
							};

							// Cancel all modification made to persons data
							$scope.cancelPhonenumberChanges = function() {
								// Note: We could also just reload everything,
								// but we save a little bit of network traffic
								// this way
								$scope.person.phonenumbers = angular
										.copy($scope.originalPhonenumbersModel);
								$scope.phonenumberForm.$setPristine();
							};

							// Save all modification made to persons data
							$scope.savePhonenumberChanges = function() {

								console.log("Update all phone numbers: "
										+ JSON.stringify($scope.person));
								var saveSuccess = function(data) {
									// Reload the data
									$scope.getDetails($scope.personId);
								};
								var saveFail = function() {
									var msg = "Failed to save new phone number";
									console.log(msg);
									ShareDataService.addMessage(msg, "ERROR");
								};
								// We could select only the modified data to be
								// sent, but the JPA will have only the actual
								// changes persisted (so we can be lazy here)
								RestServices.savePhoneNumberChanges(saveSuccess,
										saveFail, $scope.person);
							};

							$scope.getDetails($scope.personId);
							$scope.getPhonenumberTypes();
						} ]);