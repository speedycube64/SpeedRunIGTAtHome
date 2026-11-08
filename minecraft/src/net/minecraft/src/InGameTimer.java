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
    private boolean timerRunning = false;
    private int stoppingBlockID = 0;

    // grab the time from ticks.txt inside the world folder if it exists, otherwise it's 0
    // also grab whether the timer is running or stopped
    // called when a world is opened
    public void loadDataFromWorldFile(String worldDirectory){

        File file = new File(worldDirectory + "/ticks.txt");

        try(Scanner reader = new Scanner(file)){
            String ticks = reader.nextLine();
            this.tickCount = Integer.parseInt(ticks);
            System.out.println("[SpeedRunIGT At Home] Loaded time from ticks.txt: " 
                                + this.getTimeString());

            try{
                String running = reader.nextLine();
                this.timerRunning = Boolean.parseBoolean(running);
                if(this.timerRunning){
                    System.out.println("[SpeedRunIGT At Home] Timer is running");
                }
                else{
                    System.out.println("[SpeedRunIGT At Home] Timer is not running");
                }
            }
            catch(Exception e){
                System.out.println("[SpeedRunIGT At Home] Assuming timer is running");
                this.timerRunning = true;
            }
        }
        catch(Exception e){
            System.out.println("[SpeedRunIGT At Home] Did not find ticks.txt. Starting at 0:00");
            this.tickCount = 0;
            this.timerRunning = true;
        }
    }

    // grab the desired block id at which to stop the timer (i.e. completing the monument)
    public void loadStoppingBlockID(String gameDirectory){
        File file = new File(gameDirectory + "/stopping_block.txt");

        try(Scanner reader = new Scanner(file)){
            String id = reader.nextLine();
            this.stoppingBlockID = Integer.parseInt(id);
            System.out.println("[SpeedRunIGT At Home] Loaded stopping block ID: " 
                                + this.stoppingBlockID);
        }
        catch(Exception e){
            System.out.println("[SpeedRunIGT At Home] Did not find stopping block ID");
            this.stoppingBlockID = 0;
        }
    }

    // save the current tick count to ticks.txt
    // also save whether the timer is running
    // called when a world is saved/closed
    public void saveDataToWorldFile(String worldDirectory){
        File file = new File(worldDirectory + "/ticks.txt");
            try(PrintWriter writer = new PrintWriter(file)) {
            writer.println(this.tickCount);
            writer.println(this.timerRunning);
            System.out.println("[SpeedRunIGT At Home] Saved time to ticks.txt: " 
                                + this.getTimeString());
            if(this.timerRunning){
                System.out.println("[SpeedRunIGT At Home] Timer is running");
            }
            else{
                System.out.println("[SpeedRunIGT At Home] Timer is not running");
            }
        } catch(Exception e){
            System.out.println("[SpeedRunIGT At Home] Could not save data to ticks.txt"); // how
        }
    }

    // when a block is placed, this checks if it's the stopping block,
    // and if it is, it stops the timer
    public void processBlockPlaced(int blockID){
        if(blockID == stoppingBlockID){
            this.stopTimer();
        }
    }

    public void updateTickCount(){
        if(this.timerRunning){
            this.tickCount++;
        }
    }

    public void startTimer(){
        if(this.tickCount == 0){
            this.timerRunning = true;
        }
    }

    public void stopTimer(){
        this.timerRunning = false;
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

    // mostly for debugging
    public long getTickCount(){
        return this.tickCount;
    }
    // Also mostly for debugging
    public void printTickCount(){
        System.out.println("[SpeedRunIGT At Home] Game time: " + this.tickCount + " ticks");
    }
}

// speedrunigtathome