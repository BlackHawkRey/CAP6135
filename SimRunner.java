import java.io.PrintWriter;
import java.util.*;

public class SimRunner {

    private static final int Omega = 100000;
    private static final int simsAmount = 100;
    private static final int clusterNodes = 10;
    private static final int clusterOffset = 1000;
    private static final int simulN = 3;

    private static boolean localPreference = false;
    private static double probability = 0.8;

    public static void main(String[] args) {

        int simulTime = 800;
        int rountTick = 1;
        int vulnerableNodes = Omega / (clusterOffset / clusterNodes);
        PrintWriter cumulativeValues = null;
        PrintWriter roundFile = null;

        HashMap < Integer, List < Integer >> allRounds = new HashMap < Integer, List < Integer >> ();

        for (int simCount = 0; simCount < simsAmount; simCount++) {
            List < Integer > infectedNum = new ArrayList < Integer > ();
            Set < Nodes > infectedIPs = new HashSet < Nodes > ();
            HashMap < Integer, Nodes > ipToHostMap = new HashMap < Integer, Nodes > ();

            for (int baseIPs = 0; baseIPs < Omega; baseIPs += clusterOffset) {
                for (int clusterNum = 1; clusterNum <= clusterNodes; clusterNum++) {
                    int ipAddress = baseIPs + clusterNum;
                    ipToHostMap.put(ipAddress, new Nodes(ipAddress));
                }
            }

            List < Integer > allHostIPs = new ArrayList < Integer > (ipToHostMap.keySet());

            Integer infectedIP = (Integer) SimSupport.randomElement(allHostIPs);
            ipToHostMap.get(infectedIP).infect();

            infectedIPs.add(ipToHostMap.get(infectedIP));
            infectedNum.add(1);

            for (int timeTick = 1; infectedIPs.size() != ipToHostMap.values().size(); timeTick++) {

                Set < Nodes > infectedIPs2 = new HashSet < Nodes > (infectedIPs);

                for (Nodes infectedIPs1: infectedIPs2) {
                    for (int scanAmount = 1; scanAmount <= simulN; scanAmount++) {

                        Integer attemptNode;
                        if (localPreference && SimSupport.generateBoolean(probability)) {
                            attemptNode = SimSupport.localRandomIPAddress(infectedIPs1.getNodeIP());
                        } else {

                            attemptNode = SimSupport.randomIPAddress(Omega);
                        }

                        if (ipToHostMap.containsKey(attemptNode)) {

                            Nodes hostToInfect = ipToHostMap.get(attemptNode);

                            hostToInfect.infect();
                            infectedIPs.add(hostToInfect);
                        }
                    }
                }
                infectedNum.add(infectedIPs.size());
            }
            allRounds.put(simCount, infectedNum);
        }

        for (List < Integer > runData: allRounds.values()) {
        	simulTime = Math.max(simulTime, runData.size());
        }

        int averageTimeToInfectAll = 0;

        for (List < Integer > runData: allRounds.values()) {
            averageTimeToInfectAll += runData.size();
            while (runData.size() != simulTime) {
                runData.add(vulnerableNodes);
            }
        }
        averageTimeToInfectAll = averageTimeToInfectAll / simsAmount;

        List < Integer > avgInfectious = new ArrayList < Integer > ();
        for (int tick = 0; tick < simulTime; tick++) {
            int totalInfected = 0;
            for (List < Integer > runData: allRounds.values()) {
                totalInfected = totalInfected + runData.get(tick);
            }
            avgInfectious.add(totalInfected / simsAmount);
        }

        try {
            roundFile = new PrintWriter("ResultsFile.txt", "UTF-8");
            String fileName = "CumulativeValuesSimpleRun.txt";
            if (localPreference) {
                fileName = "CumulativeValuesLocalPreferenceRun.txt";
            }

            cumulativeValues = new PrintWriter(fileName, "UTF-8");
            for (int tick = 0; tick < avgInfectious.size(); tick++) {
                cumulativeValues.print(avgInfectious.get(tick));
                if (tick < avgInfectious.size() - 1) {
                    cumulativeValues.print("\n");
                }
            }

            List < Integer > round1 = allRounds.get(0);
            List < Integer > round2 = allRounds.get(1);
            List < Integer > round3 = allRounds.get(2);
            
            for (int currentTick = 0; currentTick < simulTime; currentTick++) {
                roundFile.print(rountTick + " " + round1.get(currentTick) + " " + round2.get(currentTick) + " " + round3.get(currentTick));

                if (currentTick < simulTime - 1) {
                    roundFile.print("\n");
                    rountTick++;
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();

        } finally {
            cumulativeValues.close();
            roundFile.close();

            System.out.println("Worm Propagation Simulation Done.");
            System.out.println("Please Check The Files Generated.");
        }
    }
}