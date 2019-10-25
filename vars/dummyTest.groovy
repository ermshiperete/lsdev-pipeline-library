#!/usr/bin/groovy

import sil.pipeline.GitHub
import sil.pipeline.Utils

def call(body) {
  // evaluate the body block, and collect configuration into the object
  def params = [:]
  body.resolveStrategy = Closure.DELEGATE_FIRST
  body.delegate = params
  body()

  def gitHub = new GitHub()
  def utils = new Utils()

  if (gitHub.isPRBuild() && utils.isManuallyTriggered() && gitHub.isPRFromTrustedUser()) {
    // ask for permission to build PR from this untrusted user
    pullRequest.comment('A team member has to approve this pull request on the CI server before it can be built...')
    input(message: "Build ${env.BRANCH_NAME} from ${env.CHANGE_AUTHOR} (${env.CHANGE_URL})?")
  }

  ansiColor('xterm') {
    timestamps {

      node('linux') {
        sh 'echo "Hello World!"'
      }
    }
  }
}
