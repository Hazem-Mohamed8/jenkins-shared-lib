def call(Map config) {
    def fullImage = "${config.imageName}:${config.imageTag}"

    echo "BuildImage: ${fullImage}"
    sh "docker build -t ${fullImage} ."

    sh "trivy image --cache-dir /opt/trivy-cache --no-progress --severity HIGH,CRITICAL --exit-code 0 ${fullImage}"

    withCredentials([usernamePassword(credentialsId: 'dockerhub-creds', usernameVariable: 'DOCKER_USER', passwordVariable: 'DOCKER_PASS')]) {
        sh 'echo $DOCKER_PASS | docker login -u $DOCKER_USER --password-stdin'
        sh "docker push ${fullImage}"
    }

    sh "docker rmi ${fullImage}"
}
