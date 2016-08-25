/*
 * Copyright (c) 2014, 2016 IBM Corporation and other Contributors.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM - Initial Contribution
 */
package com.ibm.mqlight.sample;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.ibm.mqlight.api.ClientException;
import com.ibm.mqlight.api.ClientOptions;
import com.ibm.mqlight.api.CompletionListener;
import com.ibm.mqlight.api.NonBlockingClient;
import com.ibm.mqlight.api.NonBlockingClientAdapter;
import com.ibm.mqlight.api.QOS;
import com.ibm.mqlight.api.SendOptions;
import com.ibm.mqlight.api.SendOptions.SendOptionsBuilder;
import com.ibm.mqlight.api.SubscribeOptions;
import com.ibm.mqlight.api.SubscribeOptions.SubscribeOptionsBuilder;
import com.ibm.mqlight.api.DestinationAdapter;
import com.ibm.mqlight.api.StringDelivery;
import com.ibm.mqlight.api.JsonDelivery;
import com.ibm.mqlight.api.BytesDelivery;
import com.ibm.mqlight.api.MalformedDelivery;
import com.ibm.mqlight.api.Delivery;

import com.google.gson.*;

/**
 * The java backend worker for the MQ Light sample application.
 */
public class BackendWorker {

  /** The topic we publish on to send data to the back-end */
  private static final String PUBLISH_TOPIC = "mqlight/sample/wordsuppercase";

  /** The topic we subscribe on to receive notifications from the back-end */
  private static final String SUBSCRIBE_TOPIC = "mqlight/sample/words";

  private static final String SHARE_ID = "fishalive-workers";

  /** Simple logging */
  private final static Logger logger = Logger.getLogger(BackendWorker.class.getName());

  private NonBlockingClient mqlightClient;

  public static void main(String[] args) throws Exception {
    BackendWorker bw = new BackendWorker();
  }

  /**
   * Default Constructor
   */
  public BackendWorker() {
    logger.log(Level.INFO, "Initialising...");
      
    try {
      logger.log(Level.INFO,"Creating an MQ Light client...");

      String service = null;
      if (System.getenv("VCAP_SERVICES") == null) {
        service = "amqp://localhost:5672";
      }
      
      mqlightClient = NonBlockingClient.create(service, new NonBlockingClientAdapter<Void>() {

        @Override
        public void onStarted(NonBlockingClient client, Void context) {
          System.out.printf("Connected to %s using client-id %s\n", client.getService(), client.getId());

          SubscribeOptions opts = SubscribeOptions.builder().setShare(SHARE_ID).build();

          client.subscribe(SUBSCRIBE_TOPIC, opts, new DestinationAdapter<Void>() {
            public void onMessage(NonBlockingClient client, Void context, Delivery delivery) {
              logger.log(Level.INFO,"Received message of type: " + delivery.getType());
              switch (delivery.getType()) {
                case JSON:
                  JsonDelivery jd = (JsonDelivery)delivery;
                  logger.log(Level.INFO,"Data: " + jd.getData());
                  processMessage(jd.getData().toString());
                  break;
                case STRING:
                  StringDelivery sd = (StringDelivery)delivery;
                  logger.log(Level.INFO,"Data: " + sd.getData());
                  processMessage(sd.getData());
                  break;
                case BYTES:
                  BytesDelivery bd = (BytesDelivery)delivery;
                  logger.log(Level.INFO,"Data: " + bd.getData());
                  processMessage(bd.getData().toString());
                  break;
                case MALFORMED:
                  MalformedDelivery md = (MalformedDelivery)delivery;
                  logger.log(Level.WARNING,"Malformed message: " + md.getDescription());
                  processMessage("MALFORMED");
                  break;
              }
            }
          }, new CompletionListener<Void>() {
            @Override
            public void onSuccess(NonBlockingClient c, Void ctx) {
              logger.log(Level.INFO, "Subscribed!");
            }
            @Override
            public void onError(NonBlockingClient c, Void ctx, Exception exception) {
              logger.log(Level.SEVERE, "Exception while subscribing. ", exception);
            }
          }, null);
        }

        @Override
        public void onRetrying(NonBlockingClient client, Void context, ClientException throwable) {
            System.out.println("*** error ***");
            if (throwable != null) System.err.println(throwable.getMessage());
        }

        @Override
        public void onStopped(NonBlockingClient client, Void context, ClientException throwable) {
            if (throwable != null) {
                System.err.println("*** error ***");
                System.err.println(throwable.getMessage());
            }
            logger.log(Level.INFO,"MQ Light client stopped.");
        }
      }, null);
      logger.log(Level.INFO,"MQ Light client created. Current state: " + mqlightClient.getState());
    } catch (Exception e) {
      logger.log(Level.SEVERE, "Failed to initialise", e);
      throw new RuntimeException(e);
    }
    logger.log(Level.INFO, "Completed initialisation.");
  }

  public void processMessage(String message) {

    JsonParser parser = new JsonParser();
    JsonObject messageJSON = (JsonObject)parser.parse(message);

    String word = messageJSON.get("word").getAsString();

    JsonObject reply = new JsonObject();
    reply.addProperty("word", word.toUpperCase());
    reply.addProperty("backend", "JavaAPI: " + toString());

    SendOptions opts = SendOptions.builder().setQos(QOS.AT_LEAST_ONCE).build();
    mqlightClient.send(PUBLISH_TOPIC, reply.toString(), null, opts, new CompletionListener<Void>() {
      public void onSuccess(NonBlockingClient client, Void context) {
        logger.log(Level.INFO, "Sent reply!");
      }
      public void onError(NonBlockingClient client, Void context, Exception exception) {
        logger.log(Level.INFO,"Error!." + exception.toString());
      }
    }, null);
  }
    
}
