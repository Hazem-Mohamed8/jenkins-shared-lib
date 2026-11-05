// BuildApp: unit test and build the Node.js application
def call() {
    echo "BuildApp: installing dependencies, running unit tests"
    sh "node -v && npm -v"
    sh "npm install"
    sh "npm test"
}
