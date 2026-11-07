// speedrunigtathome

package net.minecraft.src;

import java.io.File;
import java.io.PrintWriter;
import java.util.Scanner;

public class InGameTimer {

    // make it so we can access this class anywhere else (singleton pattern)
    private static final InGameTimer instance = new InGameTimer();
    public static InGameTimer getInstance(){return instance;}

    private long tickCount = 0;

    // grab the time from ticks.txt inside the world folder if it exists, otherwise it's 0
    // called when a world is opened
    public void loadTimeFromWorldFile(String worldDirectory){

        File file = new File(worldDirectory + "/ticks.txt");

        try (Scanner reader = new Scanner(file)) {
            String ticks = reader.nextLine();
            this.tickCount = Integer.parseInt(ticks);
            System.out.println("[SpeedRunIGT At Home] Loaded time from ticks.txt: " 
                                + this.getTimeString());

        } catch (Exception e) {
            System.out.println("[SpeedRunIGT At Home] Did not find ticks.txt. Defaulting to 0");
            this.tickCount = 0;
        }
    }

    // save the current tick count to ticks.txt
    // called when a world is saved/closed
    public void saveTimeToWorldFile(String worldDirectory){
        File file = new File(worldDirectory + "/ticks.txt");
            try(PrintWriter writer = new PrintWriter(file)) {
            writer.print(this.tickCount);
            System.out.println("[SpeedRunIGT At Home] Saved time to ticks.txt: " 
                                + this.getTimeString());

        } catch (Exception e) {
            System.out.println("[SpeedRunIGT At Home] Could not save time to ticks.txt");
        }
    }

    public void updateTickCount(){
        this.tickCount++;
    }

    public long getTickCount(){
        return this.tickCount;
    }

    // mostly for debugging
    public void printTickCount(){
        System.out.println("[SpeedRunIGT At Home] Game time: " + this.tickCount + " ticks");
    }

    // convert ticks to hh:mm:ss.00 format (thanks yoraia)
    public String getTimeString(){
        long ms = this.tickCount * 50;
        long hours = ms / (1000 * 60 * 60);
        long minutes = (ms / (1000 * 60)) % 60;
        long seconds = (ms / 1000) % 60;
        long milliseconds = ms % 1000;

        return String.format("%02d:%02d:%02d.%03d", hours, minutes, seconds, milliseconds);
    }
}

// speedrunigtathome