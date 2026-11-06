// BuildImage: build, scan, push and delete the Docker image
// Usage: BuildImage(imageName, imageTag)
def call(String imageName, String imageTag, String credentialsId = "dockerhub") {
    echo "BuildImage: ${imageName}:${imageTag}"

    sh "docker build -t ${imageName}:${imageTag} ."

    sh "trivy image --cache-dir /opt/trivy-cache --no-progress --severity HIGH,CRITICAL --exit-code 0 ${imageName}"
    
    withCredentials([usernamePassword(credentialsId: credentialsId,
                                      usernameVariable: "DH_USER",
                                      passwordVariable: "DH_PASS")]) {
        sh "echo \"\$DH_PASS\" | docker login -u \"\$DH_USER\" --password-stdin"
        sh "docker push ${imageName}:${imageTag}"
    }

    sh "docker rmi ${imageName}:${imageTag}"
}
