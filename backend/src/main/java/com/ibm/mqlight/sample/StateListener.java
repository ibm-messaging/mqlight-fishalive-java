// ******************************************************************
//
// Program name: mqlight_sample_frontend_web
//
// Description:
//
// A http servlet that demonstrates use of the IBM Bluemix MQ Light Service.
//
// <copyright
// notice="lm-source-program"
// pids=""
// years="2014"
// crc="659007836" >
// Licensed Materials - Property of IBM
//
//
// (C) Copyright IBM Corp. 2014 All Rights Reserved.
//
// US Government Users Restricted Rights - Use, duplication or
// disclosure restricted by GSA ADP Schedule Contract with
// IBM Corp.
// </copyright>
// *******************************************************************

package com.ibm.mqlight.sample;

import javax.annotation.Resource;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

import com.ibm.mqlight.sample.BackendWorker;

@WebListener
public class StateListener implements ServletContextListener {

  public StateListener() {
    System.out.println("StateListener, standing by");
  }

  public void contextDestroyed(ServletContextEvent sce) {

  }

  public void contextInitialized(ServletContextEvent sce) {
    BackendWorker bw = new BackendWorker();
  }

} 