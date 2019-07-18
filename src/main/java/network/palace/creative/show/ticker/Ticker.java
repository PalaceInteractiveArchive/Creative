package network.palace.creative.show.ticker;

public class Ticker implements Runnable {

    @Override
    public void run() {
        new TickEvent().call();
    }
}
