/*
 * Copyright (c) 2016 highstreet technologies GmbH and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */

define(['angularAMD', 
        'app/routingConfig', 
        'app/core/core.services', 
        'common/config/env.module',
        'app/mwtnCommons/mwtnCommons.module'], function(ng) {
  var onapMsoApp = angular.module('app.onapMso', ['ui.grid', 'ui.bootstrap', 'app.core',
      'ui.router.state', 'config', 'ui.grid.exporter',
      'ui.grid.moveColumns', 'ui.grid.pinning', 'ui.grid.selection',
      'ui.grid.resizeColumns', 'ui.grid.infiniteScroll','ui.grid.pagination' ]);

  onapMsoApp.config(function($stateProvider, $compileProvider, $controllerProvider, $provide, NavHelperProvider, $translateProvider) {
    onapMsoApp.register = {
      controller : $controllerProvider.register,
      directive : $compileProvider.directive,
      factory : $provide.factory,
      service : $provide.service
    };


    NavHelperProvider.addControllerUrl('app/onapMso/onapMso.controller');
    NavHelperProvider.addToMenu('onapMso', {
     "link" : "#/onapMso/",
     "active" : "main.onapMso",
     "title" : "ONAP MSO",
     "icon" : "fa fa-music",  // Add navigation icon css class here
     "page" : {
        "title" : "ONAP MSO",
        "description" : "Open Network Automation Platform (ONAP) - Master Service Orchestrator (MSO)"
    }
    });

    var access = routingConfig.accessLevels;

    $stateProvider.state('main.onapMso', {
        url: 'onapMso/:nodeId',
        access: access.admin,
        views : {
            'content' : {
                templateUrl: 'src/app/onapMso/onapMso.tpl.html',
                controller: 'onapMsoCtrl'
            }
        }
    });

  });

  return onapMsoApp;
});