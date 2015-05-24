Jboss batch for the EAP6.4 ${project.version} - CLI deployment archive
==================
 
- Fork Wilfdly ${version.wildfly.extension}
- The deployment script do not changed the javaee.api module

=== 1. Deployment

Start the server.
Connect to the server through  CLI.

Execute these commands:
deploy jboss-batch-dist-${project.version}.cli
deploy jboss-batch-dist-${project.version}.cli --script=deploy-extension.cli
deploy jboss-batch-dist-${project.version}.cli --script=deploy-batch.cli
shutdown --restart=true

=== 2. Undeploy the batch extension

Start the server.
Connect to the server through  CLI.

Execute these commands:
deploy jboss-batch-dist-${project.version}.cli --script=undeploy-batch.cli
deploy jboss-batch-dist-${project.version}.cli --script=undeploy-extension.cli
shutdown --restart=true
deploy jboss-batch-dist-${project.version}.cli --script=undeploy.scr





