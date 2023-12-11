package com.example;

import java.util.*;
import org.apache.tinkerpop.gremlin.driver.*;
import org.apache.tinkerpop.gremlin.util.ser.GraphBinaryMessageSerializerV1;
import org.apache.tinkerpop.gremlin.util.ser.Serializers;
import org.apache.tinkerpop.gremlin.util.MessageSerializer;
import org.apache.tinkerpop.gremlin.structure.io.binary.TypeSerializerRegistry;
import org.janusgraph.graphdb.tinkerpop.JanusGraphIoRegistry;
import org.apache.tinkerpop.gremlin.driver.remote.DriverRemoteConnection;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import static org.apache.tinkerpop.gremlin.process.traversal.AnonymousTraversalSource.traversal;

public class GremlinJavaExample {

private static MessageSerializer createGraphBinaryMessageSerializerV1() {
  final GraphBinaryMessageSerializerV1 serializer = new GraphBinaryMessageSerializerV1();
  final Map<String, Object> config = new HashMap<>();
  config.put(GraphBinaryMessageSerializerV1.TOKEN_IO_REGISTRIES, Collections.singletonList(JanusGraphIoRegistry.class.getName()));
  serializer.configure(config, Collections.emptyMap());
  return serializer;
}


    public static void main(String[] args) {
        // Specify the Gremlin Server's address and port
        String gremlinServer = "jgserver";
        int gremlinServerPort = 8182;

        // Create a Cluster object to connect to the Gremlin Server
        try {

            /*
            TypeSerializerRegistry typeSerializerRegistry = TypeSerializerRegistry.build()
                        .addRegistry(JanusGraphIoRegistry.instance())
                        .create();

            */

            Cluster cluster = Cluster.build()
                .addContactPoint(gremlinServer)
                .port(gremlinServerPort)
                // .serializer(new GraphBinaryMessageSerializerV1(typeSerializerRegistry))
                .serializer(createGraphBinaryMessageSerializerV1())
                .create();

            // cluster.getSerializer().addCustomModule(GraphBinaryModule.instance());

            // Create a Client object to interact with the Gremlin Server
            try {
                Client client = cluster.connect();
                DriverRemoteConnection conn = DriverRemoteConnection.using(client);
                GraphTraversalSource g = traversal().withRemote(conn);

                System.out.println(">>" + g.V().valueMap().toList());

                System.out.println("=============================");

                // Example Gremlin query
                String upsertQuery0 = "g.addV().property('name': 'test')";
                String upsertQuery1 = "g.mergeV([(T.label): 'vertex', name: 'vx']).option(onCreate, [name: 'vx', age: 21]).option(onMatch, [age: 25])";
                // String upsertQuery2 = "g.mergeV().option(onCreate, [name: 'vx', age: 21]).option(onMatch, [age: 23])";
                ResultSet res0 = client.submit(upsertQuery0);
                for (Result result : res0) {
                    System.out.println(result.getString());
                }
                ResultSet res1 = client.submit(upsertQuery1);
                for (Result result : res1) {
                    System.out.println(result.getString());
                }
                /*
                ResultSet res2 = client.submit(upsertQuery2);
                for (Result result : res2) {
                    System.out.println(result.getString());
                }
                */

                String gremlinQuery = "g.V().valueMap()";

                // Execute the query
                ResultSet resultSet = client.submit(gremlinQuery);

                // Process the results
                for (Result result : resultSet) {
                    System.out.println(result.getString());
                }
                System.out.println("Finished!");
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                    try {
                            cluster.close();
                    }
                    catch (Exception e) {
                            e.printStackTrace();
                    }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
