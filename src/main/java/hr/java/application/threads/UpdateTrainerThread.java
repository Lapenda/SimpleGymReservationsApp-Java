package hr.java.application.threads;

import hr.java.application.model.Trainer;

public class UpdateTrainerThread extends DatabaseThread implements Runnable{

    private final Trainer trainer;
    private final Integer id;

    public UpdateTrainerThread(Trainer trainer, Integer id){
        this.trainer = trainer;
        this.id = id;
    }

    @Override
    public void run() {
        super.updateTrainer(trainer, id);
    }
}
