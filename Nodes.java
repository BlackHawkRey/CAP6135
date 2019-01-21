public class Nodes {

    public final static int EMPTY = 0;
    public final static int INFECTED = 1;
    private int status = EMPTY;
    private int nodeIP;

    public Nodes(int nodeIP) {
        this.nodeIP = nodeIP;
    }

    public int getNodeIP() {
        return nodeIP;
    }

    public boolean isInfected() {
        return (status == INFECTED);
    }

    public void infect() {
        status = INFECTED;
    }

    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Nodes other = (Nodes) obj;
        if (this.nodeIP != other.nodeIP) {
            return false;
        }
        return true;
    }

    public int hashCode() {
        int hash = 3;
        hash = 61 * hash + this.nodeIP;
        return hash;
    }
}