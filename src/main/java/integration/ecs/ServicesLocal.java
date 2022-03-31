package integration.ecs;

import software.amazon.awssdk.services.ecs.EcsClient;
import software.amazon.awssdk.services.ecs.model.*;

public class ServicesLocal {

    public static String CreateNewService(EcsClient ecsClient,
                                          String clusterName,
                                          String serviceName,
                                          String securityGroups,
                                          String subnets,
                                          String taskDefinition) {

        try {
            AwsVpcConfiguration vpcConfiguration = AwsVpcConfiguration.builder()
                .securityGroups(securityGroups)
                .subnets(subnets)
                .assignPublicIp(AssignPublicIp.ENABLED)
                .build();

            NetworkConfiguration configuration = NetworkConfiguration.builder()
                .awsvpcConfiguration(vpcConfiguration)
                .build();

            CreateServiceRequest serviceRequest = CreateServiceRequest.builder()
                .cluster(clusterName)
                .networkConfiguration(configuration)
                .desiredCount(1)
                .launchType(LaunchType.FARGATE)
                .serviceName(serviceName)
                .taskDefinition(taskDefinition)
                .build();

            CreateServiceResponse response = ecsClient.createService(serviceRequest) ;
            System.out.println(response.service().serviceArn());
            return response.service().serviceArn();

        } catch (EcsException e) {
            System.err.println(e.awsErrorDetails().errorMessage());
            System.exit(1);
        }
        return "";
    }
}
