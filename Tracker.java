
public abstract class Tracker
{
    private int total;
    public Tracker(int total)
    {
        this.total = total;
    }
    public int get_total()
    {
        return this.total;
    }
    public abstract void update(int current);
}
