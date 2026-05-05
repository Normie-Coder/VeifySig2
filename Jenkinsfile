pipeline {
	agent any
	
	environment {
        // Reference the global variable or define it here
        ANDROID_HOME = "${env.ANDROID_HOME}"
    }
 

	stages {
        stage('Checkout') {
            steps {
                checkout scm
				echo 'Checking out source code...'
            }
        }
		
		
		
		stage("Prepare Environment") {
			steps {
				bat  'chmod +x gradlew'		
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
