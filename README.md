# Call Event Service 1.0

A sample application using the technologies:
* Spring Boot - for running the application and take advantage of framework features.
* gRPC - RPC protocol framework for sending event data to the backend 
* Docker - run the application in a container
* NGINX - load balancer
* Neo4J - Graph DB

This sample application collects and process voice call events.  The following information is included in the call event:

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

## Features
* All components run in docker
* docker network (Bridge) 
* clustering / load balancing
* list cellphone brands used in a telecom provider in a specific country (uses cypher to query neo4j)



 
## Architecture

![TD Image](appdesign.png)

Notes on NGINX Load Balancing:
1. gRPC messages are transported over HTTP/2 either over TLS or not.
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
Install **Java 1.8 SDK**, **Git**, **Maven 3**, **Docker**, **call-event-proto**.\
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

Prerequisite:  Start Neo4j Graph DB (Community or Enterprise edition).  Then create a neo4j user (disable Force Password Change).  Set the Uri, Username and Password in **application.properties**.

To run the docker image in foreground (add -d to run in background), execute:\
&nbsp;\
`docker run --name {name} -it -v {host-local-log-dir}:/app/logs {docker.image.name.prefix}/{project.artifactId}:{tag}`\
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
Prerequisite:
1. Create **/apps/nginxlogs**, **/apps/logs1** and **/apps/logs2**.  Make sure these directories are writable by the application
2. Create the bridge network with name **ces-brnet** and subnet address **172.19.0.0/16**\
`$ docker network create --driver bridge --subnet 172.19.0.0/16 ces-brnet`
&nbsp;
3. Start Neo4j Cluster.  This project has src/docker-compose.yml which defines the neo4j db cluster.  Execute:  
`$ docker-compose up` or `$ docker-compose up -d`\
wait for a few minutes then connect to neo4j browser via http://{localhost or your machine IP}:7474
&nbsp;

Procedure:
1.  Run N (2 for this guide) application containers assigning each with an ip address.\
`$ docker run --name ces1 --network=ces-brnet --ip=172.19.3.6 -itd -v /apps/logs1:/app/logs aureus-prototype/call-event-service:1.0`\
`$ docker run --name ces2 --network=ces-brnet --ip=172.19.3.7 -itd -v /apps/logs2:/app/logs aureus-prototype/call-event-service:1.0`\
&nbsp;\
Note:  Inside the docker container,  the application listens in port **9090** and the working directory is **/app**\
&nbsp;
2.  Build NGINX-LB container\
`$ cd {path/to/call-event-service}/src/main/nginx`\
`$ docker build -t aureus-prototype/nginx-lb .`
&nbsp;\
&nbsp;
3.  Run NGINX-LB container and assign it with an ip address\
`$ docker run --network=ces-brnet --ip=172.19.3.5 -v /apps/nginxlogs:/var/log/nginx -itd -p 8080:80 aureus-prototype/nginx-lb`\
&nbsp;\
access.log and error.log should be created in your /apps/nginxlogs directory.
&nbsp;
4.  To test, run `CallEventClient.java` which is located in src/test/java.  Check **/apps/nginxlogs**, **/apps/logs1** and **/apps/logs2**.  Messages should be logged both in **apps/logs1** and **apps/logs2**.  
 
