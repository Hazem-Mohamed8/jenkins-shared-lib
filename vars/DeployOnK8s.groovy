// DeployOnK8s: edit the deployment file and push it to GitHub (Argo CD does the deploy)
// Usage: DeployOnK8s(imageName, imageTag)
def call(String imageName, String imageTag,
         String deploymentFile = "k8s/deployment.yaml",
         String repoUrl = "github.com/Hazem-Mohamed8/ivolve-app.git",
         String branch = "main",
         String gitCredsId = "github-creds") {
    echo "DeployOnK8s: ${imageName}:${imageTag} -> push to ${repoUrl} (${branch})"

    // only the application image is replaced, the mysql init container image is untouched
    sh "sed -i \"s|image: ${imageName}:.*|image: ${imageName}:${imageTag}|\" ${deploymentFile}"
    sh "grep -n \"image:\" ${deploymentFile}"

    withCredentials([usernamePassword(credentialsId: gitCredsId,
                                      usernameVariable: "GIT_USER",
                                      passwordVariable: "GIT_TOKEN")]) {
        sh """
            git config user.email "jenkins@ivolve.io"
            git config user.name "jenkins"
            git add ${deploymentFile}
            git commit -m "Update image to ${imageName}:${imageTag} [skip ci]" || { echo "No changes to commit"; exit 0; }
            git push https://\${GIT_USER}:\${GIT_TOKEN}@${repoUrl} HEAD:${branch}
        """
    }
}
