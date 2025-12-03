def call {string giturl,string ssh key}
pipeline {
    agent { label "slave-1" }

    stages {

        stage('Checkout') {
            steps {
                checkout([
                    $class: 'GitSCM',
                    branches: [[name: '*/main']],
                    userRemoteConfigs: [[
                        url: giturl,
                        credentialsId: 'ssh key'
                    ]]
                ])
            }
        }

        stage('Build') {
            steps {
                sh 'mvn clean install'
            }
        }

        stage('Test') {
            steps {
                sh 'mvn test'
            }
        }

        stage('Deploy to Artifactory') {
            steps {
                configFileProvider(
                    [configFile(fileId: "322cfcc8-59c4-4cc5-90b6-cef808c15e9b", variable: 'MAVEN_SETTINGS')]
                ) {
                    sh 'mvn deploy -s $MAVEN_SETTINGS'
                }
            }
        }
    }
}
