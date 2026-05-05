pipeline {
	agent any
	
	tools {
        jdk 'JDK_17' // Must match the name set in Global Tool Configuration
    }
	
	environment {
        // Reference the global variable or define it here
         ANDROID_HOME  = "C:\\Users\\clifford\\AppData\\Local\\Android\\Sdk"
    }
 

	stages {
        stage('Checkout') {
            steps {
                checkout scm
				echo 'Checking out source code...'
            }
        }
		
		
		
		stage("Build Package") {
			steps {
				bat 'gradlew.bat clean assembleDebug'		
			}
		}
		

		stage('Archive Artifacts') {
            steps {
                // Save the generated APK as a Jenkins artifact
                archiveArtifacts artifacts: 'app/build/outputs/apk/**/*.apk', fingerprint: true
            }
        }
		
		
	}
}
