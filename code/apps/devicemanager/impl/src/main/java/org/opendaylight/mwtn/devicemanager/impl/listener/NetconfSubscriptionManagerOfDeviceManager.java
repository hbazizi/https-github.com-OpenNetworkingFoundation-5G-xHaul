/*
* Copyright (c) 2016 Wipro Ltd. and others. All rights reserved.
*
* This program and the accompanying materials are made available under the
* terms of the Eclipse Public License v1.0 which accompanies this distribution,
* and is available at http://www.eclipse.org/legal/epl-v10.html
*/

package org.opendaylight.mwtn.devicemanager.impl.listener;

import java.util.Map.Entry;
import org.opendaylight.controller.md.sal.binding.api.DataBroker;
import org.opendaylight.controller.md.sal.binding.api.DataChangeListener;
import org.opendaylight.controller.md.sal.common.api.data.AsyncDataBroker.DataChangeScope;
import org.opendaylight.controller.md.sal.common.api.data.AsyncDataChangeEvent;
import org.opendaylight.controller.md.sal.common.api.data.LogicalDatastoreType;
import org.opendaylight.mwtn.devicemanager.api.DeviceManagerService;
import org.opendaylight.yang.gen.v1.urn.opendaylight.netconf.node.topology.rev150114.NetconfNode;
//import org.opendaylight.yang.gen.v1.urn.opendaylight.netconf.node.topology.rev150114.NetconfNodeFields.ConnectionStatus;
import org.opendaylight.yang.gen.v1.urn.opendaylight.netconf.node.topology.rev150114.NetconfNodeConnectionStatus.ConnectionStatus;
import org.opendaylight.yang.gen.v1.urn.opendaylight.netconf.node.topology.rev150114.network.topology.topology.topology.types.TopologyNetconf;
import org.opendaylight.yang.gen.v1.urn.tbd.params.xml.ns.yang.network.topology.rev131021.NetworkTopology;
import org.opendaylight.yang.gen.v1.urn.tbd.params.xml.ns.yang.network.topology.rev131021.NodeId;
import org.opendaylight.yang.gen.v1.urn.tbd.params.xml.ns.yang.network.topology.rev131021.TopologyId;
import org.opendaylight.yang.gen.v1.urn.tbd.params.xml.ns.yang.network.topology.rev131021.network.topology.Topology;
import org.opendaylight.yang.gen.v1.urn.tbd.params.xml.ns.yang.network.topology.rev131021.network.topology.TopologyKey;
import org.opendaylight.yang.gen.v1.urn.tbd.params.xml.ns.yang.network.topology.rev131021.network.topology.topology.Node;
import org.opendaylight.yang.gen.v1.urn.tbd.params.xml.ns.yang.network.topology.rev131021.network.topology.topology.NodeKey;
import org.opendaylight.yangtools.concepts.ListenerRegistration;
import org.opendaylight.yangtools.yang.binding.DataObject;
import org.opendaylight.yangtools.yang.binding.Identifier;
import org.opendaylight.yangtools.yang.binding.InstanceIdentifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NetconfSubscriptionManagerOfDeviceManager implements DataChangeListener {

    private static final Logger LOG = LoggerFactory.getLogger(NetconfSubscriptionManagerOfDeviceManager.class);

    public static final InstanceIdentifier<Topology> NETCONF_TOPO_IID = InstanceIdentifier.create(NetworkTopology.class)
            .child(Topology.class, new TopologyKey(new TopologyId(TopologyNetconf.QNAME.getLocalName())));

    private final DeviceManagerService deviceManagerService;
    private final DataBroker dataBroker;
    private ListenerRegistration<DataChangeListener> dclReg;

    public NetconfSubscriptionManagerOfDeviceManager(DeviceManagerService deviceManagerService, DataBroker dataBroker) {
        this.deviceManagerService = deviceManagerService;
        this.dataBroker = dataBroker;
    }

    public void register() {
        dclReg = dataBroker.registerDataChangeListener(LogicalDatastoreType.OPERATIONAL,
                NETCONF_TOPO_IID.child(Node.class), this, DataChangeScope.SUBTREE);
    }

    @Override
    public void onDataChanged(AsyncDataChangeEvent<InstanceIdentifier<?>, DataObject> change) {

        //LOG.debug("OnDataChange, change: {}", change); //<- Seems to be very long in cases
        LOG.debug("OnDataChange, change");

        for (Entry<InstanceIdentifier<?>, DataObject> entry : change.getUpdatedData().entrySet()) {
            if (entry.getKey().getTargetType() == NetconfNode.class) {
                NodeId nodeId = getNodeId(entry.getKey());

                // Not interested
                if (nodeId.getValue().equals("controller-config")) {
                    return;
                }

                NetconfNode nnode = (NetconfNode) entry.getValue();
                ConnectionStatus csts = nnode.getConnectionStatus();

                switch (csts) {
                    case Connected: {
                        LOG.debug("NETCONF Node: {} is fully connected", nodeId.getValue());
                        deviceManagerService.startListenerOnNode(nodeId.getValue());
                        break;
                    }

                    case Connecting: {
                        LOG.debug("NETCONF Node: {} was disconnected", nodeId.getValue());
                        deviceManagerService.removeListenerOnNode(nodeId.getValue());
                        break;
                    }
                    case UnableToConnect: {
                        LOG.debug("NETCONF Node: {} connection failed", nodeId.getValue());
                        deviceManagerService.removeListenerOnNode(nodeId.getValue());
                        break;
                    }
                }
            }
        }
    }

    private NodeId getNodeId(final InstanceIdentifier<?> path) {
        for (InstanceIdentifier.PathArgument pathArgument : path.getPathArguments()) {
            if (pathArgument instanceof InstanceIdentifier.IdentifiableItem<?, ?>) {
                final Identifier<?> key = ((InstanceIdentifier.IdentifiableItem<?, ?>) pathArgument).getKey();
                if (key instanceof NodeKey) {
                    return ((NodeKey) key).getNodeId();
                }
            }
        }
        return null;
    }

    public void close() {
        if (dclReg != null) {
            dclReg.close();
        }
    }
}
