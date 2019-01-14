# Call Event Service

A data collector for voice call events.  

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

This service is a prototype to integrate the following technologies:
1. Spring Boot - for running the application and application configuration.
2. gRPC - RPC protocol framework 
3. Docker - run the application in a container and use docker networking
4. NGINX - load balancer 
 
## Technical Design

![TD Image](appdesign.png)

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
`$ docker run -itd -p {host-port}:9090 -v {host-local-log-dir}:/app/logs {docker.image.name.prefix}/{project.artifactId}:{tag}`\
&nbsp;\
where,
* `{docker.image.name.prefix}` and `{project.artifactId}` values are from `pom.xml`.
* `{tag}` is `project.version` from pom.xml.
* `{host-port}` is the port to listen to in your host operating system.
* `{host-local-log-dir}` is the  path to a file directory in your host operating system for logfiles.
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
`$ docker run --network=ces-brnet --ip=172.19.3.6 -itd -p {host-port}:9090 -v {host-local-log-dir}:/app/logs {docker.image.name.prefix}/{project.artifactId}:{tag}`\
`$ docker run --network=ces-brnet --ip=172.19.3.7 -itd -p {host-port}:9090 -v {host-local-log-dir}:/app/logs {docker.image.name.prefix}/{project.artifactId}:{tag}`\
&nbsp;\
where,
* `{docker.image.name.prefix}` and `{project.artifactId}` values are from `pom.xml`.
* `{tag}` is `project.version` from pom.xml.
* `{host-port}` is the port to listen to in your host operating system.
* `{host-local-log-dir}` is the  path to a file directory in your host operating system for logfiles.
&nbsp;\
Note:  Inside the docker container,  the application listens in port **9090** and the working directory is **/app**\
&nbsp;
3.  Attach the containers on the bridge network\
`$ docker network connect ces-brnet {container-id-1}`\
`$ docker network connect ces-brnet {container-id-2}`\
&nbsp;\
run `docker ps` to get container ids.\
&nbsp;
4.  Build NGIN-LB container\
`$ cd {path/to/call-event-service}/main/nginx`\
`$ docker build -t aureus-prototype/nginx-lb .`
&nbsp;\
&nbsp;
5.  Run NGINX-LB container and assign it with an ip address\
`$ docker run --network=ces-brnet --ip=172.19.3.5 -it -p 8080:80 aureus-prototype/nginx-lb`\
&nbsp;
6.  Test 
