# Call Event Service 1.0

A data collector and processing for voice call events.  

* Mobile Network Code (MNC) 
* Mobile Country Code (MCC) 
* Network Name (e.g. T-Mobile / AT&T)
* Network Type (e.g. 2G/3G), or LTE for VoLTE
* Signal DBM
* Signal ASU
* Device Brand
* Device Model
* OS Name
* OS Version
* Latitude
* Longtitude

For version 1.0, the events received are discarded.  Next version is to publish the events to **Apache Kafka**.

Call Event Service 1.0 is a prototype to integrate the following technologies:
1. Spring Boot - for running the application and application configuration.
2. gRPC - RPC protocol framework 
3. Docker - run the application in a container and use docker networking
4. NGINX - load balancer 
 
## Technical Design

![TD Image](appdesign.png)

Notes on NGINX Load Balancing:
1. gRPC messages are transported over HTTP/2 either over on encrypted TLS or not.
2. NGINX receives gRPC traffic using HTTP and proxies it using **grpc_pass** directive.

Following is an extract from:

src/main/nginx/nginx.conf
~~~~
http {
  ...
  include /etc/nginx/conf.d/*.conf;
}
~~~~
&nbsp;\
&nbsp;\
src/main/nginx/conf.d/default.conf  
~~~~
upstream grpcservers {
  server 172.19.3.6:9090;
  server 172.19.3.7:9090;
}

server {
  listen 80 http2;

  location / {
    grpc_pass grpc://grpcservers;
  }
}
~~~~
## Building the Application
Prerequisite:\
Install **Java 1.8 SDK**, **Git**, **Maven**, **Docker**, **call-event-proto**.\
Note:  see https://github.com/CNAChino/call-event-proto to install call-event-proto.\
&nbsp;\
Procedure:
1.  Get the source code\
`$ git clone https://github.com/CNAChino/call-event-service.git`

2.  Compile and package jar file\
`$ mvn package`\
OR\
`$ mvn clean package`\
Note:  This will create the application jar file and the docker image.\
&nbsp;\
To check if the docker image was created, run the following command:\
`$ docker image ls`\
&nbsp;\
To remove the image from your docker local repository, run the following command:\
`$ docker image rm {docker.image.name.prefix}/{project.artifactId}:{tag}`\
&nbsp;\
just replace values of `docker.image.name.prefix` and `project.artifactId` from `pom.xml`.\
For `tag` use `project.version` from pom.xml.\

## Running the Application

To run the docker image, execute:\
&nbsp;\
`docker run --name {name} --network={network-name} --ip={ip-addr} -itd -v {host-local-log-dir}:/app/logs {docker.image.name.prefix}/{project.artifactId}:{tag}`\
&nbsp;\
where,
* `{name}` is the name of container.
* `{network-name}` is the name of docker network.
* `{ip-addr}` is ip address that will be assigned to the container.
* `{host-local-log-dir}` is the  path to a file directory in your host operating system for logfiles.
* `{docker.image.name.prefix}` and `{project.artifactId}` values are from `pom.xml`.
* `{tag}` is `project.version` from pom.xml.
&nbsp;\
Note:  Inside the docker container,  the application listens in port **9090** and the working directory is **/app**


## Clustering the application on a docker bridge network

Note:  Docker's bridge network applies to containers running on the same docker daemon host.\
For clustering containers on multiple docker host, use an overlay network.\
&nbsp;\
Prerequisite:\
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Create **/apps/logs1** and **/app/logs2**\
Procedure:
1.  Create the bridge network with name **ces-brnet** and subnet address **172.19.0.0/16**\
`$ docker network create --driver bridge --subnet 172.19.0.0/16 ces-brnet`\
&nbsp;
2.  Run N (2 for this guide) application containers assigning each with an ip address.\
`$ docker run --name ces1 --network=ces-brnet --ip=172.19.3.6 -itd -v {host-local-log-dir}:/app/logs {docker.image.name.prefix}/{project.artifactId}:{tag}`\
`$ docker run --name ces2 --network=ces-brnet --ip=172.19.3.7 -itd -v {host-local-log-dir}:/app/logs {docker.image.name.prefix}/{project.artifactId}:{tag}`\
&nbsp;\
where,
* `{host-local-log-dir}` is the  path to a file directory in your host operating system for logfiles.
* `{docker.image.name.prefix}` and `{project.artifactId}` values are from `pom.xml`.
* `{tag}` is `project.version` from pom.xml.
&nbsp;\
Note:  Inside the docker container,  the application listens in port **9090** and the working directory is **/app**\
&nbsp;
3.  Build NGIN-LB container\
`$ cd {path/to/call-event-service}/main/nginx`\
`$ docker build -t aureus-prototype/nginx-lb .`
&nbsp;\
&nbsp;
4.  Run NGINX-LB container and assign it with an ip address\
`$ docker run --network=ces-brnet --ip=172.19.3.5 -it -p 8080:80 aureus-prototype/nginx-lb`\
&nbsp;
5.  Run `CallEventClient.java` to test located in src/test/java
 
