pipeline {
	agent any
 

	stages {
        stage('Checkout') {
            steps {
                checkout scm
				echo 'Checking out source code...'
            }
        }
		
		
		
		stage("Prepare Environment") {
			steps {
				sh  'chmod +x gradlew'		
			}
		}
		
		stage("Build Package") {
			steps {
				sh './gradlew clean assembleDebug'		
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
