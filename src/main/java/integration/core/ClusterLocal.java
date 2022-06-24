package integration.core;

import software.amazon.awssdk.services.ecs.EcsClient;
import software.amazon.awssdk.services.ecs.model.*;

import java.util.ArrayList;
import java.util.List;

public class ClusterLocal {

    private EcsClient ecsClient;
    private List<String> clusters = new ArrayList<>();

    public ClusterLocal(EcsClient ecsClient) {
        this.ecsClient = ecsClient;
    }

    public List<String> getClusters () {
        return clusters;
    }

    public String createCluster( String clusterName ) {
        try {
            ExecuteCommandConfiguration commandConfiguration =  ExecuteCommandConfiguration.builder()
              .logging(ExecuteCommandLogging.DEFAULT)
              .build();

            ClusterConfiguration clusterConfiguration = ClusterConfiguration.builder()
              .executeCommandConfiguration(commandConfiguration)
              .build();

            CreateClusterRequest clusterRequest = CreateClusterRequest.builder()
              .clusterName(clusterName)
              .configuration(clusterConfiguration)
              .build();

            CreateClusterResponse response = ecsClient.createCluster(clusterRequest) ;
            return response.cluster().clusterArn();

        } catch (EcsException e) {
            System.err.println(e.awsErrorDetails().errorMessage());
            System.exit(1);
        }
        return "";
    }

    public List<String> listAllClusters() {
        try {
            if ( getClusters().size() > 0 ) {
                return getClusters();
            }
            ListClustersResponse response = ecsClient.listClusters();
            clusters = response.clusterArns();
            System.out.println("Clusters"+clusters);
            for (String cluster: clusters) {
                System.out.println("The cluster arn is "+cluster);
            }
            return clusters;
        } catch (EcsException e) {
            System.err.println(e.awsErrorDetails().errorMessage());
            System.exit(1);
        }
        return null;
    }

    public static void descCluster(EcsClient ecsClient, String clusterArn) {
        try {
            DescribeClustersRequest clustersRequest = DescribeClustersRequest.builder()
                .clusters(clusterArn)
                .build();

            DescribeClustersResponse response = ecsClient.describeClusters(clustersRequest);
            List<Cluster> clusters = response.clusters();
            for (Cluster cluster: clusters) {
                System.out.println("The cluster name is "+cluster.clusterName());
            }

        } catch (EcsException e) {
            System.err.println(e.awsErrorDetails().errorMessage());
            System.exit(1);
        }
    }
}
