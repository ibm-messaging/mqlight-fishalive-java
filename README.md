# IBM MQ Light Java Sample Application

This project contains Java samples demonstrating how to use the MQ Light
Service for Bluemix to write cloud apps which can perform worker offload.

Check out the blog post at [IBM Messaging][ibm-messaging] for more info on this
sample.

## Deploying to Bluemix

The sample can be used with the 'Message Hub' service. Pre-built binaries are included in this
source repository for convenience and can be deployed immediately.

1.  Create an instance of the service using either the Bluemix console or the
    Bluemix cf command line tool.

2.  In the Message Hub service Dashboard, create a topic called "MQLight" with a single partition.

3.  Edit the `manifest.yml` file in the root directory of the sample to reflect
    the name of the service created above.

 ```yml
   services:
   - <TheNameOfYourService>
 ...
   services:
   - <TheNameOfYourService>
 ```

4.  From the root directory of the sample use the Bluemix cf command line
    tool to push the sample to Bluemix, as below:
    ```sh
    $ cf push
    ```

For further information about Bluemix command line tooling, see
[IBM Bluemix Command Line Tooling](https://www.ng.bluemix.net/docs/starters/install_cli.html)

## Building (optional)

The following needs to be available:
* Java SDK v7.0 or later [with JAVA_HOME pointing to its install directory]
* Maven

From the samples root directory:
```sh
cd frontend
mvn package
cd ../backend
mvn package
cd ..
```

[ibm-messaging]: https://developer.ibm.com/messaging/2015/05/22/getting-started-with-java-apps-using-the-mq-light-service-for-bluemix/

