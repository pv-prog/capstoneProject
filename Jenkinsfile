pipeline {
  agent any

  tools {
    // Install the Maven version configured as "M3" and add it to the path.
    maven "M2_HOME"
  }

  stages {
    stage('stage 1->cloning git repo') {
      steps {
        // Get some code from a GitHub repository
        git 'https://github.com/pv-prog/capstoneProject.git'
            }

      }
    stage('stage 2->compiling code') {
      steps {
        sh 'mvn compile'
            }

      }
      stage('stage 3->running testcases') {
      steps {
        // Get some code from a GitHub repository
        sh 'mvn test'
      }

      }
      stage('stage 4->packaging to jar') {
      steps {

        sh 'mvn package'
      }

      }
}
       post {
      //   // If Maven was able to run the tests, even if some of the test
      //   // failed, record the test results and archive the jar file.
        success {
      emailext body: 'Congrats!!the build is success...', subject: 'Hi', to: 'lakshmanpvl@gmail.com'

         }
       }

  }