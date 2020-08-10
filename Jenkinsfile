pipeline {
    agent { docker { image 'maven:3.6.3-openjdk-15' } }
    stages {
        stage('build') {
            steps {
                sh 'mvn --version'
            }
        }
    }
}