'use strict';

//var MainControllers = angular.module('MainControllers', []);

var angularPOC = angular.module('angularPOC');

/*
 * Main functionality for the controller layer. Define all the dependencies to
 * be injected by name ['depName', function(depName)]. Note: Mind the
 * apostrophes
 */
angularPOC
		.controller(
				'MainController',
				[
						'$scope',
						'$http',
						'$location',
						'RestServices',
						'ShareDataService',
						function($scope, $http, $location, RestServices,
								ShareDataService) {
							
							$scope.useDemoData = false;
							$scope.criteriaString = "";
							$scope.criteriaTypes = [];
							$scope.criteriaType = $scope.criteriaTypes[0];
							$scope.language = "fi"; // Default, see i18n/i18n.js
							// for other options
							$scope.style = "default";
							$scope.hello = "Not called anything yet!";
							// Will hold the unmodified data
							$scope.originalPersonsModel = [];
							$scope.persons = [ {
								"id" : 1,
								"timestamp" : "2014-02-13T16:54:18.498",
								"names" : "Harry 'Japanese version'",
								"lastName" : "Potteruu"
							}, {
								"id" : 2,
								"timestamp" : "2014-02-21T08:42:57.852",
								"names" : "Peter 'Spidey'",
								"lastName" : "Parker"
							}, {
								"id" : 3,
								"timestamp" : "2014-02-13T11:53:04.903",
								"names" : "John",
								"lastName" : "Rambo"
							}, {
								"id" : 4,
								"timestamp" : "2014-02-13T11:53:04.911",
								"names" : "Jason",
								"lastName" : "Voorhees"
							}, {
								"id" : 5,
								"timestamp" : "2014-02-13T11:54:36.457",
								"names" : "Wolverine",
								"lastName" : "Logan"
							}, {
								"id" : 6,
								"timestamp" : "2014-02-20T15:57:36.186",
								"names" : "Henry VIII",
								"lastName" : "Tudor"
							}, {
								"id" : 7,
								"timestamp" : "2014-02-17T15:50:57.893",
								"names" : "Freddy",
								"lastName" : "Krueger"
							} ];
							$scope.newPerson = '';

							// For testing
							$scope.getHello = function() {
								console.log("get hello");
								var updateHelloSuccess = function(data) {
									console.log("updateHelloSuccess: "
											+ JSON.stringify(data));
									if (data != undefined && data != '') {
										$scope.hello = data.content;
									} else {
										$scope.hello = "Response, but no Hi. :(";
									}
								};

								var helloFail = function() {
									var msg = "Failed to update persons";
									console.log(msg);
									ShareDataService.addMessage(msg, "ERROR");
								};

								RestServices.getHello(updateHelloSuccess,
										helloFail);
							};

							// Cancel all modification made to persons data
							$scope.cancelPersonChanges = function() {
								// Note: We could also just reload everything,
								// but we save a little bit of network traffic
								// this way
								$scope.persons = angular
										.copy($scope.originalPersonsModel);
								$scope.personsForm.$setPristine();
							};

							// Save all modification made to persons data
							$scope.savePersonChanges = function() {

								console.log("Update all persons: "
										+ JSON.stringify($scope.persons));
								var saveSuccess = function(data) {
									// Reload the data
									$scope.getAllPersons();
								};
								var saveFail = function() {
									var msg = "Failed to save new person";
									console.log(msg);
									ShareDataService.addMessage(msg, "ERROR");
								};
								// We could select only the modified data to be
								// sent, but the JPA will have only the actual
								// changes persisted (so we can be lazy here)
								RestServices.savePersonChanges(saveSuccess,
										saveFail, $scope.persons);
							};

							// Define the expected structure for creating a new
							// person
							// This structure should match with the entity
							// structure by 'Person.java'
							$scope.getNewPersonStructure = function() {
								console.log("Get structure for new person");

								var emptyStructureSuccess = function(data) {
									console
											.log("Structure for new Person should be: "
													+ JSON
															.stringify(data.payload.person[0]));
									$scope.newPerson = data.payload.person[0];
								};

								var emptyStructureFail = function() {
									var msg = "Failed to get empty person structure";
									console.log(msg);
									ShareDataService.addMessage(msg, "ERROR");
								};

								RestServices.getEmptyPerson(
										emptyStructureSuccess,
										emptyStructureFail);
							};

							// Add new person
							$scope.addPerson = function() {
								console.log("add person clicked: "
										+ JSON.stringify($scope.newPerson));
								var addPersonsSuccess = function() {
									$scope.getNewPersonStructure();
									$scope.getAllPersons();
								};
								var addPersonsFail = function() {
									var msg = "Failed to add new person";
									console.log(msg);
									ShareDataService.addMessage(msg, "ERROR");
								};

								RestServices.addPerson(addPersonsSuccess,
										addPersonsFail, $scope.newPerson);
							};

							// Get a list of persons with limiting phone number
							// criteria
							$scope.getLimitedPersons = function() {
								console.log("get limited persons");
								var getPersonsSuccess = function(data) {

									if (data != undefined && data != '') {
										$scope.persons = data.payload.person;
										$scope.originalPersonsModel = angular
												.copy($scope.persons);
									} else {
										var msg = "No persons found with the specified criteria";
										console.log(msg);
									}
								};

								var getPersonsFail = function() {
									var msg = "Failed to get persons list";
									console.log(msg);
									ShareDataService.addMessage(msg, "ERROR");
									$scope.persons = [];
								};

								// Load data from db conditionally
								if (!$scope.useDemoData) {
									RestServices.getAllPersons(
											getPersonsSuccess, getPersonsFail,
											$scope.criteriaString,
											$scope.criteriaType.key);
								} else {
									console.log("Using demo data");
								}
							};

							// Get a list of all persons stored in the phonebook
							$scope.getAllPersons = function() {
								console.log("get all persons");
								var getPersonsSuccess = function(data) {
									console.log("Success: "
											+ JSON.stringify(data));
									if (data != undefined && data != '') {
										$scope.persons = data.payload.person;
										$scope.originalPersonsModel = angular
												.copy($scope.persons);
										// Prevent from ugly error when
										// initially loading the page
										if (typeof $scope != 'undefined'
												&& typeof $scope.personsForm != 'undefined') {
											$scope.personsForm.$setPristine();
										}
									} else {
										$scope.persons = [];
									}
								};

								var getPersonsFail = function() {
									var msg = "Failed to get persons list";
									console.log(msg);
									ShareDataService.addMessage(msg, "ERROR");
									$scope.persons = [];
								};

								// Load data from db conditionally
								if (!$scope.useDemoData) {
									RestServices.getAllPersons(
											getPersonsSuccess, getPersonsFail);
								} else {
									console.log("Using demo data");
								}
							};

							// Remove a single person
							$scope.removePerson = function(id) {
								console.log("Remove person by id: " + id);
								var removeSuccess = function(data) {
									// Reload the data
									$scope.getAllPersons();
								};
								var removeFail = function() {
									var msg = "Failed to remove person";
									console.log(msg);
									ShareDataService.addMessage(msg, "ERROR");
								};
								RestServices.removePerson(removeSuccess,
										removeFail, id);
							};

							// Marks a person selected so that the info already
							// loaded is accessible on the details page
							$scope.selectPerson = function(person) {
								console.log("Select person: "
										+ JSON.stringify(person));
								ShareDataService.setPerson(person);
								var path = "/ajeer/details/" + person.id;
								console.log("Redirect to: " + path);
								$location.path(path);
							};

							$scope.getCriteriaTypes = function() {
								console.log("Get criteria types");
								var getTypesSuccess = function(data) {
									$scope.criteriaTypes = data.payload.keyValuePair;
									// Set the 1st value selected by default
									$scope.criteriaType = $scope.criteriaTypes[0];
								};
								var getTypesFail = function() {
									var msg = "Failed to get criteria types";
									console.log(msg);
									ShareDataService.addMessage(msg, "ERROR");
								};
								RestServices.getCriteriaTypes(getTypesSuccess,
										getTypesFail);
							};

							// Call these when the page is loaded
							$scope.getAllPersons();
							$scope.getCriteriaTypes();
							// This is just an example how the person
							// responsible for UI development can see the
							// structure expected with the person entity
							$scope.getNewPersonStructure();
						} ]);