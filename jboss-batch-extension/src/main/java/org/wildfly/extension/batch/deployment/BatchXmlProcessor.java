/*
 * JBoss, Home of Professional Open Source.
 * Copyright 2014, Red Hat, Inc., and individual contributors
 * as indicated by the @author tags. See the copyright.txt file in the
 * distribution for a full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.wildfly.extension.batch.deployment;

import java.io.IOException;
import java.util.List;
import org.jboss.as.ee.structure.DeploymentType;
import org.jboss.as.ee.structure.DeploymentTypeMarker;
import org.jboss.as.server.deployment.Attachments;
import org.jboss.as.server.deployment.DeploymentPhaseContext;
import org.jboss.as.server.deployment.DeploymentUnit;
import org.jboss.as.server.deployment.DeploymentUnitProcessingException;
import org.jboss.as.server.deployment.DeploymentUnitProcessor;
import org.jboss.as.server.deployment.module.ResourceRoot;
import org.jboss.vfs.VirtualFile;
import org.jboss.vfs.VirtualFileFilter;
import org.wildfly.extension.batch._private.BatchLogger;

/**
 *
 * @author Andrej_Petras
 */
public class BatchXmlProcessor implements DeploymentUnitProcessor {

    public static final String WEB_INF_BATCH_XML = "WEB-INF/classes/META-INF/batch-jobs";
    public static final String META_INF_BATCH_XML = "META-INF/batch-jobs";

    @Override
    public void deploy(DeploymentPhaseContext phaseContext) throws DeploymentUnitProcessingException {
        final DeploymentUnit deploymentUnit = phaseContext.getDeploymentUnit();
        ResourceRoot deploymentRoot = deploymentUnit.getAttachment(Attachments.DEPLOYMENT_ROOT);
        if (deploymentRoot == null) {
            return;
        }

        // Get the root file
        final VirtualFile root = deploymentRoot.getRoot();
        VirtualFile jobsDir = null;
        // Only files in the META-INF/batch-jobs directory
        if (DeploymentTypeMarker.isType(DeploymentType.WAR, deploymentUnit)) {
            jobsDir = root.getChild(BatchXmlProcessor.WEB_INF_BATCH_XML);
        } else if (!DeploymentTypeMarker.isType(DeploymentType.EAR, deploymentUnit)) {
            jobsDir = root.getChild(BatchXmlProcessor.META_INF_BATCH_XML);
        }

        if (jobsDir != null && jobsDir.exists() && !jobsDir.getChildren().isEmpty()) {
                        
            final List<VirtualFile> jobFiles;
            try {
                // Create the job XML resolver service with the files allowed to be used
                jobFiles = jobsDir.getChildren(JobXmlFilter.INSTANCE);
            } catch (IOException e) {
                throw BatchLogger.LOGGER.errorProcessingBatchJobsDir(e);
            }
            
            ExplicitBatchArchiveMetadata batchArchiveMetadata = new ExplicitBatchArchiveMetadata(jobsDir, jobFiles);
            ExplicitBatchArchiveMetadataContainer deploymentMetadata = new ExplicitBatchArchiveMetadataContainer(batchArchiveMetadata);
            deploymentUnit.putAttachment(ExplicitBatchArchiveMetadataContainer.ATTACHMENT_KEY, deploymentMetadata);            
            // mark the deployment as requiring Batch integration
            BatchDeploymentMarker.mark(deploymentUnit);            
        }
    }

    @Override
    public void undeploy(DeploymentUnit du) {

    }


    private static class JobXmlFilter implements VirtualFileFilter {

        static final JobXmlFilter INSTANCE = new JobXmlFilter();

        @Override
        public boolean accepts(final VirtualFile file) {
            return file.isFile() && file.getName().endsWith(".xml");
        }
    }    
}
