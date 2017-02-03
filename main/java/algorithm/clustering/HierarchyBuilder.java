/*******************************************************************************
 * Copyright 2013 Lars Behnke
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/

package algorithm.clustering;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HierarchyBuilder {

    private DistanceMap distances;
    private List<Cluster> clusters;
	private HashMap<String, Cluster> cls;

    public DistanceMap getDistances() {
        return distances;
    }

    public List<Cluster> getClusters() {
        return clusters;
    }

    public HierarchyBuilder(List<Cluster> clusters, DistanceMap distances) {
		this.clusterPerIteration = new ArrayList<>();
        this.clusters = clusters;
        this.distances = distances;
		this.schedule = new ArrayList<>();
		this.cls = new HashMap<>();
    }

    /**
     * Returns Flattened clusters, i.e. clusters that are at least apart by a given threshold
     * @param linkageStrategy
     * @param threshold
     * @return
     */
    public List<Cluster> flatAgg(LinkageStrategy linkageStrategy, Double threshold)
    {
        while((!isTreeComplete()) && (distances.minDist() != null) && (distances.minDist() <= threshold))
        {
            //System.out.println("Cluster Distances: " + distances.toString());
            //System.out.println("Cluster Size: " + clusters.size());
            agglomerate(linkageStrategy);
        }

        //System.out.println("Final MinDistance: " + distances.minDist());
        //System.out.println("Tree complete: " + isTreeComplete());
        return clusters;
    }
	
	private int clusterCount = 0;
	private int scheduleResult = 0;
	private ArrayList<Double> schedule;

	public HashMap<String, Cluster> getCls() {
		return cls;
	}
	
	private List<List<Cluster>> clusterPerIteration;
	
    public void agglomerate(LinkageStrategy linkageStrategy) {
        ClusterPair minDistLink = distances.removeFirst();
        if (minDistLink != null) {
            clusters.remove(minDistLink.getrCluster());
            clusters.remove(minDistLink.getlCluster());

            Cluster oldClusterL = minDistLink.getlCluster();
            Cluster oldClusterR = minDistLink.getrCluster();
            Cluster newCluster = minDistLink.agglomerate(null);
			cls.put(newCluster.getName(), newCluster);
			
            for (Cluster iClust : clusters) {
                ClusterPair link1 = findByClusters(iClust, oldClusterL);
                ClusterPair link2 = findByClusters(iClust, oldClusterR);
                ClusterPair newLinkage = new ClusterPair();
                newLinkage.setlCluster(iClust);
                newLinkage.setrCluster(newCluster);
                Collection<Distance> distanceValues = new ArrayList<Distance>();

                if (link1 != null) {
					Double distVal = link1.getLinkageDistance();
                    Double weightVal = link1.getOtherCluster(iClust).getWeightValue();
                    distanceValues.add(new Distance(distVal, weightVal));
                    distances.remove(link1);
                }
                if (link2 != null) {
					Double distVal = link2.getLinkageDistance();
					Double weightVal = link2.getOtherCluster(iClust).getWeightValue();
                    distanceValues.add(new Distance(distVal, weightVal));
                    distances.remove(link2);
                }

                Distance newDistance = linkageStrategy.calculateDistance(distanceValues);

				newLinkage.setLinkageDistance(newDistance.getDistance());
				
                distances.add(newLinkage);
				//System.out.println("DALEM i: "+i+" -> "+newCluster.getDistance().getDistance());
            }
			clusterCount++;
			this.schedule.add(newCluster.getDistance().getDistance());
            clusters.add(newCluster);
        }
		ArrayList<Cluster> temp = new ArrayList<>();
		for (Cluster cluster : clusters) {
			temp.add(cluster);
		}
		clusterPerIteration.add(temp);
    }

	public List<List<Cluster>> getClusterPerIteration() {
		return clusterPerIteration;
	}
	
	public int getScheduleResult() {
		int maxIndex = 0;
		double maxDiff = 0;
		double currentDiff = 0;
		
		for (int i = 0; i < this.schedule.size()-1; i++) {
			currentDiff = this.schedule.get(i+1) - this.schedule.get(i);
			if (currentDiff > maxDiff) {
				maxIndex = i+1;
				maxDiff = currentDiff;
			}
		}
		System.out.println("maxDiff: "+maxDiff);
		return this.schedule.size()-maxIndex+1;
	}

    private ClusterPair findByClusters(Cluster c1, Cluster c2) {
        return distances.findByCodePair(c1, c2);
    }

    public boolean isTreeComplete() {
        return clusters.size() == 1;
    }

    public Cluster getRootCluster() {
        if (!isTreeComplete()) {
            throw new RuntimeException("No root available");
        }
        return clusters.get(0);
    }

}
