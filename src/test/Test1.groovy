#!/usr/bin/env groovy
package test

@Library('util').test.Test2;
import test.Test2;

node('toolz-slave') {
    timestamps {
        wrap([$class: 'AnsiColorBuildWrapper']) {
            ws("$env.WORKSPACE/$env.BUILD_NUMBER/Dev") {
                wrap([$class: 'BuildUser']) {
                    def user = env.BUILD_USER_ID
                    def user_email = env.BUILD_USER_EMAIL
                    echo "Build started by $user ($user_email)"

                    stage('Echo'){

                        echo " "+Test2.someFunction();
                    }



                    try {


                    } catch (hudson.plugins.git.GitException err) {
                        echo 'Err: Git error deleting workspace'
                        echo err.toString()
                        currentBuild.result = 'FAILURE'
                        throw err
                    } catch (err) {
                        echo 'Err: Build failed'
                        echo err.toString()
                        currentBuild.result = 'FAILURE'
                        throw err
                    } finally {
                        echo "Mail to $user_email"
                        step([$class                  : 'Mailer',
                              notifyEveryUnstableBuild: true,
                              recipients              : ["$user_email", "mark@matific.com"].join("\n"),
                              sendToIndividuals       : true])
                        emailext (
                                to: "$user_email",
                                subject: "Infra Published - '[${env.BUILD_NUMBER}] (Web)'",
                                body: """Infra ${env.INFRA_VERSION} - uploaded to internal data bucket under PackedInfraVersions""",
                                recipientProviders: []
                        )
                    }
                }
            }

        }
    }
}
