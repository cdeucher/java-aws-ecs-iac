package integration.core;

import software.amazon.awssdk.services.ecs.EcsClient;
import software.amazon.awssdk.services.ecs.model.*;

import java.util.ArrayList;
import java.util.List;

public class ServicesLocal {

    AwsVpcConfiguration vpcConfiguration;
    NetworkConfiguration configuration;
    String clusterName;
    String serviceName;
    String taskDefinition;
    List<String> services = new ArrayList<>();

    public ServicesLocal(String clusterName,
                         String serviceName,
                         String securityGroups,
                         String subnets,
                         String taskDefinition) {
        this.clusterName = clusterName;
        this.serviceName = serviceName;
        this.taskDefinition = taskDefinition;
        this.vpcConfiguration = AwsVpcConfiguration.builder()
            .securityGroups(securityGroups)
            .subnets(subnets)
            .assignPublicIp(AssignPublicIp.ENABLED)
            .build();
        this.configuration = NetworkConfiguration.builder()
            .awsvpcConfiguration(vpcConfiguration)
            .build();
    }

    public String CreateNewService(EcsClient ecsClient) {
        try {
            CreateServiceRequest serviceRequest = CreateServiceRequest.builder()
                .cluster(clusterName)
                .networkConfiguration(configuration)
                .desiredCount(1)
                .launchType(LaunchType.FARGATE)
                .serviceName(serviceName)
                .taskDefinition(taskDefinition)
                .build();

            CreateServiceResponse serviceResponse = ecsClient.createService(serviceRequest) ;
            System.out.println(serviceResponse.service().serviceArn());
            return serviceResponse.service().serviceArn();
        } catch (EcsException e) {
            System.err.println(e.awsErrorDetails().errorMessage());
            System.exit(1);
        }
        return "";
    }

    public List<String> getService(EcsClient ecsClient){
        if (services.size() > 0)
            return services;

        DescribeServicesRequest  serviceRequest = DescribeServicesRequest.builder()
            .cluster(clusterName)
            .services(serviceName)
            .build();

        DescribeServicesResponse serviceResponse = ecsClient.describeServices(serviceRequest);
        System.out.println(serviceResponse.services().get(0).serviceName());
        List<Service> list = serviceResponse.services();
        for (int i = list.size(); i < list.size(); i++) {
            services.add(list.get(i).serviceName());
        }
        return services;
    }
}
