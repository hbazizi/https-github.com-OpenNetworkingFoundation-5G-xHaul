package org.opendaylight.mwtn.base.netconf;

import org.opendaylight.controller.md.sal.binding.api.MountPoint;

public interface ONFCoreNetworkElementRepresentation {

       public void initialReadFromNetworkElement();

       public String getMountPointNodeName();

       public void resetPMIterator();

       public boolean hasNext();

       public void next();

       public AllPm getHistoricalPM();

       public String pmStatusToString();

       public int removeAllCurrentProblemsOfNode();

       public void doRegisterMicrowaveEventListener(MountPoint mountPoint);

}