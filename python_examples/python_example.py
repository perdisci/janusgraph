# Python 3.10.12
# janusgraph 1.0.0
# gremlinpython==3.7.0

from gremlin_python.process.anonymous_traversal import traversal
from gremlin_python.driver.driver_remote_connection import DriverRemoteConnection
from gremlin_python.driver import serializer

# you need to turn GraphSONSerializersV3 in the JanusGraph server config
message_serializer = serializer.GraphSONSerializersV3d0()

connection = DriverRemoteConnection("ws://jgserver:8182/gremlin", "g", message_serializer=message_serializer)
g = traversal().withRemote(connection)

g.V().drop().iterate()
g.E().drop().iterate()
v1 = g.addV('vertex').property('name', 'v1').next()
v1 = g.addV('vertex').property('name', 'v2').next()
print(g.V().toList())