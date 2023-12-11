# Janusgraph tips and code

## Setting up JanusGraph containers

First, create a network:
`docker network create --ip-range 192.168.254.128/25 janusgraph_network`


Then, start a cassandra container
`docker run -d --name cassandra --hostname cassandra --network janusgraph_network cassandra:latest`


Start elasticsearch container. This requires lots of memory. We might need to revert to Lucene.
`docker run -d --name elasticsearch --hostname elasticsearch --net janusgraph_network -p 9200:9200 -p 9300:9300 -e "discovery.type=single-node" elasticsearch:8.11.1`

Start JanusGraph server container
`docker run -it --name janusgraph_server --hostname jgserver --net janusgraph_network -p 8182:8182 janusgraph/janusgraph:latest bash`

Setup server config file to run with Cassandra and Elasticsearch or Lucene

Start Gremlin console
`docker run -it --name janusgraph_client --hostname jgclient --net janusgraph_network janusgraph/janusgraph:latest bash`

```
gremlin> :remote connect tinkerpop.server conf/remote.yaml session
gremlin> :remote console
gremlin> g.V()
```

