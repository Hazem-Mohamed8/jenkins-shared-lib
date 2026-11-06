// vars/BuildImage.groovy
def call(String imageName, String imageTag) {
    def fullImage = "${imageName}:${imageTag}"

    echo "BuildImage: ${fullImage}"
    sh "docker build -t ${fullImage} ."
    sh "trivy image --cache-dir /opt/trivy-cache --no-progress --severity HIGH,CRITICAL --exit-code 0 ${fullImage}"

    withCredentials([usernamePassword(credentialsId: 'dockerhub', usernameVariable: 'DOCKER_USER', passwordVariable: 'DOCKER_PASS')]) {
        sh 'echo $DOCKER_PASS | docker login -u $DOCKER_USER --password-stdin'
        sh "docker push ${fullImage}"
    }
    sh "docker rmi ${fullImage}"
}
