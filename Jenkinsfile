pipeline {
  agent any

  tools {
    // Install the Maven version configured as "M3" and add it to the path.
    maven "M3"
  }

  stages {
    stage('stage 1->cloning git repo') {
      steps {
        // Get some code from a GitHub repository
        git([url: 'https://github.com/pv-prog/capstoneProject.git', branch: 'main'])
            }

      }
    stage('stage 2->compiling code') {
      steps {
        sh 'mvn -f backend/pom.xml compile'
            }

      }
      stage('stage 3->running testcases') {
      steps {
        // Get some code from a GitHub repository
        sh 'mvn -f backend/pom.xml test'
      }

      }
      stage('stage 4->packaging to jar') {
      steps {

        sh 'mvn -f backend/pom.xml clean package -DskipTests'
      }

      }
}
       post {
      //   // If Maven was able to run the tests, even if some of the test
      //   // failed, record the test results and archive the jar file.
        success {
      sh 'echo "Success"'
         }
       }

  }