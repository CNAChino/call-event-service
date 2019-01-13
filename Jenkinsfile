def image

pipeline {
	agent any
	tools {
    	maven 'Maven350'
  	}

	stages {
	    stage('maven-clean-install') {
	    	steps {
		    	checkout scm
				sh 'cd calleventservice; mvn clean package'
				stash includes: '**/target/*.jar', name: 'app'
	    	}
	    }

	    stage('docker-build') {
	    	steps {
	    		unstash 'app'
	    		script {
	        		image = docker.build("cnaureusdev/call-event-service", "./calleventservice")
	        	}
	        }
	    }

	    stage('docker-test') {
	    	steps {
	    		script {
		       	image.inside {
		            	sh 'java -version'
		        	}
		        }
	        }
	    }

	    stage('docker-push') {
	    	steps {
	    		script {
	    		    docker.withRegistry('https://registry.hub.docker.com', 'docker-hub-credentials') {
			            image.push("${env.BUILD_NUMBER}")
			            image.push("latest")
			        }
	    		}
	        }
	    }
	}


}