# coding = utf-8
import networkx as nx
import matplotlib.pyplot as plt

g = nx.Graph()

g_data = []
with open('a.txt') as f:
    for line in f.readlines():
        line = line.strip().replace('\n', '')
        if line == '' or line.startswith('source,'):
            continue
        s, t, w = line.split(',')
        if 80 < int(w) < 180:
            g_data.append([s, t, int(w)])

# g.add_nodes_from()

g.add_weighted_edges_from(g_data)
labels = nx.get_edge_attributes(g, 'weight')
print(labels)
# nx.draw_networkx()

plt.scatter
nx.draw_circular(g, node_size=100, node_shape='o', node_color='gray', edge_color='gray')
pos = nx.circular_layout(g)

pos_labels = {}
offset = 0.08
for k, v in pos.items():
    x, y = pos[k]
    pos_labels[k] = (x + offset, y)
    print(k, v)

# exit(0)

nx.draw_networkx_labels(g, pos=pos_labels, font_size=14)

nx.draw_networkx_edge_labels(g, pos, edge_labels=labels, font_size=7)

fig = plt.gcf()
fig.set_size_inches(14, 10)
fig.savefig('path.png', dpi=300)
