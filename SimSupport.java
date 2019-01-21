import java.util.ArrayList;
import java.util.List;

public class SimSupport {

    public static boolean generateBoolean(double trueProbability) {
        return Math.random() <= trueProbability;
    }

    public static Object randomElement(List<Integer> objects) {
        int randomIndex = 0 + (int)(Math.random() * (objects.size()));
        return objects.get(randomIndex);
    }

    public static Integer randomIPAddress(int numAddresses) {
        int randomIP = 0 + (int)(Math.random() * (numAddresses));
        return randomIP;
    }

    public static Integer localRandomIPAddress(int clusterIP) {
        List < Integer > possibleIPs = new ArrayList < Integer > ();
        for (int offset = 1; offset <= 10; offset++) {
            possibleIPs.add(clusterIP - offset);
            possibleIPs.add(clusterIP + offset);
        }
        return (Integer) randomElement(possibleIPs);
    }
}