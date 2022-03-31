package integration.ecs;

import software.amazon.awssdk.services.ecs.EcsClient;
import software.amazon.awssdk.services.ecs.model.*;

import java.util.ArrayList;
import java.util.List;

public class TasksLocal {

    private EcsClient ecsClient;
    private List<String> tasks = new ArrayList<>();
    private List<String> taskDefinitions = new ArrayList<>();

    public TasksLocal(EcsClient ecsClient) {
        this.ecsClient = ecsClient;
    }

    public List<String> getTasks() {
        return tasks;
    }
    public List<String> getTaskDefinitions() {
        return taskDefinitions;
    }

    public String createTaskDefinition(String family
            , String taskExecutionArn, String taskRoleArn) {

        List<PortMapping> portMappings = new ArrayList<PortMapping>();
        PortMapping mapping = PortMapping.builder()
            .containerPort(80)
            .hostPort(80)
            .build();
        portMappings.add(mapping);

        ContainerDefinition container = ContainerDefinition.builder()
            .name("containerName")
            .cpu(1024)
            .memory(2048)
            .image("public.ecr.aws/nginx/nginx")
            .portMappings(portMappings)
            .build();

        RegisterTaskDefinitionRequest taskDefinition = RegisterTaskDefinitionRequest.builder()
            .family(family)
            .networkMode("awsvpc")
            .cpu("1024")
            .memory("2048")
            .requiresCompatibilities(Compatibility.EC2)
            .executionRoleArn(taskExecutionArn)
            .taskRoleArn(taskRoleArn)
            .containerDefinitions(container)
            .build();

        RegisterTaskDefinitionResponse responde = ecsClient.registerTaskDefinition(taskDefinition);
        System.out.println(responde.taskDefinition().taskDefinitionArn());
        return responde.taskDefinition().taskDefinitionArn();
    }

    public List<String> listTaskDefinitions() {
        if ( getTaskDefinitions().size() > 0 ) {
            return getTaskDefinitions();
        }
        taskDefinitions = ecsClient.listTaskDefinitions().taskDefinitionArns();
        return taskDefinitions;
    }

    public List<String> listTasks(String clusterArn) {
        if ( getTasks().size() > 0 )
            return getTasks();

        DescribeTasksRequest tasksRequest = DescribeTasksRequest.builder()
                .cluster(clusterArn)
                .build();
        if ( tasksRequest.tasks().size() <= 0)
            return tasks;

        DescribeTasksResponse response = ecsClient.describeTasks(tasksRequest);
        List<Task> taskList = response.tasks();
        for (Task task: taskList) {
            tasks.add(task.taskArn());
        }
        return tasks;
    }

    public String createTask (String clusterName, String taskDefinition,
                              String securityGroups, String subnets) {

        AwsVpcConfiguration awsvpcConfiguration = AwsVpcConfiguration.builder()
            .securityGroups(securityGroups)
            .subnets(subnets)
            .assignPublicIp(AssignPublicIp.DISABLED)
            .build();
        NetworkConfiguration vpcConfiguration = NetworkConfiguration.builder()
            .awsvpcConfiguration(awsvpcConfiguration)
            .build();

        RunTaskRequest runTask = RunTaskRequest.builder()
            .cluster(clusterName)
            .taskDefinition(taskDefinition)
            //.capacityProviderStrategy(capacityProviderItem)
            .networkConfiguration(vpcConfiguration)
            .count(1)
            .build();
        RunTaskResponse response = ecsClient.runTask(runTask);

        System.out.println("Task:" + response.tasks().get(0).lastStatus());
        return response.tasks().get(0).taskArn();
    }

}
