// DeployOnK8s: edit the deployment file and deploy to the cluster
// Usage: DeployOnK8s(imageName, imageTag)
def call(String imageName, String imageTag,
         String namespace = "ivolve",
         String deploymentName = "ivolveapp",
         String deploymentFile = "k8s/deployment.yaml",
         String kubeconfigId = "k8s-kubeconfig") {
    echo "DeployOnK8s: ${imageName}:${imageTag} -> namespace ${namespace}"

    // only the application image is replaced, the mysql init container image is untouched
    sh "sed -i \"s|image: ${imageName}:.*|image: ${imageName}:${imageTag}|\" ${deploymentFile}"
    sh "grep -n \"image:\" ${deploymentFile}"

    withCredentials([file(credentialsId: kubeconfigId, variable: "KUBECONFIG")]) {
        sh "kubectl apply -f ${deploymentFile} -n ${namespace}"
        sh "kubectl rollout status deployment/${deploymentName} -n ${namespace} --timeout=180s"
    }
}
