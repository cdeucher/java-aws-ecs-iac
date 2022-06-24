package integration.fargate;

import integration.core.ClusterLocal;
import integration.core.ServicesLocal;
import integration.core.TasksLocal;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.ecs.EcsClient;

public class Fargate {

    public static void main(String[] args) {
        String clusterName = "cluster-react";
        String serviceName = "service-react";
        String family = "devops-react";
        String securityGroup = System.getenv("AWS_SECURITY_GROUP");
        String subNets = System.getenv("AWS_SUBNETS");
        String taskExecutionArn = System.getenv("AWS_IAM_ROLE_NAME_TASK");
        String taskRoleArn = System.getenv("AWS_IAM_ROLE_TASK_PERMISSIONS");
        String definitionRegisterArn;
        String clusterArn;

        Region region = Region.US_EAST_1;

        EcsClient ecsClient = EcsClient.builder()
                .region(region)
                .build();

        ClusterLocal cluster = new ClusterLocal(ecsClient);
        TasksLocal task = new TasksLocal(ecsClient);

        if( cluster.listAllClusters().isEmpty() ) {
            clusterArn = cluster.createCluster(clusterName);
        } else {
            clusterArn = cluster.getClusters().get(0);
        }
        if( task.listTaskDefinitions().isEmpty() ) {
            definitionRegisterArn = task.createTaskDefinition(family,taskExecutionArn,taskRoleArn);
        } else {
            definitionRegisterArn = task.getTaskDefinitions().get(0);
        }

        ServicesLocal service = new ServicesLocal(clusterArn,
            serviceName,
            securityGroup,
            subNets,
            definitionRegisterArn);

        if( service.getService(ecsClient).size() < 0 )
            service.CreateNewService(ecsClient);

        task.createTask(clusterName, definitionRegisterArn,securityGroup,subNets);

        ecsClient.close();
    }

}
