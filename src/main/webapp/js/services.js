'use strict';
var services = angular.module('services');

services.factory('TimeTableService', function () {

    var currentMatchDay = 1;

    return {
        getCurrentMatchDay: function () {
            return currentMatchDay;
        },
        setCurrentMatchDay: function (matchDay) {
            currentMatchDay = matchDay;
        }
    }

});