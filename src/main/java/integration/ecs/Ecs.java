package integration.ecs;

import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.ecs.EcsClient;
import software.amazon.awssdk.services.ecs.model.RunTaskResponse;
import software.amazon.awssdk.services.ecs.model.Task;

import java.util.List;

public class Ecs {

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

        ServicesLocal.CreateNewService(ecsClient,
            clusterArn,
            serviceName,
            securityGroup,
            subNets,
            definitionRegisterArn
        );

        ecsClient.close();
    }

}
