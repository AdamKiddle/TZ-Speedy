package splitterlive;

import java.util.logging.Level;
import java.util.logging.Logger;

public class TimerObject implements Runnable {
    /*
     * The two class variables used in the timer object are to
     * decide the time period of the Timer and to set if it's
     * running or not.
     */

    private static boolean timerHasStarted = true;
    private static int TIMER_PERIOD_MS = 11;

    /*
     * The main routine is private as it should only be called
     * upon the class creation. The routine requires an input of
     * the timer period and sets the corresponding class variable
     * to this value. The loopLogic void routine is then called.
     */

    /*
     * The setRunning void is the only public routine in the class.
     * It is used for setting the timer to either running or stopped.
     * The routine calls the loopLogic routine if the timer wasn't
     * running beforehand so that it may start. If the timer was
     * already running beforehand, it will automatically stop when the
     * class boolean 'timerHasStarted' is set to false and therefore
     * in this instance the loopLogic thread is not needed to be called.
     */
    public static void setRunning(boolean timerIsSetToRunning) {
        timerHasStarted = timerIsSetToRunning;
    }

    /*
     * The following is where the actual looping happens. If the timer is
     * set to be started, the loop runs.each iteration calls the 'onTimerTick'
     * method in the main class and then waits for a period of time
     * defined by the class variable 'timerPeriodInMilliseconds'.
     */
    private static void loopLogic() {
        while (timerHasStarted) {
            SplitterLive.onTimerTick();
            try {
                Thread.sleep(TIMER_PERIOD_MS);
            } catch (InterruptedException ex) {
                Logger.getLogger(TimerObject.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    @Override
    public void run() {
        loopLogic();
    }
}
