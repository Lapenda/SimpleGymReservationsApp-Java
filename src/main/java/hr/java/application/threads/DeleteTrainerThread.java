package hr.java.application.threads;

import hr.java.application.model.Trainer;

public class DeleteTrainerThread extends DatabaseThread implements Runnable{

    private final Trainer trainer;

    public DeleteTrainerThread(Trainer trainer){ this.trainer = trainer; }

    @Override
    public void run() {
        super.deleteTrainer(trainer);
    }
}
